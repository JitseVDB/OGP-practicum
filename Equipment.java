import java.util.*;

import be.kuleuven.cs.som.annotate.*;

/**
 * A class representing pieces of equipment in the game.
 *
 * Each piece of equipment has a weight, a base value, an identification number,
 * and can be owned by an entity or be stored in a backpack.
 *
 * @invar   Each piece of equipment must have a valid weight.
 *          | isValidWeight(getWeight())
 *
 * @invar   Each piece of equipment must have a valid identification number.
 *          | isValidIdentification(getClass(), getIdentification())
 *
 * @invar   Each piece of equipment must have a valid base value.
 *          | isValidValue(getCurrentValue())
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
     *
     * @param   baseValue
     *          The base value (in dukaten) for the equipment.
     *
     * @post    The given weight is registered as the weight of this equipment.
     *          | new.getWeight() == weight
     *
     * @post    The given base value is registered as the base value.
     *          | new.getBaseValue() == baseValue
     *
     * @post    A randomly generated identification number is registered as the identification number.
     *          | new.getIdentification() == generateIdentification();
     *
     * @effect  The identification number is added to the map to keep track of all identification numbers
     *          for each equipment type.
     *          | addIdentification(this.getClass(), identification)
     *
     * @throws  IllegalArgumentException
     *          If the given weight is invalid.
     *          |!isValidWeight(weight)
     *
     * @throws  IllegalArgumentException
     *          If the given identification number is invalid.
     *          | !isValidIdentification(this.getClass(), identification)
     *
     * @throws  IllegalArgumentException
     *          If the given base Value is invalid.
     *          | !isValidValue(baseValue)
     */
    public Equipment(int weight, int baseValue)
            throws IllegalArgumentException {

        if (!isValidWeight(weight))
            throw new IllegalArgumentException("Weight cannot be negative.");
        if (!isValidValue(baseValue))
            throw new IllegalArgumentException("Base value must be between 1 and 1000.");

        this.weight = weight;
        this.baseValue = baseValue;

        // Generates identification number and adds it to map to keep track of all identification numbers for each equipment type.
        this.identification = generateIdentification();
        addIdentification(this.getClass(), identification);
    }

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
     * @return  True if and only if the given weight is positive.
     *          | result == (weight >= 0)
     */
    public static boolean isValidWeight(int weight) {
        return (weight >= 0);
    }

    /**********************************************************
     * Identification
     **********************************************************/

    /**
     * Unique identification number of this piece of equipment.
     */
    private final long identification;

    /**
     * Static set containing all the identification numbers
     */
    private static Map<Class<?>, Set<Long>> equipmentByType = new HashMap<>();

    /**
     * Returns the identification number of this piece of equipment.
     */
    @Raw @Basic
    public long getIdentification() {
        return identification;
    }

    /**
     * Adds a new identification number for a specific type of equipment.
     *
     * This method ensures that the given identification number is added to the set
     * of identification numbers for the specified equipment type. If the set of
     * IDs for the given equipment type does not exist, it is created.
     *
     * @param   equipmentType
     *          The class type of the equipment for which the identification number is being added.
     *
     * @param   identification
     *          The unique identification number to add for the specified equipment type.
     *
     * @post    The equipmentByType map will contain the key equipmentType after this method is executed.
     *          | equipmentByType.containsKey(equipmentType) == true
     *
     * @post    The set of IDs for the given equipment type will contain the added identification number.
     *          | equipmentByType.get(equipmentType).contains(identification) == true
     *
     * @post    The size of the set of IDs for the specified equipment type will increase by 1 after adding the new ID.
     *          | (equipmentByType.get(equipmentType).size() == old(equipmentByType.get(equipmentType).size()) + 1)
     */
    public static void addIdentification(Class<?> equipmentType, long identification) {
        // Get the existing set of ID's for the given type of equipment
        Set<Long> existingIDs = equipmentByType.get(equipmentType);

        // If the set does not exist, create a new set associated with the equipmentType in the equipmentByType map
        if (existingIDs == null) {
            existingIDs = new HashSet<>();
            equipmentByType.put(equipmentType, existingIDs);
        }

        // Add the identification number to the set associated with the equipmentType
        existingIDs.add(identification);

    }

    /**
     * Check whether the given identification number is a valid identification number for this piece of equipment.
     *
     * @param   equipmentType
     *          The class type of the equipment for which the identification number is being validated.
     *
     * @param   identification
     *          The identification number to check.
     *
     * @return  True if the identification number is positive and unique within the given equipment type;
     *          false otherwise.
     *          | result == (identification >= 0) && (isUniqueForType(instance of this ,getIdentification())
     */
    public boolean isValidIdentification(Class<?> equipmentType, long identification) {
        return (identification >= 0) && (isUniqueForType(equipmentType, identification));
    }

    /**
     * Check wether the given identification number is unique for the specified equipment type.
     *
     * @param   equipmentType
     *          The class type of the equipment for which the identification number is being validated.
     *
     * @param   identification
     *          The identification number to check for uniqueness.
     *
     * @return  True if the identification number is unique for the given equipment type;
     *          false if the identification number already exists for that equipment type.
     *          | result == (existingIDs == null || !existingIDs.contains(identification))
     */
    public static boolean isUniqueForType(Class<?> equipmentType, long identification) {
        // Get the existing set of ID's for the given type of equipment
        Set<Long> existingIDs = equipmentByType.get(equipmentType);

        // Iterate over every ID in the set for this equipment type
        if (existingIDs != null) {
            for (Long existingID : existingIDs) {
                // If the ID already exists, return false
                if (existingID == identification) {
                    return false;  // ID is not unique
                }
            }
        }

        // If no matching ID is found, return true (ID is unique)
        return true;

    }

    /**
     * Generates a valid and unique identification number for this piece of equipment.
     *
     * A valid identification number must be non-negative and unique among all equipment of the same type.
     * The method randomly generates identification numbers until it finds one that is valid.
     *
     * @return  A non-negative and unique identification number that satisfies the conditions defined by isValidIdentification.
     *          | result >= 0 && isValidIdentification(this.getClass(), result)
     *
     * @post    The returned identification number is guaranteed to be unique among all equipment of the same type.
     *          | isValidIdentification(this.getClass(), result)
     *
     * @note    The identification number is not automatically added to the registry; this must be done separately
     *          (via addIdentification()).
     */
    protected long generateIdentification() {
        Random random = new Random();
        long possibleID = Math.abs(random.nextLong());

        while (!isValidIdentification(this.getClass(), possibleID)) {
            possibleID = Math.abs(random.nextLong());
        }

        return possibleID;
    }


    /**********************************************************
     * Value
     **********************************************************/

    /**
     * Base value of this piece of equipment, in dukaten.
     */
    private final int baseValue;

    /**
     * Maximum value of a piece of equipment, in dukaten.
     */
    private final int maximumValue = 1000;

    /**
     * Returns the base value of this piece of equipment.
     */
    @Raw @Basic
    public int getBaseValue() {
        return baseValue;
    }

    /**
     * Returns the maximum value of a piece of equipment
     */
    @Basic @Immutable
    public int getMaximumValue() {
        return maximumValue;
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
     *          exceed the maximum allowed value dukaten.
     *          | result == (value > 0 && value <= maximumValue)
     */
    protected boolean isValidValue(int value) {
        return value > 0 && value <= maximumValue;
    }

    /**
     * Calculate the current value of this piece of equipment.
     *
     * @return  The calculated current value, guaranteed to be positive and at most 1000.
     *          | result > 0 && result <= 1000
     */
    protected abstract int calculateCurrentValue();

    /**********************************************************
     * Backpack -> verder uitwerken later
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
     * Owner -> verder uitwerken later
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

}
