package com.metrohorror.game.entities;

public enum WeaponType {
    BASIC_KNIFE("Базовый нож", 1, 46f, 5f, 0.16f, 0.78f, 0.84f, 0.86f),
    RUSTY_SWORD("Rusty Sword", 1, 56f, 8f, 0.22f, 0.82f, 0.82f, 0.86f),
    IRON_SWORD("Iron Sword", 2, 72f, 10f, 0.2f, 0.75f, 0.85f, 1f),
    HEAVY_AXE("Heavy Axe", 3, 60f, 14f, 0.34f, 0.82f, 0.45f, 0.3f),
    SPEAR("Spear", 2, 96f, 6f, 0.18f, 0.95f, 0.9f, 0.45f);

    private final String displayName;
    private final int damage;
    private final float range;
    private final float thickness;
    private final float swingDuration;
    private final float colorR;
    private final float colorG;
    private final float colorB;

    WeaponType(String displayName, int damage, float range, float thickness, float swingDuration,
               float colorR, float colorG, float colorB) {
        this.displayName = displayName;
        this.damage = damage;
        this.range = range;
        this.thickness = thickness;
        this.swingDuration = swingDuration;
        this.colorR = colorR;
        this.colorG = colorG;
        this.colorB = colorB;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDamage() {
        return damage;
    }

    public float getRange() {
        return range;
    }

    public float getThickness() {
        return thickness;
    }

    public float getSwingDuration() {
        return swingDuration;
    }

    public float getColorR() {
        return colorR;
    }

    public float getColorG() {
        return colorG;
    }

    public float getColorB() {
        return colorB;
    }
}
