package entity.animateentity;

import entity.Entity;
import entity.staticentity.*;
import graphics.Sprite;
import variables.Variables;

import static variables.Variables.FLAME_SHAPE.*;

public class Flame extends AnimateEntity {
    public static int flameLength = 1;
    protected int flameShape = 0;

    public Flame(int x, int y, Sprite sprite, Variables.FLAME_SHAPE fs) {
        super(x, y, sprite);
        animation.put(BOMB_EXPLODED, Sprite.BOMB_EXPLODED);
        animation.put(VERTICAL, Sprite.EXPLOSION_VERTICAL);
        animation.put(HORIZONTAL, Sprite.EXPLOSION_HORIZONTAL);
        animation.put(HORIZONTAL_LEFT_LAST, Sprite.EXPLOSION_HORIZONTAL_LEFT_LAST);
        animation.put(HORIZONTAL_RIGHT_LAST, Sprite.EXPLOSION_HORIZONTAL_RIGHT_LAST);
        animation.put(VERTICAL_TOP_LAST, Sprite.EXPLOSION_VERTICAL_TOP_LAST);
        animation.put(VERTICAL_DOWN_LAST, Sprite.EXPLOSION_VERTICAL_DOWN_LAST);
        currentAnimate = animation.get(fs);
    }

    @Override
    public void update() {
        checkCollison();
        updateAnimation();
        updateDestroyAnimation();
    }

    // ==============================
    // UC3.9: Vụ nổ kết thúc, hệ thống dọn dẹp các đối tượng Bom và Tia lửa khỏi bộ nhớ
    // ==============================
    @Override
    public void updateDestroyAnimation() {
        checkCollison();
        if (timeDestroy == 0) {
            delete();
        } else {
            timeDestroy--;
            updateAnimation();
        }
    }

    // ==============================
    // UC3.8: Hệ thống kiểm tra va chạm của tia lửa với các ô trên bản đồ
    // ==============================
    public void interactWith(Entity entity) {
        // ==============================
        // UC3.8a.1. Nếu chạm Tường mềm (Brick): Hệ thống phá gạch, chặn tia lửa.
        // ==============================
        // (Lưu ý: Logic "chặn tia lửa" đã được xử lý bằng cờ false trong vòng lặp class Bomb. Tại đây chỉ xử lý "phá hủy gạch")
        if (entity instanceof Brick) {
            ((Brick) entity).destroyed = true;
        } else if (entity instanceof Item) {
            entity.setBlock(false);
            if (entity instanceof SpeedItem) {
                entity.setSprite(Sprite.powerup_speed);
            } else if (entity instanceof BombItem) {
                entity.setSprite(Sprite.powerup_bombs);
            } else if (entity instanceof FlameItem) {
                entity.setSprite(Sprite.powerup_flames);
            }
        } else if (entity instanceof Portal) {
            entity.setBlock(false);
            entity.setSprite(Sprite.portal);
        }
    }

    public void checkCollison() {
        // ==============================
        // UC3.8a.2. Nếu chạm Quái vật (Enemy): Hệ thống tiêu diệt quái vật.
        // ==============================
        map.getEnemies().forEach(enemy -> {
            if (this.isCollider(enemy)) {
                enemy.destroy();
            }
        });
        // ==============================
        // UC3.8a.3. Nếu chạm Người chơi (Player): Hệ thống trừ mạng người chơi
        // ==============================
        if (this.isCollider(map.getPlayer()) && map.getPlayer().getImmortal() == 0 && !map.getPlayer().isDestroyed()) {
            map.getPlayer().destroy();
        }
    }

    @Override
    public void delete() {
        this.remove();
    }
}