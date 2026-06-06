package entity.animateentity.character.development_test_uc2;
import input.KeyInput;
import input.PlayerInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import variables.Variables.DIRECTION;

import static org.junit.jupiter.api.Assertions.*;
import static variables.Variables.DIRECTION.*;

class PlayerInputTest {

    private PlayerInput playerInput;

    @BeforeEach
    void setUp() {
        playerInput = new PlayerInput();
        playerInput.initialization();

        // Vì keyInput trong interface là static nên nên reset toàn bộ trước mỗi test
        KeyInput.keyInput.replaceAll((key, value) -> false);
    }

    @Test
    void handleKeyInput_ShouldReturnUp_WhenWIsPressed() {
        KeyInput.keyInput.put("W", true);

        DIRECTION result = playerInput.handleKeyInput();

        assertEquals(UP, result);
    }

    @Test
    void handleKeyInput_ShouldReturnRight_WhenDIsPressed() {
        KeyInput.keyInput.put("D", true);

        DIRECTION result = playerInput.handleKeyInput();

        assertEquals(RIGHT, result);
    }

    @Test
    void handleKeyInput_ShouldReturnDown_WhenSIsPressed() {
        KeyInput.keyInput.put("S", true);

        DIRECTION result = playerInput.handleKeyInput();

        assertEquals(DOWN, result);
    }

    @Test
    void handleKeyInput_ShouldReturnLeft_WhenAIsPressed() {
        KeyInput.keyInput.put("A", true);

        DIRECTION result = playerInput.handleKeyInput();

        assertEquals(LEFT, result);
    }

    @Test
    void handleKeyInput_ShouldReturnPlaceBomb_WhenSpaceIsPressed() {
        KeyInput.keyInput.put("SPACE", true);

        DIRECTION result = playerInput.handleKeyInput();

        assertEquals(PLACEBOMB, result);
    }

    @Test
    void handleKeyInput_ShouldReturnNone_WhenNoKeyIsPressed() {
        DIRECTION result = playerInput.handleKeyInput();

        assertEquals(NONE, result);
    }
}