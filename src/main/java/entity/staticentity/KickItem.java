package entity.staticentity;

import graphics.Sprite;
import javafx.scene.image.Image;

public class KickItem extends Item {
    /** Thời gian hiệu lực: 20 giây * 60 frame/giây = 1200 frame */
    public static final int EFFECT_DURATION = 20 * 60;

    private static Image customImage;
    
    static {
        try {
            customImage = new Image(KickItem.class.getResourceAsStream("/sprites/powerup_kickbomitem.png"), Sprite.SCALED_SIZE, Sprite.SCALED_SIZE, true, true);
        } catch (Exception e) {
            System.err.println("Cannot load /sprites/powerup_kickbomitem.png: " + e.getMessage());
            customImage = null;
        }
    }

    public KickItem(int x, int y, Sprite sprite) {
        super(x, y, sprite);
    }
    
    @Override
    public void setSprite(Sprite sprite) {
        super.setSprite(sprite);
        // Khi tia lửa làm lộ diện item (Flame.java gán Sprite.powerup_detonator)
        if (sprite == Sprite.powerup_detonator && customImage != null) {
            this.image = customImage;
        }
    }
}
