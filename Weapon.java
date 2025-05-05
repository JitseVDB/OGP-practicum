import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import be.kuleuven.cs.som.annotate.*;

/**
 * Represents a weapon a hero can use.
 */
public class Weapon extends Equipment {

    /**
     * Initialize a new weapon with the given weight, base value, and damage.
     *
     * @param weight
     *        The weight of the weapon.
     * @param baseValue
     *        The base value of the weapon, in dukaten.
     * @param damage
     *        The damage this weapon can inflict.
     *
     * @throws IllegalArgumentException
     *         If the given weight is invalid.
     *         | !isValidWeight(weight)
     *
     * @throws IllegalArgumentException
     *         If the given base value is invalid.
     *         | !isValidValue(baseValue)
     *
     * @throws IllegalArgumentException
     *         If the given damage is invalid: it must be positive,
     *         at most the maximum allowed damage, and divisible by 7.
     *         | !isValidDamage(damage)
     *
     * @post The new weapon has the given weight and base value.
     *       | new.getWeight() == weight
     *       | new.getBaseValue() == baseValue
     *
     * @post The new weapon has the given damage.
     *       | new.getDamage() == damage
     *
     * @post The weapon has a unique, valid identification number.
     *       | new.getIdentification() >= 0
     */
    public Weapon(int weight, int baseValue, int damage) {
        super(weight, baseValue);

        if (!isValidDamage(weight))
            throw new IllegalArgumentException("Damage cannot be negative, must be below the maximum damage and must be a multiple of 7.");

        this.damage = damage;
    }

    /**********************************************************
     * Identification
     **********************************************************/

    /**
     * Check whether the given identification number is a valid identification number for this weapon.
     *
     * @param equipmentType  The class type of the equipment for which the identification number is being validated.
     * @param identification The identification number to check.
     * @return True if the identification number is non-negative, divisible by 2, divisible by 3,
     * and unique within the given equipment type; false otherwise.
     * | result == super.isValidIdentification(equipmentType, identification)
     * |        && (identification % 2 == 0)
     * |        && (identification % 3 == 0)
     */
    @Override
    public boolean isValidIdentification(Class<?> equipmentType, long identification) {
        return super.isValidIdentification(equipmentType, identification)
                // The identification number must be divisible by 2 and 3.
                && identification % 2 == 0
                && identification % 3 == 0;
    }

    /**********************************************************
     * Damage
     **********************************************************/

    /**
     * Variable expressing the damage of this weapon
     */
    private int damage;

    /**
     * Constant expressing the maximum amount of damage a weapon can deal.
     */
    private static final int maximumDamage = 100;

    /**
     * Check whether the given damage is valid for this weapon.
     *
     * A valid damage is a positive integer that does not exceed the maximum allowed damage
     * and is a multiple of 7.
     *
     * @param damage The damage to check.
     * @return True if and only if the given damage is positive, does not exceed the maximum allowed damage,
     *         and is a multiple of 7.
     *         | result == (damage > 0 && damage <= maximumDamage && damage % 7 == 0)
     */
    public boolean isValidDamage(int damage) {
        return damage > 0 && damage <= maximumDamage && damage % 7 == 0;
    }


    /**********************************************************
     * Value
     **********************************************************/

    /**
     * Maximum value of a weapon, in dukaten.
     */
    protected int maximumValue = 200;

    /**
     * Variable registering the value per damage unit for a weapon.
     */
    private static final int valuePerDamageUnit = 2; // Default value, 2 dukaten per damage unit

    /**
     * Returns the base value of this weapon.
     */
    @Raw
    @Basic
    public int getBaseValue() {
        return baseValue;
    }

    /**
     * Check whether the given value is a valid value for this weapon.
     *
     * @param value The value to check.
     * @return True if and only if the given value is positive, does not
     * exceed the maximum allowed value in dukaten and is equal to the damage
     * multiplied by the damage per unit.
     * | result == (value > 0 && value <= maximumValue)
     */
    protected boolean isValidValue(int value) {
        return super.isValidValue(value)
                && value == damage * valuePerDamageUnit;
    }

    /**
     * Calculate the current value of this weapon.
     *
     * @return The calculated current value, guaranteed to be positive and at most 1000.
     * | result > 0 && result <= 1000
     */
    protected int calculateCurrentValue() {
        return damage * valuePerDamageUnit;
    }
}

