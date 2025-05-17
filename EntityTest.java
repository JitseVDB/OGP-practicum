import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * A JUnit (5) test class for testing the non-private methods of the Entity Class.
 *
 * @author Ernest De Gres
 * @version 1.0
 */

public class EntityTest {

    // HERO
    private static Hero hero_A;

    // EQUIPMENT
    private static Armor armor_A;
    private static Equipment armor_B;
    private static Weapon weapon_A;
    private static Weapon weapon_B;
    private static Purse purse_A;
    private Backpack backpack_A;


    @BeforeEach
    public void setUpEntity() {
        hero_A = new Hero("Ben", 100, 100);

        // This initializes 5 anchorpoints as well and sets the hitpoints to closest prime number

        armor_A = new Armor(30, 80, ArmorType.BRONZE);
        armor_B = new Armor(30, 30, ArmorType.BRONZE);
        weapon_A = new Weapon(30, 35);
        weapon_B = new Weapon(30, 35);
        purse_A = new Purse(30, 30);
        backpack_A = new Backpack(30, 30, 200);

    }

    // CONSTRUCTORS

    @Test
    void testConstructor_ValidArguments_ShouldInitializeFields() {
        // 1. Postcondition on name
        assertEquals("Ben", hero_A.getName());

        // 2. Postcondition on hitpoints
        assertEquals(97, hero_A.getHitPoints()); // closest prime number

        // 3. Postcondition on maxHitPoints
        assertEquals(100, hero_A.getMaxHitPoints());

        // 5. Postcondition on hero is not fighting
        assertFalse(hero_A.isFighting());
    }

