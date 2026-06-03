package sound;

import java.util.ArrayList;
import java.util.List;

public class Sound {

    // =====================================
    // UC5.12a - Trạng thái Tắt/Bật âm thanh
    // =====================================
    private static boolean muted = false;

    // =====================================
    // Danh sách các hiệu ứng âm thanh của hệ thống
    // =====================================
    public static SoundPlay bomb_explosion =
            new SoundPlay("src/main/resources/sounds/bomb_explosion.wav");

    public static SoundPlay menu_sound =
            new SoundPlay("src/main/resources/sounds/menu_sound.wav");

    public static SoundPlay stage_sound =
            new SoundPlay("src/main/resources/sounds/stage_sound.wav");

    public static SoundPlay bomber_die =
            new SoundPlay("src/main/resources/sounds/bomber_die.wav");

    public static SoundPlay game_over =
            new SoundPlay("src/main/resources/sounds/game_over.wav");

    public static SoundPlay level_complete =
            new SoundPlay("src/main/resources/sounds/level_complete.wav");

    public static SoundPlay level_start =
            new SoundPlay("src/main/resources/sounds/level_start.wav");

    public static SoundPlay place_bomb =
            new SoundPlay("src/main/resources/sounds/place_bomb.wav");

    public static SoundPlay get_item =
            new SoundPlay("src/main/resources/sounds/get_item.wav");

    public static SoundPlay walk =
            new SoundPlay("src/main/resources/sounds/walk.wav");

    // =====================================
    // UC5.12a - Danh sách quản lý âm thanh tập trung
    // Dùng để thực hiện Mute/Unmute toàn cục
    // =====================================
    private static final List<SoundPlay> allSounds = new ArrayList<>() {{
        add(bomb_explosion);
        add(menu_sound);
        add(stage_sound);
        add(bomber_die);
        add(game_over);
        add(level_complete);
        add(level_start);
        add(place_bomb);
        add(get_item);
        add(walk);
    }};

    // =====================================
    // UC5.12a.1
    // Hệ thống tiếp nhận yêu cầu tắt âm thanh
    // =====================================
    public static void muteAll() {

        muted = true;

        // =====================================
        // UC5.12a.2
        // Hệ thống dừng toàn bộ âm thanh đang phát
        // =====================================
        for (SoundPlay sound : allSounds) {
            sound.stop();
        }
    }

    // =====================================
    // UC5.12a.3
    // Hệ thống tiếp nhận yêu cầu bật lại âm thanh
    // =====================================
    public static void unmuteAll() {
        muted = false;
    }

    // =====================================
    // UC5.12a.4
    // Kiểm tra trạng thái âm thanh hiện tại
    // =====================================
    public static boolean isMuted() {
        return muted;
    }

    // =====================================
    // UC1.8 & UC5.12a
    // Phát lại nhạc nền màn chơi khi âm thanh
    // được bật trở lại
    // =====================================
    public static void playStageSound() {

        if (!muted) {
            stage_sound.play();
            stage_sound.loop();
        }
    }
}