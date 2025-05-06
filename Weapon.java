import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import be.kuleuven.cs.som.annotate.*;

/**
 * A class of weapons, involving damage.
 *
 * @invar   Each weapon must have a valid damage.
 *          | isValidDamage(getDamage());
 *
 * @note    Only the additional invariants that are not already defined in the
 *          superclass `Equipment` need to be specified.
 *
 * @author  Jitse Vandenberghe
 * @version 1.0
 */
public class Weapon extends Equipment {

    /**********************************************************
     * Constructors
     **********************************************************/

    /**
     * Initialize a new weapon with the given weight, base value, and damage.
     *
     * @param   weight
     *          The weight of the weapon.
     * @param   baseValue
     *          The base value of the weapon, in dukaten.
     * @param   damage
     *          The damage this weapon can inflict.
     *
     * @effect  The weapon is initialized as a piece of equipment.
     *          (weight, base value are set and an identification number is generated and assigned)
     *          | super(weight, baseValue)
     *
     * @post    The damage is set to the given damage
     *          | setDamage(damage);
     */
    public Weapon(int weight, int baseValue, int damage) {
        super(weight, baseValue);

        if (!isValidDamage(weight))
            throw new IllegalArgumentException("Damage cannot be negative, must be below the maximum damage and must be a multiple of 7.");

        setDamage(damage);
    }

    /**********************************************************
     * Identification
     **********************************************************/

    /**
     * Check whether the given identification number is a valid identification number for this weapon.
     *
     * @param   equipmentType
     *          The class type of the equipment for which the identification number is being validated.
     *
     * @param   identification
     *          The identification number to check.
     *
     * @return  True if the identification number is non-negative, divisible by 2, divisible by 3,
     *          and unique within the given equipment type; false otherwise.
     *          | result == super.isValidIdentification(equipmentType, identification)
     *          |        && (identification % 2 == 0)
     *          |        && (identification % 3 == 0)
     */
    @Override
    public boolean canHaveAsIdentification(Class<?> equipmentType, long identification) {
        return super.canHaveAsIdentification(equipmentType, identification)
                // The identification number must be divisible by 2 and 3.
                && identification % 2 == 0
                && identification % 3 == 0;
    }

    /**********************************************************
     * Damage - nominal programming
     **********************************************************/

    /**
     * Variable expressing the damage of this weapon
     */
    private int damage;

    /**
     * Variable expressing the maximum amount of damage a weapon can deal.
     */
    private static final int maximumDamage = 100;

    /**
     * Return the damage of this weapon.
     */
    @Basic
    public int getDamage() {
        return damage;
    }

    /**
     * Set the damage of this weapon to the given damage.
     *
     * @param   damage
     *          The new damage for this weapon.
     *
     * @pre     The given damage must be legal.
     *          | isValidDamage(damage)
     * @post    The given damage is registered as the damage of this weapon.
     *          | new.getDamage() == damage
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * Return the maximum damage a weapon can deal.
     */
    @Basic @Immutable
    public static int getMaximumDamage() {
        return maximumDamage;
    }

    /**
     * Check whether the given damage is valid for this weapon.
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


    /**********************************************************
     * Value
     **********************************************************/

    /**
     * Variable expressing the maximum value of a weapon, in dukaten.
     */
    protected int maximumValue = 200;

    /**
     * Variable expressing the value per damage unit for a weapon.
     */
    private static final int valuePerDamageUnit = 2; // Default value, 2 dukaten per damage unit

    /**
     * Returns the base value of this weapon.
     */
    @Basic
    public int getBaseValue() {
        return baseValue;
    }

    /**
     * Returns the maximum value a weapon can have, in dukaten.
     */
    @Basic
    public int getMaximumValue() {
        return maximumValue;
    }

    /**
     * Returns the value per damage unit for a weapon, in dukaten.
     */
    @Basic
    public int getValuePerDamageUnit() {
        return valuePerDamageUnit;
    }

    /**
     * Check whether the given value is a valid value for this weapon.
     *
     * @param   value
     *          The value to check.
     *
     * @return  True if and only if the value is a valid equipment value
     *          (in other words positive and not exceeding the maximum), and equal to
     *          the product of this weapon's damage and the value per damage unit.
     *          | result == (super.canHaveAsValue(value)
     *          |            && value == damage * valuePerDamageUnit)
     */
    protected boolean canHaveAsValue(int value) {
        return super.canHaveAsValue(value)
                && value == damage * valuePerDamageUnit;
    }

    /**
     * Calculate the current value of this weapon.
     *
     * @return  The calculated current value, guaranteed to be positive and at most 200.
     *          | result == damage * valuePerDamageUnit
     */
    protected int calculateCurrentValue() {
        return damage * valuePerDamageUnit;
    }
}

