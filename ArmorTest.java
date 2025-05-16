import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A JUnit (5) test class for testing the non-private methods of the Armor Class.
 *
 * @author  Jitse Vandenberghe
 * @version 1.0
 */
public class ArmorTest {

    // AUXILIARY METHOD
    private long square(long x) {
        return Math.multiplyExact(x, x);
    }

    // ARMOR
    private static Armor armor_A, armor_B;

    @BeforeEach
    public void setUpEquipment() {
        armor_A = new Armor(50, 100, ArmorType.BRONZE);
        armor_B = new Armor(50, 100, ArmorType.TIN);
    }

    /**
     * CONSTRUCTORS
     */

    @Test
    void testConstructor_ValidArguments_ShouldInitializeFields() {
        int oldSizeIdentificationOfArmorB4C = Equipment.equipmentByType.get(Armor.class).size();
        Armor armor_C = new Armor(35, 75, ArmorType.TIN);

        // 1. effect of super(weight, baseValue)
        // 1.1. postcondition on weight
        assertEquals(50, armor_A.getWeight());
        // 1.2. postcondition on base value
        assertEquals(100, armor_A.getBaseValue());
        // 1.3 Postcondition on identification
        // 1.3.1 Postcondition on the uniqueness of the ID and on the fact that the ID is positive
        // Check that the assigned identification is strictly positive and divisible by 2 and 3
        assertTrue(armor_A.isPrime(armor_A.getIdentification()));
        assertTrue(armor_A.getIdentification() > 0);
        // (!) At this point, the identification is already present in the map for this equipment type.
        //     Therefore, canHaveAsIdentification will return false, not because the ID is negative or not divisible by 2 and 3,
        //     but because it is no longer unique. This confirms the method correctly enforces uniqueness.
        assertFalse(armor_A.canHaveAsIdentification(Armor.class, armor_A.getIdentification()));
        // 1.4 effect of addIdentification()
        // 1.4.1 postcondition on the keys of equipmentByType map
        assertTrue(Equipment.equipmentByType.containsKey(Armor.class));
        // 1.4.2 postcondition on the contents of the set containing the IDs associated with an equipment type
        assertTrue(Equipment.equipmentByType.get(Armor.class).contains(armor_A.getIdentification()));
        // 1.4.3 postcondition on the size of the set containing the IDs for the specified equipment type
        assertEquals(Equipment.equipmentByType.get(Armor.class).size(), oldSizeIdentificationOfArmorB4C + 1);
        // 2. postcondition on type
        assertEquals(ArmorType.BRONZE, armor_A.getType());
        // 3. postcondition on maximalprotection
        assertEquals(ArmorType.BRONZE.getMaxProtection(), armor_A.getMaximalProtection());
        // 4. effect of setCurrentProtection()
        assertEquals(ArmorType.BRONZE.getMaxProtection(), armor_A.getCurrentProtection());
        // 5. postcondition on shininess
        assertTrue(armor_A.isShiny());
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
     * IDENTIFICATION
     */

    @Test
    public void testCanHaveAsIdentification_allCases() {
        // 1. positive
        assertTrue(armor_A.canHaveAsIdentification(armor_A.getClass(), Math.abs(armor_A.generateIdentification())));
        assertFalse(armor_A.canHaveAsIdentification(armor_A.getClass(), -(armor_A.generateIdentification())));

        // 2. uniqueness of identification
        assertTrue(armor_A.canHaveAsIdentification(armor_A.getClass(), armor_A.generateIdentification()));
        assertFalse(armor_A.canHaveAsIdentification(armor_A.getClass(), armor_A.getIdentification()));

        // 3. prime
        assertTrue(armor_A.canHaveAsIdentification(armor_A.getClass(), armor_A.generateIdentification()));
        assertFalse(armor_A.canHaveAsIdentification(armor_A.getClass(), square(armor_A.generateIdentification())));
    }

    @Test
    public void testIsPrime_NegativeAndZeroAndOne() {
        assertFalse(armor_A.isPrime(-10));
        assertFalse(armor_A.isPrime(0));
        assertFalse(armor_A.isPrime(1));
    }

    @Test
    public void testIsPrime_SmallPrimes() {
        assertTrue(armor_A.isPrime(2));
        assertTrue(armor_A.isPrime(3));
        assertTrue(armor_A.isPrime(5));
        assertTrue(armor_A.isPrime(7));
    }

    @Test
    public void testIsPrime_SmallNonPrimes() {
        assertFalse(armor_A.isPrime(4));
        assertFalse(armor_A.isPrime(6));
        assertFalse(armor_A.isPrime(8));
        assertFalse(armor_A.isPrime(9));
        assertFalse(armor_A.isPrime(10));
    }

    @Test
    public void testIsPrime_LargePrime() {
        assertTrue(armor_A.isPrime(7919)); // known prime
    }

    @Test
    public void testIsPrime_LargeNonPrime() {
        assertFalse(armor_A.isPrime(7920)); // not prime
    }

    @Test
    public void testIsPrime_EvenNumbersGreaterThan2() {
        assertFalse(armor_A.isPrime(100));
        assertFalse(armor_A.isPrime(123456));
    }

    @Test
    public void testIsPrime_OddCompositeNumbers() {
        assertFalse(armor_A.isPrime(121));  // 11 * 11
        assertFalse(armor_A.isPrime(143));  // 11 * 13
    }

    @Test
    public void testGenerateIdentification_allCases() {
        long identification = armor_A.generateIdentification();

        // 1. postcondition on identification number
        assertTrue(armor_A.canHaveAsIdentification(armor_A.getClass(), identification));
    }

    /**
     * PROTECTION
     */

    @Test
    public void testIsValidMaximalProtection_allCases() {
        assertTrue(armor_A.isValidMaximalProtection(ArmorType.BRONZE.getMaxProtection()));
        assertTrue(armor_B.isValidMaximalProtection(ArmorType.TIN.getMaxProtection()));
        assertFalse(armor_A.isValidMaximalProtection(-1));
        assertFalse(armor_A.isValidMaximalProtection(0));
        assertTrue(armor_A.isValidMaximalProtection(100));
        assertFalse(armor_A.isValidMaximalProtection(101));
    }

    @Test
    public void testSetCurrentProtection_shouldSetField() {
        armor_A.setCurrentProtection(armor_A.getMaximalProtection());
        assertEquals(armor_A.getMaximalProtection(), armor_A.getCurrentProtection());
        armor_A.setCurrentProtection(0);
        assertEquals(0, armor_A.getCurrentProtection());
    }

    @Test
    public void testSetCurrentProtection_AboveMaximum_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> armor_A.setCurrentProtection(armor_A.getMaximalProtection()+1));
    }

    @Test
    public void testSetCurrentProtection_BelowMinimum_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> armor_A.setCurrentProtection(-(armor_A.getMaximalProtection())));
    }

    @Test
    public void testisValidCurrentProtection_allCases() {
        assertTrue(armor_A.isValidCurrentProtection(ArmorType.BRONZE.getMaxProtection()));
        assertTrue(armor_B.isValidCurrentProtection(ArmorType.TIN.getMaxProtection()));
        assertFalse(armor_A.isValidCurrentProtection(-1));
        assertTrue(armor_A.isValidCurrentProtection(0));
        assertTrue(armor_A.isValidCurrentProtection(50));
        assertFalse(armor_A.isValidCurrentProtection(ArmorType.BRONZE.getMaxProtection()+1));
    }

    /**
     * VALUE
     */

    @Test
    public void testCalculateCurrentValue_allCases() {
        Armor armor = new Armor(100, 100, ArmorType.BRONZE);
        armor.setCurrentProtection(armor.getMaximalProtection()/2);
        assertEquals(50, armor.calculateCurrentValue());

        armor = new Armor(100, 100, ArmorType.BRONZE);
        armor.setCurrentProtection(0);
        assertEquals(0, armor.calculateCurrentValue());

        armor = new Armor(100, 100, ArmorType.BRONZE);
        armor.setCurrentProtection(armor.getMaximalProtection());
        assertEquals(100, armor.calculateCurrentValue());

        armor = new Armor(100, 100, ArmorType.BRONZE);
        armor.setCurrentProtection(armor.getMaximalProtection()/3);
        assertEquals(33, armor.calculateCurrentValue());

        armor = new Armor(100, 80, ArmorType.BRONZE);
        armor.setCurrentProtection(armor.getMaximalProtection());
        assertEquals(80, armor.calculateCurrentValue());
    }


}