import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HeroesTest {

    private Hero testhero;

    @BeforeEach
    void setUp() {
        testhero = new Hero("Arthur", 50, 10.0);
    }

    // --- Constructor Tests ---

    @Test
    void constructor_ValidName_ShouldCreateHero() {
        assertEquals("Arthur", testhero.getName());
        assertEquals(50, testhero.getHitPoints());
    }

    @Test
    void constructor_InvalidName_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Hero("arthur", 50, 10.0));
        assertThrows(IllegalArgumentException.class, () -> new Hero("Arthur!!", 50, 10.0));
        assertThrows(IllegalArgumentException.class, () -> new Hero("Ar:thur", 50, 10.0));
        assertThrows(IllegalArgumentException.class, () -> new Hero(null, 50, 10.0));
        assertThrows(IllegalArgumentException.class, () -> new Hero("", 50, 10.0));
    }

    // --- Strength Modification Tests ---

    @Test
    void multiplyStrength_ValidFactor_ShouldMultiply() {
        testhero.multiplyStrength(2);
        assertEquals(20.0, testhero.getAttackPower() - getWeaponDamage(testhero));
    }

    @Test
    void multiplyStrength_ZeroFactor_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> testhero.multiplyStrength(0));
    }

    @Test
    void divideStrength_ValidFactor_ShouldDivide() {
        testhero.divideStrength(2);
        assertEquals(5.0, testhero.getAttackPower() - getWeaponDamage(testhero));
    }

    @Test
    void divideStrength_ZeroFactor_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> testhero.divideStrength(0));
    }

    // --- Fighting Logic and Hitpoints Tests ---

    @Test
    void setFighting_ToFalse_AdjustsHitpointsToClosestLowerPrime() {
        Hero h = new Hero("PrimeTest", 50, 10.0);
        // Using reflection to simulate damage (removeHitPoints is private)
        setPrivateField(h, "hitpoints", 44);
        h.setFighting(false); // Should reduce to 43
        assertEquals(43, h.getHitPoints());
    }

    @Test
    void setFighting_ToTrue_ShouldNotAdjustHitpoints() {
        setPrivateField(testhero, "hitpoints", 44);
        testhero.setFighting(true);
        assertEquals(44, testhero.getHitPoints());
    }

    // --- Weapon Tests ---

    @Test
    void equipLeftAndRightWeapon_ShouldAffectAttackPower() {
        Weapon sword = new Weapon(10, 14);
        Weapon dagger = new Weapon(10, 7);
        testhero.equipLeftHand(sword);
        testhero.equipRightHand(dagger);
        assertEquals(25.0, testhero.getAttackPower());
    }

    @Test
    void equipNullWeapon_ShouldNotAddDamage() {
        testhero.equipLeftHand(null);
        testhero.equipRightHand(null);
        assertEquals(10.0, testhero.getAttackPower());
    }

    // --- Helper Methods ---

    private int getWeaponDamage(Hero testhero) {
        // Trick to extract weapon damage from attack power
        return (int) (testhero.getAttackPower() - testhero.getIntrinsicStrength());
    }

    private void setPrivateField(Hero testhero, String fieldName, int value) {
        try {
            var field = Hero.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(testhero, value);
        } catch (Exception e) {
            fail("Reflection failed on field: " + fieldName);
        }
    }
}
