package path;

import entity.animateentity.character.Bomber;
import entity.animateentity.character.enemy.Enemy;
import map.Map;
import variables.Variables.DIRECTION;

import java.util.ArrayList;
import java.util.Random;

/**
 * Thuật toán di chuyển ngẫu nhiên cho kẻ địch
 * Triển khai logic chọn hướng cơ bản theo UC4.2 và xử lý va chạm UC4.4a
 */
public class RandomPath extends Path {
    public RandomPath(Map map, Bomber player, Enemy enemy) {
        super(map, player, enemy);
    }

    /*
    |--------------------------------------------------------------------------
    | AI Logic - Thực hiện UC4.2 & UC4.3
    |--------------------------------------------------------------------------
     */
    public DIRECTION path() {
        // =============================================================
        // UC4.4a.2: Hệ thống kích hoạt logic chọn hướng mới khi:
        // 1. Quái vật bị chặn (isCollider)
        // 2. Quái vật đã di chuyển trọn vẹn vào một ô lưới (isInATile)
        // =============================================================
        if (enemy.isCollider() || enemy.isInATile()) {
            ArrayList<DIRECTION> canDirections = new ArrayList<>();

            // =============================================================
            // UC4.3: Hệ thống tính toán các hướng di chuyển khả thi
            // =============================================================
            for (int k = 0; k < 4; k++) {
                // UC4.4a.1: Kiểm tra tọa độ dự kiến có chứa vật cản (Wall, Brick, Bomb) hay không
                if (!enemy.checkTileCollider(intToDirection(k), false)) {
                    // Nếu không có vật cản, thêm vào danh sách hướng có thể đi (UC4.4)
                    canDirections.add(intToDirection(k));
                }
            }

            // UC4.4a.2: Nếu bị chặn hoàn toàn cả 4 hướng, hệ thống giữ nguyên hướng cũ (tạm dừng)
            if (canDirections.size() == 0) {
                return enemy.getDirection();
            }

            // =============================================================
            // UC4.2: Xác định thuật toán AI là di chuyển ngẫu nhiên
            // UC4.3: Chọn ngẫu nhiên một hướng trong danh sách để trả về tọa độ dự kiến (x', y')
            // =============================================================
            int random = new Random().nextInt(canDirections.size());
            return canDirections.get(random);
        } else {
            // =============================================================
            // UC4.4: Nếu không có vật cản và đang trong quá trình di chuyển giữa các ô,
            // hệ thống tiếp tục duy trì hướng đi và hoạt ảnh hiện tại.
            // =============================================================
            return enemy.getDirection();
        }
    }
}