package io.github.some_example_name.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.github.some_example_name.MyGdxGame;
import io.github.some_example_name.Achievement;
import io.github.some_example_name.Static.GameResources;
import io.github.some_example_name.Static.GameSettings;
import io.github.some_example_name.components.ButtonView;

public class AchievementScreen extends ScreenAdapter {

    private static final String BLACKOUT_MIDDLE_IMG_PATH = "blackout_middle.png";

    private enum AchievementView {
        COMPLETED, INCOMPLETE
    }

    private AchievementView currentView = AchievementView.COMPLETED;

    private final MyGdxGame game;
    private Stage stage;
    private Texture backgroundTexture;
    private Texture blackoutTexture;

    private Texture contentBgTexture;
    private NinePatchDrawable contentBgDrawable;

    private Table mainLayoutTable;
    private Table contentContainerTable;

    private TextButton backTextButton;

    private Label.LabelStyle titleStyle;
    private TextButton.TextButtonStyle tabButtonStyle;
    private Label.LabelStyle nameStyle;
    private Label.LabelStyle descStyle;
    private NinePatchDrawable separatorDrawable;
    private Texture onePixelTexture;

    private Texture achievementRowBgTexture;
    private NinePatchDrawable achievementRowBgDrawable;

    private List<Achievement> allAchievements;
    private List<Achievement> completedAchievements;
    private List<Achievement> incompleteAchievements;

    public AchievementScreen(final MyGdxGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        if (stage == null) {
            this.stage = new Stage(new ScreenViewport());
        }
        Gdx.input.setInputProcessor(stage);
        stage.clear();

        backgroundTexture = new Texture(Gdx.files.internal(GameResources.BACKGROUND_IMG_PATH));
        loadAndFilterAchievements();

        try {
            blackoutTexture = new Texture(Gdx.files.internal(BLACKOUT_MIDDLE_IMG_PATH));
        } catch (Exception e) {
            Gdx.app.error("AchievementScreen", "Error loading blackout texture: " + e.getMessage());
        }

        prepareStyles();
        setupUI();
    }

    private void loadAndFilterAchievements() {
        if (game.achievementManager == null) return;

        allAchievements = game.achievementManager.getAchievements();
        completedAchievements = new ArrayList<>();
        incompleteAchievements = new ArrayList<>();

        for(Achievement ach : allAchievements) {
            if (ach.unlocked) {
                completedAchievements.add(ach);
            } else {
                incompleteAchievements.add(ach);
            }
        }

        Comparator<Achievement> titleComparator = new Comparator<Achievement>() {
            @Override
            public int compare(Achievement a1, Achievement a2) {
                return a1.title.compareTo(a2.title);
            }
        };

        Collections.sort(completedAchievements, titleComparator);
        Collections.sort(incompleteAchievements, titleComparator);
    }

