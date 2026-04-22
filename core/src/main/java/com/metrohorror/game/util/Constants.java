package com.metrohorror.game.util;

public class Constants {
    private Constants() {}

    public static final float WORLD_WIDTH = 3000f;
    public static final float WORLD_HEIGHT = 1000f;

    public static final float PLAYER_WIDTH = 40f;
    public static final float PLAYER_HEIGHT = 60f;
    public static final int PLAYER_MAX_HEALTH = 100;
    public static final float PLAYER_SPEED = 300f;
    public static final float PLAYER_JUMP_POWER = 560f;
    public static final float GRAVITY = -1200f;

    public static final float ENEMY_WIDTH = 32f;
    public static final float ENEMY_HEIGHT = 52f;
    public static final float ENEMY_SPEED = 120f;
    public static final float ENEMY_AGGRO_RANGE = 340f;
    public static final float ENEMY_ATTACK_RANGE = 58f;
    public static final float ENEMY_ATTACK_COOLDOWN = 1.1f;
    public static final int ENEMY_DAMAGE = 1;

    public static final float LOOT_SIZE = 20f;

    public static final float GROUND_Y = 100f;
    public static final float GROUND_HEIGHT = 50f;

    public static final float CAMERA_LERP = 3.5f;
}
