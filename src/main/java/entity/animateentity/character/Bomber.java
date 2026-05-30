package entity.animateentity.character;

import entity.Entity;
import entity.animateentity.Bomb;
import entity.animateentity.Brick;
import entity.animateentity.character.enemy.Enemy;
import entity.staticentity.*;
import graphics.Sprite;
import input.KeyInput;
import sound.Sound;
import texture.BombTexture;

import static graphics.Sprite.*;
import static variables.Variables.DIRECTION.*;

public class Bomber extends Character {
    public KeyInput keyInput;
    private int timeRevival;

    public boolean passWall = false;
    public int passWallTimer = 0;
    public boolean passBomb = false;
    public int passBombTimer = 0;
    public boolean hasShield = false;
    public int shieldTimer = 0;
    public boolean isFlamePass = false;
    public int flamePassTimer = 0;

    public Bomber(int x, int y, Sprite sprite, KeyInput keyInput) {
        super(x, y, sprite);
        initAnimation();
        this.keyInput = keyInput;
        this.keyInput.initialization();
        this.defaultVel = 1;
        this.speed = 2;
        this.life = 3;
    }

    @Override
    protected boolean canPass(Entity entity) {
        // =============================================================
        // UC2.4a.1. Nếu tại tọa độ tiếp theo có va chạm trực diện với vật cản cứng (Wall, Brick, Bomb),
        // hệ thống kiểm tra cờ trạng thái xuyên thấu của nhân vật.
        // UC2.4a.2. Nếu nhân vật có hiệu ứng Xuyên tường (WallPass) hoặc Xuyên Bom (BombPass)... hệ thống cho phép đi qua.
        // =============================================================
        if (entity instanceof Brick && this.passWall) {
            return true;
        }
        if (entity instanceof Bomb && this.passBomb) {
            return true;
        }
        return super.canPass(entity); // Nếu không có cờ, trả về false (Bị chặn lại)
    }

    private void initAnimation() {
        animation.put(LEFT, Sprite.PLAYER_LEFT);
        animation.put(RIGHT, Sprite.PLAYER_RIGHT);
        animation.put(UP, Sprite.PLAYER_UP);
        animation.put(DOWN, Sprite.PLAYER_DOWN);
        animation.put(DESTROYED, Sprite.PLAYER_DESTROYED);
        currentAnimate = animation.get(DOWN);
    }

    public void placeBombAt(int x, int y) {
        // Giải mã tọa độ pixel sang tọa độ lưới (Grid)
        int bombX = Math.round((float) x / SCALED_SIZE);
        int bombY = Math.round((float) y / SCALED_SIZE);

        // ==============================
        // UC3.3: Kiểm tra số lượng bom tối đa
        // ==============================

        // ==============================
        // UC3.3a.1: Nếu số lượng bom người chơi đã đặt trên bản đồ đạt ngưỡng tối đa
        // ==============================
        if (!isBombLimitAvailable()) {
            return; // UC3.3a.2: Hệ thống bỏ qua lệnh
        }

        // ==============================
        // UC3.4a.1: Kiểm tra vị trí hợp lệ (Vật cản, Bom khác, Enemy)
        // ==============================
        if (!isValidPlaceToSetBomb(bombX, bombY)) {
            // UC3.4a.2: Hệ thống không cho phép đặt đè
            return;
        }

        // ==============================
        // UC3.4: Khởi tạo và kích hoạt bom
        // ==============================
        executePlaceBomb(bombX, bombY);
    }

    private boolean isBombLimitAvailable() {
        return map.getBombs().size() < Bomb.limit;
    }

    private boolean isValidPlaceToSetBomb(int bx, int by) {
        // Kiểm tra nền phải là cỏ
        if (!(map.getTile(bx, by) instanceof Grass)) return false;

        // Kiểm tra xem đã có quả bom nào ở tọa độ này chưa
        boolean hasBomb = map.getBombs().stream()
                .anyMatch(b -> b.getTileX() == bx && b.getTileY() == by);

        // Kiểm tra xem có quái vật đang đứng đây không
        boolean hasEnemy = map.getEnemies().stream()
                .anyMatch(e -> e.getTileX() == bx && e.getTileY() == by);

        return !hasBomb && !hasEnemy;
    }