    private void setupUI() {
        mainLayoutTable = new Table();
        mainLayoutTable.setFillParent(true);

        mainLayoutTable.top().center();

        Label titleLabel = new Label("ACHIEVEMENTS", titleStyle);
        mainLayoutTable.add(titleLabel).padTop(10).padBottom(5).row();

        Table tabButtonsTable = new Table();

        TextButton completedButton = new TextButton("COMPLETED (" + completedAchievements.size() + ")", tabButtonStyle);
        TextButton incompleteButton = new TextButton("INCOMPLETE (" + incompleteAchievements.size() + ")", tabButtonStyle);

        completedButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentView != AchievementView.COMPLETED) {
                    currentView = AchievementView.COMPLETED;
                    updateContent();
                }
            }
        });

        incompleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentView != AchievementView.INCOMPLETE) {
                    currentView = AchievementView.INCOMPLETE;
                    updateContent();
                }
            }
        });

        float tabBtnHeight = 35;
        float tabHorizontalPad = 10;
        float tabVerticalPad = 5;

        tabButtonsTable.add(completedButton).height(tabBtnHeight).pad(tabVerticalPad, tabHorizontalPad, tabVerticalPad, tabHorizontalPad).minWidth(0);
        tabButtonsTable.add(incompleteButton).height(tabBtnHeight).pad(tabVerticalPad, tabHorizontalPad, tabVerticalPad, tabHorizontalPad).minWidth(0).row();

        mainLayoutTable.add(tabButtonsTable).row();

        contentContainerTable = new Table();

        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(new Color(0f, 0f, 0f, 0.7f));
        bgPixmap.fill();
        contentBgTexture = new Texture(bgPixmap);
        bgPixmap.dispose();
        contentBgDrawable = new NinePatchDrawable(new NinePatch(contentBgTexture, 0, 0, 0, 0));

        contentContainerTable.setBackground(contentBgDrawable);

        contentContainerTable.top().pad(5, 0, 5, 0);

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();

        ScrollPane scrollPane = new ScrollPane(contentContainerTable, scrollStyle);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        float scrollPaneWidth = GameSettings.SCREEN_WIDTH * 0.6f;

        mainLayoutTable.add(scrollPane)
            .expandY()
            .fillY()
            .width(scrollPaneWidth)
            .padTop(10)
            .padBottom(10)
            .row();

        backTextButton = new TextButton("BACK", tabButtonStyle);
        backTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.menuScreen);
            }
        });

        mainLayoutTable.add(backTextButton).width(GameSettings.SCREEN_WIDTH * 0.35f).height(60).padBottom(10).row();

        stage.addActor(mainLayoutTable);

        updateContent();
    }

    private void updateContent() {
        contentContainerTable.clear();

        List<Achievement> listToDisplay = (currentView == AchievementView.COMPLETED)
            ? completedAchievements
            : incompleteAchievements;


        if (listToDisplay.isEmpty()) {
            String message = (currentView == AchievementView.COMPLETED)
                ? "No achievements completed yet."
                : "No incomplete achievements!";
            Label emptyLabel = new Label(message, nameStyle);
            emptyLabel.setColor(Color.WHITE);
            contentContainerTable.add(emptyLabel).pad(50).row();
        } else {
            for (Achievement ach : listToDisplay) {
                addAchievementRow(contentContainerTable, ach.title, ach.description, ach.unlocked);
            }
        }
    }

    private void addAchievementRow(Table parentTable, String name, String desc, boolean unlocked) {
        Color titleColor = unlocked ? Color.YELLOW : Color.LIGHT_GRAY;
        Color descColor = unlocked ? Color.WHITE : Color.GRAY;
        String prefix = unlocked ? "★ " : "☐ ";

        Table entryTable = new Table();
        entryTable.center().pad(5);

        if (achievementRowBgDrawable != null) {
            entryTable.setBackground(achievementRowBgDrawable);
        }

        float rowWidth = GameSettings.SCREEN_WIDTH * 0.5f;

        Table textTable = new Table();
        textTable.center();

        Label nameLabel = new Label(prefix + name, nameStyle);
        nameLabel.setColor(titleColor);
        nameLabel.setWrap(true);
        nameLabel.setAlignment(Align.center);

        textTable.add(nameLabel).width(rowWidth).center().pad(0, 0, 0, 0).row();

        Label descLabel = new Label(desc, descStyle);
        descLabel.setColor(descColor);
        descLabel.setWrap(true);
        descLabel.setAlignment(Align.center);

        textTable.add(descLabel).width(rowWidth).center().pad(0, 0, 5, 0).row();

        entryTable.add(textTable).row();

        parentTable.add(entryTable).width(GameSettings.SCREEN_WIDTH * 0.55f).padBottom(5).row();

        Table separatorTable = new Table();
        separatorTable.setBackground(separatorDrawable);
        parentTable.add(separatorTable).height(1).width(GameSettings.SCREEN_WIDTH * 0.5f).padBottom(5).row();
    }

    private void prepareStyles() {
        titleStyle = new Label.LabelStyle(game.largeWhiteFont, Color.WHITE);
        nameStyle = new Label.LabelStyle(game.commonWhiteFont, Color.WHITE);
        descStyle = new Label.LabelStyle(game.commonWhiteFont, Color.WHITE);

        Pixmap rowBgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        rowBgPixmap.setColor(new Color(0.1f, 0.1f, 0.1f, 0.5f));
        rowBgPixmap.fill();
        achievementRowBgTexture = new Texture(rowBgPixmap);
        rowBgPixmap.dispose();
        achievementRowBgDrawable = new NinePatchDrawable(new NinePatch(achievementRowBgTexture, 0, 0, 0, 0));

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.setColor(Color.DARK_GRAY);
        pixmap.fill();
        onePixelTexture = new Texture(pixmap);
        pixmap.dispose();

        NinePatch ninePatch = new NinePatch(onePixelTexture, 0, 0, 0, 0);
        separatorDrawable = new NinePatchDrawable(ninePatch);

        TextureRegionDrawable buttonDrawable;
        try {
            Texture buttonTexture = new Texture(Gdx.files.internal(GameResources.BUTTON_SHORT_BG_IMG_PATH));
            buttonDrawable = new TextureRegionDrawable(buttonTexture);
        } catch (Exception e) {
            Gdx.app.error("AchievementScreen", "Error loading button texture for tabs: " + e.getMessage());
            buttonDrawable = null;
        }

        tabButtonStyle = new TextButton.TextButtonStyle(buttonDrawable, buttonDrawable, buttonDrawable, game.commonBlackFont);

        tabButtonStyle.font.getData().markupEnabled = true;

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        if (backgroundTexture != null) {
            game.batch.draw(backgroundTexture, 0, 0, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        }
        game.batch.end();

        stage.act(delta);
        stage.draw();

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
            backgroundTexture = null;
        }
        if (blackoutTexture != null) {
            blackoutTexture.dispose();
            blackoutTexture = null;
        }
        if (onePixelTexture != null) {
            onePixelTexture.dispose();
            onePixelTexture = null;
        }
        if (contentBgTexture != null) {
            contentBgTexture.dispose();
            contentBgTexture = null;
        }
        if (achievementRowBgTexture != null) {
            achievementRowBgTexture.dispose();
            achievementRowBgTexture = null;
        }
    }

    private void handleInput() {
    }


    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }
}
