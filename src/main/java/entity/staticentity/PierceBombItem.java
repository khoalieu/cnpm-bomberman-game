package entity.staticentity;

import graphics.Sprite;
import javafx.scene.image.Image;

/**
 * Vật phẩm Bom Xuyên Thấu (Pierce Bomb).
 * Khi nhặt được, người chơi có khả năng đặt bom mà tia lửa
 * có thể xuyên qua nhiều viên gạch mềm (Brick) liên tiếp thay vì dừng lại ở viên đầu tiên.
 * Hiệu lực kéo dài 20 giây (20s x 60 frame = 1200 frame).
 */
public class PierceBombItem extends Item {

    /** Thời gian hiệu lực: 20 giây * 60 frame/giây = 1200 frame */
    public static final int EFFECT_DURATION = 20 * 60;

    private static Image customImage;

    static {
        try {
            customImage = new Image(
                PierceBombItem.class.getResourceAsStream("/sprites/powerup_piercebomb.png"),
                Sprite.SCALED_SIZE, Sprite.SCALED_SIZE, true, true
            );
        } catch (Exception e) {
            System.err.println("Cannot load /sprites/powerup_piercebomb.png: " + e.getMessage());
            customImage = null;
        }
    }

    public PierceBombItem(int x, int y, Sprite sprite) {
        super(x, y, sprite);
    }

    /**
     * Khi tia lửa (Flame) làm lộ diện item và gán Sprite.powerup_piercebomb,
     * override để thay bằng ảnh PNG thực tế thay vì dùng sprite từ SpriteSheet.
     */
    @Override
    public void setSprite(Sprite sprite) {
        super.setSprite(sprite);
        if (sprite == Sprite.powerup_piercebomb && customImage != null) {
            this.image = customImage;
        }
    }

    @Override
    public void update() {
        // Logic kích hoạt được xử lý trong Bomber.handleItemCollision()
    }
}

