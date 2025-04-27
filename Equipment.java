import be.kuleuven.cs.som.annotate.*;

/**
 * A class representing pieces of equipment in the game.
 *
 * Each piece of equipment has a weight, a base value, an identification number,
 * and can be owned by an entity or be stored in a backpack.
 *
 * @invar Each piece of equipment must have a valid weight.
 *      | isValidWeight(getWeight())
 *
 * @author Jitse Vandenberghe
 *
 * @version 1.1
 */
public abstract class Equipment {

    /**********************************************************
     * Constructors
     **********************************************************/

    /**
     * Initialize a new piece of equipment with the given weight, identification number and base value.
     *
     * @param   weight
     *          The weight of the new piece of equipment.
     * @param   identification
     *          The unique identification number for the equipment.
     * @param   baseValue
     *          The base value (in dukaten) for the equipment.
     *
     * @post    The given weight is registered as the weight of this equipment.
     *          | new.getWeight() == weight
     * @post    The given identification number is registered as the identification number.
     *          | new.getIdentification() == identification
     * @post    The given base value is registered as the base value.
     *          | new.getBaseValue() == baseValue
     *
     * @throws  IllegalArgumentException
     *          If the given weight is negative or the base value is not valid.
     *          | weight < 0 || !isValidValue(baseValue)
     */
    public Equipment(int weight, long identification, int baseValue)
            throws IllegalArgumentException {

        if (!isValidWeight(weight))
            throw new IllegalArgumentException("Weight cannot be negative.");
        if (!isValidValue(baseValue))
            throw new IllegalArgumentException("Base value must be between 1 and 1000.");

        this.weight = weight;
        this.identification = identification;
        this.baseValue = baseValue;
    }

    /**********************************************************
     * Identification
     **********************************************************/

    /**
     * Unique identification number of this piece of equipment.
     */
    private final long identification;

    /**
     * Returns the identification number of this piece of equipment.
     */
    @Raw @Basic
    public long getIdentification() {
        return identification;
    }

    /**********************************************************
     * Backpack
     **********************************************************/

    /**
     * Variable referencing the backpack (if any) in which this equipment is stored.
     */
    private Backpack backpack = null;

    /**
     * Returns the backpack in which this equipment is stored.
     */
    @Raw @Basic
    public Backpack getBackpack() {
        return backpack;
    }

    /**
     * Set the backpack in which this equipment is stored.
     *
     * @param   backpack
     *          The new backpack to set, or null if the equipment is not stored.
     */
    @Raw @Basic
    public void setBackpack(Backpack backpack) {
        this.backpack = backpack;
    }

    /**********************************************************
     * Owner
     **********************************************************/

    /**
     * Variable referencing the entity (if any) that owns this piece of equipment.
     */
    private Entity owner = null;

    /**
     * Returns the entity that currently owns this piece of equipment.
     */
    @Raw @Basic
    public Entity getOwner() {
        return owner;
    }

    /**
     * Sets the owner of this piece of equipment.
     *
     * @param   owner
     *          The entity that will own this equipment, or null if the item doesn't have an owner.
     */
    @Raw @Basic
    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    /**********************************************************
     * Value
     **********************************************************/

    /**
     * Base value of this piece of equipment, in dukaten.
     */
    private final int baseValue;

    /**
     * Returns the base value of this piece of equipment.
     */
    @Raw @Basic
    public int getBaseValue() {
        return baseValue;
    }

    /**
     * Returns the current value of this piece of equipment.
     */
    @Raw @Basic
    public int getCurrentValue() {
        return calculateCurrentValue();
    }

    /**
     * Check whether the given value is a valid value for this piece of equipment.
     *
     * @param   value
     *          The value to check.
     *
     * @return  True if and only if the given value is positive and does not
     *          exceed 1000 dukaten.
     *          | result == (value > 0 && value <= 1000)
     */
    protected boolean isValidValue(int value) {
        return value > 0 && value <= 1000;
    }

    /**
     * Calculate the current value of this piece of equipment.
     *
     * @return  The calculated current value, guaranteed to be positive and at most 1000.
     *          | result > 0 && result <= 1000
     */
    protected abstract int calculateCurrentValue();

    /**********************************************************
     * Weight
     **********************************************************/

    /**
     * The weight of this piece of equipment.
     */
    private final int weight;

    /**
     * Returns the weight of this piece of equipment.
     */
    @Raw @Basic
    public int getWeight() {
        return weight;
    }

    /**
     * Check whether the given weight is a valid weight for a piece of equipment.
     *
     * @param   weight
     *          The weight to check.
     *
     * @return  True if and only if the given weight is not negative.
     *          | result == (weight >= 0)
     */
    public static boolean isValidWeight(int weight) {
        return (weight >= 0);
    }
}
