import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * A class of backpacks, involving contents.
 *
 * @author  Jitse Vandenberghe
 * @version 1.0
 */
public class Backpack extends StorageItem {

    /**********************************************************
     * Constructors
     **********************************************************/

    /**
     * Initialize a new backpack with the given weight, base value and capacity.
     *
     * @param   weight
     *          The weight of the backpack.
     *
     * @param   baseValue
     *          The base value of the backpack, in dukaten.
     *
     * @param   capacity
     *          The maximal amount of weight the backpack can carry.
     *
     * @effect  The backpack is initialized as a storage item.
     *          (weight, base value are set and an identification number is generated and assigned)
     *          | super(weight, baseValue, capacity)
     *
     * @note The owner of the backpack is initialized to null.
     */
    public Backpack(int weight, int baseValue, int capacity) {
        super(weight, baseValue, capacity);
    }

    /**********************************************************
     * Content
     **********************************************************/

    /**
     * Variable referencing a map that collects all equipment items contained in this
     * backpack, grouped by their identification number.
     *
     * The backpack is responsible for maintaining this collection. Items can only be
     * added and removed using the `addItem` and `removeItem` methods, which perform
     * defensive checks to ensure only valid items are stored. Heroes or monsters are
     * never allowed in a backpack.
     *
     * The key in the map is the identification number of the equipment, and the value is
     * a list of all items with that ID. This structure allows efficient lookup of all
     * equipment items with a given identification number, in time proportional to the number
     * of items with that same ID — independent of the total number of items in the backpack.
     *
     * @invar   The contents map is always effective (non-null).
     *          | contents != null
     *
     * @invar   Each key in the map is an integer identifying a valid equipment ID.
     *          | for each key in contents.keySet():
     *          |   for each item in contents.get(key):
     *          |     canHaveAsIdentification(item.getIdentification())
     *
     * @invar   Each value is a list of effective and non-null equipment items.
     *          | for each list in contents.values():
     *          |   list != null &&
     *          |   for each item in list:
     *          |     item != null
     *
     * @invar   Each item in the map references an item that references
     *          back to this backpack.
     *          | for each item in contents:
     *          |   item.getBackpack() == this
     *
     * @note The backpack class is the sole controller of the backpack–equipment relationship.
     *       Items can only be added or removed via the backpack’s own interface.
     */
    final Map<Long, ArrayList<Equipment>> contents = new HashMap<>();

    /**
     * Check whether the given item can be stored inside this backpack.
     *
     * @param   item
     *          The item to be checked.
     *
     * @return 	If storing the item results in surpassing the weight capacity of the backpack or the item is null,
     *          then false, otherwise true.
     * 			| result == ((getTotalWeight() + item.getWeight() < getCapacity()) && (item != null))
     */
    @Raw
    public boolean canHaveAsItem(@Raw Equipment item) {
        // Check if the item is null, which would be an invalid input
        if (item == null) {
            return false;
        }

        return getTotalWeight() + item.getWeight() <= getCapacity();
    }

    /**
     * Add the given item to the contents registered in this backpack.
     *
     * @param   item
     *          The item to be added.
     *
     * @post    The contents map will contain the identification as a key after this method is executed.
     *          | contents.containsKey(item.getIdentification()) == true
     *
     * @post    The array list of items for the identification number will contain the added item.
     *          | contents.get(item.getIdentification()).contains(item) == true
     *
     * @post    The size of the array list of items for the identification number will increase by 1 after adding the new item.
     *          | (contents.get(item.getIdentification).size() == old(contents.get(item.getIdentification).size()) + 1)
     *
     * @throws  IllegalArgumentException
     *          The item already exists in this backpack
     *          | hasAsItem(item)
     *
     * @throws  IllegalArgumentException
     *          This backpack can not store the given item.
     *          | !canHaveAsItem(item)
     *
     * @throws	IllegalStateException
     * 			The reference of the given (effective) item to this backpack is not yet set.
     * 			| (item != null) && !item.getParentDirectory() == this
     *
     * @note	This is an auxiliary method that helps set up a bidirectional relationship.
     * 			It should only be called from within the controlling class.
     * 			At that point, the other direction of the relationship should already be set up,
     * 			the given item is thus in a raw state.
     * 			All methods called with this raw item thus require a raw annotation of their parameter.
     *
     * @note	The throws clauses of the effects are cancelled by the throws clauses of this method.
     */
    @Model
    protected void addItem(@Raw Equipment item) throws IllegalArgumentException, IllegalStateException {
        if(hasAsItem(item))
            throw new IllegalArgumentException("The given item already exists in this backpack.");
        if(!canHaveAsItem(item))
            throw new IllegalArgumentException("The given item is not allowed in this backpack.");
        if (item.getBackpack() != this)
            throw new IllegalStateException("The given item does not yet reference this backpack.");

        // Get the existing list of items stored in the backpack with the given ID
        ArrayList<Equipment> itemsWithSameID = contents.get(item.getIdentification());

        // If the list does not exist, create a new list and put it in the map
        if (itemsWithSameID == null) {
            itemsWithSameID = new ArrayList<>();
            contents.put(item.getIdentification(), itemsWithSameID);
        }

        // Add the item to the list
        itemsWithSameID.add(item);
    }

