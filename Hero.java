
import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;



/**
 * A class representing heroic characters with a name, dynamic hit points, strength, and carrying capacity.
 *
 * Each hero has a name that must follow specific formatting rules, a maximum number of hit points
 * supplied at construction, and a current amount of hit points that may change during combat.
 * The hero's intrinsic strength is stored with two decimal places and determines the hero's carrying capacity.
 *
 * 
 * @author Jitse Vandenberghe
 * @author Ernest De Gres
 * @author Guillaume Vandemoortele
  *
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
     * Constructors
     **********************************************************/

    /**
     * Initializes a new hero with the given name, maximum hit points, and intrinsic strength.
     *
     * @param   name
     *          The name of the hero.
     *
     * @param   maxHitPoints
     *          The maximum number of hit points the hero can have.
     *
     * @param   strength
     *          The intrinsic strength of the hero. Must be strictly positive.
     *
     * @effect  The hero is initialized as an entity with the given
     *          name and maximum hit points.
     *          | super(name, maxHitPoints)
     *
     * @post    The intrinsic strength of the hero is set to the given strength,
     *          rounded to two decimal places.
     *          | new.getIntrinsicStrength() == roundToTwoDecimals(strength)
     *
     * @post    The protection of the hero is initialized to 10.
     *          | new.getProtection() == 10
     *
     * @post    The capacity of the hero is set to 20 times the intrinsic strength.
     *          | new.getCapacity() == (int)(20 * getIntrinsicStrength())
     *
     * @throws  IllegalArgumentException
     *          If the given strength is not strictly positive.
     *          | strength <= 0
     */
    public Hero(String name, int maxHitPoints, double strength) {
        super(name, maxHitPoints);
        if (strength <= 0)
            throw new IllegalArgumentException("Strength must be positive");

        this.intrinsicStrength = Math.round(strength * 100) / 100.0;
        this.protection = 10;
        this.capacity = (int)(20 * intrinsicStrength);

    }

    /**
     * Initializes a new hero with the given name, maximum hit-points, intrinsic strength
     * and an initial set of equipment items.
     *
     * @param name
     *        The hero’s name.
     *
     * @param maxHitPoints
     *        The maximum number of hit-points the hero can have.
     *
     * @param strength
     *        The intrinsic strength of the hero. Must be strictly positive.
     *
     * @param startItems
     *        Zero or more equipment items that will be assigned to this hero at creation.
     *
     * @effect  Calls the simpler constructor
     *          Hero(String name, int maxHitPoints, double strength) to set the
     *          basic fields (name, hit-points, intrinsic strength, protection and capacity).
     *          | this(name, maxHitPoints, strength)
     *
     * @effect  Each equipment item in startItems is made the property of this hero.
     *          Empty (null) elements are ignored.
     *          | for each item in startItems:
     *          |       if (item != null) then item.getOwner() == this
     */
    public Hero(String name, int maxHitPoints, double strength, Equipment... startItems) {
        this(name, maxHitPoints, strength); // delegate to base constructor

        for (Equipment item : startItems) {
            if (item != null) {
                item.setOwner(this);
            }
        }
    }

    /**********************************************************
     * Name
     **********************************************************/

    /**
     * Check whether the given name is a legal name for a hero.
     *
     * @param  	name
     *			The name to be checked
     *
     * @return	True if the given string is effective, not
     * 			empty, the name start with a capital letter,
     * 		    has at most 2 apostrophes, consisting only of
     * 		    letters, spaces apostrophes and colons, and
     * 		    each colon must be followed by a space ; false otherwise.
     *          | result ==
     *          |     (name != null)
     *          |     && !name.isEmpty()
     *          |     && Character.isUpperCase(name.charAt(0))
     *          |     && name.chars().filter(c -> c == '\'').count() <= 2
     *          |     && name.matches("[A-Za-z ':]+")
     *          |     && (for all i in [0..name.length()-1]:
     *          |         if (name.charAt(i) == ':') then (i+1 < name.length() && name.charAt(i+1) == ' '))
     */
    @Override
    public boolean canHaveAsName(String name) {
        if (name == null || name.isEmpty()) return false;

        // Must start with uppercase
        if (!Character.isUpperCase(name.charAt(0))) return false;

        // At most two apostrophes
        long apostropheCount = name.chars().filter(c -> c == '\'').count();
        if (apostropheCount > 2) return false;

        // Must match allowed pattern
        if (!name.matches("[A-Za-z' :]+")) return false;

        // Each ':' must be followed by a space
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) == ':') {
                if (i + 1 >= name.length() || name.charAt(i + 1) != ' ') {
                    return false;
                }
            }
        }

        return true;
    }


    /**********************************************************
     * Protection
     **********************************************************/

    /**
     * The protection factor of the entity.
     */
    private int protection;

    /**
     * Returns the protection factor of the entity.
     */
    @Raw @Basic
    public int getProtection() {
        return protection;
    }

    /**
     * Sets the raw protection value of this hero.
     *
     * @param   protection
     *          The new base protection value.
     *
     * @pre     The given protection must be legal.
     *          | isValidProtection()
     *
     * @post    The given protection is registered as the protection of this hero.
     *          | new.getProtection() == protection
     */
    public void setProtection(int protection) {
        this.protection = protection;
    }

    /**
     * Check whether the given protection factor is valid.
     *
     * @param   protection
     *          The protection factor to check.
     *
     * @return  True if and only if the protection factor is strictly positive.
     *          | result == (protection > 0)
     */
    public static boolean isValidProtection(int protection) {
        return protection > 0;
    }

    /**
     * Returns the total protection value of this hero during combat.
     *
     * The total protection is the sum of the hero’s base protection
     * and the protection provided by the equipped armor, if any.
     *
     * @return The hero’s base protection plus armor protection if armor is equipped;
     *         otherwise just the base protection.
     *         | if getArmor() == null
     *         |    then result == getProtection()
     *         |    else result == getProtection() + getArmor().getCurrentProtection()
     */
    @Basic
    public int getRealProtection() {
        int base = getProtection(); // = standard protection
        int armorBonus = 0;
        if (armor != null) {
            armorBonus = armor.getCurrentProtection();
        }
        return base + armorBonus;
    }


    /**********************************************************
     * Strength
     **********************************************************/

    /**
     * The intrinsic strength of the hero, stored as a decimal number
     * rounded to two decimal places.
     */
    private double intrinsicStrength;


     /**
     * Multiply the strength by a given integer.
     *
     * @param   factor
     *          A non-zero integer.
     *
     * @post    The intrinsic strength is updated to its previous value multiplied by the factor,
     *          rounded to two decimal places.
     *          | new.getIntrinsicStrength() == Math.round(old(getIntrinsicStrength()) * factor * 100) / 100.0
     *
     * @throws  IllegalArgumentException
     *          If the given factor is not 0.
     *          | factor == 0
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
     * @param   divisor
     *          A non-zero integer.
     *
     * @post    The intrinsic strength is updated to its previous value divided by the divisor,
     *          rounded to two decimal places.
     *          | new.getIntrinsicStrength() == Math.round(old(getIntrinsicStrength()) / divisor * 100) / 100.0
     *
     * @throws  IllegalArgumentException
     *          If the given factor is not 0.
     *          | factor == 0
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
     * the damage of the weapon in the left hand (if any),
     * and the damage of the weapon in the right hand (if any).
     *
     * @return  The total attack power as a double.
     *          | let weaponDamage = 0;
     *          | if (getLeftHandWeapon() != null) then weaponDamage += getLeftHandWeapon().getDamage()
     *          | if (getRightHandWeapon() != null) then weaponDamage += getRightHandWeapon().getDamage()
     *          | result == getIntrinsicStrength() + weaponDamage
     */
    @Basic
    public double getAttackPower() {
        int weaponDamage = 0;

        if (leftHandWeapon != null) weaponDamage += leftHandWeapon.getDamage();
        if (rightHandWeapon != null) weaponDamage += rightHandWeapon.getDamage();

        return intrinsicStrength + weaponDamage;
    }


    /**
     * Return the intrinsic strength of this hero
     */
    @Basic
    protected double getIntrinsicStrength() {
        return this.intrinsicStrength;
    }

    /**********************************************************
     * Armor
     **********************************************************/

    /**
     * Variable for the armor equipped by the hero
     */
    private Armor armor = null;

    /**
     * Returns the armor currently equipped by this hero.
     */
    @Basic
    public Armor getArmor() {
        return armor;
    }

    /**
     * Returns the number of armor items this hero is currently carrying.
     *
     * @return  The number of equipment items currently carried by the hero
     *          that are instances of Armor.
     *          | result == the number of anchor points where getItem() instanceof Armor
     */
    @Basic
    public int getNbArmorsCarried() {
        int count = 0;
        for (AnchorPoint ap : anchorPoints) {
            Equipment item = ap.getItem();
            if (item instanceof Armor) {
                count++;
            }
        }
        return count;
    }

    /**
     * Set the equipped armor to the given armor.
     *
     * @param   armor
     *          The armor to equip
     *
     * @post    The equipped armor is set to the given armor.
     *          | new.getArmor() = armor
     *
     * @note    This method does not handle the logic of equipping the armor itself;  
     *          it only updates the reference to the currently worn armor.
     */
    public void equipArmor(Armor armor) {
        this.armor = armor;
    }


    /**********************************************************
     * Hit
     **********************************************************/

    /**
     * Attacks the given monster. If the random attack roll exceeds or equals
     * the monster's current protection value, damage is dealt.
     * 
     * If the monster is defeated (i.e. its hit points drop to 0 or below),
     * the hero is healed and any treasure the monster was carrying is collected.
     *
     * @param    monster
     *           The monster to be attacked.
     *
     * @effect    If the random attack roll exceeds or equals the monster's
     *            current protection value, damage is dealt.
     *            | if (roll >= monster.getCurrentProtection() then monster.removeHitPoints(calaculateDamage)
     *
     * @effect    If the damage exceeds the hitpoints of the monster the hero is healed.
     *            | if (damage >= old.monster.getHitPoints) then healAfterKill()
     *
     * @effect    If the  damage exceeds the hitpoints of the monster the hero collects the treasure from the monster.
     *            | if (damage >= old.monster.getHitPoints) then collectTreasureFrom(monster)
     *
     * @throws   NullPointerException
     *           If the given monster is null.
     *           | monster == null
     */
    public void hit(Monster monster) {
        if (monster == null) {
            throw new NullPointerException("Monster target cannot be null.");
        }

        Random r = new Random();
        int roll = r.nextInt(101); // random getal tussen 0 en 100

        if (roll >= monster.getCurrentProtection()) {
            int damage = calculateDamage();
            int beforeHP = monster.getHitPoints();

            monster.removeHitPoints(damage);

            if (damage >= beforeHP) {
                healAfterKill();
                collectTreasureFrom(monster);
            }
        }
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
     *                   Collect Treasures
     **********************************************************/

    /**
     *
     * aanvullen
     *
     */
    public void collectTreasureFrom(Monster monster) {
        if (monster == null) return;

        Map<String, Equipment> loot = monster.getAnchors();

        for (Equipment item : loot.values()) {
            if (item != null) {
                try {
                    item.setOwner(this); // stel eigenaar in en checkt ook of dit mag/kan
                } catch (IllegalArgumentException e) {
                    // Dit item past niet in de hero, probeer in backpack te steken
                    for (Equipment heroItem : getAllItems()) {
                        if (heroItem instanceof Backpack) {
                            try {
                                item.setBackpack((Backpack) heroItem); // steek item in backpack als kan/mag
                            } catch (IllegalArgumentException a) {
                                // Dit item past niet in de hero, probeer de volgende
                            }
                        }
                    }
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
     *       | this.leftHandWeapon = weapon
     */
    public void equipLeftHand(Weapon weapon) {
        this.leftHandWeapon = weapon;
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
     *     | this.rightHandWeapon = weapon
     */
    public void equipRightHand(Weapon weapon) {
        this.rightHandWeapon = weapon;
    }

    /**
     * Returns the weapon currently equipped in the hero's left hand.
     */
    public Weapon getLeftHandWeapon() {
        return this.leftHandWeapon;
    }

    /**
     * Returns the weapon currently equipped in the hero's right hand.
     */
    public Weapon getRightHandWeapon() {
        return this.rightHandWeapon;
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
     * Add the given item to the first anchorpoint of this entity that accepts it.
     *
     * @param item
     *        The equipment to add.
     *
     * @effect The item is added to the first valid anchor point.
     *       | anchorpoint.setItem(item)
     *
     * @effect if item is armor and is added to body, equip armor.
     *         | if (item == armor)
     *         |      getArmor() = item
     *
     * @effect if item is weapon and is added to righthand , equip weapon to righthand.
     *         | if (item == weapon && anchorpoint == righthand )
     *         |      getRightHandWeapon() = item
     *
     * @effect if item is weapon and is added to lefthand , equip weapon to lefthand.
     *         | if (item == weapon && anchorpoint == lefthand )
     *         |      getLeftHandWeapon() = item
     *
     *
     */
    @Override
    public void addToAnchorPoint(Equipment item) {
        for (int i = 1; i <= getNbAnchorPoints(); i++) {
            AnchorPoint anchorpoint = getAnchorPointAt(i);

            if (anchorpoint.isEmpty()) {
                if (canHaveAsItemAt(item, anchorpoint)) {
                    if (anchorpoint.getName().equals("body") && item instanceof Armor) {
                        equipArmor((Armor) item);
                    }
                    if (anchorpoint.getName().equals("leftHand") && item instanceof Weapon) {
                        equipLeftHand((Weapon) item);
                    }
                    if (anchorpoint.getName().equals("rightHand") && item instanceof Weapon) {
                        equipRightHand((Weapon) item);
                    }

                    anchorpoint.setItem(item);
                    return;
                }
            }
        }
    }

    /**********************************************************
     *                      Items
     **********************************************************/

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
    @Override
    public boolean canHaveAsItemAt(Equipment item, AnchorPoint anchorpoint) {
        if (item == null || anchorpoint == null || anchorpoint.getName() == null) {
            return false;
        }

        String name = anchorpoint.getName();

        if (name.equals("body")) {
            return item instanceof Armor;
        } else if (name.equals("belt")) {
            return item instanceof Purse;
        }
        return !(item instanceof Purse);
    }

    /**
     * Add the given item to the anchor points registered to this entity.
     *
     * @param   item
     *          The equipment to be added.
     *
     * @effect  The equipment is added to available anchor point.
     *        | addToAnchorPoint(item)
     *
     *
     * @throws  IllegalArgumentException
     *          The item is already stored in this entity.
     *        | hasAsItem(item)
     *
     * @throws  IllegalArgumentException
     *          Hero can carry at most 2 armors
     *          | item == armor && getNbArmorsCarried() >= 2
     *
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
    @Override
    protected void addAsItem(Equipment item) throws IllegalArgumentException {
        if (item instanceof Armor && getNbArmorsCarried() >= 2)
            throw new IllegalArgumentException("Hero can carry at most 2 armors.");
        super.addAsItem(item);
    }

    /**
     * Remove the given item from this entity.
     *
     * @param item
     *        The equipment to remove.
     *
     * @effect The item is removed from the anchor point it was registered at.
     *         | anchorpoint.setItem(null)
     *
     * @effect if item is armor and is removed from body, unequip armor.
     *         | if (item == armor && anchorpoint == body)
     *         |      getArmor() = null
     *
     * @effect if item is weapon and is removed from lefthand , unequip weapon.
     *         | if (item == weapon && anchorpoint == lefthand )
     *         |      getLeftHandWeapon() = null
     *
     * @effect if item is weapon and is removed from righthand , unequip weapon.
     *         | if (item == weapon && anchorpoint == righthand )
     *         |      getRightHandWeapon() = null
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
    @Override
    protected void removeAsItem(@Raw Equipment item) throws IllegalArgumentException, IllegalStateException {
        if (!hasAsItem(item))
            throw new IllegalArgumentException("This entity does not have the item.");

        if (item != null && item.getOwner() == this)
            throw new IllegalStateException("Item still references this entity as its owner.");

        for (int i = 1; i <= getNbAnchorPoints(); i++) {
            AnchorPoint anchorpoint = getAnchorPointAt(i);
            if (anchorpoint.getItem() == item) {
                // Als dit armor was op het body-anker: reset armor-referentie
                if (anchorpoint.getName().equals("body") && item instanceof Armor) {
                    equipArmor(null);
                }
                if (anchorpoint.getName().equals("leftHand") && item instanceof Weapon) {
                    equipLeftHand(null);
                }
                if (anchorpoint.getName().equals("rightHand") && item instanceof Weapon) {
                    equipRightHand(null);
                }

                anchorpoint.setItem(null);
                return;

            }

        }

    }
}
