import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import be.kuleuven.cs.som.annotate.*;

/**
 * A class representing monsters in the game
 *
 * @invar	  Each monster must have a properly spelled name.
 * 			    | canHaveAsName(getName())
 *
 * @invar   Each monster must have a valid damage.
 *          | isValidDamage(getDamage());
 *
 * @invar   Each monster must have a valid capacity.
 *          | isValidCapacity(getCapacity());
 *
 * @author  Jitse Vandenberghe
 * @author  Guillaume Vandemoortele
 *
 * @version 1.1
 */
public class Monster extends Entity {

    /**********************************************************
     * Constructors
     **********************************************************/

    /**
     * Initialize a new monster with the given name, maximum hitpoints, strength, and initial items.
     *
     * @param   name
     *          The name of the monster.
     * @param   initialItems
     *          A list of equipment items to randomly distribute over anchor points.
     * @param   maxHitPoints
     *          The base value of the monster.
     *
     * @effect  The monster is initialized as an entity with the given
     *          name, max hitpoints and protection.
     *          | super(name, maxHitPoints, 10)
     *
     * @effect  The anchor points are initialized.
     *          | initializeAnchorPoints()
     *
     * @effect  The given items are randomly distributed over the anchor points,
     *          and the capacity is set accordingly.
     *          | distributeInitialItems(initialItems)
     *
     * @effect  The new monster has the given damage.
     *          | setDamage(damage)
     *
     * @post    The new capacity is set to the given capacity.
     *          | new.getCapacity() = capacity
     *
     * @throws  IllegalArgumentException
     *          If the given damage is invalid.
     *          |!isValidDamage(damage)
     */
    public Monster(String name, int maxHitPoints, List<Equipment> initialItems) {
        super(name, maxHitPoints, 10);

        if (!isValidDamage(damage))
            throw new IllegalArgumentException("Damage cannot be negative, must be below the maximum damage and must be a multiple of 7.");
        if (!isValidCapacity(capacity))
            throw new IllegalArgumentException("Capacity cannot be negative.");

        distributeInitialItems(initialItems);
        setDamage(damage);
        this.capacity = getTotalWeight();
    }

    /**********************************************************
     * Name
     **********************************************************/

    /**
     * Check whether the given name is a legal name for a monster.
     *
     * @param  	name
     *			The name to be checked
     *
     * @return	True if the given string is effective, not
     * 			empty, consisting only of letters, spaces
     * 			and apostrophes, and the name start
     * 			with a capital letter; false otherwise.
     * 			| result ==
     * 			|	(name != null) && name.matches("[A-Za-z '’]+") && (Character.isUpperCase(name.charAt(0)))
     */
    @Override @Raw
    public boolean canHaveAsName(String name) {
        return (name != null && name.matches("[A-Za-z '’]+") && Character.isUpperCase(name.charAt(0)));
    }

    /**********************************************************
     * Anchors
     **********************************************************/

    /**
     * Initializes a random number of anchor points for this monster.
     *
     * This method generates a random number between 0 (inclusive) and Integer.MAX_VALUE (exclusive),
     * and adds that many anchor points to this monster. Each anchor point is initialized with null
     * as its name.
     *
     * @effect  Each newly created anchor point is added to this monster using addAnchorPoint.
     *          | for each i in 1..amount:
     *          |   addAnchorPoint(new AnchorPoint(null))
     *
     * @post    The total number of anchor points for this monster will increase by the generated amount.
     *          | getNbAnchorPoints() == amount
     */
    @Override
    public void initializeAnchorPoints() {
        Random random = new Random();
        int amount = random.nextInt(Integer.MAX_VALUE);

        for (int i = 1; i <= amount; i++) {
            addAnchorPoint(new AnchorPoint(null));
        }
    }

