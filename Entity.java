import be.kuleuven.cs.som.annotate.*;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * A class representing the entities (monsters and heroes) in the game.
 *
 * @invar  The name of the entity must be valid.
 *       | canHaveAsName(getName())
 * @invar  The amount of hitpoints must be valid.
 *       | isValidHitPoints(getHitPoints())
 * @invar  Each entity must have a valid protection factor.
 *       | isValidProtection(getProtection())
 * @invar  The maximum amount of hitpoints of an entity must be valid
 *       | isValidMaxHitPoints(getMaxHitPoints())
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
     * @param name
     *        The name of the new entity.
     * @param maxHitPoints
     *        The maximum number of hitpoints.
     * @param protection
     *        The protection factor of the entity.
     *
     * @post The name of the entity is set to the given name.
     *       | new.getName() == name
     * @post The maximum and current hitpoints are set to the given value.
     *       | new.getMaxHitPoints() == maxHitPoints
     *       | new.getHitPoints() == maxHitPoints
     * @post The protection factor is set to the given value.
     *       | new.getProtection() == protection
     *
     * @throws IllegalArgumentException
     *         If the given name is invalid
     *         | !canHaveAsName(name)
     *
     *         If the given amount of maximum hitpoints in invalid
     *         | !isValidMaxHitPoints(maxHitPoints)
     *
     *         If the given protection factor is invalid
     *         | !isValidProtection(protection)*
     *
     */
    public Entity(String name, int maxHitPoints, int protection)
            throws IllegalArgumentException {
        if (!canHaveAsName(name))
            throw new IllegalArgumentException("Invalid name for the entity.");
        if (!isValidMaxHitPoints(maxHitPoints))
            throw new IllegalArgumentException("Max hitpoints cannot be negative.");
        if (!isValidProtection(protection))
            throw new IllegalArgumentException("Protection must be strictly positive.");

        this.name = name;
        this.maxHitPoints = maxHitPoints;
        this.hitPoints = maxHitPoints;
        this.protection = protection;
        this.anchorPoints = new ArrayList<>();
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
    public abstract boolean canHaveAsName(String name);

    /**********************************************************
     * HitPoints
     **********************************************************/

    /**
     * The current number of hitpoints of the entity.
     */
    private int hitPoints;

    /**
     * The maximum number of hitpoints of the entity.
     */
    private final int maxHitPoints;

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
     * Check whether the given amount of maximum hitpoints is a valid amount.
     *
     * @param   maxHitPoints
     *          The amount to check.
     *
     * @return  True if and only if the amount of maximum hitpoints is positive.
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
        return ((hitPoints >= 0) && (hitPoints <= maxHitPoints));
    }

    /**
     * Adds the hero's hitpoints by a given amount.
     * If the hero is not fighting, the result will be adjusted to the closest lower prime if necessary.
     *
     * @pre The given amount must be a positive number.
     *      | amount > 0
     * @pre The resulting hitpoints after addition must not exceed the maximum hitpoints.
     *      | amount + getHitpoints() <= maxHitPoints
     */
    public void addHitPoints(int amount) {
        hitPoints += amount;
    }

    /**
     * Decreases the hero's hitpoints by a given amount.
     * If the hero is not fighting, the result will be adjusted to the closest lower prime if necessary.
     *
     * @pre The given amount must be a positive number.
     *      | amount > 0
     * @pre The resulting hitpoints after subtraction must not fall below zero.
     *      | getHitpoints() - amount >= 0
     */

    public void removeHitPoints(int amount) {
        hitPoints -= amount;
    }

    /**
     * Reduces the hitpoints of an entity based on the amount of damage and after specific calculations
     *
     * @param damage
     *        The amount of damage to apply.
     *
     * @post The hitpoints are reduced after calculations
     */
    public abstract void receiveDamage(int damage);

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
     * Protection
     **********************************************************/

    /**
     * The protection factor of the entity.
     */
    private final int protection;

    /**
     * Returns the protection factor of the entity.
     */
    @Raw @Basic
    public int getProtection() {
        return protection;
    }

    /**
     * Check whether the given protection factor is valid.
     *
     * @param   protection
     *          The protection factor to check.
     *
     * @return  True if and only if the protection factor is strictly positive.
     *          | result == (protection >= 0)
     */
    public static boolean isValidProtection(int protection) {
        return protection >= 0;
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
     * @throws  IllegalArgumentException
     *          The item cannot be added to this entity (illegal equipment).
     *        | !canHaveAsItem(item)
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

        if (!canHaveAsItem(item))
            throw new IllegalArgumentException("This item is not allowed.");

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


    public abstract boolean canHaveAsItem(Equipment item);

    /**
     * Add the given item to the first anchorpoint of this entity that accepts it.
     *
     * @param item
     *        The equipment to add.
     *
     * @effect The item is added to the first valid anchor point.
     *       | anchorpoint.setItem(item)
     *
     * @throws IllegalArgumentException
     *         If no valid anchor point is found.
     *       |    for I in 1..getNbAnchorPoints() :
     * 	     | 	      (!canHaveAsItemAt(item, i))
     */
    public void addToAnchorPoint(Equipment item) throws IllegalArgumentException {
        for (int i = 1; i <= getNbAnchorPoints(); i++) {
            AnchorPoint anchorpoint = getAnchorPointAt(i);

            if (anchorpoint.isEmpty() && canHaveAsItemAt(item, anchorpoint)) {
                anchorpoint.setItem(item);
                return;
            }
        }

        throw new IllegalArgumentException("No valid anchor point available.");
    }

    public abstract boolean canHaveAsItemAt(Equipment item, AnchorPoint anchorpoint);

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
     *               Protection + GetAnchorPoints
     *****************************************************************/
    /**
     * Variable for the
     */
    private int realProtection;

    /**
     * Returns the raw protection value of this entity.
     *
     * @return The entity’s raw protection value.
     *
     * @post result == realProtection
     */
    @Raw @Basic
    public int getRealProtection() { return realProtection; }

    /**
     * Sets the raw protection value of this entity.
     *
     * @param protection
     *        The new base protection value.
     *
     * @post getRealProtection() == protection
     */
    public void setRealProtection(int protection) {
        this.realProtection = protection;
    }


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



}






