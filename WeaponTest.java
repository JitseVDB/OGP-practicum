import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A JUnit (5) test class for testing the non-private methods of the Weapon Class.
 *
 * @author  Jitse Vandenberghe
 * @version 1.0
 */
public class WeaponTest {

    // WEAPONS
    private static Weapon weapon_A;

    @BeforeEach
    public void setUpEquipment() {
        weapon_A = new Weapon(50,  91);
    }

    /**
     * CONSTRUCTORS
     */

    @Test
    void testConstructor_ValidArguments_ShouldInitializeFields() {
        int oldSizeIdentificationOfWeaponB4B = Equipment.equipmentByType.get(Weapon.class).size();
        Weapon weapon_B = new Weapon(50, 70);

        // 1. effect of super(weight, baseValue)
        // 1.1. postcondition on weight
        assertEquals(50, weapon_A.getWeight());
        // 1.2. postcondition on base value
        assertEquals(0, weapon_A.getBaseValue());
        // 1.3 Postcondition on identification
        // 1.3.1 Postcondition on the uniqueness of the ID and on the fact that the ID is positive
        // Check that the assigned identification is strictly positive and divisible by 2 and 3
        assertTrue(weapon_A.getIdentification() % 2 == 0 & weapon_A.getIdentification() % 3 == 0);
        assertTrue(weapon_A.getIdentification() > 0);
        // (!) At this point, the identification is already present in the map for this equipment type.
        //     Therefore, canHaveAsIdentification will return false, not because the ID is negative or not divisible by 2 and 3,
        //     but because it is no longer unique. This confirms the method correctly enforces uniqueness.
        assertFalse(weapon_A.canHaveAsIdentification(Weapon.class, weapon_A.getIdentification()));
        // 1.4 effect of addIdentification()
        // 1.4.1 postcondition on the keys of equipmentByType map
        assertTrue(Equipment.equipmentByType.containsKey(Weapon.class));
        // 1.4.2 postcondition on the contents of the set containing the IDs associated with an equipment type
        assertTrue(Equipment.equipmentByType.get(Weapon.class).contains(weapon_A.getIdentification()));
        // 1.4.3 postcondition on the size of the set containing the IDs for the specified equipment type
        assertEquals(Equipment.equipmentByType.get(Weapon.class).size(), oldSizeIdentificationOfWeaponB4B + 1);
        // 2. effect of setDamage()
        assertEquals(91, weapon_A.getDamage());
    }

    @Test
    void testConstructor_InvalidWeight_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Weapon(-100,  91));
    }

    @Test
    void testConstructor_InvalidDamage_ShouldThrowException() {
        // Test upper limit
        assertThrows(IllegalArgumentException.class, () -> new Weapon(50, Weapon.getMaximumDamage()+1));
        // Test lower limit
        assertThrows(IllegalArgumentException.class, () -> new Weapon(50, 0));
        // Test divisble by 7
        assertThrows(IllegalArgumentException.class, () -> new Weapon(50, 96));
    }

    /**
     * IDENTIFICATION
     */

    @Test
    public void testCanHaveAsIdentification_allCases() {
        long identification_A = weapon_A.getIdentification();
        long possibleIdentification_A = weapon_A.generateIdentification();

        // Valid: divisible by both 2 and 3
        assertTrue(weapon_A.canHaveAsIdentification(Weapon.class, possibleIdentification_A));

        // Invalid: not divisible by 2 or 3
        assertFalse(weapon_A.canHaveAsIdentification(Weapon.class, possibleIdentification_A+1));

        // Invalid: divisible by 2 and 3 but not unique
        assertFalse(weapon_A.canHaveAsIdentification(Weapon.class, identification_A));

        // Invalid: negative number
        assertFalse(weapon_A.canHaveAsIdentification(Weapon.class, -(possibleIdentification_A+1)));

        // Valid: divisible by both 2 and 3 and unique
        assertTrue(weapon_A.canHaveAsIdentification(Weapon.class, possibleIdentification_A));
    }

    @Test
    public void testGenerateIdentification_allCases() {
        long identification = weapon_A.generateIdentification();

        // 1. postcondition on identification number
        assertTrue(weapon_A.canHaveAsIdentification(weapon_A.getClass(), identification));
    }

    /**
     * DAMAGE
     */

    @Test
    public void testsetDamage_allCases() {
        weapon_A.setDamage(7);
        assertEquals(7, weapon_A.getDamage());
    }

    @Test
    public void testIsValidDamage_allCases() {
        long identification_A = weapon_A.getIdentification();

        // 1. positive
        assertTrue(weapon_A.isValidDamage(7));
        assertFalse(weapon_A.isValidDamage(0));
        assertFalse(weapon_A.isValidDamage(-7));

        // 2. less than or equal maximum damage
        int invalidDamage = Weapon.getMaximumDamage() + (7 - Weapon.getMaximumDamage() % 7);
        assertFalse(weapon_A.isValidDamage(invalidDamage));

        // 3. Divisible by 7
        assertTrue(weapon_A.isValidDamage(49));
        assertFalse(weapon_A.isValidDamage(50));
    }

    /**
     * DAMAGE
     */

    @Test
    public void testCalculateCurrentValue_allCases() {
        assertEquals(182, weapon_A.calculateCurrentValue());
    }

}
