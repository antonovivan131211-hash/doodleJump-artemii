package io.github.some_example_name.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef;
import java.util.Random;

import io.github.some_example_name.Static.GameSettings;

public class PlateObject extends GameObject {
    private static Random random = new Random();

    private boolean isMoving;
    private float movementSpeed = 1.3f;
    private float screenWidthPixels;
    private float halfWidthPixels;

    public PlateObject(float y, float width, float height, short cBits, World world, float screenWidth, boolean isMoving) {

        super("i1.png",
            random.nextFloat() * (screenWidth - width) + width/2,
            y,
            width,
            height,
            cBits,
            world,
            isMoving ? BodyDef.BodyType.KinematicBody : BodyDef.BodyType.StaticBody);

        this.isMoving = isMoving;
        this.screenWidthPixels = screenWidth;
        this.halfWidthPixels = width / 2f;

        if (isMoving) {
            float direction = random.nextBoolean() ? movementSpeed : -movementSpeed;
            body.setLinearVelocity(direction, 0);
        }
    }

    public PlateObject(float y, float width, float height, short cBits, World world, float screenWidth) {
        this(y, width, height, cBits, world, screenWidth, false);
    }

    public void update(float delta) {
        if (isMoving) {
            float currentXWorld = body.getPosition().x;
            float currentXPixels = currentXWorld / GameSettings.SCALE;

            float leftEdge = currentXPixels - halfWidthPixels;
            float rightEdge = currentXPixels + halfWidthPixels;
            float velocityX = body.getLinearVelocity().x;


            if (rightEdge >= screenWidthPixels && velocityX > 0) {
                body.setLinearVelocity(-movementSpeed, 0);
            }
            else if (leftEdge <= 0 && velocityX < 0) {
                body.setLinearVelocity(movementSpeed, 0);
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
