package io.github.some_example_name.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

import io.github.some_example_name.Managers.ScoreManager;
import io.github.some_example_name.MyGdxGame;
import io.github.some_example_name.Static.GameResources;
import io.github.some_example_name.Static.GameSettings;
import io.github.some_example_name.components.ButtonView;
import io.github.some_example_name.components.ImageView;
import io.github.some_example_name.components.TextView;


public class SettingsScreen extends ScreenAdapter {


    private static final String BLACKOUT_MIDDLE_IMG_PATH = "blackout_middle.png";

    MyGdxGame myGdxGame;

    ImageView backgroundView;
    TextView titleTextView;
    ImageView blackoutImageView;
    ButtonView returnButton;
    TextView musicSettingView;
    TextView soundSettingView;
    TextView timerSettingView;
    TextView clearRecordsSettingView;
    TextView clearAchievementsSettingView;

    public SettingsScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        backgroundView = new ImageView(0,0, GameSettings.SCREEN_WIDTH,GameSettings.SCREEN_HEIGHT, GameResources.BACKGROUND_IMG_PATH);
        titleTextView = new TextView(myGdxGame.largeWhiteFont, 256, 956, "Settings");

        float textX = 120;
        float textYStart = 800;
        float textSpacing = 70;
        float buttonY = 400;

        blackoutImageView = new ImageView(85, 365,550,471, BLACKOUT_MIDDLE_IMG_PATH);

        musicSettingView = new TextView(myGdxGame.commonWhiteFont, textX, textYStart, "Music: " + translateStateToText(ScoreManager.loadIsMusicOn()));
        soundSettingView = new TextView(myGdxGame.commonWhiteFont, textX, textYStart - textSpacing, "Sound: " + translateStateToText(ScoreManager.loadIsSoundOn()));
        timerSettingView = new TextView(myGdxGame.commonWhiteFont, textX, textYStart - 2 * textSpacing, "Timer: " + translateStateToText(ScoreManager.loadTimerVisibility()));

        clearAchievementsSettingView = new TextView(myGdxGame.commonWhiteFont, textX, textYStart - 3 * textSpacing, "Clear Achievements");

        clearRecordsSettingView = new TextView(myGdxGame.commonWhiteFont, textX, textYStart - 4 * textSpacing, "Clear Records");

        returnButton = new ButtonView(
            280, buttonY,
            160, 70,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "return"
        );

    }

    @Override
    public void render(float delta) {
        handleInput();
        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();

        backgroundView.draw(myGdxGame.batch);
        titleTextView.draw(myGdxGame.batch);
        blackoutImageView.draw(myGdxGame.batch);

        musicSettingView.draw(myGdxGame.batch);
        soundSettingView.draw(myGdxGame.batch);
        timerSettingView.draw(myGdxGame.batch);
        clearAchievementsSettingView.draw(myGdxGame.batch);
        clearRecordsSettingView.draw(myGdxGame.batch);

        returnButton.draw(myGdxGame.batch);


        myGdxGame.batch.end();
    }

    @Override
    public void show() {
        musicSettingView.setText("Music: " + translateStateToText(ScoreManager.loadIsMusicOn()));
        soundSettingView.setText("Sound: " + translateStateToText(ScoreManager.loadIsSoundOn()));
        timerSettingView.setText("Timer: " + translateStateToText(ScoreManager.loadTimerVisibility()));
        clearAchievementsSettingView.setText("Clear Achievements");
        clearRecordsSettingView.setText("Clear Records");
    }

    void handleInput() {
        if (Gdx.input.justTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (returnButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.menuScreen);
            }
            if (clearRecordsSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.scoreManager.loadScores();
                myGdxGame.scoreManager.getScoreData().highScore = 0;
                myGdxGame.scoreManager.getScoreData().minScore = 0;
                myGdxGame.scoreManager.getScoreData().totalGames = 0;
                myGdxGame.scoreManager.saveScore(0);

                ScoreManager.saveTableOfRecords(new ArrayList<>());
                clearRecordsSettingView.setText("Clear Records (Cleared)");
            }
            if (clearAchievementsSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.achievementManager.clearAchievements();
                clearAchievementsSettingView.setText("Clear Achievements (Cleared)");
            }
            if (musicSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                ScoreManager.saveMusicSettings(!ScoreManager.loadIsMusicOn());
                musicSettingView.setText("Music: " + translateStateToText(ScoreManager.loadIsMusicOn()));
                myGdxGame.soundManager.updateMusicFlag();
            }
            if (soundSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                ScoreManager.saveSoundSettings(!ScoreManager.loadIsSoundOn());
                soundSettingView.setText("Sound: " + translateStateToText(ScoreManager.loadIsSoundOn()));
                myGdxGame.soundManager.updateSoundState();
            }
            if (timerSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                ScoreManager.saveTimerVisibility(!ScoreManager.loadTimerVisibility());
                timerSettingView.setText("Timer: " + translateStateToText(ScoreManager.loadTimerVisibility()));
            }
        }
    }
    private String translateStateToText(boolean state) {
        return state ? "ON" : "OFF";
    }
    @Override
    public void dispose() {
        backgroundView.dispose();
        titleTextView.dispose();
        blackoutImageView.dispose();
        returnButton.dispose();
        musicSettingView.dispose();
        soundSettingView.dispose();
        timerSettingView.dispose();
        clearRecordsSettingView.dispose();
        clearAchievementsSettingView.dispose();
    }
}
