package entity.animateentity.character.development_test_uc3;

import input.KeyInput;
import variables.Variables.DIRECTION;

import static variables.Variables.DIRECTION.NONE;

public class FakeKeyInput implements KeyInput {

    private DIRECTION direction;

    public FakeKeyInput(DIRECTION direction) {
        this.direction = direction;
    }

    @Override
    public void initialization() {
        // Development unit test: no real key mappings needed
    }

    @Override
    public DIRECTION handleKeyInput() {
        return direction == null ? NONE : direction;
    }

    public void setDirection(DIRECTION direction) {
        this.direction = direction;
    }
}
