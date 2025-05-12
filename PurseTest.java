import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A JUnit (5) test class for testing the non-private methods of the Purse Class.
 *
 * @author  Jitse Vandenberghe
 * @version 1.0
 */
public class PurseTest {

    // PURSE
    private static Purse    purse_A, fullPurse500, fullPurse1000, emptyPurse500, emptyPurse300,
                            purseWithCap500AndContents250, purseWithCap1000AndContents250,
                            purseWithCap1000AndContents50, purseWithZeroCapacity, destroyedPurse;

    @BeforeEach
    public void setUpEquipment() {
        purse_A = new Purse(10, 50);
        fullPurse500 = new Purse(10,500);
        fullPurse500.addToContents(500);
        fullPurse1000 = new Purse(10, 500);
        fullPurse1000.addToContents(1000);
        emptyPurse500 = new Purse(10, 500);
        emptyPurse300 = new Purse(10, 300);
        purseWithCap500AndContents250 = new Purse(10,500);
        purseWithCap500AndContents250.addToContents(250);
        purseWithCap1000AndContents50 = new Purse(10, 50);
        purseWithCap1000AndContents50.addToContents(50);
        purseWithCap1000AndContents250 = new Purse(10,1000);
        purseWithCap1000AndContents250.addToContents(250);
        purseWithZeroCapacity = new Purse(10, 0);
        destroyedPurse = new Purse(10, 100);
        destroyedPurse.destroy();
    }

    /**
     * CONSTRUCTOR
     */

    @Test
    void testConstructor_ValidArguments_ShouldInitializeFields() {
        int oldSizeIdentificationOfPurseB4B = Equipment.equipmentByType.get(Purse.class).size();
        Purse Purse_B = new Purse(15, 70);

        // 1. effect of super(weight, 0, capacity)
        // 1.1 postcondition on weight
        assertEquals(10, purse_A.getWeight());
        // 1.2 postcondition on base value
        assertEquals(0, purse_A.getBaseValue());
        // 1.3 Postcondition on identification
        // 1.3.1 Postcondition on the uniqueness of the ID and on the fact that the ID is positive
        // Check that the assigned identification is strictly positive
        assertTrue(purse_A.getIdentification() > 0);
        // (!) At this point, the identification is already present in the map for this equipment type.
        //     Therefore, canHaveAsIdentification will return false, not because the ID is negative,
        //     but because it is no longer unique. This confirms the method correctly enforces uniqueness.
        assertFalse(purse_A.canHaveAsIdentification(Purse.class, purse_A.getIdentification()));
        // 1.4 effect of addIdentification()
        // 1.4.1 postcondition on the keys of equipmentByType map
        assertTrue(Equipment.equipmentByType.containsKey(Purse.class));
        // 1.4.2 postcondition on the contents of the set containing the IDs associated with an equipment type
        assertTrue(Equipment.equipmentByType.get(Purse.class).contains(purse_A.getIdentification()));
        // 1.4.3 postcondition on the size of the set containing the IDs for the specified equipment type
        assertEquals(Equipment.equipmentByType.get(Purse.class).size(), oldSizeIdentificationOfPurseB4B + 1);
        // 1.5 postcondition on capacity
        assertEquals(50, purse_A.getCapacity());
    }