    /**
     * Distribute the given items across this monster's anchor points in order.
     *
     * The items are assigned to anchor points in the order they appear in the list.
     * If there are more items than anchor points, only the first items up to the number of anchor points
     * will be assigned. If there are fewer items than anchor points, some anchor points will remain empty.
     *
     * @param items
     *        The items to distribute.
     *
     * @post  Each item is assigned to one of this monster's anchor points.
     *          | for each i in 0 .. min(items.size(), getNbAnchorPoints()):
     *          |   getAnchorPointAt(i).setItem(items.get(i))
     *
     */
    public void distributeInitialItems(List<Equipment> items) {
        int maxItems = Math.min(items.size(), getNbAnchorPoints());

        for (int i = 0; i < maxItems; i++) {
            Equipment item = items.get(i);
            AnchorPoint anchorPoint = getAnchorPointAt(i);
            anchorPoint.setItem(item);
        }
    }

    /**
     * Determines whether this monster is allowed to carry the given item
     * by checking if there exists any anchor point where it can be legally placed
     *
     * @param   item
     *          The item to check
     *
     * @post    The result is true if there exists at least one anchor point such that:
     *          the anchor is empty and the capacity is not exceeded.
     *          | result == (exists ap in anchorPoints:
     *          |               ap.isEmpty() && getTotalWeight() + item.getWeight() <= getCapacity)
     *
     * @return  true if the item if there adding the item does not exceed the capacity and
     *          there is an empty anchorpoint, false otherwise.
     */
    @Override
    public boolean canHaveAsItem(Equipment item) {
        if (getTotalWeight() + item.getWeight() <= getCapacity()) {
            for (AnchorPoint ap : anchorPoints) {
                if (ap.isEmpty())
                    return true;
            }
            return false;
        }
        return false;
    }

    /**********************************************************
     * Capacity
     **********************************************************/


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

    /**********************************************************
     * Damage
     **********************************************************/

    /**
     * Variable referencing the maximum amount of damage a weapon can deal.
     */
    private static final int maximumDamage = 100;

    /**
     * Return the maximum damage a weapon can deal.
     */
    @Basic @Immutable
    public static int getMaximumDamage() {
        return maximumDamage;
    }

    /**
     * Variable registering the damage of this monster
     */
    private int damage;

    /**
     * Return the damage of this monster.
     */
    @Raw @Basic
    public int getDamage() {
        return damage;
    }

    /**
     * Set the damage of this monster to the given damage.
     *
     * @param   damage
     *          The new damage for this monster.
     *
     * @pre     The given damage must be legal.
     *          | isValidDamage(damage)
     * @post    The given damage is registered as the damage of this monster.
     *          | new.getDamage() == damage
     */
    @Raw
    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * Check whether the given damage is valid for this monster.
     *
     * @param   damage The damage to check.
     *
     * @return  True if and only if the given damage is positive, does not exceed the maximum allowed damage,
     *          and is a multiple of 7.
     *          | result == (damage > 0 && damage <= maximumDamage && damage % 7 == 0)
     */
    public boolean isValidDamage(int damage) {
        return damage > 0 && damage <= maximumDamage && damage % 7 == 0;
    }

    /**
     * Applies damage to this monster.
     * The effective damage is reduced by the monster's protection factor.
     *
     * @param damage
     *        The raw damage value to apply (must be non-negative).
     */
    @Override
    public void receiveDamage(int damage) {
        if (damage < 0) return;

        int effectiveDamage = Math.max(0, damage - getProtection());
        int newHitPoints = getHitPoints() - effectiveDamage;

        if (newHitPoints < 0) {
            newHitPoints = 0;
        }

        removeHitPoints(getHitPoints() - newHitPoints);
    }


    /**********************************************************
     * Hit
     **********************************************************/

