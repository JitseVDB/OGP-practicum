
import be.kuleuven.cs.som.annotate.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * A class representing the entities (monsters and heroes) in the game.
 *
 * @invar   The name of the entity must be valid.
 *          | canHaveAsName(getName())
 *
 * @invar   The amount of hitpoints must be valid.
 *          | isValidHitPoints(getHitPoints())
 *
 * @invar   Each entity must have a valid protection factor.
 *          | isValidProtection(getProtection())
 *
 * @invar   The maximum amount of hitpoints of an entity must be valid
 *          | isValidMaxHitPoints(getMaxHitPoints())
 *
 * @author Ernest De Gres
 * @author Jitse Vandenberghe
 *
 * @version 1.1
 */


public abstract class Entity {

    /**********************************************************
     * Constructors
     **********************************************************/

    /**
     * Initialize a new entity with the given name, hitpoints, max hitpoints and protection factor.
     *
     * @param   name
     *          The name of the new entity.
     *
     * @param   maxHitPoints
     *          The maximum number of hitpoints.
     *
     * @pre     The maximum amount of hitpoints must be strictly positive
     *          | isValidMaxHitPoints(getMaxHitPoints())
     *
     * @post    The name of the entity is set to the given name.
     *          | new.getName() == name
     *
     * @post    The maximum hitpoints is set to the given value.
     *          | new.getMaxHitPoints() == maxHitPoints
     *
     * @post    The hitpoints are set to the given value, unless it is not prime. In that case, they are corrected to the closest lower prime.
     *          | if (!isPrime(maxHitPoints)) then new.getHitPoints() == getClosestLowerPrime(maxHitPoints)
     *          | else new.getHitPoints() == maxHitPoints
     *
     * @post    The new entity is not fighting.
     *          | !new.isFighting()
     *
     * @effect  The anchor points are initialized.
     *          | initializeAnchorPoints()
     *
     * @throws IllegalArgumentException
     *         If the given name is invalid
     *         | !canHaveAsName(name)
     *
     */
    public Entity(String name, int maxHitPoints)
            throws IllegalArgumentException {
        if (!canHaveAsName(name))
            throw new IllegalArgumentException("Invalid name for the entity.");

        this.name = name;
        this.maxHitPoints = maxHitPoints;
        this.hitPoints = maxHitPoints;
        this.anchorPoints = new ArrayList<>();

        // Prime-correction at initialization, because not fighting
        if (!isPrime(getHitPoints())) {
            int p = getClosestLowerPrime(getHitPoints());
            this.hitPoints = p;
        }

        // Initialize AnchorPoints
        initializeAnchorPoints();
    }

    /**********************************************************
     * Name
     **********************************************************/

    /**
     * The name of the entity.
     */
    private final String name;

    /**
     * Returns the name of the entity.
     */
    @Raw @Basic
    public String getName() {
        return name;
    }

    /**
     * Check if the name is valid
     *
     * @param	name
     * 			The name to be validated.
     *
     * @return  True if name is valid
     *
     */
    @Raw
    public abstract boolean canHaveAsName(String name);

    /**********************************************************
     * HitPoints
     **********************************************************/

    /**
     * Variable that indicates whether the entity is currently fighting. He is initialized as not fighting
     */
    private boolean isFighting = false;

    /**
     * The current number of hitpoints of the entity.
     */
    private int hitPoints;

    /**
     * The maximum number of hitpoints of the entity.
     */
    private int maxHitPoints;

    /**
     * Returns the current hitpoints.
     */
    @Raw @Basic
    public int getHitPoints() {
        return hitPoints;
    }

    /**
     * Returns the maximum hitpoints.
     */
    @Raw @Basic
    public int getMaxHitPoints() {
        return maxHitPoints;
    }

