package com.metrohorror.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.metrohorror.game.util.Constants;

public class Loot {
    private final String name;
    private final int amount;
    private final Rectangle bounds;
    private boolean collected = false;

    public Loot(String name, int amount, float x, float y) {
        this.name = name;
        this.amount = amount;
        this.bounds = new Rectangle(x, y, Constants.LOOT_SIZE, Constants.LOOT_SIZE);
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
    }
}