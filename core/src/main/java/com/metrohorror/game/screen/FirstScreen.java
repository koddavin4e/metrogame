package com.metrohorror.game.screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.metrohorror.game.entities.Enemy;
import com.metrohorror.game.entities.Loot;
import com.metrohorror.game.entities.Player;
import com.metrohorror.game.entities.WeaponType;
import com.metrohorror.game.systems.CameraSystem;
import com.metrohorror.game.systems.CombatSystem;
import com.metrohorror.game.systems.InventorySystem;
import com.metrohorror.game.systems.LootSystem;
import com.metrohorror.game.ui.InventoryUI;
import com.metrohorror.game.util.Constants;
import com.metrohorror.game.world.GameMap;
import com.metrohorror.game.world.Platform;

public class FirstScreen implements Screen {

    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    private Player player;
    private Array<Enemy> enemies;

    private GameMap gameMap;
    private CombatSystem combatSystem;
    private InventorySystem inventorySystem;
    private LootSystem lootSystem;
    private CameraSystem cameraSystem;
    private InventoryUI inventoryUI;

    private boolean inventoryVisible = false;
    private boolean tabPressedLastFrame = false;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, 1280, 720);

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();

        player = new Player(100, 200);
        enemies = new Array<>();
        spawnEnemies();

        gameMap = new GameMap();
        combatSystem = new CombatSystem();
        inventorySystem = new InventorySystem();
        lootSystem = new LootSystem();
        cameraSystem = new CameraSystem();
        inventoryUI = new InventoryUI();
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.06f, 0.06f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Ground
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(
                gameMap.getGround().x,
                gameMap.getGround().y,
                gameMap.getGround().width,
                gameMap.getGround().height
        );

        // Platforms
        shapeRenderer.setColor(Color.GRAY);
        for (Platform platform : gameMap.getPlatforms()) {
            shapeRenderer.rect(
                    platform.getBounds().x,
                    platform.getBounds().y,
                    platform.getBounds().width,
                    platform.getBounds().height
            );
        }

        // Player
        shapeRenderer.setColor(player.isRecentlyDamaged() ? Color.SCARLET : Color.WHITE);
        shapeRenderer.rect(
                player.getBounds().x,
                player.getBounds().y,
                player.getBounds().width,
                player.getBounds().height
        );
        renderWeapon();

        for (Enemy enemy : enemies) {
            renderEnemy(enemy);
        }

        // Loot
        shapeRenderer.setColor(Color.GOLD);
        for (Loot loot : lootSystem.getDrops()) {
            shapeRenderer.rect(
                    loot.getBounds().x,
                    loot.getBounds().y,
                    loot.getBounds().width,
                    loot.getBounds().height
            );
        }

        shapeRenderer.end();

        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        WeaponType selectedWeapon = inventorySystem.getSelectedWeapon();
        String weaponName = selectedWeapon == null ? "None" : selectedWeapon.getDisplayName();
        font.draw(batch, "A/D - move | SPACE - jump | J - attack | 1-4 switch weapon | TAB - inventory", 20, 710);
        font.draw(batch, "Current weapon: " + weaponName, 20, 685);
        font.draw(batch, "HP: " + player.getHealth() + " | Enemies: " + countAliveEnemies(), 20, 660);
        inventoryUI.render(batch, font, inventorySystem, inventoryVisible);
        batch.end();
    }

    private void update(float delta) {
        handleInput();
        player.applyGravity(delta);
        player.update(delta);

        resolveWorldCollisions();
        updateEnemies(delta);
        combatSystem.update(delta, player, enemies, inventorySystem.getSelectedWeapon());

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive() && !enemy.isLootDropped()) {
                lootSystem.spawnLootFromEnemy(enemy);
                enemy.markLootDropped();
            }
        }

        lootSystem.collectLoot(player, inventorySystem);
        cameraSystem.follow(camera, player, delta);
        uiCamera.update();
    }

    private void handleInput() {
        player.stopX();

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.moveLeft();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.moveRight();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.jump();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            inventorySystem.selectWeaponSlot(0);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            inventorySystem.selectWeaponSlot(1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            inventorySystem.selectWeaponSlot(2);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            inventorySystem.selectWeaponSlot(3);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
            combatSystem.startAttack(inventorySystem.getSelectedWeapon());
        }

        boolean tabPressed = Gdx.input.isKeyPressed(Input.Keys.TAB);
        if (tabPressed && !tabPressedLastFrame) {
            inventoryVisible = !inventoryVisible;
        }
        tabPressedLastFrame = tabPressed;
    }

    private void resolveWorldCollisions() {
        boolean landed = false;

        // Ground collision
        float groundTop = gameMap.getGround().y + gameMap.getGround().height;
        if (player.getBounds().y <= groundTop) {
            player.setY(groundTop);
            player.setVelocityY(0);
            landed = true;
        }

        // Platform collision
        for (Platform platform : gameMap.getPlatforms()) {
            float platformTop = platform.getBounds().y + platform.getBounds().height;

            boolean horizontalOverlap =
                    player.getBounds().x + player.getBounds().width > platform.getBounds().x &&
                    player.getBounds().x < platform.getBounds().x + platform.getBounds().width;

            boolean nearTop =
                    player.getBounds().y <= platformTop &&
                    player.getBounds().y >= platformTop - 25 &&
                    player.getVelocityY() <= 0;

            if (horizontalOverlap && nearTop) {
                player.setY(platformTop);
                player.setVelocityY(0);
                landed = true;
            }
        }

        player.setOnGround(landed);

        if (player.getX() < 0) player.setX(0);
        if (player.getX() > Constants.WORLD_WIDTH - Constants.PLAYER_WIDTH) {
            player.setX(Constants.WORLD_WIDTH - Constants.PLAYER_WIDTH);
        }
    }

    private void renderWeapon() {
        WeaponType weapon = combatSystem.getVisibleWeapon(inventorySystem.getSelectedWeapon());
        if (weapon == null) {
            return;
        }

        float handX = combatSystem.getHandPosition().x;
        float handY = combatSystem.getHandPosition().y;
        float tipX = combatSystem.getWeaponTipPosition().x;
        float tipY = combatSystem.getWeaponTipPosition().y;
        float dir = player.isFacingRight() ? 1f : -1f;

        shapeRenderer.setColor(0.25f, 0.16f, 0.08f, 1f);
        shapeRenderer.rectLine(handX - dir * 6f, handY - 2f, handX + dir * 8f, handY - 4f, 5f);

        shapeRenderer.setColor(weapon.getColorR(), weapon.getColorG(), weapon.getColorB(), 1f);
        shapeRenderer.rectLine(handX, handY, tipX, tipY, weapon.getThickness());

        if (combatSystem.isAttacking()) {
            float progress = combatSystem.getSwingProgress();
            float trailX = handX + (tipX - handX) * Math.max(0f, progress - 0.18f);
            float trailY = handY + (tipY - handY) * Math.max(0f, progress - 0.18f);
            shapeRenderer.setColor(weapon.getColorR(), weapon.getColorG(), weapon.getColorB(), 0.16f);
            shapeRenderer.rectLine(handX, handY, trailX, trailY, weapon.getThickness() + 8f);
            shapeRenderer.setColor(1f, 1f, 1f, 0.25f);
            shapeRenderer.circle(tipX, tipY, Math.max(4f, weapon.getThickness() * 0.7f), 14);
        }
    }

    private void renderEnemy(Enemy enemy) {
        if (!enemy.isAlive()) {
            return;
        }

        shapeRenderer.setColor(0.72f, 0.12f, 0.12f, 1f);
        shapeRenderer.rect(
                enemy.getBounds().x,
                enemy.getBounds().y,
                enemy.getBounds().width,
                enemy.getBounds().height
        );

        float headX = enemy.getBounds().x + enemy.getBounds().width / 2f;
        float headY = enemy.getBounds().y + enemy.getBounds().height * 0.65f;
        float clawX = headX + (enemy.isFacingRight() ? 22f : -22f);
        float clawY = enemy.getBounds().y + enemy.getBounds().height * 0.45f;
        if (enemy.isAttacking()) {
            float progress = enemy.getAttackProgress();
            clawX += (enemy.isFacingRight() ? 1f : -1f) * 12f * progress;
        }

        shapeRenderer.setColor(0.2f, 0.02f, 0.02f, 1f);
        shapeRenderer.rectLine(headX, headY, clawX, clawY, 7f);
        shapeRenderer.rectLine(headX, enemy.getBounds().y + 10f, headX + (enemy.isFacingRight() ? 12f : -12f),
                enemy.getBounds().y - 4f, 5f);
    }

    private void updateEnemies(float delta) {
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                enemy.update(delta);
                continue;
            }

            float distanceToPlayer = player.getX() - enemy.getX();
            float absDistance = Math.abs(distanceToPlayer);

            if (absDistance < Constants.ENEMY_AGGRO_RANGE && absDistance > Constants.ENEMY_ATTACK_RANGE) {
                enemy.moveToward(player.getX(), Constants.ENEMY_SPEED, delta);
            }

            if (absDistance <= Constants.ENEMY_ATTACK_RANGE && enemy.canAttack() && player.isAlive()) {
                enemy.triggerAttack();
                player.takeDamage(Constants.ENEMY_DAMAGE);
            }

            enemy.update(delta);
        }
    }

    private int countAliveEnemies() {
        int alive = 0;
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                alive++;
            }
        }
        return alive;
    }

    private void spawnEnemies() {
        float groundY = Constants.GROUND_Y + Constants.GROUND_HEIGHT;
        enemies.add(new Enemy(820, groundY));
        enemies.add(new Enemy(1120, groundY));
        enemies.add(new Enemy(1540, groundY));
        enemies.add(new Enemy(1960, groundY));
    }

    @Override public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        uiCamera.setToOrtho(false, width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}
