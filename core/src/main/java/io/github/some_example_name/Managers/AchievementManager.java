package io.github.some_example_name.Managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import io.github.some_example_name.Achievement;

public class AchievementManager {
    private HashMap<String, Achievement> achievements;
    private Json json;
    private FileHandle saveFile;

    private List<Achievement> activeAchievements;
    private static final float ACHIEVEMENT_HEIGHT_OFFSET = 100f;
    private static final float INITIAL_Y = 700f;

    public AchievementManager() {
        this.achievements = new HashMap<>();
        this.json = new Json();
        this.json.setOutputType(JsonWriter.OutputType.json);
        this.activeAchievements = new ArrayList<>();

        if (Gdx.files.isLocalStorageAvailable()) {
            saveFile = Gdx.files.local("achievements.json");
        } else {
            saveFile = Gdx.files.internal("achievements.json");
        }

        initializeAchievements();
        loadAchievements();
    }

    private void initializeAchievements() {
        addAchievement(new Achievement("welcome", "Welcome!", "First time playing"));
        addAchievement(new Achievement("first_jump", "First Jump", "Make first jump"));
        addAchievement(new Achievement("height_1000", "Height 1000", "Reach height 1000"));
        addAchievement(new Achievement("height_500", "Height 500", "Reach height 500"));
        addAchievement(new Achievement("height_5000", "Height 5000", "Reach height 5000"));
        addAchievement(new Achievement("height_10000", "Height 10000", "Reach height 10000"));
        addAchievement(new Achievement("height_100", "Height 100", "Reach height 100"));


        addAchievement(new Achievement("first_enemy", "First Enemy", "Encounter first enemy"));
        addAchievement(new Achievement("first_kill", "First Kill", "Defeat first enemy with a shot"));
    }

    public void addAchievement(Achievement achievement) {
        achievements.put(achievement.id, achievement);
    }

    public void unlockAchievement(String id) {
        Achievement achievement = achievements.get(id);
        if (achievement != null) {
            if (!achievement.unlocked) {
                achievement.unlocked = true;

                float yPos = INITIAL_Y;
                for (Achievement active : activeAchievements) {
                    yPos -= ACHIEVEMENT_HEIGHT_OFFSET;
                }
                achievement.setYPosition(yPos);

                achievement.show();

                activeAchievements.add(achievement);

                Gdx.app.log("ACHIEVEMENT", "Разблокировано: " + achievement.title);

                saveAchievements();

                try {
                    Gdx.input.vibrate(200);
                } catch (Exception e) {
                }
            }
        }
    }

    public void showAchievement(String id) {
        Achievement achievement = achievements.get(id);
        if (achievement != null && achievement.unlocked) {
            achievement.show();
        }
    }

    public List<Achievement> getAchievements() {
        return new ArrayList<>(achievements.values());
    }

    public void clearAchievements() {
        for (Achievement achievement : achievements.values()) {
            achievement.unlocked = false;
        }
        activeAchievements.clear();
        Gdx.app.log("ACHIEVEMENT", "Все достижения сброшены.");
        saveAchievements();
        loadAchievements();
    }


    public void update(float delta) {

        for (int i = activeAchievements.size() - 1; i >= 0; i--) {
            Achievement achievement = activeAchievements.get(i);
            achievement.update(delta);

            if (!achievement.isShowing) {
                activeAchievements.remove(i);
                repositionActiveAchievements();
            }
        }
    }

    private void repositionActiveAchievements() {
        float yPos = INITIAL_Y;
        for (Achievement active : activeAchievements) {
            active.setYPosition(yPos);
            yPos -= ACHIEVEMENT_HEIGHT_OFFSET;
        }
    }


    public void draw(Batch batch, BitmapFont font) {
        if (font == null) return;


        for (Achievement achievement : activeAchievements) {
            achievement.draw(batch, font);
        }
    }

    public void saveAchievements() {
        try {
            AchievementSaveData saveData = new AchievementSaveData();

            for (Achievement achievement : achievements.values()) {
                saveData.unlockedAchievements.put(achievement.id, achievement.unlocked);
            }

            String jsonData = json.toJson(saveData);
            saveFile.writeString(jsonData, false);

        } catch (Exception e) {
            Gdx.app.error("AchievementManager", "ERROR saving achievements: " + e.getMessage());
        }
    }

    public void loadAchievements() {
        try {
            if (saveFile.exists()) {
                String data = saveFile.readString();
                AchievementSaveData saveData = json.fromJson(AchievementSaveData.class, data);

                for (Map.Entry<String, Boolean> entry : saveData.unlockedAchievements.entrySet()) {
                    String achievementId = entry.getKey();
                    Boolean unlocked = entry.getValue();

                    Achievement achievement = achievements.get(achievementId);
                    if (achievement != null) {
                        achievement.unlocked = unlocked;
                    }
                }
            } else {
                saveAchievements();
            }

        } catch (Exception e) {
            Gdx.app.error("AchievementManager", "ERROR loading achievements: " + e.getMessage());
        }
    }

    public boolean isAchievementUnlocked(String id) {
        Achievement achievement = achievements.get(id);
        return achievement != null && achievement.unlocked;
    }

    public static class AchievementSaveData {
        public HashMap<String, Boolean> unlockedAchievements = new HashMap<>();
    }
}
