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
 */
public class DodgePath extends Path {
    public DodgePath(Map map, Bomber player, Enemy enemy) {
        super(map, player, enemy);
    }
    /*
    |--------------------------------------------------------------------------
    | AI Logic
    |--------------------------------------------------------------------------
     */
    @Override
    public DIRECTION path() {
        // Chỉ tính toán lại hướng đi khi quái vật đã nằm gọn trong một ô lưới
        if (enemy.isInATile()) {

            // Trường hợp 1: Nếu người chơi ở ngay sát bên (khoảng cách = 1), xác định hướng tấn công trực diện
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
            // Trường hợp 2: Tìm hướng đi có khoảng cách ngắn nhất đến người chơi, né tránh Tường cứng
            int minDistance = INF;
            DIRECTION nowDirection = UP;
            for (int k = 0; k < 4; k++) {
                // Kiểm tra nếu ô tiếp theo không phải là Tường cứng (Wall)
                if (!(map.getTile(enemy.getTileX() + dx[k], enemy.getTileY() + dy[k]) instanceof Wall)) {

                    // Bỏ qua hướng đi nếu vướng phải các vật cản khác (như Bom)
                    if (enemy.checkTileCollider(intToDirection(k), true)) {
                        continue;
                    }

                    // Tính toán khoảng cách thực tế từ ô dự kiến đến vị trí Bomber
                    int curDistance = Distance(enemy.getTileY() + dy[k], enemy.getTileX() + dx[k],
                            player.getTileY(), player.getTileX(), true);

                    // Cập nhật hướng đi tối ưu nhất (khoảng cách nhỏ nhất)
                    if (minDistance > curDistance) {
                        minDistance = curDistance;
                        nowDirection = intToDirection(k);
                    }
                }
            }
            return nowDirection;
        } else {
            // Tiếp tục di chuyển theo hướng hiện tại nếu chưa vào giữa ô lưới
            return enemy.getDirection();
        }
    }
}