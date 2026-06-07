package entity.animateentity.character.development_test_uc2;

import entity.animateentity.character.Bomber;
import graphics.Sprite;
import org.junit.jupiter.api.DisplayName;
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

    // Mã ca kiểm thử: TC2_DEV_17
    @Test
    @DisplayName("TC2_DEV_17 - Hiệu ứng đi xuyên tường bị tắt khi thời gian còn lại về 0")
    void updateBuffTimers_ShouldDisablePassWall_WhenTimerReachesZero() {
        Bomber bomber = createBomber();
        bomber.passWall = true;
        bomber.passWallTimer = 1;

        bomber.updateBuffTimersForTestableLogic();

        assertEquals(0, bomber.passWallTimer);
        assertFalse(bomber.passWall);
    }

    // Mã ca kiểm thử: TC2_DEV_18
    @Test
    @DisplayName("TC2_DEV_18 - Thời gian hiệu ứng đi xuyên tường giảm khi vẫn còn hiệu lực")
    void updateBuffTimers_ShouldDecreasePassWallTimer_WhenTimerStillGreaterThanZero() {
        Bomber bomber = createBomber();
        bomber.passWall = true;
        bomber.passWallTimer = 2;

        bomber.updateBuffTimersForTestableLogic();

        assertEquals(1, bomber.passWallTimer);
        assertTrue(bomber.passWall);
    }

    // Mã ca kiểm thử: TC2_DEV_19
    @Test
    @DisplayName("TC2_DEV_19 - Hiệu ứng đi xuyên bom bị tắt khi thời gian còn lại về 0")
    void updateBuffTimers_ShouldDisablePassBomb_WhenTimerReachesZero() {
        Bomber bomber = createBomber();
        bomber.passBomb = true;
        bomber.passBombTimer = 1;

        bomber.updateBuffTimersForTestableLogic();

        assertEquals(0, bomber.passBombTimer);
        assertFalse(bomber.passBomb);
    }

    // Mã ca kiểm thử: TC2_DEV_20
    @Test
    @DisplayName("TC2_DEV_20 - Khiên bảo vệ bị tắt khi thời gian còn lại về 0")
    void updateBuffTimers_ShouldDisableShield_WhenTimerReachesZero() {
        Bomber bomber = createBomber();
        bomber.hasShield = true;
        bomber.shieldTimer = 1;

        bomber.updateBuffTimersForTestableLogic();

        assertEquals(0, bomber.shieldTimer);
        assertFalse(bomber.hasShield);
    }

    // Mã ca kiểm thử: TC2_DEV_21
    @Test
    @DisplayName("TC2_DEV_21 - Hiệu ứng đi xuyên lửa bị tắt khi thời gian còn lại về 0")
    void updateBuffTimers_ShouldDisableFlamePass_WhenTimerReachesZero() {
        Bomber bomber = createBomber();
        bomber.isFlamePass = true;
        bomber.flamePassTimer = 1;

        bomber.updateBuffTimersForTestableLogic();

        assertEquals(0, bomber.flamePassTimer);
        assertFalse(bomber.isFlamePass);
    }

    // Mã ca kiểm thử: TC2_DEV_22
    @Test
    @DisplayName("TC2_DEV_22 - Không thay đổi trạng thái khi thời gian hiệu ứng đã bằng 0")
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