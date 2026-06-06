package entity.animateentity;

import entity.staticentity.Grass;
import graphics.Sprite;
import sound.Sound;
import texture.FlameTexture;

import static variables.Variables.STATUS.*;

public class Bomb extends AnimateEntity {
    protected int timetoExplode = 120;
    public static int limit = 1;
    private boolean up = true;
    private boolean left = true;
    private boolean right = true;
    private boolean down = true;
    private int cnt = 0;

    private int velocityX = 0;
    private int velocityY = 0;
    private boolean isMoving = false;
    private int speed = 2;

    /**
     * UC3.4 (Thuộc tính Xuyên Thấu):
     * Cờ isPierce được gán từ Bomber.executePlaceBomb() dựa trên trạng thái hasPierceBomb của người chơi.
     * - Nếu true: tia lửa xuyên qua Brick (gạch mềm) nhưng vẫn bị Wall (tường cứng) chặn.
     * - Nếu false: hành vi bom thông thường, tia lửa bị Brick chặn lại.
     */
    public boolean isPierce = false;

    public Bomb(int x, int y, Sprite sprite) {
        super(x, y, sprite);
        animation.put(NOTEXPLODEDYET, Sprite.BOMB);
        currentAnimate = animation.get(NOTEXPLODEDYET);
        block = false;
    }

