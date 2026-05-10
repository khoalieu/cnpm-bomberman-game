package entity.animateentity.character.enemy;

import entity.animateentity.character.Bomber;
import graphics.Sprite;
import map.Map;
import path.SpeedDistancePath;
import variables.Variables.DIRECTION;

import static variables.Variables.DIRECTION.*;

/**
 * Thực thể Oneal: Loại quái vật có trí tuệ nhân tạo nâng cao
 * Triển khai logic UC4 với khả năng thay đổi tốc độ và bám đuổi người chơi
 */
public class Oneal extends Enemy{
    public Oneal(int x, int y, Sprite sprite) {
        super(x, y, sprite);

        // =============================================================
        // UC4.4: Khởi tạo tập hợp hoạt ảnh (Animation) di chuyển cho Oneal
        // =============================================================
        animation.put(UP, Sprite.ONEAL_LEFT);
        animation.put(DOWN, Sprite.ONEAL_RIGHT);
        animation.put(LEFT, Sprite.ONEAL_LEFT);
        animation.put(RIGHT, Sprite.ONEAL_RIGHT);
        animation.put(DESTROYED, Sprite.ONEAL_DESTROYED);

        // Thiết lập các chỉ số vận tốc cơ bản và bộ đếm trạng thái
        this.direction = UP;
        this.defaultVel = 1;
        this.speed = 1;
        this.defaultCntMove = 5;
        this.defaultChangeSpeed = 10;
    }

    /*
    |--------------------------------------------------------------------------
    | AI Logic - Xử lý trí tuệ nhân tạo
    |--------------------------------------------------------------------------
     */
    @Override
    public DIRECTION path(Map map, Bomber player, Enemy enemy){
        // =========================================================================
        // UC4.2: Hệ thống xác định thuật toán AI nâng cao (SpeedDistancePath)
        // Thuật toán này cho phép Oneal thay đổi vận tốc dựa trên vị trí của Bomber
        // =========================================================================
        SpeedDistancePath speedRandomPath = new SpeedDistancePath(map, player, enemy);

        // =============================================================
        // UC4.3: Tính toán hướng di chuyển và xác định tọa độ dự kiến (x', y')
        // =============================================================
        DIRECTION _direction = speedRandomPath.path();
        return _direction;
    }

    /*
    |--------------------------------------------------------------------------
    | Methods - Xử lý trạng thái thực thể
    |--------------------------------------------------------------------------
     */
    @Override
    public void delete() {
        // =============================================================
        // UC4.5a.3: Thực thể quái vật bị xóa khỏi bộ nhớ sau khi bị tiêu diệt
        // =============================================================
        this.remove();
    }
}