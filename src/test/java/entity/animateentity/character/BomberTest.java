package entity.animateentity.character;

import entity.animateentity.Bomb;
import entity.animateentity.character.enemy.Balloom; // Giả sử Balloom là 1 class kế thừa Enemy
import entity.animateentity.character.enemy.Enemy;
import entity.staticentity.Grass;
import input.PlayerInput;
import map.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BomberTest {

    private Bomber bomber;
    private Map gameMap;

    @BeforeEach
    public void setUp() {
        // Lấy instance của Map (Singleton)
        gameMap = Map.getGameMap();

        // Dọn dẹp danh sách bom và quái vật trước mỗi Test Case để đảm bảo tính độc lập
        if (gameMap.getBombs() != null) gameMap.getBombs().clear();
        if (gameMap.getEnemies() != null) gameMap.getEnemies().clear();

        // MÔI TRƯỜNG KIỂM THỬ (TIỀN ĐIỀU KIỆN CHUNG)
        // 1. Khởi tạo Bomber (Nó sẽ tự động liên kết với gameMap thông qua class cha)
        PlayerInput input = new PlayerInput();
        bomber = new Bomber(32, 32, null, input);

        // 2. Giới hạn bom mặc định ban đầu là 1
        Bomb.limit = 1;

        // Khởi tạo tạm mảng tiles cho Map để tránh lỗi NullPointerException khi getTile()
        try {
            java.lang.reflect.Method resetMethod = Map.class.getDeclaredMethod("resetEntities");
            resetMethod.setAccessible(true);
            resetMethod.invoke(gameMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("TC3.1: Đặt bom hợp lệ trên đường đi trống")
    public void testPlaceBombValid_TC3_1() {
        // Chuẩn bị: Đặt nền tại tọa độ Grid (1, 1) là Grass (Đường đi trống)
        int gridX = 1;
        int gridY = 1;
        gameMap.setTile(gridX, gridY, new Grass(gridX, gridY, null));

        // Xác nhận số lượng bom ban đầu là 0
        assertEquals(0, gameMap.getBombs().size(), "Lỗi: Bản đồ ban đầu không được có bom");

        // Hành động: Nhân vật đặt bom tại tọa độ pixel (32, 32)
        bomber.placeBombAt(32, 32);

        // Kết quả mong đợi: Hệ thống tạo thành công 1 quả bom
        assertEquals(1, gameMap.getBombs().size(), "Hệ thống phải khởi tạo 1 đối tượng Bom tại vị trí hợp lệ");
    }

    @Test
    @DisplayName("TC3.2: Vượt quá giới hạn bom cho phép")
    public void testPlaceBombExceedLimit_TC3_2() {
        // Chuẩn bị: Thêm sẵn 1 quả bom vào bản đồ để mô phỏng việc đã đạt giới hạn tối đa
        Bomb existingBomb = new Bomb(1, 1, null);
        gameMap.getBombs().add(existingBomb);

        // Xác nhận tiền điều kiện
        assertEquals(1, gameMap.getBombs().size());
        assertEquals(1, Bomb.limit);

        // Hành động: Người chơi cố gắng đặt thêm quả bom thứ 2
        bomber.placeBombAt(32, 32);

        // Kết quả mong đợi: Lệnh bị bỏ qua, danh sách bom vẫn chỉ có 1
        assertEquals(1, gameMap.getBombs().size(), "Hệ thống phải bỏ qua lệnh đặt bom do đã vượt giới hạn");
    }

    @Test
    @DisplayName("TC3.3: Vị trí đặt bom không hợp lệ do bị đè lên quái vật")
    public void testPlaceBombInvalidPosition_TC3_3() {
        // Chuẩn bị: Đặt nền là Grass, nhưng có một con quái vật đứng tại ô lưới (1, 1)
        int gridX = 1;
        int gridY = 1;
        gameMap.setTile(gridX, gridY, new Grass(gridX, gridY, null));

        // Khởi tạo một Enemy (ví dụ Balloom) tại ô (1, 1) -> pixel là 32, 32
        Enemy dummyEnemy = new Balloom(gridX, gridY, null);
        gameMap.getEnemies().add(dummyEnemy);

        // Hành động: Cố gắng đặt bom ngay dưới chân quái vật
        bomber.placeBombAt(32, 32);

        // Kết quả mong đợi: Lệnh đặt bom bị từ chối, map vẫn có 0 quả bom
        assertEquals(0, gameMap.getBombs().size(), "Hệ thống không cho phép đặt bom đè lên Enemy");
    }
}