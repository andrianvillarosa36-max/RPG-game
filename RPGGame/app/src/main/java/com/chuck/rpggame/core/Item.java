package com.chuck.rpggame.core;

public class Item {
    public enum Type { WEAPON, ARMOR, ACCESSORY, CONSUMABLE }
    public enum Rarity { COMMON, RARE, EPIC }

    private final String name;
    private final Type type;
    private final Rarity rarity;
    private final int attackBonus;
    private final int defenseBonus;
    private final int hpBonus;

    public Item(String name, Type type, Rarity rarity, int attackBonus, int defenseBonus, int hpBonus) {
        this.name = name;
        this.type = type;
        this.rarity = rarity;
        this.attackBonus = attackBonus;
        this.defenseBonus = defenseBonus;
        this.hpBonus = hpBonus;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public int getHpBonus() {
        return hpBonus;
    }
}
