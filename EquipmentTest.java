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
    private static Weapon weapon_A, weapon_B, weapon_C, weapon_destroyed;

    // ARMOR
    private static Armor armor_A;

    // PURSE
    private static Purse purse_A;

    // BACKPACK
    private static Backpack backpack_A, backpack_B;

    // TESTING EQUIPMENT
    private Equipment equipment;

    // AXULIARY METHOD
    public int getOldSizeA() {
        int oldSizeA;
        if (backpack_A.contents.get(weapon_A.getIdentification()) != null) {
            oldSizeA = backpack_A.contents.get(weapon_A.getIdentification()).size();
        } else {
            oldSizeA = 0;
        }

        return oldSizeA;
    }

    // AXULIARY METHOD
    public int getOldSizeB() {
        int oldSizeA;
        if (backpack_B.contents.get(weapon_A.getIdentification()) != null) {
            oldSizeA = backpack_B.contents.get(weapon_A.getIdentification()).size();
        } else {
            oldSizeA = 0;
        }

        return oldSizeA;
    }

    @BeforeEach
    public void setUpEquipment() {
        hero_A = new Hero("HeroA", 10, 10);
        hero_B = new Hero("HeroB", 10, 10);
        weapon_A = new Weapon(10, 70);
        weapon_B = new Weapon(20, 63);
        weapon_C = new Weapon(300, 63);
        weapon_destroyed = new Weapon(10, 70);
        weapon_destroyed.destroy();
        armor_A = new Armor(75, 100, ArmorType.BRONZE);
        purse_A = new Purse(50, 100);
        backpack_A = new Backpack(10, 50, 150);
        backpack_B = new Backpack(15, 45, 175);
        backpack_A.setOwner(hero_A);
        backpack_B.setOwner(hero_B);
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
    public void testcanHaveAsWeight_allCases() {
        assertTrue(Equipment.canHaveAsWeight(100));
        assertTrue(Equipment.canHaveAsWeight(0));
        assertFalse(Equipment.canHaveAsWeight(-100));
        assertTrue(Equipment.canHaveAsWeight(Integer.MAX_VALUE));

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

        // 2. effect of removeAsItem on old owner
        // not applicable

        // 3. effect of setBackpack(null)
        // not applicable

        // 4. effect of addAsItem on new owner
        // 4.1 effect of addToAnchorPoint
        assertTrue(hero_A.hasAsItem(weapon_A));
    }

    @Test
    public void testSetOwner_PreviousDifferentOwner() {
        weapon_A.setOwner(hero_A);
        weapon_A.setOwner(hero_B);


        // 1. postcondition on owner
        assertEquals(hero_B, weapon_A.getOwner());

        // 2. effect of removeAsItem on old owner
        // 2.1 effect of anchorpoint.setItem
        assertFalse(hero_A.hasAsItem(weapon_A));

        // 3. effect of setBackpack(null)
        // not applicable

        // 4. effect of addAsItem on new owner
        // 4.1 effect of addToAnchorPoint
        assertTrue(hero_B.hasAsItem(weapon_A));
    }

    @Test
    public void testSetOwner_PreviousSameOwner() {
        weapon_A.setOwner(hero_A);
        weapon_A.setOwner(hero_A);

        // 1. postcondition on owner
        assertEquals(hero_A, weapon_A.getOwner());

        // 2. effect of removeAsItem on old owner
        // not applicable, this effect is undone by effect (4)

        // 3. effect of setBackpack(null)
        // not applicable

        // 4. effect of addAsItem on new owner
        // 4.1 effect of addToAnchorPoint
        assertTrue(hero_A.hasAsItem(weapon_A));
    }

    @Test
    public void testSetOwner_PreviousDifferentOwnerInBackpack() {
        weapon_A.setBackpack(backpack_A);
        weapon_A.setOwner(hero_B);

        // 1. postcondition on owner
        assertEquals(hero_B, weapon_A.getOwner());

        // 2. effect of removeAsItem on old owner
        // not applicable

        // 3. effect of setBackpack(null)
        // 3.1 postcondition on backpack
        assertNull(weapon_A.getBackpack());
        // 3.1 postcondition on owner
        // not applicable, this effect is undone by later
        // 3.1 effect on old backpack
        assertFalse(backpack_A.hasAsItem(weapon_A));

        // 4. effect of addAsItem on new owner
        // 4.1 effect of addToAnchorPoint
        assertTrue(hero_B.hasAsItem(weapon_A));
    }

    @Test
    public void testSetOwner_PreviousSameOwnerInBackpack() {
        weapon_A.setBackpack(backpack_A);
        weapon_A.setOwner(hero_A);

        // 1. postcondition on owner
        assertEquals(hero_A, weapon_A.getOwner());

        // 2. effect of removeAsItem on old owner
        // not applicable

        // 3. effect of setBackpack(null)
        // 3.1 postcondition on backpack
        assertNull(weapon_A.getBackpack());
        // 3.1 postcondition on owner
        // not applicable, this effect is undone by later
        // 3.1 effect on old backpack
        assertFalse(backpack_A.hasAsItem(weapon_A));

        // 4. effect of addAsItem on new owner
        // 4.1 effect of addToAnchorPoint
        assertTrue(hero_A.hasAsItem(weapon_A));
    }

    @Test
    public void testSetOwner_nullNoPreviousOwner() {
        weapon_A.setOwner(null);

        // 1. postcondition on owner
        assertNull(weapon_A.getOwner());

        // 2. effect of removeAsItem on old owner
        // not applicable

        // 3. effect of setBackpack(null)
        // not applicable

        // 4. effect of addAsItem on new owner
        // 4.1 effect of addToAnchorPoint
        // not applicable
    }

    @Test
    public void testSetOwner_nullPreviousOwner() {
        weapon_A.setOwner(hero_A);
        weapon_A.setOwner(null);

        // 1. postcondition on owner
        assertNull(weapon_A.getOwner());

        // 2. effect of removeAsItem on old owner
        assertFalse(hero_A.hasAsItem(weapon_A));

        // 3. effect of setBackpack(null)
        // not applicable

        // 4. effect of addAsItem on new owner
        // 4.1 effect of addToAnchorPoint
        // not applicable
    }

    @Test
    public void testSetOwner_nullPreviousBackpack() {
        weapon_A.setBackpack(backpack_A);
        weapon_A.setOwner(null);

        // 1. postcondition on owner
        assertNull(weapon_A.getOwner());

        // 2. effect of removeAsItem on old owner
        // not applicable

        // 3. effect of setBackpack(null)
        // 3.1 postcondition on backpack
        assertNull(weapon_A.getBackpack());
        // 3.1 postcondition on owner
        // not applicable, this effect is undone by later
        // 3.1 effect on old backpack
        assertFalse(backpack_A.hasAsItem(weapon_A));

        // 4. effect of addAsItem on new owner
        // 4.1 effect of addToAnchorPoint
        // not applicable
    }

    @Test
    void testSetOwner_InvalidOwner_ShouldThrowException() {
        weapon_A.setOwner(hero_A);
        weapon_B.setOwner(hero_A);
        armor_A.setOwner(hero_A);
        purse_A.setOwner(hero_A);
        // Backpack_A already belongs to hero_A
        // 1. No empty anchor points.
        assertThrows(IllegalArgumentException.class, () -> weapon_C.setOwner(hero_A));
    }

    /**
     * BACKPACK
     */

    @Test
    public void testSetBackpack_noOwnerToBackpack() {
        // Ensure everything is properly initialized
        assertNotNull(backpack_A);
        assertNotNull(weapon_A);

        int oldSizeA = getOldSizeA();

        weapon_A.setBackpack(backpack_A);

        // 1. postcondition on backpack
        assertEquals(backpack_A, weapon_A.getBackpack());

        // 2. postcondition on owner
        assertEquals(hero_A, weapon_A.getOwner());

        // 3. effect removeItem on old backpack
        // not applicable

        // 4. effect addItem on new backpack
        assertTrue(backpack_A.hasAsItem(weapon_A));
        // 4.1 postcondition on map regarding identification as a key
        assertTrue(backpack_A.contents.containsKey(weapon_A.getIdentification()));
        // 4.2 postcondition on map regarding item as value linked to key
        assertTrue(backpack_A.contents.get(weapon_A.getIdentification()).contains(weapon_A));
        // 4.3 postcondition regarding size of list within map
        assertEquals(oldSizeA+1, backpack_A.contents.get(weapon_A.getIdentification()).size());
    }

    @Test
    public void testSetBackpack_SetToAnotherBackpack() {
        // Ensure everything is properly initialized
        assertNotNull(backpack_A);
        assertNotNull(weapon_A);

        weapon_A.setBackpack(backpack_A);
        int oldSizeA = getOldSizeA();
        int oldSizeB = getOldSizeB();
        weapon_A.setBackpack(backpack_B);

        // 1. postcondition on backpack
        assertEquals(backpack_B, weapon_A.getBackpack());

        // 2. postcondition on owner
        assertEquals(hero_B, weapon_A.getOwner());

        // 3. effect removeItem on old backpack
        // 3.1 postcondition on contents of list
        assertFalse(backpack_A.hasAsItem(weapon_A));
        // 3.2 postcondition on size of list
        assertEquals(oldSizeA-1, backpack_A.contents.get(weapon_A.getIdentification()).size());

        // 4. effect addItem on new backpack
        assertTrue(backpack_B.hasAsItem(weapon_A));
        // 4.1 postcondition on map regarding identification as a key
        assertTrue(backpack_B.contents.containsKey(weapon_A.getIdentification()));
        // 4.2 postcondition on map regarding item as value linked to key
        assertTrue(backpack_B.contents.get(weapon_A.getIdentification()).contains(weapon_A));
        // 4.3 postcondition regarding size of list within map
        assertEquals(oldSizeB+1, backpack_B.contents.get(weapon_A.getIdentification()).size());
    }

    @Test
    public void testSetBackpack_SetToSameBackpack() {
        // Ensure everything is properly initialized
        assertNotNull(backpack_A);
        assertNotNull(weapon_A);

        weapon_A.setBackpack(backpack_A);
        int oldSizeA = getOldSizeA();
        weapon_A.setBackpack(backpack_A);

        // 1. postcondition on backpack
        assertEquals(backpack_A, weapon_A.getBackpack());

        // 2. postcondition on owner
        assertEquals(hero_A, weapon_A.getOwner());

        // 3. effect removeItem on old backpack
        // not applicable

        // 4. effect addItem on new backpack
        assertTrue(backpack_A.hasAsItem(weapon_A));
        // 4.1 postcondition on map regarding identification as a key
        assertTrue(backpack_A.contents.containsKey(weapon_A.getIdentification()));
        // 4.2 postcondition on map regarding item as value linked to key
        assertTrue(backpack_A.contents.get(weapon_A.getIdentification()).contains(weapon_A));
        // 4.3 postcondition regarding size of list within map
        // effect is undone
    }

    @Test
    public void testSetBackpack_NullNoPreviousBackpack() {
        weapon_A.setBackpack(null);

        // 1. postcondition on backpack
        assertNull(weapon_A.getBackpack());

        // 2. postcondition on owner
        assertNull(weapon_A.getOwner());

        // 3. effect removeItem on old backpack
        // not applicable

        // 4. effect addItem on new backpack
        // not applicable
    }

    @Test
    public void testSetBackpack_NullPreviousBackpack() {
        weapon_A.setBackpack(backpack_A);

        int oldSizeA = getOldSizeA();

        weapon_A.setBackpack(null);

        // 1. postcondition on backpack
        assertNull(weapon_A.getBackpack());

        // 2. postcondition on owner
        assertNull(weapon_A.getOwner());

        // 3. effect removeItem on old backpack
        // 3.1 postcondition on contents of list
        assertFalse(backpack_A.hasAsItem(weapon_A));
        // 3.2 postcondition on size of list
        assertEquals(oldSizeA-1, backpack_A.contents.get(weapon_A.getIdentification()).size());

        // 4. effect addItem on new backpack
        // not applicable
    }

    @Test
    public void testSetBackpack_anchorToBackpackWithinSameOwner() {
        // Ensure everything is properly initialized
        assertNotNull(backpack_A);
        assertNotNull(weapon_A);

        weapon_A.setOwner(hero_A);
        int oldSizeA = getOldSizeA();
        weapon_A.setBackpack(backpack_A);

        // 1. postcondition on backpack
        assertEquals(backpack_A, weapon_A.getBackpack());

        // 2. postcondition on owner
        assertEquals(hero_A, weapon_A.getOwner());

        // 3. effect removeItem on old backpack
        // not applicable

        // 4. effect addItem on new backpack
        assertTrue(backpack_A.hasAsItem(weapon_A));
        // 4.1 postcondition on map regarding identification as a key
        assertTrue(backpack_A.contents.containsKey(weapon_A.getIdentification()));
        // 4.2 postcondition on map regarding item as value linked to key
        assertTrue(backpack_A.contents.get(weapon_A.getIdentification()).contains(weapon_A));
        // 4.3 postcondition regarding size of list within map
        assertEquals(oldSizeA+1, backpack_A.contents.get(weapon_A.getIdentification()).size());
    }

    @Test
    public void testSetBackpack_anchorToBackpackWithinDifferentOwner() {
        // Ensure everything is properly initialized
        assertNotNull(backpack_A);
        assertNotNull(weapon_A);

        weapon_B.setOwner(hero_A);
        int oldSizeA = getOldSizeA();
        weapon_A.setBackpack(backpack_A);

        // 1. postcondition on backpack
        assertEquals(backpack_A, weapon_A.getBackpack());

        // 2. postcondition on owner
        assertEquals(hero_A, weapon_A.getOwner());

        // 3. effect removeItem on old backpack
        // not applicable

        // 4. effect addItem on new backpack
        assertTrue(backpack_A.hasAsItem(weapon_A));
        // 4.1 postcondition on map regarding identification as a key
        assertTrue(backpack_A.contents.containsKey(weapon_A.getIdentification()));
        // 4.2 postcondition on map regarding item as value linked to key
        assertTrue(backpack_A.contents.get(weapon_A.getIdentification()).contains(weapon_A));
        // 4.3 postcondition regarding size of list within map
        assertEquals(oldSizeA+1, backpack_A.contents.get(weapon_A.getIdentification()).size());
    }

    @Test
    void testSetBackpack_InvalidBackpack_ShouldThrowException() {
        // 1. Adding item exceeding capacity
        assertThrows(IllegalArgumentException.class, () -> weapon_C.setBackpack(backpack_A));
    }

    /**
     * SHININESS
     */

    @Test
    void testSetShiny_allCases() {
        // 1. shiny to not shiny
        weapon_A.setShiny(false);
        assertFalse(weapon_A.isShiny());
        // 2. not shiny to shiny
        backpack_A.setShiny(true);
        assertTrue(backpack_A.isShiny());
        // 3. shiny to shiny
        weapon_A.setShiny(true);
        assertTrue(weapon_A.isShiny());
        // 4. not shiny to not shiny
        backpack_A.setShiny(false);
        assertFalse(backpack_A.isShiny());
    }

    /**
     * CONDITION
     */

    @Test
    void testSetCondition_allCases() {
        // 1. good to destroyed
        weapon_A.setCondition(Condition.DESTROYED);
        assertTrue(weapon_A.isDestroyed());
        // 2. destroyed to good
        weapon_destroyed.setCondition(Condition.GOOD);
        assertTrue(weapon_destroyed.isDestroyed());
        // 3. good to good
        weapon_B.setCondition(Condition.GOOD);
        assertFalse(weapon_B.isDestroyed());
        // 4. destroyed to destroyed
        weapon_destroyed.setCondition(Condition.DESTROYED);
        assertTrue(weapon_destroyed.isDestroyed());
    }

}