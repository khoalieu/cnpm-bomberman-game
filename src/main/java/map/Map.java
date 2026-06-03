package map;

import entity.animateentity.Bomb;
import entity.animateentity.character.Bomber;
import entity.animateentity.character.Character;
import entity.animateentity.character.enemy.*;
import entity.animateentity.Flame;
import entity.Entity;
import entity.staticentity.Item;
import entity.staticentity.Score;
import game.MainGame;
import game.Menu;
import texture.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileNotFoundException;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import static variables.Variables.*;
import static graphics.Sprite.*;

public class Map {
    private static Map map;
    private static int levelNumber;
    private int time = 60 * 200;
    private Image topInfoImage;
//    private Entity[][] tiles;

    //    private ArrayList<Enemy> enemies;
//    private ArrayList<Bomb> bombs;
//    private ArrayList<Flame> flames;
//    private ArrayList<Item> items;
//    private ArrayList<Score> scores;
    private Entity[][] tiles = new Entity[HEIGHT][WIDTH];

    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Bomb> bombs = new ArrayList<>();
    private ArrayList<Flame> flames = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Score> scores = new ArrayList<>();

    private Bomber player;
    private boolean revival;
    private int renderX;
    private int renderY;
    //uc4+
    private boolean lastEnemyEnragedTriggered = false;

    public static Map getGameMap() {
        if (map == null) {
            map = new Map();
        }
        return map;
    }

    private void resetEntities() {
        tiles = new Entity[HEIGHT][WIDTH];
        enemies = new ArrayList<>();
        bombs = new ArrayList<>();
        flames = new ArrayList<>();
        items = new ArrayList<>();
        scores = new ArrayList<>();
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void resetNumber() {
        Flame.flameLength = 1;
        Bomb.limit = 1;
        MainGame.setNewScore(-MainGame.getScore());
        player.setSpeed(2);
        time = 60 * 200;
    }

    // [UC1.2 - Bước 1.6]: Yêu cầu khởi tạo map (Đọc file txt từ Variables)
    public void createMap(String mapPath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(mapPath));
        topInfoImage = new Image("/top_info.png");
        String _string = scanner.nextLine();
        levelNumber = _string.charAt(0) - '0';
        resetEntities();
        revival = false;

        // Danh sách lưu các tọa độ ô Cỏ trống để có thể đặt vật phẩm ngẫu nhiên
        java.util.ArrayList<int[]> grassPositions = new java.util.ArrayList<>();

        //uc4+
        lastEnemyEnragedTriggered = false;

        // [UC1.4]: Vòng lặp duyệt từng dòng (i) và từng ký tự (j) trong ma trận
        for (int i = 0; i < HEIGHT; i++) {
            String string = scanner.hasNextLine() ? scanner.nextLine() : "";
            for (int j = 0; j < WIDTH; j++) {
                char c = (j < string.length()) ? string.charAt(j) : ' '; // Phân tích ký tự 'c'

                // Khôi phục tường biên an toàn nếu file text bị thiếu hụt khoảng trắng ở cuối
                if (j == WIDTH - 1 || i == 0 || i == HEIGHT - 1 || j == 0) {
                    c = '#';
                }

                // -------------------------------------------------------------
                // [TỐI ƯU CHO LEVEL 2]: Tách biệt logic nhận diện ô cỏ trống để rải item ngẫu nhiên
                // -------------------------------------------------------------
                if (levelNumber == 2) {
                    // Nếu là ô trống HOẶC là ô chứa vật phẩm cố định của Lvl 1 (w, q, m, i, f, b, s)
                    if (c == ' ' || c == 'w' || c == 'q' || c == 'm' || c == 'i' || c == 'f' || c == 'b' || c == 's') {
                        // Thu thập tọa độ ô này để chuẩn bị random vật phẩm
                        if (!((i == 1 && j == 1) || (i == 1 && j == 2) || (i == 2 && j == 1))) {
                            grassPositions.add(new int[]{i, j});
                        }

                        // Ép ký tự c thành ô cỏ trống ' ' ĐÚNG NGHĨA để hệ thống không sinh item cố định tại đây
                        c = ' ';
                    }
                }

                // [UC1.4 - Bước 1.6.2 & 1.6.3]: Tạo StaticEntity (Wall, Grass...)
                tiles[i][j] = StaticTexture.setStatic(c, i, j);

                // ==============================
                // UC1.5: Lưu tất cả các đối tượng vừa tạo vào danh sách quản lý đồ họa
                // ==============================
                if (tiles[i][j] == null) {
                    tiles[i][j] = StaticTexture.setStatic(' ', i, j);
                }

                // [UC1.4a]: Nếu ký tự tương ứng là Vật phẩm (Item), hệ thống nhận diện thực thể Item
                if (tiles[i][j] instanceof Item) {
                    items.add((Item) tiles[i][j]);
                }
                if (c == '*') {
                    tiles[i][j] = BrickTexture.setBrick(i, j);
                }

                // [UC1.4 - Bước 1.6.4 & 1.6.5]: Tạo AnimateEntity (Bomber, Enemy...)
                Character character = CharacterTexture.setCharacter(c, i, j);
                // [UC1.4 - Bước 1.6.6]: Phân loại và lưu vào các danh sách quản lý (player, enemies)
                if (character != null) {
                    if (c == 'p') {
                        player = (Bomber) character;
                    } else {
                        enemies.add((Enemy) character);
                    }
                }
            }
        }

// =========================================================================
        // PHÁT TRIỂN LEVEL 2: KHỞI TẠO VẬT PHẨM NGẪU NHIÊN TRÊN CÁC Ô CỎ TRỐNG
        // =========================================================================
        if (levelNumber == 2 && !grassPositions.isEmpty()) {
            char[] level2Items = {'b', 'f', 's', 'w', 'q', 'm', 'i'};
            java.util.Collections.shuffle(grassPositions);

            int itemsToSpawn = Math.min(level2Items.length, grassPositions.size());
            for (int k = 0; k < itemsToSpawn; k++) {
                int[] pos = grassPositions.get(k);
                int row = pos[0];
                int col = pos[1];
                char itemChar = level2Items[k];

                tiles[row][col] = StaticTexture.setStatic(itemChar, row, col);

                if (tiles[row][col] == null) {
                    tiles[row][col] = StaticTexture.setStatic('s', row, col);
                }

                if (tiles[row][col] instanceof Item) {
                    items.add((Item) tiles[row][col]);
                }
            }
        }
    }

