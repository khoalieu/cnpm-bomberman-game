package entity.animateentity;

import entity.staticentity.Grass;
import game.MainGame;
import graphics.Sprite;

import static variables.Variables.STATUS.EXPLODING;
import static variables.Variables.STATUS.NOTEXPLODEDYET;

public class Brick extends AnimateEntity {
    public Brick(int x, int y, Sprite sprite) {
        super(x, y, sprite);
        animation.put(NOTEXPLODEDYET, Sprite.BRICK);
        animation.put(EXPLODING, Sprite.BRICK_EXPLODED);
        currentAnimate = animation.get(NOTEXPLODEDYET);
        timeDestroy = 30;
        this.block = true;
    }

    @Override
    public void update() {
        // ==============================
        // UC3.8a.1: [LUỒNG NGOẠI LỆ] Nếu tia lửa chạm Tường mềm (Brick):
        //   Cờ destroyed được gán = true bởi Flame.interactWith() khi tia lửa chạm vào.
        //   Tại đây, hệ thống kích hoạt hoạt ảnh phá gạch (EXPLODING) và đếm ngược timeDestroy.
        //   Khi timeDestroy về 0, gọi delete() để xóa Brick khỏi bản đồ.
        // ==============================
        if (destroyed == true) {
            currentAnimate = animation.get(EXPLODING);
            if (timeDestroy > 0) {
                updateDestroyAnimation();
                timeDestroy--;
            } else {
                delete();
            }
        }
        else {
            updateAnimation();
        }
    }

    @Override
    public void updateAnimation() {
    }

    @Override
    public void updateDestroyAnimation() {
        long time = MainGame.time;
        sprite = Sprite.movingSprite(currentAnimate, 3, time);
        image = sprite.getFxImage();
    }

    @Override
    public void delete() {
        // ==============================
        // UC3.8a.1 (tiếp): Hoàn tất phá gạch –
        //   Hệ thống thay thế ô Brick bằng ô Grass trống trên bản đồ.
        //   Nếu bên dưới Brick có Item ẩn (block = true), Item đó đã được mở khóa và hiển thị
        //   bởi Flame.interactWith() trước đó.
        //   Logic "chặn tia lửa" (dừng lan truyền hướng đó) được xử lý bởi
        //   Bomb.checkWallCollision() thông qua cờ isPierce.
        // ==============================
        map.setTile(this.tileY, this.tileX, new Grass(this.tileX, this.tileY, Sprite.grass));
    }
}