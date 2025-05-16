import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Raw;
import be.kuleuven.cs.som.annotate.Value;

/**
 * An enumeration of skin types.
 * In its current definition, the class only distinguishes between
 * though, thick and scaly skin. With each skin type, a maximal protection is associated.
 *
 * @invar      Each type must have a valid maximal protection.
 *             | isValidMaximalProtection(getMaxProtection())
 *
 * @author     Jitse Vandenberghe
 * @version    1.1
 */
@Value
public enum SkinType {
    TOUGH(10), THICK(20), SCALY(30);

    /**
     * Initialize a new type of skin with given maximal protection.
     *
     * @param   maxProtection
     *          The maximal protection of the new type of skin.
     *
     * @post    The given maximal protection is registered as the maximal protection of this skin type.
     *          | new.getWeight() == weight
     *
     * @throws  IllegalArgumentException
     *          If the given maximal protection is invalid.
     *          |!isValidMaxProtection(maxProtection)
     */
    @Raw
    SkinType(int maxProtection) {
        if (!isValidMaxProtection(maxProtection))
            throw new IllegalArgumentException("The maximal protection must be between 0 and 100");

        this.maxProtection = maxProtection;
    }

    /**
     * Return the maximal protection of this skin type.
     */
    @Raw @Basic @Immutable
    public int getMaxProtection() {
        return maxProtection;
    }

    /**
     * Check whether the given maximal protection is a valid maximal protection for this skin type.
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
     * Variable registering the maximal protection of this type of skin.
     */
    private final int maxProtection;
}
