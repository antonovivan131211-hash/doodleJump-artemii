package io.github.some_example_name.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.some_example_name.Static.GameResources;
import io.github.some_example_name.Static.GameSettings;
import io.github.some_example_name.Managers.ScoreManager;
import io.github.some_example_name.MyGdxGame;
import io.github.some_example_name.components.ButtonView;
import io.github.some_example_name.components.ImageView;
import io.github.some_example_name.components.TextView;

public class RecordsScreen extends ScreenAdapter {

    private static final String BLACKOUT_MIDDLE_IMG_PATH = "blackout_middle.png";

    MyGdxGame myGdxGame;
    ImageView backgroundView;

    ImageView blackoutImageView;

    TextView titleView, highScoreView, lastScoreView, minScoreView, totalGamesView;
    ButtonView backButton, settingsButton;

    public RecordsScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        backgroundView = new ImageView(0, 0, 720, 1280, GameResources.BACKGROUND_IMG_PATH);
        titleView = new TextView(myGdxGame.largeWhiteFont, 250, 1100, "Records");

        float blockX = 85;
        float blockY = 550;
        float blockWidth = 550;
        float blockHeight = 450;
        float textX = blockX + 35;
        float textYStart = blockY + blockHeight - 40;
        float textSpacing = 60;

        blackoutImageView = new ImageView(
            (int) blockX,
            (int) blockY,
            (int) blockWidth,
            (int) blockHeight,
            BLACKOUT_MIDDLE_IMG_PATH
        );

        highScoreView = new TextView(myGdxGame.commonWhiteFont, textX, textYStart, "");
        lastScoreView = new TextView(myGdxGame.commonWhiteFont, textX, textYStart - textSpacing, "");
        minScoreView = new TextView(myGdxGame.commonWhiteFont, textX, textYStart - 2 * textSpacing, "");
        totalGamesView = new TextView(myGdxGame.commonWhiteFont, textX, textYStart - 3 * textSpacing, "");

        float buttonWidth = 200;
        float buttonHeight = 70;
        float spacing = 40;
        float startX = (GameSettings.SCREEN_WIDTH - (2 * buttonWidth + spacing)) / 2;
        float buttonY = 400;

        backButton = new ButtonView(
            (int) startX, (int) buttonY,
            (int) buttonWidth, (int) buttonHeight,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "Back"
        );

        settingsButton = new ButtonView(
            (int) (startX + buttonWidth + spacing), (int) buttonY,
            (int) buttonWidth, (int) buttonHeight,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "Settings"
        );

        updateScoresDisplay();
    }

    @Override
    public void show() {

        updateScoresDisplay();


        myGdxGame.camera.position.set(
            GameSettings.SCREEN_WIDTH / 2,
            GameSettings.SCREEN_HEIGHT / 2,
            0
        );
        myGdxGame.camera.update();
    }

    private void updateScoresDisplay() {
        myGdxGame.scoreManager.loadScores();
        ScoreManager.ScoreData scores = myGdxGame.scoreManager.getScoreData();

        highScoreView.setText("High Score: " + scores.highScore);
        lastScoreView.setText("Last Score: " + scores.lastScore);
        minScoreView.setText("Min Score: " + scores.minScore);
        totalGamesView.setText("Total Games: " + scores.totalGames);
    }

    @Override
    public void render(float delta) {
        handleInput();

        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();

        backgroundView.draw(myGdxGame.batch);
        titleView.draw(myGdxGame.batch);

        blackoutImageView.draw(myGdxGame.batch);

        highScoreView.draw(myGdxGame.batch);
        lastScoreView.draw(myGdxGame.batch);
        minScoreView.draw(myGdxGame.batch);
        totalGamesView.draw(myGdxGame.batch);

        backButton.draw(myGdxGame.batch);
        settingsButton.draw(myGdxGame.batch);

        myGdxGame.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (backButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.menuScreen);
            }

            if (settingsButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.settingsScreen);
            }
        }
    }

    @Override
    public void dispose() {
        backgroundView.dispose();
        titleView.dispose();
        blackoutImageView.dispose();
        highScoreView.dispose();
        lastScoreView.dispose();
        minScoreView.dispose();
        totalGamesView.dispose();
        backButton.dispose();
        settingsButton.dispose();
    }
}
