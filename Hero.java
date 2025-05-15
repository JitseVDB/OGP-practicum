import java.util.Random;
import java.util.List;
import java.util.ArrayList;


/**
 * A class representing heroic characters with a name, dynamic hit points, strength, and carrying capacity.
 *
 * Each hero has a name that must follow specific formatting rules, a maximum number of hit points
 * supplied at construction, and a current amount of hit points that may change during combat.
 * The hero's intrinsic strength is stored with two decimal places and determines the hero's carrying capacity.
 *
 * @author Guillaume Vandemoortele
 * @version 1.9
 *
 * @invar The hero's intrinsic strength is always stored with two decimal places.
 *        | Math.round(intrinsicStrength * 100) / 100.0 == intrinsicStrength
 * @invar The hero's capacity is always between 0 and its maximum capacity.
 *        | 0 <= getCapacity() <= getMaxCapacity()
 * @invar All items on anchor points are valid for their location.
 *        | for (AnchorPoint ap : anchorPoints)
 *        |     if (ap.getItem() != null) ==> canHaveAsItemAt(ap.getItem(), ap)
 * @invar The armor field matches the item on the body anchor, if any.
 *        | getArmor() == getAnchorPoint("body").getItem() || getArmor() == null
 *
 */
public class Hero extends Entity {

    /**********************************************************
     *                      Constructors
     **********************************************************/

    /**
     * Initializes a hero with the given name, maximum hit points, and intrinsic strength.
     * A new hero is initialized as not fighting. Both weapon slots are empty and no armor is equipped.
     * The hero's current hit points are set equal to the maximum hit points at creation.
     *
     * @param name
     *        the hero's name
     * @param maxHitPoints
     *        the maximum number of hit points
     * @param strength
     *        the hero's intrinsic strength (stored rounded to two decimal places)
     *
     * @pre strength > 0
     *
     * @post getIntrinsicStrength() == Math.round(strength * 100) / 100.0
     * @post getLeftHandWeapon() == null
     * @post getRightHandWeapon() == null
     * @post getArmor() == null
     */
    public Hero(String name, int maxHitPoints, double strength) {
        super(name, maxHitPoints, 10);
        this.intrinsicStrength = Math.round(strength * 100) / 100.0;
        this.capacity = 0;

    }

