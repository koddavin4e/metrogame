package com.metrohorror.game.systems;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.metrohorror.game.entities.Enemy;
import com.metrohorror.game.entities.Player;
import com.metrohorror.game.entities.WeaponType;

public class CombatSystem {
    private final Rectangle attackBounds = new Rectangle();
    private final Vector2 handPosition = new Vector2();
    private final Vector2 weaponTipPosition = new Vector2();

    private WeaponType activeWeapon;
    private float attackTimer;
    private boolean damageApplied;

    public boolean startAttack(WeaponType weapon) {
        if (weapon == null || isAttacking()) {
            return false;
        }

        activeWeapon = weapon;
        attackTimer = weapon.getSwingDuration();
        damageApplied = false;
        return true;
    }

    public void update(float delta, Player player, Array<Enemy> enemies, WeaponType equippedWeapon) {
        WeaponType renderWeapon = isAttacking() ? activeWeapon : equippedWeapon;
        updateWeaponPose(player, renderWeapon, getSwingProgress());

        if (!isAttacking()) {
            attackBounds.set(0f, 0f, 0f, 0f);
            return;
        }

        attackTimer = Math.max(0f, attackTimer - delta);
        updateWeaponPose(player, activeWeapon, getSwingProgress());

        if (!damageApplied) {
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && attackBounds.overlaps(enemy.getBounds())) {
                    enemy.takeDamage(activeWeapon.getDamage());
                    damageApplied = true;
                    break;
                }
            }
        }

        if (!isAttacking()) {
            activeWeapon = null;
            attackBounds.set(0f, 0f, 0f, 0f);
            updateWeaponPose(player, equippedWeapon, 0f);
        }
    }

    private void updateWeaponPose(Player player, WeaponType weapon, float progress) {
        handPosition.set(
                player.getX() + player.getBounds().width / 2f,
                player.getY() + player.getBounds().height * 0.58f
        );

        if (weapon == null) {
            weaponTipPosition.set(handPosition);
            attackBounds.set(0f, 0f, 0f, 0f);
            return;
        }

        float facingDirection = player.isFacingRight() ? 1f : -1f;
        float idleAngle = player.isFacingRight() ? 18f : 162f;
        float startAngle = idleAngle - (55f * facingDirection);
        float endAngle = idleAngle + (90f * facingDirection);
        float angle = MathUtils.lerp(startAngle, endAngle, progress);

        float radians = angle * MathUtils.degreesToRadians;
        float tipX = handPosition.x + MathUtils.cos(radians) * weapon.getRange();
        float tipY = handPosition.y + MathUtils.sin(radians) * weapon.getRange();
        weaponTipPosition.set(tipX, tipY);

        if (isAttacking()) {
            float minX = Math.min(handPosition.x, tipX) - weapon.getThickness();
            float minY = Math.min(handPosition.y, tipY) - weapon.getThickness();
            float width = Math.abs(tipX - handPosition.x) + weapon.getThickness() * 2f;
            float height = Math.abs(tipY - handPosition.y) + weapon.getThickness() * 2f;
            attackBounds.set(minX, minY, width, height);
        } else {
            attackBounds.set(0f, 0f, 0f, 0f);
        }
    }

    public boolean isAttacking() {
        return attackTimer > 0f;
    }

    public float getSwingProgress() {
        if (activeWeapon == null || activeWeapon.getSwingDuration() <= 0f) {
            return 0f;
        }
        return MathUtils.clamp(1f - (attackTimer / activeWeapon.getSwingDuration()), 0f, 1f);
    }

    public Rectangle getAttackBounds() {
        return attackBounds;
    }

    public Vector2 getHandPosition() {
        return handPosition;
    }

    public Vector2 getWeaponTipPosition() {
        return weaponTipPosition;
    }

    public WeaponType getVisibleWeapon(WeaponType equippedWeapon) {
        return activeWeapon != null ? activeWeapon : equippedWeapon;
    }
}
