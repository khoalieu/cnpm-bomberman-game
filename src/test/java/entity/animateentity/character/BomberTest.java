package entity.animateentity.character;

import entity.animateentity.Bomb;
import entity.animateentity.Brick;
import entity.animateentity.character.enemy.Balloom;
import entity.animateentity.character.enemy.Enemy;
import entity.staticentity.Grass;
import input.PlayerInput;
import map.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import variables.Variables;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BomberTest {

    private Bomber bomber;
    private Map gameMap;

    @BeforeEach
    public void setUp() {
        // 1. Lấy instance của Map (Singleton)
        gameMap = Map.getGameMap();

        // 2. Dọn dẹp danh sách thực thể để đảm bảo môi trường test sạch sẽ
        if (gameMap.getBombs() != null) gameMap.getBombs().clear();
        if (gameMap.getEnemies() != null) gameMap.getEnemies().clear();
        if (gameMap.getFlames() != null) gameMap.getFlames().clear();

        // 3. Khởi tạo mảng tiles và trải thảm cỏ (Grass) TRỰC TIẾP
        // Tránh dùng gameMap.setTile() vì đang bị ngược trục X, Y trong Map.java
        entity.Entity[][] tiles = gameMap.getTiles();
        for (int i = 0; i < Variables.HEIGHT; i++) {
            for (int j = 0; j < Variables.WIDTH; j++) {
                // i là trục Y (Height), j là trục X (Width)
                tiles[i][j] = new Grass(j, i, null);
            }
        }

        // 4. Khởi tạo Bomber (nó sẽ tự động link với gameMap thông qua class cha)
        PlayerInput input = new PlayerInput();
        bomber = new Bomber(32, 32, null, input);

        // 5. Giới hạn bom mặc định ban đầu là 1
        Bomb.limit = 1;
    }

    @Test
    @DisplayName("TC3.1: Đặt bom hợp lệ trên đường đi trống")
    public void testPlaceBombValid_TC3_1() {
        assertEquals(0, gameMap.getBombs().size(), "Lỗi: Bản đồ ban đầu không được có bom");

        // Hành động: Nhân vật đặt bom tại tọa độ pixel (32, 32)
        bomber.placeBombAt(32, 32);

        // Kết quả mong đợi
        assertEquals(1, gameMap.getBombs().size(), "Hệ thống phải khởi tạo 1 đối tượng Bom tại vị trí hợp lệ");
    }

    @Test
    @DisplayName("TC3.2: Vượt quá giới hạn bom cho phép")
    public void testPlaceBombExceedLimit_TC3_2() {
        // Chuẩn bị: Thêm sẵn 1 quả bom vào bản đồ
        Bomb existingBomb = new Bomb(1, 1, null);
        gameMap.getBombs().add(existingBomb);

        assertEquals(1, gameMap.getBombs().size());
        assertEquals(1, Bomb.limit);

        // Hành động: Cố gắng đặt thêm quả thứ 2
        bomber.placeBombAt(32, 32);

        // Kết quả mong đợi: Lệnh bị bỏ qua
        assertEquals(1, gameMap.getBombs().size(), "Hệ thống phải bỏ qua lệnh đặt bom do đã vượt giới hạn");
    }

    @Test
    @DisplayName("TC3.3: Vị trí đặt bom không hợp lệ do bị đè lên quái vật")
    public void testPlaceBombInvalidPosition_TC3_3() {
        // Chuẩn bị: Khởi tạo một Enemy tại ô (1, 1)
        Enemy dummyEnemy = new Balloom(1, 1, null);
        gameMap.getEnemies().add(dummyEnemy);

        Bomb.limit = 2; // Tăng limit để chắc chắn không bị lỗi do giới hạn số lượng

        // Hành động: Cố gắng đặt bom ngay dưới chân quái vật (pixel 32, 32 tương đương ô 1, 1)
        bomber.placeBombAt(32, 32);

        // Kết quả mong đợi
        assertEquals(0, gameMap.getBombs().size(), "Hệ thống không cho phép đặt bom đè lên Enemy");
    }

    @Test
    @DisplayName("TC3.4: Bom nổ phá gạch mềm và mở khóa vật phẩm ẩn")
    public void testBombExplodeBrickAndRevealItem_TC3_4() {
        // Chuẩn bị Bom tại ô (1, 1)
        Bomb bomb = new Bomb(1, 1, null);
        bomb.setTimetoExplode(1); // Ép nổ ở frame kế tiếp
        gameMap.getBombs().add(bomb);

        // Chuẩn bị Brick tại ô (2, 1) -> Tương đương X = 2, Y = 1
        Brick brick = new Brick(2, 1, null);
        gameMap.getTiles()[1][2] = brick; // Gán trực tiếp mảng: mảng[Y][X]

        // Hành động: Cập nhật vòng đời của bom
        bomb.update(); // Giảm đếm ngược về 0
        bomb.update(); // Kích nổ, sinh Flame

        // Cập nhật va chạm của Flame
        if (gameMap.getFlames() != null) {
            gameMap.getFlames().forEach(entity.animateentity.Flame::update);
        }

        // Kết quả mong đợi
        assertTrue(brick.isDestroyed(), "Cờ destroyed của Brick phải bằng true khi bị tia lửa chạm vào");
    }

    @Test
    @DisplayName("TC3.5: Kích hoạt kỹ năng Đá bom (Kick Bomb)")
    public void testKickBomb_TC3_5() {
        // Chuẩn bị: Bomber đã có ở (32, 32)
        bomber.hasKickAbility = true;

        // Đặt quả bom ngay bên phải Bomber (ô 2, 1 -> pixel X = 64, Y = 32)
        Bomb bomb = new Bomb(2, 1, null);
        bomb.setBlock(true);
        gameMap.getBombs().add(bomb);

        int initialBombX = bomb.getPixelX();

        // Hành động: Ép Bomber quay mặt sang phải và húc vào bom
        bomber.direction = Variables.DIRECTION.RIGHT;
        bomber.setVelocity(2, 0);

        bomber.checkCollision(); // Kích hoạt lệnh kick()
        bomb.update();           // Cập nhật bom để nó trượt đi

        // Kết quả mong đợi
        assertTrue(bomb.getPixelX() > initialBombX, "Quả bom phải trượt đi (Pixel X tăng) sau khi Bomber húc vào");
    }

    @Test
    @DisplayName("TC3.6: Kích hoạt kỹ năng Bom xuyên thấu (Pierce Bomb)")
    public void testPierceBomb_TC3_6() {
        // Chuẩn bị 1: Cấp kỹ năng Bom xuyên thấu cho Bomber
        bomber.hasPierceBomb = true;

        // Chuẩn bị 2: Tăng độ dài tia lửa (flameLength) lên 2 để lửa có thể lan tới viên gạch thứ hai
        entity.animateentity.Flame.flameLength = 2;

        // Chuẩn bị 3: Đặt 2 viên gạch mềm liên tiếp nhau trên trục X (ô 2,1 và ô 3,1)
        Brick brick1 = new Brick(2, 1, null);
        Brick brick2 = new Brick(3, 1, null);
        gameMap.getTiles()[1][2] = brick1; // Lưới tọa độ là mảng[Y][X]
        gameMap.getTiles()[1][3] = brick2;

        // Hành động 1: Bomber đặt bom tại vị trí của mình (ô 1,1 -> pixel 32,32)
        // Việc dùng placeBombAt giúp kiểm tra luôn logic truyền cờ (flag) từ Bomber sang Bomb
        bomber.placeBombAt(32, 32);

        // Lấy quả bom vừa đặt ra và ép thời gian nổ về 1 frame
        Bomb bomb = gameMap.getBombs().get(0);
        bomb.setTimetoExplode(1);

        // Hành động 2: Cập nhật vòng đời của bom
        bomb.update(); // Khung hình 1: Giảm đếm ngược về 0
        bomb.update(); // Khung hình 2: Kích nổ, sinh tia lửa và quét va chạm xuyên qua gạch

        // Cập nhật va chạm của Flame (để chắc chắn các hàm tương tác được gọi)
        if (gameMap.getFlames() != null) {
            gameMap.getFlames().forEach(entity.animateentity.Flame::update);
        }

        // Kết quả mong đợi: CẢ HAI viên gạch đều phải bị phá hủy do tính năng xuyên thấu
        assertTrue(brick1.isDestroyed(), "Viên gạch đầu tiên phải bị phá hủy khi trúng tia lửa");
        assertTrue(brick2.isDestroyed(), "Viên gạch thứ 2 cũng phải bị phá hủy vì tia lửa xuyên qua viên thứ 1");
    }

}