import java.util.Random;
import be.kuleuven.cs.som.annotate.*;

/**
 * A class representing monsters in the game
 *
 * @invar	Each monster must have a properly spelled name.
 * 			| canHaveAsName(getName())
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
     * Initialize a new monster with the given name, maximum hitpoints, and strength.
     *
     * @param   name
     *          The name of the monster.
     * @param   maxHitPoints
     *          The base value of the monster.
     * @param   strength
     *          The strength of the monster.
     *
     * @effect  The monster is initialized as an entity with the given
     *          name, max hitpoints and protection.
     *          | super(name, maxHitPoints, 10)
     *
     * @effect  The anchor points are initialized.
     *          | initializeAnchorpoints();
     */
    public Monster(String name, int maxHitPoints, double strength) {
        super(name, maxHitPoints, 10);

        initializeAnchorPoints();
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

    /**********************************************************
     * Damage
     **********************************************************/

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