    private void executePlaceBomb(int bx, int by) {
        Bomb bomb = BombTexture.setBomb(bx, by);
        map.getBombs().add(bomb);
        Sound.place_bomb.play();
    }

    // --- HỆ THỐNG DI CHUYỂN & VA CHẠM ---

    @Override
    public void setDirection() {
        // ==============================
        //UC2.2 - UC3.2: Gán hướng đã giải mã vào thuộc tính direction của nhân vật
        // ==============================
        direction = keyInput.handleKeyInput();
        this.setVelocity(0, 0);

        switch (direction) {
            // =============================================================
            // UC2.3: Hệ thống thiết lập vận tốc (Velocity) dựa trên hướng
            // =============================================================
            case LEFT -> this.setVelocity(-defaultVel, 0);
            case RIGHT -> this.setVelocity(defaultVel, 0);
            case UP -> this.setVelocity(0, -defaultVel);
            case DOWN -> this.setVelocity(0, defaultVel);
            // ==============================
            //UC3.2 (tiếp): Kich hoạt bom
            // ==============================
            case PLACEBOMB -> {
                placeBombAt(pixelX, pixelY);
                direction = NONE; // Tránh việc đặt bom liên tục khi giữ phím
            }
        }

        if (direction != NONE) {
            // =============================================================
            // UC2.3 (tiếp): Cập nhật hoạt ảnh (Animation) tương ứng với hướng đi
            // =============================================================
            currentAnimate = animation.get(direction);
            updateAnimation();
            // =============================================================
            // UC2.6: Hệ thống phát âm thanh bước chân khi di chuyển
            // =============================================================
            Sound.walk.play();
        }
    }

    @Override
    public void checkCollision() {
        // =============================================================
        // UC2.4: Hệ thống thực hiện kiểm tra va chạm tại tọa độ dự kiến
        // =============================================================
        super.checkCollision();
        handleImmortalState();
        handleEnemyCollision();
        handleItemCollision();
        handleBombBlocking();

        // =============================================================
        // UC2.4a.3. Nếu nhân vật không có hiệu ứng, hệ thống chặn di chuyển.
        // Hệ thống sẽ thử dịch chuyển nhân vật một khoảng nhỏ (Sliding sensitivity) để lách qua vật cản nếu lệch mép.
        // =============================================================
        if (isCollision) {
            slidingSensivity();
        }

        // =============================================================
        // UC2.5: Cập nhật tọa độ mới (Pixel và Grid) sau khi đã xử lý va chạm/trượt
        // =============================================================
        tileX = pixelX / SCALED_SIZE;
        tileY = pixelY / SCALED_SIZE;
    }

    private void handleImmortalState() {
        if (immortal > 0) immortal--;
    }

    private void handleEnemyCollision() {
        map.getEnemies().forEach(enemy -> {
            // =============================================================
            // UC2.4c.1. Nếu hệ thống phát hiện tọa độ đè lên Quái vật (Enemy) hoặc vùng Lửa nổ (Flame),
            // hệ thống kiểm tra cờ trạng thái Bất tử (Invincible).
            // =============================================================
            if (this.isCollider(enemy) && immortal == 0) {
                if (hasShield || isFlamePass) {
                    // =============================================================
                    // UC2.4c.2. Nếu nhân vật đang bất tử, hệ thống bỏ qua sát thương, nhân vật an toàn.
                    // =============================================================
                } else {
                    // =============================================================
                    // UC2.4c.3. Nếu nhân vật không bất tử, hệ thống chuyển nhân vật sang trạng thái bị tiêu diệt, trừ mạng và kích hoạt luồng hồi sinh. Luồng di chuyển bị hủy bỏ.
                    // =============================================================
                    destroy();
                }
            }
        });
    }

