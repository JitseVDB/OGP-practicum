/**
 * Represents a weapon a hero can use.
 */
public class Weapon {
    private String name;
    private int damage; // Fixed damage dealt by the weapon

    public Weapon(String name, int damage) {
        this.name = name;
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    public String getName() {
        return name;
    }
}
