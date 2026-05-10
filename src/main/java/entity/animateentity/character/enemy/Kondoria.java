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
 * Triển khai UC4 với khả năng xử lý va chạm môi trường đặc biệt
 */
public class Kondoria extends Enemy {
    public Kondoria(int x, int y, Sprite sprite) {
        super(x, y, sprite);

        // =============================================================
        // UC4.4: Khởi tạo tập hợp hoạt ảnh (Animation) cho Kondoria
        // =============================================================
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
        // =============================================================
        // UC4.2: Hệ thống xác định thuật toán AI né tránh (DodgePath)
        // UC4.3: Tính toán hướng di chuyển an toàn dựa trên vị trí Bom và Bomber
        // =============================================================
        DodgePath dodgePath = new DodgePath(map, map.getPlayer(), this);
        return dodgePath.path();
    }

    /*
    |--------------------------------------------------------------------------
    | Collision Logic - Xử lý ngoại lệ UC4.4a
    |--------------------------------------------------------------------------
     */
    @Override
    public void checkCollision() {
        // UC4.4: Giả lập di chuyển tới tọa độ dự kiến (x', y') để kiểm tra va chạm
        isCollision = false;
        pixelX += this.velocityX;
        pixelY += this.velocityY;

        // =========================================================================
        // UC4.4a.1: Hệ thống kiểm tra va chạm với Tường cứng (Wall)
        // Lưu ý: Kondoria không bị chặn bởi Brick (Tường gạch) theo đặc tính thực thể
        // =========================================================================
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                Entity entity = map.getTile(j, i);

                if (entity.isBlock() && this.isCollider(entity) && (entity instanceof Wall)) {
                    // UC4.4a.2: Hệ thống xác định trạng thái va chạm, dừng di chuyển hướng này
                    isCollision = true;
                }
            }
        }

        // =========================================================================
        // UC4.4a.1 (tiếp): Kiểm tra va chạm với Bom (Kondoria không thể xuyên qua Bom)
        // =========================================================================
        map.getBombs().forEach(bomb -> {
            Entity entity1 = bomb;
            if (entity1.isBlock() && this.isCollider(entity1)) {
                isCollision = true;
            }
            if(this.isCollider(entity1) && this instanceof Enemy) {
                isCollision = true;
            }
        });

        // Hoàn tất kiểm tra, trả lại tọa độ thực tế để chờ cập nhật chính thức
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
        // UC4.5a.3: Quái vật bị tiêu diệt, xóa thực thể khỏi bộ nhớ hệ thống
        // =============================================================
        this.remove();
    }
}