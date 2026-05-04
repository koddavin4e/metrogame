package com.metrohorror.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.metrohorror.game.util.Constants;

public class Enemy {
    public enum Type {
        GROUND,
        FLYING_SHOOTER,
        FLYING_SLASHER
    }

    public enum AttackStyle {
        CLAW(46f, 1.28f, 0.20f, 1, 1.9f),
        LUNGE(70f, 1.55f, 0.26f, 2, 2.6f),
        HEAVY(58f, 2.05f, 0.36f, 4, 1.35f);

        private final float range;
        private final float cooldown;
        private final float animationDuration;
        private final int damageBonus;
        private final float lungePower;

        AttackStyle(float range, float cooldown, float animationDuration, int damageBonus, float lungePower) {
            this.range = range;
            this.cooldown = cooldown;
            this.animationDuration = animationDuration;
            this.damageBonus = damageBonus;
            this.lungePower = lungePower;
        }
    }

    private float x;
    private float y;
    private final float homeX;
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
    private final float hoverBaseY;
    private final float hoverAmplitude;
    private final float projectileSpeed;
    private final float projectileRange;
    private final float preferredDistance;
    private final float boundsWidth;
    private final float boundsHeight;
    private final Type type;
    private boolean alive = true;
    private float attackCooldown;
    private float attackAnimationTimer;
    private float alertness;
    private float decisionTimer;
    private float strafeDirection = 1f;
    private float damageFlashTimer;
    private boolean lootDropped;
    private final AttackStyle attackStyle;

    private final Rectangle bounds;

    public Enemy(float x, float y) {
        this(x, y, 3, Constants.ENEMY_SPEED, 7, AttackStyle.CLAW, 0.20f, 0.08f, 0.10f, 0.82f, 0.70f, 0.58f);
    }

    public Enemy(float x, float y, int health, float speed, int damage, float coatR, float coatG, float coatB,
                 float skinR, float skinG, float skinB) {
        this(x, y, health, speed, damage, AttackStyle.CLAW, coatR, coatG, coatB, skinR, skinG, skinB);
    }

    public Enemy(float x, float y, int health, float speed, int damage, AttackStyle attackStyle,
                 float coatR, float coatG, float coatB, float skinR, float skinG, float skinB) {
        this(x, y, health, speed, damage, attackStyle, coatR, coatG, coatB, skinR, skinG, skinB,
                Type.GROUND, Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT, y, 0f, 0f, 0f, 0f);
    }

    private Enemy(float x, float y, int health, float speed, int damage, AttackStyle attackStyle,
                  float coatR, float coatG, float coatB, float skinR, float skinG, float skinB,
                  Type type, float boundsWidth, float boundsHeight, float hoverBaseY, float hoverAmplitude,
                  float projectileSpeed, float projectileRange, float preferredDistance) {
        this.x = x;
        this.y = y;
        this.homeX = x;
        this.health = health;
        this.speed = speed;
        this.damage = damage;
        this.coatR = coatR;
        this.coatG = coatG;
        this.coatB = coatB;
        this.skinR = skinR;
        this.skinG = skinG;
        this.skinB = skinB;
        this.type = type;
        this.boundsWidth = boundsWidth;
        this.boundsHeight = boundsHeight;
        this.hoverBaseY = hoverBaseY;
        this.hoverAmplitude = hoverAmplitude;
        this.projectileSpeed = projectileSpeed;
        this.projectileRange = projectileRange;
        this.preferredDistance = preferredDistance;
        this.attackStyle = attackStyle;
        this.bounds = new Rectangle(x, y, boundsWidth, boundsHeight);
    }

    public static Enemy createFlyingShooter(float x, float y) {
        return new Enemy(
                x, y,
                9,
                118f,
                8,
                AttackStyle.CLAW,
                0.30f, 0.08f, 0.10f,
                0.82f, 0.70f, 0.58f,
                Type.FLYING_SHOOTER,
                78f, 42f,
                y,
                16f,
                305f,
                540f,
                200f
        );
    }

