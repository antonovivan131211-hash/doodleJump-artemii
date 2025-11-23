package io.github.some_example_name.Screens;

import static io.github.some_example_name.Enum.GameState.PAUSED;
import static io.github.some_example_name.Enum.GameState.PLAYING;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.Iterator;

import io.github.some_example_name.Managers.ContactManager;
import io.github.some_example_name.Static.GameResources;
import io.github.some_example_name.GameSession;
import io.github.some_example_name.Static.GameSettings;
import io.github.some_example_name.MyGdxGame;
import io.github.some_example_name.Managers.PlatformManager;
import io.github.some_example_name.Managers.ScoreManager;
import io.github.some_example_name.components.ButtonView;
import io.github.some_example_name.components.ImageView;
import io.github.some_example_name.components.TextView;
import io.github.some_example_name.objects.BulletObject;
import io.github.some_example_name.objects.DoodleObject;
import io.github.some_example_name.objects.EnemyObject;
import io.github.some_example_name.Managers.AchievementManager;

public class GameScreen extends ScreenAdapter {
    MyGdxGame myGdxGame;
    ImageView backGround, topBlackoutView, fullBlackoutView;
    ButtonView buttonView, buttonView1, pauseButton, homeButton, continueButton;
    TextView pauseTextView, scoreTextView, gameOverTextView, timerTextView;
    ArrayList<BulletObject> bulletArray;
    ArrayList<EnemyObject> enemyArray;

    GameSession gameSession;
    Box2DDebugRenderer debugRenderer;
    PlatformManager platformManager;
    ContactManager contactManager;

    int r = 0;
    boolean canShot, hasShot;
    Batch batch;
    public DoodleObject doodleObject;
    boolean tr;
    private float cameraOffsetY;
    private float respawnTimer = 0;
    private boolean waitingForRespawn = false;
    private float lastEnemyY = 0;

    private float totalTimeElapsed;
    private boolean isTimerVisible;

    private OrthographicCamera uiCamera;