    @Override
    public void update() {
        // ==============================
        // UC3.9a.2 (Đá bom – Kick Bomb): Nếu quả bom đang trong trạng thái bị đá (isMoving == true),
        // hệ thống dịch chuyển bom theo hướng đã được thiết lập cho đến khi chạm vật cản hoặc phát nổ.
        // ==============================
        if (isMoving) {
            moveAndCheckCollision();
        }

        // ==============================
        // UC3.5: Hệ thống bắt đầu đếm ngược thời gian nổ mỗi frame (timetoExplode--).
        //        Mặc định timetoExplode = 120 frame ≈ 2 giây ở 60 FPS.
        // ==============================
        if (timetoExplode != 0) {
            updateAnimation();
            timetoExplode--;
        } else {
            // ==============================
            // UC3.6: Hết thời gian đếm ngược → hệ thống kích hoạt trạng thái "Nổ" (Explode).
            //        Xóa đối tượng Bom khỏi bản đồ và sinh tia lửa trung tâm (be = bomb_exploded).
            // ==============================
            delete(); // UC3.9 (một phần): Dọn dẹp đối tượng Bom khỏi bộ nhớ
            Flame flm = FlameTexture.setFlame("be", this.tileX, this.tileY);
            map.getFlames().add(flm);

            // ==============================
            // UC3.7: Hệ thống tính toán và vẽ các tia lửa (Flame) lan ra 4 hướng
            //        (Lên, Xuống, Trái, Phải) dựa trên chỉ số sức mạnh (flameLength) hiện tại.
            //        Tại mỗi ô lan tới, nếu là đường đi trống (Grass), hệ thống sinh tia lửa và lan truyền tiếp.
            // ==============================
            for (int i = 1; i <= flm.flameLength; i++) {
                int x = flm.getTileX();
                int y = flm.getTileY();

                // === HƯỚNG XUỐNG ===
                if (down) {
                    // ==============================
                    // UC3.8a.4: Kiểm tra nổ dây chuyền – nếu ô phía xuống có Bom khác → ép nổ ngay
                    // ==============================
                    triggerChainBomb(x, y + i, "down");
                    checkFlameOverlap(x, y + i, "down");
                    // ==============================
                    // UC3.7a.1: [LUỒNG NGOẠI LỆ] Nếu ô tiếp theo chứa vật cản (Wall hoặc Brick):
                    //   - Nếu là Wall: LUÔN chặn tia lửa.
                    //   - Nếu là Brick và isPierce == false: chặn tia lửa.
                    //   - Nếu là Brick và isPierce == true: tia lửa xuyên qua.
                    // ==============================
                    checkWallCollision(x, y + i, "down", flm);
                }

                // === HƯỚNG LÊN ===
                if (up) {
                    // ==============================
                    // UC3.8a.4: Kiểm tra nổ dây chuyền – nếu ô phía trên có Bom khác → ép nổ ngay
                    // ==============================
                    triggerChainBomb(x, y - i, "up");
                    checkFlameOverlap(x, y - i, "up");
                    // ==============================
                    // UC3.7a.1: [LUỒNG NGOẠI LỆ] Nếu ô tiếp theo chứa vật cản (Wall hoặc Brick):
                    //   - Nếu là Wall: LUÔN chặn tia lửa.
                    //   - Nếu là Brick và isPierce == false: chặn tia lửa.
                    //   - Nếu là Brick và isPierce == true: tia lửa xuyên qua.
                    // ==============================
                    checkWallCollision(x, y - i, "up", flm);
                }

                // === HƯỚNG PHẢI ===
                if (right) {
                    // ==============================
                    // UC3.8a.4: Kiểm tra nổ dây chuyền – nếu ô phía phải có Bom khác → ép nổ ngay
                    // ==============================
                    triggerChainBomb(x + i, y, "right");
                    checkFlameOverlap(x + i, y, "right");
                    // ==============================
                    // UC3.7a.1: [LUỒNG NGOẠI LỆ] Nếu ô tiếp theo chứa vật cản (Wall hoặc Brick):
                    //   - Nếu là Wall: LUÔN chặn tia lửa.
                    //   - Nếu là Brick và isPierce == false: chặn tia lửa.
                    //   - Nếu là Brick và isPierce == true: tia lửa xuyên qua.
                    // ==============================
                    checkWallCollision(x + i, y, "right", flm);
                }

                // === HƯỚNG TRÁI ===
                if (left) {
                    // ==============================
                    // UC3.8a.4: Kiểm tra nổ dây chuyền – nếu ô phía trái có Bom khác → ép nổ ngay
                    // ==============================
                    triggerChainBomb(x - i, y, "left");
                    checkFlameOverlap(x - i, y, "left");
                    // ==============================
                    // UC3.7a.1: [LUỒNG NGOẠI LỆ] Nếu ô tiếp theo chứa vật cản (Wall hoặc Brick):
                    //   - Nếu là Wall: LUÔN chặn tia lửa.
                    //   - Nếu là Brick và isPierce == false: chặn tia lửa.
                    //   - Nếu là Brick và isPierce == true: tia lửa xuyên qua.
                    // ==============================
                    checkWallCollision(x - i, y, "left", flm);
                }

                // ==============================
                // UC3.7 (tiếp): Nếu cờ hướng vẫn == true (ô đó là Grass hoặc đã xuyên qua Brick),
                //               hệ thống sinh tia lửa tại ô đó và tiếp tục lan truyền.
                // ==============================
                if (down)  spawnFlame(x, y + i, i, flm.flameLength, "down");
                if (up)    spawnFlame(x, y - i, i, flm.flameLength, "up");
                if (right) spawnFlame(x + i, y, i, flm.flameLength, "right");
                if (left)  spawnFlame(x - i, y, i, flm.flameLength, "left");
            }

            if (cnt == 0) {
                Sound.bomb_explosion.play();
            }
        }
    }

    // ================= CÁC HÀM HỖ TRỢ ĐÃ ĐƯỢC TÁCH (EXTRACT METHODS) =================

    /**
     * UC3.8a.4: [LUỒNG NGOẠI LỆ] Kích nổ dây chuyền –
     * Nếu tia lửa chạm một Bom khác đang nằm trên bản đồ,
     * hệ thống ép quả bom đó đếm ngược về 0 ngay lập tức (timetoExplode = 0).
     * Quả bom đó sẽ nổ ngay ở frame tiếp theo, tạo hiệu ứng nổ dây chuyền.
     */
    private void triggerChainBomb(int x, int y, String dir) {
        map.getBombs().forEach(bomb -> {
            if (bomb.getTileX() == x && bomb.getTileY() == y) {
                // ==============================
                // UC3.8a.4: Ép quả bom tại (x, y) đếm ngược về 0 → kích nổ ngay lập tức
                // ==============================
                bomb.setTimetoExplode(0);
                // Cập nhật biến đếm âm thanh để tránh phát âm thanh nổ trùng lặp
                switch (dir) {
                    case "down"  -> { if (bomb.up)    cnt++; }
                    case "up"    -> { if (bomb.down)  cnt++; }
                    case "right" -> { if (bomb.left)  cnt++; }
                    case "left"  -> { if (bomb.right) cnt++; }
                }
            }
        });
    }

