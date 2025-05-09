import be.kuleuven.cs.som.annotate.Value;

/**
 * A class enumerating the possible conditions of a purse.
 *
 * @author  Jitse Vandenberghe
 * @version 1.0
 */
@Value
public enum Condition {
    /**
     * Indicates that the purse is functional and in good condition.
     */
    GOOD,

    /**
     * Indicates that the purse has been destroyed and can no longer be used.
     */
    DESTROYED;
}
