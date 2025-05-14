/**
 * A class representing heroic characters with a name, dynamic hit points, strength, and carrying capacity.
 *
 * Each hero has a name that must follow specific formatting rules, a maximum number of hit points
 * supplied at construction, and a current amount of hit points that may change during combat.
 * The hero's intrinsic strength is stored with two decimal places and determines the hero's carrying capacity.
 *
 * @author Guillaume Vandemoortele
 * @version 1.6
 *
 * @invar The hero's name must be valid according to the custom-defined format rules.
 *        | canHaveAsName(getName())
 * @invar The hero's hit points are always between 0 and the maximum hit points.
 *        | 0 <= getHitPoints() <= getMaxHitPoints()
 * @invar If the hero is not fighting, their hit points are always a prime number.
 *        | !isFighting() ==> isPrime(getHitPoints())
 * @invar The hero's intrinsic strength is always stored with two decimal places.
 *        | Math.round(intrinsicStrength * 100) / 100.0 == intrinsicStrength
 * @invar The hero's capacity is always between 0 and its maximum capacity.
 *        | 0 <= getCapacity() <= getMaxCapacity()
 */
public class Hero extends Entity {

    /**
     * The intrinsic strength of the hero.
     * Must be a positive decimal number, typically with 2 digits after the comma.
     */
    private double intrinsicStrength;

    /**
     * Stands for which weapon is carried in the left and right hand.
     * Standard the hero carries no weapons
     * !!! Kan pas gebruikt worden wanneer de class Weapon gemaakt wordt !!!
     */
    private Weapon leftHandWeapon = null;
    private Weapon rightHandWeapon = null;


    /**
     * Variable that indicates whether the hero is currently fighting. He is initialized as not fighting
     */
    private boolean isFighting;

    /**
     * Variable setting the capacity of the hero
     */
    private int capacity;

    /**
     * Variable setting the maximum capacity of the hero
     */
    private int maxCapacity;

    /**********************************************************
     *                      Constructors
     **********************************************************/

    /**
     * Initializes a hero with the given name, maximum hit points, and intrinsic strength.
     *
     * @param name
     *     the hero's name (must satisfy canHaveAsName)
     * @param maxHitPoints
     *     the maximum number of hit points (must be >= 0)
     * @param strength
     *     the hero's intrinsic strength (must be > 0); stored rounded to two decimal places
     * @throws IllegalArgumentException
     *     if the name is invalid, maxHitPoints is negative, or strength is not positive
     *
     * @post  getName().equals(name)
     * @post  getMaxHitPoints() == maxHitPoints
     * @post  getIntrinsicStrength() == Math.round(strength * 100) / 100.0
     * @implNote If the initial hit points are not a prime number (hero not fighting at start),
     *           they are adjusted to the closest lower prime. Anchor points are then initialized.
     */
    public Hero(String name, int maxHitPoints, double strength) {
        super(name, maxHitPoints, 10);
        if (strength <= 0)
            throw new IllegalArgumentException("Strength must be positive");// standaard protection=10
        this.isFighting = false;
        this.intrinsicStrength = Math.round(strength * 100) / 100.0;
        this.capacity = 0;
        this.maxCapacity = (int)(20 * this.intrinsicStrength);

        // Prime-correctie bij start, omdat niet vechtend
        if (!isPrime(getHitPoints())) {
            int p = getClosestLowerPrime(getHitPoints());
            super.removeHitPoints(getHitPoints() - p);
        }
        // Anchor points initialiseren
        initializeAnchorPoints();
    }

    /**********************************************************
     *                          Name
     **********************************************************/

