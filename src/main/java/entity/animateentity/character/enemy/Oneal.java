package entity.animateentity.character.enemy;

import entity.animateentity.character.Bomber;
import graphics.Sprite;
import map.Map;
import path.SpeedDistancePath;
import variables.Variables.DIRECTION;

import static variables.Variables.DIRECTION.*;

/**
 * Thực thể Oneal: Loại quái vật có trí tuệ nhân tạo nâng cao, có khả năng thay đổi tốc độ và bám đuổi người chơi
 */
public class Oneal extends Enemy{
    public Oneal(int x, int y, Sprite sprite) {
        super(x, y, sprite);

        // Khởi tạo tập hợp hoạt ảnh di chuyển và trạng thái bị tiêu diệt
        animation.put(UP, Sprite.ONEAL_LEFT);
        animation.put(DOWN, Sprite.ONEAL_RIGHT);
        animation.put(LEFT, Sprite.ONEAL_LEFT);
        animation.put(RIGHT, Sprite.ONEAL_RIGHT);
        animation.put(DESTROYED, Sprite.ONEAL_DESTROYED);

        // Thiết lập các chỉ số vận tốc cơ bản và bộ đếm thay đổi trạng thái
        this.direction = UP;
        this.defaultVel = 1;
        this.speed = 1;
        this.defaultCntMove = 5;
        this.defaultChangeSpeed = 10;
    }

    /*
    |--------------------------------------------------------------------------
    | AI Logic
    |--------------------------------------------------------------------------
     */
    public DIRECTION path(Map map, Bomber player, Enemy enemy){
        // Sử dụng thuật toán tìm đường dựa trên tốc độ và khoảng cách tới người chơi
        SpeedDistancePath speedRandomPath = new SpeedDistancePath(map, player, enemy);
        DIRECTION _direction = speedRandomPath.path();
        return _direction;
    }

    /*
    |--------------------------------------------------------------------------
    | Methods
    |--------------------------------------------------------------------------
     */
    @Override
    public void delete() {
        // Thực hiện xóa thực thể khỏi bản đồ khi bị tiêu diệt
        this.remove();
    }
}