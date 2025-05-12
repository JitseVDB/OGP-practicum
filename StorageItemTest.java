import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A JUnit (5) test class for testing the non-private methods of the Storage Item Class.
 * The class is abstract, so we can use either one of the subclasses to test the members of the superclass.
 *
 * @author  Jitse Vandenberghe
 * @version 1.0
 */
public class StorageItemTest {

    // BACKPACK
    private static Backpack backpack_A;

    @BeforeEach
    public void setUpEquipment() {
        backpack_A = new Backpack(10, 50, 150);
    }

    /**
     * CONSTRUCTORS
     */

    @Test
    void testConstructor_ValidArguments_ShouldInitializeFields() {
        int oldSizeIdentificationOfBackpackB4C = Equipment.equipmentByType.get(Backpack.class).size();
        Backpack backpack_B = new Backpack(50, 30, 70);

        // 1. effect of super(weight, baseValue)
        // 1.1 postcondition on weight
        assertEquals(10, backpack_A.getWeight());
        // 1.2 postcondition on base value
        assertEquals(50, backpack_A.getBaseValue());
        // 1.3 Postcondition on identification
        // 1.3.1 Postcondition on the uniqueness of the ID and on the fact that the ID is positive
        // Check that the assigned identification is strictly positive
        assertTrue(backpack_A.getIdentification() > 0);
        // (!) At this point, the identification is already present in the map for this equipment type.
        //     Therefore, canHaveAsIdentification will return false, not because the ID is negative,
        //     but because it is no longer unique. This confirms the method correctly enforces uniqueness.
        assertFalse(backpack_A.canHaveAsIdentification(Backpack.class, backpack_A.getIdentification()));
        // 1.4 effect of addIdentification()
        // 1.4.1 postcondition on the keys of equipmentByType map
        assertTrue(Equipment.equipmentByType.containsKey(Backpack.class));
        // 1.4.2 postcondition on the contents of the set containing the IDs associated with an equipment type
        assertTrue(Equipment.equipmentByType.get(Backpack.class).contains(backpack_A.getIdentification()));
        // 1.4.3 postcondition on the size of the set containing the IDs for the specified equipment type
        assertEquals(Equipment.equipmentByType.get(Backpack.class).size(), oldSizeIdentificationOfBackpackB4C + 1);
        // 2. postcondition on capacity
        assertEquals(150, backpack_A.getCapacity());
    }

    @Test
    void testConstructor_InvalidWeight_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Backpack(-10, 30, 70));
    }

    @Test
    void testConstructor_InvalidBaseValue_ShouldThrowException() {
        // Test upper limit canHaveAsValue
        assertThrows(IllegalArgumentException.class, () -> new Backpack(50, 501, 70));
        // Test lower limit canHaveAsValue
        assertThrows(IllegalArgumentException.class, () -> new Backpack(50, -10, 70));
    }

    @Test
    void testConstructor_InvalidCapacity_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Backpack(50, 100, -10));
    }

    /**
     * CAPACITY
     */

    @Test
    public void testIsValidCapacity_allCases() {
        assertTrue(backpack_A.isValidCapacity(10));
        assertTrue(backpack_A.isValidCapacity(0));
        assertFalse(backpack_A.isValidCapacity(-10));
    }

}
