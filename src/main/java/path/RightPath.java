package path;

import entity.animateentity.character.Bomber;
import entity.animateentity.character.enemy.Enemy;
import map.Map;
import variables.Variables.DIRECTION;

import static variables.Variables.DIRECTION.*;

/**
 * Thuật toán RightPath: Kết hợp khả năng tấn công trực diện (HeadPath) và ưu tiên rẽ phải khi gặp vật cản
 * Triển khai logic AI linh hoạt theo quy trình UC4.2 và UC4.3
 */
public class RightPath extends Path{
    public RightPath(Map map, Bomber player, Enemy enemy) {
        super(map, player, enemy);
    }
    /*
    |--------------------------------------------------------------------------
    | AI Logic - Thực hiện UC4.2 & UC4.3
    |--------------------------------------------------------------------------
     */
    @Override
    public DIRECTION path() {
        // =========================================================================
        // UC4.2: Hệ thống ưu tiên kiểm tra thuật toán tấn công trực diện (HeadPath)
        // Đây là bước xác định trạng thái AI: "Truy đuổi" hay "Tuần tra"
        // =========================================================================
        DIRECTION headPath = new HeadPath(map, player, enemy).path();

        if (headPath == NONE) {
            // =============================================================
            // TRẠNG THÁI TUẦN TRA: Khi không phát hiện người chơi (UC4.3)
            // =============================================================
            enemy.setSpeed(1);

            // UC4.4a.1: Hệ thống phát hiện va chạm với vật cản môi trường (isCollider)
            if (enemy.isCollider()) {
                // =============================================================
                // UC4.4a.2: Hệ thống kích hoạt logic chọn hướng mới (Quy tắc rẽ phải/trái)
                // =============================================================
                if (enemy.checkTileCollider(RIGHT, false)) {
                    return LEFT; // Nếu bên phải bị chặn, quay lại bên trái
                } else {
                    return RIGHT; // Ưu tiên rẽ phải nếu trống
                }
            } else {
                // UC4.4: Nếu không có vật cản, tiếp tục di chuyển theo hướng hiện tại
                return enemy.getDirection();
            }
        } else {
            // =============================================================
            // TRẠNG THÁI TRUY ĐUỔI: Khi phát hiện người chơi (UC4.3 & UC4.5)
            // Hệ thống tăng tốc độ và hướng trực tiếp tới tọa độ của Bomber
            // =============================================================
            enemy.setSpeed(2);
            return headPath;
        }
    }
}