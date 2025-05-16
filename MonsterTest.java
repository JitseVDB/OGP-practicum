import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A JUnit (5) test class for testing the non-private methods of the Monster class.
 *
 * @author Ernest De Gres
 * @version 1.1
 */
public class MonsterTest {

    // MONSTER
    private static Monster monster_A;

    // OPPONENT
    private static Hero hero_A;

    // EQUIPMENT
    private static Weapon weapon_A;
    private static Weapon weapon_B;
    private static Armor armor_A;
    private static Armor armor_B;
    private static Backpack backpack_A;
    private static Backpack backpack_B;
    private static Purse purse_A;
    private static Purse purse_B;

    // LISTS
    private List<Equipment> items;

    @BeforeEach
    public void setUpMonster() {
        hero_A = new Hero("Ben", 100, 4);

        weapon_A = new Weapon(40, 49);
        weapon_B = new Weapon(40, 49);
        armor_A = new Armor(30, 80, ArmorType.BRONZE);
        armor_B = new Armor(30, 80, ArmorType.BRONZE);
        backpack_A = new Backpack(30, 60, 100);
        backpack_B = new Backpack(30, 60, 100);
        purse_A = new Purse(10, 50);
        purse_B = new Purse(10, 50);

        items = new ArrayList<>();
        items.add(weapon_A);
        items.add(armor_A);
        items.add(backpack_A);
        items.add(purse_A);

        monster_A = new Monster("Tom", 70, 49, items);
        // 1. Adds two anchorpoints because we have to have 2 free anchorpoints for the tests
        monster_A.addAnchorPoint(new AnchorPoint(null));
        monster_A.addAnchorPoint(new AnchorPoint(null));
    }

    // CONSTRUCTORS

    @Test
    void testConstructor_ValidArguments_ShouldInitializeFields() {
        // 1. effect of initializing name
        assertEquals("Tom", monster_A.getName());

        // 2. effect of initializing hitpoints
        assertEquals(67, monster_A.getHitPoints()); // closest lower prime number

        // 3. effect of initializing maxHitPoints
        assertEquals(70, monster_A.getMaxHitPoints());

        // 4. effect of initializing protection
        assertEquals(10, monster_A.getProtection());
    }

