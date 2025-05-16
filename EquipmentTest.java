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
    // HERO
    private static Hero hero_A, hero_B;

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
        hero_A = new Hero("HeroA", 10, 10);
        hero_B = new Hero("HeroB", 10, 10);
        weapon_A = new Weapon(10, 70);
        weapon_B = new Weapon(20, 63);
        weapon_C = new Weapon(10, 63);
        armor_A = new Armor(75, 100, ArmorType.BRONZE);
        purse_A = new Purse(50, 100);
        backpack_A = new Backpack(10, 50, 150);
        backpack_B = new Backpack(15, 45, 175);
        backpack_A.setOwner(hero_A);
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
        Equipment equipment_B = new Weapon(50, 70);

        // 1. postcondition on weight
        assertEquals(10, weapon_A.getWeight());

        // 2. postcondition on base value
        assertEquals(0, weapon_A.getBaseValue());

        // 3. Postcondition on identification
        // 3.1 Postcondition on the uniqueness of the ID and on the fact that the ID is positive

        // Check that the assigned identification is strictly positive
        assertTrue(weapon_A.getIdentification() > 0);

        // (!) At this point, the identification is already present in the map for this equipment type.
        //     Therefore, canHaveAsIdentification will return false, not because the ID is negative,
        //     but because it is no longer unique. This confirms the method correctly enforces uniqueness.
        assertFalse(weapon_A.canHaveAsIdentification(Weapon.class, weapon_A.getIdentification()));

        // 4. postcondition on condition
        assertFalse(weapon_A.isDestroyed());

        // 5. effect of addIdentification()
        // 5.1 postcondition on the keys of equipmentByType map
        assertTrue(Equipment.equipmentByType.containsKey(Weapon.class));

        // 5.2 postcondition on the contents of the set containing the IDs associated with an equipment type
        assertTrue(Equipment.equipmentByType.get(Weapon.class).contains(weapon_A.getIdentification()));

        // 5.3 postcondition on the size of the set containing the IDs for the specified equipment type
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
        assertTrue(Equipment.isUniqueForType(Purse.class, weapon_A.generateIdentification()));

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
     * OWNER
     */

    @Test
    public void testSetOwner_noPreviousOwner() {
        weapon_A.setOwner(hero_A);

        // 1. postcondition on owner
        assertEquals(hero_A, weapon_A.getOwner());

        // 2. effect on old owner when setting different owner
        // not applicable

        // 3. effect on old backpack when setting different owner
        // not applicable

        // 4. effect on new owner when setting different owner
        // 4.1 effect of addToAnchorPoint
        assertTrue(hero_A.hasAsItem(weapon_A));
    }

    @Test
    public void testSetOwner_PreviousDifferentOwner() {
        weapon_A.setOwner(hero_A);
        weapon_A.setOwner(hero_B);


        // 1. postcondition on owner
        assertEquals(hero_B, weapon_A.getOwner());

        // 2. effect on old owner when setting different owner
        // 2.1 effect of anchorpoint.setItem
        assertFalse(hero_A.hasAsItem(weapon_A));

        // 3. effect on old backpack when setting different owner
        // not applicable

        // 4. effect on new owner when setting different owner
        // 4.1 effect of addToAnchorPoint
        assertTrue(hero_B.hasAsItem(weapon_A));
    }

    @Test
    public void testSetOwner_PreviousSameOwner() {
        weapon_A.setOwner(hero_A);
        weapon_A.setOwner(hero_A);

        // 1. postcondition on owner
        assertEquals(hero_A, weapon_A.getOwner());

        // 2. effect on old owner when setting different owner
        // not applicable, this effect is undone by effect (4)

        // 3. effect on old backpack when setting different owner
        // not applicable

        // 4. effect on new owner when setting different owner
        assertTrue(hero_A.hasAsItem(weapon_A));
    }

    @Test
    public void testSetOwner_PreviousDifferentOwnerInBackpack() {
        weapon_A.setBackpack(backpack_A);
        weapon_A.setOwner(hero_B);

        // 1. postcondition on owner
        assertEquals(hero_B, weapon_A.getOwner());

        // 2. effect on old owner when setting different owner
        // not applicable

        // 3. effect on backpack
        // 3.1 effect on setBackpack
        // 3.1.1 postcondition on backpack
        assertNull(weapon_A.getBackpack());
        // 3.1.2 postcondition on owner
        // not applicable, this effect is undone by later
        // 3.1.3 effect on old backpack
        assertFalse(backpack_A.hasAsItem(weapon_A));

        // 4. effect on new owner when setting different owner
        assertTrue(hero_B.hasAsItem(weapon_A));
    }

    @Test
    public void testSetOwner_PreviousSameOwnerInBackpack() {
        weapon_A.setBackpack(backpack_A);
        weapon_A.setOwner(hero_A);

        // 1. postcondition on owner
        assertEquals(hero_A, weapon_A.getOwner());

        // 2. effect on old owner when setting different owner
        // not applicable

        // 3. effect on backpack
        // 3.1 effect on setBackpack
        // 3.1.1 postcondition on backpack
        assertNull(weapon_A.getBackpack());
        // 3.1.2 postcondition on owner
        // not applicable, this effect is undone by later
        // 3.1.3 effect on old backpack
        assertFalse(backpack_A.hasAsItem(weapon_A));

        // 4. effect on new owner when setting different owner
        assertTrue(hero_A.hasAsItem(weapon_A));
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

        // 1. postc
        assertTrue(weapon_A.hasProperBackpack());
        assertTrue(backpack_A.hasAsItem(weapon_A));

        // 2.There is no way of constructing items with an invalid backpack.
    }

    @Test
    public void testSetBackpack_noOwnerToBackpack() {
        // Ensure backpack_A is properly initialized
        assertNotNull(backpack_A);
        assertNotNull(weapon_A);

        weapon_A.setBackpack(backpack_A);

        // 1. postcondition on backpack
        assertEquals(backpack_A, weapon_A.getBackpack());

        // 2. postcondition on owner
        assertEquals(hero_A, weapon_A.getOwner());

        // 3. effect on old backpack when setting a different backpack
        // not applicable

        // 4. effect on new backpack when setting a backpack different from the old backpack
        assertTrue(backpack_A.hasAsItem(weapon_A));

    }

    @Test
    public void testSetBackpack_anchorToBackpackWithinSameOwner() {
        assertNotNull(backpack_A);
        assertNotNull(weapon_A);

        weapon_A.setOwner(hero_A);

        weapon_A.setBackpack(backpack_A);

        // 1. postcondition on backpack
        assertEquals(backpack_A, weapon_A.getBackpack());

        // 2. postcondition on owner
        assertEquals(hero_A, weapon_A.getOwner());

        // 3. effect on old backpack when setting a different backpack
        // not applicable

        // 4. effect on new backpack when setting a different backpack
        assertTrue(backpack_A.hasProperBackpack());
    }

    @Test
    void testSetBackpack_InvalidBackpack_ShouldThrowException() {
        // 1. Adding item exceeding capacity
        assertThrows(IllegalArgumentException.class, () -> weapon_B.setBackpack(backpack_A));
    }
}