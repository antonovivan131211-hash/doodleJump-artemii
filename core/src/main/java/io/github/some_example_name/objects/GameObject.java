package io.github.some_example_name.objects;

import static io.github.some_example_name.Static.GameSettings.SCALE;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import io.github.some_example_name.Static.GameSettings;

public abstract class GameObject {
    public short cBits;
    public int width;
    public int height;
    public Body body;
    Texture texture;

    GameObject(String texturePath, int x, int y, int width, int height, short cBits, World world) {
        this.width = width;
        this.height = height;
        this.cBits = cBits;
        this.texture = new Texture(texturePath);
        this.body = createBody(x, y, world, BodyDef.BodyType.DynamicBody);
    }

    GameObject(String texturePath, float x, float y, float width, float height, short cBits, World world, BodyDef.BodyType bodyType) {
        this.width = (int) width;
        this.height = (int) height;
        this.cBits = cBits;
        this.texture = new Texture(texturePath);
        this.body = createBody(x, y, world, bodyType);
    }

    private Body createBody(float x, float y, World world, BodyDef.BodyType bodyType) {
        BodyDef def = new BodyDef();
        def.type = bodyType;
        def.fixedRotation = true;

        Body body = world.createBody(def);

        if (bodyType == BodyDef.BodyType.DynamicBody) {
            CircleShape circleShape = new CircleShape();
            float radius = Math.max(width, height) * SCALE / 2f;
            circleShape.setRadius(radius);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = circleShape;
            fixtureDef.density = 0.8f;
            fixtureDef.friction = 0.0f;
            fixtureDef.restitution = 0.0f;
            fixtureDef.filter.categoryBits = cBits;

            if (cBits == GameSettings.DOODLE_BIT) {
                fixtureDef.filter.maskBits = GameSettings.DOODLE_MASK;
            } else if (cBits == GameSettings.BULLET_BIT) {
                fixtureDef.filter.maskBits = GameSettings.BULLET_MASK;
            }

            Fixture fixture = body.createFixture(fixtureDef);
            fixture.setUserData(this);
            circleShape.dispose();
        } else {

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(width * SCALE / 2f, height * SCALE / 2f);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = cBits;
            fixtureDef.filter.maskBits = GameSettings.PLATE_MASK;


            fixtureDef.isSensor = false;
            fixtureDef.friction = 0.0f;
            fixtureDef.restitution = 0.0f;

            Fixture fixture = body.createFixture(fixtureDef);
            fixture.setUserData(this);
            shape.dispose();
        }

        body.setTransform(x * SCALE, y * SCALE, 0);
        return body;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture,
            getX() - (width / 2f),
            getY() - (height / 2f),
            width,
            height);
    }
    public int getX() {
        return (int) (body.getPosition().x / SCALE);
    }

    public int getY() {
        return (int) (body.getPosition().y / SCALE);
    }

    public void setX(int x) {
        body.setTransform(x * SCALE, body.getPosition().y, 0);
    }

    public void setY(int y) {
        body.setTransform(body.getPosition().x, y * SCALE, 0);
    }

    public void hit() {
    }

    public void dispose() {
        texture.dispose();
    }

    protected void update(float delta) {
    }
}
