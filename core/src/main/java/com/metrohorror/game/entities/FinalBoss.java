package com.metrohorror.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.metrohorror.game.util.Constants;

public class FinalBoss {
    private static final class FrameSlice {
        private final int x;
        private final int y;
        private final int width;
        private final int height;

        private FrameSlice(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    private static final float WIDTH = 124f;
    private static final float HEIGHT = 142f;
    private static final float MOVE_SPEED = 138f;
    private static final float CHARGE_SPEED = 860f;
    private static final float CHARGE_WINDUP = 0.55f;
    private static final float STUN_DURATION = 2.2f;
    private static final float PULL_WINDUP_DURATION = 0.28f;
    private static final float PULL_RECOVERY_DURATION = 0.22f;
    private static final float PULL_HAND_SPEED = 760f;
    private static final float PULL_HAND_RETURN_SPEED = 680f;
    private static final float PULL_PLAYER_DRAG_SPEED = 520f;
    private static final float PULL_MAX_RANGE = 560f;
    private static final float PULL_HAND_START_DISTANCE = 92f;
    private static final float THROW_RECOVERY = 0.55f;
    private static final float HEAD_SPEED = 560f;
    private static final float HEAD_RETURN_SPEED = 700f;
    private static final int MAX_HEALTH = 180;
    private static final float FACE_PLAYER_DEADZONE = 18f;
    // The leg strokes extend below the logical bounds, so the render needs a small lift.
    private static final float VISUAL_GROUND_OFFSET = 20f;
    private static final float SLASH_DURATION = 0.90f;
    private static final float WALK_FRAME_DURATION = 0.18f;
    private static final float RUN_FRAME_DURATION = 0.11f;
    private static final float SPRITE_DRAW_WIDTH = 178f;
    private static final float SPRITE_DRAW_HEIGHT = 168f;
    private static final float SPRITE_DRAW_OFFSET_Y = 10f;
    private static final float PULL_DRAW_SCALE = 0.84f;
    private static final float PULL_DRAW_OFFSET_Y = 2f;
    private static final float SLASH_DRAW_SCALE = 0.84f;
    private static final float SLASH_DRAW_OFFSET_Y = 2f;
    private static final boolean SLASH_SOURCE_FACES_RIGHT = true;
    private static final boolean PULL_SOURCE_FACES_RIGHT = true;
    private static final FrameSlice[] IDLE_FRAME_SLICES = new FrameSlice[] {
            new FrameSlice(0, 0, 213, 379)
    };
    private static final FrameSlice[] RUN_FRAME_SLICES = new FrameSlice[] {
            new FrameSlice(0, 0, 189, 236),
            new FrameSlice(189, 0, 207, 236),
            new FrameSlice(396, 0, 209, 236),
            new FrameSlice(605, 0, 216, 236),
            new FrameSlice(821, 0, 239, 236),
            new FrameSlice(1060, 0, 220, 236)
    };
    private static final FrameSlice[] WALK_FRAME_SLICES = new FrameSlice[] {
            new FrameSlice(47, 283, 177, 297),
            new FrameSlice(272, 283, 144, 297),
            new FrameSlice(480, 283, 168, 297),
            new FrameSlice(694, 283, 167, 297),
            new FrameSlice(905, 283, 178, 297),
            new FrameSlice(1132, 283, 146, 297),
            new FrameSlice(1349, 283, 166, 297),
            new FrameSlice(1564, 283, 163, 297)
    };
    private static final FrameSlice[] SLASH_FRAME_SLICES = new FrameSlice[] {
            new FrameSlice(18, 0, 160, 207),
            new FrameSlice(208, 0, 182, 207),
            new FrameSlice(400, 0, 300, 207),
            new FrameSlice(906, 0, 336, 207)
    };
    private static final float[] SLASH_FRAME_PIVOT_X = new float[] { 78f, 88f, 104f, 118f };
    private static final FrameSlice[] PULL_BOSS_FRAME_SLICES = new FrameSlice[] {
            new FrameSlice(200, 0, 250, 185),
            new FrameSlice(450, 0, 320, 185)
    };
    private static final float[] PULL_BOSS_PIVOT_X = new float[] { 92f, 108f };
    private static final FrameSlice PULL_HAND_SLICE = new FrameSlice(1294, 0, 90, 185);
    private static Texture[] idleFrameTextures;
    private static Texture[] walkFrameTextures;
    private static Texture[] runFrameTextures;
    private static Texture slashSheetTexture;
    private static Texture pullSheetTexture;
    private static TextureRegion[] runFrames;
    private static TextureRegion[] walkFrames;
    private static TextureRegion[] idleFrames;
    private static TextureRegion[] slashFrames;
    private static TextureRegion[] pullBossFrames;
    private static TextureRegion pullHandFrame;

    private enum State {
        IDLE,
        SLASH,
        CHARGE_WINDUP,
        CHARGING,
        STUNNED,
        PULL,
        THROW_HEAD
    }

    private enum PullPhase {
        WINDUP,
        OUTBOUND,
        RETURNING,
        RECOVERY
    }

    private final Rectangle bounds = new Rectangle();
    private final Rectangle slashBounds = new Rectangle();
    private final Rectangle pullBounds = new Rectangle();
    private final Rectangle headBounds = new Rectangle();

    private float x;
    private float y;
    private boolean facingRight;
    private boolean attackFacingRight;
    private boolean pullFacingRight;
    private int health = MAX_HEALTH;
    private boolean alive = true;
    private State state = State.IDLE;
    private float stateTimer;
    private float slashCooldown;
    private float chargeCooldown;
    private float pullCooldown;
    private float throwCooldown;
    private float playerHitCooldown;
    private float damageFlashTimer;
    private boolean attackHitApplied;
    private float chargeDirection;

    private boolean headActive;
    private boolean headReturning;
    private float headX;
    private float headY;
    private float headVelocityX;
    private float headDamageCooldown;
    private float headPressureTimer;
    private float headRepelCooldown;
    private float stompRepelCooldown;
    private PullPhase pullPhase = PullPhase.WINDUP;
    private boolean pullHandActive;
    private boolean pullHandGrabbed;
    private float pullHandX;
    private float pullHandY;
    private float pullHandDistance;
    private float idleAnimationTime;
    private float moveAnimationTime;
    private float currentMoveSpeed;

    public FinalBoss(float x, float y) {
        reset(x, y);
    }

    public void reset(float x, float y) {
        this.x = x;
        this.y = y;
        health = MAX_HEALTH;
        alive = true;
        facingRight = false;
        attackFacingRight = false;
        pullFacingRight = false;
        state = State.IDLE;
        stateTimer = 0f;
        slashCooldown = 0f;
        chargeCooldown = 1.4f;
        pullCooldown = 2.8f;
        throwCooldown = 2.2f;
        playerHitCooldown = 0f;
        damageFlashTimer = 0f;
        attackHitApplied = false;
        chargeDirection = -1f;
        headActive = false;
        headReturning = false;
        headX = x;
        headY = y + 88f;
        headVelocityX = 0f;
        headDamageCooldown = 0f;
        headPressureTimer = 0f;
        headRepelCooldown = 0f;
        stompRepelCooldown = 0f;
        pullPhase = PullPhase.WINDUP;
        pullHandActive = false;
        pullHandGrabbed = false;
        pullHandX = x;
        pullHandY = y + 56f;
        pullHandDistance = 0f;
        idleAnimationTime = 0f;
        moveAnimationTime = 0f;
        currentMoveSpeed = 0f;
        slashBounds.set(0f, 0f, 0f, 0f);
        pullBounds.set(0f, 0f, 0f, 0f);
        updateBounds();
    }

    public void update(float delta, Player player, float arenaMinX, float arenaMaxX, float groundY) {
        float previousX = x;
        slashCooldown = Math.max(0f, slashCooldown - delta);
        chargeCooldown = Math.max(0f, chargeCooldown - delta);
        pullCooldown = Math.max(0f, pullCooldown - delta);
        throwCooldown = Math.max(0f, throwCooldown - delta);
        playerHitCooldown = Math.max(0f, playerHitCooldown - delta);
        damageFlashTimer = Math.max(0f, damageFlashTimer - delta);
        headDamageCooldown = Math.max(0f, headDamageCooldown - delta);
        headRepelCooldown = Math.max(0f, headRepelCooldown - delta);
        headPressureTimer = Math.max(0f, headPressureTimer - delta * 0.75f);
        stompRepelCooldown = Math.max(0f, stompRepelCooldown - delta);

        if (!alive) {
            slashBounds.set(0f, 0f, 0f, 0f);
            pullBounds.set(0f, 0f, 0f, 0f);
            headActive = false;
            y = groundY;
            updateBounds();
            return;
        }

        // The boss is grounded in this arena, so its hitbox bottom stays on the floor plane.
        y = groundY;

        float playerCenterX = player.getBounds().x + player.getBounds().width * 0.5f;
        float playerCenterY = player.getBounds().y + player.getBounds().height * 0.5f;
        float bossCenterX = x + WIDTH * 0.5f;
        float bossCenterY = y + HEIGHT * 0.52f;
        float dx = playerCenterX - bossCenterX;
        float absDx = Math.abs(dx);
        float absDy = Math.abs(playerCenterY - bossCenterY);
        updateHeadPressureFromContact(player, delta, arenaMinX, arenaMaxX);

        updateFacingTowardsPlayer(dx);

        stateTimer = Math.max(0f, stateTimer - delta);
        updateHead(delta, player, arenaMinX, arenaMaxX);

        if (state == State.IDLE) {
            updateIdle(delta, absDx, absDy, dx);
            pickAttack(dx, absDx, absDy);
        } else if (state == State.SLASH) {
            updateSlash(delta, player);
            if (stateTimer <= 0f) {
                state = State.IDLE;
                slashBounds.set(0f, 0f, 0f, 0f);
            }
        } else if (state == State.CHARGE_WINDUP) {
            x -= chargeDirection * 110f * delta;
            if (stateTimer <= 0f) {
                state = State.CHARGING;
                stateTimer = 1.1f;
                moveAnimationTime = 0f;
                attackHitApplied = false;
            }
        } else if (state == State.CHARGING) {
            updateCharge(delta, player, arenaMinX, arenaMaxX);
        } else if (state == State.STUNNED) {
            slashBounds.set(0f, 0f, 0f, 0f);
            pullBounds.set(0f, 0f, 0f, 0f);
            if (stateTimer <= 0f) {
                state = State.IDLE;
            }
        } else if (state == State.PULL) {
            updatePull(delta, player, arenaMinX, arenaMaxX);
            if (pullPhase == PullPhase.RECOVERY && stateTimer <= 0f) {
                state = State.IDLE;
                pullBounds.set(0f, 0f, 0f, 0f);
            }
        } else if (state == State.THROW_HEAD) {
            if (!headActive && stateTimer <= 0f) {
                state = State.IDLE;
            }
        }

        x = MathUtils.clamp(x, arenaMinX, arenaMaxX - WIDTH);
        currentMoveSpeed = Math.abs(x - previousX) / Math.max(delta, 0.0001f);
        if ((state == State.IDLE || state == State.CHARGING) && currentMoveSpeed > 8f) {
            moveAnimationTime += delta * MathUtils.clamp(currentMoveSpeed / MOVE_SPEED, 0.85f, 1.9f);
        } else {
            idleAnimationTime += delta;
        }
        updateBounds();
    }

    private void updateIdle(float delta, float absDx, float absDy, float dx) {
        slashBounds.set(0f, 0f, 0f, 0f);
        pullBounds.set(0f, 0f, 0f, 0f);

        if (absDy > 170f) {
            return;
        }

        float preferredDistance = 162f;
        if (absDx > preferredDistance + 30f) {
            x += Math.signum(dx) * MOVE_SPEED * delta;
        } else if (absDx < preferredDistance - 34f) {
            x -= Math.signum(dx) * MOVE_SPEED * 0.68f * delta;
        }
    }

    private void updateFacingTowardsPlayer(float dx) {
        if (Math.abs(dx) <= FACE_PLAYER_DEADZONE) {
            return;
        }
        facingRight = dx > 0f;
    }

    private void pickAttack(float dx, float absDx, float absDy) {
        if (absDy > 190f) {
            return;
        }

        if (chargeCooldown <= 0f && absDx > 260f) {
            state = State.CHARGE_WINDUP;
            stateTimer = CHARGE_WINDUP;
            chargeCooldown = 12f;
            moveAnimationTime = 0f;
            attackHitApplied = false;
            chargeDirection = facingRight ? 1f : -1f;
            return;
        }

        if (throwCooldown <= 0f && absDx > 210f) {
            state = State.THROW_HEAD;
            stateTimer = THROW_RECOVERY;
            throwCooldown = 8f;
            spawnHead();
            return;
        }

        if (pullCooldown <= 0f && absDx > 115f && absDx < 390f) {
            facingRight = dx >= 0f;
            state = State.PULL;
            stateTimer = PULL_WINDUP_DURATION;
            pullCooldown = 8f;
            attackHitApplied = false;
            pullFacingRight = facingRight;
            pullPhase = PullPhase.WINDUP;
            pullHandActive = false;
            pullHandGrabbed = false;
            pullHandDistance = 0f;
            return;
        }

        if (slashCooldown <= 0f && absDx < 170f) {
            state = State.SLASH;
            stateTimer = SLASH_DURATION;
            slashCooldown = 8f;
            attackHitApplied = false;
            attackFacingRight = facingRight;
        }
    }

    private void updateSlash(float delta, Player player) {
        int frameIndex = getSlashFrameIndex();
        setSlashBoundsForFrame(frameIndex);

        if (!attackHitApplied && frameIndex >= 2 && slashBounds.overlaps(player.getBounds())) {
            hitPlayer(player, frameIndex >= 4 ? 16 : 14);
            attackHitApplied = true;
        }
    }

    private int getSlashFrameIndex() {
        float progress = MathUtils.clamp(1f - stateTimer / SLASH_DURATION, 0f, 0.999f);
        return Math.min(SLASH_FRAME_SLICES.length - 1, (int)(progress * SLASH_FRAME_SLICES.length));
    }

    private void setSlashBoundsForFrame(int frameIndex) {
        switch (frameIndex) {
            case 0:
                setMirroredSlashBounds(18f, 34f, 44f, 68f);
                return;
            case 1:
                setMirroredSlashBounds(26f, 40f, 70f, 74f);
                return;
            case 2:
                setMirroredSlashBounds(48f, 32f, 128f, 96f);
                return;
            case 3:
                setMirroredSlashBounds(28f, 18f, 186f, 98f);
                return;
            default:
                setMirroredSlashBounds(18f, -2f, 214f, 74f);
        }
    }

    private void setMirroredSlashBounds(float offsetX, float offsetY, float width, float height) {
        float slashX = attackFacingRight ? x + offsetX : x + WIDTH - offsetX - width;
        slashBounds.set(slashX, y + offsetY, width, height);
    }

    private void drawAnchoredFrame(SpriteBatch batch, TextureRegion frame, float anchorX, float drawY, float scale,
                                   float pivotX, boolean facingRight, boolean sourceFacesRight) {
        float drawWidth = frame.getRegionWidth() * scale;
        float drawHeight = frame.getRegionHeight() * scale;
        float pivotScaled = pivotX * scale;
        boolean flip = facingRight != sourceFacesRight;
        float drawX = flip ? anchorX + pivotScaled : anchorX - pivotScaled;
        batch.draw(frame,
                flip ? drawX + drawWidth : drawX,
                drawY,
                flip ? -drawWidth : drawWidth,
                drawHeight);
    }

    private float getPullCapturedPlayerTargetX() {
        if (pullFacingRight) {
            return x + WIDTH - Constants.PLAYER_WIDTH * 0.72f;
        }
        return x - Constants.PLAYER_WIDTH * 0.28f;
    }

    private void updateCharge(float delta, Player player, float arenaMinX, float arenaMaxX) {
        pullBounds.set(0f, 0f, 0f, 0f);
        slashBounds.set(0f, 0f, 0f, 0f);
        x += chargeDirection * CHARGE_SPEED * delta;
        Rectangle chargeBounds = new Rectangle(x + (chargeDirection > 0f ? 44f : 8f), y + 26f, 72f, 74f);

        if (!attackHitApplied && chargeBounds.overlaps(player.getBounds())) {
            hitPlayer(player, 20);
            float shove = chargeDirection * 88f;
            player.setX(MathUtils.clamp(player.getX() + shove, arenaMinX, arenaMaxX - Constants.PLAYER_WIDTH));
            attackHitApplied = true;
        }

        boolean hitWall = x <= arenaMinX || x + WIDTH >= arenaMaxX;
        if (hitWall) {
            x = MathUtils.clamp(x, arenaMinX, arenaMaxX - WIDTH);
            state = State.STUNNED;
            stateTimer = STUN_DURATION;
        } else if (stateTimer <= 0f) {
            // Leaving charge without a wall impact should not stun the boss.
            state = State.IDLE;
        }
    }

    private void updatePull(float delta, Player player, float arenaMinX, float arenaMaxX) {
        if (pullPhase == PullPhase.WINDUP) {
            pullFacingRight = facingRight;
            pullBounds.set(0f, 0f, 0f, 0f);
            if (stateTimer <= 0f) {
                startPullHandFlight();
            }
            return;
        }

        if (pullPhase == PullPhase.RECOVERY) {
            pullBounds.set(0f, 0f, 0f, 0f);
            return;
        }

        float direction = pullFacingRight ? 1f : -1f;
        float anchorX = getPullHandAnchorX();
        float anchorY = getPullHandAnchorY();

        if (pullPhase == PullPhase.OUTBOUND) {
            pullHandDistance = Math.min(PULL_MAX_RANGE, pullHandDistance + PULL_HAND_SPEED * delta);
            pullHandX = anchorX + direction * pullHandDistance;
            pullHandY = anchorY;
            pullBounds.set(pullHandX - 28f, pullHandY - 22f, 56f, 44f);

            boolean canGrabPlayer = Math.abs(player.getBounds().y - y) < 170f;
            if (canGrabPlayer && pullBounds.overlaps(player.getBounds())) {
                pullHandGrabbed = true;
                attackHitApplied = true;
                pullPhase = PullPhase.RETURNING;
                stateTimer = 1f;
            } else if (pullHandDistance >= PULL_MAX_RANGE) {
                pullPhase = PullPhase.RETURNING;
                stateTimer = 1f;
            }
            return;
        }

        float dxToAnchor = anchorX - pullHandX;
        float returnStep = Math.min(Math.abs(dxToAnchor), PULL_HAND_RETURN_SPEED * delta);
        pullHandX += Math.signum(dxToAnchor) * returnStep;
        pullHandY = anchorY;
        pullBounds.set(pullHandX - 28f, pullHandY - 22f, 56f, 44f);

        if (pullHandGrabbed) {
            float playerTargetX = getPullCapturedPlayerTargetX();
            float clampedTargetX = MathUtils.clamp(playerTargetX, arenaMinX, arenaMaxX - Constants.PLAYER_WIDTH);
            float dragDistance = clampedTargetX - player.getX();
            float dragStep = Math.min(Math.abs(dragDistance), PULL_PLAYER_DRAG_SPEED * delta);
            player.setX(player.getX() + Math.signum(dragDistance) * dragStep);
            player.setY(y);
            player.setVelocityY(0f);
            player.setOnGround(false);
        }

        if (Math.abs(anchorX - pullHandX) <= 16f) {
            pullHandActive = false;
            pullHandGrabbed = false;
            pullBounds.set(0f, 0f, 0f, 0f);
            pullPhase = PullPhase.RECOVERY;
            stateTimer = PULL_RECOVERY_DURATION;
        }
    }

    private void startPullHandFlight() {
        pullPhase = PullPhase.OUTBOUND;
        pullHandActive = true;
        pullHandGrabbed = false;
        pullHandDistance = PULL_HAND_START_DISTANCE;
        pullHandX = getPullHandAnchorX() + (pullFacingRight ? 1f : -1f) * pullHandDistance;
        pullHandY = getPullHandAnchorY();
        stateTimer = (PULL_MAX_RANGE - PULL_HAND_START_DISTANCE) / PULL_HAND_SPEED;
    }

    private float getPullHandAnchorX() {
        return x + (pullFacingRight ? WIDTH + 6f : WIDTH - 4f);
    }

    private float getPullHandAnchorY() {
        return y + 76f;
    }

    private void spawnHead() {
        headActive = true;
        headReturning = false;
        headX = x + (facingRight ? WIDTH + 10f : -34f);
        headY = y + 76f;
        headVelocityX = (facingRight ? 1f : -1f) * HEAD_SPEED;
        headDamageCooldown = 0f;
    }

    private void updateHead(float delta, Player player, float arenaMinX, float arenaMaxX) {
        if (!headActive) {
            headBounds.set(0f, 0f, 0f, 0f);
            return;
        }

        headX += headVelocityX * delta;
        headBounds.set(headX, headY, 34f, 34f);

        if (!headReturning) {
            boolean reachedWall = headX <= arenaMinX || headX + headBounds.width >= arenaMaxX;
            boolean reachedRange = Math.abs(headX - x) > 360f;
            if (reachedWall || reachedRange) {
                headReturning = true;
                headVelocityX = (x + WIDTH * 0.5f > headX ? 1f : -1f) * HEAD_RETURN_SPEED;
            }
        } else {
            headVelocityX = (x + WIDTH * 0.5f > headX ? 1f : -1f) * HEAD_RETURN_SPEED;
            if (Math.abs((x + WIDTH * 0.5f) - (headX + 17f)) < 26f) {
                headActive = false;
                headBounds.set(0f, 0f, 0f, 0f);
                return;
            }
        }

        if (headDamageCooldown <= 0f && headBounds.overlaps(player.getBounds())) {
            hitPlayer(player, 11);
            headDamageCooldown = 0.45f;
        }
    }

    private void hitPlayer(Player player, int damage) {
        if (playerHitCooldown > 0f || !player.isAlive()) {
            return;
        }
        player.takeDamage(damage);
        playerHitCooldown = 0.48f;
    }

    public void takeDamage(int damage) {
        if (!alive) {
            return;
        }
        health = Math.max(0, health - damage);
        damageFlashTimer = 0.2f;
        if (health == 0) {
            alive = false;
            state = State.STUNNED;
            stateTimer = 0f;
            headActive = false;
        }
    }

    public Rectangle getHeadBounds() {
        return new Rectangle(x + 34f, y + 92f, 54f, 34f);
    }

    public boolean absorbHeadPressure(Player player, float pressureAmount, float arenaMinX, float arenaMaxX) {
        headPressureTimer = Math.min(1.4f, headPressureTimer + pressureAmount);
        if (headRepelCooldown > 0f || headPressureTimer < 0.9f) {
            return false;
        }

        float bossCenterX = x + WIDTH * 0.5f;
        float playerCenterX = player.getBounds().x + player.getBounds().width * 0.5f;
        float pushDirection = playerCenterX >= bossCenterX ? 1f : -1f;
        float targetX = player.getX() + pushDirection * 96f;
        player.setX(MathUtils.clamp(targetX, arenaMinX, arenaMaxX - Constants.PLAYER_WIDTH));
        player.setVelocityY(Constants.PLAYER_JUMP_POWER * 0.38f);
        player.setOnGround(false);
        headPressureTimer = 0f;
        headRepelCooldown = 0.8f;
        return true;
    }

    private static void ensureAnimationFramesLoaded() {
        if (idleFrames != null && walkFrames != null && runFrames != null && slashFrames != null && pullBossFrames != null && pullHandFrame != null) {
            return;
        }

        idleFrameTextures = loadFrameTextures("boss_idle_sheet.png", IDLE_FRAME_SLICES);
        walkFrameTextures = loadFrameTextures("boss_walk_sheet.png", WALK_FRAME_SLICES);
        runFrameTextures = loadFrameTextures("boss_run_sheet.png", RUN_FRAME_SLICES);
        idleFrames = toRegions(idleFrameTextures);
        walkFrames = toRegions(walkFrameTextures);
        runFrames = toRegions(runFrameTextures);
        slashSheetTexture = new Texture(Gdx.files.internal("boss_slash_sheet.png"));
        slashSheetTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        slashFrames = new TextureRegion[SLASH_FRAME_SLICES.length];
        for (int i = 0; i < SLASH_FRAME_SLICES.length; i++) {
            FrameSlice slice = SLASH_FRAME_SLICES[i];
            slashFrames[i] = new TextureRegion(slashSheetTexture, slice.x, slice.y, slice.width, slice.height);
        }
        pullSheetTexture = new Texture(Gdx.files.internal("boss_pull_sheet.png"));
        pullSheetTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pullBossFrames = new TextureRegion[PULL_BOSS_FRAME_SLICES.length];
        for (int i = 0; i < PULL_BOSS_FRAME_SLICES.length; i++) {
            FrameSlice slice = PULL_BOSS_FRAME_SLICES[i];
            pullBossFrames[i] = new TextureRegion(pullSheetTexture, slice.x, slice.y, slice.width, slice.height);
        }
        pullHandFrame = new TextureRegion(pullSheetTexture, PULL_HAND_SLICE.x, PULL_HAND_SLICE.y, PULL_HAND_SLICE.width, PULL_HAND_SLICE.height);
    }

    private static Texture[] loadFrameTextures(String assetPath, FrameSlice[] slices) {
        Pixmap source = new Pixmap(Gdx.files.internal(assetPath));
        int normalizedWidth = 0;
        int normalizedHeight = 0;
        for (FrameSlice slice : slices) {
            normalizedWidth = Math.max(normalizedWidth, slice.width);
            normalizedHeight = Math.max(normalizedHeight, slice.height);
        }

        Texture[] textures = new Texture[slices.length];
        for (int i = 0; i < slices.length; i++) {
            FrameSlice slice = slices[i];
            Pixmap framePixmap = new Pixmap(normalizedWidth, normalizedHeight, Pixmap.Format.RGBA8888);
            int drawX = (normalizedWidth - slice.width) / 2;
            int drawY = normalizedHeight - slice.height;
            framePixmap.drawPixmap(source, drawX, drawY, slice.x, slice.y, slice.width, slice.height);
            Texture texture = new Texture(framePixmap);
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            textures[i] = texture;
            framePixmap.dispose();
        }
        source.dispose();
        return textures;
    }

    private static TextureRegion[] toRegions(Texture[] textures) {
        TextureRegion[] regions = new TextureRegion[textures.length];
        for (int i = 0; i < textures.length; i++) {
            regions[i] = new TextureRegion(textures[i]);
        }
        return regions;
    }

    public void renderSprite(SpriteBatch batch) {
        if (!alive) {
            return;
        }

        ensureAnimationFramesLoaded();
        boolean walking = state == State.IDLE && currentMoveSpeed > 8f;
        boolean charging = state == State.CHARGING && currentMoveSpeed > 8f;
        TextureRegion frame;
        float drawY;

        if (state == State.SLASH && slashFrames != null) {
            int slashFrameIndex = getSlashFrameIndex();
            frame = slashFrames[slashFrameIndex];
            drawY = y + SLASH_DRAW_OFFSET_Y;
            drawAnchoredFrame(batch, frame, x + WIDTH * 0.5f, drawY, SLASH_DRAW_SCALE,
                    SLASH_FRAME_PIVOT_X[slashFrameIndex], attackFacingRight, SLASH_SOURCE_FACES_RIGHT);
        } else if (state == State.PULL && pullBossFrames != null) {
            int pullFrameIndex = pullPhase == PullPhase.WINDUP ? 0 : 1;
            frame = pullBossFrames[pullFrameIndex];
            drawY = y + PULL_DRAW_OFFSET_Y;
            drawAnchoredFrame(batch, frame, x + WIDTH * 0.5f, drawY, PULL_DRAW_SCALE,
                    PULL_BOSS_PIVOT_X[pullFrameIndex], pullFacingRight, PULL_SOURCE_FACES_RIGHT);
        } else if (walking && walkFrames != null) {
            frame = walkFrames[(int)(moveAnimationTime / WALK_FRAME_DURATION) % walkFrames.length];
            drawY = y + SPRITE_DRAW_OFFSET_Y;
            batch.draw(frame, facingRight ? x + WIDTH * 0.5f + SPRITE_DRAW_WIDTH * 0.5f : x + WIDTH * 0.5f - SPRITE_DRAW_WIDTH * 0.5f, drawY,
                    facingRight ? SPRITE_DRAW_WIDTH : -SPRITE_DRAW_WIDTH, SPRITE_DRAW_HEIGHT);
        } else if (charging) {
            frame = runFrames[(int)(moveAnimationTime / RUN_FRAME_DURATION) % runFrames.length];
            drawY = y + SPRITE_DRAW_OFFSET_Y;
            batch.draw(frame, facingRight ? x + WIDTH * 0.5f + SPRITE_DRAW_WIDTH * 0.5f : x + WIDTH * 0.5f - SPRITE_DRAW_WIDTH * 0.5f, drawY,
                    facingRight ? -SPRITE_DRAW_WIDTH : SPRITE_DRAW_WIDTH, SPRITE_DRAW_HEIGHT);
        } else {
            frame = idleFrames[0];
            drawY = y + SPRITE_DRAW_OFFSET_Y;
            batch.draw(frame, facingRight ? x + WIDTH * 0.5f + SPRITE_DRAW_WIDTH * 0.5f : x + WIDTH * 0.5f - SPRITE_DRAW_WIDTH * 0.5f, drawY,
                    facingRight ? -SPRITE_DRAW_WIDTH : SPRITE_DRAW_WIDTH, SPRITE_DRAW_HEIGHT);
        }

        if (state == State.PULL && pullHandActive && pullHandFrame != null) {
            float handAnchorX = pullHandX;
            float handHeight = pullHandFrame.getRegionHeight() * PULL_DRAW_SCALE;
            float handDrawY = pullHandY - handHeight * 0.58f;
            drawAnchoredFrame(batch, pullHandFrame, handAnchorX, handDrawY, PULL_DRAW_SCALE, 45f, pullFacingRight, PULL_SOURCE_FACES_RIGHT);
        }
    }

    private void updateHeadPressureFromContact(Player player, float delta, float arenaMinX, float arenaMaxX) {
        Rectangle playerBounds = player.getBounds();
        Rectangle bossHeadBounds = getHeadBounds();
        boolean stompingHead =
                playerBounds.x + playerBounds.width > bossHeadBounds.x + 6f &&
                playerBounds.x < bossHeadBounds.x + bossHeadBounds.width - 6f &&
                playerBounds.y <= bossHeadBounds.y + bossHeadBounds.height + 14f &&
                playerBounds.y >= bossHeadBounds.y - 18f;

        if (!stompingHead) {
            return;
        }

        absorbHeadPressure(player, delta * 1.5f, arenaMinX, arenaMaxX);
        if (stompRepelCooldown <= 0f && player.getY() >= bossHeadBounds.y - 6f) {
            player.setVelocityY(Constants.PLAYER_JUMP_POWER * 0.34f);
            player.setOnGround(false);
            stompRepelCooldown = 0.24f;
        }
    }

    public void render(ShapeRenderer shapeRenderer, float time) {
        if (!alive) {
            renderCorpse(shapeRenderer);
            return;
        }

        if (idleFrames != null || walkFrames != null || runFrames != null) {
            renderAnimatedEffects(shapeRenderer, time);
            return;
        }

        renderFallbackBody(shapeRenderer, time);
    }

    private void renderFallbackBody(ShapeRenderer shapeRenderer, float time) {
        if (!alive) {
            return;
        }

        float drawY = y + VISUAL_GROUND_OFFSET;
        float pulse = 0.5f + 0.5f * MathUtils.sin(time * 3.1f);
        float breathe = MathUtils.sin(time * 2.4f) * 2.2f;
        float dir = facingRight ? 1f : -1f;
        float flash = MathUtils.clamp(damageFlashTimer / 0.2f, 0f, 1f);
        float headOffset = state == State.CHARGE_WINDUP ? -8f * (1f - stateTimer / CHARGE_WINDUP) : 0f;

        shapeRenderer.setColor(MathUtils.lerp(0.18f, 0.95f, flash), MathUtils.lerp(0.12f, 0.18f, flash), MathUtils.lerp(0.10f, 0.14f, flash), 1f);
        shapeRenderer.rect(x + 26f, drawY + 26f, 72f, 84f + breathe);
        shapeRenderer.setColor(MathUtils.lerp(0.48f, 1f, flash), MathUtils.lerp(0.42f, 0.24f, flash), MathUtils.lerp(0.36f, 0.20f, flash), 1f);
        shapeRenderer.rect(x + 34f + headOffset, drawY + 92f + breathe, 54f, 34f);
        shapeRenderer.setColor(0.03f, 0.03f, 0.04f, 1f);
        shapeRenderer.rect(x + 44f + headOffset, drawY + 108f + breathe, 10f, 6f);
        shapeRenderer.rect(x + 68f + headOffset, drawY + 108f + breathe, 10f, 6f);
        shapeRenderer.setColor(0.85f, 0.16f + pulse * 0.10f, 0.10f, 1f);
        shapeRenderer.rect(x + (dir > 0f ? 72f : 42f) + headOffset, drawY + 110f + breathe, 8f, 8f);
        shapeRenderer.setColor(0.14f, 0.10f, 0.10f, 1f);
        shapeRenderer.rectLine(x + 18f, drawY + 34f, x - 22f - dir * 4f, drawY + 18f, 16f);
        shapeRenderer.rectLine(x + 106f, drawY + 34f, x + 146f + dir * 4f, drawY + 18f, 16f);
        shapeRenderer.rectLine(x + 44f, drawY + 22f, x + 34f, drawY - 20f, 18f);
        shapeRenderer.rectLine(x + 80f, drawY + 22f, x + 92f, drawY - 18f, 18f);

        if (state == State.PULL && pullHandActive) {
            shapeRenderer.setColor(0.22f, 0.76f, 0.84f, 0.18f + pulse * 0.10f);
            shapeRenderer.rect(pullBounds.x, pullBounds.y, pullBounds.width, pullBounds.height);
            shapeRenderer.setColor(0.68f, 0.94f, 1f, 0.58f);
            shapeRenderer.rectLine(getPullHandAnchorX(), getPullHandAnchorY(), pullHandX, pullHandY, 4f);
        } else if (state == State.CHARGING) {
            shapeRenderer.setColor(0.95f, 0.20f, 0.14f, 0.28f);
            shapeRenderer.rect(x - dir * 54f, drawY + 18f, WIDTH + 54f, 92f);
        } else if (state == State.STUNNED) {
            shapeRenderer.setColor(0.92f, 0.82f, 0.38f, 0.88f);
            shapeRenderer.circle(x + WIDTH * 0.5f, drawY + 150f, 8f, 16);
            shapeRenderer.circle(x + WIDTH * 0.5f - 18f, drawY + 142f, 5f, 12);
            shapeRenderer.circle(x + WIDTH * 0.5f + 18f, drawY + 142f, 5f, 12);
        }

        if (headActive) {
            renderHead(shapeRenderer, headX, headY, dir);
        }
    }

    private void renderAnimatedEffects(ShapeRenderer shapeRenderer, float time) {
        float pulse = 0.5f + 0.5f * MathUtils.sin(time * 3.1f);
        float drawY = y + VISUAL_GROUND_OFFSET;
        float dir = facingRight ? 1f : -1f;

        if (state == State.PULL && pullHandActive) {
            shapeRenderer.setColor(0.22f, 0.76f, 0.84f, 0.18f + pulse * 0.10f);
            shapeRenderer.rect(pullBounds.x, pullBounds.y, pullBounds.width, pullBounds.height);
            shapeRenderer.setColor(0.68f, 0.94f, 1f, 0.58f);
            shapeRenderer.rectLine(getPullHandAnchorX(), getPullHandAnchorY(), pullHandX, pullHandY, 4f);
        } else if (state == State.CHARGING) {
            shapeRenderer.setColor(0.95f, 0.20f, 0.14f, 0.28f);
            shapeRenderer.rect(x - dir * 54f, drawY + 18f, WIDTH + 54f, 92f);
        } else if (state == State.STUNNED) {
            shapeRenderer.setColor(0.92f, 0.82f, 0.38f, 0.88f);
            shapeRenderer.circle(x + WIDTH * 0.5f, drawY + 150f, 8f, 16);
            shapeRenderer.circle(x + WIDTH * 0.5f - 18f, drawY + 142f, 5f, 12);
            shapeRenderer.circle(x + WIDTH * 0.5f + 18f, drawY + 142f, 5f, 12);
        }

        if (headActive) {
            renderHead(shapeRenderer, headX, headY, dir);
        }
    }

    private void renderHead(ShapeRenderer shapeRenderer, float drawX, float drawY, float dir) {
        shapeRenderer.setColor(0.50f, 0.44f, 0.38f, 1f);
        shapeRenderer.rect(drawX, drawY, 34f, 34f);
        shapeRenderer.setColor(0.03f, 0.03f, 0.04f, 1f);
        shapeRenderer.rect(drawX + 8f, drawY + 19f, 6f, 5f);
        shapeRenderer.rect(drawX + 20f, drawY + 19f, 6f, 5f);
        shapeRenderer.setColor(0.82f, 0.16f, 0.10f, 1f);
        shapeRenderer.rect(drawX + (dir > 0f ? 20f : 8f), drawY + 19f, 6f, 6f);
        shapeRenderer.setColor(0.80f, 0.70f, 0.56f, 1f);
        shapeRenderer.rect(drawX + 10f, drawY + 8f, 14f, 7f);
    }

    private void renderCorpse(ShapeRenderer shapeRenderer) {
        float drawY = y + VISUAL_GROUND_OFFSET;
        shapeRenderer.setColor(0.10f, 0.06f, 0.06f, 1f);
        shapeRenderer.rect(x + 16f, drawY + 8f, 98f, 30f);
        shapeRenderer.setColor(0.28f, 0.18f, 0.16f, 1f);
        shapeRenderer.rect(x + 54f, drawY + 22f, 38f, 20f);
    }

    private void updateBounds() {
        bounds.set(x, y, WIDTH, HEIGHT);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return MAX_HEALTH;
    }

    public boolean isStunned() {
        return state == State.STUNNED && alive;
    }

    public static void disposeAssets() {
        if (idleFrameTextures != null) {
            for (Texture texture : idleFrameTextures) {
                texture.dispose();
            }
            idleFrameTextures = null;
            idleFrames = null;
        }
        if (walkFrameTextures != null) {
            for (Texture texture : walkFrameTextures) {
                texture.dispose();
            }
            walkFrameTextures = null;
            walkFrames = null;
        }
        if (runFrameTextures != null) {
            for (Texture texture : runFrameTextures) {
                texture.dispose();
            }
            runFrameTextures = null;
            runFrames = null;
        }
        if (slashSheetTexture != null) {
            slashSheetTexture.dispose();
            slashSheetTexture = null;
            slashFrames = null;
        }
        if (pullSheetTexture != null) {
            pullSheetTexture.dispose();
            pullSheetTexture = null;
            pullBossFrames = null;
            pullHandFrame = null;
        }
    }
}
