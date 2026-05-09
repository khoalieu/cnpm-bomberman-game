package path;

import entity.animateentity.character.Bomber;
import entity.animateentity.character.enemy.Enemy;
import map.Map;
import variables.Variables.DIRECTION;

import static variables.Variables.DIRECTION.*;

/**
 * Thuật toán RightPath: Kết hợp khả năng tấn công trực diện (HeadPath) và ưu tiên rẽ phải khi gặp vật cản
 */
public class RightPath extends Path{
    public RightPath(Map map, Bomber player, Enemy enemy) {
        super(map, player, enemy);
    }
    /*
    |--------------------------------------------------------------------------
    | AI Logic
    |--------------------------------------------------------------------------
     */
    @Override
    public DIRECTION path() {
        // Ưu tiên kiểm tra xem có thể tấn công thẳng vào người chơi hay không
        DIRECTION headPath = new HeadPath(map, player, enemy).path();

        if (headPath == NONE) {
            // Khi không thấy người chơi: Di chuyển ở tốc độ bình thường
            enemy.setSpeed(1);
            if (enemy.isCollider()) {
                // Xử lý va chạm: Kiểm tra ô bên phải, nếu bị chặn thì quay lại bên trái
                if (enemy.checkTileCollider(RIGHT, false)) {
                    return LEFT;
                } else {
                    return RIGHT;
                }
            } else {
                return enemy.getDirection();
            }
        } else {
            // Khi phát hiện người chơi: Tăng tốc độ để truy đuổi trực diện
            enemy.setSpeed(2);
            return headPath;
        }
    }
}