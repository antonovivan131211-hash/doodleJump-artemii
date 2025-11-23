package io.github.some_example_name;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;

public class Achievement {
    public String id;
    public String title;
    public String description;
    public boolean unlocked;
    public float showTimer;
    public boolean isShowing;

    private static final float SHOW_DURATION = 3.5f;
    private static final float ANIMATION_DURATION = 0.5f;
    private static final float PADDING = 15f;

    private float screenWidth = 720f;
    private float startX;
    private float targetX;
    private float endX;
    private float currentX;
    private float y;


    private float bgWidth = 350f;
    private float bgHeight = 80f;

    public Achievement(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.unlocked = false;
        this.showTimer = 0;
        this.isShowing = false;


        this.startX = screenWidth + 10;
        this.targetX = screenWidth - bgWidth - 20;
        this.endX = screenWidth + 10;

        this.currentX = startX;
    }


    public void setYPosition(float y) {
        this.y = y;
    }

    public void show() {
        this.unlocked = true;
        this.isShowing = true;
        this.showTimer = SHOW_DURATION;
        this.currentX = startX;
    }

    public void update(float delta) {
        if (isShowing) {
            showTimer -= delta;

            float timeElapsed = SHOW_DURATION - showTimer;

            if (timeElapsed < ANIMATION_DURATION) {
                float progress = timeElapsed / ANIMATION_DURATION;
                currentX = Interpolation.pow3Out.apply(startX, targetX, progress);
            } else if (showTimer > ANIMATION_DURATION) {
                currentX = targetX;
            } else {
                float progress = 1.0f - (showTimer / ANIMATION_DURATION);
                currentX = Interpolation.pow3In.apply(targetX, endX, progress);
            }

            if (showTimer <= 0) {
                isShowing = false;
            }
        }
    }

    public void draw(Batch batch, BitmapFont font) {
        if (!isShowing || font == null) return;

        float alpha = 1.0f;


        if (showTimer > SHOW_DURATION - ANIMATION_DURATION) {

            float animProgress = (SHOW_DURATION - showTimer) / ANIMATION_DURATION;
            alpha = Interpolation.pow2Out.apply(animProgress);
        } else if (showTimer < ANIMATION_DURATION) {

            float animProgress = showTimer / ANIMATION_DURATION;
            alpha = Interpolation.pow2Out.apply(animProgress);
        }


        Color oldBatchColor = batch.getColor();


        font.setColor(0, 0, 0, alpha);


        float textX = currentX + PADDING;
        float titleY = y + bgHeight - PADDING;
        float descY = y + PADDING + 10;


        font.draw(batch, "Achievement unlocked!", textX, titleY);
        font.draw(batch, title, textX, titleY - 20);
        font.draw(batch, description, textX, descY);

        font.setColor(Color.WHITE);
        batch.setColor(oldBatchColor);
    }

    @Override
    public String toString() {
        return String.format("Achievement{id='%s', title='%s', unlocked=%s}", id, title, unlocked);
    }
}
