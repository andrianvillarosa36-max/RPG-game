package com.chuck.rpggame.core;

/**
 * Template for an active ability: tryUse() handles the shared cooldown/SP
 * bookkeeping, apply() is where each concrete skill does its own thing.
 */
public abstract class Skill {
    private final String name;
    private final long cooldownMs;
    private final int spCost;
    private long lastUsedTime = -1_000_000L;

    protected Skill(String name, long cooldownMs, int spCost) {
        this.name = name;
        this.cooldownMs = cooldownMs;
        this.spCost = spCost;
    }

    public boolean canUse(Player player, long nowMs) {
        return nowMs - lastUsedTime >= cooldownMs && player.getSp() >= spCost;
    }

    /** Returns true if the skill actually fired. */
    public boolean tryUse(Player player, GameWorld world, long nowMs) {
        if (!canUse(player, nowMs)) return false;
        lastUsedTime = nowMs;
        player.spendSp(spCost);
        apply(player, world, nowMs);
        return true;
    }

    protected abstract void apply(Player player, GameWorld world, long nowMs);

    public String getName() {
        return name;
    }

    public long getCooldownMs() {
        return cooldownMs;
    }

    /** 0 when ready, approaching 1 right after use. */
    public float getCooldownRemainingRatio(long nowMs) {
        long elapsed = nowMs - lastUsedTime;
        if (elapsed >= cooldownMs) return 0f;
        return 1f - (float) elapsed / cooldownMs;
    }

    public int getSpCost() {
        return spCost;
    }
}