    private boolean leftButtonPressed = false;
    private boolean rightButtonPressed = false;
    public GameScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        batch = myGdxGame.batch;
        Box2D.init();
        bulletArray = new ArrayList<>();
        enemyArray = new ArrayList<>();

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);

        resetCamera();


        platformManager = new PlatformManager(myGdxGame.world);

        doodleObject = new DoodleObject(
            GameResources.DOODLE_PATH,
            GameSettings.SCREEN_WIDTH / 2,
            (int) platformManager.getStartY(),
            GameSettings.DOODLE_WIDTH,
            GameSettings.DOODLE_HEIGHT,
            GameSettings.DOODLE_BIT,
            myGdxGame.world,
            myGdxGame
        );

        contactManager = new ContactManager(myGdxGame.world, doodleObject, myGdxGame);

        topBlackoutView = new ImageView(0, 1180, 720, 100, GameResources.BLACKOUT_TOP_IMG_PATH);
        fullBlackoutView = new ImageView(0, 0, 720, 1280, GameResources.PAUSE_SCREEN_IMG_PATH);
        backGround = new ImageView(0, 0, 720, 1280, GameResources.BACKGROUND_PATH);
        pauseButton = new ButtonView(605, 1200, 46, 54, GameResources.PAUSE_IMG_PATH);
        buttonView = new ButtonView(0, 0, 630 / 3, 630 / 3, GameResources.BUTTON_FLIPED_PATH);
        gameSession = new GameSession();
        gameSession.startGame();
        buttonView1 = new ButtonView(510, 0, 630 / 3, 630 / 3, GameResources.BUTTON_PATH);
        debugRenderer = new Box2DDebugRenderer();
        pauseTextView = new TextView(myGdxGame.largeWhiteFont, 290, 950, "Pause");
        homeButton = new ButtonView(190, 750, 160, 70, myGdxGame.commonWhiteFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");
        continueButton = new ButtonView(390, 750, 160, 70, myGdxGame.commonWhiteFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Continue");
        scoreTextView = new TextView(myGdxGame.commonWhiteFont, 20, 1250, "Score: 0");
        gameOverTextView = new TextView(myGdxGame.largeWhiteFont, 200, 700, "Game Over");

        totalTimeElapsed = 0;
        isTimerVisible = ScoreManager.loadTimerVisibility();
        timerTextView = new TextView(myGdxGame.commonWhiteFont, 360, 1250, "Time: 00:00");

        cameraOffsetY = 0;
        lastEnemyY = platformManager.getStartY();
    }

    private void resetCamera() {
        myGdxGame.camera.position.set(
            GameSettings.SCREEN_WIDTH / 2,
            GameSettings.SCREEN_HEIGHT / 2,
            0
        );
        myGdxGame.camera.update();
    }

    @Override
    public void show() {
        resetCamera();
        cameraOffsetY = 0;

        myGdxGame.batch.setColor(Color.WHITE);

        if (!doodleObject.isAlive()) {
            restartGame();
        }

        isTimerVisible = ScoreManager.loadTimerVisibility();

        if (gameSession.state == PLAYING) {
            myGdxGame.setGamePaused(false);
        }
    }

    @Override
    public void render(float delta) {
        myGdxGame.stepWorld();

        if (doodleObject.isAlive() && gameSession.state == PLAYING) {

            if (myGdxGame.achievementManager != null) {
                myGdxGame.achievementManager.update(delta);
            }
            totalTimeElapsed += delta;

            doodleObject.updateCameraPosition(cameraOffsetY);
            doodleObject.update(delta);
            updateCamera();

            platformManager.update(delta, cameraOffsetY, doodleObject);

            doodleObject.updateMovement();

            updateBullets();

            updateEnemies(delta, doodleObject.getX(), doodleObject.getY());

            respawnTimer = 0;
            waitingForRespawn = false;
        }
        else if (!doodleObject.isAlive()) {
            if (!waitingForRespawn) {
                waitingForRespawn = true;
                respawnTimer = 0;
                System.out.println("Waiting for respawn...");
            }

            respawnTimer += delta;
            if (respawnTimer >= 2.0f) {
                restartGame();
            }
        }

        handleInput();
        draw();
    }


    private String formatTime(float totalSeconds) {
        int minutes = (int) (totalSeconds / 60);
        int seconds = (int) (totalSeconds % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }


    private void updateCamera() {
        if (!doodleObject.isAlive() || gameSession.state == PAUSED) return;

        float targetY = doodleObject.getY() - GameSettings.SCREEN_HEIGHT / 3;

        if (targetY > cameraOffsetY) {
            cameraOffsetY = targetY;
        }

        myGdxGame.camera.position.y = cameraOffsetY + GameSettings.SCREEN_HEIGHT / 2;
    }

    private void draw() {
        myGdxGame.batch.setColor(Color.WHITE);
        ScreenUtils.clear(Color.CLEAR);

        uiCamera.update();
        myGdxGame.batch.setProjectionMatrix(uiCamera.combined);
        myGdxGame.batch.begin();
        backGround.draw(myGdxGame.batch);
        myGdxGame.batch.end();

        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        myGdxGame.batch.begin();

        platformManager.drawAll(myGdxGame.batch);

        if (doodleObject.isAlive()) {
            doodleObject.draw(myGdxGame.batch);
        }

        for (EnemyObject enemy : enemyArray) {
            enemy.draw(myGdxGame.batch);
        }

        for (BulletObject bullet : bulletArray) {
            bullet.draw(myGdxGame.batch);
        }

        myGdxGame.batch.end();

        uiCamera.update();
        myGdxGame.batch.setProjectionMatrix(uiCamera.combined);
        myGdxGame.batch.begin();

        myGdxGame.batch.setColor(Color.WHITE);
        topBlackoutView.draw(myGdxGame.batch);
        pauseButton.draw(myGdxGame.batch);
        buttonView.draw(myGdxGame.batch);
        buttonView1.draw(myGdxGame.batch);

        scoreTextView.setText("Score: " + doodleObject.getCurrentScore());
        scoreTextView.draw(myGdxGame.batch);

        if (isTimerVisible) {
            timerTextView.setText("Time: " + formatTime(totalTimeElapsed));
            timerTextView.draw(myGdxGame.batch);
        }

        if (gameSession.state == PAUSED) {
            fullBlackoutView.draw(myGdxGame.batch);
            pauseTextView.draw(myGdxGame.batch);
            homeButton.draw(myGdxGame.batch);
            continueButton.draw(myGdxGame.batch);
        }

        if (!doodleObject.isAlive()) {
            if (waitingForRespawn) {
                int timeLeft = (int) (2.0f - respawnTimer) + 1;
                gameOverTextView.setText("Game Over\nRestart in: " + timeLeft);
            } else {
                gameOverTextView.setText("Game Over");
            }
            gameOverTextView.draw(myGdxGame.batch);
        }

        if (myGdxGame.achievementManager != null) {
            myGdxGame.achievementManager.draw(myGdxGame.batch, myGdxGame.commonWhiteFont);
        }

        myGdxGame.batch.end();
    }

    private void updateBullets() {
        for (int i = 0; i < bulletArray.size(); i++) {
            BulletObject bullet = bulletArray.get(i);

            bullet.setCurrentCameraY(cameraOffsetY);

            if (bullet.hasToBeDestroyed()) {
                myGdxGame.world.destroyBody(bullet.body);
                bulletArray.remove(i--);
            }
        }
    }

    private void updateEnemies(float delta, float doodleX, float doodleY) {
        Iterator<EnemyObject> iterator = enemyArray.iterator();
        while (iterator.hasNext()) {
            EnemyObject enemy = iterator.next();

            if (gameSession.state == PLAYING) {
                enemy.update(delta, doodleX, doodleY);
            }

            if (enemy.hasToBeDestroyed()) {
                myGdxGame.world.destroyBody(enemy.body);
                iterator.remove();
            }
        }
    }

    public void spawnNewEnemy() {
        float spawnY = myGdxGame.camera.position.y + GameSettings.SCREEN_HEIGHT / 2 + 100f;
        float spawnX = MathUtils.random(EnemyObject.ENEMY_WIDTH / 2f, GameSettings.SCREEN_WIDTH - EnemyObject.ENEMY_WIDTH / 2f);

        EnemyObject newEnemy = new EnemyObject(
            GameResources.TRASH_PATH,
            spawnX,
            spawnY,
            myGdxGame.world
        );
        enemyArray.add(newEnemy);
        lastEnemyY = spawnY;

        System.out.println(" Enemy spawned at: (" + spawnX + ", " + spawnY + ")");
    }


    private void handleInput() {
        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        uiCamera.unproject(touchPos);

        boolean currentLeftPressed = buttonView.isHit((int)touchPos.x, (int)touchPos.y) && Gdx.input.isTouched();
        boolean currentRightPressed = buttonView1.isHit((int)touchPos.x, (int)touchPos.y) && Gdx.input.isTouched();

        if (doodleObject.isAlive() && gameSession.state == PLAYING) {
            if (currentLeftPressed && !leftButtonPressed) {
                doodleObject.startMovingLeft();
                leftButtonPressed = true;
            } else if (!currentLeftPressed && leftButtonPressed) {
                doodleObject.stopMoving();
                leftButtonPressed = false;
            }

            if (currentRightPressed && !rightButtonPressed) {
                doodleObject.startMovingRight();
                rightButtonPressed = true;
            } else if (!currentRightPressed && rightButtonPressed) {
                doodleObject.stopMoving();
                rightButtonPressed = false;
            }

            if (Gdx.input.justTouched()) {
                if (pauseButton.isHit((int)touchPos.x, (int)touchPos.y)) {
                    gameSession.pauseGame();
                    myGdxGame.setGamePaused(true);
                    System.out.println("Game paused");
                }

                if (!(pauseButton.isHit((int)touchPos.x, (int)touchPos.y)) &&
                    !(buttonView1.isHit((int)touchPos.x, (int)touchPos.y)) &&
                    !(buttonView.isHit((int)touchPos.x, (int)touchPos.y)) &&
                    !(topBlackoutView.isHit((int)touchPos.x, (int)touchPos.y))) {

                    BulletObject laserBullet = new BulletObject(
                        doodleObject.getX(),
                        doodleObject.getY() + doodleObject.height / 2,
                        GameSettings.BULLET_WIDTH,
                        GameSettings.BULLET_HEIGHT,
                        GameResources.BULLET_IMG_PATH,
                        myGdxGame.world,
                        myGdxGame.soundManager
                    );
                    bulletArray.add(laserBullet);
                }
            }
        }

        if (Gdx.input.justTouched() && gameSession.state == PAUSED) {
            if (homeButton.isHit((int)touchPos.x, (int)touchPos.y)) {
                saveCurrentScore();
                resetGame();
                resetCamera();
                myGdxGame.setGamePaused(false);
                myGdxGame.setScreen(myGdxGame.menuScreen);
            }
            if (continueButton.isHit((int)touchPos.x, (int)touchPos.y)) {
                gameSession.resumeGame();
                myGdxGame.setGamePaused(false);
                myGdxGame.resetAccumulator();
                System.out.println("Game resumed");
            }
        }

        if (!Gdx.input.isTouched()) {
            if (leftButtonPressed || rightButtonPressed) {
                doodleObject.stopMoving();
                leftButtonPressed = false;
                rightButtonPressed = false;
            }
        }
    }

    private void saveCurrentScore() {
        int currentScore = doodleObject.getCurrentScore();
        if (currentScore > 0) {
            myGdxGame.scoreManager.saveScore(currentScore);
            System.out.println("Score saved: " + currentScore);
        }
    }

    private void resetGame() {
        System.out.println("Resetting game...");

        myGdxGame.disposeWorld();
        myGdxGame.createWorld();

        for (BulletObject bullet : bulletArray) {
        }
        bulletArray.clear();

        for (EnemyObject enemy : enemyArray) {
        }
        enemyArray.clear();

        platformManager.dispose();
        platformManager = new PlatformManager(myGdxGame.world);

        doodleObject = new DoodleObject(
            GameResources.DOODLE_PATH,
            GameSettings.SCREEN_WIDTH / 2,
            (int) platformManager.getStartY(),
            GameSettings.DOODLE_WIDTH,
            GameSettings.DOODLE_HEIGHT,
            GameSettings.DOODLE_BIT,
            myGdxGame.world,
            myGdxGame
        );

        contactManager = new ContactManager(myGdxGame.world, doodleObject, myGdxGame);

        resetCamera();
        cameraOffsetY = 0;

        gameSession = new GameSession();
        gameSession.startGame();

        waitingForRespawn = false;
        respawnTimer = 0;
        leftButtonPressed = false;
        rightButtonPressed = false;
        tr = false;
        lastEnemyY = platformManager.getStartY();

        totalTimeElapsed = 0;

        System.out.println("Game reset complete!");
    }

    private void restartGame() {
        System.out.println("Auto-restarting game...");

        int finalScore = doodleObject.getCurrentScore();
        if (finalScore > 0) {
            myGdxGame.scoreManager.saveScore(finalScore);
            System.out.println("Final score saved: " + finalScore);
        }

        resetGame();
    }

    public void setTr(boolean r, boolean t) {
        tr = r;
    }

    @Override
    public void dispose() {
        platformManager.dispose();
        if (backGround != null) {
            backGround.dispose();
        }
    }
}
