package com.chuck.rpggame.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameWorld {
    private final Player player;
    private final List<Enemy> enemies = new ArrayList<>();
    private final float worldWidth, worldHeight;
    private final Random random = new Random();

    private long lastSpawnTime = 0;
    private static final long SPAWN_INTERVAL_MS = 3000;
    private static final int MAX_ENEMIES = 6;
    private static final float LOOT_DROP_CHANCE = 0.35f;

    public GameWorld(float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.player = new Player(worldWidth / 2f, worldHeight / 2f);
        spawnEnemy();
        spawnEnemy();
        spawnEnemy();
    }

    private void spawnEnemy() {
        float ex = random.nextFloat() * Math.max(1f, worldWidth - 44f);
        float ey = random.nextFloat() * Math.max(1f, worldHeight - 44f);
        enemies.add(new Enemy(ex, ey));
    }

    /** Call once per frame. nowMs should be a monotonically increasing wall clock. */
    public void update(float deltaSeconds, long nowMs) {
        player.update(deltaSeconds);
        clampToWorld(player);

        Iterator<Enemy> it = enemies.iterator();
        while (it.hasNext()) {
            Enemy e = it.next();
            if (!e.isAlive()) {
                player.gainXp(e.getXpReward());
                if (random.nextFloat() < LOOT_DROP_CHANCE) {
                    player.getInventory().add(ItemFactory.randomDrop());
                }
                it.remove();
                continue;
            }
            e.update(deltaSeconds, player);
            clampToWorld(e);
            if (player.intersects(e) && e.canDealContactDamage(nowMs)) {
                int scaledDamage = Math.round(e.getContactDamage() * GameSettings.enemyDamageMultiplier());
                player.takeDamage(Math.max(1, scaledDamage));
                e.markContactDamageDealt(nowMs);
            }
        }

        long scaledSpawnInterval = Math.round(SPAWN_INTERVAL_MS * GameSettings.enemySpawnIntervalMultiplier());
        if (nowMs - lastSpawnTime > scaledSpawnInterval && enemies.size() < MAX_ENEMIES) {
            spawnEnemy();
            lastSpawnTime = nowMs;
        }
    }

    public void tryPlayerAttack(long nowMs) {
        if (!player.canAttack(nowMs)) return;
        player.performAttack(nowMs);
        damageEnemiesInRange(player.getAttackDamage(), player.getAttackRange());
    }

    public boolean tryPlayerSkill(int index, long nowMs) {
        Skill skill = player.getSkill(index);
        if (skill == null) return false;
        return skill.tryUse(player, this, nowMs);
    }

    /** Damages every living enemy within `range` of the player's center. Shared by the basic attack and skills. */
    public void damageEnemiesInRange(int damage, float range) {
        float pcx = player.getX() + player.getWidth() / 2f;
        float pcy = player.getY() + player.getHeight() / 2f;
        for (Enemy e : enemies) {
            if (!e.isAlive()) continue;
            float ecx = e.getX() + e.getWidth() / 2f;
            float ecy = e.getY() + e.getHeight() / 2f;
            float dist = (float) Math.sqrt((ecx - pcx) * (ecx - pcx) + (ecy - pcy) * (ecy - pcy));
            if (dist <= range) {
                e.takeDamage(damage);
            }
        }
    }

    private void clampToWorld(Entity e) {
        if (e.getX() < 0) e.setX(0);
        if (e.getY() < 0) e.setY(0);
        if (e.getX() > worldWidth - e.getWidth()) e.setX(worldWidth - e.getWidth());
        if (e.getY() > worldHeight - e.getHeight()) e.setY(worldHeight - e.getHeight());
    }

    public Player getPlayer() {
        return player;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public float getWorldWidth() {
        return worldWidth;
    }

    public float getWorldHeight() {
        return worldHeight;
    }
}
