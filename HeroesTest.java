import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


/**
 * A JUnit (5) test class for testing the non-private methods of the Hero class.
 *
 * @author Ernest De Gres
 * @version 1.1
 */

public class HeroesTest {


    // HERO
    private static Hero hero_A, hero_B, hero_C;

    // OPPONENT
    private static Monster monster_A;

    // EQUIPMENT
    private static Weapon weapon_A;
    private static Weapon weapon_B;
    private static Armor armor_A;
    private static Armor armor_B;
    private static Equipment armor_C;
    private static Backpack backpack_A;
    private static Backpack backpack_B;
    private static Purse purse_A;
    private static Purse purse_B;

    // LISTS
    private List<Equipment> items;

    @BeforeEach
    public void setUpMonster() {
        weapon_A = new Weapon(40, 98);
        weapon_B = new Weapon(40, 98);
        armor_A = new Armor(30, 80, ArmorType.BRONZE);
        armor_B = new Armor(30, 80, ArmorType.BRONZE);
        armor_C = new Armor(30, 80, ArmorType.BRONZE);
        backpack_A = new Backpack(30, 60, 100);
        backpack_B = new Backpack(30, 60, 100);
        purse_A = new Purse(10, 50);
        purse_B = new Purse(10, 50);


        hero_A = new Hero("Ben", 97, 20.0);
        hero_B = new Hero("Bert", 97, 20.0, weapon_B, armor_B, null, purse_B, backpack_B);
        hero_C = new Hero("Bart", 97, 5.0);


        items = new ArrayList<>();
        items.add(weapon_A);
        items.add(armor_A);
        items.add(null);
        items.add(backpack_A);
        items.add(purse_A);

        monster_A = new Monster("Tom", 70, 49, items, SkinType.SCALY);
    }
    /********************************************************************
     *                      CONSTRUCTOR TEST
     ********************************************************************/

    @Test
    public void testFirstConstructor_ValidArguments_ShouldInitializeFields() {
        assertEquals("Ben", hero_A.getName());
        assertEquals(97, hero_A.getMaxHitPoints());
        assertEquals(97, hero_A.getHitPoints()); // geen prime correctie nodig
        assertEquals(20.0, hero_A.getIntrinsicStrength(), 0.001);
        assertEquals(10, hero_A.getProtection());
        assertEquals(400, hero_A.getCapacity());
        assertFalse(hero_A.isFighting());
        assertNull(hero_A.getLeftHandWeapon());
        assertNull(hero_A.getRightHandWeapon());
        assertNull(hero_A.getArmor());
    }

