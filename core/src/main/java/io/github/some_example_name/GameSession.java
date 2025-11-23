package io.github.some_example_name;

import com.badlogic.gdx.utils.TimeUtils;

import io.github.some_example_name.Enum.GameState;

public class GameSession {

    long sessionStartTime;
    public GameState state;
    long pauseStartTime;
    private int score;

    public GameSession() {
        state = GameState.PLAYING;
    }

    public int getScore() {
        return score;
    }

    public void startGame() {
        sessionStartTime = TimeUtils.millis();
        state = GameState.PLAYING;
    }

    public void pauseGame() {
        state = GameState.PAUSED;
        pauseStartTime = TimeUtils.millis();
    }

    public void resumeGame() {
        state = GameState.PLAYING;
        sessionStartTime += TimeUtils.millis() - pauseStartTime;
    }

    public void endGame() {
        state = GameState.ENDED;
    }
}
