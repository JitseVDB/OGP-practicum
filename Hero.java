import java.util.Random;

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

    //#################################################################################
    //                                 VARIABLES
    //#################################################################################

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
     * Variable setting the protection of the hero
     */
    private int standardProtection;

    private int realProtection;

    //#################################################################################
    //                              CONSTRUCTOR
    //#################################################################################


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
        this.standardProtection = 10;

    }

    //#################################################################################
    //                                  NAME
    //#################################################################################


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

    //#################################################################################
    //                              HITPOINTS
    //#################################################################################

    /**
     * Return the number of hitpoints that this hero has
     */
    public int getHitpoints() {
        return this.hitpoints;
    }

    /**
     * Return the maximum of hitpoints of this hero
     */
    public Integer getMaxHitPoints() {
        return maxHitPoints;
    }

    /**
     * Adds the hero's hitpoints by a given amount.
     *
     * @param amount
     *        number of hitpoints to add to this hero
     * @pre The given amount must be a positive number.
     *      | amount > 0
     * @pre The resulting hitpoints after addition must not exceed the maximum hitpoints.
     *      | amount + getHitpoints() <= maxHitPoints
     * @post   If not fighting and result not prime, rounds down to closest lower prime:
     *      | !isFighting && !isPrime(hitpoints) => hitpoints == getClosestLowerPrime(old + amount)
     */
    public void addHitPoints(Integer amount) {
        this.hitpoints += amount;

        if (!isFighting && !isPrime(hitpoints)) {
            hitpoints = getClosestLowerPrime(hitpoints);
        }
    }

    /**
     * Decreases the hero's hitpoints by a given amount.
     *
     * @param amount
     *        number of hitpoints to add to this hero
     * @pre The given amount must be a positive number.
     *      | amount > 0
     * @pre The resulting hitpoints after subtraction must not fall below zero.
     *      | getHitpoints() - amount >= 0
     * @post   If not fighting and result not prime, rounds down to closest lower prime:
     *      | !isFighting && !isPrime(hitpoints) => hitpoints == getClosestLowerPrime(old + amount)
     */

    public void removeHitPoints(Integer amount) {
        this.hitpoints -= amount;

        if (!isFighting && !isPrime(hitpoints)) {
            hitpoints = getClosestLowerPrime(hitpoints);
        }
    }

    /**
     * Return the maximum of hitpoints of this hero
     *
     * @param maxHitPoints
     *        amount of hitpoints will be set for the maximum amount of hitpoints of this hero
     * @throws IllegalArgumentException if maxHitPoints < 0
     */
    public void setMaxHitPoints(Integer maxHitPoints) {
        this.maxHitPoints = maxHitPoints;
    }


    /**
     * Sets the fighting status of the hero.
     *
     * @param status
     *        true if the hero is fighting, false otherwise.
     * @effect if exiting combat and hitpoints not prime, rounds down to closest lower prime
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
     * @return true if the number is prime, false otherwise.
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
     * @return the greatest prime where 2 <= the prime < start
     */
    private int getClosestLowerPrime(int start) {
        for (int i = start - 1; i >= 2; i--) {
            if (isPrime(i)) return i;
        }
        return 2; // fallback
    }

    //#################################################################################
    //                              STRENGTH
    //#################################################################################

    /**
     * Multiply the strength by a given factor.
     *
     * @param factor
     *        A non-zero integer.
     * @throws IllegalArgumentException if factor is zero.
     * @post   intrinsicStrength updated and rounded to two decimals
     */
    public void multiplyStrength(int factor) {
        if (factor == 0)
            throw new IllegalArgumentException("intrinsicStrength cannot be multiplied by zero.");
        this.intrinsicStrength *= factor;
        // round intrinsicStrength to two decimal places
        this.intrinsicStrength = Math.round(this.intrinsicStrength * 100) / 100.0;

    }

    /**
     * Divide the strength by a given divisor.
     *
     * @param divisor
     *        A non-zero integer.
     * @throws IllegalArgumentException if divisor is zero.
     * @post   intrinsicStrength updated and rounded to two decimals
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
     * @return the sum of the intrinsicStrength and damage values of each weapon
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

    //#################################################################################
    //                              CAPACITY
    //#################################################################################

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


    //#################################################################################
    //                              PROTECTION
    //#################################################################################

    /**
     * Sets the protection to the given value if the value is strict positive.
     *
     * @param protection
     *        positive number as the value of the new standard protection of the hero.
     * @throws IllegalArgumentException if protection < 0
     */
    public void setProtection(int protection) {
        if (protection < 0) throw new IllegalArgumentException("protection cannot be negative.");
        this.standardProtection = protection;
    }

    /**
     * Returns the standard protection of this hero
     */
    public int getStandardProtection() {return this.standardProtection;}

    /**
     * Returns the real protection of this hero
     *
     * @return standardProtection + left-hand weapon armor value
     */
    public int getRealProtection() {
        this.realProtection = getStandardProtection() +  leftHandWeapon.getProtectionArmor();
        return this.realProtection;
    }

    //#################################################################################
    //                              HIT
    //#################################################################################

    /**
     * Attempt to hit the given monster.
     * Performs a random attack roll between 0 and 100. If the roll is greater than or equal to
     * the monster's real protection monster.getRealProtection(), this hero deals damage:
     * calculateDamage() is invoked, the resulting value is applied to the monster's hitpoints,
     * and if that attack reduces the monster to zero or below, the hero heals a random percentage of missing hitpoints.
     *
     * @param monster
     *        the target to attack
     * @throws NullPointerException
     *         if monster is null
     * @effect if monsters dies this hero heals
     *         | healAfterKill()
     */
    public void hit(Monster monster) {
        if (monster == null)
            throw new NullPointerException("Monster target cannot be null.");
        Random r= new Random();                                 // random number between 0-100
        int roll = r.nextInt(101);
        if (roll >= monster.getRealProtection()) {            // random number has to be higher than protection
                                                                // of the monster to be effective
            int damage = calculateDamage();
            int beforeHP = monster.getHitpoints();    // how much hitpoints the monster had before the hit
            monster.removeHitPoints(damage);          // subtract hitpoints from the monster
            if (damage > beforeHP) {                  // to check if the monster is dead
                healAfterKill();                      //              -> then he can heal now
            }
        }

    }

    /**
     * Calculate weapon damage to deal based on strength and weapon stats.
     *
     * @return non-negative integer damage
     */
    private int calculateDamage() {
        double base = this.intrinsicStrength + leftHandWeapon.getWeaponDamage() + rightHandWeapon.getWeaponDamage();
        int raw = (int) ((base - 10) / 2);
        return Math.max(raw, 0);
    }

    //#################################################################################
    //                              HEAL
    //#################################################################################

    /**
     * Restore a random proportion of missing hitpoints after a successful kill.
     * Computes missing hitpoints: maxHitPoints - hitpoints. If positive,
     * selects a random integer percentage in [0,100], calculates heal amount as
     * floor(missing * percentage / 100) and invokes addHitPoints(int)
     * to restore those hitpoints.
     *
     * @post If missing > 0 then new hitpoints == oldHitpoints + healAmount (capped and rounded per addHitPoints)
     * @effect uses addHitPoints(int) to perform the actual heal, applying its rounding logic when not fighting
     */
    private void healAfterKill() {
        int missing = this.maxHitPoints - this.hitpoints;   // left over (missing) hitpoints (100-70 = 30 hitpoints left)
        if (missing <= 0) return;                           // the leftover hitpoints can't be an negative number
        Random d= new Random();                             //
        int percentage = d.nextInt(101);             // percentage random number between 0-100
        int healAmount = (missing * percentage) / 100;      //
        addHitPoints(healAmount);                           // add healAmount to hitpoints
    }
}
