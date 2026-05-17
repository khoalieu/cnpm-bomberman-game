package entity.animateentity.character.enemy;

import entity.animateentity.character.Bomber;
import entity.animateentity.character.Character;
import graphics.Sprite;
import map.Map;

import static variables.Variables.*;

/**
 * Lớp trừu tượng định nghĩa các đặc tính chung cho kẻ địch (Enemy)
 * Thực hiện khung logic cho UC4 - Điều khiển quái vật
 */
public abstract class Enemy extends Character {

    protected int cntMove;
    protected int changeSpeed;
    protected int defaultCntMove;
    protected int defaultChangeSpeed;

    public Enemy(int x, int y, Sprite sprite) {
        super(x, y, sprite);
    }

    public void setCntMove(int cntMove) {
        this.cntMove = cntMove;
    }

    public int getCntMove() {

        return this.cntMove;
    }

    public int getChangeSpeed() {
        return this.changeSpeed;
    }

    public void setChangeSpeed(int changeSpeed) {

        this.changeSpeed = changeSpeed;
    }

    /*
    |--------------------------------------------------------------------------
    | AI & Movement Logic
    |--------------------------------------------------------------------------
     */

    /**
     * UC4.2 & UC4.3: Xác định thuật toán AI và tính toán hướng di chuyển dự kiến.
     * Đây là phương thức trừu tượng để mỗi loại quái (Doll, Balloom,...) tự cài đặt
     * thuật toán riêng (Ngẫu nhiên hoặc Tìm đường).
     */
    public abstract DIRECTION path(Map map, Bomber player, Enemy enemy);

    /**
     * UC4.1: Hệ thống bắt đầu chu kỳ cập nhật hướng và vận tốc
     */
    public void setDirection() {
        // =============================================================
        // UC4.3: Hệ thống xác định hướng di chuyển từ kết quả của thuật toán AI
        // =============================================================
        direction = path(map, map.getPlayer(), this);

        // =============================================================
        // UC4.4: Thiết lập vận tốc (Velocity) dựa trên hướng đã tính toán
        // Nếu không có vật cản (xử lý ở lớp Character), quái sẽ di chuyển tới vị trí mới
        // =============================================================
        switch (direction) {
            case UP -> this.setVelocity(0, -defaultVel);
            case DOWN -> this.setVelocity(0, defaultVel);
            case LEFT -> this.setVelocity(-defaultVel, 0);
            case RIGHT -> this.setVelocity(defaultVel, 0);
            default -> this.setVelocity(0, 0);
        }

        // =============================================================
        // UC4.4 (tiếp): Cập nhật hoạt ảnh (Animation) tương ứng với hướng đi mới
        // =============================================================
        if (animation.containsKey(direction)) {
            currentAnimate = animation.get(direction);
        }
    }
}