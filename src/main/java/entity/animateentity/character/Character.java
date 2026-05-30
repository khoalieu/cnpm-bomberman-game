package entity.animateentity.character;

import entity.animateentity.AnimateEntity;
import entity.animateentity.Bomb;
import entity.animateentity.Brick;
import entity.animateentity.character.enemy.Enemy;
import entity.Entity;
import entity.staticentity.Portal;
import entity.staticentity.Wall;
import game.MainGame;
import graphics.Sprite;
import variables.Variables;
import variables.Variables.DIRECTION;

import static variables.Variables.*;

import static graphics.Sprite.*;
import static variables.Variables.DIRECTION.*;

public abstract class Character extends AnimateEntity {
    protected int velocityX;
    protected int velocityY;
    protected int defaultVel;
    protected int speed;

    protected DIRECTION direction;
    protected boolean isCollision;
    protected boolean stand;
    protected int life;

    protected int immortal;

    public Character(int x, int y, Sprite sprite) {
        super(x, y, sprite);
        defaultVel = 0;
        velocityX = 0;
        velocityY = 0;
        life = 0;
        speed = 0;
        isCollision = false;
        stand = true;
        direction = NONE;
        immortal = 0;
    }

    public void setVelocity(int velocityX, int velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public DIRECTION getDirection() {
        return direction;
    }

    public boolean isCollider() {
        return isCollision;
    }


    public void addVelocity(int velocityX, int velocityY) {
        this.velocityX += velocityX;
        this.velocityY += velocityY;
    }

    public void move() {
        pixelX += velocityX;
        pixelY += velocityY;
        tileX = pixelX / SCALED_SIZE;
        tileY = pixelY / SCALED_SIZE;
    }

    public void checkCollision() {
        isCollision = false;
        pixelX += this.velocityX;
        pixelY += this.velocityY;
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                Entity entity = map.getTile(j, i);

                // =============================================================
                // UC2.4a.1. Nếu tại tọa độ tiếp theo có va chạm trực diện với vật cản cứng (Wall, Brick, Bomb),
                // hệ thống kiểm tra cờ trạng thái xuyên thấu của nhân vật.
                // =============================================================
                if (entity.isBlock() && this.isCollider(entity)) {
                    // =============================================================
                    // UC2.4a.2. Nếu nhân vật có hiệu ứng Xuyên tường (WallPass) hoặc Xuyên Bom (BombPass) tương ứng với vật cản,
                    // hệ thống cho phép đi qua bình thường và chuyển sang bước UC2.5.
                    // =============================================================
                    if (this instanceof Bomber && entity instanceof Brick && ((Bomber) this).passWall) {
                        // Pass through, do nothing (Đi xuyên qua gạch)
                    } else {
                        // =============================================================
                        // UC2.4a.3. Nếu nhân vật không có hiệu ứng, hệ thống chặn di chuyển.
                        // Hệ thống sẽ thử dịch chuyển nhân vật một khoảng nhỏ (Sliding sensitivity) để lách qua vật cản nếu lệch mép.
                        // =============================================================
                        isCollision = true;
                    }
                }
                // =====================================
                // UC5.6 - Bomber đi vào Portal đã được kích hoạt("entity instanceof Portal")
                // =====================================

                // =====================================
                // UC5.7 - Hệ thống xác nhận điều kiện chuyển màn hợp lệ("((Portal) entity).isAccessAble()")
                // =====================================
                if (this instanceof Bomber && this.isCollider(entity) && entity instanceof Portal && ((Portal) entity).isAccessAble() && entity.getTileX() == j && entity.getTileY() == i) {
                    MainGame.setBackToMenu(true);
                    MainGame.setWin(true);
                }
            }
        }
        map.getBombs().forEach(bomb -> {
            if (this.isCollider(bomb) && bomb.isBlock()) {
                if (this instanceof Bomber && ((Bomber) this).passBomb) {
                    // =====================================
                    // UC2.4a.2: Pass through, do nothing (Đi xuyên qua bom)
                    // =====================================
                } else if (immortal == 0) {
                    // =====================================
                    // UC2.4a.3: Bị chặn lại
                    // =====================================
                    isCollision = true;
                }
            }
            if(this.isCollider(bomb) && this instanceof Enemy) {
                if (immortal == 0) {
                    isCollision = true;
                }
            }
        });
        stand = (velocityX == 0 && velocityY == 0) || isCollision;
        pixelX -= this.velocityX;
        pixelY -= this.velocityY;
    }

    @Override
    // =====================================
    // UC5.3 - Hệ thống kiểm tra trạng thái của Bomber
    // =====================================
    public void update() {
        if (isDestroyed()) {
            updateDestroyAnimation();
        } else {
            for (int i = 0; i < speed; i++) {
                setDirection();
                checkCollision();
                if (!stand || this instanceof Enemy) {
                    updateAnimation();
                }
                if (!isCollision) {
                    move();
                }
            }
        }
    }

    public int getLife() {
        return life;
    }

    public boolean checkTileCollider(DIRECTION direction, boolean dodge) {
        boolean ok = false;
        int k = 0;
        switch (direction) {
            case UP -> k = 0;
            case DOWN -> k = 1;
            case LEFT -> k = 2;
            case RIGHT -> k = 3;
        }
        int lastPixelX = this.getPixelX();
        int lastPixelY = this.getPixelY();
        this.setTile(this.getTileX() + dx[k], this.getTileY() + dy[k]);
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                Entity entity = map.getTile(j, i);
                if (entity.isBlock() && this.isCollider(entity)) {
                    if (dodge) {
                        if (entity instanceof Wall) {
                            ok = true;
                        }
                    } else {
                        ok = true;
                    }
                }
                for (Bomb bomb: map.getBombs()) {
                    if (this instanceof Enemy) {
                        if (this.isCollider(bomb)) {
                            ok = true;
                        }
                    }
                    if (bomb.isBlock() && this.isCollider(bomb)) {
                        ok = true;
                    }
                }
            }
        }
        this.setPosition(lastPixelX, lastPixelY);
        return ok;
    }

    public int getImmortal() {
        return immortal;
    }

    public void setImmortal(int immortal) {
        this.immortal = immortal;
    }

    @Override
    public void updateAnimation() {
        long time = MainGame.time;
        sprite = Sprite.movingSprite(currentAnimate, 3, time * this.speed);
        image = sprite.getFxImage();
    }

    public abstract void setDirection();

    // ==========================================
    // BỔ SUNG CÁC HÀM GETTER / SETTER NÀY
    // VÀO FILE Character.java (hoặc Bomber.java)
    // ==========================================

    public int getVelocityX() {
        // Trả về vận tốc trục X hiện tại.
        // Tuỳ vào cách bạn code ở lớp cha, biến này có thể tên là velX, velocityX, v.v.
        return this.velocityX;
    }

    public int getVelocityY() {
        // Trả về vận tốc trục Y hiện tại.
        return this.velocityY;
    }

    public boolean isCollision() {
        return this.isCollision;
    }

    public void setCollision(boolean collision) {
        this.isCollision = collision;
    }

    public int getPixelY() {
        return this.pixelY;
    }

    public void setPixelY(int y) {
        this.pixelY = y;
    }

    // Thuộc tính Life nằm ở Bomber, và theo file Map.java bạn gửi,
    // có vẻ hàm getLife() đã tồn tại, nên không cần viết lại.
}
