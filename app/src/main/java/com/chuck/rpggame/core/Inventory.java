package com.chuck.rpggame.core;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private final List<Item> items = new ArrayList<>();
    private final int capacity;

    public Inventory(int capacity) {
        this.capacity = capacity;
    }

    public boolean add(Item item) {
        if (items.size() >= capacity) return false;
        items.add(item);
        return true;
    }

    public boolean remove(Item item) {
        return items.remove(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public int getCapacity() {
        return capacity;
    }
}
