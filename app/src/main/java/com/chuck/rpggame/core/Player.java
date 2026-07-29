package com.chuck.rpggame.core;

public class Player extends Entity {
    private static final int STARTING_MAX_HP = 100;

    private int level = 1;
    private int xp = 0;
    private int xpToNextLevel = 100;

    private int baseStrength = 10;
    private int baseMaxHp = STARTING_MAX_HP;

    private int equipAttackBonus = 0;
    private int equipDefenseBonus = 0;
    private int equipMaxHpBonus = 0;

    private Item equippedWeapon;
    private Item equippedArmor;
    private Item equippedAccessory;

    private final Inventory inventory = new Inventory(20);

    private float sp = 50f;
    private static final float MAX_SP = 50f;
    private static final float SP_REGEN_PER_SEC = 5f;

    private final Skill[] skills = new Skill[] {
            new PowerStrikeSkill(),
            new WhirlwindSkill(),
            new HealSkill()
    };

    private final float moveSpeed = 220f; // px/sec
    private float dirX = 0, dirY = 0;
    private float facingX = 0, facingY = 1;

    private long lastAttackTime = 0;
    private static final long ATTACK_COOLDOWN_MS = 400;
    private static final float ATTACK_RANGE = 70f;

    public Player(float x, float y) {
        super(x, y, 48, 48, STARTING_MAX_HP);
    }

    /** dx/dy expected roughly in [-1, 1], as fed by the virtual joystick. */
    public void setMoveDirection(float dx, float dy) {
        dirX = dx;
        dirY = dy;
        if (dx != 0 || dy != 0) {
            facingX = dx;
            facingY = dy;
        }
    }

    public void update(float deltaSeconds) {
        x += dirX * moveSpeed * deltaSeconds;
        y += dirY * moveSpeed * deltaSeconds;
        sp = Math.min(MAX_SP, sp + SP_REGEN_PER_SEC * deltaSeconds);
    }

    @Override
    public void takeDamage(int amount) {
        int reduced = Math.max(1, amount - equipDefenseBonus);
        super.takeDamage(reduced);
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    public boolean canAttack(long nowMs) {
        return nowMs - lastAttackTime >= ATTACK_COOLDOWN_MS;
    }

    public void performAttack(long nowMs) {
        lastAttackTime = nowMs;
    }

    public int getAttackDamage() {
        return baseStrength + level * 2 + equipAttackBonus;
    }

    public void gainXp(int amount) {
        xp += amount;
        while (xp >= xpToNextLevel) {
            xp -= xpToNextLevel;
            levelUp();
        }
    }

    private void levelUp() {
        level++;
        baseMaxHp += 20;
        maxHp = baseMaxHp + equipMaxHpBonus;
        hp = maxHp;
        baseStrength += 3;
        xpToNextLevel = Math.round(xpToNextLevel * 1.25f);
    }

    public void equipWeapon(Item item) {
        if (item != null && item.getType() != Item.Type.WEAPON) return;
        if (equippedWeapon != null) removeBonus(equippedWeapon);
        equippedWeapon = item;
        if (item != null) applyBonus(item);
    }

    public void equipArmor(Item item) {
        if (item != null && item.getType() != Item.Type.ARMOR) return;
        if (equippedArmor != null) removeBonus(equippedArmor);
        equippedArmor = item;
        if (item != null) applyBonus(item);
    }

    public void equipAccessory(Item item) {
        if (item != null && item.getType() != Item.Type.ACCESSORY) return;
        if (equippedAccessory != null) removeBonus(equippedAccessory);
        equippedAccessory = item;
        if (item != null) applyBonus(item);
    }

    private void applyBonus(Item item) {
        equipAttackBonus += item.getAttackBonus();
        equipDefenseBonus += item.getDefenseBonus();
        equipMaxHpBonus += item.getHpBonus();
        maxHp = baseMaxHp + equipMaxHpBonus;
        if (hp > maxHp) hp = maxHp;
    }

    private void removeBonus(Item item) {
        equipAttackBonus -= item.getAttackBonus();
        equipDefenseBonus -= item.getDefenseBonus();
        equipMaxHpBonus -= item.getHpBonus();
        maxHp = baseMaxHp + equipMaxHpBonus;
        if (hp > maxHp) hp = maxHp;
    }

    public Item getEquippedWeapon() {
        return equippedWeapon;
    }

    public Item getEquippedArmor() {
        return equippedArmor;
    }

    public Item getEquippedAccessory() {
        return equippedAccessory;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Skill getSkill(int index) {
        if (index < 0 || index >= skills.length) return null;
        return skills[index];
    }

    public int getSp() {
        return (int) sp;
    }

    public int getMaxSp() {
        return (int) MAX_SP;
    }

    public void spendSp(int amount) {
        sp = Math.max(0, sp - amount);
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getXpToNextLevel() {
        return xpToNextLevel;
    }

    public float getFacingX() {
        return facingX;
    }

    public float getFacingY() {
        return facingY;
    }

    public float getAttackRange() {
        return ATTACK_RANGE;
    }
}
