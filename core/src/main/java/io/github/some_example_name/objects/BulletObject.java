package io.github.some_example_name.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import io.github.some_example_name.Static.GameSettings;

public class BulletObject extends GameObject {

    private boolean hasToBeDestroyed = false;
    private static final float BULLET_SPEED = 500f;
    private static final float DESPAWN_CAMERA_OFFSET = GameSettings.SCREEN_HEIGHT * 2.5f;
    private float currentCameraY = 0;
    public BulletObject(float x, float y, int width, int height, String texturePath, World world) {
        super(texturePath, (int)x, (int)y, width, height, GameSettings.BULLET_BIT, world);

        body.setGravityScale(0);
        body.setBullet(true);
        body.setLinearVelocity(new Vector2(0, BULLET_SPEED));
    }

    public void setCurrentCameraY(float cameraY) {
        this.currentCameraY = cameraY;
    }

    public void setHasToBeDestroyed(boolean destroy) {
        this.hasToBeDestroyed = destroy;
    }

    public boolean hasToBeDestroyed() {
        float cameraCenterY = currentCameraY + GameSettings.SCREEN_HEIGHT / 2f;
        float despawnHeight = cameraCenterY + DESPAWN_CAMERA_OFFSET;

        return hasToBeDestroyed || getY() > despawnHeight;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!hasToBeDestroyed) {
            batch.draw(texture, getX() - (width / 2f), getY() - (height / 2f), width, height);
        }
    }

    public void update(float delta) {
    }
}
