package entity.animateentity.character.development_test_uc2;

import input.KeyInput;
import variables.Variables.DIRECTION;

import static variables.Variables.DIRECTION.NONE;

class FakeKeyInput implements KeyInput {

    private DIRECTION direction;

    FakeKeyInput(DIRECTION direction) {
        this.direction = direction;
    }

    @Override
    public void initialization() {
        // Development unit test:
        // Không cần khởi tạo map phím thật.
    }

    @Override
    public DIRECTION handleKeyInput() {
        return direction == null ? NONE : direction;
    }

    void setDirection(DIRECTION direction) {
        this.direction = direction;
    }
}