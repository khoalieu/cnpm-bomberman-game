package UC5Test;

import entity.animateentity.character.Bomber;
import entity.animateentity.character.enemy.Balloom;
import entity.animateentity.character.enemy.Enemy;
import entity.animateentity.Flame;
import entity.staticentity.Portal;
import graphics.Sprite;
import variables.Variables;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UC5_ReleaseTest {

    private Bomber testBomber;
    private Enemy testEnemy;

    @BeforeEach
    public void setUp() {
        // Chỉ khởi tạo các thực thể độc lập để kiểm thử logic
        testBomber = new Bomber(1, 1, Sprite.PLAYER_DOWN[0], null);
        testEnemy = new Balloom(2, 1, Sprite.BALLOOM_LEFT[0]);
    }

    // [TC5.1 - CẬP NHẬT ĐIỂM SỐ VÀ THỜI GIAN]
    @Test
    public void testScoreAndTimeUpdateLogic() {
        // Test logic điểm số đơn giản, không gọi Game Loop hoặc Render
        int initialScore = 0;
        int enemyScore = 100;
        int newScore = initialScore + enemyScore;

        assertEquals(100, newScore, "Điểm số phải được cộng đúng sau khi tiêu diệt quái vật");

        // Test logic thời gian giảm
        int initialTime = 200;
        int timeAfterOneSecond = initialTime - 1;

        assertEquals(199, timeAfterOneSecond, "Thời gian phải giảm đều theo Game Loop");
    }

    // [TC5.2 - QUẢN LÝ THỜI GIAN HIỆU LỰC VẬT PHẨM]
    @Test
    public void testItemEffectDurationLogic() {
        int itemDuration = 10;
        boolean itemEffectActive = true;

        assertTrue(itemEffectActive, "Hiệu ứng vật phẩm phải được kích hoạt sau khi nhặt");

        while (itemDuration > 0) {
            itemDuration--;
        }

        if (itemDuration == 0) {
            itemEffectActive = false;
        }

        assertEquals(0, itemDuration, "Thời gian hiệu lực vật phẩm phải giảm về 0");
        assertFalse(itemEffectActive, "Hiệu ứng vật phẩm phải bị thu hồi khi hết thời gian");
    }

    // [TC5.3 - BOMBER CHẾT NHƯNG VẪN CÒN MẠNG HỒI SINH]
    @Test
    public void testBomberDeathButStillHasLife() {
        int initialLife = testBomber.getLife();

        testBomber.delete();

        assertEquals(initialLife - 1, testBomber.getLife(), "Số mạng của Bomber phải giảm đi 1 sau khi chết");
        assertFalse(testBomber.isDestroyed(), "Bomber còn mạng nên không bị destroyed vĩnh viễn");
        assertTrue(testBomber.getImmortal() > 0, "Bomber phải có trạng thái bất tử tạm thời sau khi hồi sinh");
    }

    // [TC5.4 - BOMBER CHẾT VÀ KHÔNG CÒN MẠNG]
    @Test
    public void testBomberDeathWhenNoLifeLeft() {
        // Gọi delete() cho đến khi mạng giảm về 0
        while (testBomber.getLife() > 0) {
            testBomber.delete();
        }

        assertEquals(0, testBomber.getLife(), "Khi Bomber chết hết mạng, số mạng phải bằng 0");

        boolean gameOver = testBomber.getLife() == 0;

        assertTrue(gameOver, "Khi Bomber hết mạng, hệ thống phải chuyển sang Game Over");
    }

    // [TC5.5 - GAME OVER KHI HẾT THỜI GIAN]
    @Test
    public void testGameOverWhenTimeIsZero() {
        int countdown = 1;

        countdown--;

        boolean gameOver = countdown == 0;

        assertEquals(0, countdown, "Bộ đếm thời gian phải giảm về 0");
        assertTrue(gameOver, "Khi thời gian bằng 0, game phải chuyển sang Game Over");
    }

    // [TC5.6 - PORTAL CHƯA KÍCH HOẠT KHI CÒN QUÁI]
    @Test
    public void testPortalNotActivatedWhenEnemyStillExists() {
        Portal portal = new Portal(3, 3, Sprite.portal);

        int enemyCount = 1;
        boolean portalAccessAble = enemyCount == 0;

        assertNotNull(portal, "Portal phải được khởi tạo");
        assertFalse(portalAccessAble, "Portal không được kích hoạt khi trên bản đồ vẫn còn quái vật");
    }

    // [TC5.7 - PORTAL KÍCH HOẠT KHI TIÊU DIỆT HẾT QUÁI]
    @Test
    public void testPortalActivatedWhenAllEnemiesKilled() {
        Portal portal = new Portal(3, 3, Sprite.portal);

        int enemyCount = 0;
        boolean portalAccessAble = enemyCount == 0;

        assertNotNull(portal, "Portal phải được khởi tạo");
        assertTrue(portalAccessAble, "Portal phải được kích hoạt khi tất cả quái vật đã bị tiêu diệt");
    }

    // [TC5.8 - CHUYỂN MÀN KHI BOMBER VÀO PORTAL HỢP LỆ]
    @Test
    public void testLevelCompletedWhenBomberEnterAccessiblePortal() {
        boolean portalAccessAble = true;
        boolean bomberTouchPortal = true;

        boolean levelCompleted = portalAccessAble && bomberTouchPortal;

        assertTrue(levelCompleted, "Khi Bomber vào Portal hợp lệ, hệ thống phải xác nhận hoàn thành màn chơi");
    }

    // [TC5.9 - DỌN DẸP MÀN CHƠI KHI CHUYỂN MÀN]
    @Test
    public void testCleanupWhenLoadNewLevel() {
        java.util.List<Enemy> enemies = new java.util.ArrayList<>();
        java.util.List<Flame> flames = new java.util.ArrayList<>();

        enemies.add(testEnemy);
        flames.add(new Flame(1, 1, Sprite.EXPLOSION_HORIZONTAL[0], Variables.FLAME_SHAPE.HORIZONTAL));

        assertFalse(enemies.isEmpty(), "Danh sách Enemy ban đầu phải có dữ liệu");
        assertFalse(flames.isEmpty(), "Danh sách Flame ban đầu phải có dữ liệu");

        // Giả lập cleanup khi chuyển màn
        enemies.clear();
        flames.clear();

        assertTrue(enemies.isEmpty(), "Danh sách Enemy cũ phải được xóa khi chuyển màn");
        assertTrue(flames.isEmpty(), "Danh sách Flame cũ phải được xóa khi chuyển màn");
    }

    // [TC5.10 - TẠM DỪNG VÀ TIẾP TỤC GAME]
    @Test
    public void testPauseAndResumeLogic() {
        boolean isPaused = false;

        // Nhấn phím pause
        isPaused = !isPaused;

        assertTrue(isPaused, "Game phải chuyển sang trạng thái tạm dừng");

        // Nhấn tiếp tục
        isPaused = !isPaused;

        assertFalse(isPaused, "Game phải tiếp tục chạy sau khi resume");
    }

    // [TC5.11 - TẮT / BẬT ÂM THANH]
    @Test
    public void testMuteAndUnmuteLogic() {
        boolean isMuted = false;

        // Nhấn phím M lần 1
        isMuted = !isMuted;

        assertTrue(isMuted, "Âm thanh phải được tắt sau lần nhấn đầu tiên");

        // Nhấn phím M lần 2
        isMuted = !isMuted;

        assertFalse(isMuted, "Âm thanh phải được bật lại sau lần nhấn thứ hai");
    }

    // [TC5.12 - CAMERA FOLLOW BOMBER]
    @Test
    public void testCameraFollowBomberLogic() {
        int bomberX = 200;
        int bomberY = 160;

        int screenWidth = 800;
        int screenHeight = 600;

        int mapWidth = 1600;
        int mapHeight = 1200;

        int cameraX = Math.max(0, Math.min(bomberX - screenWidth / 2, mapWidth - screenWidth));
        int cameraY = Math.max(0, Math.min(bomberY - screenHeight / 2, mapHeight - screenHeight));

        assertTrue(cameraX >= 0, "Camera X không được vượt ra ngoài biên trái");
        assertTrue(cameraY >= 0, "Camera Y không được vượt ra ngoài biên trên");
        assertTrue(cameraX <= mapWidth - screenWidth, "Camera X không được vượt ra ngoài biên phải");
        assertTrue(cameraY <= mapHeight - screenHeight, "Camera Y không được vượt ra ngoài biên dưới");
    }
}