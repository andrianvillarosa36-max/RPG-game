package com.chuck.rpggame;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class SpriteSheet {
    private final Bitmap bitmap;
    private final int frameWidth;
    private final int frameHeight;
    private final int columnsPerRow;

    public SpriteSheet(Bitmap bitmap, int frameWidth, int frameHeight, int columnsPerRow) {
        this.bitmap = bitmap;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.columnsPerRow = Math.max(1, columnsPerRow);
    }

    public Rect frameRect(int row, int column) {
        int left = column * frameWidth;
        int top = row * frameHeight;
        return new Rect(left, top, left + frameWidth, top + frameHeight);
    }

    public Rect frameRectForIndex(int index) {
        int row = index / columnsPerRow;
        int column = index % columnsPerRow;
        return frameRect(row, column);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public static int frameForTime(long nowMs, long frameDurationMs, int frameCount) {
        return (int) ((nowMs / frameDurationMs) % frameCount);
    }
}
