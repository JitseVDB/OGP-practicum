import java.util.Random;

public class Battle {

    private final Hero hero;
    private final Monster monster;

    public Battle(Hero hero, Monster monster) {
        if (hero == null || monster == null)
            throw new IllegalArgumentException("Hero and Monster cannot be null.");
        this.hero = hero;
        this.monster = monster;
    }

    public void fight() {
        System.out.println("Battle starts between " + hero.getName() + " and " + monster.getName());

        Random random = new Random();
        boolean heroStarts = random.nextBoolean();

        while (hero.isAlive() && monster.isAlive()) {
            if (heroStarts) {
                System.out.println(hero.getName() + " attacks " + monster.getName());
                hero.hit(monster);
            } else {
                System.out.println(monster.getName() + " attacks " + hero.getName());
                monster.hit(hero);
            }

            heroStarts = !heroStarts;
        }

        Entity winner;
        if (hero.isAlive()) {
            winner = hero;
        } else {
            winner = monster;
        }

        System.out.println("Winner: " + winner.getName());
        System.out.println("Remaining hit points: " + winner.getHitPoints());
    }

    public Hero getHero() {
        return hero;
    }

    public Monster getMonster() {
        return monster;
    }
}

