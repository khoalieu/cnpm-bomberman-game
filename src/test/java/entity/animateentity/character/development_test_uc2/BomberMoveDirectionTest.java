package entity.animateentity.character.development_test_uc2;

import entity.animateentity.character.Bomber;
import graphics.Sprite;
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

    @Test
    void setDirection_ShouldMoveUp_WhenInputIsUp() {
        Bomber bomber = createBomberWithInput(UP);

        bomber.setDirection();

        assertEquals(UP, bomber.getDirection());
        assertEquals(0, bomber.getVelocityX());
        assertTrue(bomber.getVelocityY() < 0);
    }

    @Test
    void setDirection_ShouldMoveDown_WhenInputIsDown() {
        Bomber bomber = createBomberWithInput(DOWN);

        bomber.setDirection();

        assertEquals(DOWN, bomber.getDirection());
        assertEquals(0, bomber.getVelocityX());
        assertTrue(bomber.getVelocityY() > 0);
    }

    @Test
    void setDirection_ShouldMoveLeft_WhenInputIsLeft() {
        Bomber bomber = createBomberWithInput(LEFT);

        bomber.setDirection();

        assertEquals(LEFT, bomber.getDirection());
        assertTrue(bomber.getVelocityX() < 0);
        assertEquals(0, bomber.getVelocityY());
    }

    @Test
    void setDirection_ShouldMoveRight_WhenInputIsRight() {
        Bomber bomber = createBomberWithInput(RIGHT);

        bomber.setDirection();

        assertEquals(RIGHT, bomber.getDirection());
        assertTrue(bomber.getVelocityX() > 0);
        assertEquals(0, bomber.getVelocityY());
    }

    @Test
    void setDirection_ShouldStop_WhenInputIsNone() {
        Bomber bomber = createBomberWithInput(NONE);

        bomber.setDirection();

        assertEquals(NONE, bomber.getDirection());
        assertEquals(0, bomber.getVelocityX());
        assertEquals(0, bomber.getVelocityY());
    }
}