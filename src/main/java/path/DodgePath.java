package path;

import entity.animateentity.character.Bomber;
import entity.animateentity.character.enemy.Enemy;
import entity.staticentity.Wall;
import map.Map;
import variables.Variables.DIRECTION;

import static variables.Variables.*;
import static variables.Variables.DIRECTION.*;

/**
 * Thuật toán né tránh và tìm đường nâng cao, ưu tiên tránh Tường cứng (Wall)
 * Triển khai logic điều khiển AI cho các quái vật thông minh (như Kondoria) theo UC4.
 */
public class DodgePath extends Path {
    public DodgePath(Map map, Bomber player, Enemy enemy) {
        super(map, player, enemy);
    }
    /*
    |--------------------------------------------------------------------------
    | AI Logic - Thực hiện UC4.2 & UC4.3
    |--------------------------------------------------------------------------
     */
    @Override
    public DIRECTION path() {
        // UC4.1: Hệ thống kiểm tra điều kiện để bắt đầu chu kỳ tính toán hướng mới
        if (enemy.isInATile()) {

            // =============================================================
            // UC4.5: Kiểm tra va chạm giữa quái vật và Bomber (Khoảng cách = 1)
            // Hệ thống xác định hướng tấn công trực diện để tiêu diệt người chơi
            // =============================================================
            if (Distance(enemy.getTileY(), enemy.getTileX(), player.getTileY(), player.getTileX(), true) == 1) {
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
            // UC4.3: Hệ thống tính toán hướng di chuyển tối ưu (x', y')
            // Ưu tiên các ô không vướng vật cản để duy trì luồng di chuyển mượt mà
            // =============================================================
            int minDistance = INF;
            DIRECTION nowDirection = UP;
            for (int k = 0; k < 4; k++) {

                // UC4.4a.1: Kiểm tra tọa độ dự kiến có chứa Tường cứng (Wall) hay không
                if (!(map.getTile(enemy.getTileX() + dx[k], enemy.getTileY() + dy[k]) instanceof Wall)) {

                    // UC4.4a.1 (tiếp): Kiểm tra va chạm với các vật cản khác (như Bom)
                    if (enemy.checkTileCollider(intToDirection(k), true)) {
                        // UC4.4a.2: Hệ thống bỏ qua hướng di chuyển bị chặn
                        continue;
                    }

                    // Tính toán khoảng cách thực tế từ ô dự kiến đến vị trí Bomber
                    int curDistance = Distance(enemy.getTileY() + dy[k], enemy.getTileX() + dx[k],
                            player.getTileY(), player.getTileX(), true);

                    // Cập nhật hướng đi tối ưu nhất (UC4.3)
                    if (minDistance > curDistance) {
                        minDistance = curDistance;
                        nowDirection = intToDirection(k);
                    }
                }
            }
            return nowDirection;
        } else {
            // =============================================================
            // UC4.4: Nếu chưa vào giữa ô lưới, quái vật tiếp tục di chuyển
            // theo hướng cũ để đảm bảo tính liên tục của hoạt ảnh
            // =============================================================
            return enemy.getDirection();
        }
    }
}