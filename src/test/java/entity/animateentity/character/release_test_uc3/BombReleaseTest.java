//package entity.animateentity.character.release_test_uc3;
//
//import entity.animateentity.Bomb;
//import entity.animateentity.character.Bomber;
//import entity.animateentity.character.release_test_uc3.dummy_class.DummyEnemy;
//import entity.animateentity.character.release_test_uc3.dummy_class.DummyKeyInput;
//import entity.animateentity.character.release_test_uc3.dummy_class.DummyMap;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class BombReleaseTest {
//
//    private Bomber bomber;
//    private entity.animateentity.character.release_test_uc3.dummy_class.DummyMap testMap;
//    private DummyKeyInput testKeyInput;
//
//
//    // =========================================================================
//    // 2. THIẾT LẬP MÔI TRƯỜNG KIỂM THỬ
//    // =========================================================================
//
//    @BeforeEach
//    public void setUp() {
//        testMap = new DummyMap();
//        testKeyInput = new DummyKeyInput();
//
//        // Khởi tạo Bomber tại tọa độ pixel (32, 32) -> tương đương ô lưới (1, 1)
//        bomber = new Bomber(32, 32, null, testKeyInput);
//
//        // Giả định class Entity hoặc Bomber có hàm setMap() để gán bản đồ
//        // Nếu biến map là protected, bạn có thể gán trực tiếp: bomber.map = testMap;
//        bomber.setMap(testMap);
//    }
//
//    // =========================================================================
//    // 3. CÁC TEST CASES ĐƯỢC VIẾT BẰNG PURE JUNIT 5
//    // =========================================================================
//
//    /**
//     * Tương đương TC3.1: Đặt bom hợp lệ trên đường đi trống.
//     */
//    @Test
//    public void testPlaceBomb_ValidPosition() {
//        Bomb.limit = 1;
//
//        // Hành động: Cố gắng đặt bom
//        try {
//            bomber.placeBombAt(32, 32);
//        } catch (Exception e) {
//            // Bắt lỗi NullPointerException do Sound.place_bomb.play() hoặc BombTexture thiếu hình ảnh
//            // Chúng ta chỉ quan tâm luồng logic có đẩy bom vào Map hay không.
//        }
//
//        // Kiểm tra kết quả: Xác minh rằng một quả bom đã được add vào danh sách của Map
//        assertEquals(1, testMap.getBombs().size(), "Hệ thống phải đặt thành công 1 quả bom trên bản đồ trống.");
//    }
//
//    /**
//     * Tương đương TC3.2: Vượt quá giới hạn bom cho phép.
//     */
//    @Test
//    public void testPlaceBomb_ExceedLimit() {
//        Bomb.limit = 1;
//
//        // Giả lập trên bản đồ ĐÃ CÓ sẵn 1 quả bom (đạt giới hạn)
//        testMap.getBombs().add(new Bomb(1, 1, null));
//
//        // Hành động: Cố gắng đặt thêm quả thứ 2
//        try {
//            bomber.placeBombAt(32, 32);
//        } catch (Exception e) {}
//
//        // Kiểm tra kết quả: Số lượng bom vẫn phải là 1 (Lệnh đặt bom bị từ chối)
//        assertEquals(1, testMap.getBombs().size(), "Hệ thống phải từ chối khi vượt quá giới hạn bom.");
//    }
//
//    /**
//     * Tương đương TC3.3: Vị trí đặt bom không hợp lệ do bị đè lên quái vật.
//     */
//    @Test
//    public void testPlaceBomb_InvalidPosition_OverlapEnemy() {
//        Bomb.limit = 2; // Đảm bảo không bị vướng lỗi giới hạn bom
//
//        // Giả lập: Có quái vật đang đứng tại ô lưới (1, 1)
//        testMap.getEnemies().add(new DummyEnemy(1, 1));
//
//        // Hành động
//        try {
//            bomber.placeBombAt(32, 32);
//        } catch (Exception e) {}
//
//        // Kiểm tra kết quả: Số lượng bom trên bản đồ phải là 0 vì bị đè lên quái vật
//        assertEquals(0, testMap.getBombs().size(), "Hệ thống phải từ chối lệnh đặt đè lên quái vật.");
//    }
//}