    /**
     * Remove the given item from this backpack.
     *
     * @param 	item
     *        	The item to remove.
     *
     * @post    The array list of items for the identification number will not contain the given item.
     *          | contents.get(item.getIdentification()).contains(item) == false
     *
     * @post    The size of the array list of items for the identification number will decrease by 1 after removing the new item.
     *          | (contents.get(item.getIdentification).size() == old(contents.get(item.getIdentification).size()) - 1)
     *
     * @throws 	IllegalArgumentException
     *         	The given item is not in the backpack
     *         	| !hasAsItem(item)
     *
     * @throws	IllegalStateException
     * 			The reference of the given (effective) item to its backpack must already be broken down.
     * 			| (item != null) && item.getBackpack() == this
     *
     * @note	This is an auxiliary method that helps break down a bidirectional relationship.
     * 			It should only be called from within the controlling class.
     * 			At that point, the other direction of the relationship should already be broken down,
     * 			this directory is thus in a raw state. That item may also be in a raw state (no
     * 			parent and not yet terminated, or a new parent that doesn't point back, etc.).
     * 			All methods called with this raw item thus require a raw annotation of their parameter.
     */
    @Model @Raw
    protected void removeItem(@Raw Equipment item) throws IllegalArgumentException, IllegalStateException {
        if(!hasAsItem(item))
            throw new IllegalArgumentException("This item is not present in this backpack.");
        if((item != null) && item.getBackpack() == this)
            throw new IllegalStateException("The given item is still stored inside this backpack.");

        // Get the existing list of items stored in the backpack with the given ID
        ArrayList<Equipment> itemsWithSameID = contents.get(item.getIdentification());

        // Remove the item from the list
        itemsWithSameID.remove(item);
    }

    /**
     * Return the number of items with a given identification number that are stored inside this backpack.
     */
    @Basic @Raw
    public int getNbItemsWithID(long identification) {
        // Get the existing list of items stored in the backpack with the given ID
        ArrayList<Equipment> itemsWithSameID = contents.get(identification);

        if (itemsWithSameID == null) {
            return 0;  // No items with this ID, return 0
        }
        return itemsWithSameID.size();  // Return the number of items
    }

