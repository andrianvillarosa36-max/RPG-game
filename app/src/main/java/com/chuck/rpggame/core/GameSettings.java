package com.chuck.rpggame.core;

/**
 * Simple global settings. Written only from the UI thread (taps on the
 * settings screen), read every frame from the game loop thread, so the
 * backing fields are volatile.
 */
public class GameSettings {
    public enum Difficulty { EASY, NORMAL, HARD }
    public enum JoystickSize { SMALL, MEDIUM, LARGE }

    private static volatile Difficulty difficulty = Difficulty.NORMAL;
    private static volatile JoystickSize joystickSize = JoystickSize.MEDIUM;

    public static Difficulty getDifficulty() {
        return difficulty;
    }

    public static void cycleDifficulty() {
        if (difficulty == Difficulty.EASY) difficulty = Difficulty.NORMAL;
        else if (difficulty == Difficulty.NORMAL) difficulty = Difficulty.HARD;
        else difficulty = Difficulty.EASY;
    }

    public static float enemyDamageMultiplier() {
        if (difficulty == Difficulty.EASY) return 0.6f;
        if (difficulty == Difficulty.HARD) return 1.6f;
        return 1f;
    }

    public static float enemySpawnIntervalMultiplier() {
        if (difficulty == Difficulty.EASY) return 1.5f;
        if (difficulty == Difficulty.HARD) return 0.6f;
        return 1f;
    }

    public static JoystickSize getJoystickSize() {
        return joystickSize;
    }

    public static void cycleJoystickSize() {
        if (joystickSize == JoystickSize.SMALL) joystickSize = JoystickSize.MEDIUM;
        else if (joystickSize == JoystickSize.MEDIUM) joystickSize = JoystickSize.LARGE;
        else joystickSize = JoystickSize.SMALL;
    }

    public static float joystickSizeMultiplier() {
        if (joystickSize == JoystickSize.SMALL) return 0.75f;
        if (joystickSize == JoystickSize.LARGE) return 1.3f;
        return 1f;
    }
}