    // =====================================
    // UC5.8 - Hệ thống dọn dẹp dữ liệu màn hiện tại
    // =====================================
    private void removeEntities() {
        ArrayList<Enemy> removedEnemies = new ArrayList<>();
        ArrayList<Bomb> removedBombs = new ArrayList<>();
        ArrayList<Flame> removedFlames = new ArrayList<>();
        ArrayList<Item> removedItems = new ArrayList<>();
        ArrayList<Score> removedScores = new ArrayList<>();
        scores.forEach(score -> {
            if (score.isRemoved()) {
                removedScores.add(score);
            }
        });
        items.forEach(item -> {
            if (item.isRemoved()) {
                removedItems.add(item);
            }
        });
        enemies.forEach(enemy -> {
            if (enemy.isRemoved()) {
                removedEnemies.add(enemy);
            }
        });
        bombs.forEach(bomb -> {
            if (bomb.isRemoved()) {
                removedBombs.add(bomb);
            }
        });
        flames.forEach(flame -> {
            if (flame.isRemoved()) {
                removedFlames.add(flame);
            }
        });
        if (player.isRemoved()) player = null;
        removedEnemies.forEach(enemy -> {
            if (enemy instanceof Balloom) {
                Score score = ScoreTexture.setScore('b', enemy.getTileX(), enemy.getTileY());
                scores.add(score);
            } else if (enemy instanceof Oneal) {
                Score score = ScoreTexture.setScore('o', enemy.getTileX(), enemy.getTileY());
                scores.add(score);
            } else if (enemy instanceof Doll) {
                Score score = ScoreTexture.setScore('d', enemy.getTileX(), enemy.getTileY());
                scores.add(score);
            } else if (enemy instanceof Minvo) {
                Score score = ScoreTexture.setScore('m', enemy.getTileX(), enemy.getTileY());
                scores.add(score);
            } else if (enemy instanceof Kondoria) {
                Score score = ScoreTexture.setScore('k', enemy.getTileX(), enemy.getTileY());
                scores.add(score);
            }
            enemies.remove(enemy);
        });
        removedBombs.forEach(bomb -> {
            bombs.remove(bomb);
        });
        removedFlames.forEach(flame -> {
            flames.remove(flame);
        });
        removedItems.forEach(item -> {
            items.remove(item);
        });
        removedScores.forEach(score -> {
            scores.remove(score);
        });
    }

    //uc4+
    private void triggerLastEnemyEnragedIfNeeded() {
        if (lastEnemyEnragedTriggered) return;

        if (enemies.size() == 1) {
            Enemy lastEnemy = enemies.get(0);

            if (!lastEnemy.isDestroyed() && !lastEnemy.isRemoved()) {
                lastEnemy.becomeEnraged();
                lastEnemyEnragedTriggered = true;
            }
        }
    }

