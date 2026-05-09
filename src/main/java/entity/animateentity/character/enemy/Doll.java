package entity.animateentity.character.enemy;

import entity.animateentity.character.Bomber;
import graphics.Sprite;
import map.Map;
import path.RightPath;
import variables.Variables.DIRECTION;

import static variables.Variables.DIRECTION.*;

/**
 * Thực thể Doll: Loại quái vật có 2 mạng và khả năng tăng tốc khi di chuyển
 */
public class Doll extends Enemy {
    public Doll(int x, int y, Sprite sprite) {
        super(x, y, sprite);

        // Thiết lập tập hợp hoạt ảnh di chuyển và bị tiêu diệt
        animation.put(UP, Sprite.DOLL_LEFT);
        animation.put(DOWN, Sprite.DOLL_RIGHT);
        animation.put(LEFT, Sprite.DOLL_LEFT);
        animation.put(RIGHT, Sprite.DOLL_RIGHT);
        animation.put(DESTROYED, Sprite.DOLL_DESTROYED);

        // Khởi tạo chỉ số: 2 mạng, hướng phải, tốc độ cơ bản và các bộ đếm di chuyển
        life = 2;
        this.direction = RIGHT;
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
    public DIRECTION path(Map map, Bomber player, Enemy enemy) {
        // Tăng tốc độ và sử dụng thuật toán tìm đường RightPath
        setSpeed(3);
        RightPath rightPath = new RightPath(map, player, enemy);
        return rightPath.path();
    }

    /*
    |--------------------------------------------------------------------------
    | Methods
    |--------------------------------------------------------------------------
     */
    public void delete() {
        // Giảm số mạng hiện có; chỉ thực hiện xóa thực thể khi số mạng về 0
        life --;
        destroyed = false;
        if (life == 0) {
            this.remove();
        }
    }
}