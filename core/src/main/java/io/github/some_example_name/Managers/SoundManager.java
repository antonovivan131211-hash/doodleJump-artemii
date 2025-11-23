package io.github.some_example_name.Managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import io.github.some_example_name.Static.GameResources;

public class SoundManager {
    private Sound jumpSound;
    private Sound deathSound;

    private Music backgroundMusic;
    private boolean soundsEnabled = true;
    private boolean musicEnabled = true;
    private float volume = 0.7f;

    public SoundManager() {
        loadSounds();

        loadMusic();

        updateSoundState();
        updateMusicFlag();
    }

    private void loadSounds() {
        try {
            jumpSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.JUMP_SOUND_PATH));
            deathSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.DEATH_SOUND_PATH));
            System.out.println("✅ Sounds loaded successfully");
        } catch (Exception e) {
            System.err.println("❌ Error loading sounds: " + e.getMessage());
            soundsEnabled = false;
        }
    }

    private void loadMusic() {
        try {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(GameResources.BACKGROUND_MUSIC_PATH));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(volume * 0.5f);
            System.out.println("✅ Background music loaded successfully");
        } catch (Exception e) {
            System.err.println("❌ Error loading background music: " + e.getMessage());
        }
    }

    public void updateMusicFlag() {
        musicEnabled = ScoreManager.loadIsMusicOn();

        if (backgroundMusic != null) {
            if (musicEnabled) {
                if (!backgroundMusic.isPlaying()) {
                    backgroundMusic.play();
                }
            } else {
                if (backgroundMusic.isPlaying()) {
                    backgroundMusic.stop();
                }
            }
        }
    }

    public void updateSoundState() {
        this.soundsEnabled = ScoreManager.loadIsSoundOn();
    }

    public void playJumpSound() {
        if (soundsEnabled && jumpSound != null) {
            jumpSound.play(volume);
        }
    }

    public void playDeathSound() {
        if (soundsEnabled && deathSound != null) {
            deathSound.play(volume);
        }
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0, Math.min(1, volume));
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(this.volume * 0.5f);
        }
    }

    public void dispose() {
        if (jumpSound != null) {
            jumpSound.dispose();
        }
        if (deathSound != null) {
            deathSound.dispose();
        }
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
    }
}
