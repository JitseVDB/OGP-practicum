import java.util.Random;

/**
 * A class representing monsters in the game
 *
 */
public class Monster extends Entity {

    /**
     * Variable that indicates whether the hero is currently fighting. He is initialized as not fighting
     */
    private boolean isFighting;

    /**********************************************************
     *                     Constructor
     **********************************************************/

    /**
     * Creates a new monster with the given name, maximum hit points and strength.
     * The monster's protection is fixed at 10 due to its natural armor.
     *
     * @param name
     *        The name of the monster (must be valid).
     * @param maxHitPoints
     *        The maximum number of hit points (must be positive).
     * @param strength
     *        The intrinsic strength of the monster.
     */
    public Monster(String name, int maxHitPoints, double strength) {
        super(name, maxHitPoints, 10);

        initializeAnchorPoints();
    }

    /**********************************************************
     *                        Name
     **********************************************************/

    /**
     * Checks whether the given name is valid for a monster.
     * A valid name must: be non-null and non-empty, starts with an uppercase letter
     *                    and only contains letters, spaces, or apostrophes
     * @param name
     *        The name to validate.
     * @return true if the name is valid, false otherwise.
     */
    @Override
    public boolean canHaveAsName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        if (!Character.isUpperCase(name.charAt(0))) {
            return false;
        }

        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isLetter(c) && c != ' ' && c != '\'') {
                return false;
            }
        }

        return true;
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
     *                     Anchors
     **********************************************************/

    /**
     * Initializes a random number of anchor points between 1 and 5.
     * Anchor points are named "point1", "point2", etc.
     */
    @Override
    public void initializeAnchorPoints() {
        Random random = new Random();
        int amount = random.nextInt(5) + 1; // tussen 2 en 5 anchor points

        for (int i = 1; i <= amount; i++) {
            addAnchorPoint(new AnchorPoint("point" + i));
        }
    }

    /**********************************************************
     *                     Hitpoints
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
