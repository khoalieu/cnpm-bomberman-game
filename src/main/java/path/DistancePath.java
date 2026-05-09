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
 */
public class DistancePath extends Path {

    public DistancePath(Map map, Bomber player, Enemy enemy) {
        super(map, player, enemy);
    }
    /*
    |--------------------------------------------------------------------------
    | AI Logic
    |--------------------------------------------------------------------------
     */
    public DIRECTION path() {
        // Chỉ tính toán hướng đi mới khi quái vật nằm gọn trong một ô lưới
        if (enemy.isInATile()) {

            // Trường hợp 1: Nếu khoảng cách bằng 1, tìm hướng tấn công trực tiếp người chơi
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

            // Trường hợp 2: Tìm hướng đi tối ưu (khoảng cách ngắn nhất) thông qua các ô cỏ (Grass)
            int minDistance = INF;
            DIRECTION nowDirection = UP;
            for (int k = 0; k < 4; k++) {
                if (map.getTile(enemy.getTileX() + dx[k], enemy.getTileY() + dy[k]) instanceof Grass) {
                    int curDistance = Distance(enemy.getTileY() + dy[k], enemy.getTileX() + dx[k],
                            player.getTileY(), player.getTileX(), false);

                    // Bỏ qua nếu hướng đi này vướng vật cản
                    if (enemy.checkTileCollider(intToDirection(k), true)) {
                        continue;
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
            // Tiếp tục di chuyển theo hướng hiện tại nếu quái vật chưa vào giữa ô lưới
            return enemy.getDirection();
        }
    }
}