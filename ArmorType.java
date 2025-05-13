import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Raw;
import be.kuleuven.cs.som.annotate.Value;

/**
 * An enumeration of armor types.
 * In its current definition, the class only distinguishes between
 * tin and bronze pieces of armor. With each armor type, a maximal protection is associated.
 *
 * @invar      Each type must have a valid maximal protection.
 *             | isValidMaximalProtection(getMaxProtection())
 *
 * @author     Jitse Vandenberghe
 * @version    1.1
 */
@Value
public enum ArmorType {
    TIN(70), BRONZE(90);

    /**
     * Initialize a new type of armor with given maximal protection.
     *
     * @param   maxProtection
     *          The maximal protection of the new type of armor.
     *
     * @post    The given maximal protection is registered as the maximal protection of this armor type.
     *          | new.getWeight() == weight
     *
     * @throws  IllegalArgumentException
     *          If the given maximal protection is invalid.
     *          |!isValidMaxProtection(maxProtection)
     */
    @Raw
    ArmorType(int maxProtection) {
        if (!isValidMaxProtection(maxProtection))
            throw new IllegalArgumentException("The maximal protection must be between 0 and 100");

        this.maxProtection = maxProtection;
    }

    /**
     * Return the maximal protection of this armor type.
     */
    @Raw @Basic @Immutable
    public int getMaxProtection() {
        return maxProtection;
    }

    /**
     * Check whether the given maximal protection is a valid maximal protection for this armor type.
     *
     * @param   maximalProtection
     *          The maximal protection to check.
     *
     * @return  True if and only if the given maximal protection is positive and does not exceed 100.
     *          | result == (value > 0 && value <= 100)
     */
    public boolean isValidMaxProtection(int maximalProtection) {
        return maximalProtection > 0 && maximalProtection <= 100;
    }

    /**
     * Variable registering the maximal protection of this type of armor.
     */
    private final int maxProtection;
}
