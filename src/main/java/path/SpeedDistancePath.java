package path;

import entity.animateentity.character.Bomber;
import entity.animateentity.character.enemy.Enemy;
import map.Map;
import variables.Variables.DIRECTION;

import java.util.Random;

/**
 * Thuật toán SpeedDistancePath: Kết hợp đuổi theo mục tiêu khi ở gần và di chuyển ngẫu nhiên đổi tốc độ khi ở xa
 * Thực hiện logic phân loại hành vi quái vật theo UC4.2 và UC4.3.
 */
public class SpeedDistancePath extends Path {
    public SpeedDistancePath(Map map, Bomber player, Enemy enemy) {
        super(map, player, enemy);
    }
    /*
    |--------------------------------------------------------------------------
    | AI Logic - Thực hiện UC4.2 & UC4.3
    |--------------------------------------------------------------------------
     */
    public DIRECTION path() {
        // =========================================================================
        // UC4.2: Hệ thống xác định thuật toán AI dựa trên khoảng cách tới người chơi
        // Nếu khoảng cách <= 5 ô, kích hoạt chế độ "Truy đuổi" (DistancePath)
        // =========================================================================
        if (Distance(enemy.getTileY(), enemy.getTileX(), player.getTileY(), player.getTileX(), false) <= 5) {
            enemy.setCntMove(5);
            // UC4.3: Tính toán hướng di chuyển dự kiến (x', y') thông qua DistancePath
            return new DistancePath(map, player, enemy).path();
        }
        // =========================================================================
        // UC4.2 (tiếp): Nếu người chơi ở xa, kích hoạt chế độ "Tuần tra tự do"
        // =========================================================================
        else {
            int cntMove = enemy.getCntMove();
            int changeSpeed = enemy.getChangeSpeed();

            // UC4.4: Tiếp tục di chuyển theo hướng hiện tại nếu không va chạm
            if (!enemy.isCollider() && cntMove > 0) {
                enemy.setCntMove(cntMove - 1);

                // Logic hệ thống tự động hóa hành vi (Mô tả UC4): Thay đổi tốc độ linh hoạt
                if (changeSpeed == 0) {
                    enemy.setSpeed(1 + new Random().nextInt(player.getSpeed()));
                    // Thiết lập thời gian duy trì tốc độ mới
                    if (enemy.getSpeed() == 1) {
                        enemy.setChangeSpeed(30);
                    } else {
                        enemy.setChangeSpeed(10);
                    }
                } else {
                    enemy.setChangeSpeed(changeSpeed - 1);
                }
                return enemy.getDirection();
            }

            // =========================================================================
            // UC4.4a.2: Khi hết lượt di chuyển hoặc phát hiện va chạm (isCollider)
            // Hệ thống kích hoạt logic chọn hướng mới ngẫu nhiên (RandomPath)
            // =========================================================================
            enemy.setCntMove(5);
            return new RandomPath(map, player, enemy).path();
        }
    }
}