    /**
     * Checks whether the given name is valid according to these specific rules:
     *      non-null, non-empty, starts with an uppercase letter
     *      and contains only letters, spaces, colons (each followed by a space),
     *      and at maximum 2 apostrophes
     * @param	name
     * 			The name to be validated.
     *
     * @post	If the name is null, empty, or does not start with an uppercase letter,
     * 			the result is false.
     * 			If the name contains characters other than letters
     * 		   	or the name isn't part of the allowed characters
     * 			or more than two apostrophes, or if a colon is not followed by a space,
     * 			the result is false.
     * 			In all other cases, the result is true.
     * 			| if (name == null || name.isEmpty() || !Character.isUpperCase(name.charAt(0)))
     * 			|		then result == false
     * 			| else if (name contains invalid characters
     * 			|			or more than two apostrophes
     * 			|			or ':' not followed by ' ')
     * 			|		then result == false
     * 			| else result == true
     *
     * @effect	The method does not alter any state or have side effects. It just checks
     *          if the name is valid and gives back true or false.
     * 			| result == true <==> name is valid according to the defined format
     */
    @Override
    public boolean canHaveAsName(String name) {
        char[] allowedChars = {' ', ':', '\''};

        if (name == null || name.isEmpty() || !Character.isUpperCase(name.charAt(0))) {
            return false;
        }

        int apostrophes = 0;

        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);

            if (Character.isLetter(c)) {
                continue;
            }

            boolean allowed = false;
            for (char ac : allowedChars) {
                if (c == ac) {
                    allowed = true;
                    break;
                }
            }

            if (!allowed) {
                return false;
            }

            if (c == '\'') {
                apostrophes++;
                if (apostrophes > 2) {
                    return false;
                }
            }

