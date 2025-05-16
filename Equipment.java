import java.util.*;

import be.kuleuven.cs.som.annotate.*;

/**
 * A class representing pieces of equipment.
 *
 * @invar   Each piece of equipment must have a valid weight.
 *          | canHaveAsWeight(getWeight())
 *
 * @invar   Each piece of equipment must have a valid identification number.
 *          | canHaveAsIdentification(getClass(), getIdentification())
 *
 * @invar   Each piece of equipment must have a valid base value.
 *          | canHaveAsValue(getCurrentValue())
 *
 * @invar   isDestroyed() implies that the item is unusable()
 *          | isDestroyed(getCondition)
 *
 * @author Jitse Vandenberghe
 * @version 1.0
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
     *          |!canHaveAsWeight(weight)
     *
     * @throws  IllegalArgumentException
     *          If the given base Value is invalid.
     *          | !canHaveAsValue(baseValue)
     */
    public Equipment(int weight, int baseValue)
            throws IllegalArgumentException {

        if (!canHaveAsWeight(weight))
            throw new IllegalArgumentException("Weight cannot be negative.");
        if (!canHaveAsValue(baseValue))
            throw new IllegalArgumentException("Base value must be between 1 and the maximum value.");

        this.weight = weight;
        this.baseValue = baseValue;

        // Generates identification number and adds it to map to keep track of all identification numbers for each equipment type.
        this.identification = generateIdentification();
        addIdentification(this.getClass(), identification);
    }

    /**********************************************************
     * Weight - total programming
     **********************************************************/

    /**
     * Variable referencing the weight of this piece of equipment.
     */
    final int weight;

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
    public static boolean canHaveAsWeight(int weight) {
        return (weight >= 0);
    }

    /**********************************************************
     * Identification
     **********************************************************/

    /**
     * Variable referencing the unique identification number of this piece of equipment.
     */
    final long identification;

    /**
     * Variable referencing a static set containing all the identification numbers
     */
    static final Map<Class<?>, Set<Long>> equipmentByType = new HashMap<>();

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
    private static void addIdentification(Class<?> equipmentType, long identification) {
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
    public boolean canHaveAsIdentification(Class<?> equipmentType, long identification) {
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
     * @return  A non-negative and unique identification number that satisfies the conditions defined by canHaveAsIdentification.
     *          | result >= 0 && canHaveAsIdentification(this.getClass(), result)
     *
     * @post    The returned identification number is guaranteed to be unique among all equipment of the same type and positive.
     *          | canHaveAsIdentification(this.getClass(), result)
     *
     * @note    The identification number is not automatically added to the registry; this must be done separately
     *          (via addIdentification()).
     */
    public long generateIdentification() {
        Random random = new Random();
        long possibleID = Math.abs(random.nextLong());

        while (!canHaveAsIdentification(this.getClass(), possibleID)) {
            possibleID = Math.abs(random.nextLong());
        }

        return possibleID;
    }


    /**********************************************************
     * Value
     **********************************************************/

    /**
     * Variable referencing the base value of this piece of equipment, in dukaten.
     */
    final int baseValue;

    /**
     * Returns the maximum value of a piece of equipment.
     */
    @Basic
    public int getMaximumValue() {
        return Integer.MAX_VALUE;
    }

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
     *          exceed the maximum allowed value dukaten.
     *          | result == (value > 0 && value <= maximumValue)
     */
    public boolean canHaveAsValue(int value) {
        return value >= 0 && value <= getMaximumValue();
    }

    /**
     * Calculate the current value of this piece of equipment.
     *
     * @return  the current value in dukaten (always positive and less than the maximum value)
     *          | result > 0 && result <= maximumValue
     *
     * @note    We can say here that the result will be positive,
     * 	        there's not much more known at this level.
     * 	        (It is not really necessary to say this here.)
     */
    protected abstract int calculateCurrentValue();

    /**********************************************************
     * Owner 
     **********************************************************/

    /**
     * Variable referencing the entity (if any) that owns this piece of equipment.
     *
     * @note 	This class is the controlling class for the bidirectional relationship.
     */
    private static Entity owner = null;

    /**
     * Return the owner of this piece of equipment (if any).
     */
    @Raw @Basic
    public static Entity getOwner() {
        return owner;
    }

    /**
     * Set the owner to which this item belongs.
     *
     * This setter maintains the bidirectional relationship in both directions
     * and ensures that invariants on both ends are satisfied.
     *
     * @param   owner
     *          The new owner to which this item will belong.
     *
     * @pre     The equipment is not destroyed.
     *          | !isDestroyed()
     *
     * @post    The owner of this item is set to the given owner.
     *          | new.getOwner() == owner
     *
     * @effect  If the current owner is different from the given owner and the item is not stored in a backpack,
     *          it is removed from the current owner.
     *          | if (getOwner() != owner && !hasProperBackpack())
     *          | then getOwner().removeAsItem(this)
     *
     * @effect  If the item is currently stored in a backpack, it is removed from that backpack.
     *          | if hasProperBackpack()
     *          | then setBackpack(null)
     *
     * @effect  If the given owner is effective (non-null) and different from the current owner,
     *          the item is added to the new owner.
     *          | if (owner != null && getOwner() != owner)
     *          | then owner.addAsItem(this)
     *
     * @throws  IllegalArgumentException
     *          The given owner is non-null but cannot have this item.
     *          | owner != null && !owner.canHaveAsItem(this)
     *
     * @note    This setter ensures the consistency of the bidirectional relationship between the item and its owner.
     *          It also enforces the restrictions imposed by the referenced parent (owner).
     *
     * @note    Any exceptions potentially thrown by intermediate operations are suppressed here
     *          because they are covered by the explicit throws clause.
     */
    @Raw @Basic
    public void setOwner(Entity owner) {
        if (owner != null && !owner.canHaveAsItem(this))
            throw new IllegalArgumentException("This item can not belong to the given owner.");

        // Remember the previous owner
        Entity previousOwner = getOwner();

        // First, set up / break down the relationship from this side:
        this.owner = owner;


        // Then, break down the old relationship from the other side, if it existed
        // if item in backpack, you do not have to remove item from owner
        if ((previousOwner != null) && (!hasProperBackpack())){
            try{
                previousOwner.removeAsItem(this);
                // the prime object is now in a raw state!
            }catch(IllegalArgumentException e) {
                // Should never occur!
                throw e;
            }
        }

        // if item in backpack, then remove item from backpack
        if (hasProperBackpack()) {
            setBackpack(null);
        }


        // Finally, set up the new relationship from the other side, if needed
        if (owner != null) {
            try{
                owner.addAsItem(this);
            }catch(IllegalArgumentException e) {
                // Should never occur!
                throw e;
            }
        }
    }

    /**********************************************************
     * Backpack
     **********************************************************/

    /**
     * Variable referencing the backpack (if any) to which this
     * equipment belongs. (Default = null)
     *
     * @note 	This class is the controlling class for the bidirectional relationship.
     */
    private Backpack backpack = null;

    /**
     * Check whether the bidirectional relationship between this disk item and its parent directory is consistent.
     *
     * @return  True if the backpack has registered this item in its contents,
     *          false otherwise.
     *          | result == (getParentDirectory().hasAsItem(this))
     *
     * @note    This checker ensures that the parent directory has this item in its contents, maintaining the consistency
     *          of the bidirectional relationship between the item and its parent directory.
     */
    @Raw
    public boolean hasProperBackpack() {
        return getBackpack() != null && getBackpack().hasAsItem(this);
    }


    /**
     * Returns the backpack in which this equipment is stored (if any).
     */
    @Raw @Basic
    public Backpack getBackpack() {
        return backpack;
    }

    /**
     * Sets the backpack in which this item is stored.
     *
     * Maintains the bidirectional relationship between this item and its backpack,
     * and ensures that all relevant invariants are satisfied.
     *
     * @param   backpack
     *          The new backpack in which this item should be stored.
     *
     * @pre     The equipment is not destroyed.
     *          | !isDestroyed()
     *
     * @post    The backpack of this item is set to the given backpack.
     *          | getBackpack() == backpack
     *
     * @post    The owner of this item is updated to match the owner of the given backpack,
     *          or set to null if the backpack is null.
     *          | if backpack == null then new.getOwner() = null
     *          | else new.getOwner() = backpack.getOwner()
     *
     * @effect  If the new backpack is different from the current one, this item is
     *          removed from its current backpack.
     *          | if (old.getBackpack() != backpack)
     *          |     then old.getBackpack().removeItem(this)
     *
     * @effect  If the given backpack is non-null and not already the current backpack,
     *          this item is added to it.
     *          | if (backpack != null && old.getBackpack() != backpack)
     *          |     then backpack.addItem(this)
     *
     * @throws  IllegalArgumentException
     *          If the given backpack is non-null and cannot contain this item.
     *          | backpack != null && !backpack.canHaveAsItem(this)
     *
     * @note    This setter is solely responsible for maintaining the bidirectional link
     *          between this item and its backpack. It ensures both the consistency of
     *          the relationship and the ownership invariants.
     *
     * @note    Any exceptions that may be thrown internally by {@code addItem} or {@code removeItem}
     *          are caught and assumed not to occur under correct usage.
     */
    @Raw @Model
    public void setBackpack(Backpack backpack)
            throws IllegalArgumentException {
        if (backpack != null && !backpack.canHaveAsItem(this))
            throw new IllegalArgumentException("This item is not allowed by the given parent backpack!");

        // Remember the old parent directory
        Backpack oldBackpack = getBackpack();

        // First, set up / break down the relationship from this side:
        this.backpack = backpack;
        this.owner = Backpack.getOwner();

        // Then, break down the old relationship from the other side, if it existed
        if (oldBackpack != null) {
            try{
                oldBackpack.removeItem(this);
                // the prime object is now in a raw state!
            }catch(IllegalArgumentException e) {
                // Should never occur!
                assert false;
            }
        }

        // Finally, set up the new relationship from the other side, if needed
        if (backpack != null) {
            try{
                backpack.addItem(this);
                this.owner = Backpack.getOwner();
            }catch(IllegalArgumentException e) {
                // Should never occur!
                assert false;
            }
        }
    }

    /**********************************************************
     * Shininess
     **********************************************************/

    /**
     * Indicates whether this equipment is shiny.
     */
    boolean isShiny = false;

    /**
     * Return whether this equipment is shiny.
     *
     * @return  True if the equipment is shiny, false otherwise.
     *          | result == isShiny
     */
    @Basic
    public boolean isShiny() {
        return isShiny;
    }

    /**
     * Set whether this equipment is shiny.
     *
     * @param   shiny
     *          True if the equipment should be shiny, false otherwise.
     */
    @Model
    void setShiny(boolean shiny) {
        this.isShiny = shiny;
    }

    /**********************************************************
     * Condition
     **********************************************************/

    /**
     * The condition of the equipment, either GOOD or DESTROYED.
     */
    private Condition condition = Condition.GOOD;

    /**
     * Return the condition of this equipment.
     */
    @Basic
    public Condition getCondition() {
        return condition;
    }

    /**
     * Set the condition of this equipment to the given value,
     * but only if the current condition is GOOD.
     *
     * @param   condition
     *          The new condition to set.
     *
     * @post    If the current condition is GOOD, it will be changed to the given condition.
     *          Otherwise, the condition remains unchanged.
     *          | if (old(getCondition()) == Condition.GOOD)
     *          |     then getCondition() == condition
     *          | else
     *          |     getCondition() == old(getCondition())
     */
    @Model
    void setCondition(Condition condition) {
        if (getCondition() == Condition.GOOD) {
            this.condition = condition;
        }
    }

    /**
     * Set this equipment to destroyed.
     *
     * @effect  The condition is set to DESTROYED
     *          | setCondition(Condition.DESTROYED)
     */
    @Model
    void destroy() {
        setCondition(Condition.DESTROYED);
    }

    /**
     * Check if this equipment is destroyed.
     *
     * @return  If and only if the equipment is destroyed, false otherwise.
     *          | result == (this.condition == Condition.DESTROYED)
     */
    public boolean isDestroyed() {
        return this.condition == Condition.DESTROYED;
    }
}
