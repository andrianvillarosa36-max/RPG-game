package com.chuck.rpggame.core;

public class Enemy extends Entity {
    private final float moveSpeed = 90f;
    private final int contactDamage = 8;
    private final int xpReward = 25;
    private final float aggroRange = 260f;

    private long lastContactDamageTime = 0;
    private static final long CONTACT_DAMAGE_COOLDOWN_MS = 800;

    public Enemy(float x, float y) {
        super(x, y, 44, 44, 40);
    }

    public void update(float deltaSeconds, Player player) {
        if (!alive) return;
        float dx = player.getX() - x;
        float dy = player.getY() - y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist < aggroRange && dist > 1f) {
            x += (dx / dist) * moveSpeed * deltaSeconds;
            y += (dy / dist) * moveSpeed * deltaSeconds;
        }
    }

    public boolean canDealContactDamage(long nowMs) {
        return nowMs - lastContactDamageTime >= CONTACT_DAMAGE_COOLDOWN_MS;
    }

    public void markContactDamageDealt(long nowMs) {
        lastContactDamageTime = nowMs;
    }

    public int getContactDamage() {
        return contactDamage;
    }

    public int getXpReward() {
        return xpReward;
    }
}