    /**
     * Tránh vẽ đè tia lửa nếu tại ô đó đã có tia lửa sẵn.
     * Đặt cờ hướng = false để dừng lan truyền thêm.
     */
    private void checkFlameOverlap(int x, int y, String dir) {
        map.getFlames().forEach(flame -> {
            if (flame.getTileX() == x && flame.getTileY() == y) {
                switch (dir) {
                    case "down"  -> down  = false;
                    case "up"    -> up    = false;
                    case "right" -> right = false;
                    case "left"  -> left  = false;
                }
            }
        });
    }

    /**
     * UC3.7a.1: [LUỒNG NGOẠI LỆ] Xử lý va chạm tia lửa với vật cản trên bản đồ.
     * - Ô trống (Grass): bỏ qua, tia lửa tiếp tục lan truyền.
     * - Wall (Tường cứng): LUÔN chặn tia lửa, kể cả bom xuyên thấu.
     * - Brick (Tường mềm):
     *     + isPierce == false → UC3.7a.1: chặn tia lửa (hành vi bom thường).
     *     + isPierce == true  → UC3.7a.1: tia lửa xuyên qua Brick đã bị phá (hành vi bom xuyên thấu).
     * - Các vật thể khác (bom khác, ...): chặn tia lửa.
     * Hàm interactWith() được gọi để xử lý tương tác phụ (phá gạch, lộ item, ...).
     */
    private void checkWallCollision(int x, int y, String dir, Flame centerFlame) {
        entity.Entity tile = map.getTile(x, y);
        if (tile instanceof Grass) return; // Ô trống → tia lửa lan tiếp, không cần xử lý

        boolean wasBlock = tile.isBlock();

        // UC3.8: Gọi tương tác phụ (phá Brick, mở khóa Item ẩn, lộ Portal,...)
        centerFlame.interactWith(tile);

        if (tile instanceof entity.staticentity.Wall) {
            // Tường cứng (Wall): LUÔN chặn – dù là bom thường hay bom xuyên thấu
            stopFlame(dir);
        } else if (tile instanceof entity.animateentity.Brick || (wasBlock && (tile instanceof entity.staticentity.Item || tile instanceof entity.staticentity.Portal))) {
            // ==============================
            // UC3.7a.1: Gạch mềm (Brick):
            //   - Bom thường (isPierce == false): chặn tia lửa, gạch bị phá.
            //   - Bom xuyên thấu (isPierce == true): tia lửa tiếp tục xuyên qua, gạch vẫn bị phá.
            // ==============================
            if (!this.isPierce) {
                stopFlame(dir); // Chặn lại nếu KHÔNG phải bom xuyên thấu
            }
            // isPierce == true: không gọi stopFlame() → tia lửa xuyên qua gạch đã bị phá
        } else {
            // Vật cản khác (bom đang đặt, vật phẩm block, ...): chặn lại
            if (tile.isBlock()) {
                stopFlame(dir);
            }
        }
    }

    /**
     * Ngừng lan truyền tia lửa theo hướng chỉ định bằng cách đặt cờ = false.
     */
    private void stopFlame(String dir) {
        switch (dir) {
            case "down"  -> down  = false;
            case "up"    -> up    = false;
            case "right" -> right = false;
            case "left"  -> left  = false;
        }
    }

