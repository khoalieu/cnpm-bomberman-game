package entity.animateentity.character.enemy;

import entity.animateentity.character.Bomber;
import graphics.Sprite;
import map.Map;
import path.RightPath;
import variables.Variables.DIRECTION;

import static variables.Variables.DIRECTION.*;

/**
 * Thực thể Doll: Loại quái vật có 2 mạng và khả năng tăng tốc khi di chuyển
 * Kế thừa từ Enemy - Lớp cha xử lý UC4.1 (Update Loop)
 */
public class Doll extends Enemy {

    public Doll(int x, int y, Sprite sprite) {
        super(x, y, sprite);

        // =============================================================
        // UC4.4: Thiết lập tập hợp hoạt ảnh (Animation) cho quái vật
        // =============================================================
        animation.put(UP, Sprite.DOLL_LEFT);
        animation.put(DOWN, Sprite.DOLL_RIGHT);
        animation.put(LEFT, Sprite.DOLL_LEFT);
        animation.put(RIGHT, Sprite.DOLL_RIGHT);
        animation.put(DESTROYED, Sprite.DOLL_DESTROYED);

        // Khởi tạo chỉ số cơ bản
        life = 2;
        this.direction = RIGHT;
        this.defaultVel = 1;
        this.speed = 1;
        this.defaultCntMove = 5;
        this.defaultChangeSpeed = 10;
    }

    /*
    |--------------------------------------------------------------------------
    | AI Logic - Xử lý quyết định hướng đi
    |--------------------------------------------------------------------------
     */
    @Override
    public DIRECTION path(Map map, Bomber player, Enemy enemy) {
        // =============================================================
        // UC4.2: Hệ thống xác định thuật toán AI (Ở đây là RightPath)
        // =============================================================
        setSpeed(3);

        // =============================================================
        // UC4.3: Hệ thống tính toán hướng di chuyển dự kiến
        // =============================================================
        RightPath rightPath = new RightPath(map, player, enemy);
        return rightPath.path();
    }

    /*
    |--------------------------------------------------------------------------
    | Methods - Xử lý trạng thái thực thể
    |--------------------------------------------------------------------------
     */
    @Override
    public void delete() {
        // =============================================================
        // UC4.5a: Quái vật bị tiêu diệt bởi tia lửa (Flame)
        // =============================================================

        // UC4.5a.2: Giảm mạng và chuẩn bị thực hiện hoạt ảnh nổ/chết
        life --;
        destroyed = false; // Reset trạng thái chờ để thực hiện animation chết

        if (life == 0) {
            // UC4.5a.3: Thực thể bị xóa khỏi bộ nhớ (Map) khi hết mạng
            this.remove();
        }
    }
}