package com.chuck.rpggame;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * A draggable on-screen stick. Touch handling happens on the UI thread
 * (via GameView.onTouchEvent) while dirX/dirY are read every frame on the
 * game loop thread, so the fields that cross threads are volatile.
 */
public class VirtualJoystick {
    private final float baseX, baseY;
    private final float radius;

    private volatile float knobX, knobY;
    private volatile boolean active = false;
    private volatile int pointerId = -1;
    private volatile float dirX = 0, dirY = 0;

    public VirtualJoystick(float baseX, float baseY, float radius) {
        this.baseX = baseX;
        this.baseY = baseY;
        this.radius = radius;
        this.knobX = baseX;
        this.knobY = baseY;
    }

    public boolean isTouchInside(float x, float y) {
        float dx = x - baseX;
        float dy = y - baseY;
        return Math.sqrt(dx * dx + dy * dy) <= radius * 1.6f;
    }

    public void startTouch(int pointerId, float x, float y) {
        this.pointerId = pointerId;
        active = true;
        updateKnob(x, y);
    }

    public void moveTouch(int pointerId, float x, float y) {
        if (pointerId != this.pointerId) return;
        updateKnob(x, y);
    }

    public void endTouch(int pointerId) {
        if (pointerId != this.pointerId) return;
        active = false;
        this.pointerId = -1;
        knobX = baseX;
        knobY = baseY;
        dirX = 0;
        dirY = 0;
    }

    private void updateKnob(float x, float y) {
        float dx = x - baseX;
        float dy = y - baseY;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist > radius) {
            dx = dx / dist * radius;
            dy = dy / dist * radius;
            dist = radius;
        }
        knobX = baseX + dx;
        knobY = baseY + dy;
        if (dist > radius * 0.15f) {
            dirX = dx / radius;
            dirY = dy / radius;
        } else {
            dirX = 0;
            dirY = 0;
        }
    }

    public boolean isActive() {
        return active;
    }

    public int getPointerId() {
        return pointerId;
    }

    public float getDirX() {
        return dirX;
    }

    public float getDirY() {
        return dirY;
    }

    public void draw(Canvas canvas, Paint basePaint, Paint knobPaint) {
        canvas.drawCircle(baseX, baseY, radius, basePaint);
        canvas.drawCircle(knobX, knobY, radius * 0.45f, knobPaint);
    }
}
