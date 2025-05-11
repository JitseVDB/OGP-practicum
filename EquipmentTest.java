import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A JUnit (5) test class for testing the non-private methods of the Equipment Class.
 * The class is abstract, so we can use either one of the subclasses to test the members of the superclass.
 *
 * @author  Jitse Vandenberghe
 * @version 1.0
 */
public class EquipmentTest {

    // WEAPONS
    private static Weapon weapon_A, weapon_B, weapon_C;

    // ARMOR
    private static Armor armor_A;

    // PURSE
    private static Purse purse_A;

    // BACKPACK
    private static Backpack backpack_A, backpack_B;

    // TESTING EQUIPMENT
    private Equipment equipment;

    @BeforeEach
    public void setUpEquipment() {
        weapon_A = new Weapon(50, 30, 70);
        weapon_B = new Weapon(150, 28, 63);
        weapon_C = new Weapon(140, 28, 63);
        armor_A = new Armor(75, 100, ArmorType.BRONZE);
        purse_A = new Purse(50, 100);
        backpack_A = new Backpack(10, 50, 150);
        backpack_B = new Backpack(15, 45, 175);
    }

    @AfterEach
    void tearDown() {
        Equipment.equipmentByType.clear();
    }


    /**
     * CONSTRUCTORS
     */

    @Test
    void testConstructor_ValidArguments_ShouldInitializeFields() {
        int oldSizeEquipmentByType = Equipment.equipmentByType.get(weapon_A.getClass()).size();
        Equipment equipment_B = new Weapon(50, 30, 70);

        // 1. postcondition on weight
        assertEquals(50, weapon_A.getWeight());

        // 2. postcondition on base value
        assertEquals(30, weapon_A.getBaseValue());

        // 3. Postcondition on identification
        // 3.1 Postcondition on the uniqueness of the ID and on the fact that the ID is positive

        // Check that the assigned identification is strictly positive
        assertTrue(weapon_A.getIdentification() > 0);

        // (!) At this point, the identification is already present in the map for this equipment type.
        //     Therefore, canHaveAsIdentification will return false, not because the ID is negative,
        //     but because it is no longer unique. This confirms the method correctly enforces uniqueness.
        assertFalse(weapon_A.canHaveAsIdentification(Weapon.class, weapon_A.getIdentification()));

        // 4. effect of addIdentification()
        // 4.1 postcondition on the keys of equipmentByType map
        assertTrue(Equipment.equipmentByType.containsKey(Weapon.class));

        // 4.2 postcondition on the contents of the set containing the IDs associated with an equipment type
        assertTrue(Equipment.equipmentByType.get(Weapon.class).contains(weapon_A.getIdentification()));

        // 4.3 postcondition on the size of the set containing the IDs for the specified equipment type
        assertEquals(Equipment.equipmentByType.get(Weapon.class).size(), oldSizeEquipmentByType + 1);
    }

    @Test
    void testConstructor_InvalidWeight_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Armor(-10, 30, ArmorType.TIN));
    }

    @Test
    void testConstructor_InvalidBaseValue_ShouldThrowException() {
        // Test upper limit canHaveAsValue
        assertThrows(IllegalArgumentException.class, () -> new Armor(50, 1001, ArmorType.TIN));
        // Test lower limit canHaveAsValue
        assertThrows(IllegalArgumentException.class, () -> new Armor(50, -10, ArmorType.TIN));
    }

    /**
     * WEIGHT
     */

    @Test
    public void testIsValidWeight_allCases() {
        assertTrue(Equipment.isValidWeight(100));
        assertTrue(Equipment.isValidWeight(0));
        assertFalse(Equipment.isValidWeight(-100));
        assertTrue(Equipment.isValidWeight(Integer.MAX_VALUE));

    }

    /**
     * IDENTIFICATION
     */

    @Test
    public void testCanHaveAsIdentification_allCases() {
        long identification_A = weapon_A.getIdentification();

        // 1. positive
        assertTrue(weapon_A.canHaveAsIdentification(weapon_A.getClass(), Math.abs(weapon_A.generateIdentification())));
        assertFalse(weapon_A.canHaveAsIdentification(weapon_A.getClass(), -(weapon_A.generateIdentification())));

        // 2. Uniqueness of identification
        assertTrue(weapon_A.canHaveAsIdentification(weapon_A.getClass(), weapon_A.generateIdentification()));
        assertFalse(weapon_A.canHaveAsIdentification(weapon_A.getClass(), identification_A));
    }

    @Test
    public void testIsUniqueForType_allCases() {
        long identification_A = weapon_A.getIdentification();

        // 1. No existing identification numbers within equipment type
        assertTrue(Equipment.isUniqueForType(Armor.class, identification_A));

        // 2. Existing identification numbers within equipment type
        assertFalse(Equipment.isUniqueForType(Weapon.class, identification_A));
        assertTrue(Equipment.isUniqueForType(Weapon.class, weapon_A.generateIdentification()));
    }

    @Test
    public void testGenerateIdentification_allCases() {
        long identification = weapon_A.generateIdentification();

        // 1. postcondition on identification number
        assertTrue(weapon_A.canHaveAsIdentification(weapon_A.getClass(), identification));
    }

    /**
     * VALUE
     */

    @Test
    public void getCurrentValue_allCases() {
        assertTrue(weapon_A.getCurrentValue() >= 0);
        assertTrue(armor_A.getCurrentValue() >= 0);
        assertTrue(purse_A.getCurrentValue() >= 0);
        assertTrue(backpack_A.getCurrentValue() >= 0);
    }

    @Test
    public void testCanHaveAsValue_allCases() {
        assertTrue(weapon_A.canHaveAsValue(weapon_A.getMaximumValue()-1));
        assertTrue(backpack_A.canHaveAsValue(backpack_A.getMaximumValue()));
        assertTrue(purse_A.canHaveAsValue(0));
        assertFalse(armor_A.canHaveAsValue(armor_A.getMaximumValue()+1));
        assertFalse(weapon_A.canHaveAsValue(-armor_A.getMaximumValue()));
    }

    /**
     * BACKPACK
     */

    @Test
    public void testHasProperBackpack_allCases() {
        // Ensure backpack_A is properly initialized
        assertNotNull(backpack_A);
        assertNotNull(weapon_A);

        weapon_A.setBackpack(backpack_A);

        // 1. We can check proper backpacks.
        assertTrue(weapon_A.hasProperBackpack());
        assertTrue(backpack_A.hasAsItem(weapon_A));

        // 2.There is no way of constructing items with an invalid backpack.
    }

    @Test
    public void testSetBackpack_allCases() {
        // Ensure backpack_A is properly initialized
        assertNotNull(backpack_A);
        assertNotNull(weapon_A);

        weapon_A.setBackpack(backpack_A);

        // 1. postcondition on backpack
        assertEquals(backpack_A, weapon_A.getBackpack());

        weapon_A.setBackpack(backpack_B);

        // 2. effect on old backpack when setting a different backpack
        assertFalse(backpack_A.hasAsItem(weapon_A));

        // 3. effect on new backpack when setting a different backpack
        assertTrue(backpack_B.hasAsItem(weapon_A));
    }

    @Test
    void testSetBackpack_InvalidBackpack_ShouldThrowException() {
        // 1. Adding item exceeding capacity
        assertThrows(IllegalArgumentException.class, () -> weapon_B.setBackpack(backpack_A));

        // 2. Adding item equalling capacity
        assertThrows(IllegalArgumentException.class, () -> weapon_C.setBackpack(backpack_A));
    }
}