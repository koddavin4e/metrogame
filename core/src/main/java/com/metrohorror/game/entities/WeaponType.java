package com.metrohorror.game.entities;

public enum WeaponType {
    BASIC_KNIFE("\u041d\u043e\u0436", "ui/weapons/basic_knife.png", 1, 46f, 5f, 0.42f, 0.78f, 0.84f, 0.86f),
    RUSTY_SWORD("\u0420\u0436\u0430\u0432\u044b\u0439 \u043c\u0435\u0447", "ui/weapons/rusty_sword.png", 1, 56f, 8f, 0.46f, 0.82f, 0.82f, 0.86f),
    IRON_SWORD("\u0416\u0435\u043b\u0435\u0437\u043d\u044b\u0439 \u043c\u0435\u0447", "ui/weapons/iron_sword.png", 2, 72f, 10f, 0.44f, 0.75f, 0.85f, 1f),
    HEAVY_AXE("\u0422\u044f\u0436\u0435\u043b\u044b\u0439 \u0442\u043e\u043f\u043e\u0440", "ui/weapons/heavy_axe.png", 3, 60f, 14f, 0.56f, 0.82f, 0.45f, 0.3f),
    SPEAR("\u041a\u043e\u043f\u044c\u0435", "ui/weapons/spear.png", 2, 96f, 6f, 0.44f, 0.95f, 0.9f, 0.45f);

    private final String displayName;
    private final String iconAssetPath;
    private final int damage;
    private final float range;
    private final float thickness;
    private final float swingDuration;
    private final float colorR;
    private final float colorG;
    private final float colorB;

    WeaponType(String displayName, String iconAssetPath, int damage, float range, float thickness, float swingDuration,
               float colorR, float colorG, float colorB) {
        this.displayName = displayName;
        this.iconAssetPath = iconAssetPath;
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

    public String getIconAssetPath() {
        return iconAssetPath;
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
