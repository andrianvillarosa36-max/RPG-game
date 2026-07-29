package com.chuck.rpggame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.chuck.rpggame.core.Enemy;
import com.chuck.rpggame.core.GameWorld;
import com.chuck.rpggame.core.Player;
import com.chuck.rpggame.core.Skill;

import java.util.concurrent.atomic.AtomicBoolean;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private Thread gameThread;
    private volatile boolean running = false;

    private GameWorld world;
    private VirtualJoystick joystick;
    private InventoryPanel inventoryPanel;

    private RectF attackButtonBounds;
    private RectF[] skillButtonBounds;
    private RectF inventoryToggleBounds;

    private int attackButtonPointerId = -1;
    private volatile boolean attackPressedThisFrame = false;
    private final AtomicBoolean[] skillPressed = {
            new AtomicBoolean(false), new AtomicBoolean(false), new AtomicBoolean(false)
    };
    private static final String[] SKILL_LABELS = {"PWR", "WHR", "HL"};

    private SpriteSheet playerSheet;
    private SpriteSheet enemySheet;
    private static final long SPRITE_FRAME_MS = 150;
    private static final int SPRITE_FRAME_COUNT = 4;

    private final Paint groundPaint = new Paint();
    private final Paint gridPaint = new Paint();
    private final Paint hpBarBackPaint = new Paint();
    private final Paint hpBarFrontPaint = new Paint();
    private final Paint uiPaint = new Paint();
    private final Paint joystickBasePaint = new Paint();
    private final Paint joystickKnobPaint = new Paint();
    private final Paint attackButtonPaint = new Paint();
    private final Paint skillButtonReadyPaint = new Paint();
    private final Paint skillButtonCooldownPaint = new Paint();
    private final Paint inventoryToggleButtonPaint = new Paint();
    private final Paint textPaint = new Paint();
    private final Paint smallTextPaint = new Paint();

    private long lastFrameTime;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        initPaints();
        initSprites();
    }

    private void initPaints() {
        groundPaint.setColor(Color.rgb(40, 60, 40));
        gridPaint.setColor(Color.rgb(55, 80, 55));
        gridPaint.setStrokeWidth(2f);

        hpBarBackPaint.setColor(Color.rgb(50, 20, 20));
        hpBarFrontPaint.setColor(Color.rgb(210, 60, 60));

        uiPaint.setColor(Color.WHITE);
        uiPaint.setTextSize(36f);
        uiPaint.setAntiAlias(true);

        joystickBasePaint.setColor(Color.argb(90, 255, 255, 255));
        joystickKnobPaint.setColor(Color.argb(160, 255, 255, 255));

        attackButtonPaint.setColor(Color.argb(140, 220, 60, 60));
        skillButtonReadyPaint.setColor(Color.argb(170, 80, 120, 200));
        skillButtonCooldownPaint.setColor(Color.argb(90, 70, 70, 80));
        inventoryToggleButtonPaint.setColor(Color.argb(160, 120, 100, 60));

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(28f);
        textPaint.setAntiAlias(true);

        smallTextPaint.setColor(Color.WHITE);
        smallTextPaint.setTextSize(22f);
        smallTextPaint.setAntiAlias(true);
    }

    private void initSprites() {
        playerSheet = new SpriteSheet(
                PlaceholderArt.generateCharacterSheet(48, 48, Color.rgb(70, 160, 235), Color.WHITE), 48, 48);
        enemySheet = new SpriteSheet(
                PlaceholderArt.generateCharacterSheet(44, 44, Color.rgb(200, 70, 70), Color.rgb(30, 10, 10)), 44, 44);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int w = getWidth();
        int h = getHeight();
        if (world == null) {
            world = new GameWorld(w, h);
        }

        float joyRadius = Math.min(w, h) * 0.12f;
        joystick = new VirtualJoystick(joyRadius * 1.4f, h - joyRadius * 1.4f, joyRadius);

        float btnRadius = joyRadius * 0.9f;
        attackButtonBounds = new RectF(
                w - btnRadius * 2.6f, h - btnRadius * 2.6f,
                w - btnRadius * 0.6f, h - btnRadius * 0.6f
        );

        float skillBtnSize = btnRadius * 1.3f;
        skillButtonBounds = new RectF[3];
        for (int i = 0; i < 3; i++) {
            float bx = w - btnRadius * 2.6f - (3 - i) * (skillBtnSize + 16f);
            float by = h - btnRadius * 2.6f;
            skillButtonBounds[i] = new RectF(bx, by, bx + skillBtnSize, by + skillBtnSize);
        }

        float bagSize = Math.min(w, h) * 0.09f;
        inventoryToggleBounds = new RectF(w - bagSize - 24f, 24f, w - 24f, 24f + bagSize);

        inventoryPanel = new InventoryPanel(w, h);

        lastFrameTime = System.currentTimeMillis();
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        running = false;
        boolean retry = true;
        while (retry) {
            try {
                gameThread.join();
                retry = false;
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public void run() {
        while (running) {
            long now = System.currentTimeMillis();
            float delta = (now - lastFrameTime) / 1000f;
            if (delta > 0.05f) delta = 0.05f; // clamp so a stall doesn't teleport entities
            lastFrameTime = now;

            update(delta, now);
            draw();
        }
    }

    private void update(float delta, long now) {
        if (world == null) return;
        if (inventoryPanel != null && inventoryPanel.isOpen()) {
            return; // paused while browsing inventory
        }
        world.getPlayer().setMoveDirection(joystick.getDirX(), joystick.getDirY());
        world.update(delta, now);
        if (attackPressedThisFrame) {
            world.tryPlayerAttack(now);
            attackPressedThisFrame = false;
        }
        for (int i = 0; i < skillPressed.length; i++) {
            if (skillPressed[i].compareAndSet(true, false)) {
                world.tryPlayerSkill(i, now);
            }
        }
    }

    private void draw() {
        if (!getHolder().getSurface().isValid()) return;
        Canvas canvas = getHolder().lockCanvas();
        if (canvas == null) return;
        long nowMs = System.currentTimeMillis();
        try {
            drawWorld(canvas, nowMs);
            drawHud(canvas);
            drawControls(canvas, nowMs);
            inventoryPanel.draw(canvas, world.getPlayer());
        } finally {
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void drawWorld(Canvas canvas, long nowMs) {
        canvas.drawColor(Color.rgb(25, 35, 25));
        canvas.drawRect(0, 0, world.getWorldWidth(), world.getWorldHeight(), groundPaint);

        int gridSize = 64;
        for (int gx = 0; gx < world.getWorldWidth(); gx += gridSize) {
            canvas.drawLine(gx, 0, gx, world.getWorldHeight(), gridPaint);
        }
        for (int gy = 0; gy < world.getWorldHeight(); gy += gridSize) {
            canvas.drawLine(0, gy, world.getWorldWidth(), gy, gridPaint);
        }

        int frame = SpriteSheet.frameForTime(nowMs, SPRITE_FRAME_MS, SPRITE_FRAME_COUNT);
        Rect enemySrc = enemySheet.frameRect(0, frame);
        for (Enemy e : world.getEnemies()) {
            RectF dst = new RectF(e.getX(), e.getY(), e.getX() + e.getWidth(), e.getY() + e.getHeight());
            canvas.drawBitmap(enemySheet.getBitmap(), enemySrc, dst, null);
            drawHpBar(canvas, e.getX(), e.getY() - 12, e.getWidth(), e.getHp(), e.getMaxHp());
        }

        Player p = world.getPlayer();
        Rect playerSrc = playerSheet.frameRect(0, frame);
        RectF playerDst = new RectF(p.getX(), p.getY(), p.getX() + p.getWidth(), p.getY() + p.getHeight());
        boolean facingLeft = p.getFacingX() < -0.01f;
        canvas.save();
        if (facingLeft) {
            canvas.scale(-1, 1, playerDst.centerX(), playerDst.centerY());
        }
        canvas.drawBitmap(playerSheet.getBitmap(), playerSrc, playerDst, null);
        canvas.restore();
        drawHpBar(canvas, p.getX(), p.getY() - 12, p.getWidth(), p.getHp(), p.getMaxHp());
    }

    private void drawHpBar(Canvas canvas, float x, float y, float width, int hp, int maxHp) {
        canvas.drawRect(x, y, x + width, y + 6, hpBarBackPaint);
        float ratio = maxHp > 0 ? (float) hp / maxHp : 0;
        canvas.drawRect(x, y, x + width * ratio, y + 6, hpBarFrontPaint);
    }

    private void drawHud(Canvas canvas) {
        Player p = world.getPlayer();
        canvas.drawText("Lv " + p.getLevel(), 24, 48, uiPaint);
        canvas.drawText("HP " + p.getHp() + "/" + p.getMaxHp(), 24, 88, textPaint);
        canvas.drawText("SP " + p.getSp() + "/" + p.getMaxSp(), 24, 118, textPaint);
        canvas.drawText("XP " + p.getXp() + "/" + p.getXpToNextLevel(), 24, 148, textPaint);
    }

    private void drawControls(Canvas canvas, long nowMs) {
        joystick.draw(canvas, joystickBasePaint, joystickKnobPaint);

        canvas.drawRoundRect(attackButtonBounds, 24, 24, attackButtonPaint);
        canvas.drawText("ATK", attackButtonBounds.centerX() - 40, attackButtonBounds.centerY() + 12, textPaint);

        Player p = world.getPlayer();
        for (int i = 0; i < skillButtonBounds.length; i++) {
            RectF b = skillButtonBounds[i];
            Skill skill = p.getSkill(i);
            float cdRatio = skill.getCooldownRemainingRatio(nowMs);
            boolean enoughSp = p.getSp() >= skill.getSpCost();
            Paint bg = (cdRatio > 0f || !enoughSp) ? skillButtonCooldownPaint : skillButtonReadyPaint;
            canvas.drawRoundRect(b, 16, 16, bg);
            canvas.drawText(SKILL_LABELS[i], b.left + 8, b.centerY() + 6, smallTextPaint);
            if (cdRatio > 0f) {
                int secondsLeft = (int) Math.ceil(cdRatio * skill.getCooldownMs() / 1000f);
                canvas.drawText(String.valueOf(secondsLeft), b.centerX() - 6, b.bottom + 22, smallTextPaint);
            }
        }

        canvas.drawRoundRect(inventoryToggleBounds, 14, 14, inventoryToggleButtonPaint);
        canvas.drawText("BAG", inventoryToggleBounds.left + 4, inventoryToggleBounds.centerY() + 8, smallTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                float x = event.getX(pointerIndex);
                float y = event.getY(pointerIndex);

                if (inventoryPanel.isOpen()) {
                    inventoryPanel.handleTouch(x, y, world.getPlayer());
                    break;
                }
                if (inventoryToggleBounds.contains(x, y)) {
                    inventoryPanel.toggle();
                    break;
                }
                if (attackButtonBounds.contains(x, y) && attackButtonPointerId == -1) {
                    attackButtonPointerId = pointerId;
                    attackPressedThisFrame = true;
                    break;
                }
                boolean skillHit = false;
                for (int i = 0; i < skillButtonBounds.length; i++) {
                    if (skillButtonBounds[i].contains(x, y)) {
                        skillPressed[i].set(true);
                        skillHit = true;
                        break;
                    }
                }
                if (skillHit) break;

                if (joystick.isTouchInside(x, y) && !joystick.isActive()) {
                    joystick.startTouch(pointerId, x, y);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                for (int i = 0; i < event.getPointerCount(); i++) {
                    int pid = event.getPointerId(i);
                    if (pid == joystick.getPointerId()) {
                        joystick.moveTouch(pid, event.getX(i), event.getY(i));
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP: {
                if (pointerId == attackButtonPointerId) {
                    attackButtonPointerId = -1;
                }
                joystick.endTouch(pointerId);
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                attackButtonPointerId = -1;
                joystick.endTouch(joystick.getPointerId());
                break;
            }
            default:
                break;
        }
        return true;
    }
}
