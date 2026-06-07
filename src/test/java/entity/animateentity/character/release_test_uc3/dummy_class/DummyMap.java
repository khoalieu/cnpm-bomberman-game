//package entity.animateentity.character.release_test_uc3.dummy_class;
//
//import entity.animateentity.Bomb;
//import entity.animateentity.character.enemy.Enemy;
//import entity.staticentity.Grass;
//import map.Map;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Lớp giả lập cho Map, tự quản lý danh sách Bom và Quái vật trong bộ nhớ tạm
// */
//public class DummyMap extends Map {
//    private List<Bomb> bombs = new ArrayList<>();
//    private List<Enemy> enemies = new ArrayList<>();
//    private entity.Entity[][] tiles = new entity.Entity[20][20];
//
//    @Override
//    public ArrayList<Bomb> getBombs() {
//        return (ArrayList<Bomb>) bombs;
//    }
//
//    @Override
//    public ArrayList<Enemy> getEnemies() {
//        return (ArrayList<Enemy>) enemies;
//    }
//
//    @Override
//    public entity.Entity getTile(int x, int y) {
//        if (tiles[x][y] == null) {
//            // Mặc định trả về Grass (đường đi trống) nếu chưa thiết lập vật cản
//            return new Grass(x, y, null);
//        }
//        return tiles[x][y];
//    }
//
//    // Hàm hỗ trợ riêng cho test để chèn vật cản/quái vật vào bản đồ giả
//    public void setTile(int x, int y, entity.Entity tile) {
//        tiles[x][y] = tile;
//    }
//}
