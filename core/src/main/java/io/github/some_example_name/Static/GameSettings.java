package io.github.some_example_name.Static;

public class GameSettings {
    public static final int SCREEN_WIDTH = 720;
    public static final int SCREEN_HEIGHT = 1280;


    public static final float SCALE = 0.04f;

    public static final float STEP_TIME = 1f / 60;
    public static final int VELOCITY_ITERATIONS = 6;
    public static final int POSITION_ITERATIONS = 6;

    public static float SHIP_FORCE_RATIO = 10;
    public static final int BULLET_WIDTH = 50;
    public static final int BULLET_HEIGHT = 50;

    public static final short DOODLE_BIT = 1;
    public static final short PLATE_BIT = 2;
    public static final short MONSTER_BIT = 4;
    public static final short BULLET_BIT = 8;

    public static final short DOODLE_MASK = PLATE_BIT | MONSTER_BIT;
    public static final short PLATE_MASK = DOODLE_BIT;
    public static final short BULLET_MASK = MONSTER_BIT;


    public static final int DOODLE_WIDTH = 70;
    public static final int DOODLE_HEIGHT = 90;


    public static final int PLATFORM_WIDTH = 110;
    public static final int PLATFORM_HEIGHT = 25;

    public static final float DOODLE_JUMP_FORCE = 78f;
    public static final float GRAVITY_SCALE = 2.8f;
    public static final float MAX_JUMP_HEIGHT = 800f;


    public static final float PLATFORM_SPACING = 220f;
    public static final float MIN_PLATFORM_SPACING = 200f;
    public static final float MAX_PLATFORM_SPACING = 280f;
}