    /**
     *  Return the item registered at the given position for a specific identification number in this backpack.
     *
     * @param   identification
     *          The identification number of the item group.
     *
     * @param 	index
     *        	The index of the item to be returned (1-based index).
     *
     * @throws 	IllegalStateException
     *         	There are no items associated with the given identification.
     *         	| (itemsWithSameID == null)
     *
     * @throws  IndexOutOfBoundsException
     *          The index that was given is out of the valid range.
     *          | (index < 1 || index > itemsWithSameID.size())
     *
     * @note	We catch the exception that could be thrown when accessing the internal representation
     * 			and formulate a better suited one for the user of this class.
     * 			This adds to the encapsulation of the internal representation of this class.
     */
    @Basic @Raw
    public Equipment getItemAtWithID(long identification, int index) throws IndexOutOfBoundsException {
        // Retrieve the list of items associated with the given identification
        ArrayList<Equipment> itemsWithSameID = contents.get(identification);

        // Check if the identification exists in the map
        if (itemsWithSameID == null) {
            throw new IllegalStateException("No items found with identification: " + identification);
        }

        try {
            // Try to retrieve the item at the adjusted index (index - 1)
            return itemsWithSameID.get(index - 1);
        } catch (IndexOutOfBoundsException e) {
            // Catch the IndexOutOfBoundsException and rethrow it with a more informative message
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }

    /**
     * Check whether the given item is registered in this backpack.
     *
     * @param 	item
     *        	The item to be checked.
     *
     * @return 	True if an item equal to the given item is registered at some
     *         	position in this backpack;
     *         	false otherwise.
     *         	| result ==
     *         	|    for some I in 1..getNbItems() :
     *         	| 	      (getItemAt(I) == item)
     */
    @Raw
    public boolean hasAsItem(@Raw Equipment item) {
        for (int i=1; i<=getNbItemsWithID(item.getIdentification()); i++) {
            if (getItemAtWithID(item.getIdentification(), i) == item)
                return true;
        }
        return false;
    }

    /**
     * Check whether this directory contains an item with the given identification.
     *
     * @param	identification
     * 			The identification number to check.
     *
     * @return  True if an item with the given identification is stored inside the backpack.
     *          | result == (
     */
    public boolean containsItemWithIdentification(long identification){
        return contents.containsKey(identification) && !contents.get(identification).isEmpty();
    }

    /**********************************************************
     * Weight - total programming
     **********************************************************/

    /**
     * Returns the total weight of this backpack (the weight of the backpack itself and the weight of the items stored inside).
     */
    public int getTotalWeight() {
        int totalWeight = getWeight();

        // Iterate over every identification number (key) in the contents map
        for (Long identification : contents.keySet()) {
            // Retrieve the list of items (Equipment) associated with the current identification number
            ArrayList<Equipment> itemsWithSameID = contents.get(identification);

            // Iterate over each individual item in the list associated with the current identification number
            for (Equipment item : itemsWithSameID) {
                if (item instanceof Backpack) {
                    totalWeight += ((Backpack) item).getTotalWeight();
                }
                else if (item instanceof Purse) {
                    totalWeight += ((Purse) item).getTotalWeight();
                }
                else {
                    totalWeight += item.getWeight();
                }
            }
        }
        return totalWeight;
    }

    /**********************************************************
     * Value
     **********************************************************/

    /**
     * Returns the maximum value of a piece of equipment.
     */
    @Override @Basic @Immutable
    public int getMaximumValue() {
        return 500;
    }

    /**
     * Calculate the current value of this backpack.
     *
     * @return  The current value of the backpack, which is the sum of the base value of the backpack
     *          and the current values of all items stored inside the backpack.
     *          | result == getBaseValue() + + sum of item.getCurrentValue() for each item in the contents
     */
    @Override
    protected int calculateCurrentValue() {
        int totalValue = getBaseValue();

        // Iterate over every identification number (key) in the contents map
        for (Long identification : contents.keySet()) {
            // Retrieve the list of items (Equipment) associated with the current identification number
            ArrayList<Equipment> itemsWithSameID = contents.get(identification);

            // Iterate over each individual item in the list associated with the current identification number
            for (Equipment item : itemsWithSameID) {
                totalValue += item.getCurrentValue();
            }
        }
        return totalValue;
    }

    /**********************************************************
     * Condition
     **********************************************************/

    /**
     * Set this backpack to destroyed and empty its contents.
     *
     * @effect The condition is set to DESTROYED.
     *         | setCondition(Condition.DESTROYED)
     *
     * @effect All items in the backpack are removed using setBackpack(null).
     *         | for each item in contents: item.setBackpack(null)
     */
    @Override @Model
    void destroy() {
        // Create a copy to avoid ConcurrentModificationException
        List<Equipment> allItems = new ArrayList<>();
        for (List<Equipment> itemList : contents.values()) {
            allItems.addAll(itemList);
        }

        // Remove all items from the backpack
        for (Equipment item : allItems) {
            item.setBackpack(null);
        }

        setCondition(Condition.DESTROYED);
    }

}