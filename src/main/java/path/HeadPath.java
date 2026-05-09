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
 */
public class HeadPath extends Path{
    public HeadPath(Map map, Bomber player, Enemy enemy) {
        super(map, player, enemy);
    }
    /*
    |--------------------------------------------------------------------------
    | AI Logic
    |--------------------------------------------------------------------------
     */
    @Override
    public DIRECTION path() {
        // Nếu không cùng hàng hoặc cùng cột, không kích hoạt trạng thái đuổi theo trực diện
        if (player.getTileX() != enemy.getTileX() && player.getTileY() != enemy.getTileY()) {
            return NONE;
        }

        // Trường hợp: Người chơi và kẻ địch nằm trên cùng một Cột (X)
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

            // Kiểm tra vật cản (Bom và Block) ở khoảng giữa hai thực thể theo trục dọc
            if (player.getTileY() < enemy.getTileY()) {
                for (Bomb bomb: map.getBombs()) {
                    if (bomb.getTileY() >= player.getTileY() && bomb.getTileY() <= enemy.getTileY()) {
                        return NONE;
                    }
                }
                for (int i = player.getTileY(); i < enemy.getTileY(); i++) {
                    if (map.getTile(player.getTileX(), i).isBlock()) {
                        return NONE;
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
        // Trường hợp: Người chơi và kẻ địch nằm trên cùng một Hàng (Y)
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

            // Kiểm tra vật cản (Bom và Block) ở khoảng giữa hai thực thể theo trục ngang
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

        // Xử lý khi cả hai nằm trong cùng một ô Tile (xác định hướng theo pixel)
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

        // Trả về hướng di chuyển tấn công dựa trên vị trí tương đối của Tile
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