    /**
     * Performs a single hit from this monster to the given opponent.
     *
     * @param opponent
     *        The entity that is the target of the hit.
     *
     * @effect  The monster will loot the defeated opponent when a fatal blow is dealt.
     *          | if opponent.getHitPoints() - damage <= 0 then loot(opponent)
     *
     * @post    If the hit is successful and fatal, opponent's hit points are zero.
     *          | if opponent.getHitPoints() - damage <= 0 then opponent.getHitPoints() == 0
     *
     * @post    If the hit is successful but not fatal, opponent's hit points decrease by the damage amount.
     *          | if opponent.getHitPoints() - damage > 0 then opponent.getHitPoints() == old opponent.getHitPoints() - getDamage()
     *
     * @post    If the hit misses, opponent's hit points remain unchanged.
     *          | if impact < opponent.getProtection() then opponent.getHitPoints() == old opponent.getHitPoints()
     */
    public void hit(Entity opponent) {
        Random random = new Random();

        int impact = random.nextInt(101); // between 0 (inclusive) and bound value '101' (exclusive)


        if (impact >= getHitPoints()) {
            impact = getHitPoints();
        }

        if (impact >= opponent.getHitPoints())


            if (impact >= opponent.getProtection()) {
                // land a succesful hit
                int damage = getDamage();
                int newHitPoints = opponent.getHitPoints() - damage;

                if (newHitPoints <= 0) {
                    // Final blow: opponent defeated
                    opponent.setHitPoints(0);

                    // Let monster take any possessions and destroy any weapons/armor left behind
                    loot(opponent);

                } else {
                    // Regular hit, adjust life points
                    opponent.setHitPoints(newHitPoints);
                }
            } else {
                // Miss the target: no effect
            }
    }

    /**
     * Attempt to loot items from a defeated opponent.
     *
     * This method is called after the monster deals a fatal blow.
     * The monster attempts to pick up as many weapons and armors as possible from the opponent,
     * given its available anchor points.
     *
     * Weapons and armors that are not looted are destroyed.
     * Backpacks and purses that are not looted remain behind on the ground.
     *
     * @param   opponent
     *          The defeated opponent from which to loot.
     *
     * @effect  The items carried by the opponent are collected and removed from the opponent.
     *          | for each item in opponent.getAnchorPointAt(i).getItem():
     *          |     item.setOwner(null)
     *          |     opponent.getAnchorPointAt(i).setItem(null)
     *
     * @effect  Attempts to add each item to the monster's inventory if there is a free anchor point.
     *          | if hasFreeAnchorPoint() then addToAnchorPoint(item) and item.setOwner(this)
     *
     * @effect  All non-looted weapons and armors are destroyed.
     *          | if item instanceof Weapon or Armor and not looted then item.destroy()
     *
     * @post    All looted items are removed from the opponent.
     *          | for each item looted:
     *          |     item.getOwner() == this
     *          |     opponent does not have the item anymore in any anchor point
     *
     * @post    All looted items are removed from the opponent.
     *          | for each item looted:
     *          |     item.getOwner() == this
     *          |     opponent does not have the item anymore in any anchor point
     *
     * @post    All non-looted weapons and armors from the opponent are destroyed.
     *          | for each non-looted weapon or armor item:
     *          |     item.isDestroyed() == true
     *
     * @post    The monster may have looted up to its available capacity.
     *          | getNbItemsCarried() <= getNbAnchorPoints()
     */
    public void loot(Entity opponent) {
        List<Equipment> potentialLoot = new ArrayList<>();

        // Step 1: Collect all items from opponent's anchor points
        for (int i = 1; i < opponent.getNbAnchorPoints(); i++) {
            AnchorPoint ap = opponent.getAnchorPointAt(i);
            Equipment item = ap.getItem();
            if (item != null) {
                potentialLoot.add(item); // Add item to list containing potential loot
                ap.setItem(null);        // Remove item from opponent
                item.setOwner(null);     // Remove ownership from opponent
            }
        }

        // Step 2: Loot equipment if there is space
        for (Equipment item : potentialLoot) {
            if (hasFreeAnchorPoint()) {
                addToAnchorPoint(item);
                item.setOwner(this);
            } else if ((item instanceof Weapon || item instanceof Armor)) {
                // Destroy non-looted weapons and armors only
                item.destroy();
            }
            // Backpacks and purses not looted remain on the ground, do nothing
        }
    }
}
