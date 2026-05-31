package entity.animateentity;

import entity.Entity;
import entity.staticentity.*;
import graphics.Sprite;
import variables.Variables;
import map.Map;

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
        // ==============================
        // UC3.8: Mỗi frame, hệ thống kiểm tra va chạm của tia lửa với tất cả các thực thể trên bản đồ.
        // ==============================
        checkCollison();
        updateAnimation();
        updateDestroyAnimation();
    }

    // ==============================
    // UC3.9: Vụ nổ kết thúc – hệ thống dọn dẹp các đối tượng Tia lửa (Flame) khỏi bộ nhớ.
    //        timeDestroy đếm ngược mỗi frame; khi về 0, gọi delete() để xóa tia lửa.
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
    // UC3.8: Hệ thống kiểm tra va chạm của tia lửa với các ô trên bản đồ và xử lý tương tác phụ.
    //        Hàm này được gọi từ Bomb.checkWallCollision() khi tia lửa chạm một ô không phải Grass.
    // ==============================
    public void interactWith(Entity entity) {
        if (entity instanceof Brick) {
            // ==============================
            // UC3.8a.1: [LUỒNG NGOẠI LỆ] Nếu chạm Tường mềm (Brick):
            //   - Hệ thống phá gạch: gán cờ destroyed = true → Brick.update() sẽ phát hoạt ảnh nổ gạch.
            //   - Logic "chặn tia lửa" được xử lý bằng cờ isPierce trong Bomb.checkWallCollision().
            // ==============================
            ((Brick) entity).destroyed = true;

        } else if (entity instanceof Item) {
            if (!entity.isBlock()) {
                // ==============================
                // UC3.8a.5: [LUỒNG NGOẠI LỆ] Nếu chạm Vật phẩm đã lộ diện (Item với block = false):
                //   Ở màn chơi số 1, nếu tia lửa chạm vào vật phẩm đang hiển thị bình thường trên bản đồ,
                //   hệ thống thiêu rụi và xóa vĩnh viễn vật phẩm đó khỏi trò chơi.
                // ==============================
                destroyItemWhenBombExplodes(entity);
            } else {
                // ==============================
                // UC3.8a.1 (tiếp): Nếu bên dưới viên gạch có ẩn chứa một Vật phẩm (Item với block = true),
                //   hệ thống mở khóa (setBlock(false)) và hiển thị hình ảnh của vật phẩm đó lên bản đồ.
                //   (VD: KickItem, PierceBombItem, SpeedItem, BombItem, FlameItem, ...)
                // ==============================
                entity.setBlock(false);
                if (entity instanceof SpeedItem) {
                    entity.setSprite(Sprite.powerup_speed);
                } else if (entity instanceof BombItem) {
                    entity.setSprite(Sprite.powerup_bombs);
                } else if (entity instanceof FlameItem) {
                    entity.setSprite(Sprite.powerup_flames);
                } else if (entity instanceof WallPassItem) {
                    entity.setSprite(Sprite.powerup_wallpass);
                } else if (entity instanceof BombPassItem) {
                    entity.setSprite(Sprite.powerup_bombpass);
                } else if (entity instanceof FlamePassItem) {
                    entity.setSprite(Sprite.powerup_flamepass);
                } else if (entity instanceof MysteryItem) {
                    entity.setSprite(Sprite.powerup_mystery);
                } else if (entity instanceof KickItem) {
                    entity.setSprite(Sprite.powerup_detonator); // Dùng tạm sprite detonator cho KickItem
                } else if (entity instanceof PierceBombItem) {
                    entity.setSprite(Sprite.powerup_piercebomb);
                }
            }
        } else if (entity instanceof Portal) {
            entity.setBlock(false);
            entity.setSprite(Sprite.portal);
        }
    }

    public void checkCollison() {
        // ==============================
        // UC3.8a.2: [LUỒNG NGOẠI LỆ] Nếu tia lửa chạm Quái vật (Enemy):
        //           Hệ thống tiêu diệt quái vật bằng cách gọi enemy.destroy().
        // ==============================
        map.getEnemies().forEach(enemy -> {
            if (this.isCollider(enemy)) {
                enemy.destroy();
            }
        });

        // ==============================
        // UC3.8a.3: [LUỒNG NGOẠI LỆ] Nếu tia lửa chạm Người chơi (Player):
        //           Hệ thống kiểm tra trạng thái miễn nhiễm trước khi trừ mạng.
        // ==============================
        // =============================================================
        // UC2.4c.1. Nếu hệ thống phát hiện tọa độ đè lên Quái vật (Enemy) hoặc vùng Lửa nổ (Flame),
        // hệ thống kiểm tra cờ trạng thái Bất tử (Invincible).
        // =============================================================
        if (this.isCollider(map.getPlayer()) && map.getPlayer().getImmortal() == 0 && !map.getPlayer().isDestroyed()) {
            if (map.getPlayer().hasShield || map.getPlayer().isFlamePass) {
                // =============================================================
                // UC2.4c.2. Nếu nhân vật đang bất tử, hệ thống bỏ qua sát thương, nhân vật an toàn.
                // =============================================================
            } else {
                // =============================================================
                // UC2.4c.3. Nếu nhân vật không bất tử, hệ thống chuyển nhân vật sang trạng thái
                // bị tiêu diệt, trừ mạng và kích hoạt luồng hồi sinh. Luồng di chuyển bị hủy bỏ.
                // =============================================================
                map.getPlayer().destroy();
            }
        }
    }

    @Override
    public void delete() {
        // ==============================
        // UC3.9: Dọn dẹp đối tượng Tia lửa (Flame) khỏi danh sách quản lý bản đồ.
        // ==============================
        this.remove();
    }

    /**
     * UC3.8a.5: [LUỒNG NGOẠI LỆ] Thiêu rụi vật phẩm đang hiển thị trên bản đồ.
     * Ở màn chơi số 1: xóa vĩnh viễn vật phẩm đó khỏi trò chơi.
     * (Được yêu cầu áp dụng thử ở màn 1, sau này sẽ dùng cho màn 2)
     */
    public void destroyItemWhenBombExplodes(Entity entity) {
        if (Map.getLevelNumber() == 1) {
            entity.remove();
            if (entity instanceof Item) {
                ((Item) entity).delete();
            }
        }
    }
}