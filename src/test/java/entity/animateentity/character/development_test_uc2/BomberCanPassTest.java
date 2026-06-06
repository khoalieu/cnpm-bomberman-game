package entity.animateentity.character.development_test_uc2;

import entity.Entity;
import entity.animateentity.Bomb;
import entity.animateentity.Brick;
import entity.animateentity.character.Bomber;
import graphics.Sprite;
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

    @Test
    void canPass_ShouldReturnTrue_WhenEntityIsBrickAndPassWallIsEnabled() {
        Bomber bomber = createBomber();
        bomber.passWall = true;

        Brick brick = new Brick(32, 32, dummySprite());

        boolean actual = bomber.canPass(brick);

        assertTrue(actual);
    }

    @Test
    void canPass_ShouldReturnFalse_WhenEntityIsBrickAndPassWallIsDisabled() {
        Bomber bomber = createBomber();
        bomber.passWall = false;

        Brick brick = new Brick(32, 32, dummySprite());

        boolean actual = bomber.canPass(brick);

        assertFalse(actual);
    }

    @Test
    void canPass_ShouldReturnTrue_WhenEntityIsBombAndPassBombIsEnabled() {
        Bomber bomber = createBomber();
        bomber.passBomb = true;

        Bomb bomb = new Bomb(1, 1, dummySprite());

        boolean actual = bomber.canPass(bomb);

        assertTrue(actual);
    }

    @Test
    void canPass_ShouldReturnFalse_WhenEntityIsBombAndPassBombIsDisabled() {
        Bomber bomber = createBomber();
        bomber.passBomb = false;

        Bomb bomb = new Bomb(1, 1, dummySprite());

        boolean actual = bomber.canPass(bomb);

        assertFalse(actual);
    }

    @Test
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

        // Tùy logic super.canPass(entity) của Character/Entity.
        // Nếu mặc định vật thể thường không cho đi qua thì assertFalse.
        assertFalse(actual);
    }
}