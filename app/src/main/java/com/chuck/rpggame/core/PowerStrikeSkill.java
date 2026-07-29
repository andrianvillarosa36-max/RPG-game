package com.chuck.rpggame.core;

public class PowerStrikeSkill extends Skill {
    private static final long COOLDOWN_MS = 2000;
    private static final int SP_COST = 15;
    private static final float DAMAGE_MULTIPLIER = 2f;

    public PowerStrikeSkill() {
        super("Power Strike", COOLDOWN_MS, SP_COST);
    }

    @Override
    protected void apply(Player player, GameWorld world, long nowMs) {
        int damage = Math.round(player.getAttackDamage() * DAMAGE_MULTIPLIER);
        world.damageEnemiesInRange(damage, player.getAttackRange());
    }
}
