package path;

import entity.animateentity.Bomb;
import entity.Entity;
import entity.staticentity.Wall;
import map.Map;
import entity.animateentity.character.Bomber;
import entity.animateentity.character.enemy.Enemy;

import java.util.LinkedList;
import java.util.Queue;

import static variables.Variables.*;
import static variables.Variables.DIRECTION.*;

/**
 * Lớp trừu tượng định nghĩa các thuật toán tìm đường cho kẻ địch
 */
public abstract class Path {

    /*
    |--------------------------------------------------------------------------
    | Fields & Internal Classes
    |--------------------------------------------------------------------------
     */
    protected Map map;
    protected Bomber player;
    protected Enemy enemy;

    // Lớp nội bộ biểu diễn một đỉnh (ô lưới) trong thuật toán tìm đường
    private class Vertex {
        int x;
        int y;
        int value;

        Vertex(int x, int y, int value) {
            this.x = x;
            this.y = y;
            this.value = value;
        }
    }

    public Path(Map map, Bomber player, Enemy enemy) {
        this.map = map;
        this.player = player;
        this.enemy = enemy;
    }

    /*
    |--------------------------------------------------------------------------
    | Helper Methods
    |--------------------------------------------------------------------------
     */

    // Kiểm tra tọa độ (x, y) có nằm trong phạm vi bản đồ hay không
    private boolean isValid(int x, int y) {
        return (x >= 0 && x < HEIGHT && y >= 0 && y < WIDTH);
    }

    // Chuyển đổi số nguyên sang kiểu dữ liệu DIRECTION tương ứng
    public DIRECTION intToDirection(int x) {
        switch (x) {
            case 0:
                return UP;
            case 1:
                return DOWN;
            case 2:
                return LEFT;
            case 3:
                return RIGHT;
            default:
                return NONE;
        }

    }

    /*
    |--------------------------------------------------------------------------
    | Pathfinding Logic (BFS)
    |--------------------------------------------------------------------------
     */

    /**
     * Tính toán khoảng cách ngắn nhất giữa hai điểm sử dụng thuật toán BFS
     * @param dodge Nếu true, cho phép tính toán xuyên qua các vật thể không phải Tường cứng (Wall)
     */
    public int Distance(int x1, int y1, int x2, int y2, boolean dodge) {
        if (map.getTile(y2, x2).isBlock()) {
            return INF;
        }
        if (map.getTile(y1, x1).isBlock()) {
            if (dodge) {
                if (map.getTile(y1, x1) instanceof Wall) {
                    return INF;
                }
            } else {
                return INF;
            }
        }

        int statusTiles[][] = new int[HEIGHT][WIDTH];
        int distanceTiles[][] = new int [HEIGHT][WIDTH];

        // Khởi tạo trạng thái vật cản cho từng ô trên bản đồ
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                distanceTiles[i][j] = INF;
                Entity tile = map.getTile(j, i);
                if (tile.isBlock()) {
                    statusTiles[i][j] = 1;
                    // Nếu ở chế độ né tránh (dodge), các khối như Gạch (Brick) không được tính là vật cản
                    if (dodge && !(tile instanceof Wall)) {
                        statusTiles[i][j] = 0;
                    }
                } else {
                    statusTiles[i][j] = 0;
                }
            }
        }

        // Cập nhật vị trí các quả bom hiện có thành vật cản
        for (Bomb bomb: map.getBombs()) {
            statusTiles[bomb.getTileY()][bomb.getTileX()] = 1;
        }

        // Thực hiện thuật toán tìm kiếm theo chiều rộng (BFS) để tính khoảng cách
        Queue<Vertex> pq = new LinkedList<>();
        pq.add(new Vertex(x1, y1, 0));
        distanceTiles[x1][y1] = 0;
        while (!pq.isEmpty()) {
            Vertex cur = pq.poll();
            for (int k = 0; k < 4; k++) {
                int _x = cur.x + dx[k];
                int _y = cur.y + dy[k];
                if (isValid(_x, _y) && statusTiles[_x][_y] == 0 && distanceTiles[_x][_y] == INF) {
                    distanceTiles[_x][_y] = cur.value + 1;
                    pq.add(new Vertex(_x, _y, cur.value + 1));
                }
            }
        }
        return distanceTiles[x2][y2];
    }

    /*
    |--------------------------------------------------------------------------
    | Abstract Method
    |--------------------------------------------------------------------------
     */

    // Hàm trừu tượng trả về hướng di chuyển tiếp theo dựa trên thuật toán AI cụ thể
    public abstract DIRECTION path();
}