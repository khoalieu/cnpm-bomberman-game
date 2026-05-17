package path;

import entity.animateentity.character.Bomber;
import entity.animateentity.character.enemy.Enemy;
import entity.staticentity.Grass;
import map.Map;
import variables.Variables.DIRECTION;

import static variables.Variables.DIRECTION.*;
import static variables.Variables.*;

/**
 * Thuật toán tìm đường thông minh dựa trên khoảng cách tới người chơi.
 * Triển khai chi tiết bước UC4.2 và UC4.3 trong kịch bản điều khiển quái vật.
 */
public class DistancePath extends Path {

    public DistancePath(Map map, Bomber player, Enemy enemy) {
        super(map, player, enemy);
    }
    /*
    |--------------------------------------------------------------------------
    | AI Logic - Thực hiện UC4.2 & UC4.3
    |--------------------------------------------------------------------------
     */
    public DIRECTION path() {
        // Hệ thống chỉ tính toán lại hướng đi khi thực thể đã nằm gọn trong một ô lưới (Grid)
        if (enemy.isInATile()) {

            // =============================================================
            // UC4.3: Trường hợp tấn công trực diện (Khoảng cách = 1)
            // Hệ thống xác định hướng di chuyển dự kiến để va chạm với Bomber (UC4.5)
            // =============================================================
            if (Distance(enemy.getTileY(), enemy.getTileX(), player.getTileY(), player.getTileX(), false) == 1) {
                int enemyPixelX = enemy.getPixelX();
                int enemyPixelY = enemy.getPixelY();
                DIRECTION nowDirection = UP;
                for (int k = 0; k < 4; k++) {
                    enemy.setTile(enemy.getTileX() + dx[k], enemy.getTileY() + dy[k]);
                    if (player.isCollider(enemy)) {
                        nowDirection = intToDirection(k);
                    }
                    enemy.setPosition(enemyPixelX, enemyPixelY);
                }
                return nowDirection;
            }

            // =============================================================
            // UC4.3 (tiếp): Tìm hướng đi tối ưu thông qua các ô cỏ (Grass)
            // Đây là logic AI cốt lõi để xác định tọa độ dự kiến (x', y')
            // =============================================================
            int minDistance = INF;
            DIRECTION nowDirection = UP;
            for (int k = 0; k < 4; k++) {
                if (map.getTile(enemy.getTileX() + dx[k], enemy.getTileY() + dy[k]) instanceof Grass) {
                    int curDistance = Distance(enemy.getTileY() + dy[k], enemy.getTileX() + dx[k],
                            player.getTileY(), player.getTileX(), false);

                    // =============================================================
                    // UC4.4a.1: Kiểm tra vật cản (Wall, Brick, Bomb) tại ô mục tiêu
                    // =============================================================
                    if (enemy.checkTileCollider(intToDirection(k), true)) {
                        continue; // UC4.4a.2: Bỏ qua hướng đi bị chặn
                    }

                    // Cập nhật hướng đi dẫn đến khoảng cách ngắn nhất tới người chơi
                    if (minDistance > curDistance) {
                        minDistance = curDistance;
                        nowDirection = intToDirection(k);
                    }
                }
            }
            return nowDirection;
        } else {
            // =============================================================
            // UC4.4: Duy trì hướng di chuyển hiện tại nếu chưa vào giữa ô lưới
            // =============================================================
            return enemy.getDirection();
        }
    }
}