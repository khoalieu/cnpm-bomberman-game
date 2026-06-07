package entity.animateentity.character.development_test_uc2;

import entity.animateentity.character.Bomber;
import graphics.Sprite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static variables.Variables.DIRECTION.*;

class BomberMoveDirectionTest {

    private Sprite dummySprite() {
        return new Sprite(Sprite.DEFAULT_SIZE, 0xffffffff);
    }

    private Bomber createBomberWithInput(variables.Variables.DIRECTION direction) {
        return new Bomber(32, 32, dummySprite(), new FakeKeyInput(direction));
    }

    // Mã ca kiểm thử: TC2_DEV_07
    @Test
    @DisplayName("TC2_DEV_07 - Bomber di chuyển lên khi nhận hướng đi lên")
    void setDirection_ShouldMoveUp_WhenInputIsUp() {
        Bomber bomber = createBomberWithInput(UP);

        bomber.setDirection();

        assertEquals(UP, bomber.getDirection());
        assertEquals(0, bomber.getVelocityX());
        assertTrue(bomber.getVelocityY() < 0);
    }

    // Mã ca kiểm thử: TC2_DEV_08
    @Test
    @DisplayName("TC2_DEV_08 - Bomber di chuyển xuống khi nhận hướng đi xuống")
    void setDirection_ShouldMoveDown_WhenInputIsDown() {
        Bomber bomber = createBomberWithInput(DOWN);

        bomber.setDirection();

        assertEquals(DOWN, bomber.getDirection());
        assertEquals(0, bomber.getVelocityX());
        assertTrue(bomber.getVelocityY() > 0);
    }

    // Mã ca kiểm thử: TC2_DEV_09
    @Test
    @DisplayName("TC2_DEV_09 - Bomber di chuyển sang trái khi nhận hướng sang trái")
    void setDirection_ShouldMoveLeft_WhenInputIsLeft() {
        Bomber bomber = createBomberWithInput(LEFT);

        bomber.setDirection();

        assertEquals(LEFT, bomber.getDirection());
        assertTrue(bomber.getVelocityX() < 0);
        assertEquals(0, bomber.getVelocityY());
    }

    // Mã ca kiểm thử: TC2_DEV_10
    @Test
    @DisplayName("TC2_DEV_10 - Bomber di chuyển sang phải khi nhận hướng sang phải")
    void setDirection_ShouldMoveRight_WhenInputIsRight() {
        Bomber bomber = createBomberWithInput(RIGHT);

        bomber.setDirection();

        assertEquals(RIGHT, bomber.getDirection());
        assertTrue(bomber.getVelocityX() > 0);
        assertEquals(0, bomber.getVelocityY());
    }

    // Mã ca kiểm thử: TC2_DEV_11
    @Test
    @DisplayName("TC2_DEV_11 - Bomber dừng lại khi không có hướng di chuyển")
    void setDirection_ShouldStop_WhenInputIsNone() {
        Bomber bomber = createBomberWithInput(NONE);

        bomber.setDirection();

        assertEquals(NONE, bomber.getDirection());
        assertEquals(0, bomber.getVelocityX());
        assertEquals(0, bomber.getVelocityY());
    }
}