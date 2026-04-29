package com.metrohorror.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.metrohorror.game.util.Constants;

public final class Player {
    private static final float PARRY_ACTIVE_DURATION = 0.17f;
    private static final float PARRY_COOLDOWN_DURATION = 1.35f;
    private static final float PARRY_SUCCESS_DURATION = 0.24f;

    private float x;
    private float y;
    private float velocityX;
    private float velocityY;
    private boolean onGround;
    private boolean facingRight = true;
    private int health = Constants.PLAYER_MAX_HEALTH;
    private float damageFlashTimer;
    private float animationTime;
    private float parryTimer;
    private float parryCooldownTimer;
    private float parrySuccessTimer;

    private final Rectangle bounds;

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
        this.bounds = new Rectangle(x, y, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
        updateBounds();
    }

    public void update(float delta) {
        x += velocityX * delta;
        y += velocityY * delta;
        float speedRatio = Math.min(1f, Math.abs(velocityX) / Constants.PLAYER_SPEED);
        animationTime += delta * (onGround ? 2.4f + speedRatio * 5.2f : 2f);
        damageFlashTimer = Math.max(0f, damageFlashTimer - delta);
        parryTimer = Math.max(0f, parryTimer - delta);
        parryCooldownTimer = Math.max(0f, parryCooldownTimer - delta);
        parrySuccessTimer = Math.max(0f, parrySuccessTimer - delta);
        updateBounds();
    }

    public void updateBounds() {
        bounds.setPosition(x, y);
    }

    public void applyGravity(float delta) {
        velocityY += Constants.GRAVITY * delta;
    }

    public void moveLeft() {
        velocityX = -Constants.PLAYER_SPEED;
        facingRight = false;
    }

    public void moveRight() {
        velocityX = Constants.PLAYER_SPEED;
        facingRight = true;
    }

    public void stopX() {
        velocityX = 0;
    }

    public void jump() {
        if (onGround) {
            velocityY = Constants.PLAYER_JUMP_POWER;
            onGround = false;
        }
    }

    public boolean startParry() {
        if (parryTimer > 0f || parryCooldownTimer > 0f) {
            return false;
        }

        parryTimer = PARRY_ACTIVE_DURATION;
        parryCooldownTimer = PARRY_COOLDOWN_DURATION;
        return true;
    }

    public boolean isParrying() {
        return parryTimer > 0f;
    }

    public boolean registerParrySuccess() {
        if (!isParrying()) {
            return false;
        }

        parryTimer = Math.max(parryTimer, 0.08f);
        parrySuccessTimer = PARRY_SUCCESS_DURATION;
        return true;
    }

    public boolean isParrySuccessVisible() {
        return parrySuccessTimer > 0f;
    }

    public float getParrySuccessTimer() {
        return parrySuccessTimer;
    }

    public float getParryCooldownProgress() {
        if (parryCooldownTimer <= 0f) {
            return 0f;
        }
        return parryCooldownTimer / PARRY_COOLDOWN_DURATION;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
        updateBounds();
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
        updateBounds();
    }

    public float getVelocityY() {
        return velocityY;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public void bounceFromEnemy() {
        velocityY = Constants.PLAYER_JUMP_POWER * 0.72f;
        onGround = false;
    }

    public void bounceFromDownAttack(float power) {
        velocityY = power;
        onGround = false;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
        damageFlashTimer = 0.18f;
    }

    public void heal(int amount) {
        health = Math.min(Constants.PLAYER_MAX_HEALTH, health + amount);
    }

    public void healToFull() {
        health = Constants.PLAYER_MAX_HEALTH;
        damageFlashTimer = 0f;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public boolean isRecentlyDamaged() {
        return damageFlashTimer > 0f;
    }

    public float getAnimationTime() {
        return animationTime;
    }
}
