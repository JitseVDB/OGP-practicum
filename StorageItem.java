
/**
 * A class of storage items, involving capacity.
 *
 * @invar	The capacity of a purse is a positive number.
 * 			| isValidCapacity(getCapacity())
 *
 * @note    The content attribute is not implemented in the superclass because its implementation
 *          differs significantly between Backpack and Purse.
 *
 * @author  Jitse Vandenberghe
 * @version 1.0
 */
public abstract class StorageItem extends Equipment{

    /**********************************************************
     * Constructors
     **********************************************************/

    /**
     * Initialize a new storage item with the given weight, base value and capacity.
     *
     * @param   weight
     *          The weight of the storage item.
     *
     * @param   baseValue
     *          The base value of the storage item, in dukaten.
     *
     * @param   capacity
     *          The weight capacity that the storage item can carry.
     *
     * @effect  The storage item is initialized as a piece of equipment.
     *          (weight, base value are set and an identification number is generated and assigned)
     *          | super(weight, baseValue)
     *
     * @post    The capacity is set to the given capacity.
     *          | new.getCapacity() == capacity
     *
     * @note    The owner of the storage item is initialized to null.
     */
    public StorageItem(int weight, int baseValue, int capacity) {
        super(weight, baseValue);

        this.capacity = capacity;
    }

    /**********************************************************
     * Capacity
     **********************************************************/

    /**
     * Variable referencing the capacity of this storage item.
     */
    private final int capacity;

    /**
     * Return the capacity of this storage item.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Checks whether a given capacity is a valid capacity.
     *
     * @param 	capacity
     * 			The capacity to check.
     * @return	True if the capacity is a positive number
     * 			| result == (capacity >= 0)
     *
     */
    public boolean isValidCapacity(int capacity) {
        return capacity >= 0;
    }


}