            if (c == ':') {
                if (i + 1 >= name.length() || name.charAt(i + 1) != ' ') {
                    return false;
                }
            }
        }

        return true;
    }

    /**********************************************************
     *                      AnchorPoints
     **********************************************************/

    /**
     *  Initializes anchor points for the hero. For his left hand, right hand, back, body and belt
     */
    @Override
    public void initializeAnchorPoints() {
        addAnchorPoint(new AnchorPoint("leftHand"));
        addAnchorPoint(new AnchorPoint("rightHand"));
        addAnchorPoint(new AnchorPoint("back"));
        addAnchorPoint(new AnchorPoint("body"));
        addAnchorPoint(new AnchorPoint("belt"));
    }

    /**
     * Player gets damaged and loses his hitpoints
     * if the given damage is greater than the hero's protection.
     * @param damage
     *        The amount of damage to apply.
     *
     * @effect The hero’s hit points are reduced by this value.
     */
    @Override
    public void receiveDamage(int damage) {
        int actual = damage - getProtection();
        if (actual < 0) actual = 0;
        super.removeHitPoints(actual);
    }

    /**********************************************************
     *                      Hitpoints
     **********************************************************/

    /**
     * Sets the fighting status.
     *
     * @param status
     *        true if the hero is fighting, false if he is not
     * @post   If the hero stops fighting and their hit points are not prime,
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
     * Adds a number of hitpoints to this hero
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
     * Removes a number of hitpoints to this hero
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

    /**********************************************************
     *                      Strenght
     **********************************************************/

    /**
     * Multiply the strength by a given integer.
     *
     * @param factor
     *        A non-zero integer.
     * @throws IllegalArgumentException if factor is zero.
     */
    public void multiplyStrength(int factor) {
        if (factor == 0)
            throw new IllegalArgumentException("intrinsicStrength cannot be multiplied by zero.");
        this.intrinsicStrength *= factor;
        // round intrinsicStrength to two decimal places
        this.intrinsicStrength = Math.round(this.intrinsicStrength * 100) / 100.0;

    }

    /**
     * Divide the strength by a given integer.
     *
     * @param divisor A non-zero integer.
     * @throws IllegalArgumentException if divisor is zero.
     */
    public void divideStrength(int divisor) {
        if (divisor == 0)
            throw new IllegalArgumentException("Cannot divide by zero.");
        this.intrinsicStrength /= divisor;
        // round intrinsicStrength to two decimal places
        this.intrinsicStrength = Math.round(this.intrinsicStrength * 100) / 100.0;

    }

    /**
     * Calculates the attack power of the hero.
     * It includes the hero's current strength and the damage from the weapons held in both hands.
     *
     * @return The total attack power.
     */
    public double getAttackPower() {
        int weaponDamage = 0;

        if (leftHandWeapon != null) weaponDamage += leftHandWeapon.getDamage();
        if (rightHandWeapon != null) weaponDamage += rightHandWeapon.getDamage();

        return intrinsicStrength + weaponDamage;
    }

    /**
     * Return the intrinsic strength of this hero
     */
    protected double getIntrinsicStrength() {
        return this.intrinsicStrength;
    }

    /**********************************************************
     *                   Weapon Equipment
     **********************************************************/

    /**
     * Equip a weapon in the left hand.
     *
     * @param weapon The weapon to equip (can be null to unequipped).
     */
    public void equipLeftHand(Weapon weapon) {
        this.leftHandWeapon = weapon;
    }

    /**
     * Equip a weapon in the right hand.
     *
     * @param weapon The weapon to equip (can be null to unequipped).
     */
    public void equipRightHand(Weapon weapon) {
        this.rightHandWeapon = weapon;
    }

    /**********************************************************
     *                      Capacity
     **********************************************************/

    /**
     * Return the capacity of this hero
     */
    public int getCapacity() {
        return this.capacity;
    }

    /**
     * Return the maximum capacity of this hero
     */
    public int getMaxCapacity() {
        return (int)(20 * intrinsicStrength);
    }


}
    }

    /**********************************************************
     * Equipment
     **********************************************************/

    /**
     * Check whether this hero can have the given item as one of its items.
     *
     * @param item
     *        The item to be checked.
     *
     * @return False if the item is null or if the item is invalid for this hero.
     *       | if (item == null) then result == false
     *       | else if (item instanceof Armor) then result == (getNbArmors() < 2)
     *       | else if (item instanceof Purse) then result == (hasPurse())
     *       | else result == true
     *
     * @note	This checker does not verify the consistency of the bidirectional relationship,
     * 			nor the ordering in this directory.
     * @note	This checker can be used to verify existing items in this directory, as well as to verify whether
     * 			a new item can be added to this directory.
     */
    @Raw @Override
    public boolean canHaveAsItem(@Raw Equipment item) {
        if (item == null)
            return false;

        // Maximum 2 harnassen
        if (item instanceof Armor && getNbArmors() >= 2)
            return false;

        // Maximum 1 geldbeurs
        if (item instanceof Purse && hasPurse())
            return false;

        return true;
    }

    /**
     * Return the number of armors held by this hero.
     */
    public int getNbArmors() {
        int amount = 0;
        for (int i = 1; i <= getNbAnchorPoints(); i++) {
            Equipment item = getAnchorPointAt(i).getItem();
            if (item instanceof Armor)
                amount++;
        }
        return amount;
    }

    /**
     * Check if this hero has a purse.
     *
     * @return True if hero holds a purse.
     *       | result ==
     *       |   for some I in 1..getNbAnchorPoints() :
     *       |       getAnchorPointAt(i).getItem() instanceof Purse
     */
    public boolean hasPurse() {
        for (int i = 1; i <= getNbAnchorPoints(); i++) {
            Equipment item = getAnchorPointAt(i).getItem();
            if (item instanceof Purse)
                return true;
        }
        return false;
    }

    /**
     * Check whether the given item can be added in the given anchor point.
     *
     * @param item
     *        The equipment to add.
     * @param anchorpoint
     *        The anchor point to check.
     *
     * @return True if and only if the item is allowed on the given anchor point.
     *       | if point.getName().equals("belt") then result == (item instanceof Purse)
     *       | else result == true
     *
     * @note We have already checked if item != null, anchorpoint.isEmpty(), canHaveAsItem() and !hasAsItem().
     */
    @Override
    public boolean canHaveAsItemAt(Equipment item, AnchorPoint anchorpoint) {
        String name = anchorpoint.getName();

        if (name.equals("belt"))
            return item instanceof Purse;

        return true;
    }

}