    @Test
    void testConstructor_InvalidDamage_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Monster("InvalidDamage", 70, -1 , items);
        });
    }



    // NAME

    @Test
    void testCanHaveAsName_allCases() {
        // 1. Valid cases
        assertTrue(monster_A.canHaveAsName("Tom"));
        assertTrue(monster_A.canHaveAsName("O'Tom"));

        // 2. Invalid cases
        assertFalse(monster_A.canHaveAsName(null));
        assertFalse(monster_A.canHaveAsName("tom"));
        assertFalse(monster_A.canHaveAsName("tom123"));
    }

    // ANCHORS

    @Test
    void testInitializeAnchorPoints_ShouldCreateAnchorPoints() {
        assertTrue(monster_A.getNbAnchorPoints() >= 0);
    }

    @Test
    void testDistributeInitialItems_ShouldAssignToAnchorPoints() {
        // 2. Items distributed in initialization
        assertEquals(weapon_A, monster_A.getAnchorPointAt(1).getItem());
        assertEquals(armor_A, monster_A.getAnchorPointAt(2).getItem());
    }

    @Test
    void testCanHaveAsItem_NoEmptyAnchorPoints_ShouldReturnFalse() {
        // fills all anchorpoints
        while (monster_A.hasFreeAnchorPoint()) {
            Weapon newWeapon = new Weapon(40, 49);
            newWeapon.setOwner(monster_A);
        }
        assertFalse(monster_A.canHaveAsItem(weapon_B));
    }

    // Capacity

    @Test
    void testIsValidCapacity_AllCases() {
        // 1. Valid cases
        assertTrue(monster_A.isValidCapacity(10));
        assertTrue(monster_A.isValidCapacity(0));

        // 2. Invalid case
        assertFalse(monster_A.isValidCapacity(-1));
    }

    // DAMAGE

    @Test
    void testGetMaximumDamage_ShouldReturnConstant() {
        assertEquals(100, Monster.getMaximumDamage());
    }

    @Test
    void testSetAndGetDamage_ShouldReturnValue() {
        monster_A.setDamage(49);
        assertEquals(49, monster_A.getDamage());
    }

    @Test
    void testIsValidDamage_allCases() {
        // 1. Valid case
        assertTrue(monster_A.isValidDamage(7));

        // 2. Invalid cases
        assertFalse(monster_A.isValidDamage(-1));
        assertFalse(monster_A.isValidDamage(0));
        assertFalse(monster_A.isValidDamage(101));
        assertFalse(monster_A.isValidDamage(10)); // can´t divide by 7
    }

    @Test
    void testReceiveDamage_ShouldReduceHitPoints() {
        int current = monster_A.getHitPoints();
        monster_A.receiveDamage(15);
        assertTrue(monster_A.getHitPoints() < current);
    }

    @Test
    void testReceiveDamage_NegativeDamage_ShouldDoNothing() {
        int current = monster_A.getHitPoints();
        monster_A.receiveDamage(-10);
        assertEquals(current, monster_A.getHitPoints());
    }

    // HIT

    @Test
    void testHit_FatalBlow_ShouldHaveZeroHitPoints() {
        // 1. fatal hit
        hero_A.setRealProtection(0); // decrease protection so hit is succesfull
        monster_A.setDamage(10000); // fatal
        monster_A.hit(hero_A);

        // 2. opponent has zero hitpoints
        assertEquals(0, hero_A.getHitPoints());
    }

    @Test
    void testHit_MissHit_ShouldNotChangeHitPoints() {
        hero_A.setHitPoints(80);
        monster_A.setDamage(20);

        // increase protection so hit fails
        hero_A.setRealProtection(999);
        monster_A.hit(hero_A);

        assertEquals(80, hero_A.getHitPoints());
    }

    @Test
    void testHit_NonFatal_ShouldReduceHitPoints() {
        hero_A.setHitPoints(80);
        monster_A.setDamage(10);
        hero_A.setRealProtection(0);

        monster_A.hit(hero_A);
        assertTrue(hero_A.getHitPoints() < 80);
    }


    // Loot

    @Test
    void testLoot_LootedItemsRemovedFromOpponent() {
        weapon_B.setOwner(hero_A);
        armor_B.setOwner(hero_A);

        // Had 2 free anchorpoints added
        monster_A.loot(hero_A);

        // Postcondition: all looted items are removed from the opponent
        assertFalse(hero_A.hasAsItem(weapon_B));
        assertFalse(hero_A.hasAsItem(armor_B));
    }

    @Test
    void testLoot_OwnerSetToMonster() {
        weapon_B.setOwner(hero_A);
        armor_B.setOwner(hero_A);

        // Had 2 free anchorpoints added
        monster_A.loot(hero_A);

        assertTrue(monster_A.hasAsItem(weapon_B) && weapon_B.getOwner() == monster_A);
        assertTrue(monster_A.hasAsItem(armor_B) && armor_B.getOwner() == monster_A);
    }

    @Test
    void testLoot_DestroyNonLootedWeaponsAndArmors() {
        // fills all anchorpoints
        while (monster_A.hasFreeAnchorPoint()) {
            Weapon newWeapon = new Weapon(40, 49);
            newWeapon.setOwner(monster_A);
        }

        weapon_B.setOwner(hero_A);
        armor_B.setOwner(hero_A);

        monster_A.loot(hero_A);

        assertTrue(weapon_B.isDestroyed());
        assertTrue(armor_B.isDestroyed());
    }

    @Test
    void testLoot_DoesNotDestroyNonLootedBackpacksAndPurses() {
        // fills all anchorpoints
        while (monster_A.hasFreeAnchorPoint()) {
            Weapon newWeapon = new Weapon(40, 49);
            newWeapon.setOwner(monster_A);
        }

        backpack_B.setOwner(hero_A);
        purse_B.setOwner(hero_A);

        monster_A.loot(hero_A);

        assertFalse(backpack_B.isDestroyed());
        assertFalse(purse_B.isDestroyed());
    }

    @Test
    void testLoot_ShinyItemsLootedFirst() {
        // Set armor not shiny
        armor_B.setShiny(false);

        weapon_B.setOwner(hero_A);
        armor_B.setOwner(hero_A);

        // fills all anchorpoints
        while (monster_A.hasFreeAnchorPoint()) {
            Weapon newWeapon = new Weapon(40, 49);
            newWeapon.setOwner(monster_A);
        }

        // adds one free anchorpoints
        monster_A.addAnchorPoint(new AnchorPoint(null));

        monster_A.loot(hero_A);

        // shiny weapon looted, armor not because no space.
        assertTrue(monster_A.hasAsItem(weapon_B));
        assertFalse(monster_A.hasAsItem(armor_B));
    }

}
