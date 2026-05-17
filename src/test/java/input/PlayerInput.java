package input;

import java.util.Set;
import variables.Variables.DIRECTION;
import static variables.Variables.DIRECTION.*;

public class PlayerInput implements KeyInput {

    public void initialization() {
        keyInput.put("A", false);
        keyInput.put("D", false);
        keyInput.put("W", false);
        keyInput.put("S", false);
        keyInput.put("SPACE", false);
    }

    @Override
    public DIRECTION handleKeyInput() {
        Set<String> keySet = keyInput.keySet();
        for (String code : keySet) {
            // =============================================================
            // UC2.1: Hệ thống nhận tín hiệu vật lý khi người chơi nhấn phím
            // =============================================================
            if (keyInput.get(code)) {
                switch (code) {
                    // =============================================================
                    // UC2.2: Hệ thống giải mã phím bấm (W,A,S,D) thành hằng số hướng
                    // =============================================================
                    case ("W"):
                        return UP;
                    case ("D"):
                        return RIGHT;
                    case ("S"):
                        return DOWN;
                    case ("A"):
                        return LEFT;
                    // ==============================
                    // UC3.1: Người chơi nhấn phím Space
                    // ==============================
                    case ("SPACE"):
                        return PLACEBOMB;
                }
            }
        }
        return NONE;
    }
}
