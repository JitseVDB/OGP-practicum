import java.util.Random;

/**
 * A class to simulate a battle between a Hero and a Monster.
 *
 * @invar The hero and the monster must not be null.
 *        | getHero() != null && getMonster() != null
 */
public class Battle {

    private final Hero hero;
    private final Monster monster;

    /**
     * Creates a new battle instance between the given hero and monster.
     *
     * @param hero
     *        The hero participating in the battle.
     * @param monster
     *        The monster participating in the battle.
     *
     * @throws IllegalArgumentException
     *         if either hero or monster is null.
     *
     * @pre hero != null
     * @pre monster != null
     *
     * @post getHero() == hero
     * @post getMonster() == monster
     */
    public Battle(Hero hero, Monster monster) {
        if (hero == null || monster == null)
            throw new IllegalArgumentException("Hero and Monster cannot be null.");
        this.hero = hero;
        this.monster = monster;
    }

    /**
     * Starts and executes the full battle simulation between the hero and the monster.
     * A random boolean determines who begins the fight. Each participant takes turns
     * attacking the other until one of them dies.
     * After the fight, the name and remaining hit points of the winner are printed.
     *
     * @post One of the two participants is no longer alive.
     */
    public void fight() {
        System.out.println("Battle starts between " + hero.getName() + " and " + monster.getName());
        Random random = new Random();
        boolean heroStarts = random.nextBoolean();

        while (hero.isAlive() && monster.isAlive()) {
            if (heroStarts) {
                System.out.println(hero.getName() + " hits " + monster.getName());
                hero.hit(monster);
            } else {
                System.out.println(monster.getName() + " hits " + hero.getName());
                monsterHit(hero);
            }
            heroStarts = !heroStarts;
        }

        Entity winner;
        if (hero.isAlive()) {
            winner = hero;
        } else {
            winner = monster;
        }
        System.out.println("Winner is: " + winner.getName());
        System.out.println("Remaining hit points: " + winner.getHitPoints());
    }

    /**
     * Executes a single attack from the monster to the hero.
     * The monster generates a random attack roll. If the value is greater than
     * or equal to the hero’s protection (including armor), the hit succeeds and
     * the hero takes damage equal to the monster’s damage value.
     *
     * @param hero
     *        The hero being attacked.
     *
     * @pre hero != null
     *
     * @effect The hero may receive damage if the hit succeeds.
     * @effect The monster may collect items from the hero if the hero dies.
     */
    private void monsterHit(Hero hero) {
        monster.setFighting(true);
        int attackRoll;
        Random random = new Random();

        // Simuleer attackRoll volgens de monsterregels
        int randomRoll = random.nextInt(101);
        attackRoll = Math.min(randomRoll, monster.getHitPoints());

        int targetProtection = hero.getProtection();
        if (hero.getArmor() != null) {
            targetProtection += hero.getArmor().getCurrentProtection();
        }

        if (attackRoll >= targetProtection) {
            int damage = 10; // stel vast of de monsterklasse schade bevat
            hero.removeHitPoints(damage);
            System.out.println(monster.getName() + " dealt " + damage + " damage.");
        } else {
            System.out.println(monster.getName() + "'s attack missed!");
        }
        monster.setFighting(false);
    }
}
