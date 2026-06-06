package sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class SoundPlay {

    private Clip clip;
    private String path;

    public SoundPlay(String path) {
        this.path = path;

        try {
            // SỬA LỖI WINDOWS BẰNG CÁCH REPLACE DẤU \
            String resourcePath = path.replace("\\", "/");
            resourcePath = resourcePath.replace("src/main/resources", "");
            if (!resourcePath.startsWith("/")) {
                resourcePath = "/" + resourcePath;
            }

            // Lấy URL của file âm thanh nằm TỪ TRONG file .jar
            URL url = getClass().getResource(resourcePath);

            if (url == null) {
                System.err.println("Lỗi: Không tìm thấy file âm thanh tại " + resourcePath);
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

        } catch (Exception e) {
            System.err.println("Lỗi khi load âm thanh " + path + ": " + e.getMessage());
        }
    }

    public void play() {
        if (Sound.isMuted() || clip == null) {
            return;
        }
        clip.setFramePosition(0);
        clip.start();
    }

    public void loop() {
        if (Sound.isMuted() || clip == null) {
            return;
        }
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }

    public boolean isFinish() {
        if (clip == null) return true;
        return clip.getMicrosecondLength() == clip.getMicrosecondPosition();
    }
}