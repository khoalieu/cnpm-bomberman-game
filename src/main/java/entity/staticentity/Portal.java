package entity.staticentity;

import graphics.Sprite;

public class Portal extends StaticEntity {
    private boolean accessAble = false;

    public Portal(int x, int y, Sprite sprite) {
        super(x, y, sprite);
        setBlock(true);
    }

    public boolean isAccessAble() {
        return accessAble;
    }

    @Override
    // =====================================
    // UC5.4 - Hệ thống kiểm tra điều kiện hoàn thành màn chơi
    // UC5.5 - Portal được kích hoạt khi tất cả quái vật bị tiêu diệt
    // =====================================
    public void update() {
        // =====================================
        // UC5.5a - Portal chưa được kích hoạt do vẫn còn quái vật
        //======================================
        if(map.getEnemies().size() == 0 && !isBlock()) {
            accessAble = true;
        }
    }
}
