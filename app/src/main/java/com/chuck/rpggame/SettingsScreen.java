package com.chuck.rpggame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.chuck.rpggame.core.GameSettings;

public class SettingsScreen {
    private final RectF panelBounds;
    private final RectF difficultyButton;
    private final RectF joystickSizeButton;
    private final RectF backButton;

    private final Paint overlayPaint = new Paint();
    private final Paint panelPaint = new Paint();
    private final Paint titlePaint = new Paint();
    private final Paint buttonPaint = new Paint();
    private final Paint buttonTextPaint = new Paint();

    public SettingsScreen(float w, float h) {
        float pw = w * 0.5f;
        float ph = h * 0.6f;
        panelBounds = new RectF((w - pw) / 2f, (h - ph) / 2f, (w + pw) / 2f, (h + ph) / 2f);

        float bw = pw * 0.8f;
        float bh = ph * 0.15f;
        float cx = panelBounds.centerX();
        float startY = panelBounds.top + ph * 0.3f;
        float gap = bh * 1.3f;

        difficultyButton = new RectF(cx - bw / 2f, startY, cx + bw / 2f, startY + bh);
        joystickSizeButton = new RectF(cx - bw / 2f, startY + gap, cx + bw / 2f, startY + gap + bh);
        backButton = new RectF(cx - bw / 2f, startY + gap * 2.4f, cx + bw / 2f, startY + gap * 2.4f + bh);

        overlayPaint.setColor(Color.argb(150, 0, 0, 0));
        panelPaint.setColor(Color.argb(235, 30, 35, 30));

        titlePaint.setColor(Color.WHITE);
        titlePaint.setTextSize(bh * 0.5f);
        titlePaint.setAntiAlias(true);
        titlePaint.setFakeBoldText(true);
        titlePaint.setTextAlign(Paint.Align.CENTER);

        buttonPaint.setColor(Color.argb(200, 70, 110, 70));

        buttonTextPaint.setColor(Color.WHITE);
        buttonTextPaint.setTextSize(bh * 0.36f);
        buttonTextPaint.setAntiAlias(true);
        buttonTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void draw(Canvas canvas, float screenW, float screenH) {
        canvas.drawRect(0, 0, screenW, screenH, overlayPaint);
        canvas.drawRoundRect(panelBounds, 20, 20, panelPaint);
        canvas.drawText("Settings", panelBounds.centerX(), panelBounds.top + panelBounds.height() * 0.15f, titlePaint);

        drawButton(canvas, difficultyButton, "Difficulty: " + GameSettings.getDifficulty());
        drawButton(canvas, joystickSizeButton, "Joystick: " + GameSettings.getJoystickSize());
        drawButton(canvas, backButton, "Back");
    }

    private void drawButton(Canvas canvas, RectF bounds, String label) {
        canvas.drawRoundRect(bounds, 16, 16, buttonPaint);
        canvas.drawText(label, bounds.centerX(), bounds.centerY() + buttonTextPaint.getTextSize() * 0.35f, buttonTextPaint);
    }

    /** Cycles whichever setting was tapped. Caller is responsible for Back (isBack). */
    public void handleTouch(float x, float y) {
        if (difficultyButton.contains(x, y)) {
            GameSettings.cycleDifficulty();
        } else if (joystickSizeButton.contains(x, y)) {
            GameSettings.cycleJoystickSize();
        }
    }

    public boolean isBack(float x, float y) {
        return backButton.contains(x, y);
    }
}