    // =============================================================
    //  UC2.4b - Tương tác với Vật phẩm (Item):
    // =============================================================
    private void handleItemCollision() {
        // =============================================================
        // UC2.4b.1. Nếu tọa độ di chuyển đè lên một Vật phẩm (Item), hệ thống nhận diện loại vật phẩm.
        // =============================================================
        map.getItems().forEach(item -> {
            if (this.isCollider(item)) {
                // =============================================================
                // UC2.4b.2. Hệ thống áp dụng hiệu ứng buff (Tốc độ, Xuyên tường, Xuyên bom, Bất tử...)
                // và kích hoạt bộ đếm thời gian (Timer) cho hiệu ứng đó.
                // =============================================================
                if (item instanceof SpeedItem) {
                    setSpeed(SpeedItem.increasedSpeed);
                } else if (item instanceof WallPassItem) {
                    passWall = true;
                    passWallTimer = 600;
                } else if (item instanceof BombPassItem) {
                    passBomb = true;
                    passBombTimer = 600;
                } else if (item instanceof MysteryItem) {
                    hasShield = true;
                    shieldTimer = 600;
                } else if (item instanceof FlamePassItem) {
                    isFlamePass = true;
                    flamePassTimer = 600;
                }

                // =============================================================
                // UC2.4b.3. Hệ thống xóa vật phẩm khỏi bản đồ và phát âm thanh nhặt đồ. Nhân vật tiếp tục di chuyển.
                // =============================================================
                Sound.get_item.play();
                item.setActivated(true);
                item.remove();
                item.delete();
            }
        });
    }

    private void handleBombBlocking() {
        map.getBombs().forEach(bomb -> {
            if (!this.isCollider(bomb)) {
                bomb.setBlock(true);
            }
        });
    }

    private void slidingSensivity() {
        // Chi tiết logic của UC2.4a.2: "Nắn" tọa độ nhân vật để lướt qua vật cản
        for (int i = -8 - speed; i <= 8 + speed; i++) {
            switch (direction) {
                case UP, DOWN -> pixelX += i;
                case LEFT, RIGHT -> pixelY += i;
            }
            super.checkCollision();
            if (!isCollision) break;
            switch (direction) {
                case UP, DOWN -> pixelX -= i;
                case LEFT, RIGHT -> pixelY -= i;
            }
        }
    }

    @Override
    public void delete() {
        // =====================================
        // UC5.3a - Bomber chết nhưng vẫn còn mạng hồi sinh
        // =====================================
        //UC5.3a.2. Hệ thống giảm số mạng hiện tại
        this.life--;
        timeRevival = 7;
        immortal = 100;
        //UC5.3a.3. Hệ thống kích hoạt trạng thái hồi sinh
        map.setRevival(true);
        //UC5.3a.4. Bomber được đưa về vị trí bắt đầu
        setPosition(SCALED_SIZE, SCALED_SIZE);
        destroyed = false;
        direction = NONE;
        setSprite(Sprite.PLAYER_DOWN[0]);
        Sound.bomber_die.play();
    }

    public int getTimeRevival() {
        return timeRevival;
    }

    @Override
    public void update() {
        // =============================================================
        // UC2.4b.4 (Bổ sung): Hệ thống đếm ngược thời gian hiệu lực của Vật phẩm và tự động gỡ bỏ buff khi hết hạn.
        // =============================================================
        if (passWallTimer > 0) {
            passWallTimer--;
            if (passWallTimer == 0) passWall = false;
        }
        if (passBombTimer > 0) {
            passBombTimer--;
            if (passBombTimer == 0) passBomb = false;
        }
        if (shieldTimer > 0) {
            shieldTimer--;
            if (shieldTimer == 0) hasShield = false;
        }
        if (flamePassTimer > 0) {
            flamePassTimer--;
            if (flamePassTimer == 0) isFlamePass = false;
        }

        // Gọi lại hàm update của class cha (Character) để Bomber vẫn di chuyển và xét va chạm bình thường
        super.update();
    }
}