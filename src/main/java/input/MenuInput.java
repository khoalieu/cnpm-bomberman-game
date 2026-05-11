package input;

import java.util.Set;
import variables.Variables.DIRECTION;
import static variables.Variables.DIRECTION.*;

public class MenuInput implements KeyInput {

    public void initialization() {
        // [UC1.1 - Bước 1.3.1]: Reset lại các phím về false sau khi đã xử lý xong tín hiệu,
        // đảm bảo qua màn mới không bị kẹt phím.
        keyInput.put("W", false);
        keyInput.put("S", false);
        keyInput.put("ENTER", false);
    }

    @Override
    public DIRECTION handleKeyInput() {
        // [UC1.1 - Bước 1.2.1]: Nếu phím ENTER (hoặc SPACE) mang giá trị true trong HashMap
        // -> Trả về trạng thái DIRECTION.DESTROYED (Tương đương lệnh chọn Start)
        Set<String> keySet = keyInput.keySet();
        for (String code : keySet) {
            if (keyInput.get(code)) {
                switch (code) {
                    case ("W"):
                        return UP;
                    case ("S"):
                        return DOWN;
                    case ("ENTER"):
                        return DESTROYED;
                }
            }
        }
        return NONE;
    }
}
