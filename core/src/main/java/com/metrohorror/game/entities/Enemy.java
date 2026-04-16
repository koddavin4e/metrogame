package com.metrohorror.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.metrohorror.game.util.Constants;

public class Enemy {
    private float x;
    private float y;
    private boolean facingRight = false;
    private int health = 3;
    private boolean alive = true;
    private float attackCooldown;
    private float attackAnimationTimer;
    private boolean lootDropped;

    private final Rectangle bounds;

    public Enemy(float x, float y) {
        this.x = x;
        this.y = y;
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

    public boolean isLootDropped() {
        return lootDropped;
    }

    public void markLootDropped() {
        lootDropped = true;
    }
}
