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

        // =============================================================
        // UC5.11a & UC5.12a: Reset trạng thái phím chức năng mới
        // =============================================================
        keyInput.put("P", false);
        keyInput.put("M", false);
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
                    case ("W"): return UP;
                    case ("D"): return RIGHT;
                    case ("S"): return DOWN;
                    case ("A"): return LEFT;
                    // ==============================
                    // UC3.1: Người chơi nhấn phím Space để đặt bom.
                    // UC3.2: Hệ thống nhận và giải mã lệnh đặt bom từ người chơi –
                    //        trả về hằng số PLACEBOMB để Bomber.setDirection() xử lý tiếp.
                    // ==============================
                    case ("SPACE"): return PLACEBOMB;
                }
            }
        }
        return NONE;
    }
}