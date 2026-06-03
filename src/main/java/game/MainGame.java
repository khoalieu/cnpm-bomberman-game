package game;

import input.KeyInput;
import sound.Sound;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import map.Map;

import java.io.FileNotFoundException;

import static graphics.Sprite.SCALED_SIZE;
import static variables.Variables.*;

public class MainGame extends Application {
    private static Map map = Map.getGameMap();
    private static Menu menu = new Menu();
    private static int score = 0;
    private static boolean backToMenu = false;
    private static boolean win = false;

    // =====================================
    // Cờ trạng thái cho UC5.11a và UC5.12a
    // =====================================
    private static boolean isPaused = false;
    private static boolean isMuted = false;

    private GraphicsContext graphicsContext;
    private GraphicsContext topInfoContext;
    private GraphicsContext gameMenuContext;
    private Canvas canvas;
    private Canvas topInfo;
    private Canvas gameMenu;
    private final double FPS = 120.0;
    private int countdown;
    private final long timePerFrame = (long) (1000000000 / FPS);
    private long lastFrame;
    private int frames;
    public static long time;
    private long startTime;
    private long lastTime;
    private boolean choseStart = false;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(GAME_TITLE);
        canvas = new Canvas(WIDTH_SCREEN * SCALED_SIZE, HEIGHT_SCREEN * SCALED_SIZE);
        topInfo = new Canvas(WIDTH_SCREEN * SCALED_SIZE, UP_BORDER * SCALED_SIZE);
        gameMenu = new Canvas(WIDTH_SCREEN * SCALED_SIZE, (HEIGHT_SCREEN + UP_BORDER) * SCALED_SIZE);
        graphicsContext = canvas.getGraphicsContext2D();
        topInfoContext = topInfo.getGraphicsContext2D();
        gameMenuContext = gameMenu.getGraphicsContext2D();
        Font font = Font.loadFont(FONT_URLS[0], 30);
        Font menu_font = Font.loadFont(FONT_URLS[0], 35);
        topInfoContext.setFont(font);
        topInfoContext.setFill(Color.WHITE);
        gameMenuContext.setFont(menu_font);
        gameMenuContext.setFill(Color.WHITE);
        VBox root = new VBox(topInfo, canvas);
        Scene scene = new Scene(root);
        VBox root2 = new VBox(gameMenu);
        Scene scene2 = new Scene(root2);
        stage.setScene(scene2);
        stage.setResizable(false);
        stage.getIcons().add(new Image("/icon.png"));
        stage.show();

        // =====================================
        // [UC5.11a & UC5.12a] Tích hợp phím bấm toàn cục
        // =====================================
        setupInput(scene);
        setupInput(scene2);

        startTime = System.nanoTime();
        lastFrame = 0;
        lastTime = 0;
        menu.createMenu();
        Sound.menu_sound.play();
        Sound.menu_sound.loop();
        countdown = 160;
        // [UC1.6 - Bước 1.8]: Bắt đầu phát nhạc nền stage
        // (Nếu bạn gọi Sound.stage_sound.play() ở đâu đó trước vòng lặp thì comment ở đó)

        // [UC1.6 - Bước 1.9]: Khởi chạy AnimationTimer (Game Loop: Update & Render liên tục)
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long currentTime) {
                // =====================================
                // UC5.11a - Quản lý trạng thái Tạm dừng
                // =====================================
                if (isPaused) {
                    menu.renderPauseMenu(gameMenuContext);
                    return;
                }

                long now = currentTime - startTime;
                if (!choseStart || backToMenu) {
                    menu.setStart(false);
                    menu.renderMenu(gameMenuContext);

                    if(menu.isStart() || countdown != 160) {
                        if (countdown == 160) {
                            Sound.level_start.play();
                            try {
                                map.createMap(MAP_URLS[0]); // Đọc Level2.txt từ chỉ mục 0
                                map.resetNumber();
                            } catch (FileNotFoundException e) {
                                System.out.println(e);
                            }
                        }

                        countdown--;
                        menu.renderMessage('s', gameMenuContext);
                    }

                    if (countdown == 0) {
                        countdown = 160;
                        backToMenu = false;
                        choseStart = true;
                        Sound.stage_sound.play();
                        Sound.stage_sound.loop();
                        stage.setScene(scene);
                        // Lệnh khởi tạo map cũ tại đây đã được gỡ bỏ để tránh lỗi hiển thị trễ dữ liệu
                    }
                } else {
                    if (now - lastFrame >= timePerFrame) {
                        lastFrame = now;
                        map.updateMap();
                        map.renderMap(graphicsContext);
                        map.renderTopInfo(topInfoContext);

                        if ((backToMenu && !win) || (countdown != 160 && !win)) {
                            if (countdown == 160) { Sound.game_over.play(); stage.setScene(scene2); }
                            backToMenu = false; menu.renderMessage('o', gameMenuContext); countdown--;
                        }
                        if ((backToMenu && win) || (countdown != 160 && win)) {
                            if (countdown == 160) { Sound.level_complete.play(); stage.setScene(scene2); }
                            backToMenu = false; menu.renderMessage('c', gameMenuContext); countdown--;
                        }

                        // =========================================================
                        // UC5.1a - Hết thời gian chơi (Bộ đếm thời gian giảm về 0)
                        // =========================================================
                        if (countdown == 0) {
                            countdown = 160;
                            choseStart = false;
                            Sound.stage_sound.stop();
                            Sound.menu_sound.play();
                            backToMenu = true;
                            win = false;
                        }
                    }
                }
                frames++;
                if (currentTime - startTime - lastTime >= 1000000000) {
                    stage.setTitle(GAME_TITLE + " | " + frames + " FPS");
                    frames = 0;
                    lastTime = currentTime - startTime;
                }
                time = (currentTime - startTime) / 60000000 + 1;
            }
        };
        timer.start();
    }

    private void setupInput(Scene scene) {
        scene.setOnKeyPressed(keyEvent -> {
            String code = keyEvent.getCode().toString();
            KeyInput.keyInput.put(code, true);
            if (code.equals("P")) togglePause();
            if (code.equals("M")) toggleMute();
        });
        scene.setOnKeyReleased(keyEvent -> KeyInput.keyInput.put(keyEvent.getCode().toString(), false));
    }

    public static void togglePause() { isPaused = !isPaused; }

    public static void toggleMute() {

        isMuted = !isMuted;

        if (isMuted) {

            Sound.muteAll();

        } else {

            Sound.unmuteAll();

            // Nếu đang ở màn chơi
            if (!backToMenu) {
                Sound.stage_sound.play();
                Sound.stage_sound.loop();
            } else {
                Sound.menu_sound.play();
                Sound.menu_sound.loop();
            }
        }
    }

    public static void setNewScore(int enemy_score) { score += enemy_score; }
    public static int getScore() { return score; }

    public static void setBackToMenu(boolean backToMenu) {
        MainGame.backToMenu = backToMenu;
        if (map.getBombs().size() > 0) map.getBombs().remove(0);
    }

    public static void setWin(boolean win) { MainGame.win = win; }
    public static void main(String[] args) { launch(); }
}