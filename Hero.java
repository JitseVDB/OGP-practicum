/**
 * A class representing heroic characters with a name, hitpoints, and strength.
 * Each hero has a name that must follow specific formatting rules, a maximum number
 * of hitpoints (fixed at 50), and a current amount of hitpoints that may change
 * depending on combat situations. Strength is defined as an additional characteristic
 *
 * @author	Guillaume Vandemoortele
 *
 * @version 1.0
 *
 * @invar	The name of a hero is valid according to custom-defined format rules.
 * 			| isValidName(getName())
 * @invar	The hitpoints of a hero are always between 0 and the maximum hitpoints.
 * 			| 0 <= getHitpoints() && getHitpoints() <= maxHitPoints
 * @invar   If the hero is not fighting, their hitpoints are always a prime number.
 *          | !isFighting() ==> isPrime(getHitpoints())
 * @invar   The intrinsic strength of a hero is always stored with two decimal places.
 *          | Math.round(intrinsicStrength * 100) / 100.0 == intrinsicStrength
 * @invar   The capacity of a hero is always between 0 and its maximum capacity.
 *          | 0 <= getCapacity() && getCapacity() <= getMaxCapacity()
 */
public class Hero {
    /**
     * Variable setting the name of the hero
     */
    private final String name;

    /**
     * Variable setting the maximum number of hitpoints a hero can take
     */
    private int maxHitPoints;

    /**
     * Variable setting the number of hitpoints that the hero can take
     */
    private int hitpoints;

    /**
     * The intrinsic strength of the hero.
     * Must be a positive decimal number, typically with 2 digits after the comma.
     */
    private Double intrinsicStrength;

    /**
     * Variable setting the attack strength of the hero
     */
    private int attackStrength;

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

    /**
     * Initializes a hero with a given name and maximum amount of hitpoints
     *
     * @param name
     *        the name of the hero
     * @param maxHitPoints
     *        the max amount of hitpoints a hero can take
     * @throws IllegalArgumentException if name is invalid or maxHitPoints < 0
     */
    public Hero(String name, int maxHitPoints, double intrinsicStrength) {
        if (!isValidName(name))
            throw new IllegalArgumentException("Invalid hero name: " + name);
        if (maxHitPoints < 0)
            throw new IllegalArgumentException("maxHitPoints must be a positive number");

        this.name = name;
        this.maxHitPoints = maxHitPoints;
        this.isFighting = false;
        this.hitpoints = maxHitPoints;
        // Check's if hitpoints is a prime number after initialization, because he is not fighting
        if (!isPrime(this.hitpoints)) {
            this.hitpoints = getClosestLowerPrime(this.hitpoints);
        }
        this.intrinsicStrength = Math.round(intrinsicStrength * 100) / 100.0;
        this.capacity = 0;
        this.maxCapacity = (int) (20 * this.intrinsicStrength);

    }

    /**
     * Checks whether the given name is valid according to specific rules.
     *
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
    private boolean isValidName(String name) {
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


    /**
     * Return the name of this hero
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return the number of hitpoints that this hero has
     */
    public int getHitpoints() {
        return this.hitpoints;
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
        this.hitpoints += amount;

        if (!isFighting && !isPrime(hitpoints)) {
            hitpoints = getClosestLowerPrime(hitpoints);
        }
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
        this.hitpoints -= amount;

        if (!isFighting && !isPrime(hitpoints)) {
            hitpoints = getClosestLowerPrime(hitpoints);
        }
    }

    /**
     * Sets the fighting status of the hero.
     * If the hero stops fighting, their hitpoints will be adjusted to the closest lower prime if necessary.
     *
     * @param status
     *        true if the hero is fighting, false otherwise.
     */
    public void setFighting(boolean status) {
        this.isFighting = status;
        if (!status && !isPrime(hitpoints)) {
            this.hitpoints = getClosestLowerPrime(hitpoints);
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
     * Multiply the strength by a given integer.
     *
     * @param factor A non-zero integer.
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
    protected Double getIntrinsicStrength() {
        return this.intrinsicStrength;
    }

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

    /**
     * Return the maximum of hitpoints of this hero
     */
    public Integer getMaxHitPoints() {
        return maxHitPoints;
    }
    
    /**
     * Return the maximum of hitpoints of this hero
     *
     * @param maxHitPoints
     *        amount of hitpoints will be set for the maximum amount of hitpoints of this hero
     */
    public void setMaxHitPoints(Integer maxHitPoints) {
        this.maxHitPoints = maxHitPoints;
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
