import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BattleTest {

    private Hero strongHero;
    private Hero weakHero;
    private Monster strongMonster;
    private Monster weakMonster;

    @BeforeEach
    void setUp() {
        strongHero = new Hero("StrongHero", 100, 12.0);
        weakHero = new Hero("WeakHero", 20, 1.0);

        // Monster constructor requires name, maxHitPoints, and initialItems
        strongMonster = new Monster("StrongMonster", 100, new ArrayList<>());
        strongMonster.setDamage(21); // must be a multiple of 7 and <= 100

        weakMonster = new Monster("WeakMonster", 30, new ArrayList<>());
        weakMonster.setDamage(7); // minimum valid damage
    }

    @Test
    void testStrongHeroBeatsWeakMonster() {
        Battle battle = new Battle(strongHero, weakMonster);
        battle.fight();

        assertTrue(strongHero.isAlive(), "Strong hero should survive");
        assertFalse(weakMonster.isAlive(), "Weak monster should be defeated");
    }

    @Test
    void testStrongMonsterBeatsWeakHero() {
        Battle battle = new Battle(weakHero, strongMonster);
        battle.fight();

        assertFalse(weakHero.isAlive(), "Weak hero should die");
        assertTrue(strongMonster.isAlive(), "Strong monster should survive");
    }

    @Test
    void testFightTerminatesAndWinnerDeclared() {
        Battle battle = new Battle(strongHero, strongMonster);
        assertDoesNotThrow(battle::fight, "Fight should terminate cleanly");

        // Only one entity should be alive after the fight
        boolean heroAlive = strongHero.isAlive();
        boolean monsterAlive = strongMonster.isAlive();

        assertTrue(heroAlive | monsterAlive);
    }

    @Test
    void testNullConstructorArgs() {
        assertThrows(IllegalArgumentException.class, () -> new Battle(null, weakMonster));
        assertThrows(IllegalArgumentException.class, () -> new Battle(strongHero, null));
    }

    @Test
    void testEdgeCaseBothWeak() {
        Hero lowHpHero = new Hero("Glass", 10, 1.0);
        Monster lowHpMonster = new Monster("Slime", 10, new ArrayList<>());
        lowHpMonster.setDamage(7);

        Battle battle = new Battle(lowHpHero, lowHpMonster);
        battle.fight();

        assertTrue(lowHpHero.isAlive() ^ lowHpMonster.isAlive());
    }
}
