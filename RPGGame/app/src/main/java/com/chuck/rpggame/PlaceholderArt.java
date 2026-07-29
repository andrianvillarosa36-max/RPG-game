package com.chuck.rpggame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Draws a simple 4-frame "walk bob" character sheet at runtime so the
 * SpriteSheet rendering pipeline has real pixels to push before any actual
 * art exists. To swap in real art later, replace the call sites in
 * GameView.initSprites() with
 * BitmapFactory.decodeResource(getResources(), R.drawable.your_sheet) —
 * everything downstream (frame slicing, walk timing, left/right flip) is
 * unaffected.
 */
public class PlaceholderArt {

    public static Bitmap generateCharacterSheet(int frameW, int frameH, int bodyColor, int accentColor) {
        int frames = 4;
        Bitmap sheet = Bitmap.createBitmap(frameW * frames, frameH, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(sheet);

        Paint body = new Paint();
        body.setColor(bodyColor);
        body.setAntiAlias(true);

        Paint accent = new Paint();
        accent.setColor(accentColor);
        accent.setAntiAlias(true);

        for (int i = 0; i < frames; i++) {
            float ox = i * frameW;
            float bob = (i % 2 == 0) ? 0f : Math.max(1f, frameH * 0.06f);

            float bodyLeft = ox + frameW * 0.25f;
            float bodyRight = ox + frameW * 0.75f;
            float bodyTop = frameH * 0.30f + bob;
            float bodyBottom = frameH * 0.85f + bob;
            canvas.drawRoundRect(bodyLeft, bodyTop, bodyRight, bodyBottom, 6f, 6f, body);

            float headCx = ox + frameW * 0.5f;
            float headCy = frameH * 0.22f + bob;
            canvas.drawCircle(headCx, headCy, frameW * 0.18f, body);

            float stripeLeft = ox + frameW * 0.42f;
            float stripeRight = ox + frameW * 0.58f;
            float stripeTop = frameH * 0.40f + bob;
            float stripeBottom = frameH * 0.65f + bob;
            canvas.drawRect(stripeLeft, stripeTop, stripeRight, stripeBottom, accent);
        }
        return sheet;
    }
}
