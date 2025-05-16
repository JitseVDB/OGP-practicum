import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A JUnit (5) test class for testing the non-private methods of the Backpack Class.
 *
 * @author  Jitse Vandenberghe
 * @version 1.0
 */
public class BackpackTest {

    // PURSE
    private static Backpack backpack_A, fullBackpack500, fullBackpack1000, emptyBackpack500, emptyBackpack300,
            backpackWithCap500AndTotalWeight250, backpackWithCap1000AndTotalWeight250,
            backpackWithCap1000AndTotalWeight50, backpackWithZeroCapacity, backpack2Items;

    // WEAPON
    private static Weapon weapon40, weapon115, weapon240A, weapon240B, weapon490, weapon790, weapon990, weapon10A, weapon10B;

    @BeforeEach
    public void setUpEquipment() {
        weapon40 = new Weapon(40, 70);
        weapon115 = new Weapon(115, 70);
        weapon240A = new Weapon(240, 70);
        weapon240B = new Weapon(240, 70);
        weapon490 = new Weapon(490, 70);
        weapon790 = new Weapon(790, 70);
        weapon990 = new Weapon(990, 70);
        weapon10A = new Weapon(10, 70);
        weapon10B = new Weapon(10, 70);

        backpack_A = new Backpack(15, 250, 500);
        fullBackpack500 = new Backpack(10,250, 500);
        weapon490.setBackpack(fullBackpack500);
        fullBackpack1000 = new Backpack(10, 250, 1000);
        weapon990.setBackpack(fullBackpack1000);
        emptyBackpack500 = new Backpack(10, 250, 500);
        emptyBackpack300 = new Backpack(10, 250, 300);
        backpackWithCap500AndTotalWeight250 = new Backpack(10,250, 500);
        weapon240A.setBackpack(backpackWithCap500AndTotalWeight250);
        backpackWithCap1000AndTotalWeight50 = new Backpack(10, 250, 1000);
        weapon40.setBackpack(backpackWithCap1000AndTotalWeight50);
        backpackWithCap1000AndTotalWeight250 = new Backpack(10,250, 1000);
        weapon240B.setBackpack(backpackWithCap1000AndTotalWeight250);
        backpackWithZeroCapacity = new Backpack(10, 250, 0);
        backpack2Items = new Backpack(10, 250, 40);
        weapon10A.setBackpack(backpack2Items);
        weapon10B.setBackpack(backpack2Items);
    }


    /**
     * CONSTRUCTOR
     */

    @Test
    void testConstructor_ValidArguments_ShouldInitializeFields() {
        int oldSizeIdentificationOfBackpackB4B = Equipment.equipmentByType.get(Backpack.class).size();
        Backpack backpack_B = new Backpack(10, 125, 250);

        // 1. effect of super(weight, baseValue, capacity)
        // 1.1 postcondition on weight
        assertEquals(15, backpack_A.getWeight());
        // 1.2 postcondition on base value
        assertEquals(250, backpack_A.getBaseValue());
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
        assertEquals(Equipment.equipmentByType.get(Backpack.class).size(), oldSizeIdentificationOfBackpackB4B + 1);
        // 1.5 postcondition on capacity
        assertEquals(500, backpack_A.getCapacity());
    }

