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

    public Bomb(int x, int y, Sprite sprite) {
        super(x, y, sprite);
        animation.put(NOTEXPLODEDYET, Sprite.BOMB);
        currentAnimate = animation.get(NOTEXPLODEDYET);
        block = false;
    }

    @Override
    public void update() {
        // ==============================
        // UC3.5: Đếm ngược thời gian nổ của bom
        // ==============================
        if (timetoExplode != 0) {
            updateAnimation();
            timetoExplode--;
        } else {
            // ==============================
            // UC3.6: Hết thời gian đếm ngược, kích hoạt trạng thái "Nổ" (Explode)
            // ==============================
            delete(); // Dọn dẹp đối tượng Bom hiện tại
            Flame flm = FlameTexture.setFlame("be", this.tileX, this.tileY);
            map.getFlames().add(flm);

            // ==============================
            // UC3.7: Tính toán và vẽ các tia lửa (Flame) lan ra 4 hướng
            // ==============================
            for (int i = 1; i <= flm.flameLength; i++) {
                int x = flm.getTileX();
                int y = flm.getTileY();

                // Kiểm tra và tương tác luồng HƯỚNG XUỐNG
                if (down) {
                    triggerChainBomb(x, y + i, "down");
                    checkFlameOverlap(x, y + i, "down");
                    // ==============================
                    //UC3.7a.1: Nếu ô tiếp theo chứa vật cản (Wall/Brick), gọi hàm tương tác và ngừng lan truyền
                    // ==============================
                    checkWallCollision(x, y + i, "down", flm);
                }
                // Kiểm tra và tương tác luồng HƯỚNG LÊN
                if (up) {
                    triggerChainBomb(x, y - i, "up");
                    checkFlameOverlap(x, y - i, "up");
                    // ==============================
                    //UC3.7a.1: Nếu ô tiếp theo chứa vật cản (Wall/Brick), gọi hàm tương tác và ngừng lan truyền
                    // ==============================
                    checkWallCollision(x, y - i, "up", flm);
                }
                // Kiểm tra và tương tác luồng HƯỚNG PHẢI
                if (right) {
                    triggerChainBomb(x + i, y, "right");
                    checkFlameOverlap(x + i, y, "right");
                    // ==============================
                    //UC3.7a.1: Nếu ô tiếp theo chứa vật cản (Wall/Brick), gọi hàm tương tác và ngừng lan truyền
                    // ==============================
                    checkWallCollision(x + i, y, "right", flm);
                }
                // Kiểm tra và tương tác luồng HƯỚNG TRÁI
                if (left) {
                    triggerChainBomb(x - i, y, "left");
                    checkFlameOverlap(x - i, y, "left");
                    // ==============================
                    //UC3.7a.1: Nếu ô tiếp theo chứa vật cản (Wall/Brick), gọi hàm tương tác và ngừng lan truyền
                    // ==============================
                    checkWallCollision(x - i, y, "left", flm);
                }

                // ==============================
                // UC3.7. (Tiếp): Nếu ô tiếp theo là đường đi trống (Grass), hệ thống tiếp tục sinh tia lửa
                // ==============================
                // Chỉ vẽ khi cờ tương ứng vẫn bằng true
                if (down) spawnFlame(x, y + i, i, flm.flameLength, "down");
                if (up) spawnFlame(x, y - i, i, flm.flameLength, "up");
                if (right) spawnFlame(x + i, y, i, flm.flameLength, "right");
                if (left) spawnFlame(x - i, y, i, flm.flameLength, "left");
            }

            if (cnt == 0) {
                Sound.bomb_explosion.play();
            }
        }
    }

    // ================= CÁC HÀM HỖ TRỢ ĐÃ ĐƯỢC TÁCH (EXTRACT METHODS) =================
    /**
     * Kiểm tra và kích nổ các bom khác (Nổ dây chuyền)
     */
    private void triggerChainBomb(int x, int y, String dir) {
        map.getBombs().forEach(bomb -> {
            if (bomb.getTileX() == x && bomb.getTileY() == y) {
                // ==============================
                // UC3.8a.4 - Ép quả bom khác đếm ngược về 0 ngay lập tức
                // ==============================
                bomb.setTimetoExplode(0);
                // Cập nhật trạng thái tia lửa và biến đếm âm thanh
                switch (dir) {
                    case "down" -> {
                        down = false;
                        if (bomb.up) cnt++;
                    }
                    case "up" -> {
                        up = false;
                        if (bomb.down) cnt++;
                    }
                    case "right" -> {
                        right = false;
                        if (bomb.left) cnt++;
                    }
                    case "left" -> {
                        left = false;
                        if (bomb.right) cnt++;
                    }
                }
            }
        });
    }

    /**
     * Tránh vẽ đè tia lửa nếu tại ô đó đã có tia lửa rồi
     */
    private void checkFlameOverlap(int x, int y, String dir) {
        map.getFlames().forEach(flame -> {
            if (flame.getTileX() == x && flame.getTileY() == y) {
                switch (dir) {
                    case "down" -> down = false;
                    case "up" -> up = false;
                    case "right" -> right = false;
                    case "left" -> left = false;
                }
            }
        });
    }

    /**
     * UC3.7a.1: Nếu ô tiếp theo chứa vật cản (Wall/Brick), gọi hàm tương tác và ngừng lan truyền
     */
    private void checkWallCollision(int x, int y, String dir, Flame centerFlame) {
        if (!(map.getTile(x, y) instanceof Grass)) {
            centerFlame.interactWith(map.getTile(x, y));
            switch (dir) {
                case "down" -> down = false;
                case "up" -> up = false;
                case "right" -> right = false;
                case "left" -> left = false;
            }
        }
    }

    /**
     * Sinh hình ảnh tia lửa dựa vào hướng và vị trí lan truyền
     */
    private void spawnFlame(int x, int y, int i, int max, String dir) {
        String tex = "";
        if (i == max) {
            tex = switch (dir) {
                case "down" -> "vdl";
                case "up" -> "vtl";
                case "left" -> "hll";
                case "right" -> "hrl";
                default -> "";
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
        this.remove();
    }
}