    @Test
    void testConstructor_InvalidName_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Hero("ben", 100, 5.5);
        });
    }


    // HITPOINTS

    @Test
    void testIsValidMaxHitPoints_allCases() {
        // 1. Valid case
        assertTrue(Entity.isValidMaxHitPoints(1));

        // 2. Invalid cases
        assertFalse(Entity.isValidMaxHitPoints(0));
        assertFalse(Entity.isValidMaxHitPoints(-10));
    }

    @Test
    void testIsValidHitPoints_allCases() {
        // 1. Valid cases
        assertTrue(hero_A.isValidHitPoints(0));
        assertTrue(hero_A.isValidHitPoints(97));

        // 2. Invalid cases
        assertFalse(hero_A.isValidHitPoints(-1));
        assertFalse(hero_A.isValidHitPoints(101)); // more than maxHitPoints
    }

    @Test
    void testSetMaxHitPoints_ShouldSetMaxHitPoints() {
       hero_A.setMaxHitPoints(200);
       assertEquals(200, hero_A.getMaxHitPoints());
    }


    @Test
    void testSetHitPoints_ShouldSetHitPoints() {
        hero_A.setHitPoints(79);
        assertEquals(79, hero_A.getHitPoints());
    }

    @Test
    void testIsAlive_allCases() {
        // 1. Entity is alive
        assertTrue(hero_A.isAlive());

        // 2. Entity is not alive
        hero_A.removeHitPoints(hero_A.getHitPoints());
        assertFalse(hero_A.isAlive());
    }

    // isFighting

    @Test
    void testSetFighting_ShouldSetFighting() {
        hero_A.setFighting(true);
        assertTrue(hero_A.isFighting());
    }

    @Test
    void testSetFighting_ShouldChangeHitPoints() {
        hero_A.setFighting(true);
        hero_A.setHitPoints(80);
        hero_A.setFighting(false);
        assertEquals(79, hero_A.getHitPoints());
    }

    @Test
    void testIsPrime_allCases() {
        // true case
        assertTrue(hero_A.isPrime(79));

        // false cases
        assertFalse(hero_A.isPrime(80));
        assertFalse(hero_A.isPrime(1));
    }

    @Test
    void testGetClosestLowerPrime_ShouldReturnLowerPrime() {
        assertEquals(79, hero_A.getClosestLowerPrime(80));
    }

    @Test
    void testGetClosestLowerPrime_StartIsLowerThenTwo_ShouldReturnTwo() {
        assertEquals(2, hero_A.getClosestLowerPrime(1));
        assertEquals(2, hero_A.getClosestLowerPrime(0));
        assertEquals(2, hero_A.getClosestLowerPrime(-1));
    }

    @Test
    void testAddHitPoints_ShouldAddHitPoints() {
        hero_A.setHitPoints(0);
        hero_A.addHitPoints(75);
        assertEquals(73, hero_A.getHitPoints());

    }

    @Test
    void testAddHitPoints_ExceedsMaximumHitPoints_ShouldReturnMaximumHitPoints() {
        hero_A.setHitPoints(0);
        hero_A.addHitPoints(150);
        assertEquals(100, hero_A.getHitPoints());
    }

    @Test
    void testRemoveHitPoints_ShouldRemoveHitPoints() {
        // hitpoints equal to maximumHitPoints after initialization, so 100
        hero_A.removeHitPoints(50);
        assertEquals(47, hero_A.getHitPoints());
    }

    @Test
    void testAddHitPoints_UnderZeroHitPoints_ShouldReturnZeroHitPoints() {
        // hitpoints equal to maximumHitPoints after initialization, so 100
        hero_A.removeHitPoints(150);
        assertEquals(0, hero_A.getHitPoints());
    }

    // Capacity

    @Test
    void testGetCapacity_ShouldReturnCapacity() {
        // capacity = intrinsicStrength*2000
        assertEquals(2000, hero_A.getCapacity());
    }

    @Test
    void testGetTotalWeight_NoBackpack_ShouldReturnTotalWeight() {
        // add items to hero_A
        armor_A.setOwner(hero_A);
        purse_A.setOwner(hero_A); // empty purse
        weapon_A.setOwner(hero_A);

        assertEquals(90, hero_A.getTotalWeight());
    }

    @Test
    void testGetTotalWeight_WithBackpack_ShouldReturnTotalWeight() {
        backpack_A.setOwner(hero_A);
        armor_A.setBackpack(backpack_A);
        purse_A.setBackpack(backpack_A); // empty purse
        weapon_A.setBackpack(backpack_A);

        assertEquals(120, hero_A.getTotalWeight());
    }

    // ANCHOR POINTS

    @Test
    void testAddAnchorPoint_ShouldIncreaseSize() {
        // 1. Effect of new anchor being added
        AnchorPoint anchor_6 = new AnchorPoint("extra");
        hero_A.addAnchorPoint(anchor_6);

        assertEquals(6, hero_A.getNbAnchorPoints());
        assertSame(anchor_6, hero_A.getAnchorPointAt(6));
    }

    // EQUIPMENT

    @Test
    void testHasAsItem_ItemNotAdded_ShouldReturnFalse() {

        assertFalse(hero_A.hasAsItem(armor_A)); // never added
    }

    @Test
    void testHasAsItem_ItemAdded_ShouldReturnTrue() {
        armor_A.setOwner(hero_A); // adds item to hero_A
        assertTrue(hero_A.hasAsItem(armor_A));
    }

    @Test
    void testGetNbAnchorPoints_DefaultCount() {
        // Anchorpoints intialize when hero is made, always 5
        assertEquals(5, hero_A.getNbAnchorPoints());
    }

    @Test
    void testGetAnchorPointAt_ValidIndex_ShouldReturnCorrectAnchor() {
        assertEquals("leftHand", hero_A.getAnchorPointAt(1).getName());
        assertEquals("back", hero_A.getAnchorPointAt(3).getName());
    }

    @Test
    void testGetAnchorPointAt_InvalidIndex_ShouldThrowException() {
        // 1. Index out of bounds
        assertThrows(IndexOutOfBoundsException.class, () -> hero_A.getAnchorPointAt(0));
        assertThrows(IndexOutOfBoundsException.class, () -> hero_A.getAnchorPointAt(10));
    }

    @Test
    void testGetAllItems_ShouldReturnAllItems() {
        armor_A.setOwner(hero_A);
        purse_A.setOwner(hero_A);
        weapon_A.setOwner(hero_A);

        List<Equipment> items = hero_A.getAllItems();

        assertEquals(items, hero_A.getAllItems());
    }

    @Test
    void testAddToAnchorPoint_ShouldAddItemToAnchorPoint() {
        hero_A.addToAnchorPoint(armor_A);
        assertTrue(hero_A.hasAsItem(armor_A));
    }

    @Test
    void testHasFreeAnchorPoint_FreeAnchorPoint_ShouldReturnTrue() {
        // 1. No items added
        assertTrue(hero_A.hasFreeAnchorPoint());
    }

    @Test
    void testHasFreeAnchorPoint_NoFreeAnchorPoint_ShouldReturnFalse() {
        // 1. Add item to every anchorpoint
        weapon_A.setOwner(hero_A);
        weapon_B.setOwner(hero_A);
        armor_A.setOwner(hero_A);
        purse_A.setOwner(hero_A);
        armor_B.setOwner(hero_A);


        assertFalse(hero_A.hasFreeAnchorPoint());
    }

}
