package path;

import entity.animateentity.character.Bomber;
import entity.animateentity.character.enemy.Enemy;
import map.Map;
import variables.Variables.DIRECTION;

import java.util.Random;

/**
 * Thuật toán SpeedDistancePath: Kết hợp đuổi theo mục tiêu khi ở gần và di chuyển ngẫu nhiên đổi tốc độ khi ở xa
 */
public class SpeedDistancePath extends Path {
    public SpeedDistancePath(Map map, Bomber player, Enemy enemy) {
        super(map, player, enemy);
    }
    /*
    |--------------------------------------------------------------------------
    | AI Logic
    |--------------------------------------------------------------------------
     */
    public DIRECTION path() {
        // Trường hợp 1: Nếu người chơi trong phạm vi gần (<= 5 ô), chuyển sang chế độ đuổi theo (DistancePath)
        if (Distance(enemy.getTileY(), enemy.getTileX(), player.getTileY(), player.getTileX(), false) <= 5) {
            enemy.setCntMove(5);
            return new DistancePath(map, player, enemy).path();
        }
        // Trường hợp 2: Nếu người chơi ở xa, thực hiện di chuyển tự do và thay đổi tốc độ linh hoạt
        else {
            int cntMove = enemy.getCntMove();
            int changeSpeed = enemy.getChangeSpeed();

            // Tiếp tục hướng hiện tại nếu không va chạm và vẫn còn lượt di chuyển
            if (!enemy.isCollider() && cntMove > 0) {
                enemy.setCntMove(cntMove - 1);

                // Logic thay đổi tốc độ ngẫu nhiên dựa trên chỉ số của người chơi
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

            // Khi hết lượt di chuyển hoặc va chạm, chọn hướng mới ngẫu nhiên (RandomPath)
            enemy.setCntMove(5);
            return new RandomPath(map, player, enemy).path();
        }
    }
}