package path;

import entity.animateentity.character.Bomber;
import entity.animateentity.character.enemy.Enemy;
import map.Map;
import variables.Variables.DIRECTION;

import java.util.ArrayList;
import java.util.Random;

/**
 * Thuật toán di chuyển ngẫu nhiên cho kẻ địch
 */
public class RandomPath extends Path {
    public RandomPath(Map map, Bomber player, Enemy enemy) {
        super(map, player, enemy);
    }

    /*
    |--------------------------------------------------------------------------
    | AI Logic
    |--------------------------------------------------------------------------
     */
    public DIRECTION path() {
        // Kiểm tra nếu quái vật đang va chạm hoặc đang nằm gọn trong một ô lưới
        if (enemy.isCollider() || enemy.isInATile()) {
            ArrayList<DIRECTION> canDirections = new ArrayList<>();

            // Duyệt qua 4 hướng để tìm các hướng không bị vật cản chặn
            for (int k = 0; k < 4; k++) {
                if (!enemy.checkTileCollider(intToDirection(k), false)) {
                    canDirections.add(intToDirection(k));
                }
            }

            // Nếu không có hướng nào khả dụng, giữ nguyên hướng cũ
            if (canDirections.size() == 0) {
                return enemy.getDirection();
            }

            // Chọn ngẫu nhiên một hướng trong danh sách các hướng khả dụng
            int random = new Random().nextInt(canDirections.size());
            return canDirections.get(random);
        } else {
            // Tiếp tục di chuyển theo hướng hiện tại
            return enemy.getDirection();
        }
    }
}