    public void updateMap() {
        if (revival) return;
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                tiles[i][j].update();
            }
        }
        enemies.forEach(enemy -> {
            enemy.update();
        });
        player.update();
        bombs.forEach(bomb -> {
            bomb.update();
        });
        flames.forEach(flame -> {
            flame.update();
        });
        items.forEach(item -> {
            item.update();
        });
        scores.forEach(score -> {
            score.update();
        });
        removeEntities();
        triggerLastEnemyEnragedIfNeeded();
    }

    public void renderTopInfo(GraphicsContext graphicsContext) {
        graphicsContext.drawImage(topInfoImage, 0, 0);
        graphicsContext.fillText("Score: " + String.valueOf(MainGame.getScore()), 0.6 * SCALED_SIZE, SCALED_SIZE * 0.8);
        if (MainGame.getScore() > Menu.getHighscore()) {
            try {
                PrintWriter writer = new PrintWriter("src/main/resources/menu/highscore.txt");
                writer.print("");
                writer.print(MainGame.getScore());
                writer.close();
            } catch (FileNotFoundException e) {
                // [UC1.2a - Bước 2.1]: Phát hiện FileNotFoundException (Không tìm thấy file)
                // [UC1.2a - Bước 2.2]: Ghi nhật ký lỗi (log) ra Console
                System.out.println(e);
            }
        }
        if (time != 0) {
            graphicsContext.fillText("Time: " + String.valueOf((time--) / 60), 0.6 * SCALED_SIZE, SCALED_SIZE * 1.6);
        } else {
            graphicsContext.fillText("Time: " + String.valueOf(time), 0.6 * SCALED_SIZE, SCALED_SIZE * 1.6);
            MainGame.setBackToMenu(true);
        }
        graphicsContext.fillText("Stage: " + String.valueOf(levelNumber), 10.6 * SCALED_SIZE, SCALED_SIZE * 0.8);
        graphicsContext.fillText("Life: " + String.valueOf(player.getLife()), 10.6 * SCALED_SIZE, SCALED_SIZE * 1.6);
        if (player.getLife() == 0) {
            MainGame.setBackToMenu(true);
        }
    }

    private void renderRevival(GraphicsContext graphicsContext) {
        if (renderX == 0 && renderY == 0) {
            revival = false;
            return;
        } else {
            renderX = Math.max(0, renderX - player.getTimeRevival());
            renderY = Math.max(0, renderY - player.getTimeRevival());
        }
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                tiles[i][j].render(graphicsContext);
            }
        }
        enemies.forEach(enemy -> {
            enemy.render(graphicsContext);
        });
        player.render(graphicsContext);

    }

    // =========================================================================
    // [UC1.6a]: (Xử lý Camera) Xác định tọa độ của Người chơi để tính toán vùng nhìn thấy
    // (Tính toán renderX, renderY bám theo Player để thực hiện hiệu ứng cuộn camera màn hình)
    // =========================================================================
    private void updateRenderXY() {
        renderX = player.getPixelX() - (WIDTH_SCREEN / 2) * SCALED_SIZE;
        renderY = player.getPixelY() - (HEIGHT_SCREEN / 2) * SCALED_SIZE;
        if (renderX < 0) {
            renderX = 0;
        }

        if (renderX > WIDTH * SCALED_SIZE - WIDTH_SCREEN * SCALED_SIZE) {
            renderX = WIDTH * SCALED_SIZE - WIDTH_SCREEN * SCALED_SIZE;
        }
        if (renderY < 0) {
            renderY = 0;
        }
        if (renderY > HEIGHT * SCALED_SIZE - HEIGHT_SCREEN * SCALED_SIZE) {
            renderY = HEIGHT * SCALED_SIZE - HEIGHT_SCREEN * SCALED_SIZE;
        }
    }

    // =========================================================================
    // [UC1.6] & [UC1.8]: Tiến hành vẽ toàn bộ bản đồ và các thực thể theo cơ chế 2 lớp liên tục
    // =========================================================================
    public void renderMap(GraphicsContext graphicsContext) {
        if (revival) {
            renderRevival(graphicsContext);
            return;
        }
        // Gọi cập nhật Camera bám theo nhân vật trước khi bắt đầu dựng hình
        updateRenderXY();

        // ---------------------------------------------------------------------
        // [UC1.6b]: (Đồ họa Lớp 1 - Background Layer) Render mảng nền tĩnh (Cỏ, Tường, Gạch)
        // ---------------------------------------------------------------------
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                tiles[i][j].render(graphicsContext);
            }
        }
        // ---------------------------------------------------------------------
        // [UC1.6c]: (Đồ họa Lớp 2 - Foreground Layer) Render các thực thể động đè lên lớp nền
        // (Quái vật, Người chơi, Bom, Lửa, Vật phẩm, Điểm số hiển thị)
        // ---------------------------------------------------------------------
        enemies.forEach(enemy -> {
            enemy.render(graphicsContext);
        });
        player.render(graphicsContext);
        bombs.forEach(bomb -> {
            bomb.render(graphicsContext);
        });
        flames.forEach(flame -> {
            flame.render(graphicsContext);
        });
        items.forEach(item -> {
            item.render(graphicsContext);
        });
        scores.forEach(score -> {
            score.render(graphicsContext);
        });
    }


    public void setTile(int x, int y, Entity entity) {
        tiles[x][y] = entity;
    }

    public Entity getTile(int x, int y) {
        return tiles[y][x];
    }

    public Bomber getPlayer() {
        return this.player;
    }

    public ArrayList<Bomb> getBombs() {
        return bombs;
    }

    public ArrayList<Flame> getFlames() {
        return flames;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public int getRenderX() {
        return renderX;
    }

    public int getRenderY() {
        return renderY;
    }

    public void setRevival(boolean revival) {
        this.revival = revival;
    }

    public static int getLevelNumber() {
        return levelNumber;
    }

    public Entity[][] getTiles() {
        return tiles;
    }
}