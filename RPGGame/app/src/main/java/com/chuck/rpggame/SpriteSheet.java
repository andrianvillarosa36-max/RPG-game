package com.chuck.rpggame;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * Wraps a bitmap laid out as a grid of equally-sized frames. Works the same
 * whether the bitmap came from BitmapFactory.decodeResource(...) (real art)
 * or was generated at runtime (see PlaceholderArt, today's stand-in).
 */
public class SpriteSheet {
    private final Bitmap bitmap;
    private final int frameWidth;
    private final int frameHeight;

    public SpriteSheet(Bitmap bitmap, int frameWidth, int frameHeight) {
        this.bitmap = bitmap;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
    }

    public Rect frameRect(int row, int column) {
        int left = column * frameWidth;
        int top = row * frameHeight;
        return new Rect(left, top, left + frameWidth, top + frameHeight);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    /** Pure function of time so no per-entity animation state needs to be tracked. */
    public static int frameForTime(long nowMs, long frameDurationMs, int frameCount) {
        return (int) ((nowMs / frameDurationMs) % frameCount);
    }
}
