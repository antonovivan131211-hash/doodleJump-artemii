package io.github.some_example_name.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.some_example_name.Static.GameResources;
import io.github.some_example_name.Static.GameSettings;
import io.github.some_example_name.MyGdxGame;
import io.github.some_example_name.components.ButtonView;
import io.github.some_example_name.components.ImageView;
import io.github.some_example_name.components.TextView;

public class MenuScreen extends ScreenAdapter {
    MyGdxGame myGdxGame;
    ImageView backgroundView;
    TextView titleView;
    ButtonView startButtonView, recordsButtonView, settingsButtonView, achievementsButtonView, exitButtonView;
    ButtonView authorsButtonView; // <-- Новая кнопка

    public MenuScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        backgroundView = new ImageView(0, 0, 720, 1280, GameResources.BACKGROUND_IMG_PATH);
        titleView = new TextView(myGdxGame.largeWhiteFont, 180, 960, "Doodle Jump");

        startButtonView = new ButtonView(140, 646, 440, 70, myGdxGame.commonBlackFont, GameResources.BUTTON_LONG_BG_IMG_PATH, "start");
        recordsButtonView = new ButtonView(140, 561, 440, 70, myGdxGame.commonBlackFont, GameResources.BUTTON_LONG_BG_IMG_PATH, "records");

        settingsButtonView = new ButtonView(140, 476, 440, 70, myGdxGame.commonBlackFont, GameResources.BUTTON_LONG_BG_IMG_PATH, "settings");

        achievementsButtonView = new ButtonView(140, 391, 440, 70, myGdxGame.commonBlackFont, GameResources.BUTTON_LONG_BG_IMG_PATH, "achievements");

        exitButtonView = new ButtonView(140, 306, 440, 70, myGdxGame.commonBlackFont, GameResources.BUTTON_LONG_BG_IMG_PATH, "exit");

        // Кнопка Авторов: маленькая, квадратная, с буквой 'A'. Размещаем внизу слева.
        float buttonSize = 70;
        authorsButtonView = new ButtonView(
            140, // x: Левый край
            200, // y: Ниже кнопки exit
            buttonSize,
            buttonSize,
            myGdxGame.largeWhiteFont, // Используем largeWhiteFont для буквы 'A'
            GameResources.BUTTON_SHORT_BG_IMG_PATH, // Используем короткий фон для квадратной кнопки
            "A" // Текст на кнопке
        );
    }

    @Override
    public void show() {
        myGdxGame.camera.position.set(
            GameSettings.SCREEN_WIDTH / 2,
            GameSettings.SCREEN_HEIGHT / 2,
            0
        );
        myGdxGame.camera.update();
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

        exitButtonView.draw(myGdxGame.batch);
        achievementsButtonView.draw(myGdxGame.batch);
        settingsButtonView.draw(myGdxGame.batch);
        recordsButtonView.draw(myGdxGame.batch);
        startButtonView.draw(myGdxGame.batch);
        authorsButtonView.draw(myGdxGame.batch); // <-- Отрисовка новой кнопки

        myGdxGame.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (startButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.gameScreen);
            }
            if (recordsButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.recordsScreen);
            }
            if (exitButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                Gdx.app.exit();
            }

            if (settingsButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.settingsScreen);
            }

            if (achievementsButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.achievementScreen);
            }

            if (authorsButtonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) { // <-- Обработка новой кнопки
                myGdxGame.setScreen(myGdxGame.authorsScreen);
            }
        }
    }


    @Override
    public void dispose() {
        backgroundView.dispose();
        titleView.dispose();
        exitButtonView.dispose();
        achievementsButtonView.dispose();
        settingsButtonView.dispose();
        recordsButtonView.dispose();
        startButtonView.dispose();
        authorsButtonView.dispose(); // <-- Освобождение ресурса
    }
}