    public static Enemy createFlyingSlasher(float x, float y) {
        return new Enemy(
                x, y,
                12,
                164f,
                10,
                AttackStyle.LUNGE,
                0.24f, 0.05f, 0.08f,
                0.76f, 0.63f, 0.56f,
                Type.FLYING_SLASHER,
                82f, 54f,
                y,
                24f,
                0f,
                0f,
                0f
        );
    }

    public void update(float delta) {
        attackCooldown = Math.max(0f, attackCooldown - delta);
        attackAnimationTimer = Math.max(0f, attackAnimationTimer - delta);
        damageFlashTimer = Math.max(0f, damageFlashTimer - delta);
        decisionTimer = Math.max(0f, decisionTimer - delta);
        bounds.setPosition(x, y);
    }

    public void takeDamage(int damage) {
        if (!alive) return;

        health -= damage;
        damageFlashTimer = 0.22f;
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

    public void moveBy(float amount) {
        if (!alive) {
            return;
        }
        x += amount;
        if (amount != 0f) {
            facingRight = amount > 0f;
        }
        bounds.setPosition(x, y);
    }

    public void face(float targetX) {
        if (targetX != x) {
            facingRight = targetX > x;
        }
    }

    public void moveTowardY(float targetY, float speed, float delta) {
        if (!alive) {
            return;
        }

        float direction = Math.signum(targetY - y);
        y += direction * speed * delta;
        bounds.setPosition(x, y);
    }

    public void clampX(float minX, float maxX) {
        x = Math.max(minX, Math.min(maxX, x));
        bounds.setPosition(x, y);
    }

    public void clampY(float minY, float maxY) {
        y = Math.max(minY, Math.min(maxY, y));
        bounds.setPosition(x, y);
    }

    public void noticePlayer(float delta) {
        alertness = Math.min(1f, alertness + delta * 2.8f);
    }

    public void calmDown(float delta) {
        alertness = Math.max(0f, alertness - delta * 0.65f);
    }

    public boolean shouldPickNewDecision() {
        return decisionTimer <= 0f;
    }

    public void pickDecision(float timer, float direction) {
        decisionTimer = timer;
        strafeDirection = direction == 0f ? 1f : Math.signum(direction);
    }

    public boolean canAttack() {
        return alive && attackCooldown <= 0f;
    }

    public void triggerAttack() {
        attackCooldown = attackStyle.cooldown;
        attackAnimationTimer = attackStyle.animationDuration;
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

    public boolean isFlyingShooter() {
        return type == Type.FLYING_SHOOTER;
    }

    public boolean isFlyingSlasher() {
        return type == Type.FLYING_SLASHER;
    }

    public float getX() {
        return x;
    }

    public float getHomeX() {
        return homeX;
    }

    public float getY() {
        return y;
    }

    public float getHoverBaseY() {
        return hoverBaseY;
    }

    public float getHoverAmplitude() {
        return hoverAmplitude;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public boolean isAttacking() {
        return attackAnimationTimer > 0f;
    }

    public boolean isRecentlyDamaged() {
        return damageFlashTimer > 0f;
    }

    public float getDamageFlashTimer() {
        return damageFlashTimer;
    }

    public float getAlertness() {
        return alertness;
    }

    public float getStrafeDirection() {
        return strafeDirection;
    }

    public float getAttackProgress() {
        return 1f - (attackAnimationTimer / attackStyle.animationDuration);
    }

    public int getHealth() {
        return health;
    }

    public float getSpeed() {
        return speed;
    }

    public int getDamage() {
        return damage + attackStyle.damageBonus;
    }

    public AttackStyle getAttackStyle() {
        return attackStyle;
    }

    public float getAttackRange() {
        return attackStyle.range;
    }

    public float getLungePower() {
        return attackStyle.lungePower;
    }

    public float getProjectileSpeed() {
        return projectileSpeed;
    }

    public float getProjectileRange() {
        return projectileRange;
    }

    public float getPreferredDistance() {
        return preferredDistance;
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
