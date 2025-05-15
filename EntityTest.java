import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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



    @BeforeEach
    public void setUpEntity() {
        hero_A = new Hero("Ben", 100, 5.5);
        // This initializes 5 anchorpoints as well and sets the hitpoints to closest prime number

        armor_A = new Armor(30, 80, ArmorType.BRONZE);

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

        // 4. Postcondition on protection
        assertEquals(10, hero_A.getProtection());
    }

    @Test
    void testConstructor_InvalidMaxHitPoints_ShouldThrowException() {
        // 3. Exception thrown if maxHitPoints is invalid
        assertThrows(IllegalArgumentException.class, () -> new Hero("Ben", -5, 5.5));
    }

    @Test
    void testConstructor_InvalidProtection_ShouldThrowException() {
        // 4. Exception thrown if protection factor is invalid
        assertThrows(IllegalArgumentException.class, () -> new Hero("Ben", 100, 0));
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
    void testIsAlive_allCases() {
        // 1. Entity is alive
        assertTrue(hero_A.isAlive());

        // 2. Entity is not alive
        hero_A.removeHitPoints(hero_A.getHitPoints());
        assertFalse(hero_A.isAlive());
    }

    // PROTECTION

    @Test
    void testIsValidProtection_allCases() {
        // 1. Valid cases
        assertTrue(Entity.isValidProtection(0));
        assertTrue(Entity.isValidProtection(5));

        // 2. Invalid case
        assertFalse(Entity.isValidProtection(-1));
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

}
