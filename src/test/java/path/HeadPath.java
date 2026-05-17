package path;

import entity.animateentity.Bomb;
import entity.animateentity.character.Bomber;
import entity.animateentity.character.enemy.Enemy;
import map.Map;
import variables.Variables.DIRECTION;

import static graphics.Sprite.SCALED_SIZE;
import static variables.Variables.DIRECTION.*;

/**
 * Thuật toán HeadPath: Kẻ địch tấn công khi phát hiện người chơi nằm trên cùng một hàng hoặc cột (tầm nhìn thẳng)
 * Triển khai logic AI tấn công trực diện theo UC4.2 và UC4.3.
 */
public class HeadPath extends Path{
    public HeadPath(Map map, Bomber player, Enemy enemy) {
        super(map, player, enemy);
    }
    /*
    |--------------------------------------------------------------------------
    | AI Logic - Thực hiện UC4.2 & UC4.3
    |--------------------------------------------------------------------------
     */
    @Override
    public DIRECTION path() {
        // UC4.2: Hệ thống kiểm tra điều kiện kích hoạt trạng thái đuổi theo trực diện
        // Nếu không cùng hàng hoặc cùng cột, AI HeadPath trả về NONE để chuyển sang AI phụ
        if (player.getTileX() != enemy.getTileX() && player.getTileY() != enemy.getTileY()) {
            return NONE;
        }

        // =============================================================
        // UC4.3: Trường hợp người chơi và kẻ địch nằm trên cùng một Cột (X)
        // Hệ thống kiểm tra tính khả thi của việc di chuyển theo trục dọc
        // =============================================================
        if (player.getTileX() == enemy.getTileX()) {
            if (enemy.getPixelX() % SCALED_SIZE != 0) {
                return NONE;
            }
            boolean check = false;
            for (int i = -8; i <= 8; i++) {
                if ((player.getPixelX() + i) % SCALED_SIZE == 0) {
                    check = true;
                }
            }
            if (!check) {
                return NONE;
            }

            // =========================================================================
            // UC4.4a.1: Kiểm tra vật cản (Bom và Block) ở khoảng giữa hai thực thể
            // Nếu có vật cản, tầm nhìn bị chặn, hệ thống dừng logic tấn công (UC4.4a.2)
            // =========================================================================
            if (player.getTileY() < enemy.getTileY()) {
                for (Bomb bomb: map.getBombs()) {
                    if (bomb.getTileY() >= player.getTileY() && bomb.getTileY() <= enemy.getTileY()) {
                        return NONE; // Bị chặn bởi Bom
                    }
                }
                for (int i = player.getTileY(); i < enemy.getTileY(); i++) {
                    if (map.getTile(player.getTileX(), i).isBlock()) {
                        return NONE; // Bị chặn bởi Tường/Gạch
                    }
                }
            } else {
                for (Bomb bomb: map.getBombs()) {
                    if (bomb.getTileY() <= player.getTileY() && bomb.getTileY() >= enemy.getTileY()) {
                        return NONE;
                    }
                }
                for (int i = enemy.getTileY(); i < player.getTileY(); i++) {
                    if (map.getTile(player.getTileX(), i).isBlock()) {
                        return NONE;
                    }
                }
            }
        }
        // =============================================================
        // UC4.3: Trường hợp người chơi và kẻ địch nằm trên cùng một Hàng (Y)
        // Hệ thống tính toán hướng di chuyển dự kiến theo trục ngang
        // =============================================================
        else {
            if (enemy.getPixelY() % SCALED_SIZE != 0) {
                return NONE;
            }
            boolean check = false;
            for (int i = -8; i <= 8; i++) {
                if ((player.getPixelY() + i) % SCALED_SIZE == 0) {
                    check = true;
                }
            }
            if (!check) {
                return NONE;
            }

            // =========================================================================
            // UC4.4a.1: Kiểm tra vật cản ngang (Bom/Block) chặn tầm nhìn của quái vật
            // =========================================================================
            if (player.getTileX() < enemy.getTileX()) {
                for (Bomb bomb: map.getBombs()) {
                    if (bomb.getTileX() >= player.getTileX() && bomb.getTileX() <= enemy.getTileX()) {
                        return NONE;
                    }
                }
                for (int i = player.getTileX(); i < enemy.getTileX(); i++) {
                    if (map.getTile(i, player.getTileY()).isBlock()) {
                        return NONE;
                    }
                }
            } else {
                for (Bomb bomb: map.getBombs()) {
                    if (bomb.getTileX() <= player.getTileX() && bomb.getTileX() >= enemy.getTileX()) {
                        return NONE;
                    }
                }
                for (int i = enemy.getTileX(); i < player.getTileX(); i++) {
                    if (map.getTile(i, player.getTileY()).isBlock()) {
                        return NONE;
                    }
                }
            }
        }

        // =============================================================
        // UC4.5: Xử lý khi cả hai nằm trong cùng một ô Tile
        // Hệ thống tinh chỉnh hướng để kích hoạt va chạm sát thương (Death)
        // =============================================================
        if (player.getTileX() == enemy.getTileX() && player.getTileY() == enemy.getTileY()) {
            if (Math.abs(player.getPixelX() - enemy.getPixelX()) > Math.abs(player.getPixelY() - enemy.getPixelY())) {
                if (player.getPixelX() < enemy.getPixelX()) {
                    return LEFT;
                } else {
                    return RIGHT;
                }
            } else {
                if (player.getPixelY() < enemy.getPixelY()) {
                    return UP;
                } else {
                    return DOWN;
                }
            }
        }

        // =============================================================
        // UC4.3 (Kết quả): Trả về hướng di chuyển tấn công cuối cùng
        // dựa trên vị trí tương đối giữa quái vật và Bomber
        // =============================================================
        if (player.getTileX() == enemy.getTileX()) {
            if (player.getTileY() < enemy.getTileY()) {
                return UP;
            } else {
                return DOWN;
            }
        } else {
            if (player.getTileX() < enemy.getTileX()) {
                return LEFT;
            } else {
                return RIGHT;
            }
        }
    }
}