    /**
     * Sets the hitpoints of entity if valid amount.
     *
     * @param   hitPoints
     *          The amount of hitpoints to set.
     *
     * @pre     The amount of hitpoints must be valid.
     *          isValidHitPoints()
     *
     * @post    hitpoints is set to the given amount
     *          | new.getHitPoints() == hitpoints
     *
     */
    @Raw @Basic
    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    /**
     * Sets the hitpoints of entity.
     *
     * @param   maxHitPoints
     *          The entity that will own this equipment, or null if the item doesn't have an owner.
     *
     * @pre maxHitPoints >= 0
     *
     * @post maximum hitpoints is set to the given amount
     */
    @Raw @Basic
    public void setMaxHitPoints(int maxHitPoints) {
        this.maxHitPoints = maxHitPoints;
    }

    /**
     * Check whether the given amount of maximum hitpoints is a valid amount.
     *
     * @param   maxHitPoints
     *          The amount to check.
     *
     * @return  True if and only if the amount of maximum hitpoints is strictly positive.
     *          | result == (maxHitPoints > 0)
     */
    public static boolean isValidMaxHitPoints(int maxHitPoints) {
        return maxHitPoints > 0;
    }


    /**
     * Check whether the amount of hitpoints is a valid amount.
     *
     * @param   hitPoints
     *          The amount to check.
     *
     * @return  True if and only if the amount of hitpoints is positive and less than the maximun amount of hitpoints.
     *          | result == (hitPoints >= 0 &&  hitpoints <= maxHitPoints)
     */
    public boolean isValidHitPoints(int hitPoints) {
        if (hitPoints == 0) {
            return true;
        }

        if ((hitPoints > 0) && (hitPoints <= maxHitPoints)){
            if (!isFighting && isPrime(getHitPoints())){
                return true;
            }
            if (!isFighting && !isPrime(getHitPoints())){
                return false;
            }
            return true;
        }
        return false;
    }


    /**
     * Check whether the entity is still alive.
     *
     * @return True if the entity has more than 0 hitpoints.
     *
     */
    @Raw @Basic
    public boolean isAlive() {
        return hitPoints > 0;
    }

    /**********************************************************
     * isFighting
     **********************************************************/


    /**
     * Updates the fighting status of this hero.
     *
     * @param status
     *        true if the hero enters combat, false if they exit combat.
     * @post isFighting() == status
     *
     * @effect If status == false and current hit points are not prime,
     *         the hit points are reduced to the nearest lower prime.
     */
    public void setFighting(boolean status) {
        this.isFighting = status;
        if (!status && !isPrime(getHitPoints())) {
            int p = getClosestLowerPrime(getHitPoints());
            this.hitPoints = p;
        }
    }

    /**
     * Returns true if the entity is fighting
     */
    @Basic
    public boolean isFighting() {
        return isFighting;
    }

    /**
     * Determines if a given number is a prime number.
     *
     * @param number
     *        The number to check.
     * @return true if the number is prime; false otherwise.
     */
    public boolean isPrime(int number) {
        if (number == 0) return true;

        if (number < 2 ) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }

    /**
     * Returns the closest lower prime number less than the given starting value.
     *
     * @param start
     *        The starting value.
     * @return The closest lower prime number.
     */
    public int getClosestLowerPrime(int start) {
        for (int i = start - 1; i >= 2; i--) {
            if (isPrime(i)) return i;
        }
        return 2; // fallback
    }

    /**
     * Increases the hero’s current hit points by the given amount.
     * If the hero is not fighting, and the result is not a prime number,
     * the hit points are reduced to the closest lower prime.
     * @param amount
     *        The number of hit points to add
     *
     * @post getHitPoints() is increased by amount, but not beyond getMaxHitPoints()
     * @effect If not fighting and the result is not prime, hit points are reduced
     *         to the closest lower prime.
     */
    public void addHitPoints(int amount) {
        if ((this.hitPoints += amount) > maxHitPoints) {
            this.hitPoints = maxHitPoints;
        }
        else {
            if (!isFighting && !isPrime(this.hitPoints)) {
                int p = getClosestLowerPrime(this.hitPoints);
                this.hitPoints = p;
            }
        }
    }

