package io.github.some_example_name.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.some_example_name.MyGdxGame;
import io.github.some_example_name.Static.GameResources;
import io.github.some_example_name.Static.GameSettings;
import io.github.some_example_name.components.ButtonView;
import io.github.some_example_name.components.ImageView;
import io.github.some_example_name.components.TextView;

public class AuthorsScreen extends ScreenAdapter {

    private static final String BLACKOUT_MIDDLE_IMG_PATH = "blackout_middle.png";

    MyGdxGame myGdxGame;

    ImageView backgroundView;
    ImageView blackoutImageView;
    TextView titleTextView;
    TextView author1NameView;
    TextView author1TGView;
    TextView author2NameView;
    TextView author2TGView;
    ButtonView returnButton;

    public AuthorsScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        backgroundView = new ImageView(0,0, GameSettings.SCREEN_WIDTH,GameSettings.SCREEN_HEIGHT, GameResources.BACKGROUND_IMG_PATH);
        blackoutImageView = new ImageView(85, 365, 550, 471, BLACKOUT_MIDDLE_IMG_PATH);


        float blackoutCenterX = blackoutImageView.getX() + blackoutImageView.getWidth() / 2f;
        float blackoutTopY = blackoutImageView.getY() + blackoutImageView.getHeight();

        GlyphLayout titleLayout = new GlyphLayout(myGdxGame.largeWhiteFont, "Authors");
        float titleWidth = titleLayout.width;

        titleTextView = new TextView(myGdxGame.largeWhiteFont,
            blackoutCenterX - titleWidth / 2,
            blackoutTopY - 70,
            "Authors");

        float authorStartX = blackoutImageView.getX() + 50;
        float authorStartY = blackoutTopY - 170;

        author1NameView = new TextView(myGdxGame.commonWhiteFont, authorStartX, authorStartY, "1. Antonov Ivan Romanovich");
        author1TGView = new TextView(myGdxGame.smallWhiteFont, authorStartX + 20, authorStartY - 30, "TG: @Silentlkn");

        author2NameView = new TextView(myGdxGame.commonWhiteFont, authorStartX, authorStartY - 90, "2. Tolmachev Artemiy Ilyich");
        author2TGView = new TextView(myGdxGame.smallWhiteFont, authorStartX + 20, authorStartY - 120, "TG: @Dervizen");


        returnButton = new ButtonView(
            blackoutCenterX - 160/2,
            blackoutImageView.getY() + 50,
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
        blackoutImageView.draw(myGdxGame.batch);

        titleTextView.draw(myGdxGame.batch);
        author1NameView.draw(myGdxGame.batch);
        author1TGView.draw(myGdxGame.batch);
        author2NameView.draw(myGdxGame.batch);
        author2TGView.draw(myGdxGame.batch);
        returnButton.draw(myGdxGame.batch);

        myGdxGame.batch.end();
    }

    void handleInput() {
        if (Gdx.input.justTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (returnButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.menuScreen);
            }
        }
    }

    @Override
    public void dispose() {
        backgroundView.dispose();
        blackoutImageView.dispose();
        titleTextView.dispose();
        author1NameView.dispose();
        author1TGView.dispose();
        author2NameView.dispose();
        author2TGView.dispose();
        returnButton.dispose();
    }
}
