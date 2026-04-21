package com.metrohorror.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.metrohorror.game.util.Constants;

public class Enemy {
    private float x;
    private float y;
    private boolean facingRight = false;
    private int health;
    private final float speed;
    private final int damage;
    private final float coatR;
    private final float coatG;
    private final float coatB;
    private final float skinR;
    private final float skinG;
    private final float skinB;
    private boolean alive = true;
    private float attackCooldown;
    private float attackAnimationTimer;
    private boolean lootDropped;

    private final Rectangle bounds;

    public Enemy(float x, float y) {
        this(x, y, 3, Constants.ENEMY_SPEED, 7, 0.20f, 0.08f, 0.10f, 0.82f, 0.70f, 0.58f);
    }

    public Enemy(float x, float y, int health, float speed, int damage, float coatR, float coatG, float coatB,
                 float skinR, float skinG, float skinB) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.speed = speed;
        this.damage = damage;
        this.coatR = coatR;
        this.coatG = coatG;
        this.coatB = coatB;
        this.skinR = skinR;
        this.skinG = skinG;
        this.skinB = skinB;
        this.bounds = new Rectangle(x, y, Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT);
    }

    public void update(float delta) {
        attackCooldown = Math.max(0f, attackCooldown - delta);
        attackAnimationTimer = Math.max(0f, attackAnimationTimer - delta);
        bounds.setPosition(x, y);
    }

    public void takeDamage(int damage) {
        if (!alive) return;

        health -= damage;
        if (health <= 0) {
            alive = false;
        }
    }

    public void moveToward(float targetX, float speed, float delta) {
        if (!alive) {
            return;
        }

        float direction = Math.signum(targetX - x);
        x += direction * speed * delta;
        if (direction != 0f) {
            facingRight = direction > 0f;
        }
        bounds.setPosition(x, y);
    }

    public boolean canAttack() {
        return alive && attackCooldown <= 0f;
    }

    public void triggerAttack() {
        attackCooldown = Constants.ENEMY_ATTACK_COOLDOWN;
        attackAnimationTimer = 0.22f;
    }

    public void setY(float y) {
        this.y = y;
        bounds.setPosition(x, y);
    }

    public Rectangle getBounds() {
        return bounds;
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

    public boolean isFacingRight() {
        return facingRight;
    }

    public boolean isAttacking() {
        return attackAnimationTimer > 0f;
    }

    public float getAttackProgress() {
        return 1f - (attackAnimationTimer / 0.22f);
    }

    public int getHealth() {
        return health;
    }

    public float getSpeed() {
        return speed;
    }

    public int getDamage() {
        return damage;
    }

    public float getCoatR() {
        return coatR;
    }

    public float getCoatG() {
        return coatG;
    }

    public float getCoatB() {
        return coatB;
    }

    public float getSkinR() {
        return skinR;
    }

    public float getSkinG() {
        return skinG;
    }

    public float getSkinB() {
        return skinB;
    }

    public boolean isLootDropped() {
        return lootDropped;
    }

    public void markLootDropped() {
        lootDropped = true;
    }
}
