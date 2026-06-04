package entity.animateentity.character.enemy;

import entity.animateentity.character.Bomber;
import entity.animateentity.character.Character;
import graphics.Sprite;
import map.Map;
import variables.Variables.DIRECTION;
import graphics.Sprite;// uc4+
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
    // uc4+
    //// ========================================================================
    // UC4.6a - Kích hoạt trạng thái tức giận khi còn một quái vật cuối cùng
    // ========================================================================
    protected boolean enraged = false;
    // UC4.6a.4 - Hệ thống cập nhật hoạt ảnh của quái vật sang phiên bản tức giận
    protected java.util.HashMap<Enum, Sprite[]> normalAnimation = new java.util.HashMap<>();
    protected java.util.HashMap<Enum, Sprite[]> enragedAnimation = new java.util.HashMap<>();

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

    // uc4+
    // UC4.7a - Quái vật ở trạng thái tức giận bị trúng tia lửa
    public boolean isEnraged() {
        return enraged;
    }

    // ========================================================================
    // UC4.6a.4 - Hệ thống cập nhật hoạt ảnh của quái vật sang phiên bản tức giận.
    // Sprite của quái vật được chuyển sang tông đỏ/cam, đồng thời hiển thị hiệu ứng
    // lửa đỏ
    // bao quanh cơ thể và biểu cảm khuôn mặt tức giận.
    // ========================================================================
    protected void prepareEnragedAnimation() {
        normalAnimation.clear();
        enragedAnimation.clear();

        for (Enum key : animation.keySet()) {
            Sprite[] normalSprites = animation.get(key);

            normalAnimation.put(key, normalSprites);

            if (key == DIRECTION.DESTROYED) {
                enragedAnimation.put(key, normalSprites);
            } else {
                enragedAnimation.put(key, Sprite.createEnragedAnimation(normalSprites));
            }
        }
    }

    // ========================================================================
    // UC4.6a - Kích hoạt trạng thái tức giận khi còn một quái vật cuối cùng
    // ========================================================================
    public void becomeEnraged() {
        // UC4.6a.5 - Trạng thái Enraged chỉ được kích hoạt một lần trong mỗi màn chơi
        // nhằm tránh việc quái vật bị tăng mạng hoặc tăng tốc nhiều lần.
        if (enraged || isDestroyed() || isRemoved())
            return;
        // UC4.6a.4 - Hệ thống cập nhật hoạt ảnh của quái vật sang phiên bản tức giận.
        prepareEnragedAnimation();
        // UC4.6a.3 - Hệ thống chuyển quái vật cuối cùng sang trạng thái Enraged.
        enraged = true;

        // UC4.6a.3 - Ở trạng thái này, quái vật được tăng số mạng lên tối thiểu 3 mạng.
        this.life = Math.max(this.life, 3);

        // UC4.6a.3 - Ở trạng thái này, quái vật được tăng tốc độ di chuyển.
        this.speed += 1;
        // UC4.6a.4 - Hệ thống cập nhật hoạt ảnh của quái vật sang phiên bản tức giận.
        useEnragedAnimation();
    }

    // UC4.6a.4 - Hệ thống cập nhật hoạt ảnh của quái vật sang phiên bản tức giận
    protected void useEnragedAnimation() {
        for (Enum key : enragedAnimation.keySet()) {
            animation.put(key, enragedAnimation.get(key));
        }

        if (animation.containsKey(direction)) {
            currentAnimate = animation.get(direction);
        }
    }

    /*
     * |--------------------------------------------------------------------------
     * | AI & Movement Logic
     * |--------------------------------------------------------------------------
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
        // Nếu không có vật cản (xử lý ở lớp Character), quái sẽ di chuyển tới vị trí
        // mới
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

    // ========================================================================
    // UC4.5a - Quái vật bị tiêu diệt bởi tia lửa (Flame)
    // UC4.7a - Quái vật ở trạng thái tức giận bị trúng tia lửa
    // ========================================================================
    @Override
    public void delete() {
        // UC4.7a.1 - Nếu quái vật cuối cùng đang ở trạng thái Enraged bị Flame tác
        // động,
        // hệ thống không xóa quái vật ngay lập tức mà thực hiện trừ một mạng sống của
        // quái vật.
        if (enraged) {
            life--;
            // UC4.7a.3 - Nếu số mạng của quái vật giảm về 0, hệ thống thực hiện xóa thực
            // thể quái vật khỏi bộ nhớ quản lý.
            if (life <= 0) {
                this.remove();
            } else {
                // UC4.7a.2 - Nếu số mạng của quái vật sau khi bị trừ vẫn lớn hơn 0,
                // hệ thống hủy trạng thái destroyed tạm thời và cho quái vật tiếp tục tồn tại
                // trên bản đồ.
                destroyed = false;

                if (animation.containsKey(direction)) {
                    currentAnimate = animation.get(direction);
                }
            }

            return;
        }

        // UC4.5a.3 - Thực thể quái vật bị xóa khỏi bộ nhớ;
        // hệ thống cập nhật điểm số và kiểm tra điều kiện mở Portal.
        this.remove();
    }

    // uc4+
    //// ========================================================================
    // UC4.6a.4 - Sprite của quái vật được chuyển sang tông đỏ/cam, đồng thời hiển
    // thị
    // hiệu ứng lửa đỏ bao quanh cơ thể và biểu cảm khuôn mặt tức giận.
    // ========================================================================
    @Override
    // UC4.6a.4 - Hiển thị hiệu ứng lửa đỏ bao quanh cơ thể quái vật
    public void render(GraphicsContext graphicsContext) {
        if (enraged && !isDestroyed() && !isRemoved()) {
            // code render aura ở đây
            renderEnragedAura(graphicsContext);
        }

        super.render(graphicsContext);
    }

    private void renderEnragedAura(GraphicsContext graphicsContext) {
        double screenX = pixelX - map.getRenderX();
        double screenY = pixelY - map.getRenderY();

        double pulse = Math.sin(game.MainGame.time * 0.25) * 3;

        graphicsContext.save();

        // Lớp lửa đỏ phía ngoài
        graphicsContext.setGlobalAlpha(0.35);
        graphicsContext.setFill(Color.RED);
        graphicsContext.fillOval(screenX - 6 - pulse, screenY - 8 - pulse,
                44 + pulse * 2, 48 + pulse * 2);

        // Lớp lửa cam phía trong
        graphicsContext.setGlobalAlpha(0.45);
        graphicsContext.setFill(Color.ORANGE);
        graphicsContext.fillOval(screenX - 3, screenY - 5,
                38 + pulse, 42 + pulse);

        // Các đốm lửa nhỏ xung quanh người
        graphicsContext.setGlobalAlpha(0.75);
        graphicsContext.setFill(Color.RED);
        graphicsContext.fillOval(screenX - 4, screenY + 4 + Math.sin(game.MainGame.time * 0.3) * 3, 8, 12);
        graphicsContext.fillOval(screenX + 28, screenY + 6 + Math.cos(game.MainGame.time * 0.25) * 3, 8, 12);

        graphicsContext.setFill(Color.ORANGE);
        graphicsContext.fillOval(screenX + 5, screenY - 6 + Math.sin(game.MainGame.time * 0.35) * 3, 8, 14);
        graphicsContext.fillOval(screenX + 19, screenY - 8 + Math.cos(game.MainGame.time * 0.3) * 3, 8, 14);

        graphicsContext.restore();
    }

}