    /**
     * Constructs a new Hero with the given name, maximum hit points, intrinsic strength,
     * and a predefined set of equipment to be attached to specific anchor points.
     *
     * @param name
     *        the hero's name
     * @param maxHitPoints
     *        the maximum number of hit points
     * @param strength
     *        the hero's intrinsic strength (stored rounded to two decimal places)
     * @param startItems
     *        initialEquipment A list of items to assign to the hero’s anchors
     *
     * @throws IllegalArgumentException
     *         If any argument is invalid, or if items cannot be assigned due to weight or anchor conflicts
     *
     * @pre initialEquipment != null
     * @pre strength > 0
     *
     * @post getIntrinsicStrength() == Math.round(strength * 100) / 100.0
     * @post All equipment is validly assigned to appropriate anchors
     */
    public Hero(String name, int maxHitPoints, double strength, Equipment... startItems) {
        this(name, maxHitPoints, strength); // Roep de eenvoudige constructor aan

        for (Equipment item : startItems) {
            item.setOwner(this);
        }
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
     *                      Strenght
     **********************************************************/

    /**
     * The intrinsic strength of the hero.
     * Must be a positive decimal number, typically with 2 digits after the comma.
     */
    private double intrinsicStrength;


    /**
     * Multiply the strength by a given integer.
     *
     * @param factor
     *        A non-zero integer.
     *
     * @pre factor != 0
     *
     * @post The intrinsic strength is updated to its previous value multiplied by the factor,
     *       rounded to two decimal places.
     *       | getIntrinsicStrength() == Math.round(old(getIntrinsicStrength()) * factor * 100) / 100.0
     *
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
     * @param divisor
     *        A non-zero integer.
     *
     * @pre divisor != 0
     *
     * @post The intrinsic strength is updated to its previous value divided by the divisor,
     *       rounded to two decimal places.
     *     | getIntrinsicStrength() == Math.round(old(getIntrinsicStrength()) / divisor * 100) / 100.0
     *
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
     * Calculates the current attack power of this hero.
     * The attack power is the sum of the hero’s intrinsic strength,
     * ,the damage of the weapon in the left hand (if any)
     * ,the damage of the weapon in the right hand (if any)
     *
     * @return The total attack power as a double.
     *
     * @post The result is equal to the hero's intrinsic strength plus the damage of both equipped weapons.
     *     | result == intrinsicStrength
     *     |          + (leftHandWeapon.getDamage())
     *     |          + (rightHandWeapon.getDamage())
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
     *                      Capacity
     **********************************************************/

    /**
     * Variable setting the capacity of the hero
     */
    private int capacity;

    /**
     * Return the capacity of this hero
     */
    public int getCapacity() {
        return this.capacity;
    }

    /**
     * Return the maximum capacity of this hero
     */
    public int getMaxCapacity() {return (int)(20 * intrinsicStrength);}


    /**
     * Adds weight to the capacity
     *
     * @param amount
     */
    public void addCapacity(int amount) {
        if(0 < amount && (amount+capacity) <= getMaxCapacity()) {
            this.capacity += amount;
        }
    }

    /**
     * Removes weight from the capacity
     *
     * @param amount
     */
    public void removeCapacity(int amount) {
        if(0 < amount && (capacity-amount) > 0) {
            this.capacity -= amount;
        }
    }



    /**********************************************************
     *                      Armor
     **********************************************************/

    /**
     * Variable for the armor equipped by the hero
     */
    private Armor armor = null;

    /**
     * Returns the armor currently equipped by this hero.
     */
    public Armor getArmor() {
        return armor;
    }

    /**********************************************************
     *                      Hit
     **********************************************************/

    /**
     * Attempt to hit the given monster.
     * A random number between 0 and 100 is rolled. If the result is greater than or
     * equal to the monster's real protection, the hit succeeds and damage is applied.
     * The damage is calculated based on the hero’s intrinsic strength and equipped weapons.
     * If the damage kills the monster, the hero heals a random percentage of their missing
     * hit points using healAfterKill().
     *
     * @param monster
     *        The monster to attack
     * @throws NullPointerException
     *         if monster is null
     *
     * @pre monster != null
     *
     * @effect If the hit is successful and fatal, the hero heals.
     *         | healAfterKill()
     * @effect The monster's hit points are reduced.
     * @effect The hero's fighting state is toggled on and off around the attack.
     *
     */
    public void hit(Monster monster) {
        if (monster == null) {
            throw new NullPointerException("Monster target cannot be null.");
        }

        this.setFighting(true);

        Random r = new Random();
        int roll = r.nextInt(101); // random getal tussen 0 en 100

        if (roll >= monster.getRealProtection()) {
            int damage = calculateDamage();
            int beforeHP = monster.getHitPoints();

            monster.removeHitPoints(damage);

            if (damage >= beforeHP) {
                healAfterKill();
            }
        }

        this.setFighting(false);
    }

    /**
     * Calculates the damage dealt by this hero when a hit is successful.
     * The total power is the hero’s intrinsic strength + the damage values of any weapons held in the left and right hands
     * From this total, 10 is subtracted and the result is divided by 2.
     * Any negative result is rounded up to zero. All decimals are truncated.
     *
     * @return The integer damage value to apply.
     *
     * @post The result is equal to (totalPower - 10) / 2)
     *       where totalPower = intrinsicStrength + damage from left and right hand weapons.
     */
    private int calculateDamage() {
        int leftDamage = 0;
        int rightDamage = 0;

        if (leftHandWeapon != null) {
            leftDamage = leftHandWeapon.getDamage();
        }

        if (rightHandWeapon != null) {
            rightDamage = rightHandWeapon.getDamage();
        }

        double total = this.intrinsicStrength + leftDamage + rightDamage;
        int damage = (int) ((total - 10) / 2);

        if (damage < 0) {
            return 0;
        } else {
            return damage;
        }
    }

    /**********************************************************
     *                      Heal
     **********************************************************/

    /**
     * Heals this hero after killing a monster.
     * A random integer percentage between 0 and 100 is generated.
     * The hero recovers that percentage of the missing hit points, rounded down.
     *
     *
     * @post If the hero had missing hit points, they are increased by a random
     *       percentage of that missing amount, via addHitPoints(int).
     * @effect Uses addHitPoints(int) to apply the healing.
     */
    private void healAfterKill() {
        int missing = getMaxHitPoints() - getHitPoints();     // left over (missing) hitpoints (100-70 = 30 hitpoints left)
        if (missing <= 0) return;                                   // the leftover hitpoints can't be an negative number
        Random d= new Random();                                     //
        int percentage = d.nextInt(101);                     // percentage random number between 0-100
        int healAmount = (missing * percentage) / 100;      //
        addHitPoints(healAmount);                           // add healAmount to hitpoints
    }

    /**********************************************************
     *                      Protection
     **********************************************************/

    /**
     * Calculates the total protection value of this hero during combat.
     * The total protection is the hero’s base protection value + the protection provided by the equipped armor (if armor != null)
     *
     * @return The total protection value of this hero.
     *
     * @post The result is equal to getProtection() + armor.getCurrentProtection()
     */
    @Override
    public int getRealProtection() {
        int base = getProtection(); // = standaardbescherming (bv. 10)
        int armorBonus = 0;
        if (armor != null) {
            armorBonus = armor.getCurrentProtection(); // bv. 20
        }
        return base + armorBonus;
    }

    /**********************************************************
     *                   Collect Treasures
     **********************************************************/

    /**
     * Attempts to collect equipment items from the given monster after it has been defeated
     *
     * @param monster
     *        The monster whose belongings should be collected
     *
     * @post If monster == null, nothing happens.
     * @post For each eligible item in monster.getAllItems():
     *       if there is enough capacity and a valid empty anchor point,
     *       the item is transferred and linked to this hero.
     */
    public void collectTreasureFrom(Monster monster) {
        if (monster == null) return;

        List<Equipment> loot = monster.getAllItems();

        for (Equipment item : loot) {
            // Als we het item niet kunnen dragen, slaan we het over
            if (!canCarry(item)) continue;

            // Probeer item toe te voegen aan een geldige ankerpunt
            for (AnchorPoint ap : anchorPoints) {
                if (ap.isEmpty() && canHaveAsItemAt(item, ap)) {
                    ap.setItem(item);
                    item.setOwner(this);        // Bidirectionele link
                    this.capacity += item.getWeight();
                    break; // naar volgend item
                }
            }
        }
    }

    /**********************************************************
     *                   Weapon Equipment
     **********************************************************/

    /**
     * Stands for which weapon is carried in the left and right hand.
     * Standard the hero carries no weapons
     */
    private Weapon leftHandWeapon = null;
    private Weapon rightHandWeapon = null;


    /**
     * Equips the given weapon in the hero’s left hand.
     * The weapon is assigned to the leftHandWeapon field and linked to the "leftHand" anchor point.
     * The weapon’s weight is added to the hero’s current capacity.
     *
     * @param weapon
     *        The weapon to equip
     *
     * @post The weapon is assigned to the hero’s left hand.
     *       If the weapon is not null, it is placed on the "leftHand" anchor and its weight is added to capacity.
     *     | getLeftHandWeapon() == weapon
     *     | if (weapon != null)
     *     |     then getAnchorPoint("leftHand").getItem() == weapon
     *     |          and getCapacity() == old(getCapacity()) + weapon.getWeight()
     */
    public void equipLeftHand(Weapon weapon) {
        int leftHandweight = 0;
        this.leftHandWeapon = weapon;
        leftHandweight = weapon.getWeight();
        AnchorPoint left = getAnchorPoint("leftHand");
        if (left != null) {
            left.setItem(weapon);
        }

        addCapacity(leftHandweight);
    }

    /**
     * Equips the given weapon in the hero’s right hand.
     * The weapon is stored in the rightHandWeapon field and linked to the "rightHand" anchor point.
     * The weapon’s weight is added to the hero’s current capacity.
     *
     * @param weapon
     *        The weapon to equip
     *
     * @post The weapon is assigned to the hero’s right hand.
     *       If the weapon is not null, it is placed on the "rightHand" anchor and its weight is added to capacity.
     *     | getRightHandWeapon() == weapon
     *     | if (weapon != null)
     *     |     then getAnchorPoint("rightHand").getItem() == weapon
     *     |          and getCapacity() == old(getCapacity()) + weapon.getWeight()
     */
    public void equipRightHand(Weapon weapon) {
        this.rightHandWeapon = weapon;
        AnchorPoint right = getAnchorPoint("rightHand");
        if (right != null) {
            right.setItem(weapon);
        }

        addCapacity(this.rightHandWeapon.getWeight());
    }

    /**********************************************************
     *                      AnchorPoints
     **********************************************************/

    /**
     *  Initializes the default anchor points for this hero.
     *
     *  @post The hero has exactly five anchor points with the names:
     *       "leftHand", "rightHand", "back", "body", and "belt".
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
     * Tries to assign the given item to one of the hero’s available anchor points.
     *
     * @param item
     *        The item to add.
     *
     * @throws IllegalArgumentException If no valid anchor point is available for the item
     *
     * @pre item != null
     *
     * @post If the method completes, the item is placed at a valid anchor point and
     *       its owner remains the same.
     * @post If item is armor and placed on "body", getArmor() == item
     */
    @Override
    public void addToAnchorPoint(Equipment item) {
        for (AnchorPoint ap : anchorPoints) {
            if (ap.isEmpty() && canHaveAsItemAt(item, ap)) {
                ap.setItem(item);

                // Als armor op "body", update armor-ref
                if (ap.getName().equals("body") && item instanceof Armor) {
                    this.armor = (Armor) item;
                }

                return;
            }
        }

        throw new IllegalArgumentException("No valid anchor point found.");
    }

    /**********************************************************
     *                      Items
     **********************************************************/

    /**
     * Determines whether this hero is allowed to carry the given item
     * by checking if there exists any anchor point where it can be legally placed
     *
     * @param item
     *        The item to check
     *
     * @pre item != null
     *
     * @post The result is true if there exists at least one anchor point such that:
     *       the anchor is empty and canHaveAsItemAt(item, anchor) returns true
     *       | result == (exists ap in anchorPoints:
     *       |               ap.isEmpty() && canHaveAsItemAt(item, ap))
     *
     * @return true if the item is allowed at any empty and valid anchor point, false otherwise.
     */
    @Override
    public boolean canHaveAsItem(Equipment item) {
        for (AnchorPoint ap : anchorPoints) {
            if (ap.isEmpty() && canHaveAsItemAt(item, ap))
                return true;
        }
        return false;
    }


    /**
     * Determines whether the given item can legally be placed at the specified anchor point.
     *
     * The legality is based on the type of the item and the name of the anchor point:
     * - "leftHand" or "rightHand": only weapons are allowed
     * - "body": only armor is allowed
     * - "belt": only purses are allowed
     * - "back": any item is allowed
     * - unknown or unsupported anchors: not allowed
     *
     * @param item
     *        The item to check.
     * @param anchorpoint
     *        The anchor point to check against
     * @post Returns false if item == null, or anchorpoint == null, or anchorpoint.getName() == null.
     * @post For a known anchor name, returns true if the item type matches the allowed type.
     * @post For unknown anchor names, returns false.
     *
     * @return true if the item is allowed at the given anchor point, false otherwise.
     */
    public boolean canHaveAsItemAt(Equipment item, AnchorPoint anchorpoint) {
        if (item == null || anchorpoint == null || anchorpoint.getName() == null) {
            return false;
        }

        String name = anchorpoint.getName();

        if (name.equals("leftHand") || name.equals("rightHand")) {
            return item instanceof Weapon;
        } else if (name.equals("back")) {
            return true; // alle types mogen op de rug
        } else if (name.equals("body")) {
            return item instanceof Armor;
        } else if (name.equals("belt")) {
            // check op de naam van de klasse zonder gebruik van instanceof als Purse nog niet bestaat
            return item.getClass().getSimpleName().equals("Purse");
        } else {
            // onbekend ankerpunt
            return false;
        }
    }


    /**
     * Checks whether this hero is able to carry the given equipment item
     * without exceeding their maximum capacity.
     * The check is based on the current carried weight and the item's weight.
     *
     * @param item
     *        The equipment item to check
     *
     * @pre item != null
     *
     * @post The result is true if the current capacity plus the item’s weight
     *       does not exceed the hero’s maximum capacity.
     *     | result == (getCapacity() + item.getWeight()) <= getMaxCapacity()
     */
    public boolean canCarry(Equipment item) {
        return (getCapacity() + item.getWeight()) <= getMaxCapacity();
    }

    /**
     * Adds the given item to this hero by attaching it to a valid anchor point.
     *
     * @param item
     *        The item to be added.
     *
     * @throws IllegalArgumentException
     *         If the item is already added, not allowed, or if it would exceed capacity.
     * @throws IllegalStateException
     *         If the item is not owned by this hero.
     *
     * @pre item != null
     * @pre !hasAsItem(item)
     * @pre canHaveAsItem(item)
     * @pre item.getOwner() == this
     * @pre canCarry(item)
     *
     * @post The item is placed at a valid anchor point and capacity is increased by its weight.
     *     | hasAsItem(item)
     *     | and exists ap in anchorPoints such that ap.getItem() == item
     *     | and getCapacity() == old(getCapacity()) + item.getWeight()
     *
     * @effect Calls addToAnchorPoint(item) to perform the attachment.
     */
    @Override
    protected void addAsItem(Equipment item) throws IllegalArgumentException, IllegalStateException {
        if (hasAsItem(item))
            throw new IllegalArgumentException("Item is already carried.");
        if (!canHaveAsItem(item))
            throw new IllegalArgumentException("Item not allowed.");
        if (item != null && item.getOwner() != this)
            throw new IllegalStateException("Item does not belong to this Hero.");

        // Gewicht controleren vóór toevoegen
        if (!canCarry(item))
            throw new IllegalArgumentException("To heavy.");

        try {
            addToAnchorPoint(item);
            // Pas capaciteit aan
            this.capacity += item.getWeight();
        } catch (IllegalArgumentException e) {
            assert false;
        }
    }

    /**
     * Removes the given item from this hero.
     *
     * @param item
     *        The item to remove.
     * @throws IllegalArgumentException
     *         If the item is not linked to this hero.
     *
     * @pre item != null
     * @pre hasAsItem(item)
     *
     * @post The item is no longer attached to any anchor point.
     *       The hero’s capacity is decreased by the item’s weight.
     *     | !hasAsItem(item)
     *     | and getCapacity() == old(getCapacity()) - item.getWeight()
     */
    @Override
    protected void removeAsItem(Equipment item) throws IllegalArgumentException {
        if (!hasAsItem(item))
            throw new IllegalArgumentException("Item not with this Hero.");

        for (AnchorPoint ap : anchorPoints) {
            if (ap.getItem() == item) {
                ap.setItem(null);
                this.capacity -= item.getWeight();

                // Als dit armor was op het body-anker: reset armor-referentie
                if (ap.getName().equals("body") && item instanceof Armor) {
                    this.armor = null;
                }

                return;
            }
        }

        throw new IllegalArgumentException("Item not found in an anchor point.");
    }

    /**********************************************************
     *                   Receiving Damage
     **********************************************************/


    /**
     * Player gets damaged and loses his hitpoints
     * if the given damage is greater than the hero's protection.
     * @param damage
     *        The amount of damage to apply.
     *
     * @post The hero's hit points are reduced by (damage - getRealProtection())
     *
     * @effect Calls removeHitPoints(int) with the adjusted damage value.
     */
    @Override
    public void receiveDamage(int damage) {
        int actual = damage - getProtection();
        if (actual < 0) actual = 0;
        super.removeHitPoints(actual);
    }
}


