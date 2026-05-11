package input;

import java.util.HashMap;

import static variables.Variables.DIRECTION;

public interface KeyInput {
    // [UC1.1 - Bước 1 & 1.1]: Nơi đầu tiên ghi nhận tín hiệu vật lý từ Player (Nhấn phím)
    // và lưu trạng thái (true/false) vào cấu trúc dữ liệu HashMap.
    HashMap<String, Boolean> keyInput = new HashMap<>();

    public abstract void initialization();

    public abstract DIRECTION handleKeyInput();
}
