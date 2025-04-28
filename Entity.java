import be.kuleuven.cs.som.annotate.*;

/**
 * A class representing the entities (monsters and heroes) in the game.
 *
 * Every entity has a name, an amount of hitpoints, a maximum amount of hitpoints and a protection factor
 *
 * @invar  The name of the entity must be valid.
 *       | isValidName(getName())
 * @invar  The hitpoints must always be between 0 and the maximum hitpoints.
 *       | 0 <= getHitPoints() && getHitPoints() <= getMaxHitPoints()
 * @invar  The protection factor must be a strictly positive integer.
 *       | getProtection() > 0
 * @invar  The maximum number of hitpoints cannot be negative.
 *       | getMaxHitPoints() >= 0
 *
 * @author Ernest De Gres
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
     *         If the name is invalid, or if maxHitPoints or protection are invalid.
     */
    public Entity(String name, int maxHitPoints, int protection)
            throws IllegalArgumentException {
        if (!isValidName(name))
            throw new IllegalArgumentException("Invalid name for an entity.");
        if (maxHitPoints < 0)
            throw new IllegalArgumentException("Max hitpoints cannot be negative.");
        if (protection <= 0)
            throw new IllegalArgumentException("Protection must be strictly positive.");

        this.name = name;
        this.maxHitPoints = maxHitPoints;
        this.hitPoints = maxHitPoints;
        this.protection = protection;
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
     *
     */
    public abstract boolean isValidName(String name);

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
     * Adds the hero's hitpoints by a given amount.
     * If the hero is not fighting, the result will be adjusted to the closest lower prime if necessary.
     *
     * @pre The given amount must be a positive number.
     *      | amount > 0
     * @pre The resulting hitpoints after addition must not exceed the maximum hitpoints.
     *      | amount + getHitpoints() <= maxHitPoints
     */
    public void addHitPoints(Integer amount) {
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

    public void removeHitPoints(Integer amount) {
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
}
