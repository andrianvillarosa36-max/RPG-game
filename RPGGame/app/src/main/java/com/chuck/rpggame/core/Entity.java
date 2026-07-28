package com.chuck.rpggame.core;

/**
 * Base class for anything that lives in the game world and can take damage.
 * Deliberately free of android.* imports so this whole package can be
 * reasoned about (and eventually unit tested) without the Android SDK.
 */
public abstract class Entity {
    protected float x, y;
    protected float width, height;
    protected int hp, maxHp;
    protected boolean alive = true;

    public Entity(float x, float y, float width, float height, int maxHp) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.maxHp = maxHp;
        this.hp = maxHp;
    }

    public void takeDamage(int amount) {
        hp -= amount;
        if (hp <= 0) {
            hp = 0;
            alive = false;
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public boolean intersects(Entity other) {
        return x < other.x + other.width &&
                x + width > other.x &&
                y < other.y + other.height &&
                y + height > other.y;
    }
}