    /**
     * Decreases the hero’s current hit points by the given amount.
     * If the hero is not fighting, and the result is not a prime number,
     * the hit points are further reduced to the closest lower prime.
     *
     * @param amount
     *        The number of hit points to remove
     *
     * @post getHitPoints() is decreased by amount, but not below zero.
     * @effect If not fighting and the result is not prime,
     *         hit points are reduced to the closest lower prime.
     */
    public void removeHitPoints(int amount) {
        if ((this.hitPoints -= amount) <= 0) {
            this.hitPoints = 0;
        }
        else{
            if (!isFighting && !isPrime(getHitPoints())) {
                int p = getClosestLowerPrime(getHitPoints());
                this.hitPoints = p;
            }
        }
    }

    /**********************************************************
     * Capacity
     **********************************************************/

    /**
     * Variable referencing the capacity of this monster.
     */
    public int capacity;

    /**
     * Return the capacity of this monster.
     */
    @Raw @Basic
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns the total weight of all the items which the entity is carrying.
     * If an item is a backpack, its total weight includes the contents of the backpack.
     * Empty anchor points are ignored.
     *
     * @return The sum of the weights of all items in anchor points.
     */
    public int getTotalWeight() {
        int totalWeight = 0;

        for (int i = 1; i <= getNbAnchorPoints(); i++) {
            Equipment item = getAnchorPointAt(i).getItem();
            if (item == null) continue;

            if (item instanceof Backpack)
                totalWeight += ((Backpack) item).getTotalWeight();

            else if (item instanceof Purse)
                totalWeight += ((Purse) item).getTotalWeight();

            else totalWeight += item.getWeight();
        }

        return totalWeight;
    }

    /**
     * Checks whether this entity is able to carry the given equipment item
     * without exceeding their capacity.
     * The check is based on the current carried weight and the item's weight.
     *
     * @param item
     *        The equipment item to check
     *
     * @post The result is true if the current capacity plus the item’s weight
     *       does not exceed the hero’s maximum capacity.
     *     | result == (getTotalWeight() + item.getWeight()) <= getCapacity()
     */
    public boolean canCarry(Equipment item) {
        return (getTotalWeight() + item.getWeight()) <= getCapacity();
    }

    /**********************************************************
     * Anchors
     **********************************************************/

    /**
     * Variable referencing a list collecting all anchor points of this entity.
     *
     * @invar  ... -> indien nodig aanvullen
     */
    protected List<AnchorPoint> anchorPoints;

    public void addAnchorPoint(AnchorPoint anchorPoint){
        anchorPoints.add(anchorPoint);
    }

    public abstract void initializeAnchorPoints();

    /**********************************************************
     * Equipment
     **********************************************************/

    /**
     * Add the given item to the anchor points registered to this entity.
     *
     * @param   item
     *          The equipment to be added.
     *
     * @effect  The equipment is added to available anchor point.
     *        | addToAnchorPoint(item)
     *
     * @throws  IllegalArgumentException
     *          The item is already stored in this entity.
     *        | hasAsItem(item)

     * @throws  IllegalStateException
     *          The reference from the item to this entity has not yet been set.
     *        | (item != null) && !item.getOwner() == this
     *
     * @note    This is an auxiliary method that completes a bidirectional relationship.
     *          It should only be called from within the controlling class.
     *          At that point, the other direction of the relationship is already set up,
     *          so the given item is in a raw state.
     *          All methods called with this raw item thus require a raw annotation of their parameter.
     * @note    The throws clauses of the effects are cancelled by the throws clauses of this method.
     */
    @Model
    protected void addAsItem(Equipment item) throws IllegalArgumentException, IllegalStateException {
        if (hasAsItem(item))
            throw new IllegalArgumentException("This item is already attached.");

        if (item != null && item.getOwner() != this)
            throw new IllegalStateException("Item does not reference this entity as its owner.");

        try {
            addToAnchorPoint(item);
        } catch (IllegalArgumentException e) {
            // Should not occur
            assert false;
        }
    }