    @Test
    void testConstructor_InvalidWeight_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Purse(-10, 70));
    }

    @Test
    void testConstructor_InvalidCapacity_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Purse(10, -10));
    }

    /**
     * CONTENTS
     */

    @Test
    public void testcanHaveAsContents() {
        assertTrue(purseWithZeroCapacity.canHaveAsContents(0));
        assertFalse(purseWithZeroCapacity.canHaveAsContents(-5));
        assertFalse(purseWithZeroCapacity.canHaveAsContents(50));

        assertTrue(emptyPurse500.canHaveAsContents(0));
        assertFalse(emptyPurse500.canHaveAsContents(-5));
        assertTrue(emptyPurse500.canHaveAsContents(250));
        assertTrue(emptyPurse500.canHaveAsContents(500));
        assertFalse(emptyPurse500.canHaveAsContents(5000));

        assertTrue(purseWithCap500AndContents250.canHaveAsContents(0));
        assertFalse(purseWithCap500AndContents250.canHaveAsContents(-5));
        assertTrue(purseWithCap500AndContents250.canHaveAsContents(250));
        assertTrue(purseWithCap500AndContents250.canHaveAsContents(500));
        assertFalse(purseWithCap500AndContents250.canHaveAsContents(5000));

        assertTrue(fullPurse500.canHaveAsContents(0));
        assertFalse(fullPurse500.canHaveAsContents(-5));
        assertTrue(fullPurse500.canHaveAsContents(250));
        assertTrue(fullPurse500.canHaveAsContents(500));
        assertFalse(fullPurse500.canHaveAsContents(5000));
    }

    @Test
    public void testAddtoContents_LegalCase() {
        // 1. effect of setContents()
        // 1.1 postcondition on contents
        // 1.1.1 positive and less than or equal capacity
        emptyPurse500.addToContents(0);
        assertEquals(0, emptyPurse500.getContents());
        emptyPurse500.addToContents(200);
        assertEquals(200, emptyPurse500.getContents());
        emptyPurse500.addToContents(300);
        assertEquals(500, emptyPurse500.getContents());
        emptyPurse500.addToContents(0);
        assertEquals(500, emptyPurse500.getContents());
        purseWithCap500AndContents250.addToContents(0);
        assertEquals(250, purseWithCap500AndContents250.getContents());
        purseWithCap500AndContents250.addToContents(200);
        assertEquals(450, purseWithCap500AndContents250.getContents());
        purseWithCap500AndContents250.addToContents(50);
        assertEquals(500, purseWithCap500AndContents250.getContents());
        purseWithCap500AndContents250.addToContents(0);
        assertEquals(500, purseWithCap500AndContents250.getContents());
        fullPurse500.addToContents(0);
        assertEquals(500, fullPurse500.getContents());
        // 1.1.2 negative amount
        // This will not occur when adding contents to the purse
        // 1.1.3 positive and more than capacity
        fullPurse500.addToContents(10);
        assertEquals(0, fullPurse500.getContents());
        assertEquals(Condition.DESTROYED, fullPurse500.getCondition());
        purseWithCap1000AndContents250.addToContents(800);
        assertEquals(0, purseWithCap1000AndContents250.getContents());
        assertEquals(Condition.DESTROYED, purseWithCap1000AndContents250.getCondition());
    }

    @Test
    public void testAddtoContents_DestroyedPurse() {
        int oldAmount = destroyedPurse.getContents();
        destroyedPurse.addToContents(10);
        assertEquals(oldAmount, destroyedPurse.getContents());
    }

    @Test
    public void testAddtoContents_NegativeAmount() {
        int oldAmount = destroyedPurse.getContents();
        destroyedPurse.addToContents(-10);
        assertEquals(oldAmount, destroyedPurse.getContents());
    }

    @Test
    public void testRemoveFromContents_LegalCase() {
        // 1. effect of setContents()
        // 1.1 postcondition on contents
        // 1.1.1 positive and less than or equal capacity
        fullPurse500.removeFromContents(0);
        assertEquals(500, fullPurse500.getContents());
        fullPurse500.removeFromContents(200);
        assertEquals(300, fullPurse500.getContents());
        fullPurse500.removeFromContents(300);
        assertEquals(0, fullPurse500.getContents());
        fullPurse500.removeFromContents(0);
        assertEquals(0, fullPurse500.getContents());
        purseWithCap500AndContents250.removeFromContents(0);
        assertEquals(250, purseWithCap500AndContents250.getContents());
        purseWithCap500AndContents250.removeFromContents(200);
        assertEquals(50, purseWithCap500AndContents250.getContents());
        purseWithCap500AndContents250.removeFromContents(50);
        assertEquals(0, purseWithCap500AndContents250.getContents());
        purseWithCap500AndContents250.removeFromContents(0);
        assertEquals(0, purseWithCap500AndContents250.getContents());
        emptyPurse500.removeFromContents(0);
        assertEquals(0, emptyPurse500.getContents());
        // 1.1.2 negative amount
        fullPurse500.removeFromContents(510);
        assertEquals(0, fullPurse500.getContents());
        purseWithCap1000AndContents250.removeFromContents(300);
        assertEquals(0, purseWithCap1000AndContents250.getContents());
        // 1.1.3 positive and more than capacity
        // This will not occur when adding contents to the purse
    }

    @Test
    public void testRemoveFromContents_DestroyedPurse() {
        int oldAmount = destroyedPurse.getContents();
        destroyedPurse.removeFromContents(10);
        assertEquals(oldAmount, destroyedPurse.getContents());
    }

    @Test
    public void testRemoveFromtoContents_NegativeAmount() {
        int oldAmount = destroyedPurse.getContents();
        destroyedPurse.removeFromContents(-10);
        assertEquals(oldAmount, destroyedPurse.getContents());
    }

    @Test
    public void testEmpty_LegalCase() {
        // 1. effect of setContents
        // 1.1 postcondition on contents
        // 1.1.1 positive and less than or equal capacity
        emptyPurse500.empty();
        assertEquals(0, emptyPurse500.getContents());
        purseWithCap500AndContents250.empty();
        assertEquals(0, purseWithCap500AndContents250.getContents());
        fullPurse500.empty();
        assertEquals(0, fullPurse500.getContents());
        // 1.1.2 negative amount
        // This will not occur when adding emptying a purse
        // 1.1.3 positive and more than capacity
        // This will not occur when adding emptying a purse
    }

    @Test
    public void testFilltoCapacity_LegalCase() {
        // 1. effect of setContents
        // 1.1 postcondition on contents
        // 1.1.1 positive and less than or equal capacity
        emptyPurse500.fillToCapacity();
        assertEquals(emptyPurse500.getContents(),500);
        purseWithCap500AndContents250.fillToCapacity();
        assertEquals(purseWithCap500AndContents250.getContents(),500);
        fullPurse500.fillToCapacity();
        assertEquals(fullPurse500.getContents(),500);
        // 1.1.2 negative amount
        // This will not occur when adding filling a purse to capacity
        // 1.1.3 positive and more than capacity
        // This will not occur when adding filling a purse to capacity
    }

    @Test
    public void testFillToCapacity_destroyedPurse() {
        int oldAmount = destroyedPurse.getContents();
        destroyedPurse.fillToCapacity();
        assertEquals(oldAmount, destroyedPurse.getContents());
    }

    @Test
    public void testGetFreeSpace_LegalCase() {
        assertEquals(500, emptyPurse500.getFreeSpace());
        assertEquals(250, purseWithCap500AndContents250.getFreeSpace());
        assertEquals(750, purseWithCap1000AndContents250.getFreeSpace());
        assertEquals(0, fullPurse500.getFreeSpace());
    }

    @Test
    public void testTransferFrom_LegalCaseEmptyPurse() {
        emptyPurse500.transferFrom(emptyPurse300);
        assertEquals(0, emptyPurse500.getContents());
        assertEquals(0, emptyPurse300.getContents());
        emptyPurse500.transferFrom(purseWithCap1000AndContents250);
        assertEquals(250, emptyPurse500.getContents());
        assertEquals(0, purseWithCap1000AndContents250.getContents());
        emptyPurse500.transferFrom(fullPurse1000);
        assertEquals(0, emptyPurse500.getContents());
        assertEquals(Condition.DESTROYED, emptyPurse500.getCondition());
        assertEquals(750, fullPurse1000.getContents());
    }

    @Test
    public void testTransferFrom_LegalCaseHalfFullTank() {
        purseWithCap500AndContents250.transferFrom(emptyPurse300);
        assertEquals(250, purseWithCap500AndContents250.getContents());
        assertEquals(0, emptyPurse300.getContents());
        purseWithCap500AndContents250.transferFrom(purseWithCap1000AndContents50);
        assertEquals(300, purseWithCap500AndContents250.getContents());
        assertEquals(0, purseWithCap1000AndContents50.getContents());
        purseWithCap500AndContents250.transferFrom(fullPurse1000);
        assertEquals(700, fullPurse1000.getContents());
        assertEquals(0, purseWithCap500AndContents250.getContents());
        assertEquals(Condition.DESTROYED, purseWithCap500AndContents250.getCondition());
    }

    @Test
    public void testTransferFrom_LegalCaseFullTank() {
        fullPurse500.transferFrom(emptyPurse300);
        assertEquals(500, fullPurse500.getContents());
        assertEquals(0, emptyPurse300.getContents());
        fullPurse500.transferFrom(purseWithCap1000AndContents50);
        assertEquals(500, fullPurse1000.getContents());
        assertEquals(50, purseWithCap1000AndContents50.getContents());
        fullPurse500.transferFrom(fullPurse1000);
        assertEquals(500, fullPurse500.getContents());
        assertEquals(1000, fullPurse1000.getContents());
    }

    @Test
    public void testTransferFrom_SameTank() {
        emptyPurse500.transferFrom(emptyPurse500);
        assertEquals(0, emptyPurse500.getContents());
        purseWithCap500AndContents250.transferFrom(purseWithCap500AndContents250);
        assertEquals(250, purseWithCap500AndContents250.getContents());
        fullPurse500.transferFrom(fullPurse500);
        assertEquals(500, fullPurse500.getContents());
    }

    @Test
    public void testTransferFrom_NullTank() {
        emptyPurse500.transferFrom(null);
        assertEquals(0, emptyPurse500.getContents());
        purseWithCap500AndContents250.transferFrom(null);
        assertEquals(250, purseWithCap500AndContents250.getContents());
        fullPurse500.transferFrom(null);
        assertEquals(500, fullPurse500.getContents());
    }
}
