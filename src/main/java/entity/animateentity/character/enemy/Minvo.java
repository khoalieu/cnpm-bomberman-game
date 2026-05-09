package entity.animateentity.character.enemy;

import entity.animateentity.character.Bomber;
import entity.Entity;
import graphics.Sprite;
import map.Map;
import path.RandomPath;
import variables.Variables.DIRECTION;

import static graphics.Sprite.*;
import static variables.Variables.DIRECTION.*;
import static variables.Variables.DIRECTION.RIGHT;
import static variables.Variables.HEIGHT;
import static variables.Variables.WIDTH;

/**
 * Thực thể Minvo: Loại quái vật có 2 mạng và di chuyển ngẫu nhiên
 */
public class Minvo extends Enemy {

    public Minvo(int x, int y, Sprite sprite) {
        super(x, y, sprite);
        // Khởi tạo tập hợp hoạt ảnh di chuyển và bị tiêu diệt
        animation.put(LEFT, MINVO_LEFT);
        animation.put(UP, MINVO_LEFT);
        animation.put(RIGHT, MINVO_RIGHT);
        animation.put(DOWN, MINVO_RIGHT);
        animation.put(DESTROYED, MINVO_DESTROYED);
        // Thiết lập trạng thái ban đầu và chỉ số cơ bản (có 2 mạng)
        currentAnimate = animation.get(UP);
        this.direction = UP;
        this.defaultVel = 1;
        this.speed = 1;
        this.defaultCntMove = 5;
        this.life = 2;
    }
    /*
    |--------------------------------------------------------------------------
    | AI Logic
    |--------------------------------------------------------------------------
     */
    @Override
    public DIRECTION path(Map map, Bomber player, Enemy enemy) {
        // Tiếp tục hướng cũ nếu không va chạm và vẫn còn lượt di chuyển
        if (!enemy.isCollider() && cntMove > 0) {
            cntMove--;
            return enemy.getDirection();
        }
        // Reset bộ đếm và chọn hướng di chuyển ngẫu nhiên mới
        cntMove = defaultCntMove;
        RandomPath randomPath = new RandomPath(map, map.getPlayer(),this);
        return randomPath.path();
    }

    /*
    |--------------------------------------------------------------------------
    | Collision Logic
    |--------------------------------------------------------------------------
     */
    public void checkCollision() {
        isCollision = false;
        pixelX += this.velocityX;
        pixelY += this.velocityY;

        // Kiểm tra va chạm với các khối tĩnh trên bản đồ
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                Entity entity = map.getTile(j, i);
                if (entity.isBlock() && this.isCollider(entity)) {
                    isCollision = true;
                }
            }
        }

        // Xác định trạng thái đứng yên khi va chạm hoặc không có vận tốc
        stand = (velocityX == 0 && velocityY == 0) || isCollision;
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