    /**
     * Check whether the given item is assigned to this entity.
     *
     * @param item
     *        The equipment to be checked.
     *
     * @return True if and only if the given item is attached to one of this entity's anchor points.
     *         False otherwise
     *       | result ==
     *       |   for some I in 1..getNbAnchorPoints():
     *       |     getAnchorPointAt(i).getItem() == item
     */
    @Raw
    public boolean hasAsItem(@Raw Equipment item) {
        for (int i = 1; i <= getNbAnchorPoints(); i++) {
            if (getAnchorPointAt(i).getItem() == item)
                return true;
        }
        return false;
    }

    /**
     * Return the number of anchor points of this entity.
     */
    @Basic @Raw
    public int getNbAnchorPoints() {
        return anchorPoints.size();
    }

    /**
     * Return the anchor point at the given position in this entity.
     *
     * @param index
     *        The index of the anchor point to be returned.
     *
     * @throws IllegalArgumentException
     *         If the given index is not strictly positive or exceeds the number of anchor points
     *         registered to this entity.
     *       | (index < 1) || (index > getNbAnchorPoints())
     *
     * @note	We catch the exception that could be thrown when accessing the internal representation
     * 			and formulate a better suited one for the user of this class.
     * 			This adds to the encapsulation of the internal representation of this class. *
     */
    @Basic @Raw
    public AnchorPoint getAnchorPointAt(int index) throws IndexOutOfBoundsException {
        try {
            return anchorPoints.get(index - 1);
        } catch (IndexOutOfBoundsException e) {
            //The exception e contains a message indicating that 'index-1' is out of bounds
            //Here, we throw a new Exception with the right information
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }


    /**
     * Returns a list of all items currently carried by this entity.
     *
     * Only non-null items are included in the result. Items are retrieved from each anchor point.
     *
     * @return A list containing all non-null items held in the entity's anchor points.
     */
    public List<Equipment> getAllItems() {
        List<Equipment> equipmentList = new ArrayList<>();

        for (int i = 1; i <= getNbAnchorPoints(); i++) {
            Equipment item = getAnchorPointAt(i).getItem();
            if (item != null) {
                equipmentList.add(item);
            }
        }

        return equipmentList;
    }


    /**
     * Determines whether this entity is allowed to carry the given item
     * by checking if there exists any anchor point where it can be legally placed
     *
     * @param   item
     *          The item to check
     *
     * @return   The result is true there exists at least one anchor point such that:
     *          the anchor is empty, the capacity is not exceeded and the item can be legally placed at anchorpoint
     *          | result == (exists ap in anchorPoints:
     *          |               ap.isEmpty() && canCarry()) && canHaveAsItemAt
     *
     *
     */
    public boolean canHaveAsItem(Equipment item) {
        if (canCarry(item)) {
            for (AnchorPoint ap : anchorPoints) {
                if (ap.isEmpty() && canHaveAsItemAt(item, ap))
                    return true;
            }
            return false;
        }
        return false;
    }

    public abstract boolean canHaveAsItemAt(Equipment item, AnchorPoint anchorPoint);

    /**
     * Add the given item to the first anchorpoint of this entity that accepts it.
     *
     * @param item
     *        The equipment to add.
     *
     * @effect The item is added to the first valid anchor point.
     *       | anchorpoint.setItem(item)
     *
     */
    public void addToAnchorPoint(Equipment item) {
        for (int i = 1; i <= getNbAnchorPoints(); i++) {
            AnchorPoint anchorpoint = getAnchorPointAt(i);

            if (anchorpoint.isEmpty()) {
                if (canHaveAsItemAt(item, anchorpoint)) {
                    anchorpoint.setItem(item);
                    return;
                }
            }
        }
    }


    /**
     * Remove the given item from this entity.
     *
     * @param item
     *        The equipment to remove.
     *
     * @effect The item is removed from the anchor point it was registered at.
     *         | anchorpoint.setItem(null)
     *
     * @throws IllegalArgumentException
     *        Entity does not have the given item.
     *       | !hasAsItem(item)
     * @throws IllegalStateException
     *         The reference of the given (effective) item to its owner must already be broken down.
     *       | (item != null) && item.getOwner() == this
     *
     * @note This is an auxiliary method used to break a bidirectional relationship.
     *       It should only be called from within the controlling class.
     *       At that point, the reference from the item to this entity must already be cleared.
     */
    @Model
    @Raw
    protected void removeAsItem(@Raw Equipment item) throws IllegalArgumentException, IllegalStateException {
        if (!hasAsItem(item))
            throw new IllegalArgumentException("This entity does not have the item.");

        if (item != null && item.getOwner() == this)
            throw new IllegalStateException("Item still references this entity as its owner.");

        for (int i = 1; i <= getNbAnchorPoints(); i++) {
            AnchorPoint anchorpoint = getAnchorPointAt(i);
            if (anchorpoint.getItem() == item) {
                anchorpoint.setItem(null);
                return;

            }

        }

    }

    /**
     * Checks whether the entity has a free anchorpoint available.
     *
     * @return true if entity has an empty anchorpoint, false otherwise.
     *          | result == (anchorpoint.isEmpty())
     *
     */
    public boolean hasFreeAnchorPoint(){
        for (int i = 1; i <= getNbAnchorPoints(); i++) {
            AnchorPoint anchorpoint = getAnchorPointAt(i);
            if (anchorpoint.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /*****************************************************************
     * GetAnchorPoints
     *****************************************************************/

    /**
     * Returns the anchor point with the given name, if it exists.
     *
     * @param name
     *        The name of the anchor point to retrieve
     *
     * @return The matching AnchorPoint, or null if no such anchor exists.
     *
     * @post If an anchor point exists with a name equal to the given name,
     *       it is returned. Otherwise, the result is null.
     *     | if (name == null)
     *     |     then result == null
     *     | else if (exists ap in anchorPoints where ap.getName().equalsIgnoreCase(name))
     *     |     then result == that ap
     *     | else result == null
     */
    public AnchorPoint getAnchorPoint(String name) {
        if (name == null) return null;
        for (AnchorPoint ap : anchorPoints) {
            if (ap.getName().equalsIgnoreCase(name)) {
                return ap;
            }
        }
        return null;
    }

    /**
     * returns the achorpoint to which the item is attached.
     *
     * @param item
     *        item to return the anchorpoint of.
     * @return if item is attached to anchorpoint in this entity return the anchorpoint,
     *         return null otherwise.
     *          |  if (for some I in 1..getNbAnchorPoints()):
     *          |       getAnchorPointAt(i).getItem() == item
     *          |       return anchorpoint
     */
    public AnchorPoint getAnchorPointOfItem(Equipment item) {
        for (int i = 1; i <= getNbAnchorPoints(); i++) {
            AnchorPoint anchorpoint = getAnchorPointAt(i);
            if ((anchorpoint.getItem() == item) && item != null) {
                return anchorpoint;
            }
        }
        return null;
    }

    /**
     * Returns a map of all current anchor points and the items assigned to them.
     *
     * @return a map where each key is an anchor point name and the value is the Equipment at that point (null if empty).
     */
    public Map<String, Equipment> getAnchors() {
        Map<String, Equipment> anchors = new HashMap<>();
        for (AnchorPoint ap : anchorPoints) {
            anchors.put(ap.getName(), ap.getItem());
        }
        return anchors;
    }


}
