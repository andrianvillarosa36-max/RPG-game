package com.chuck.rpggame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class TitleScreen {
    private final RectF newGameButton;
    private final RectF settingsButton;
    private final RectF exitButton;

    private final Paint bgPaint = new Paint();
    private final Paint titlePaint = new Paint();
    private final Paint buttonPaint = new Paint();
    private final Paint buttonTextPaint = new Paint();

    public TitleScreen(float w, float h) {
        float bw = w * 0.34f;
        float bh = h * 0.11f;
        float cx = w / 2f;
        float startY = h * 0.52f;
        float gap = bh * 1.35f;

        newGameButton = new RectF(cx - bw / 2f, startY, cx + bw / 2f, startY + bh);
        settingsButton = new RectF(cx - bw / 2f, startY + gap, cx + bw / 2f, startY + gap + bh);
        exitButton = new RectF(cx - bw / 2f, startY + gap * 2, cx + bw / 2f, startY + gap * 2 + bh);

        bgPaint.setColor(Color.rgb(18, 24, 18));

        titlePaint.setColor(Color.WHITE);
        titlePaint.setTextSize(w * 0.055f);
        titlePaint.setAntiAlias(true);
        titlePaint.setFakeBoldText(true);
        titlePaint.setTextAlign(Paint.Align.CENTER);

        buttonPaint.setColor(Color.argb(200, 70, 110, 70));

        buttonTextPaint.setColor(Color.WHITE);
        buttonTextPaint.setTextSize(bh * 0.4f);
        buttonTextPaint.setAntiAlias(true);
        buttonTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void draw(Canvas canvas, float w, float h) {
        canvas.drawRect(0, 0, w, h, bgPaint);
        canvas.drawText("RPG GAME", w / 2f, h * 0.3f, titlePaint);

        drawButton(canvas, newGameButton, "New Game");
        drawButton(canvas, settingsButton, "Settings");
        drawButton(canvas, exitButton, "Exit");
    }

    private void drawButton(Canvas canvas, RectF bounds, String label) {
        canvas.drawRoundRect(bounds, 18, 18, buttonPaint);
        canvas.drawText(label, bounds.centerX(), bounds.centerY() + buttonTextPaint.getTextSize() * 0.35f, buttonTextPaint);
    }

    public boolean isNewGame(float x, float y) {
        return newGameButton.contains(x, y);
    }

    public boolean isSettings(float x, float y) {
        return settingsButton.contains(x, y);
    }

    public boolean isExit(float x, float y) {
        return exitButton.contains(x, y);
    }
}
