package io.github.some_example_name.Managers;

import com.badlogic.gdx.physics.box2d.World;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import io.github.some_example_name.Static.GameSettings;
import io.github.some_example_name.objects.DoodleObject;
import io.github.some_example_name.objects.PlateObject;

public class PlatformManager {
    private ArrayList<PlateObject> platforms;
    private World world;
    private float highestPlatformY;
    private float cameraY;
    private Random random;

    private PlateObject floorPlatform;
    private float lastSpawnHeight;

    private static final float MOVING_PLATFORM_CHANCE = 0.1f;

    public PlatformManager(World world) {
        this.world = world;
        this.platforms = new ArrayList<>();
        this.highestPlatformY = 400;
        this.cameraY = 0;
        this.random = new Random();
        this.lastSpawnHeight = 400;

        createInitialPlatforms();
    }

    private void createInitialPlatforms() {

        float floorY = 150;
        floorPlatform = new PlateObject(floorY, GameSettings.SCREEN_WIDTH * 0.9f,
            GameSettings.PLATFORM_HEIGHT * 2f, GameSettings.PLATE_BIT, world, GameSettings.SCREEN_WIDTH, false);
        platforms.add(floorPlatform);


        float startPlatformY = 250;
        createPlatform(startPlatformY, GameSettings.PLATFORM_WIDTH * 1.8f, GameSettings.PLATFORM_HEIGHT);


        float currentY = startPlatformY;
        for (int i = 1; i < 12; i++) {
            float spacing = getRandomSpacing();
            currentY += spacing;
            createPlatform(currentY, getRandomPlatformWidth(), GameSettings.PLATFORM_HEIGHT);
        }
        highestPlatformY = currentY;
        lastSpawnHeight = highestPlatformY;


    }

    private float getRandomSpacing() {

        float baseSpacing = 280f;
        float variation = random.nextFloat() * 40f - 20f;
        return Math.max(250f, Math.min(320f, baseSpacing + variation));
    }

    private float getRandomPlatformWidth() {
        if (random.nextFloat() > 0.8f) {
            return GameSettings.PLATFORM_WIDTH * 1.3f;
        }
        return GameSettings.PLATFORM_WIDTH;
    }

    private void createPlatform(float y, float width, float height) {
        try {

            float minX = width / 2f + 20f;
            float maxX = GameSettings.SCREEN_WIDTH - width / 2f - 20f;
            float randomX = minX + random.nextFloat() * (maxX - minX);

            boolean isMoving = random.nextFloat() < MOVING_PLATFORM_CHANCE;


            PlateObject platform = createPlatformAtPosition(randomX, y, width, height, isMoving);
            platforms.add(platform);
            highestPlatformY = Math.max(highestPlatformY, y);
        } catch (Exception e) {

        }
    }


    private PlateObject createPlatformAtPosition(float x, float y, float width, float height, boolean isMoving) {
        PlateObject platform = new PlateObject(y, width, height, GameSettings.PLATE_BIT, world, GameSettings.SCREEN_WIDTH, isMoving);

        platform.body.setTransform(x * GameSettings.SCALE, y * GameSettings.SCALE, 0);
        return platform;
    }

    public void update(float delta, float currentCameraY, DoodleObject doodle) {
        this.cameraY = currentCameraY;

        for (PlateObject platform : platforms) {
            platform.update(delta);
        }


        Iterator<PlateObject> iterator = platforms.iterator();
        int removedCount = 0;
        while (iterator.hasNext()) {
            PlateObject platform = iterator.next();
            if (platform != floorPlatform && platform.getY() < cameraY - 1500) {
                if (platform.body != null) {
                    world.destroyBody(platform.body);
                }
                iterator.remove();
                removedCount++;
            }
        }


        spawnPlatformsAsNeeded(currentCameraY);
    }

    private void spawnPlatformsAsNeeded(float currentCameraY) {


        float spawnTriggerHeight = lastSpawnHeight - 500f;

        if (currentCameraY > spawnTriggerHeight) {
            int platformsToSpawn = 3 + random.nextInt(3);
            float currentY = highestPlatformY;

            for (int i = 0; i < platformsToSpawn; i++) {
                float spacing = getRandomSpacing();
                currentY += spacing;
                createPlatform(currentY, getRandomPlatformWidth(), GameSettings.PLATFORM_HEIGHT);
            }

            lastSpawnHeight = highestPlatformY;

        }


        ensureMinimumPlatforms(currentCameraY);
    }

    private void ensureMinimumPlatforms(float currentCameraY) {
        float lookAheadHeight = currentCameraY + GameSettings.SCREEN_HEIGHT * 1.0f;

        if (highestPlatformY < lookAheadHeight) {
            int additionalPlatforms = 4;
            float currentY = highestPlatformY;

            for (int i = 0; i < additionalPlatforms; i++) {
                float spacing = getRandomSpacing();
                currentY += spacing;
                createPlatform(currentY, getRandomPlatformWidth(), GameSettings.PLATFORM_HEIGHT);
            }


        }
    }

    public void drawAll(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        for (PlateObject platform : platforms) {
            if (platform != null) {
                platform.draw(batch);
            }
        }
    }

    public float getStartY() {
        return 350;
    }

    public float getFloorY() {
        return 150;
    }

    public int getPlatformCount() {
        return platforms.size();
    }

    public float getHighestPlatformY() {
        return highestPlatformY;
    }

    public void dispose() {
        for (PlateObject platform : platforms) {
            if (platform != null) {
                platform.dispose();
            }
        }
        platforms.clear();
    }
}
