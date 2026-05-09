package entity.animateentity.character.enemy;

import entity.animateentity.character.Bomber;
import entity.Entity;
import entity.staticentity.Wall;
import graphics.Sprite;
import map.Map;
import path.DodgePath;
import variables.Variables.DIRECTION;

import static graphics.Sprite.*;
import static variables.Variables.DIRECTION.*;
import static variables.Variables.DIRECTION.RIGHT;
import static variables.Variables.HEIGHT;
import static variables.Variables.WIDTH;

/**
 * Thực thể Kondoria: Loại quái vật có khả năng đi xuyên tường gạch và né tránh bom
 */
public class Kondoria extends Enemy {
    public Kondoria(int x, int y, Sprite sprite) {
        super(x, y, sprite);

        // Khởi tạo tập hợp hoạt ảnh di chuyển và bị tiêu diệt
        animation.put(LEFT, KONDORIA_LEFT);
        animation.put(UP, KONDORIA_LEFT);
        animation.put(RIGHT, KONDORIA_RIGHT);
        animation.put(DOWN, KONDORIA_RIGHT);
        animation.put(DESTROYED, KONDORIA_DESTROYED);

        // Thiết lập trạng thái ban đầu và các chỉ số cơ bản
        currentAnimate = animation.get(UP);
        this.direction = UP;
        this.defaultVel = 1;
        this.speed = 1;
        this.defaultCntMove = 5;
        this.life = 1;
    }

    /*
    |--------------------------------------------------------------------------
    | AI Logic
    |--------------------------------------------------------------------------
     */
    @Override
    public DIRECTION path(Map map, Bomber player, Enemy enemy) {
        // Sử dụng thuật toán né tránh (DodgePath) để tìm đường di chuyển an toàn
        DodgePath dodgePath = new DodgePath(map, map.getPlayer(), this);
        return dodgePath.path();
    }

    /*
    |--------------------------------------------------------------------------
    | Collision Logic
    |--------------------------------------------------------------------------
     */
    @Override
    public void checkCollision() {
        isCollision = false;
        pixelX += this.velocityX;
        pixelY += this.velocityY;

        // Kiểm tra va chạm với các thực thể tĩnh trên bản đồ
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                Entity entity = map.getTile(j, i);
                // Kondoria chỉ bị chặn bởi Tường cứng (Wall), có thể xuyên qua Gạch (Brick)
                if (entity.isBlock() && this.isCollider(entity) && (entity instanceof Wall)) {
                    isCollision = true;
                }
            }
        }

        // Kiểm tra va chạm với danh sách bom hiện có trên bản đồ
        map.getBombs().forEach(bomb -> {
            Entity entity1 = bomb;
            if (entity1.isBlock() && this.isCollider(entity1)) {
                isCollision = true;
            }
            if(this.isCollider(entity1) && this instanceof Enemy) {
                isCollision = true;
            }
        });

        pixelX -= this.velocityX;
        pixelY -= this.velocityY;
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