package com.chuck.rpggame.core;

public class WhirlwindSkill extends Skill {
    private static final long COOLDOWN_MS = 4000;
    private static final int SP_COST = 25;
    private static final float RANGE_MULTIPLIER = 1.8f;

    public WhirlwindSkill() {
        super("Whirlwind", COOLDOWN_MS, SP_COST);
    }

    @Override
    protected void apply(Player player, GameWorld world, long nowMs) {
        world.damageEnemiesInRange(player.getAttackDamage(), player.getAttackRange() * RANGE_MULTIPLIER);
    }
}