    @Test
    public void testFirstConstructor_InvalidStrength_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Hero("Ben", 11, 0.0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Hero("Ben", 11, -5.0);
        });

    }

    @Test
    public void testSecondConstructor_ValidArguments_ShouldInitializeFields() {
        // Check basisvelden
        assertEquals("Bert", hero_B.getName());
        assertEquals(97, hero_B.getMaxHitPoints());
        assertEquals(97, hero_B.getHitPoints()); // geen prime correctie nodig
        assertEquals(20.0, hero_B.getIntrinsicStrength(), 0.001);
        assertEquals(10, hero_B.getProtection());
        assertEquals(400, hero_B.getCapacity());
        assertFalse(hero_B.isFighting());

        // Controleer of items effectief op ankers hangen
        assertTrue(hero_B.getAnchors().containsValue(weapon_B));
        assertTrue(hero_B.getAnchors().containsValue(armor_B));
        assertTrue(hero_B.getAnchors().containsValue(purse_B));
        assertTrue(hero_B.getAnchors().containsValue(backpack_B));

        // Controleer of items de juiste eigenaar hebben
        assertEquals(hero_B, weapon_B.getOwner());
        assertEquals(hero_B, armor_B.getOwner());
        assertEquals(hero_B, purse_B.getOwner());
        assertEquals(hero_B, backpack_B.getOwner());
    }


    /********************************************************************
     *                          NAME TEST
     ********************************************************************/


    @Test
    void testCanHaveAsName_AllCases() {
        // valid cases
        assertTrue(hero_A.canHaveAsName("Hendrik"));
        assertTrue(hero_A.canHaveAsName("Ben Bert Tom"));
        assertTrue(hero_A.canHaveAsName("Ben: Bert"));
        assertTrue(hero_A.canHaveAsName("Ben'Bert'Tom"));

        // invalid cases
        assertFalse(hero_A.canHaveAsName(null));
        assertFalse(hero_A.canHaveAsName(""));
        assertFalse(hero_A.canHaveAsName("ben"));
        assertFalse(hero_A.canHaveAsName("O'Ben D'Tom Mc'Bert"));
        assertFalse(hero_A.canHaveAsName("Ben:Bert"));
        assertFalse(hero_A.canHaveAsName("Jean-Luc#1"));
        assertFalse(hero_A.canHaveAsName("Hero 1"));
        assertFalse(hero_A.canHaveAsName("Captain:"));
    }


    /********************************************************************
     *                       PROTECTION TEST
     ********************************************************************/

    @Test
    void testSetAndGetProtection_ShouldReturnValue() {
        hero_A.setProtection(50);
        assertEquals(50, hero_A.getProtection());
    }

    @Test
    void testIsValidProtection_AllCases() {
        // valid case
        assertTrue(Hero.isValidProtection(5));

        // invalid case
        assertFalse(Hero.isValidProtection(-1));
    }

    @Test
    void testGetCurrentProtection_WithArmor_ShouldAddArmorProtection() {
        hero_A.equipArmor(armor_A); // protection of 90
        assertEquals(100, hero_A.getCurrentProtection());
    }

    @Test
    void testGetCurrentProtection_NoArmor_ShouldNotAddArmorProtection() {
        // Geen armor ingesteld
        assertEquals(10, hero_A.getCurrentProtection());
    }

    /********************************************************************
     *                          STRENGTH TEST
     ********************************************************************/

    @Test
    void testMultiplyStrength_ShouldMultiplyStrength() {
        hero_A.multiplyStrength(3);
        assertEquals(60, hero_A.getIntrinsicStrength());

        hero_A.multiplyStrength(-2);
        assertEquals(-120, hero_A.getIntrinsicStrength());
    }

    @Test
    void testMultiplyStrength_WithZero_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            hero_A.multiplyStrength(0);
        });
    }

    @Test
    void testDivideStrength_ShouldDivideStrength() {
        hero_A.divideStrength(4);
        assertEquals(5, hero_A.getIntrinsicStrength());

        hero_A.divideStrength(-2);
        assertEquals(-2.5, hero_A.getIntrinsicStrength());
    }

    @Test
    void testDivideStrength_WithZero_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            hero_A.divideStrength(0);
        });
    }

    @Test
    void testeGetAttackPower_WithoutWeapons_ShouldNotAddDamage() {
        // geen wapen
        assertEquals(20, hero_A.getAttackPower());
    }

    @Test
    void testAttackPower_WithWeapons_ShouldAddDamage() {
        hero_A.equipLeftHand(weapon_A);
        hero_A.equipRightHand(weapon_B);
        // 20 + 98 + 98
        assertEquals(216, hero_A.getAttackPower());
    }

    @Test
    void testGetIntrinsicStrength_ReturnValue() {
        assertEquals(20.0, hero_A.getIntrinsicStrength());
    }


    /********************************************************************
     *                       ARMOR TEST
     ********************************************************************/

    @Test
    void testGetArmor_NoArmorEquipped_ShouldReturnNull() {
        assertNull(hero_A.getArmor());
    }

    @Test
    void testGetArmor_ArmorEquipped_ShouldReturnEquippedArmor() {
        hero_A.equipArmor(armor_A);
        assertEquals(armor_A, hero_A.getArmor());
    }


    @Test
    void testGetNbArmorsCarried_ShouldReturnAmountOfArmors() {
        armor_A.setOwner(hero_A);
        armor_B.setOwner(hero_A);
        assertEquals(2, hero_A.getNbArmorsCarried());
    }

    /********************************************************************
     *                       HIT TEST
     ********************************************************************/

    @Test
    void testHit_SuccessfulNonFatal_ShouldReduceHitPointsMonster() {
        monster_A.setCurrentProtection(0); // zodat hit slaagt

        hero_A.hit(monster_A); // hero does 5 damage

        assertEquals(monster_A.getClosestLowerPrime(70-5), monster_A.getHitPoints());
    }

    @Test
    void testHit_NoDamageDealt() {
        hero_C.hit(monster_A);
        assertEquals(monster_A.getClosestLowerPrime(70), monster_A.getHitPoints());
    }


    @Test
    void testHit_SuccessfulFatal_ShouldTriggerHeal() {
        hero_A.removeHitPoints(50); // zodat healing zichtbaar is

        hero_A.equipLeftHand(weapon_A);
        hero_A.equipRightHand(weapon_B);

        monster_A.setCurrentProtection(0); // zodat hit slaagt

        hero_A.hit(monster_A); // hero does 103 damage, so hit is fatal


        assertEquals(0, monster_A.getHitPoints()); // monster is dood
    }

    @Test
    void testHit_SuccessfulFatal_ShouldCollectTreasure() {
        hero_A.equipLeftHand(weapon_A);
        hero_A.equipRightHand(weapon_B);

        monster_A.setCurrentProtection(0); // zodat hit slaagt

        int hpBefore = hero_A.getHitPoints();

        hero_A.hit(monster_A); // hero does 103 damage, so hit is fatal

        assertTrue(hero_A.hasAsItem(weapon_A));
        assertTrue(hero_A.hasAsItem(armor_A));
        assertTrue(hero_A.hasAsItem(backpack_A));
        assertTrue(hero_A.hasAsItem(purse_A));

        assertEquals(0, monster_A.getHitPoints()); // monster is dood
    }

    @Test
    void testHit_NullMonster_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            hero_A.hit(null);
        });
    }

    /********************************************************************
     *                        WEAPON EQUIPMENT TEST
     ********************************************************************/

    @Test
    void testGetLeftAndRightHand_NoWeaponEquipped_ShouldReturnNull() {
        assertNull(hero_A.getLeftHandWeapon());
        assertNull(hero_A.getRightHandWeapon());
    }

    @Test
    void testGetAndEquipLeftHand_WeaponEquipped_ShouldReturnEquippedLeftHandWeapon() {
        hero_A.equipLeftHand(weapon_A);
        assertEquals(weapon_A, hero_A.getLeftHandWeapon());
    }

    @Test
    void testGetAndEquipRightHand_WeaponEquipped_ShouldReturnEquippedRightHandWeapon() {
        hero_A.equipRightHand(weapon_A);
        assertEquals(weapon_A, hero_A.getRightHandWeapon());
    }

    /********************************************************************
     *                        ANCHOR TEST
     ********************************************************************/

    @Test
    void testInitializeAnchorPoints_ShouldAddAnchorPoints() {
        Map<String, Equipment> anchors = hero_A.getAnchors();

        assertTrue(anchors.containsKey("leftHand"));
        assertTrue(anchors.containsKey("rightHand"));
        assertTrue(anchors.containsKey("back"));
        assertTrue(anchors.containsKey("body"));
        assertTrue(anchors.containsKey("belt"));

    }

    @Test
    void testAddToAnchorPoint_ShouldAddItemToAnchorPoint() {
        hero_A.addToAnchorPoint(armor_A);
        assertTrue(hero_A.hasAsItem(armor_A));
    }

    @Test
    void testAddToAnchorPoint_AddWeaponToLeftAndRightHand_ShouldEquipWeapon() {
        // adds weapons to left and right hand
        hero_A.addToAnchorPoint(weapon_A);
        hero_A.addToAnchorPoint(weapon_B);

        assertEquals(weapon_A, hero_A.getLeftHandWeapon());
        assertEquals(weapon_B, hero_A.getRightHandWeapon());
    }


    /********************************************************************
     *                        ITEM TEST
     ********************************************************************/



    @Test
    void testCanHaveAsItemAt_ArmorOnBody_ShouldReturnTrue() {
        AnchorPoint anchorpoint = hero_A.getAnchorPoint("body");
        assertTrue(hero_A.canHaveAsItemAt(armor_A, anchorpoint));
        assertFalse(hero_A.canHaveAsItemAt(weapon_A, anchorpoint));
        assertFalse(hero_A.canHaveAsItemAt(backpack_A, anchorpoint));
        assertFalse(hero_A.canHaveAsItemAt(purse_A, anchorpoint));
    }


    @Test
    void testCanHaveAsItemAt_PurseOnBelt_ShouldReturnTrue() {
        AnchorPoint anchorpoint = hero_A.getAnchorPoint("belt");
        assertTrue(hero_A.canHaveAsItemAt(purse_A, anchorpoint));
        assertFalse(hero_A.canHaveAsItemAt(weapon_B, anchorpoint));
        assertFalse(hero_A.canHaveAsItemAt(backpack_A, anchorpoint));
        assertFalse(hero_A.canHaveAsItemAt(armor_A, anchorpoint));
    }

    @Test
    void testCanHaveAsItemAt_ItemNotPurseOnAnchorPointNotBodyOrBelt_ShouldReturnTrue() {
        AnchorPoint anchorpoint1 = hero_A.getAnchorPoint("lefthand");
        assertTrue(hero_A.canHaveAsItemAt(weapon_A, anchorpoint1));
        assertTrue(hero_A.canHaveAsItemAt(armor_A, anchorpoint1));
        assertTrue(hero_A.canHaveAsItemAt(backpack_A, anchorpoint1));
        assertFalse(hero_A.canHaveAsItemAt(purse_B, anchorpoint1));

        AnchorPoint anchorpoint2 = hero_A.getAnchorPoint("righthand");
        assertTrue(hero_A.canHaveAsItemAt(weapon_A, anchorpoint2));
        assertTrue(hero_A.canHaveAsItemAt(armor_A, anchorpoint2));
        assertTrue(hero_A.canHaveAsItemAt(backpack_A, anchorpoint2));
        assertFalse(hero_A.canHaveAsItemAt(purse_B, anchorpoint2));

        AnchorPoint anchorpoint3 = hero_A.getAnchorPoint("back");
        assertTrue(hero_A.canHaveAsItemAt(weapon_A, anchorpoint3));
        assertTrue(hero_A.canHaveAsItemAt(armor_A, anchorpoint3));
        assertTrue(hero_A.canHaveAsItemAt(backpack_A, anchorpoint3));
        assertFalse(hero_A.canHaveAsItemAt(purse_B, anchorpoint3));
    }


    @Test
    void testCanHaveAsItemAt_NullParameters_ShouldReturnFalse() {
        assertFalse(hero_A.canHaveAsItemAt(null, hero_A.getAnchorPoint("back")));
        assertFalse(hero_A.canHaveAsItemAt(weapon_A, null));
    }

}
