package entity.animateentity.character.enemy;

import entity.animateentity.character.Bomber;
import graphics.Sprite;
import map.Map;
import path.RandomPath;
import variables.Variables.DIRECTION;

import static variables.Variables.DIRECTION.*;
import static graphics.Sprite.*;

/**
 * Thực thể Balloom: Loại quái vật cơ bản di chuyển ngẫu nhiên
 */
public class Balloom extends Enemy {

    public Balloom(int x, int y, Sprite sprite) {
        super(x, y, sprite);

        // Khởi tạo tập hợp các hoạt ảnh (Animation)
        animation.put(LEFT, BALLOOM_LEFT);
        animation.put(UP, BALLOOM_LEFT);
        animation.put(RIGHT, BALLOOM_RIGHT);
        animation.put(DOWN, BALLOOM_RIGHT);
        animation.put(DESTROYED, BALLOOM_DESTROYED);

        // Thiết lập trạng thái ban đầu và chỉ số cơ bản
        currentAnimate = animation.get(UP);
        this.direction = UP;
        this.defaultVel = 1;
        this.speed = 1;
        this.defaultCntMove = 5;
    }

    /*
    |--------------------------------------------------------------------------
    | AI Logic
    |--------------------------------------------------------------------------
     */
    @Override
    public DIRECTION path(Map map, Bomber player, Enemy enemy) {
        // Nếu không va chạm và vẫn còn lượt di chuyển (cntMove), tiếp tục hướng cũ
        if (!enemy.isCollider() && cntMove > 0) {
            cntMove--;
            return enemy.getDirection();
        }

        // Reset bộ đếm và chọn hướng di chuyển mới ngẫu nhiên
        cntMove = defaultCntMove;
        RandomPath randomPath = new RandomPath(map, map.getPlayer(), this);
        return randomPath.path();
    }

    /*
    |--------------------------------------------------------------------------
    | Methods
    |--------------------------------------------------------------------------
     */
    @Override
    public void delete() {
        // Thực hiện xóa thực thể khỏi bản đồ
        this.remove();
    }
}