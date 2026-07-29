package com.chuck.rpggame.core;

public class HealSkill extends Skill {
    private static final long COOLDOWN_MS = 6000;
    private static final int SP_COST = 30;
    private static final int HEAL_AMOUNT = 40;

    public HealSkill() {
        super("Heal", COOLDOWN_MS, SP_COST);
    }

    @Override
    protected void apply(Player player, GameWorld world, long nowMs) {
        player.heal(HEAL_AMOUNT);
    }
}
