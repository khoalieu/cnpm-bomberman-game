package entity.animateentity.character.development_test_uc2;

import input.KeyInput;
import input.PlayerInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

        // Reset trạng thái input trước mỗi ca kiểm thử vì keyInput là static trong interface KeyInput.
        KeyInput.keyInput.replaceAll((key, value) -> false);
    }

    // Mã ca kiểm thử: TC2_DEV_01
    @Test
    @DisplayName("TC2_DEV_01 - Trả về hướng đi lên khi nhấn phím W")
    void handleKeyInput_ShouldReturnUp_WhenWIsPressed() {
        KeyInput.keyInput.put("W", true);

        DIRECTION result = playerInput.handleKeyInput();

        assertEquals(UP, result);
    }

    // Mã ca kiểm thử: TC2_DEV_02
    @Test
    @DisplayName("TC2_DEV_02 - Trả về hướng sang phải khi nhấn phím D")
    void handleKeyInput_ShouldReturnRight_WhenDIsPressed() {
        KeyInput.keyInput.put("D", true);

        DIRECTION result = playerInput.handleKeyInput();

        assertEquals(RIGHT, result);
    }

    // Mã ca kiểm thử: TC2_DEV_03
    @Test
    @DisplayName("TC2_DEV_03 - Trả về hướng đi xuống khi nhấn phím S")
    void handleKeyInput_ShouldReturnDown_WhenSIsPressed() {
        KeyInput.keyInput.put("S", true);

        DIRECTION result = playerInput.handleKeyInput();

        assertEquals(DOWN, result);
    }

    // Mã ca kiểm thử: TC2_DEV_04
    @Test
    @DisplayName("TC2_DEV_04 - Trả về hướng sang trái khi nhấn phím A")
    void handleKeyInput_ShouldReturnLeft_WhenAIsPressed() {
        KeyInput.keyInput.put("A", true);

        DIRECTION result = playerInput.handleKeyInput();

        assertEquals(LEFT, result);
    }

    // Mã ca kiểm thử: TC2_DEV_05
    @Test
    @DisplayName("TC2_DEV_05 - Trả về hành động đặt bom khi nhấn phím SPACE")
    void handleKeyInput_ShouldReturnPlaceBomb_WhenSpaceIsPressed() {
        KeyInput.keyInput.put("SPACE", true);

        DIRECTION result = playerInput.handleKeyInput();

        assertEquals(PLACEBOMB, result);
    }

    // Mã ca kiểm thử: TC2_DEV_06
    @Test
    @DisplayName("TC2_DEV_06 - Trả về trạng thái không hành động khi không nhấn phím nào")
    void handleKeyInput_ShouldReturnNone_WhenNoKeyIsPressed() {
        DIRECTION result = playerInput.handleKeyInput();

        assertEquals(NONE, result);
    }
}