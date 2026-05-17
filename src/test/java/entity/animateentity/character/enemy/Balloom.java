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
 * Triển khai các bước trong UC4 dành cho quái vật cấp thấp
 */
public class Balloom extends Enemy {

    public Balloom(int x, int y, Sprite sprite) {
        super(x, y, sprite);

        // =============================================================
        // UC4.4: Khởi tạo tập hợp các hoạt ảnh (Animation) cho Balloom
        // =============================================================
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
        // =============================================================
        // UC4.4a.1: Kiểm tra nếu không va chạm vật cản và vẫn còn lượt di chuyển
        // Hệ thống duy trì hướng đi hiện tại (UC4.4)
        // =============================================================
        if (!enemy.isCollider() && cntMove > 0) {
            cntMove--;
            return enemy.getDirection();
        }

        // =============================================================
        // UC4.4a.2: Khi gặp vật cản hoặc hết lượt di chuyển, hệ thống kích hoạt
        // logic chọn hướng mới (ở đây là ngẫu nhiên)
        // =============================================================
        cntMove = defaultCntMove;

        // UC4.2: Xác định thuật toán AI là di chuyển ngẫu nhiên (RandomPath)
        // UC4.3: Tính toán hướng di chuyển mới dựa trên thuật toán đã chọn
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
        // =============================================================
        // UC4.5a.3: Quái vật bị tia lửa tiêu diệt, thực thể bị xóa khỏi bộ nhớ
        // =============================================================
        this.remove();
    }
}