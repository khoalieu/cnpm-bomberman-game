package UC4Test;

import entity.animateentity.character.Bomber;
import entity.animateentity.character.enemy.Enemy;
import graphics.Sprite;
import map.Map;
import org.junit.platform.commons.annotation.Testable;

import variables.Variables.DIRECTION;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnemyEnragedTest {

    // Class giả lập Enemy để test logic Enraged
    static class TestEnemy extends Enemy {

        public TestEnemy() {
            super(0, 0, new Sprite(16, 0xff000000));

            Sprite[] testSprites = new Sprite[]{
                    new Sprite(16, 0xff000000),
                    new Sprite(16, 0xff000000),
                    new Sprite(16, 0xff000000)
            };

            animation.put(DIRECTION.UP, testSprites);
            animation.put(DIRECTION.DOWN, testSprites);
            animation.put(DIRECTION.LEFT, testSprites);
            animation.put(DIRECTION.RIGHT, testSprites);
            animation.put(DIRECTION.DESTROYED, testSprites);

            this.direction = DIRECTION.UP;
            this.currentAnimate = animation.get(DIRECTION.UP);

            this.speed = 1;
            this.life = 1;
        }

        @Override
        public DIRECTION path(Map map, Bomber player, Enemy enemy) {
            return DIRECTION.UP;
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Enemy> getEnemies(Map map) throws Exception {
        Field enemiesField = Map.class.getDeclaredField("enemies");
        enemiesField.setAccessible(true);
        return (ArrayList<Enemy>) enemiesField.get(map);
    }

    private void callTriggerLastEnemyEnraged(Map map) throws Exception {
        Method method = Map.class.getDeclaredMethod("triggerLastEnemyEnragedIfNeeded");
        method.setAccessible(true);
        method.invoke(map);
    }

    @Testable
    void testLastEnemyShouldBecomeEnraged() throws Exception {
        Map map = Map.getGameMap();

        ArrayList<Enemy> enemies = getEnemies(map);
        enemies.clear();

        TestEnemy enemy = new TestEnemy();
        enemies.add(enemy);

        callTriggerLastEnemyEnraged(map);

        assertTrue(enemy.isEnraged());
    }

    @Testable
    void testLastEnemyShouldHaveAtLeastThreeLives() throws Exception {
        Map map = Map.getGameMap();

        ArrayList<Enemy> enemies = getEnemies(map);
        enemies.clear();

        TestEnemy enemy = new TestEnemy();
        enemies.add(enemy);

        callTriggerLastEnemyEnraged(map);

        assertEquals(3, enemy.getLife());
    }

    @Testable
    void testLastEnemyShouldIncreaseSpeed() throws Exception {
        Map map = Map.getGameMap();

        ArrayList<Enemy> enemies = getEnemies(map);
        enemies.clear();

        TestEnemy enemy = new TestEnemy();
        enemies.add(enemy);

        int oldSpeed = enemy.getSpeed();

        callTriggerLastEnemyEnraged(map);

        assertEquals(oldSpeed + 1, enemy.getSpeed());
    }

    @Testable
    void testMoreThanOneEnemyShouldNotBecomeEnraged() throws Exception {
        Map map = Map.getGameMap();

        ArrayList<Enemy> enemies = getEnemies(map);
        enemies.clear();

        TestEnemy enemy1 = new TestEnemy();
        TestEnemy enemy2 = new TestEnemy();

        enemies.add(enemy1);
        enemies.add(enemy2);

        callTriggerLastEnemyEnraged(map);

        assertFalse(enemy1.isEnraged());
        assertFalse(enemy2.isEnraged());
    }

    @Testable
    void testEnragedEnemyShouldNotBuffTwice() throws Exception {
        Map map = Map.getGameMap();

        ArrayList<Enemy> enemies = getEnemies(map);
        enemies.clear();

        TestEnemy enemy = new TestEnemy();
        enemies.add(enemy);

        callTriggerLastEnemyEnraged(map);

        int lifeAfterFirstBuff = enemy.getLife();
        int speedAfterFirstBuff = enemy.getSpeed();

        callTriggerLastEnemyEnraged(map);

        assertEquals(lifeAfterFirstBuff, enemy.getLife());
        assertEquals(speedAfterFirstBuff, enemy.getSpeed());
    }

    @Testable
    void testEnragedEnemyShouldNeedThreeHitsToRemove() throws Exception {
        Map map = Map.getGameMap();

        ArrayList<Enemy> enemies = getEnemies(map);
        enemies.clear();

        TestEnemy enemy = new TestEnemy();
        enemies.add(enemy);

        callTriggerLastEnemyEnraged(map);

        enemy.delete();
        assertFalse(enemy.isRemoved());
        assertEquals(2, enemy.getLife());

        enemy.delete();
        assertFalse(enemy.isRemoved());
        assertEquals(1, enemy.getLife());

        enemy.delete();
        assertTrue(enemy.isRemoved());
        assertEquals(0, enemy.getLife());
    }
}