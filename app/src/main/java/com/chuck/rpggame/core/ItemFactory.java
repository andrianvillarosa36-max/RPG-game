package com.chuck.rpggame.core;

import java.util.Random;

public class ItemFactory {
    private static final Random RANDOM = new Random();

    private static final Item.Type[] EQUIP_TYPES = {
            Item.Type.WEAPON, Item.Type.ARMOR, Item.Type.ACCESSORY
    };

    private static final String[] WEAPON_NAMES = {"Shortsword", "Battle Axe", "War Hammer"};
    private static final String[] ARMOR_NAMES = {"Leather Vest", "Chainmail", "Plate Armor"};
    private static final String[] ACCESSORY_NAMES = {"Lucky Charm", "Iron Ring", "Amulet"};

    public static Item randomDrop() {
        Item.Type type = EQUIP_TYPES[RANDOM.nextInt(EQUIP_TYPES.length)];
        Item.Rarity rarity = rollRarity();
        int tier = tierFor(rarity);

        switch (type) {
            case WEAPON:
                return new Item(pick(WEAPON_NAMES) + suffix(rarity), Item.Type.WEAPON, rarity, tier * 4, 0, 0);
            case ARMOR:
                return new Item(pick(ARMOR_NAMES) + suffix(rarity), Item.Type.ARMOR, rarity, 0, tier * 2, tier * 10);
            default:
                return new Item(pick(ACCESSORY_NAMES) + suffix(rarity), Item.Type.ACCESSORY, rarity, tier, tier, tier * 5);
        }
    }

    private static String pick(String[] options) {
        return options[RANDOM.nextInt(options.length)];
    }

    private static int tierFor(Item.Rarity rarity) {
        if (rarity == Item.Rarity.EPIC) return 3;
        if (rarity == Item.Rarity.RARE) return 2;
        return 1;
    }

    private static Item.Rarity rollRarity() {
        float roll = RANDOM.nextFloat();
        if (roll < 0.08f) return Item.Rarity.EPIC;
        if (roll < 0.30f) return Item.Rarity.RARE;
        return Item.Rarity.COMMON;
    }

    private static String suffix(Item.Rarity rarity) {
        if (rarity == Item.Rarity.EPIC) return " (Epic)";
        if (rarity == Item.Rarity.RARE) return " (Rare)";
        return "";
    }
}
