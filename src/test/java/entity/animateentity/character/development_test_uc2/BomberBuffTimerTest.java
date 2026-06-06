package entity.animateentity.character.development_test_uc2;

import entity.animateentity.character.Bomber;
import graphics.Sprite;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static variables.Variables.DIRECTION.NONE;

class BomberBuffTimerTest {

    private Sprite dummySprite() {
        return new Sprite(Sprite.DEFAULT_SIZE, 0xffffffff);
    }

    private Bomber createBomber() {
        return new Bomber(32, 32, dummySprite(), new FakeKeyInput(NONE));
    }

    @Test
    void updateBuffTimers_ShouldDisablePassWall_WhenTimerReachesZero() {
        Bomber bomber = createBomber();
        bomber.passWall = true;
        bomber.passWallTimer = 1;

        bomber.updateBuffTimersForTestableLogic();

        assertEquals(0, bomber.passWallTimer);
        assertFalse(bomber.passWall);
    }

    @Test
    void updateBuffTimers_ShouldDecreasePassWallTimer_WhenTimerStillGreaterThanZero() {
        Bomber bomber = createBomber();
        bomber.passWall = true;
        bomber.passWallTimer = 2;

        bomber.updateBuffTimersForTestableLogic();

        assertEquals(1, bomber.passWallTimer);
        assertTrue(bomber.passWall);
    }

    @Test
    void updateBuffTimers_ShouldDisablePassBomb_WhenTimerReachesZero() {
        Bomber bomber = createBomber();
        bomber.passBomb = true;
        bomber.passBombTimer = 1;

        bomber.updateBuffTimersForTestableLogic();

        assertEquals(0, bomber.passBombTimer);
        assertFalse(bomber.passBomb);
    }

    @Test
    void updateBuffTimers_ShouldDisableShield_WhenTimerReachesZero() {
        Bomber bomber = createBomber();
        bomber.hasShield = true;
        bomber.shieldTimer = 1;

        bomber.updateBuffTimersForTestableLogic();

        assertEquals(0, bomber.shieldTimer);
        assertFalse(bomber.hasShield);
    }

    @Test
    void updateBuffTimers_ShouldDisableFlamePass_WhenTimerReachesZero() {
        Bomber bomber = createBomber();
        bomber.isFlamePass = true;
        bomber.flamePassTimer = 1;

        bomber.updateBuffTimersForTestableLogic();

        assertEquals(0, bomber.flamePassTimer);
        assertFalse(bomber.isFlamePass);
    }

    @Test
    void updateBuffTimers_ShouldDoNothing_WhenTimersAreZero() {
        Bomber bomber = createBomber();
        bomber.passWall = false;
        bomber.passWallTimer = 0;
        bomber.passBomb = false;
        bomber.passBombTimer = 0;

        bomber.updateBuffTimersForTestableLogic();

        assertEquals(0, bomber.passWallTimer);
        assertFalse(bomber.passWall);
        assertEquals(0, bomber.passBombTimer);
        assertFalse(bomber.passBomb);
    }
}