    @Test
    void testConstructor_InvalidWeight_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Backpack(-10, 250, 500));
    }

    @Test
    void testConstructor_InvalidBaseValue_ShouldThrowException() {
        // Test upper limit canHaveAsValue
        assertThrows(IllegalArgumentException.class, () -> new Backpack(10, 501, 500));
        // Test lower limit canHaveAsValue
        assertThrows(IllegalArgumentException.class, () -> new Backpack(10, -1, 500));
    }

    @Test
    void testConstructor_InvalidCapacity_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Backpack(10, 250, -10));
    }

    /**
     * CONTENTS
     */

    @Test
    public void testCanHaveAsItem_allCases() {
        // 1. null items
        assertFalse(backpack_A.canHaveAsItem(null));
        // 2. weight capacity
        // 2.1 above capacity
        assertFalse(backpackWithCap1000AndTotalWeight250.canHaveAsItem(weapon790));
        // 2.2 below capacity
        assertTrue(backpackWithCap500AndTotalWeight250.canHaveAsItem(weapon115));
    }

    @Test
    public void testGetNbItemsWithID_allCases() {
        assertEquals(1, fullBackpack500.getNbItemsWithID(weapon490.getIdentification()));
        assertEquals(0, fullBackpack500.getNbItemsWithID(weapon990.getIdentification()));
        assertEquals(0, fullBackpack500.getNbItemsWithID(weapon490.generateIdentification()));
        assertEquals(1, backpack2Items.getNbItemsWithID(weapon10A.getIdentification()));
        assertEquals(1, backpack2Items.getNbItemsWithID(weapon10B.getIdentification()));
        assertEquals(0, backpack2Items.getNbItemsWithID(weapon790.getIdentification()));
    }

    @Test
    public void testGetItemAtWithID_ValidIDAndIndex() {
        Backpack testBackpack = new Backpack(10, 100, 1000);
        Weapon weapon1 = new Weapon(100, 21);
        Weapon weapon2 = new Weapon(100, 21); // same ID

        weapon1.setBackpack(testBackpack);
        weapon2.setBackpack(testBackpack);

        long id1 = weapon1.getIdentification();
        long id2 = weapon2.getIdentification();

        assertEquals(weapon1, testBackpack.getItemAtWithID(id1, 1));
        assertEquals(weapon2, testBackpack.getItemAtWithID(id2, 1));
    }

    @Test
    public void testGetItemAtWithID_UnknownIdentification_ThrowsIllegalStateException() {
        long unknownID = 100;

        assertThrows(IllegalStateException.class, () -> {
            emptyBackpack300.getItemAtWithID(unknownID, 1);
        });
    }

    @Test
    public void testGetItemAtWithID_IndexLessThanOne_ThrowsIndexOutOfBoundsException() {
        weapon115.setBackpack(emptyBackpack500);

        long id = weapon115.getIdentification();

        assertThrows(IndexOutOfBoundsException.class, () -> {
            emptyBackpack500.getItemAtWithID(id, 0);
        });
    }

    @Test
    public void testGetItemAtWithID_IndexTooLarge_ThrowsIndexOutOfBoundsException() {
        weapon240B.setBackpack(emptyBackpack300);

        long id = weapon240B.getIdentification();

        assertThrows(IndexOutOfBoundsException.class, () -> {
            emptyBackpack300.getItemAtWithID(id, 2);
        });
    }

    @Test
    public void testHasAsItem_AllCases() {
        assertTrue(fullBackpack500.hasAsItem(weapon490));
        assertFalse(fullBackpack500.hasAsItem(weapon40));
        assertTrue(backpack2Items.hasAsItem(weapon10A));
        assertTrue(backpack2Items.hasAsItem(weapon10B));
        assertFalse(fullBackpack500.hasAsItem(weapon990));
        assertFalse(backpack2Items.hasAsItem(weapon790));
    }

    @Test
    public void testContainsItemWithIdentification_AllCases() {
        assertTrue(fullBackpack500.containsItemWithIdentification(weapon490.getIdentification()));
        assertFalse(fullBackpack500.containsItemWithIdentification(weapon40.getIdentification()));
        assertTrue(backpack2Items.containsItemWithIdentification(weapon10A.getIdentification()));
        assertTrue(backpack2Items.containsItemWithIdentification(weapon10B.getIdentification()));
        assertFalse(fullBackpack500.containsItemWithIdentification(weapon990.getIdentification()));
        assertFalse(backpack2Items.containsItemWithIdentification(weapon790.getIdentification()));
    }

    /**
     * WEIGHT
     */

    @Test
    public void testGetTotalWeight_allCases() {
        assertEquals(500, fullBackpack500.getTotalWeight());
        assertEquals(1000, fullBackpack1000.getTotalWeight());
        assertEquals(50, backpackWithCap1000AndTotalWeight50.getTotalWeight());
        assertEquals(250, backpackWithCap500AndTotalWeight250.getTotalWeight());
        assertEquals(250, backpackWithCap1000AndTotalWeight250.getTotalWeight());
        assertEquals(10, emptyBackpack300.getTotalWeight());
    }

    /**
     * VALUE
     */

    @Test
    public void testCalculateCurrentValue_allCases() {
        // 1. Empty backpack
        assertEquals(250, emptyBackpack300.getCurrentValue());
        // 2. Full purse
        assertEquals(390, fullBackpack500.getCurrentValue());
        // 3. Purse with multiple items
        assertEquals(530, backpack2Items.getCurrentValue());
    }

    /**
     * CONDITION
     */

    @Test
    public void testDestroy_GoodConditionWithContent() {
        backpack2Items.destroy();
        // 1. effect of setCondition
        assertEquals(Condition.DESTROYED, backpack2Items.getCondition());
        // 2. effect of items.setBackpack(null)
        // 2.1 postcondition on backpack
        assertNull(weapon10A.getBackpack());
        assertNull(weapon10B.getBackpack());
        // 2.2 postcondition on owner
        assertNull(weapon10A.getOwner());
        assertNull(weapon10B.getOwner());
        // 3.1 effect on old backpack
        assertFalse(backpack2Items.hasAsItem(weapon10A));
        assertFalse(backpack2Items.hasAsItem(weapon10B));
    }

    @Test
    public void testDestroy_GoodConditionWithoutContent() {
        emptyBackpack300.destroy();
        // 1. effect of setCondition
        assertEquals(Condition.DESTROYED, emptyBackpack300.getCondition());
        // 2. effect of items.setBackpack(null)
        // 2.1 postcondition on backpack
        // not applicable
        // 2.2 postcondition on owner
        // not applicable
        // 3.1 effect on old backpack
        // not applicable
    }

    @Test
    public void testDestroy_DestroyedCondition() {
        fullBackpack1000.destroy();
        fullBackpack1000.destroy();
        // 1. effect of setCondition
        assertEquals(Condition.DESTROYED, fullBackpack1000.getCondition());
        // 2. effect of items.setBackpack(null)
        // 2.1 postcondition on backpack
        assertNull(weapon990.getBackpack());
        // 2.2 postcondition on owner
        assertNull(weapon990.getOwner());
        // 3.1 effect on old backpack
        assertFalse(fullBackpack1000.hasAsItem(weapon990));
    }


}
