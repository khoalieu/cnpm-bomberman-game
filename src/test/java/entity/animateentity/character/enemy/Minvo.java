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
 * Triển khai logic UC4 với khả năng chịu đựng cao hơn quái vật cơ bản
 */
public class Minvo extends Enemy {

    public Minvo(int x, int y, Sprite sprite) {
        super(x, y, sprite);
        // =============================================================
        // UC4.4: Khởi tạo tập hợp hoạt ảnh (Animation) di chuyển cho Minvo
        // =============================================================
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
        // =============================================================
        // UC4.4: Nếu không va chạm và vẫn còn lượt di chuyển (cntMove)
        // Hệ thống duy trì hướng đi hiện tại của thực thể
        // =============================================================
        if (!enemy.isCollider() && cntMove > 0) {
            cntMove--;
            return enemy.getDirection();
        }

        // =============================================================
        // UC4.2: Hệ thống xác định thuật toán AI là di chuyển ngẫu nhiên (RandomPath)
        // UC4.3: Tính toán và trả về hướng di chuyển mới (x', y')
        // =============================================================
        cntMove = defaultCntMove;
        RandomPath randomPath = new RandomPath(map, map.getPlayer(),this);
        return randomPath.path();
    }

    /*
    |--------------------------------------------------------------------------
    | Collision Logic - Xử lý ngoại lệ UC4.4a
    |--------------------------------------------------------------------------
     */
    public void checkCollision() {
        // UC4.3: Hệ thống xác định tọa độ dự kiến (pixelX + velocity)
        isCollision = false;
        pixelX += this.velocityX;
        pixelY += this.velocityY;

        // =============================================================
        // UC4.4a.1: Hệ thống kiểm tra va chạm với các khối tĩnh (Tường, Gạch)
        // =============================================================
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                Entity entity = map.getTile(j, i);
                if (entity.isBlock() && this.isCollider(entity)) {
                    // UC4.4a.2: Kích hoạt trạng thái va chạm để đổi hướng ở chu kỳ sau
                    isCollision = true;
                }
            }
        }

        // Xác định trạng thái đứng yên khi va chạm hoặc không có vận tốc
        stand = (velocityX == 0 && velocityY == 0) || isCollision;

        // Hoàn tất kiểm tra, trả lại tọa độ thực tế
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
        // =============================================================
        // UC4.5a.3: Khi bị tiêu diệt hoàn toàn, thực thể bị xóa khỏi bộ nhớ
        // =============================================================
        this.remove();
    }
}