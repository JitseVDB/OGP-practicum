import be.kuleuven.cs.som.annotate.*;

import java.util.Random;

/**
 * A class of armor, involving protection.
 *
 * @invar   Each piece of armor must have a valid protection.
 *          | isValidProtection(getProtection());
 *
 * @note    Only the additional invariants that are not already defined in the
 *          superclass `Equipment` need to be specified.
 *
 * @author  Jitse Vandenberghe
 * @version 1.0
 */
public class Armor extends Equipment {

    /**********************************************************
     * Constructors
     **********************************************************/

    /**
     * Initialize a new piece of armor with the given weight, base value, and protection.
     *
     * @param   weight
     *          The weight of the piece of armor.
     *
     * @param   baseValue
     *          The base value of the piece of armor, in dukaten.
     *
     * @param   type
     *          The type of this piece of armor (Tin / Bronze).
     *
     * @effect  The piece of armor is initialized as a piece of equipment.
     *          (weight, base value are set and an identification number is generated and assigned)
     *          | super(weight, baseValue)
     *
     * @post    The maximal protection is set to the given maximal protection.
     *          | new.getMaximalProtection() == maximalProtection
     *
     * @effect  The new piece of armor is initialized with the maximal protection as current protection.
     *          | setCurrentProtection(maximalProtection)
     */
    public Armor(int weight, int baseValue, ArmorType type) {
        super(weight, baseValue);

        this.type = type;
        this.maximalProtection = type.getMaxProtection();
        setCurrentProtection(maximalProtection);
    }

    /**********************************************************
     * Identification
     **********************************************************/

    /**
     * Check whether the given identification number is a valid identification number for this piece of armor.
     *
     * @param   equipmentType
     *          The class type of the equipment for which the identification number is being validated.
     *
     * @param   identification
     *          The identification number to check.
     *
     * @return  True if the identification number is non-negative, a prime number
     *          and unique within the given equipment type; false otherwise.
     *          | result == super.isValidIdentification(equipmentType, identification)
     *          |        && isPrime(identification)
     */
    @Override
    public boolean canHaveAsIdentification(Class<?> equipmentType, long identification) {
        return super.canHaveAsIdentification(equipmentType, identification)
                // The identification number must be divisible by 2 and 3.
                && isPrime(identification);
    }

    /**
     * Check whether the given number is a prime number.
     *
     * @param   number The number to check.
     *
     * @return  True if the number is prime, false otherwise.
     */
    public static boolean isPrime(long number) {
        // Numbers less than or equal to 1 are not prime
        if (number <= 1) {
            return false;
        }

        // 2 is the only even prime number
        if (number == 2) {
            return true;
        }

        // Eliminate even numbers greater than 2
        if (number % 2 == 0) {
            return false;
        }

        // Only check for factors up to the square root of the number
        // If a number has a factor greater than its square root, it must also have one smaller
        int sqrt = (int) Math.sqrt(number);

        // Check only odd numbers starting from 3
        for (int i = 3; i <= sqrt; i += 2) {
            // If divisible, it's not prime
            if (number % i == 0) {
                return false;
            }
        }

        // If no factors found, it's prime
        return true;
    }

    /**
     * Generates a valid and unique identification number for this piece of armor.
     *
     * @return  A non-negative, prime and unique identification number that satisfies the conditions defined by canHaveAsIdentification.
     *          | canHaveAsIdentification(this.getClass(), result)
     *
     * @post   The returned identification number is guaranteed to be unique among all equipment of the same type.
     *         | canHaveAsIdentification(this.getClass(), result)
     *
     * @note   The identification number is not automatically added to the registry; this must be done separately
     *         (via addIdentification()).
     */
    @Override
    protected long generateIdentification() {
        Random random = new Random();
        long possibleID = Math.abs(random.nextLong() % 1_000_000);

        // Make sure the number is odd (since even numbers > 2 are not prime)
        if (possibleID % 2 == 0) {
            possibleID++;
        }

        // Keep generating a new number until we find a valid identification number
        while (!canHaveAsIdentification(this.getClass(), possibleID)) {
            possibleID = Math.abs(random.nextLong() % 1_000_000);
            if (possibleID % 2 == 0) {
                possibleID++;  // Ensure the number is odd
            }
        }

        return possibleID;
    }

    /**********************************************************
     * Protection
     **********************************************************/

    /**
     * Variable referencing the maximum protection this piece of armor can provide.
     */
    private final int maximalProtection;

    /**
     * Variable referencing the current protection this piece of armor can provide.
     */
    private int currentProtection;

    /**
     * Return the maximum protection this piece of armor can provide.
     */
    @Basic @Immutable
    public int getMaximalProtection() {
        return maximalProtection;
    }

    /**
     * Check whether the given maximal protection is a valid maximal protection for this piece of armor.
     *
     * @param   maximalProtection
     *          The maximal protection to check.
     *
     * @return  True if and only if the given maximal protection is positive and does not exceed 100.
     *          | result == (value > 0 && value <= 100)
     */
    public boolean isValidMaximalProtection(int maximalProtection) {
        return maximalProtection > 0 && maximalProtection <= 100;
    }

    /**
     * Return the current protection this piece of armor can provide
     */
    @Basic
    public int getCurrentProtection() {
        return currentProtection;
    }

    /**
     * Set the current protection of this piece of armor to the given damage.
     *
     * @param   currentProtection
     *          The new current protection for this piece of armor.
     *
     * @pre     The given damage must be legal.
     *          | isValidDamage(damage)
     * @post    The given damage is registered as the damage of this piece of armor.
     *          | new.getDamage() == damage
     */
    public void setCurrentProtection(int currentProtection) {
        if (!isValidCurrentProtection(currentProtection))
            throw new IllegalArgumentException("The current protection must be between 0 and " + maximalProtection);
        this.currentProtection = currentProtection;
    }

    /**
     * Check whether the given maximal protection is a valid maximal protection for this piece of armor.
     *
     * @param   currentProtection
     *          The current protection to check.
     *
     * @return  True if and only if the given current protection is greater or equal to zero and does not exceed the maximal protection.
     *          | result == (value >= 0 && value <= maximalProtection)
     */
    public boolean isValidCurrentProtection(int currentProtection) {
        return maximalProtection >= 0 && currentProtection <= maximalProtection;
    }

    /**********************************************************
     * Value
     **********************************************************/

    /**
     * Calculate the current value of this piece of armor.
     *
     * @return  The calculated current value, guaranteed to be greater or equal to zero and at most 100.
     *          | result == damage * currentProtection/maximalProtection
     */
    protected int calculateCurrentValue() {
        return baseValue * currentProtection/maximalProtection;
    }

    /**********************************************************
     * Armor Type
     **********************************************************/

    /**
     * Variable referencing the type of armor (Tin/Bronze)
     */
    private final ArmorType type;

    /**
     * Return the type of armor of this piece of armor.
     */
    public ArmorType getType() {
        return type;
    }

}