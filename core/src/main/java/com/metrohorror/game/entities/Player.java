package com.metrohorror.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.metrohorror.game.util.Constants;

public final class Player {
    private float x;
    private float y;
    private float velocityX;
    private float velocityY;
    private boolean onGround;
    private boolean facingRight = true;
    private int health = 10;
    private float damageFlashTimer;

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
        damageFlashTimer = Math.max(0f, damageFlashTimer - delta);
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

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
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

    public boolean isAlive() {
        return health > 0;
    }

    public boolean isRecentlyDamaged() {
        return damageFlashTimer > 0f;
    }
}
