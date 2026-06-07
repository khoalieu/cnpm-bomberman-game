package entity.animateentity.character.development_test_uc2;

import entity.Entity;
import entity.animateentity.Bomb;
import entity.animateentity.Brick;
import entity.animateentity.character.Bomber;
import graphics.Sprite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static variables.Variables.DIRECTION.NONE;

class BomberCanPassTest {

    private Sprite dummySprite() {
        return new Sprite(Sprite.DEFAULT_SIZE, 0xffffffff);
    }

    private Bomber createBomber() {
        return new Bomber(32, 32, dummySprite(), new FakeKeyInput(NONE));
    }

    // Mã ca kiểm thử: TC2_DEV_12
    @Test
    @DisplayName("TC2_DEV_12 - Bomber có thể đi qua gạch khi bật hiệu ứng đi xuyên tường")
    void canPass_ShouldReturnTrue_WhenEntityIsBrickAndPassWallIsEnabled() {
        Bomber bomber = createBomber();
        bomber.passWall = true;

        Brick brick = new Brick(32, 32, dummySprite());

        boolean actual = bomber.canPass(brick);

        assertTrue(actual);
    }

    // Mã ca kiểm thử: TC2_DEV_13
    @Test
    @DisplayName("TC2_DEV_13 - Bomber không thể đi qua gạch khi tắt hiệu ứng đi xuyên tường")
    void canPass_ShouldReturnFalse_WhenEntityIsBrickAndPassWallIsDisabled() {
        Bomber bomber = createBomber();
        bomber.passWall = false;

        Brick brick = new Brick(32, 32, dummySprite());

        boolean actual = bomber.canPass(brick);

        assertFalse(actual);
    }

    // Mã ca kiểm thử: TC2_DEV_14
    @Test
    @DisplayName("TC2_DEV_14 - Bomber có thể đi qua bom khi bật hiệu ứng đi xuyên bom")
    void canPass_ShouldReturnTrue_WhenEntityIsBombAndPassBombIsEnabled() {
        Bomber bomber = createBomber();
        bomber.passBomb = true;

        Bomb bomb = new Bomb(1, 1, dummySprite());

        boolean actual = bomber.canPass(bomb);

        assertTrue(actual);
    }

    // Mã ca kiểm thử: TC2_DEV_15
    @Test
    @DisplayName("TC2_DEV_15 - Bomber không thể đi qua bom khi tắt hiệu ứng đi xuyên bom")
    void canPass_ShouldReturnFalse_WhenEntityIsBombAndPassBombIsDisabled() {
        Bomber bomber = createBomber();
        bomber.passBomb = false;

        Bomb bomb = new Bomb(1, 1, dummySprite());

        boolean actual = bomber.canPass(bomb);

        assertFalse(actual);
    }

    // Mã ca kiểm thử: TC2_DEV_16
    @Test
    @DisplayName("TC2_DEV_16 - Bomber xử lý vật thể thường theo luật mặc định")
    void canPass_ShouldUseParentRule_WhenEntityIsNormalEntity() {
        Bomber bomber = createBomber();

        Entity normalEntity = new Entity(32, 32, dummySprite()) {
            @Override
            public void update() {
            }

            @Override
            public void render(javafx.scene.canvas.GraphicsContext gc) {
            }
        };

        boolean actual = bomber.canPass(normalEntity);

        // Theo logic mặc định của super.canPass(entity), vật thể thường không cho đi qua.
        assertFalse(actual);
    }
}