    /**
     * UC3.7 (tiếp): Sinh hình ảnh tia lửa dựa vào hướng và vị trí lan truyền.
     * Ô cuối cùng (i == max) dùng sprite đầu tia lửa; các ô giữa dùng sprite thân.
     */
    private void spawnFlame(int x, int y, int i, int max, String dir) {
        String tex = "";
        if (i == max) {
            tex = switch (dir) {
                case "down"  -> "vdl";
                case "up"    -> "vtl";
                case "left"  -> "hll";
                case "right" -> "hrl";
                default      -> "";
            };
        } else {
            tex = (dir.equals("up") || dir.equals("down")) ? "v" : "h";
        }
        map.getFlames().add(FlameTexture.setFlame(tex, x, y));
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        Bomb.limit = limit;
    }

    public void setTimetoExplode(int timetoExplode) {
        this.timetoExplode = timetoExplode;
    }

    @Override
    public void delete() {
        // ==============================
        // UC3.9 (một phần): Dọn dẹp đối tượng Bom khỏi danh sách quản lý bản đồ.
        // ==============================
        this.remove();
    }

    /**
     * UC3.9a.2: [LUỒNG NGOẠI LỆ] Đá bom (Kick Bomb) –
     * Được gọi từ Bomber.handleBombBlocking() khi người chơi có KickItem và húc vào bom.
     * Thiết lập vận tốc cho bom trượt đi theo hướng di chuyển của người chơi.
     * Bom sẽ tiếp tục di chuyển cho đến khi va chạm vật cản (xử lý trong moveAndCheckCollision())
     * hoặc tự phát nổ khi hết timetoExplode.
     */
    public void kick(variables.Variables.DIRECTION direction) {
        if (isMoving) return; // Bom đang trượt, không nhận lệnh đá mới
        isMoving = true;
        switch (direction) {
            case LEFT  -> { velocityX = -speed; velocityY = 0; }
            case RIGHT -> { velocityX = speed;  velocityY = 0; }
            case UP    -> { velocityX = 0; velocityY = -speed; }
            case DOWN  -> { velocityX = 0; velocityY = speed;  }
            default    -> isMoving = false;
        }
    }

    /**
     * UC3.9a.2 (tiếp): Di chuyển bom và kiểm tra va chạm khi đang trượt.
     * UC3.9a.3: [LUỒNG NGOẠI LỆ] Khi bom trượt chạm vật cản (Wall, Brick, Bom khác, Enemy),
     *           hệ thống dừng bom lại và căn giữa ô lưới gần nhất.
     */
    private void moveAndCheckCollision() {
        pixelX += velocityX;
        pixelY += velocityY;

        boolean collision = false;

        // Va chạm với gạch, tường
        for (int i = 0; i < variables.Variables.HEIGHT; i++) {
            for (int j = 0; j < variables.Variables.WIDTH; j++) {
                entity.Entity e = map.getTile(j, i);
                if (e.isBlock() && this.isCollider(e)) {
                    collision = true;
                }
            }
        }

        // Va chạm với bom khác
        for (Bomb b : map.getBombs()) {
            if (b != this && b.isBlock() && this.isCollider(b)) {
                collision = true;
            }
        }

        // Va chạm với quái vật
        for (entity.animateentity.character.enemy.Enemy enemy : map.getEnemies()) {
            if (this.isCollider(enemy)) {
                collision = true;
            }
        }

        if (collision) {
            // ==============================
            // UC3.9a.3: Bom trượt chạm vật cản → dừng lại, căn vào giữa ô lưới gần nhất.
            // ==============================
            pixelX -= velocityX;
            pixelY -= velocityY;
            isMoving = false;
            velocityX = 0;
            velocityY = 0;

            // Căn giữa ô lưới
            int gridX = Math.round((float) pixelX / graphics.Sprite.SCALED_SIZE);
            int gridY = Math.round((float) pixelY / graphics.Sprite.SCALED_SIZE);
            pixelX = gridX * graphics.Sprite.SCALED_SIZE;
            pixelY = gridY * graphics.Sprite.SCALED_SIZE;
            tileX = gridX;
            tileY = gridY;
        } else {
            tileX = pixelX / graphics.Sprite.SCALED_SIZE;
            tileY = pixelY / graphics.Sprite.SCALED_SIZE;
        }
    }
}