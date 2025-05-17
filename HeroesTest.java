import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HeroesTest {

    /********************************************************************
     *                      CONSTRUCTOR TEST (1)
     ********************************************************************/

    @Test
    public void testConstructorSetsFieldsCorrectly() {
        Hero hero = new Hero("James O'Hara", 13, 10.0);

        assertEquals("James O'Hara", hero.getName());
        assertEquals(13, hero.getMaxHitPoints());
        assertEquals(13, hero.getHitPoints()); // geen prime correctie nodig
        assertEquals(10.0, hero.getIntrinsicStrength(), 0.001);
        assertFalse(hero.isFighting());
        assertNull(hero.getLeftHandWeapon());
        assertNull(hero.getRightHandWeapon());
        assertNull(hero.getArmor());
    }


    @Test
    public void testConstructorCorrectsNonPrimeHitPoints() {
        Hero hero = new Hero("Prime Check", 10, 5.0); // 10 is géén priem, dus correctie
        assertTrue(hero.getHitPoints() <= 10); // We weten dat hij omlaag gaat
        assertEquals(7, hero.getHitPoints()); // Verwachte laagste prime onder 10
    }


    @Test
    public void testInvalidNameTooManyApostrophes() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Hero("Jam'es o'Ha'ra", 15, 10.0);
        });
    }

    @Test
    public void testInvalidNameNoCapitalLetter() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Hero("james O'Hara", 15, 10.0);
        });
    }

    @Test
    public void testInvalidNameColonWithoutSpace() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Hero("James:O'Hara", 15, 10.0);
        });
    }

    @Test
    public void testNegativeHitPointsThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Hero("Valid Name", -1, 5.0);
        });
    }

    @Test
    public void testZeroStrengthThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Hero("Valid Name", 11, 0.0);
        });
    }

    @Test
    public void testNegativeStrengthThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Hero("Valid Name", 11, -5.0);
        });
    }

    /********************************************************************
     *                      CONSTRUCTOR TESTS (2)
     ********************************************************************/

    @Test
    public void testConstructorAssignsItemsToAnchors() {

        // Gebruik unieke ID's zodat geen ander testobject in de weg zit
        Weapon sword = new Weapon(50, 14);           // uniek
        Weapon dagger = new Weapon(23, 7);           // uniek
        Armor chestplate = new Armor(30, 10, ArmorType.BRONZE); // uniek en geldig

        Hero hero = new Hero("Sir Testalot", 13, 5.5, sword, dagger, chestplate);

        // Check basisvelden
        assertEquals("Sir Testalot", hero.getName());
        assertEquals(13, hero.getHitPoints());
        assertEquals(5.5, hero.getIntrinsicStrength(), 0.01);

        // Controleer of items effectief op ankers hangen
        assertTrue(hero.getAnchors().values().contains(sword));
        assertTrue(hero.getAnchors().values().contains(dagger));
        assertTrue(hero.getAnchors().values().contains(chestplate));

        // Check of armor correct herkend is
        assertEquals(chestplate, hero.getArmor());

        // Controleer of items de juiste eigenaar hebben
        assertEquals(hero, sword.getOwner());
        assertEquals(hero, dagger.getOwner());
        assertEquals(hero, chestplate.getOwner());
    }



    @Test
    public void testConstructorRejectsOverCapacity() {
        // Held met te zwakke kracht voor veel gewicht
        Weapon heavyWeapon = new Weapon(999, 7); // zwaar
        assertThrows(AssertionError.class, () -> {
            new Hero("Weakling", 11, 1.0, heavyWeapon);
        });
    }

    @Test
    public void testConstructorHandlesAnchorConflicts() {
        Weapon sword = new Weapon(101, 14);   // geldig
        Weapon dagger = new Weapon(102, 21);  // geldig

        Hero hero = new Hero("DualWield", 17, 10.0, sword, dagger);

        int weaponsAssigned = 0;
        for (var item : hero.getAnchors().values()) {
            if (item instanceof Weapon) {
                weaponsAssigned++;
            }
        }

        assertEquals(2, weaponsAssigned); // beide wapens moeten toegewezen zijn
    }


    @Test
    public void testConstructorWithNullItems() {
        Weapon sword = new Weapon(33, 14); // geldig

        Hero hero = new Hero("NullFriendly", 17, 8.0, null, sword);

        // sword moet zijn toegekend
        assertTrue(hero.getAnchors().values().contains(sword));

        // null mag nergens toegekend zijn
        assertFalse(hero.getAnchors().values().contains(null));
    }


    /********************************************************************
     *                          NAME TEST
     ********************************************************************/


    @Test
    void testValidSimpleName() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertTrue(hero.canHaveAsName("Hendrik"));
    }

    @Test
    void testValidNameWithSpaces() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertTrue(hero.canHaveAsName("Jean Luc Delamol"));
    }

    @Test
    void testValidNameWithColons() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertTrue(hero.canHaveAsName("Captain: Sparrow"));
    }

    @Test
    void testValidNameWithApostrophes() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertTrue(hero.canHaveAsName("D'Artagnan O'Connor"));
    }

    @Test
    void testValidNameWithTwoApostrophesMax() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertTrue(hero.canHaveAsName("O'Brien D'Omer"));
    }

    @Test
    void testNullName() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertFalse(hero.canHaveAsName(null));
    }

    @Test
    void testEmptyName() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertFalse(hero.canHaveAsName(""));
    }

    @Test
    void testStartsWithLowercase() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertFalse(hero.canHaveAsName("stella"));
    }

    @Test
    void testTooManyApostrophes() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertFalse(hero.canHaveAsName("O'Brien D'Omer Mc'Donald"));
    }

    @Test
    void testColonWithoutSpace() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertFalse(hero.canHaveAsName("Captain:Jean-Luc"));
    }

    @Test
    void testInvalidCharacters() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertFalse(hero.canHaveAsName("Jean-Luc#1"));
    }

    @Test
    void testNameWithDigit() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertFalse(hero.canHaveAsName("Hero 1"));
    }

    @Test
    void testColonAtEnd() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertFalse(hero.canHaveAsName("Captain:"));
    }

    @Test
    void testColonFollowedByNonSpace() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertFalse(hero.canHaveAsName("Captain:Jean"));
    }

    /********************************************************************
     *                       HITPOINTS TEST
     ********************************************************************/

    @Test
    void testSetHitPointsToPositiveValue() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.setHitPoints(100);
        assertEquals(100, hero.getHitPoints());
    }

    @Test
    void testSetHitPointsToZero() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.setHitPoints(0);
        assertEquals(0, hero.getHitPoints());
    }

    @Test
    void testTakeDamageReducesHitPoints() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.setHitPoints(50);
        hero.receiveDamage(20);
        assertEquals(37, hero.getHitPoints()); // 50 naar 47 (door prime) - 10 (omdat protection 10 is) = 37
    }

    @Test
    void testReceiveDamageDropsHitPointsToZero() {
        Hero hero = new Hero("Guillaume", 100, 10); // protection = 10
        hero.setHitPoints(10); // initieel 10 HP
        hero.receiveDamage(20); // 20 - 10 = 10 schade

        assertEquals(0, hero.getHitPoints());
    }

    @Test
    void testDefaultFightingStatusIsFalse() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertFalse(hero.isFighting());
    }

    @Test
    void testSetFightingTrue() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.setFighting(true);
        assertTrue(hero.isFighting());
    }

    @Test
    void testSetFightingFalse() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.setFighting(true); // eerst true
        hero.setFighting(false);
        assertFalse(hero.isFighting());
    }

    @Test
    void testToggleFightingStatus() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.setFighting(true);
        assertTrue(hero.isFighting());
        hero.setFighting(false);
        assertFalse(hero.isFighting());
    }


    @Test
    void testAddHitPointsIncreasesValue() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.setHitPoints(10);
        hero.addHitPoints(5);
        assertTrue(hero.getHitPoints() > 10);
    }

    @Test
    void testAddHitPointsRoundsToHigherPrime() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.setHitPoints(10);
        hero.addHitPoints(1); // 10 + 1 = 11 → prime
        assertEquals(11, hero.getHitPoints());
    }

    @Test
    void testRemoveHitPointsReducesValue() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.setHitPoints(17); // prime
        hero.removeHitPoints(4);
        assertTrue(hero.getHitPoints() < 17);
    }

    @Test
    void testRemoveHitPointsCapsAtZero() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.setHitPoints(3);
        hero.removeHitPoints(10); // meer dan huidige HP
        assertEquals(0, hero.getHitPoints());
    }

    @Test
    void testRemoveHitPointsRoundsToLowerPrimeIfNotFighting() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.setHitPoints(14); // -> 13
        hero.setFighting(false);
        hero.removeHitPoints(1); // 12-> 11
        assertEquals(11, hero.getHitPoints());
    }

    @Test
    void testRemoveHitPointsNoPrimeCorrectionIfFighting() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.setFighting(true);
        hero.setHitPoints(14);
        hero.removeHitPoints(1); // 14 - 1 = 13
        assertEquals(13, hero.getHitPoints()); // exact, geen afronding meer nodig
    }



    /********************************************************************
     *                          STRENGTH TEST
     ********************************************************************/

    @Test
    void testMultiplyStrengthWithPositiveFactor() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.multiplyStrength(3);
        assertEquals(30, hero.getIntrinsicStrength());
    }

    @Test
    void testMultiplyStrengthWithNegativeFactor() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.multiplyStrength(-2);
        assertEquals(-20, hero.getIntrinsicStrength());
    }

    @Test
    void testMultiplyStrengthWithZeroThrowsException() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertThrows(IllegalArgumentException.class, () -> {
            hero.multiplyStrength(0);
        });
    }

    @Test
    void testDivideStrengthWithPositiveDivisor() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.divideStrength(4);
        assertEquals(2.50, hero.getIntrinsicStrength());
    }

    @Test
    void testDivideStrengthWithNegativeDivisor() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.divideStrength(-2);
        assertEquals(-5, hero.getIntrinsicStrength());
    }

    @Test
    void testDivideStrengthWithZeroThrowsException() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertThrows(IllegalArgumentException.class, () -> {
            hero.divideStrength(0);
        });
    }

    @Test
    void testMultiplyStrengthRounding() {
        Hero hero = new Hero("Guillaume", 100, 1.236);
        hero.multiplyStrength(1); // verwacht afronding op 1.24
        assertEquals(1.24, hero.getIntrinsicStrength());
    }

    @Test
    void testDivideStrengthRounding() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.divideStrength(3); // verwacht 3.33 afgerond
        assertEquals(3.33, hero.getIntrinsicStrength());
    }



    @Test
    void testAttackPowerWithoutWeapons() {
        Hero hero = new Hero("Guillaume", 100, 10);
        // geen wapens
        assertEquals(10, hero.getAttackPower());
    }

    @Test
    void testAttackPowerWithLeftWeaponOnly() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.equipLeftHand(new Weapon(30, 7));

        assertEquals(17, hero.getAttackPower());
    }

    @Test
    void testAttackPowerWithRightWeaponOnly() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.equipLeftHand(new Weapon(30, 14));

        assertEquals(24, hero.getAttackPower());
    }

    @Test
    void testAttackPowerWithBothWeapons() {
        Hero hero = new Hero("Guillaume", 100, 10);
        hero.equipLeftHand(new Weapon(30, 7));
        hero.equipRightHand(new Weapon(30, 14));
        assertEquals(31, hero.getAttackPower()); // 5 + 3 + 6
    }

    @Test
    void testGetIntrinsicStrengthReturnsCorrectValue() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertEquals(10, hero.getIntrinsicStrength());
    }


    /********************************************************************
     *                       ARMOR TEST
     ********************************************************************/

    @Test
    void testGetArmorInitiallyNull() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertNull(hero.getArmor());
    }

    @Test
    void testGetArmorReturnsEquippedArmor() {
        Hero hero = new Hero("Guillaume", 100, 10);
        Armor breastplate = new Armor(40, 5, ArmorType.BRONZE);

        hero.equipArmor(breastplate);  // veronderstelde methode
        assertEquals(breastplate, hero.getArmor());
    }

    @Test
    void testGetNbArmorsCarriedEmpty() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertEquals(0, hero.getNbArmorsCarried());
    }

    @Test
    void testGetNbArmorsCarriedWithOneArmor() {
        Hero hero = new Hero("Guillaume", 100, 10);
        Armor armor = new Armor(30, 2, ArmorType.BRONZE);
        armor.setOwner(hero); // Zorg voor correcte eigenaarschap
        AnchorPoint bodyAnchor = hero.getAnchorPoint("body");
        bodyAnchor.setItem(armor);

        assertEquals(1, hero.getNbArmorsCarried());
    }

    @Test
    void testGetNbArmorsCarriedIgnoresNonArmor() {
        Hero hero = new Hero("Guillaume", 100, 10);
        AnchorPoint back = hero.getAnchorPoint("back");

        Weapon sword = new Weapon(45, 7);
        sword.setOwner(hero);
        back.setItem(sword);

        assertEquals(0, hero.getNbArmorsCarried());
    }

    @Test
    void testGetNbArmorsCarriedMultipleArmors() {
        Hero hero = new Hero("Guillaume", 100, 10);

        Armor chest = new Armor(6, 3, ArmorType.BRONZE);
        Armor boots = new Armor(54, 1, ArmorType.BRONZE);
        Armor helmet = new Armor(21, 2, ArmorType.BRONZE);

        hero.getAnchorPoint("body").setItem(chest);
        hero.getAnchorPoint("back").setItem(boots);
        hero.getAnchorPoint("belt").setItem(helmet);

        assertEquals(3, hero.getNbArmorsCarried());
    }

    /********************************************************************
     *                       HIT TEST
     ********************************************************************/

    @Test
    void testHitSuccessfulNonFatal() {
        Hero hero = new Hero("TestHero", 100, 15);
        Weapon sword = new Weapon(21, 7);
        hero.getAnchorPoint("leftHand").setItem(sword); // correcte manier

        Monster monster = new Monster("Grass", 50, 49, new ArrayList<Equipment>(), SkinType.SCALY);
        monster.setCurrentProtection(0); // zodat hit slaagt

        hero.hit(monster);

        assertTrue(monster.getHitPoints() < 50);
    }


    @Test
    void testHitSuccessfulFatalTriggersHeal() {
        Hero hero = new Hero("TestHero", 100, 50); // Hoge kracht voor lethale hit
        hero.removeHitPoints(50); // zodat healing zichtbaar is

        Weapon axe = new Weapon(23, 7);
        hero.equipLeftHand(axe);

        List<Equipment> items = new ArrayList<>();
        Monster monster = new Monster("Examens", 10, 49, items, SkinType.SCALY); // Zwakke monster
        monster.setCurrentProtection(0);

        int hpBefore = hero.getHitPoints();
        hero.hit(monster);

        assertTrue(hero.getHitPoints() > hpBefore); // genezen na kill
        assertEquals(0, monster.getHitPoints()); // monster is dood
    }

    @Test
    void testHitFailsIfProtectionTooHigh() {
        Hero hero = new Hero("TestHero", 100, 10);
        Monster monster = new Monster("Broer", 100, 49, new ArrayList<Equipment>(), SkinType.SCALY);
        monster.setCurrentProtection(30);

        int initialHP = monster.getHitPoints();
        hero.hit(monster);

        assertEquals(initialHP, monster.getHitPoints());
        assertFalse(hero.isFighting());
    }

    @Test
    void testHitWithNullMonsterThrows() {
        Hero hero = new Hero("Hero", 100, 10);

        assertThrows(NullPointerException.class, () -> {
            hero.hit(null);
        });
    }


    /********************************************************************
     *                       HEAL TEST
     ********************************************************************/

    @Test
    void testHealAfterKillIsApplied() {
        Hero hero = new Hero("Panda", 100, 20);
        hero.setHitPoints(70); // mist 30 HP

        // Damage moet groot genoeg zijn om zeker te doden
        Weapon sword = new Weapon(21, 21); // veel damage
        hero.getAnchorPoint("leftHand").setItem(sword);

        // Geef monster minder HP zodat het gegarandeerd doodgaat
        Monster monster = new Monster("Outside", 5, 49, new ArrayList<>(), SkinType.SCALY);
        monster.setCurrentProtection(0); // geen verdediging

        hero.hit(monster);

        assertEquals(0, monster.getHitPoints(), "Monster should be dead");

        int healed = hero.getHitPoints() - 70;
        assertTrue(healed > 0 && healed <= 30, "Hero should heal between 1 and 30 HP after a kill");
    }


    /********************************************************************
     *                       PROTECTION TEST
     ********************************************************************/

    @Test
    void testGetRealProtection_WithArmor() {
        Hero hero = new Hero("TestHero", 100, 10); // 10 is hier de protection
        Armor armor = new Armor(30, 20, ArmorType.BRONZE); // 90 bescherming
        hero.equipArmor(armor);
        assertEquals(100, hero.getRealProtection());
    }

    @Test
    void testGetRealProtection_NoArmor() {
        Hero hero = new Hero("TestHero", 100, 10); // 10 is protection
        // Geen armor ingesteld
        assertEquals(10, hero.getRealProtection());
    }

    /********************************************************************
     *                       COLLECT TREASURE TEST
     ********************************************************************/

    @Test
    void testCollectTreasureFromNullMonsterDoesNothing() {
        Hero hero = new Hero("Lootless", 50, 5.0);
        int capacityBefore = hero.getCapacity();

        // Should not throw and should not alter state
        assertDoesNotThrow(() -> hero.collectTreasureFrom(null));
        assertEquals(capacityBefore, hero.getCapacity(), "Capacity should remain unchanged");
    }

    @Test
    void testCollectSingleWeaponSuccessfully() {
        Hero hero = new Hero("Lara", 100, 5.0); // max capacity = 100

        Weapon loot = new Weapon(24, 49);

        Monster monster = new Monster("Goblin", 30, 49, new ArrayList<Equipment>(), SkinType.SCALY);

        // Zorg dat het op een compatibel anchor zit (bv. "leftHand")
        monster.getAnchors().put("leftHand", loot);

        hero.collectTreasureFrom(monster);

        assertEquals(10, hero.getCapacity(), "Hero's capacity should match item weight");
        assertEquals(hero, loot.getOwner(), "Item owner should be set to hero");
        assertTrue(hero.getAnchors().containsValue(loot), "Item should be in one of hero's anchor points");
    }



    @Test
    void testTooHeavyItemIsNotCollected() {
        Hero hero = new Hero("Tiny", 10, 1.0); // max capacity = 20

        Weapon heavyLoot = new Weapon(30, 14); // too heavy
        Monster monster = new Monster("Ogre", 50, 49, new ArrayList<Equipment>(), SkinType.SCALY);

        for (Map.Entry<String, Equipment> entry : monster.getAnchors().entrySet()) {
            if (entry.getValue() == null) {
                monster.getAnchors().put(entry.getKey(), heavyLoot);
                break;
            }
        }

        hero.collectTreasureFrom(monster);

        assertEquals(0, hero.getCapacity(), "Item too heavy, capacity should stay 0");
        assertNull(heavyLoot.getOwner(), "Owner should remain null");
    }


    @Test
    void testNoValidAnchorAvailable() {
        Hero hero = new Hero("FullBoy", 100, 5.0); // max capacity = 100

        // Vul alle anchor points met dummy items
        for (Equipment item : hero.getAnchors().values()) {
            AnchorPoint dummyAnchor = new AnchorPoint("dummy");
            dummyAnchor.setItem(item); // dit is niet juist - je hebt geen toegang tot de AnchorPoints zelf
        }


        // Monster zonder items
        Monster monster = new Monster("Gremlin", 30, 49, new ArrayList<Equipment>(), SkinType.SCALY);

        // Voeg handmatig een loot-item toe aan een monster anchor
        Weapon loot = new Weapon(5, 14);
        Map<String, Equipment> monsterAnchors = monster.getAnchors();
        // Zoek lege anchor bij monster en stop daar het item in
        for (String key : monsterAnchors.keySet()) {
            if (monsterAnchors.get(key) == null) {
                monsterAnchors.put(key, loot);
                break;
            }
        }

        hero.collectTreasureFrom(monster);

        assertEquals(0, hero.getCapacity(), "Item should not be collected due to no space");
        assertNull(loot.getOwner(), "Item should not be owned");
    }


    /********************************************************************
     *                       RECEIVE DAMAGE TEST
     ********************************************************************/

    @Test
    void testReceiveDamage_LessThanProtection_NoDamage() {
        Hero hero = new Hero("Defender", 100, 10); // Protection = 10 standaard
        hero.setHitPoints(80);

        hero.receiveDamage(5); // minder dan protection → geen schade
        hero.receiveDamage(5); // minder dan protection → geen schade

        assertEquals(79, hero.getHitPoints()); // unchanged
    }

    @Test
    void testReceiveDamage_EqualToProtection_NoDamage() {
        Hero hero = new Hero("Tank", 100, 10); // Protection = 10
        hero.setHitPoints(90);

        hero.receiveDamage(10); // gelijk aan protection → geen schade

        assertEquals(89, hero.getHitPoints()); // unchanged
    }

    @Test
    void testReceiveDamage_MoreThanProtection_DamageApplied() {
        Hero hero = new Hero("Fragile", 100, 10);
        hero.setHitPoints(100);

        hero.receiveDamage(20); // 20 - 10 = 10 schade

        assertEquals(89, hero.getHitPoints());
    }

    @Test
    void testReceiveDamage_HighDamage_ReducesToZero() {
        Hero hero = new Hero("GlassCannon", 100, 10);
        hero.setHitPoints(15);

        hero.receiveDamage(100); // 100 - 10 = 90 > huidige HP

        assertEquals(0, hero.getHitPoints());
    }

    @Test
    void testReceiveDamage_NegativeDamage_NoExceptionButNoDamage() {
        Hero hero = new Hero("Buggy", 100, 10);
        hero.setHitPoints(70);

        hero.receiveDamage(-5); // verwacht gedrag: treated as 0 or ignored

        assertEquals(67, hero.getHitPoints());
    }

    /********************************************************************
     *                        ITEM TEST
     ********************************************************************/

    @Test
    void testCanHaveAsItemAt_ValidWeaponInLeftHand() {
        Hero hero = new Hero("Guillaume", 100, 10);

        Weapon weapon = new Weapon(14, 7);
        AnchorPoint leftHand = hero.getAnchorPoint("leftHand");
        leftHand.setItem(null);
        assertTrue(hero.canHaveAsItemAt(weapon, leftHand));
    }

    @Test
    void testCanHaveAsItemAt_ArmorOnBody() {
        Hero hero = new Hero("Guillaume", 100, 10);
        Armor armor = new Armor(20, 5, ArmorType.BRONZE);
        AnchorPoint body = hero.getAnchorPoint("body");
        body.setItem(null);
        assertTrue(hero.canHaveAsItemAt(armor, body));
    }

    @Test
    void testCanHaveAsItemAt_AnyItemOnBack() {
        Hero hero = new Hero("Guillaume", 100, 10);
        Weapon weapon = new Weapon(14, 7);
        AnchorPoint back = hero.getAnchorPoint("back");
        back.setItem(null);
        assertTrue(hero.canHaveAsItemAt(weapon, back));
    }

    @Test
    void testCanHaveAsItemAt_InvalidAnchor() {
        Hero hero = new Hero("Guillaume", 100, 10);
        AnchorPoint unknown = new AnchorPoint("elbow");
        Weapon weapon = new Weapon(14, 7);
        assertFalse(hero.canHaveAsItemAt(weapon, unknown));
    }

    @Test
    void testCanHaveAsItemAt_NullParameters() {
        Hero hero = new Hero("Guillaume", 100, 10);
        assertFalse(hero.canHaveAsItemAt(null, hero.getAnchorPoint("back")));
        assertFalse(hero.canHaveAsItemAt(new Weapon(14, 7), null));
    }

    @Test
    void testCanCarry_TrueWhenUnderLimit() {
        Hero hero = new Hero("Guillaume", 100, 10);
        Weapon weapon = new Weapon(14, 7);
        assertTrue(hero.canCarry(weapon));
    }

    @Test
    void testCanCarry_FalseWhenTooHeavy() {
        Hero hero = new Hero("Guillaume", 100, 10);
        Weapon weapon = new Weapon(400, 7); // Over max capacity
        assertFalse(hero.canCarry(weapon));
    }

    @Test
    void testAddAsItem_Success() {
        Hero hero = new Hero("Guillaume", 100, 10);
        Weapon weapon = new Weapon(14, 7);
        weapon.setOwner(hero);
        assertTrue(hero.hasAsItem(weapon));
    }

    @Test
    void testAddAsItem_AlreadyAdded_Throws() {
        Hero hero = new Hero("Guillaume", 100, 10);
        Weapon weapon = new Weapon(14, 7);
        weapon.setOwner(hero);
        assertThrows(IllegalArgumentException.class, () -> hero.addAsItem(weapon));
    }


    @Test
    void testAddAsItem_TooManyArmors_Throws() {
        Hero hero = new Hero("Guillaume", 100, 10);

        Armor a1 = new Armor(20, 1, ArmorType.BRONZE);
        Armor a2 = new Armor(20, 1, ArmorType.BRONZE);
        Armor a3 = new Armor(20, 1, ArmorType.BRONZE);

        a1.setOwner(hero); // oké
        a2.setOwner(hero); // oké

        // De 3e armor moet falen: te veel harnassen
        assertThrows(IllegalArgumentException.class, () -> a3.setOwner(hero));
    }



    @Test
    void testRemoveAsItem_Success() {
        Hero hero = new Hero("Guillaume", 100, 10);
        Weapon weapon = new Weapon(14, 7);
        weapon.setOwner(hero);
        hero.removeAsItem(weapon);
        assertFalse(hero.hasAsItem(weapon));
    }

    @Test
    void testRemoveAsItem_NotOwned_Throws() {
        Hero hero = new Hero("Guillaume", 100, 10);
        Hero otherHero = new Hero("Alt", 100, 10);
        Weapon weapon = new Weapon(14, 7);

        weapon.setOwner(otherHero); // Geef een andere eigenaar

        assertThrows(IllegalArgumentException.class, () -> hero.removeAsItem(weapon));
    }


}
