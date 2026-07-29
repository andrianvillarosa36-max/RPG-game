package com.chuck.rpggame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class GameOverScreen {
    private final float screenW, screenH;
    private final RectF respawnButton;
    private final RectF quitButton;

    private final Paint overlayPaint = new Paint();
    private final Paint titlePaint = new Paint();
    private final Paint statsPaint = new Paint();
    private final Paint buttonPaint = new Paint();
    private final Paint buttonTextPaint = new Paint();

    public GameOverScreen(float w, float h) {
        this.screenW = w;
        this.screenH = h;
        float bw = w * 0.34f;
        float bh = h * 0.1f;
        float cx = w / 2f;
        float startY = h * 0.55f;
        float gap = bh * 1.35f;

        respawnButton = new RectF(cx - bw / 2f, startY, cx + bw / 2f, startY + bh);
        quitButton = new RectF(cx - bw / 2f, startY + gap, cx + bw / 2f, startY + gap + bh);

        overlayPaint.setColor(Color.argb(210, 20, 0, 0));

        titlePaint.setColor(Color.rgb(230, 70, 70));
        titlePaint.setTextSize(w * 0.06f);
        titlePaint.setAntiAlias(true);
        titlePaint.setFakeBoldText(true);
        titlePaint.setTextAlign(Paint.Align.CENTER);

        statsPaint.setColor(Color.WHITE);
        statsPaint.setTextSize(w * 0.025f);
        statsPaint.setAntiAlias(true);
        statsPaint.setTextAlign(Paint.Align.CENTER);

        buttonPaint.setColor(Color.argb(200, 100, 50, 50));

        buttonTextPaint.setColor(Color.WHITE);
        buttonTextPaint.setTextSize(bh * 0.4f);
        buttonTextPaint.setAntiAlias(true);
        buttonTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void draw(Canvas canvas, int levelReached) {
        canvas.drawRect(0, 0, screenW, screenH, overlayPaint);
        canvas.drawText("YOU DIED", screenW / 2f, screenH * 0.32f, titlePaint);
        canvas.drawText("Reached level " + levelReached, screenW / 2f, screenH * 0.4f, statsPaint);

        drawButton(canvas, respawnButton, "Respawn");
        drawButton(canvas, quitButton, "Quit to Title");
    }

    private void drawButton(Canvas canvas, RectF bounds, String label) {
        canvas.drawRoundRect(bounds, 18, 18, buttonPaint);
        canvas.drawText(label, bounds.centerX(), bounds.centerY() + buttonTextPaint.getTextSize() * 0.35f, buttonTextPaint);
    }

    public boolean isRespawn(float x, float y) {
        return respawnButton.contains(x, y);
    }

    public boolean isQuit(float x, float y) {
        return quitButton.contains(x, y);
    }
}
