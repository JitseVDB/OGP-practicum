import java.util.Random;
import java.util.List;

import be.kuleuven.cs.som.annotate.*;

/**
 * A class representing monsters in the game
 *
 * @invar	Each monster must have a properly spelled name.
 * 			| canHaveAsName(getName())
 *
 * @invar   Each weapon must have a valid damage.
 *          | isValidDamage(getDamage());
 *
 * @author  Jitse Vandenberghe
 * @author  Guillaume Vandemoortele
 *
 * @version 1.1
 */
public class Monster extends Entity {

    /**
     * Variable that indicates whether the hero is currently fighting. He is initialized as not fighting
     */
    private boolean isFighting;

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
     * @throws  IllegalArgumentException
     *          If the given damage is invalid.
     *          |!isValidDamge(damage)
     */
    public Monster(String name, int maxHitPoints, List<Equipment> initialItems) {
        super(name, maxHitPoints, 10);

        if (!isValidDamage(damage))
            throw new IllegalArgumentException("Damage cannot be negative, must be below the maximum damage and must be a multiple of 7.");

        initializeAnchorPoints();
        distributeInitialItems(initialItems);
        setDamage(damage);
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
     * @effect  The capacity is set to the total weight of all the items it carries.
     *          | setCapacity(totalWeight)
     */
    public void distributeInitialItems(List<Equipment> items) {
        int maxItems = Math.min(items.size(), getNbAnchorPoints());
        int totalWeight = 0;

        for (int i = 0; i < maxItems; i++) {
            Equipment item = items.get(i);
            AnchorPoint anchorPoint = getAnchorPointAt(i);
            anchorPoint.setItem(item);
            totalWeight += item.getWeight();
        }

        setCapacity(totalWeight);
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
     * Hitpoints
     **********************************************************/

    /**
     * Sets the fighting status.
     *
     * @param status
     *        true if the monster is fighting, false if he is not
     * @post   If the monster stops fighting and their hit points are not prime,
     *  *      they are adjusted to the closest lower prime number.
     */
    public void setFighting(boolean status) {
        this.isFighting = status;
        if (!status && !isPrime(getHitPoints())) {
            int p = getClosestLowerPrime(getHitPoints());
            super.removeHitPoints(getHitPoints() - p);
        }
    }

    /**
     * Determines if a given number is a prime number.
     *
     * @param number
     *        The number to check.
     * @return true if the number is prime; false otherwise.
     */
    private boolean isPrime(int number) {
        if (number < 2) return false;
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
    private int getClosestLowerPrime(int start) {
        for (int i = start - 1; i >= 2; i--) {
            if (isPrime(i)) return i;
        }
        return 2; // fallback
    }

    /**
     * Adds a number of hitpoints to this monster
     * @param amount
     */
    @Override
    public void addHitPoints(int amount) {
        super.addHitPoints(amount);
        if (!isFighting && !isPrime(getHitPoints())) {
            int p = getClosestLowerPrime(getHitPoints());
            super.removeHitPoints(getHitPoints() - p);
        }
    }

    /**
     * Removes a number of hitpoints to this monster
     * @param amount
     */
    @Override
    public void removeHitPoints(int amount) {
        super.removeHitPoints(amount);
        if (!isFighting && !isPrime(getHitPoints())) {
            int p = getClosestLowerPrime(getHitPoints());
            super.removeHitPoints(getHitPoints() - p);
        }
    }
}
