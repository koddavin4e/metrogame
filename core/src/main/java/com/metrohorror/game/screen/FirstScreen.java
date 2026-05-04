package com.metrohorror.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.metrohorror.game.MetroHorrorGame;
import com.metrohorror.game.entities.Enemy;
import com.metrohorror.game.entities.FinalBoss;
import com.metrohorror.game.entities.Player;
import com.metrohorror.game.entities.WeaponType;
import com.metrohorror.game.screen.dialogue.DialogueScripts;
import com.metrohorror.game.screen.location.BossLocation;
import com.metrohorror.game.screen.location.LocationId;
import com.metrohorror.game.screen.location.ShantyLocation;
import com.metrohorror.game.systems.CameraSystem;
import com.metrohorror.game.systems.InventorySystem;
import com.metrohorror.game.ui.InventoryUI;
import com.metrohorror.game.util.Constants;
import com.metrohorror.game.world.DungeonMap;
import com.metrohorror.game.world.GameMap;
import com.metrohorror.game.world.Platform;

import java.util.EnumMap;
import java.util.EnumSet;

public class FirstScreen implements Screen {
    private static final float VIRTUAL_WIDTH = 1280f;
    private static final float VIRTUAL_HEIGHT = 720f;
    private static final float MAX_VISIBLE_WORLD_WIDTH = 1920f;
    private static final float MAX_VISIBLE_WORLD_HEIGHT = 1080f;
    private static final Rectangle SCHOLAR_BOUNDS = new Rectangle(610f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 42f, 72f);
    private static final Rectangle CHAPEL_BEDROOM_DOOR = new Rectangle(1096f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 96f, 156f);
    private static final Rectangle CHAPEL_DOOR = new Rectangle(648f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 108f, 176f);
    private static final Rectangle STREET_RETURN_DOOR = new Rectangle(314f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 94f, 170f);
    private static final Rectangle STREET_KNIFE_PICKUP = new Rectangle(2296f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 108f, 62f);
    private static final Rectangle BEDROOM_BED = new Rectangle(208f, Constants.GROUND_Y + Constants.GROUND_HEIGHT + 8f, 205f, 68f);
    private static final Rectangle BEDROOM_TV = new Rectangle(846f, Constants.GROUND_Y + Constants.GROUND_HEIGHT + 34f, 144f, 130f);
    private static final Rectangle BEDROOM_DOOR = new Rectangle(1046f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 88f, 154f);
    private static final float BEDROOM_LEFT_WALL = 96f;
    private static final float BEDROOM_RIGHT_WALL = 1240f;
    private static final float BEDROOM_COLLISION_RIGHT_WALL = BEDROOM_DOOR.x + BEDROOM_DOOR.width + 8f;
    private static final float BEDROOM_PLAYER_X = 300f;
    private static final float BEDROOM_BACKGROUND_Y = -74f;
    private static final float BEDROOM_BACKGROUND_HEIGHT = 826.6667f;
    private static final float BEDROOM_PLAYER_SCALE = 1.5f;
    private static final float BEDROOM_TV_SCREEN_X = 856f;
    private static final float BEDROOM_TV_SCREEN_Y = 271f;
    private static final float BEDROOM_TV_SCREEN_WIDTH = 82f;
    private static final float BEDROOM_TV_SCREEN_HEIGHT = 58f;
    private static final int BEDROOM_TV_STATIC_FRAME_COUNT = 10;
    private static final int BEDROOM_TV_STATIC_FRAME_WIDTH = 56;
    private static final int BEDROOM_TV_STATIC_FRAME_HEIGHT = 40;
    private static final float HALLWAY_PLAYER_SCALE = 1.5f;
    private static final float STREET_PLAYER_SCALE = 1.5f;
    private static final float BOSS_PLAYER_SCALE = 1.5f;
    private static final float TV_REQUIRED_WATCH_TIME = 2.5f;
    private static final float HALLWAY_BACKGROUND_X = 260f;
    private static final float HALLWAY_BACKGROUND_Y = 0f;
    private static final float HALLWAY_BACKGROUND_WIDTH = 1500f;
    private static final float HALLWAY_BACKGROUND_HEIGHT = 1000f;
    private static final float HALLWAY_LEFT_WALL = 318f;
    private static final float HALLWAY_RIGHT_WALL = 1708f;
    private static final float HALLWAY_PLAYER_EXIT_X = CHAPEL_BEDROOM_DOOR.x + 6f;
    private static final float STREET_BACKGROUND_X = 240f;
    private static final float STREET_BACKGROUND_Y = 0f;
    private static final float STREET_BACKGROUND_WIDTH = 2520f;
    private static final float STREET_BACKGROUND_HEIGHT = 1000f;
    private static final float STREET_LEFT_WALL = 286f;
    private static final float STREET_RIGHT_WALL = 2680f;
    private static final float STREET_PLAYER_EXIT_X = STREET_RETURN_DOOR.x + 18f;
    private static final float STREET_GROUND_TOP = Constants.GROUND_Y + Constants.GROUND_HEIGHT + 64f;
    private static final float BOSS_BACKGROUND_X = 150f;
    // Lower the boss-room backdrop slightly so it lines up with the floor plane.
    private static final float BOSS_BACKGROUND_Y = 0f;
    private static final float BOSS_BACKGROUND_WIDTH = 2700f;
    private static final float BOSS_BACKGROUND_HEIGHT = 960f;
    private static final float BOSS_CAMERA_ZOOM = 1.22f;
    private static final float BOSS_LEFT_WALL = 180f;
    private static final float BOSS_RIGHT_WALL = 2820f;
    private static final float BOSS_PLAYER_ENTRY_X = 240f;
    private static final float STREET_ENEMY_MIN_X = STREET_RETURN_DOOR.x + 115f;
    private static final float STREET_ENEMY_MAX_X = Constants.WORLD_WIDTH - 120f;
    private static final float SHANTY_BACKGROUND_X = 0f;
    private static final float SHANTY_BACKGROUND_Y = -36f;
    private static final float SHANTY_BACKGROUND_WIDTH = 3000f;
    private static final float SHANTY_BACKGROUND_HEIGHT = 1245f;
    private static final float SHANTY_CAMERA_PADDING = 1.01f;
    private static final float SHANTY_LEFT_WALL = 0f;
    private static final float SHANTY_RIGHT_WALL = 3000f;
    private static final float SHANTY_PLAYER_ENTRY_X = 130f;
    private static final float SHANTY_PLAYER_RETURN_X = STREET_RIGHT_WALL - 220f;
    private static final float SHANTY_PLAYER_BOSS_RETURN_X = SHANTY_RIGHT_WALL - 260f;
    private static final float SHANTY_TO_BOSS_THRESHOLD = SHANTY_RIGHT_WALL - Constants.PLAYER_WIDTH - 26f;
    private static final float STREET_TO_SHANTY_THRESHOLD = STREET_RIGHT_WALL - Constants.PLAYER_WIDTH - 26f;
    private static final float SHANTY_GROUND_TOP = 34f;
    private static final float SHANTY_PLAYER_SCALE = 1.32f;
    private static final float BOSS_ARENA_MIN_X = 390f;
    private static final float BOSS_ARENA_MAX_X = 2640f;
    // Keep both actors on the same visible floor line in the fourth room.
    private static final float BOSS_ROOM_GROUND_TOP = Constants.GROUND_Y + Constants.GROUND_HEIGHT + 66f;
    private static final float FINAL_BOSS_SPAWN_X = 2130f;
    private static final float FINAL_BOSS_SPAWN_Y = BOSS_ROOM_GROUND_TOP;
    private static final float KNIFE_PICKUP_PROMPT_RANGE = 84f;
    private static final float TYPE_SPEED = 42f;
    private static final int KNIFE_DAMAGE = 2;
    private static final int HIT_HEAL_AMOUNT = 1;
    private static final int KILL_HEAL_AMOUNT = 5;
    private static final float KNIFE_FORWARD_RANGE_BONUS = 28f;
    private static final float DOWN_ATTACK_BOUNCE = 520f;
    private static final float RESPAWN_X_OFFSET = 120f;
    private static final float SHANTY_ENEMY_VERTICAL_OFFSET = -168f;
    private static final float DAMAGE_TEXT_LIFETIME = 0.72f;
    private static final String FLYING_ENEMY_ASSET = "enemy_bat_sheet.png";
    private static final String FLYING_ENEMY_PROJECTILE_ASSET = "enemy_bat_projectile.png";
    private static final String FLYING_SLASHER_ASSET = "enemy_reaper_sheet.png";
    private static final int[] FLYING_ENEMY_FRAME_X = {8, 160, 308, 453, 608};
    private static final int[] FLYING_ENEMY_FRAME_WIDTH = {132, 130, 125, 141, 139};
    private static final float FLYING_ENEMY_DRAW_WIDTH = 118f;
    private static final float FLYING_ENEMY_DRAW_HEIGHT = 94f;
    private static final float FLYING_ENEMY_FLAP_SPEED = 10f;
    private static final float FLYING_ENEMY_ENGAGE_Y_RANGE = 170f;
    private static final float FLYING_ENEMY_VERTICAL_SPEED = 92f;
    private static final float FLYING_ENEMY_PROJECTILE_DRAW_WIDTH = 28f;
    private static final float FLYING_ENEMY_PROJECTILE_DRAW_HEIGHT = 18f;
    private static final float FLYING_ENEMY_PROJECTILE_LIFETIME = 4.5f;
    private static final float FLYING_ENEMY_PROJECTILE_GLOW = 10f;
    private static final int FLYING_SLASHER_FRAME_COUNT = 4;
    private static final int FLYING_SLASHER_FRAME_WIDTH = 543;
    private static final int FLYING_SLASHER_FRAME_HEIGHT = 724;
    private static final float FLYING_SLASHER_DRAW_WIDTH = 136f;
    private static final float FLYING_SLASHER_DRAW_HEIGHT = 112f;
    private static final int[] FLYING_SLASHER_IDLE_FRAMES = {2, 3};
    private static final float FLYING_SLASHER_IDLE_ANIM_SPEED = 3.2f;
    private static final float FLYING_SLASHER_ENGAGE_X_RANGE = 420f;
    private static final float FLYING_SLASHER_ENGAGE_Y_RANGE = 210f;
    private static final float FLYING_SLASHER_VERTICAL_SPEED = 116f;
    private static final float PAUSE_MENU_WIDTH = 360f;
    private static final float PAUSE_BUTTON_HEIGHT = 54f;
    private static final float PAUSE_BUTTON_GAP = 14f;
    private static final float INVENTORY_PANEL_WIDTH = 1300f;
    private static final float INVENTORY_PANEL_HEIGHT = 800f;
    private static final float INVENTORY_WEAPON_SLOT_WIDTH = 84f;
    private static final float INVENTORY_WEAPON_SLOT_HEIGHT = 126f;
    private static final float INVENTORY_UTILITY_SLOT_SIZE = 84f;
    private static final int INVENTORY_EQUIPPED_WEAPON_SLOT_COUNT = 2;
    private static final float INVENTORY_BAG_SLOT_WIDTH = 86f;
    private static final float INVENTORY_BAG_SLOT_HEIGHT = 78f;
    private static final float INVENTORY_BAG_SLOT_GAP_X = 16f;
    private static final float INVENTORY_BAG_SLOT_GAP_Y = 15f;
    private static final float INVENTORY_BAG_START_X = 580f;
    private static final float INVENTORY_BAG_TOP_ROW_BOTTOM_Y = 589f;
    private static final int INVENTORY_BAG_COLUMNS = 6;
    private static final float INVENTORY_CHARACTER_LEFT = 54f;
    private static final float INVENTORY_CHARACTER_BOTTOM = 324f;
    private static final float INVENTORY_CHARACTER_WIDTH = 196f;
    private static final float INVENTORY_CHARACTER_HEIGHT = 318f;
    private static final int HERO_FRAME_SIZE = 128;
    private static final int HERO_FRAME_COUNT = 17;
    private static final float HERO_BODY_DRAW_WIDTH = 88f;
    private static final float HERO_ATTACK_DRAW_WIDTH = HERO_BODY_DRAW_WIDTH;
    private static final float HERO_DRAW_HEIGHT = 96f;
    private static final float BEDROOM_PROMPT_Y = 104f;
    private static final int SAVE_SLOT_COUNT = 3;
    private static final String SAVE_PREFERENCES = "metrohorror-saves";
    private static final String[] PAUSE_BUTTON_LABELS = {
            "\u0412\u044b\u0439\u0442\u0438 \u0432 \u043c\u0435\u043d\u044e",
            "\u0414\u043e\u0441\u0442\u0438\u0436\u0435\u043d\u0438\u044f",
            "\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438",
            "\u041f\u0440\u043e\u0434\u043e\u043b\u0436\u0438\u0442\u044c"
    };

    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;
    private Viewport worldViewport;
    private Viewport hudViewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout glyphLayout;
    private Texture heroTexture;
    private Texture bedroomTexture;
    private Texture hallwayTexture;
    private Texture streetTexture;
    private Texture shantyTexture;
    private Texture fogPixelTexture;
    private Texture shantyFogTexture;
    private Texture bossTexture;
    private Texture flyingEnemyTexture;
    private Texture flyingSlasherTexture;
    private Texture flyingEnemyProjectileTexture;
    private Texture[] bedroomTvStaticFrames;
    private final EnumMap<WeaponType, Texture> weaponIconTextures = new EnumMap<WeaponType, Texture>(WeaponType.class);
    private final EnumSet<WeaponType> missingWeaponIconTextures = EnumSet.noneOf(WeaponType.class);
    private TextureRegion[] heroFrames;
    private TextureRegion[] flyingEnemyFrames;
    private TextureRegion[] flyingSlasherFrames;
    private final MetroHorrorGame game;
    private final int loadSlot;
    private final Rectangle[] pauseButtonBounds = new Rectangle[5];
    private final Vector3 pointer = new Vector3();

    private Player player;
    private Array<Enemy> dungeonEnemies;
    private Array<EnemyProjectile> enemyProjectiles;
    private Array<FloatingDamageText> floatingDamageTexts;
    private FinalBoss finalBoss;
    private GameMap baseMap;
    private GameMap dungeonMap;
    private GameMap gameMap;
    private InventorySystem inventorySystem;
    private CameraSystem cameraSystem;
    private InventoryUI inventoryUI;

    private boolean inventoryVisible;
    private InventoryDragSource inventoryDragSource = InventoryDragSource.NONE;
    private int inventoryDragSlot = -1;
    private WeaponType draggedWeapon;
    private boolean dialogueVisible;
    private boolean prologueActive;
    private int dialogueLine;
    private int locationIndex;
    private float time;
    private float dialogueTypeTimer;
    private boolean knifeCollected;
    private float knifeSwingTimer;
    private boolean knifeDamageApplied;
    private AttackDirection attackDirection = AttackDirection.FORWARD;
    private boolean chapelIntroSeen;
    private boolean engineIntroSeen;
    private boolean doorLockedUntilPlayerMoves;
    private boolean watchedBedroomTv;
    private boolean shantyCleared;
    private boolean tvScreenVisible;
    private boolean pauseMenuVisible;
    private int hoveredPauseButton = -1;
    private PausePanel pausePanel = PausePanel.NONE;
    private float tvWatchTimer;

    private String[] currentSpeakers = DialogueScripts.CHAPEL_SPEAKERS;
    private String[] currentText = DialogueScripts.CHAPEL_TEXT;

    public FirstScreen() {
        this(null, -1);
    }

    public FirstScreen(MetroHorrorGame game) {
        this(game, -1);
    }

    public FirstScreen(MetroHorrorGame game, int loadSlot) {
        this.game = game;
        this.loadSlot = loadSlot;
    }

    @Override
    public void show() {
        if (camera != null) {
            worldViewport.apply(true);
            hudViewport.apply(true);
            pauseMenuVisible = true;
            hoveredPauseButton = -1;
            return;
        }

        camera = new OrthographicCamera();
        worldViewport = createCappedWorldViewport(camera);

        uiCamera = new OrthographicCamera();
        hudViewport = new ScreenViewport(uiCamera);
        worldViewport.apply(true);
        hudViewport.apply(true);

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = createGameFont();
        glyphLayout = new GlyphLayout();
        loadHeroSpriteSheet();
        loadBedroomTexture();
        loadBedroomTvStaticFrames();
        loadHallwayTexture();
        loadStreetTexture();
        loadShantyTexture();
        loadBossTexture();
        loadFlyingEnemyTextures();
        setupPauseMenuBounds();

        player = new Player(140, 240);
        player.healToFull();
        dungeonEnemies = new Array<>();
        enemyProjectiles = new Array<>();
        floatingDamageTexts = new Array<>();
        baseMap = new GameMap();
        dungeonMap = new DungeonMap();
        gameMap = baseMap;
        inventorySystem = new InventorySystem();
        cameraSystem = new CameraSystem();
        inventoryUI = new InventoryUI();
        if (isValidSaveSlot(loadSlot) && hasSave(loadSlot)) {
            loadFromSlot(loadSlot);
        } else {
            startNewGameInBedroom();
        }
    }

    private Viewport createCappedWorldViewport(OrthographicCamera worldCamera) {
        return new ScreenViewport(worldCamera) {
            @Override
            public void update(int screenWidth, int screenHeight, boolean centerCamera) {
                float worldWidth = Math.min(screenWidth * getUnitsPerPixel(), MAX_VISIBLE_WORLD_WIDTH);
                float worldHeight = Math.min(screenHeight * getUnitsPerPixel(), MAX_VISIBLE_WORLD_HEIGHT);
                setScreenBounds(0, 0, screenWidth, screenHeight);
                setWorldSize(worldWidth, worldHeight);
                apply(centerCamera);
            }
        };
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.015f, 0.010f, 0.012f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!prologueActive) {
            worldViewport.apply();
            shapeRenderer.setProjectionMatrix(camera.combined);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            renderWorld();
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            renderWorldTextures();
            renderBedroomTvStatic();
            if (locationIndex == BossLocation.INDEX && finalBoss != null) {
                finalBoss.renderSprite(batch);
            }
            renderDungeonEnemySprites();
            renderPlayerSprite();
            renderEnemyProjectiles();
            renderFloatingDamageTexts();
            batch.end();

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            renderWorldActors();
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        hudViewport.apply();
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (prologueActive) {
            renderPrologueScene();
        }
        renderUiPanels();
        renderPauseMenuPanels();
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        if (!prologueActive && inventoryVisible) {
            batch.setProjectionMatrix(uiCamera.combined);
            batch.begin();
            inventoryUI.renderBackdrop(batch, uiCamera.viewportWidth, uiCamera.viewportHeight);
            renderInventoryPanelTextures();
            batch.end();

            shapeRenderer.setProjectionMatrix(uiCamera.combined);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            renderInventoryPanelFallback();
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        if (!prologueActive) {
            renderHud();
            renderHealthBar();
            renderInventoryHeroSprite();
            inventoryUI.render(batch, font, inventorySystem, inventoryVisible, uiCamera.viewportWidth, uiCamera.viewportHeight);
        } else {
            renderPrologueTitle();
        }
        renderDialogue();
        renderTvOverlayText();
        renderPauseMenuText();
        batch.end();
    }

    private void update(float delta) {
        time += delta;
        handleInput();

        if (pauseMenuVisible) {
            uiCamera.update();
            return;
        }

        knifeSwingTimer = Math.max(0f, knifeSwingTimer - delta);
        updateFloatingDamageTexts(delta);
        if (dialogueVisible) {
            dialogueTypeTimer += delta;
        }

        if (prologueActive) {
            uiCamera.update();
            return;
        }

        if (tvScreenVisible) {
            tvWatchTimer = Math.min(TV_REQUIRED_WATCH_TIME, tvWatchTimer + delta);
            uiCamera.update();
            return;
        }

        player.applyGravity(delta);
        player.update(delta);
        resolveWorldCollisions();
        updateLocationTransitions();
        updateWorldCameraZoom();
        updateCurrentLocationState(delta);
        updateDoorLock();
        checkTrashPileDiscovery();
        updateKnifeAttack();
        cameraSystem.follow(camera, player, delta);
        clampCameraToCurrentLocation();
        uiCamera.update();
    }

    private void handleInput() {
        player.stopX();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (inventoryVisible) {
                inventoryVisible = false;
                cancelInventoryDrag();
                return;
            }
            pauseMenuVisible = !pauseMenuVisible;
            pausePanel = PausePanel.NONE;
            hoveredPauseButton = -1;
            return;
        }

        if (pauseMenuVisible) {
            updatePauseMenuInput();
            return;
        }

        if (!prologueActive && !dialogueVisible) {
            updateWeaponSelectionInput();
        }

        if (inventoryVisible) {
            updateInventoryInput();
        }

        if (!dialogueVisible && !inventoryVisible && !prologueActive && !tvScreenVisible) {
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                player.moveLeft();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                player.moveRight();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                player.jump();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.F) || Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
                player.startParry();
            }
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && inventorySystem.getSelectedWeapon() != null) {
                startKnifeAttack();
            }
        }

        if (!prologueActive && !tvScreenVisible && Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            inventoryVisible = !inventoryVisible;
            cancelInventoryDrag();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            handleAction();
        }
    }

    private void setupPauseMenuBounds() {
        float buttonX = uiCamera.viewportWidth * 0.5f - PAUSE_MENU_WIDTH * 0.5f;
        float buttonY = uiCamera.viewportHeight * 0.5f + 88f;
        for (int i = 0; i < pauseButtonBounds.length; i++) {
            pauseButtonBounds[i] = new Rectangle(
                    buttonX,
                    buttonY - i * (PAUSE_BUTTON_HEIGHT + PAUSE_BUTTON_GAP),
                    PAUSE_MENU_WIDTH,
                    PAUSE_BUTTON_HEIGHT
            );
        }
    }

    private void updatePauseMenuInput() {
        pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        hudViewport.unproject(pointer);

        hoveredPauseButton = -1;
        for (int i = 0; i < pauseButtonBounds.length; i++) {
            if (pauseButtonBounds[i].contains(pointer.x, pointer.y)) {
                hoveredPauseButton = i;
                break;
            }
        }

        if (!Gdx.input.justTouched() || hoveredPauseButton < 0) {
            return;
        }

        if (hoveredPauseButton == 0) {
            if (game != null) {
                game.setScreen(new MainMenuScreen(game));
            }
        } else if (hoveredPauseButton == 1) {
            if (game != null) {
                game.setScreen(new MenuSectionScreen(game, MenuSectionScreen.Section.ACHIEVEMENTS, this));
            } else {
                pausePanel = PausePanel.ACHIEVEMENTS;
            }
        } else if (hoveredPauseButton == 2) {
            if (game != null) {
                game.setScreen(new MenuSectionScreen(game, MenuSectionScreen.Section.SETTINGS, this));
            } else {
                pausePanel = PausePanel.SETTINGS;
            }
        } else if (hoveredPauseButton == 3) {
            if (game != null) {
                game.setScreen(new SaveSlotScreen(game, SaveSlotScreen.Mode.SAVE, this));
            }
        } else if (hoveredPauseButton == 4) {
            pauseMenuVisible = false;
            pausePanel = PausePanel.NONE;
        }
    }

    private void updateWeaponSelectionInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            inventorySystem.selectWeaponSlot(0);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            inventorySystem.selectWeaponSlot(1);
        }

    }

    private void updateInventoryInput() {
        pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        hudViewport.unproject(pointer);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            int handSlot = getHandSlotAt(pointer.x, pointer.y);
            if (handSlot >= 0) {
                draggedWeapon = inventorySystem.takeWeaponFromHand(handSlot);
                if (draggedWeapon != null) {
                    if (handSlot == inventorySystem.getSelectedWeaponSlot()) {
                        cancelKnifeSwing();
                    }
                    inventoryDragSource = InventoryDragSource.HAND;
                    inventoryDragSlot = handSlot;
                }
                return;
            }

            int bagSlot = getBagSlotAt(pointer.x, pointer.y);
            if (bagSlot >= 0) {
                draggedWeapon = inventorySystem.takeWeaponFromBag(bagSlot);
                if (draggedWeapon != null) {
                    inventoryDragSource = InventoryDragSource.BAG;
                    inventoryDragSlot = bagSlot;
                }
            }
        }

        if (draggedWeapon != null && !Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            dropInventoryWeapon(pointer.x, pointer.y);
        }
    }

    private void dropInventoryWeapon(float x, float y) {
        int handSlot = getHandSlotAt(x, y);
        if (handSlot >= 0) {
            WeaponType displaced = inventorySystem.takeWeaponFromHand(handSlot);
            inventorySystem.equipWeapon(handSlot, draggedWeapon);
            returnDraggedWeapon(displaced);
            clearInventoryDrag();
            return;
        }

        int bagSlot = getBagSlotAt(x, y);
        if (bagSlot >= 0) {
            WeaponType displaced = inventorySystem.takeWeaponFromBag(bagSlot);
            inventorySystem.putBagWeapon(bagSlot, draggedWeapon);
            returnDraggedWeapon(displaced);
            clearInventoryDrag();
            return;
        }

        if (inventoryDragSource == InventoryDragSource.HAND && inventorySystem.addWeaponToBag(draggedWeapon)) {
            clearInventoryDrag();
            return;
        }

        returnDraggedWeapon(draggedWeapon);
        clearInventoryDrag();
    }

    private void returnDraggedWeapon(WeaponType weapon) {
        if (weapon == null) {
            return;
        }
        if (inventoryDragSource == InventoryDragSource.HAND) {
            inventorySystem.equipWeapon(inventoryDragSlot, weapon);
        } else if (inventoryDragSource == InventoryDragSource.BAG) {
            inventorySystem.putBagWeapon(inventoryDragSlot, weapon);
        } else {
            inventorySystem.addWeaponToBag(weapon);
        }
    }

    private void cancelInventoryDrag() {
        if (draggedWeapon != null) {
            returnDraggedWeapon(draggedWeapon);
            clearInventoryDrag();
        }
    }

    private void clearInventoryDrag() {
        draggedWeapon = null;
        inventoryDragSource = InventoryDragSource.NONE;
        inventoryDragSlot = -1;
    }

    private void cancelKnifeSwing() {
        knifeSwingTimer = 0f;
        knifeDamageApplied = false;
    }

    private int getHandSlotAt(float x, float y) {
        for (int i = 0; i < INVENTORY_EQUIPPED_WEAPON_SLOT_COUNT; i++) {
            if (getHandSlotBounds(i).contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

    private int getBagSlotAt(float x, float y) {
        for (int i = 0; i < InventorySystem.BAG_SLOT_COUNT; i++) {
            if (getBagSlotBounds(i).contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

    private Rectangle getHandSlotBounds(int slot) {
        float panelX = getInventoryPanelX();
        float panelY = getInventoryPanelY();
        float scale = getInventoryPanelScale();
        switch (slot) {
            case 0:
                return new Rectangle(panelX + 288f * scale, panelY + 494f * scale, INVENTORY_WEAPON_SLOT_WIDTH * scale, INVENTORY_WEAPON_SLOT_HEIGHT * scale);
            case 1:
                return new Rectangle(panelX + 415f * scale, panelY + 494f * scale, INVENTORY_WEAPON_SLOT_WIDTH * scale, INVENTORY_WEAPON_SLOT_HEIGHT * scale);
            case 2:
                return new Rectangle(panelX + 269f * scale, panelY + 341f * scale, INVENTORY_UTILITY_SLOT_SIZE * scale, INVENTORY_UTILITY_SLOT_SIZE * scale);
            case 3:
            default:
                return new Rectangle(panelX + 362f * scale, panelY + 341f * scale, INVENTORY_UTILITY_SLOT_SIZE * scale, INVENTORY_UTILITY_SLOT_SIZE * scale);
        }
    }

    private Rectangle getBagSlotBounds(int slot) {
        float panelX = getInventoryPanelX();
        float panelY = getInventoryPanelY();
        float scale = getInventoryPanelScale();
        int col = slot % INVENTORY_BAG_COLUMNS;
        int row = slot / INVENTORY_BAG_COLUMNS;
        return new Rectangle(
                panelX + (INVENTORY_BAG_START_X + col * (INVENTORY_BAG_SLOT_WIDTH + INVENTORY_BAG_SLOT_GAP_X)) * scale,
                panelY + (INVENTORY_BAG_TOP_ROW_BOTTOM_Y - row * (INVENTORY_BAG_SLOT_HEIGHT + INVENTORY_BAG_SLOT_GAP_Y)) * scale,
                INVENTORY_BAG_SLOT_WIDTH * scale,
                INVENTORY_BAG_SLOT_HEIGHT * scale
        );
    }

    private float getInventoryPanelX() {
        return uiCamera.viewportWidth / 2f - INVENTORY_PANEL_WIDTH * getInventoryPanelScale() / 2f;
    }

    private float getInventoryPanelY() {
        return uiCamera.viewportHeight / 2f - INVENTORY_PANEL_HEIGHT * getInventoryPanelScale() / 2f;
    }

    private float getInventoryPanelScale() {
        return 1f;
    }

    private void updateDoorLock() {
        if (!doorLockedUntilPlayerMoves || dialogueVisible || prologueActive) {
            return;
        }
        if (!isNear(player.getBounds(), getActiveDoor(), 95f)) {
            doorLockedUntilPlayerMoves = false;
        }
    }

    public void saveToSlot(int slot) {
        if (!isValidSaveSlot(slot)) {
            return;
        }

        Preferences preferences = Gdx.app.getPreferences(SAVE_PREFERENCES);
        String prefix = getSavePrefix(slot);
        preferences.putBoolean(prefix + "exists", true);
        preferences.putInteger(prefix + "locationIndex", locationIndex);
        preferences.putFloat(prefix + "playerX", player.getX());
        preferences.putFloat(prefix + "playerY", player.getY());
        preferences.putInteger(prefix + "health", player.getHealth());
        preferences.putBoolean(prefix + "knifeCollected", knifeCollected);
        preferences.putBoolean(prefix + "watchedBedroomTv", watchedBedroomTv);
        preferences.putBoolean(prefix + "shantyCleared", shantyCleared);
        preferences.putBoolean(prefix + "chapelIntroSeen", chapelIntroSeen);
        preferences.putBoolean(prefix + "engineIntroSeen", engineIntroSeen);
        preferences.putBoolean(prefix + "prologueActive", prologueActive);
        preferences.putBoolean(prefix + "dialogueVisible", dialogueVisible);
        preferences.putInteger(prefix + "dialogueLine", dialogueLine);
        preferences.putString(prefix + "dialogueKey", getCurrentDialogueKey());
        preferences.flush();
    }

    private void loadFromSlot(int slot) {
        Preferences preferences = Gdx.app.getPreferences(SAVE_PREFERENCES);
        String prefix = getSavePrefix(slot);

        locationIndex = preferences.getInteger(prefix + "locationIndex", 1);
        gameMap = baseMap;
        player.setX(preferences.getFloat(prefix + "playerX", 140f));
        player.setY(preferences.getFloat(prefix + "playerY", Constants.GROUND_Y + Constants.GROUND_HEIGHT));
        player.setVelocityY(0f);
        player.healToFull();
        int savedHealth = MathUtils.clamp(preferences.getInteger(prefix + "health", Constants.PLAYER_MAX_HEALTH), 1, Constants.PLAYER_MAX_HEALTH);
        player.takeDamage(Constants.PLAYER_MAX_HEALTH - savedHealth);

        knifeCollected = preferences.getBoolean(prefix + "knifeCollected", false);
        watchedBedroomTv = preferences.getBoolean(prefix + "watchedBedroomTv", false);
        shantyCleared = preferences.getBoolean(prefix + "shantyCleared", false);
        tvScreenVisible = false;
        tvWatchTimer = watchedBedroomTv ? TV_REQUIRED_WATCH_TIME : 0f;
        chapelIntroSeen = preferences.getBoolean(prefix + "chapelIntroSeen", false);
        engineIntroSeen = preferences.getBoolean(prefix + "engineIntroSeen", false);
        if (knifeCollected) {
            inventorySystem.equipWeapon(0, WeaponType.BASIC_KNIFE);
            inventorySystem.selectWeaponSlot(0);
        }
        prologueActive = preferences.getBoolean(prefix + "prologueActive", false);
        dialogueVisible = preferences.getBoolean(prefix + "dialogueVisible", false);
        dialogueLine = preferences.getInteger(prefix + "dialogueLine", 0);
        setCurrentDialogue(preferences.getString(prefix + "dialogueKey", "chapel"));
        if (dialogueLine < 0 || dialogueLine >= currentText.length) {
            dialogueLine = 0;
        }

        pauseMenuVisible = false;
        pausePanel = PausePanel.NONE;
        inventoryVisible = false;
        knifeSwingTimer = 0f;
        knifeDamageApplied = false;
        floatingDamageTexts.clear();
        if (locationIndex == ShantyLocation.INDEX) {
            prepareShantyEncounter(false);
        } else {
            dungeonEnemies.clear();
            enemyProjectiles.clear();
        }
        if (locationIndex == BossLocation.INDEX) {
            ensureFinalBossSpawned();
        }
        camera.position.x = player.getX();
        camera.update();
        clampCameraToCurrentLocation();
    }

    public static boolean hasSave(int slot) {
        if (!isValidSaveSlot(slot)) {
            return false;
        }
        return Gdx.app.getPreferences(SAVE_PREFERENCES).getBoolean(getSavePrefix(slot) + "exists", false);
    }

    public static String getSaveSummary(int slot) {
        if (!hasSave(slot)) {
            return "\u041f\u0443\u0441\u0442\u043e";
        }

        Preferences preferences = Gdx.app.getPreferences(SAVE_PREFERENCES);
        String prefix = getSavePrefix(slot);
        int location = preferences.getInteger(prefix + "locationIndex", 0);
        String place = LocationId.fromIndex(location).getDisplayName();
        int health = preferences.getInteger(prefix + "health", Constants.PLAYER_MAX_HEALTH);
        return place + " | HP " + health + "/" + Constants.PLAYER_MAX_HEALTH;
    }

    public static int getSaveSlotCount() {
        return SAVE_SLOT_COUNT;
    }

    private static boolean isValidSaveSlot(int slot) {
        return slot >= 0 && slot < SAVE_SLOT_COUNT;
    }

    private static String getSavePrefix(int slot) {
        return "slot" + slot + ".";
    }

    private String getCurrentDialogueKey() {
        if (currentText == DialogueScripts.PROLOGUE_TEXT) {
            return "prologue";
        }
        return "chapel";
    }

    private void setCurrentDialogue(String dialogueKey) {
        if ("prologue".equals(dialogueKey)) {
            currentSpeakers = DialogueScripts.PROLOGUE_SPEAKERS;
            currentText = DialogueScripts.PROLOGUE_TEXT;
        } else {
            currentSpeakers = DialogueScripts.CHAPEL_SPEAKERS;
            currentText = DialogueScripts.CHAPEL_TEXT;
        }
    }

    private void handleAction() {
        if (tvScreenVisible) {
            if (tvWatchTimer >= TV_REQUIRED_WATCH_TIME) {
                watchedBedroomTv = true;
                tvScreenVisible = false;
            }
            return;
        }

        if (dialogueVisible) {
            if (getVisibleDialogueCharacters() < currentText[dialogueLine].length()) {
                dialogueTypeTimer = currentText[dialogueLine].length() / TYPE_SPEED;
                return;
            }

            dialogueLine++;
            dialogueTypeTimer = 0f;
            if (dialogueLine >= currentText.length) {
                dialogueVisible = false;
                dialogueLine = 0;
                dialogueTypeTimer = 0f;
                if (prologueActive) {
                    prologueActive = false;
                    enterBedroomAfterPrologue();
                }
            }
            return;
        }

        if (locationIndex == 1) {
            if (isNear(player.getBounds(), BEDROOM_DOOR, 70f)) {
                tryChangeLocation();
                return;
            }
            if (isNear(player.getBounds(), BEDROOM_TV, 52f)) {
                tvScreenVisible = true;
                tvWatchTimer = watchedBedroomTv ? TV_REQUIRED_WATCH_TIME : 0f;
                inventoryVisible = false;
            }
            return;
        }

        if (locationIndex == 2 && isNear(player.getBounds(), CHAPEL_BEDROOM_DOOR, 70f)) {
            tryChangeLocation();
            return;
        }

        if (locationIndex == 3 && !knifeCollected && isNear(player.getBounds(), STREET_KNIFE_PICKUP, KNIFE_PICKUP_PROMPT_RANGE)) {
            collectKnife();
            return;
        }

        if (locationIndex == 2 || locationIndex == 3) {
            Rectangle door = getActiveDoor();
            if (locationIndex == 3 ? isNear(player.getBounds(), door, 96f) : isNear(player.getBounds(), door, 70f)) {
                tryChangeLocation();
            }
        }
    }

    private void tryChangeLocation() {
        if (locationIndex == 1 && !watchedBedroomTv) {
            return;
        }
        if (doorLockedUntilPlayerMoves) {
            return;
        }
        changeLocation();
    }

    private void startDialogue(String[] speakers, String[] text) {
        currentSpeakers = speakers;
        currentText = text;
        dialogueVisible = true;
        dialogueLine = 0;
        dialogueTypeTimer = 0f;
    }

    private void enterBedroomAfterPrologue() {
        locationIndex = 1;
        gameMap = baseMap;
        tvScreenVisible = false;
        tvWatchTimer = watchedBedroomTv ? TV_REQUIRED_WATCH_TIME : 0f;
        dialogueVisible = false;
        dialogueLine = 0;
        dialogueTypeTimer = 0f;
        inventoryVisible = false;
        player.setX(BEDROOM_PLAYER_X);
        player.setY(getCurrentGroundTop());
        player.setVelocityY(0f);
        doorLockedUntilPlayerMoves = true;
        camera.position.x = player.getX();
        camera.update();
        clampCameraToCurrentLocation();
    }

    private void changeLocation() {
        if (locationIndex == 1) {
            locationIndex = 2;
            player.setX(HALLWAY_PLAYER_EXIT_X);
        } else if (locationIndex == 2 && isNear(player.getBounds(), CHAPEL_BEDROOM_DOOR, 70f)) {
            locationIndex = 1;
            player.setX(BEDROOM_DOOR.x - 110f);
        } else if (locationIndex == 2) {
            locationIndex = 3;
            player.setX(STREET_PLAYER_EXIT_X);
        } else {
            locationIndex = 2;
            player.setX(CHAPEL_DOOR.x - 110f);
        }

        gameMap = baseMap;
        dialogueVisible = false;
        dialogueTypeTimer = 0f;
        inventoryVisible = false;
        doorLockedUntilPlayerMoves = true;
        if (locationIndex == ShantyLocation.INDEX) {
            spawnDungeonEnemies();
        } else {
            enemyProjectiles.clear();
        }
        player.setY(getCurrentGroundTop());
        player.setVelocityY(0f);
        camera.position.x = player.getX();
        camera.update();
        clampCameraToCurrentLocation();

    }

    private void updateLocationTransitions() {
        float visualMarginX = getPlayerVisualMarginX();
        if (locationIndex == 3 && player.getX() >= STREET_TO_SHANTY_THRESHOLD - visualMarginX) {
            enterShantyLocation();
        } else if (locationIndex == ShantyLocation.INDEX
                && !hasAliveDungeonEnemies()
                && player.getX() <= SHANTY_LEFT_WALL + visualMarginX) {
            returnToStreetFromShanty();
        } else if (locationIndex == ShantyLocation.INDEX
                && !hasAliveDungeonEnemies()
                && player.getX() >= SHANTY_TO_BOSS_THRESHOLD - visualMarginX) {
            enterBossLocation();
        }
    }

    private void updateWorldCameraZoom() {
        float targetZoom = getTargetCameraZoom();
        if (camera.zoom != targetZoom) {
            camera.zoom = targetZoom;
            camera.update();
        }
    }

    private float getTargetCameraZoom() {
        if (locationIndex == ShantyLocation.INDEX) {
            float widthZoom = SHANTY_BACKGROUND_WIDTH / camera.viewportWidth;
            float heightZoom = SHANTY_BACKGROUND_HEIGHT / camera.viewportHeight;
            return Math.min(widthZoom, heightZoom) * SHANTY_CAMERA_PADDING;
        }
        if (locationIndex == BossLocation.INDEX) {
            return BOSS_CAMERA_ZOOM;
        }
        return 1f;
    }

    private void enterShantyLocation() {
        locationIndex = ShantyLocation.INDEX;
        gameMap = baseMap;
        dialogueVisible = false;
        dialogueTypeTimer = 0f;
        inventoryVisible = false;
        doorLockedUntilPlayerMoves = false;
        prepareShantyEncounter(false);
        player.setX(SHANTY_PLAYER_ENTRY_X);
        player.setY(getCurrentGroundTop());
        player.setVelocityY(0f);
        camera.position.x = player.getX();
        camera.update();
        clampCameraToCurrentLocation();
    }

    private void returnToStreetFromShanty() {
        locationIndex = 3;
        gameMap = baseMap;
        dialogueVisible = false;
        dialogueTypeTimer = 0f;
        inventoryVisible = false;
        doorLockedUntilPlayerMoves = false;
        dungeonEnemies.clear();
        enemyProjectiles.clear();
        player.setX(SHANTY_PLAYER_RETURN_X);
        player.setY(getCurrentGroundTop());
        player.setVelocityY(0f);
        camera.position.x = player.getX();
        camera.update();
        clampCameraToCurrentLocation();
    }

    private void enterBossLocation() {
        locationIndex = BossLocation.INDEX;
        gameMap = baseMap;
        dialogueVisible = false;
        dialogueTypeTimer = 0f;
        inventoryVisible = false;
        doorLockedUntilPlayerMoves = false;
        dungeonEnemies.clear();
        enemyProjectiles.clear();
        player.setX(BOSS_PLAYER_ENTRY_X);
        player.setY(getCurrentGroundTop());
        player.setVelocityY(0f);
        ensureFinalBossSpawned();
        camera.position.x = player.getX();
        camera.update();
        clampCameraToCurrentLocation();
    }

    private void returnToShantyFromBoss() {
        locationIndex = ShantyLocation.INDEX;
        gameMap = baseMap;
        dialogueVisible = false;
        dialogueTypeTimer = 0f;
        inventoryVisible = false;
        doorLockedUntilPlayerMoves = false;
        spawnDungeonEnemies();
        player.setX(SHANTY_PLAYER_BOSS_RETURN_X);
        player.setY(getCurrentGroundTop());
        player.setVelocityY(0f);
        camera.position.x = player.getX();
        camera.update();
        clampCameraToCurrentLocation();
    }

    private void updateCurrentLocationState(float delta) {
        if (locationIndex == ShantyLocation.INDEX) {
            updateDungeonEnemies(delta);
            updateEnemyProjectiles(delta);
            if (!player.isAlive()) {
                respawnAtCurrentDoor();
            }
            return;
        }

        if (locationIndex != BossLocation.INDEX) {
            enemyProjectiles.clear();
            return;
        }

        enemyProjectiles.clear();
        ensureFinalBossSpawned();
        finalBoss.update(delta, player, BOSS_ARENA_MIN_X, BOSS_ARENA_MAX_X, getCurrentGroundTop());
        if (!player.isAlive()) {
            respawnAtBossEntrance();
        }
    }

    private void ensureFinalBossSpawned() {
        if (finalBoss == null) {
            // The boss is spawned lazily when the fourth location becomes active.
            finalBoss = new FinalBoss(FINAL_BOSS_SPAWN_X, FINAL_BOSS_SPAWN_Y);
        }
    }

    private void respawnAtBossEntrance() {
        player.healToFull();
        player.setX(BOSS_PLAYER_ENTRY_X + 40f);
        player.setY(getCurrentGroundTop());
        player.setVelocityY(0f);
        knifeSwingTimer = 0f;
        knifeDamageApplied = false;
        floatingDamageTexts.clear();
        ensureFinalBossSpawned();
        finalBoss.reset(FINAL_BOSS_SPAWN_X, FINAL_BOSS_SPAWN_Y);
        camera.position.x = player.getX();
        camera.update();
        clampCameraToCurrentLocation();
    }

    private Rectangle getActiveDoor() {
        if (locationIndex == 1) {
            return BEDROOM_DOOR;
        }
        if (locationIndex == 2) {
            return getNearestDoor(CHAPEL_BEDROOM_DOOR, CHAPEL_DOOR);
        }
        return STREET_RETURN_DOOR;
    }

    private Rectangle getNearestDoor(Rectangle firstDoor, Rectangle secondDoor) {
        float playerCenter = player.getBounds().x + player.getBounds().width * 0.5f;
        float firstCenter = firstDoor.x + firstDoor.width * 0.5f;
        float secondCenter = secondDoor.x + secondDoor.width * 0.5f;
        return Math.abs(playerCenter - firstCenter) <= Math.abs(playerCenter - secondCenter) ? firstDoor : secondDoor;
    }

    private float getLocationPlayerScale() {
        if (locationIndex == 1) {
            return BEDROOM_PLAYER_SCALE;
        }
        if (locationIndex == 2) {
            return HALLWAY_PLAYER_SCALE;
        }
        if (locationIndex == 3) {
            return STREET_PLAYER_SCALE;
        }
        if (locationIndex == ShantyLocation.INDEX) {
            return SHANTY_PLAYER_SCALE;
        }
        if (locationIndex == BossLocation.INDEX) {
            return BOSS_PLAYER_SCALE;
        }
        return 1f;
    }

    private float getPlayerVisualMarginX() {
        float drawWidth = Math.max(HERO_BODY_DRAW_WIDTH, HERO_ATTACK_DRAW_WIDTH) * getLocationPlayerScale();
        return Math.max(0f, drawWidth * 0.5f - Constants.PLAYER_WIDTH * 0.5f);
    }

    private float getCurrentGroundTop() {
        if (locationIndex == 3) {
            return STREET_GROUND_TOP;
        }
        if (locationIndex == ShantyLocation.INDEX) {
            return SHANTY_GROUND_TOP;
        }
        if (locationIndex == BossLocation.INDEX) {
            return BOSS_ROOM_GROUND_TOP;
        }
        return gameMap.getGround().y + gameMap.getGround().height;
    }

    private void clampCameraToCurrentLocation() {
        float halfWidth = camera.viewportWidth * camera.zoom * 0.5f;
        float halfHeight = camera.viewportHeight * camera.zoom * 0.5f;

        if (locationIndex == 1) {
            camera.position.x = BEDROOM_RIGHT_WALL * 0.5f;
        } else if (locationIndex == 2) {
            float minX = HALLWAY_LEFT_WALL + halfWidth;
            float maxX = HALLWAY_RIGHT_WALL - halfWidth;
            camera.position.x = MathUtils.clamp(camera.position.x, minX, Math.max(minX, maxX));
        } else if (locationIndex == 3) {
            float minX = STREET_LEFT_WALL + halfWidth;
            float maxX = STREET_RIGHT_WALL - halfWidth;
            camera.position.x = MathUtils.clamp(camera.position.x, minX, Math.max(minX, maxX));
        } else if (locationIndex == ShantyLocation.INDEX) {
            float minX = SHANTY_BACKGROUND_X + halfWidth;
            float maxX = SHANTY_BACKGROUND_X + SHANTY_BACKGROUND_WIDTH - halfWidth;
            float minY = SHANTY_BACKGROUND_Y + halfHeight;
            float maxY = SHANTY_BACKGROUND_Y + SHANTY_BACKGROUND_HEIGHT - halfHeight;
            camera.position.x = MathUtils.clamp(camera.position.x, minX, Math.max(minX, maxX));
            camera.position.y = MathUtils.clamp(camera.position.y, minY, Math.max(minY, maxY));
            camera.update();
            return;
        } else if (locationIndex == BossLocation.INDEX) {
            float minX = BOSS_LEFT_WALL + halfWidth;
            float maxX = BOSS_RIGHT_WALL - halfWidth;
            camera.position.x = MathUtils.clamp(camera.position.x, minX, Math.max(minX, maxX));
        } else {
            camera.position.x = MathUtils.clamp(camera.position.x, halfWidth, Constants.WORLD_WIDTH - halfWidth);
        }

        camera.position.y = MathUtils.clamp(camera.position.y, halfHeight, Constants.WORLD_HEIGHT - halfHeight);
        camera.update();
    }

    private void resolveWorldCollisions() {
        boolean landed = false;
        float groundTop = getCurrentGroundTop();
        float ceilingY = Constants.WORLD_HEIGHT - Constants.PLAYER_HEIGHT - 6f;
        if (player.getBounds().y <= groundTop) {
            player.setY(groundTop);
            player.setVelocityY(0);
            landed = true;
        }

        if (player.getBounds().y >= ceilingY) {
            player.setY(ceilingY);
            if (player.getVelocityY() > 0f) {
                player.setVelocityY(0f);
            }
        }

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
        float visualMarginX = getPlayerVisualMarginX();
        if (locationIndex == 1 && player.getX() < BEDROOM_LEFT_WALL + visualMarginX) {
            player.setX(BEDROOM_LEFT_WALL + visualMarginX);
            return;
        }
        if (locationIndex == 2 && player.getX() < HALLWAY_LEFT_WALL + visualMarginX) {
            player.setX(HALLWAY_LEFT_WALL + visualMarginX);
            return;
        }
        if (locationIndex == 3 && player.getX() < STREET_LEFT_WALL + visualMarginX) {
            player.setX(STREET_LEFT_WALL + visualMarginX);
            return;
        }
        if (locationIndex == ShantyLocation.INDEX && player.getX() < SHANTY_LEFT_WALL + visualMarginX) {
            player.setX(SHANTY_LEFT_WALL + visualMarginX);
            return;
        }
        if (locationIndex == BossLocation.INDEX && player.getX() < BOSS_LEFT_WALL + visualMarginX) {
            player.setX(BOSS_LEFT_WALL + visualMarginX);
            return;
        }
        if (player.getX() < 0) player.setX(0);
        if (locationIndex == 1 && player.getX() > BEDROOM_COLLISION_RIGHT_WALL - Constants.PLAYER_WIDTH - visualMarginX) {
            player.setX(BEDROOM_COLLISION_RIGHT_WALL - Constants.PLAYER_WIDTH - visualMarginX);
            return;
        }
        if (locationIndex == 2 && player.getX() > HALLWAY_RIGHT_WALL - Constants.PLAYER_WIDTH - visualMarginX) {
            player.setX(HALLWAY_RIGHT_WALL - Constants.PLAYER_WIDTH - visualMarginX);
            return;
        }
        if (locationIndex == 3 && player.getX() > STREET_RIGHT_WALL - Constants.PLAYER_WIDTH - visualMarginX) {
            player.setX(STREET_RIGHT_WALL - Constants.PLAYER_WIDTH - visualMarginX);
            return;
        }
        if (locationIndex == ShantyLocation.INDEX && player.getX() > SHANTY_RIGHT_WALL - Constants.PLAYER_WIDTH - visualMarginX) {
            player.setX(SHANTY_RIGHT_WALL - Constants.PLAYER_WIDTH - visualMarginX);
            return;
        }
        if (locationIndex == BossLocation.INDEX && player.getX() > BOSS_RIGHT_WALL - Constants.PLAYER_WIDTH - visualMarginX) {
            player.setX(BOSS_RIGHT_WALL - Constants.PLAYER_WIDTH - visualMarginX);
            return;
        }
        if (player.getX() > Constants.WORLD_WIDTH - Constants.PLAYER_WIDTH) {
            player.setX(Constants.WORLD_WIDTH - Constants.PLAYER_WIDTH);
        }
    }

    private void renderWorld() {
        if (locationIndex == 1) {
            renderBedroom();
        } else if (locationIndex == 2) {
            renderRuinedChapelStation();
        } else if (locationIndex == 3) {
            renderStreetExterior();
            renderStreetKnifePickup();
        } else if (locationIndex == ShantyLocation.INDEX) {
            renderShantyCity();
        } else {
            renderBossLaboratory();
        }
    }

    private void renderWorldActors() {
        // Draw shape-based actors after textured backgrounds so they are not hidden behind them.
        if (locationIndex == ShantyLocation.INDEX) {
            renderDungeonEnemies();
            renderEnemyProjectileGlow();
        }
        if (locationIndex == BossLocation.INDEX && finalBoss != null) {
            finalBoss.render(shapeRenderer, time);
        }
        // Keep slash effects in the actor pass so left/right/up/down swings stay visible.
        renderKnifeSlash();
        renderParryEffect();
    }

    private void checkTrashPileDiscovery() {
        // Knife pickup now lives on the street near the bus stop.
    }

    private void collectKnife() {
        knifeCollected = true;
        inventorySystem.equipWeapon(0, WeaponType.BASIC_KNIFE);
        inventorySystem.selectWeaponSlot(0);
    }

    private void startKnifeAttack() {
        WeaponType weapon = inventorySystem.getSelectedWeapon();
        if (knifeSwingTimer > 0f || weapon == null) {
            return;
        }
        attackDirection = getRequestedAttackDirection();
        knifeSwingTimer = weapon.getSwingDuration();
        knifeDamageApplied = false;
    }

    private AttackDirection getRequestedAttackDirection() {
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            return AttackDirection.UP;
        }
        // Down slash is reserved for aerial attacks so it cannot be triggered from the ground.
        if (!player.isOnGround() && (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))) {
            return AttackDirection.DOWN;
        }
        return AttackDirection.FORWARD;
    }

    private Rectangle getKnifeAttackBounds() {
        WeaponType weapon = inventorySystem.getSelectedWeapon();
        float range = weapon == null ? 68f : weapon.getRange() + 18f;
        Rectangle playerBounds = player.getBounds();
        if (attackDirection == AttackDirection.UP) {
            float width = playerBounds.width + 42f;
            float x = playerBounds.x - 6f + (player.isFacingRight() ? 18f : -18f);
            return new Rectangle(x, playerBounds.y + playerBounds.height + 4f,
                    width, range + 40f);
        }
        if (attackDirection == AttackDirection.DOWN) {
            float width = playerBounds.width + 52f;
            float x = playerBounds.x - 20f + (player.isFacingRight() ? 10f : -10f);
            return new Rectangle(x, playerBounds.y - range - 18f,
                    width, range + 42f);
        }

        float width = range + KNIFE_FORWARD_RANGE_BONUS;
        float height = 54f;
        float x = player.isFacingRight()
                ? playerBounds.x + playerBounds.width - 2f
                : playerBounds.x - width + 2f;
        float y = playerBounds.y + 12f;
        return new Rectangle(x, y, width, height);
    }

    private void updateKnifeAttack() {
        if (knifeSwingTimer <= 0f || knifeDamageApplied) {
            return;
        }

        WeaponType weapon = inventorySystem.getSelectedWeapon();
        if (weapon == null) {
            return;
        }
        Rectangle attackBounds = getKnifeAttackBounds();
        for (int i = 0; i < dungeonEnemies.size; i++) {
            Enemy enemy = dungeonEnemies.get(i);
            if (enemy.isAlive() && attackBounds.overlaps(enemy.getBounds())) {
                int damage = Math.max(KNIFE_DAMAGE, weapon.getDamage());
                enemy.takeDamage(damage);
                player.heal(HIT_HEAL_AMOUNT);
                spawnDamageText(enemy.getBounds().x + enemy.getBounds().width * 0.5f,
                        enemy.getBounds().y + enemy.getBounds().height * 0.76f,
                        damage, false);
                rewardEnemyKill(enemy);
                if (attackDirection == AttackDirection.DOWN) {
                    player.bounceFromDownAttack(DOWN_ATTACK_BOUNCE);
                }
                knifeDamageApplied = true;
                return;
            }
        }

        if (locationIndex == BossLocation.INDEX && finalBoss != null && finalBoss.isAlive() && attackBounds.overlaps(finalBoss.getBounds())) {
            Rectangle bossHeadBounds = finalBoss.getHeadBounds();
            boolean attackingFromAbove = player.getBounds().y >= bossHeadBounds.y - 8f;
            // Any top-side attack pressure is absorbed by the head and never converts into damage.
            if (attackingFromAbove || ((attackDirection == AttackDirection.UP || attackDirection == AttackDirection.DOWN) && attackBounds.overlaps(bossHeadBounds))) {
                finalBoss.absorbHeadPressure(player, weapon.getSwingDuration(), BOSS_LEFT_WALL, BOSS_RIGHT_WALL);
                if (attackDirection == AttackDirection.DOWN) {
                    player.bounceFromDownAttack(DOWN_ATTACK_BOUNCE);
                }
                knifeDamageApplied = true;
                return;
            }
            if (attackDirection == AttackDirection.UP) {
                finalBoss.absorbHeadPressure(player, weapon.getSwingDuration(), BOSS_LEFT_WALL, BOSS_RIGHT_WALL);
                knifeDamageApplied = true;
                return;
            }
            int damage = Math.max(KNIFE_DAMAGE, weapon.getDamage());
            finalBoss.takeDamage(damage);
            player.heal(HIT_HEAL_AMOUNT);
            spawnDamageText(finalBoss.getBounds().x + finalBoss.getBounds().width * 0.5f,
                    finalBoss.getBounds().y + finalBoss.getBounds().height * 0.82f,
                    damage, true);
            if (!finalBoss.isAlive()) {
                player.heal(KILL_HEAL_AMOUNT);
            }
            if (attackDirection == AttackDirection.DOWN) {
                player.bounceFromDownAttack(DOWN_ATTACK_BOUNCE);
            }
            knifeDamageApplied = true;
        }
    }

    private void rewardEnemyKill(Enemy enemy) {
        if (!enemy.isAlive() && !enemy.isLootDropped()) {
            player.heal(KILL_HEAL_AMOUNT);
            enemy.markLootDropped();
            if (locationIndex == ShantyLocation.INDEX && !hasAliveDungeonEnemies()) {
                shantyCleared = true;
                enemyProjectiles.clear();
            }
        }
    }

    private boolean hasAliveDungeonEnemies() {
        for (Enemy enemy : dungeonEnemies) {
            if (enemy.isAlive()) {
                return true;
            }
        }
        return false;
    }

    private void prepareShantyEncounter(boolean forceRespawn) {
        if (shantyCleared) {
            dungeonEnemies.clear();
            enemyProjectiles.clear();
            return;
        }

        if (forceRespawn || dungeonEnemies.size == 0) {
            spawnDungeonEnemies();
        }
    }

    private void spawnDamageText(float x, float y, int damage, boolean critical) {
        if (damage <= 0) {
            return;
        }
        floatingDamageTexts.add(new FloatingDamageText(x, y, damage, critical, floatingDamageTexts.size));
    }

    private void updateFloatingDamageTexts(float delta) {
        for (int i = floatingDamageTexts.size - 1; i >= 0; i--) {
            FloatingDamageText text = floatingDamageTexts.get(i);
            text.update(delta);
            if (text.isFinished()) {
                floatingDamageTexts.removeIndex(i);
            }
        }
    }

    private void renderFloatingDamageTexts() {
        if (floatingDamageTexts.size == 0) {
            return;
        }

        float previousScaleX = font.getData().scaleX;
        float previousScaleY = font.getData().scaleY;
        for (FloatingDamageText text : floatingDamageTexts) {
            float alpha = text.getAlpha();
            float scale = text.critical ? 1.02f + text.getPop() * 0.34f : 0.88f + text.getPop() * 0.24f;
            font.getData().setScale(scale);
            font.setColor(0.03f, 0.03f, 0.05f, alpha * 0.95f);
            font.draw(batch, text.value, text.x - 12f * scale + 2f, text.y + 2f);
            if (text.critical) {
                font.setColor(1f, 0.96f, 0.76f, alpha);
            } else {
                font.setColor(0.82f, 0.98f, 1f, alpha);
            }
            font.draw(batch, text.value, text.x - 12f * scale, text.y);
        }
        font.getData().setScale(previousScaleX, previousScaleY);
        font.setColor(1f, 1f, 1f, 1f);
    }

    private void updateDungeonEnemies(float delta) {
        for (int i = 0; i < dungeonEnemies.size; i++) {
            Enemy enemy = dungeonEnemies.get(i);
            if (!enemy.isAlive()) {
                enemy.update(delta);
                continue;
            }

            if (enemy.isFlyingShooter()) {
                updateFlyingEnemyAi(enemy, delta);
            } else if (enemy.isFlyingSlasher()) {
                updateFlyingSlasherAi(enemy, delta);
            } else {
                resolveEnemyGrounding(enemy);
                updateEnemyAi(enemy, delta);
            }
            enemy.update(delta);
        }
    }

    private void updateFlyingEnemyAi(Enemy enemy, float delta) {
        float playerCenterX = player.getBounds().x + player.getBounds().width / 2f;
        float playerCenterY = player.getBounds().y + player.getBounds().height / 2f;
        float enemyCenterX = enemy.getBounds().x + enemy.getBounds().width / 2f;
        float enemyCenterY = enemy.getBounds().y + enemy.getBounds().height / 2f;
        float dx = playerCenterX - enemyCenterX;
        float dy = playerCenterY - enemyCenterY;
        float absDx = Math.abs(dx);
        float absDy = Math.abs(dy);
        boolean canSeePlayer = absDx < enemy.getProjectileRange() && absDy < FLYING_ENEMY_ENGAGE_Y_RANGE;

        if (enemy.shouldPickNewDecision()) {
            float direction = MathUtils.randomBoolean() ? 1f : -1f;
            enemy.pickDecision(MathUtils.random(0.80f, 1.40f), direction);
        }

        if (canSeePlayer) {
            enemy.noticePlayer(delta);
            enemy.face(playerCenterX);
        } else {
            enemy.calmDown(delta);
        }

        float hoverTargetY = enemy.getHoverBaseY()
                + MathUtils.sin(time * 2.3f + enemy.getHomeX() * 0.015f) * enemy.getHoverAmplitude();
        if (canSeePlayer) {
            hoverTargetY += MathUtils.clamp(dy * 0.25f, -44f, 44f);
        }

        float preferredDistance = enemy.getPreferredDistance() + enemy.getAlertness() * 44f;
        if (canSeePlayer) {
            if (absDx > preferredDistance + 34f) {
                enemy.moveToward(playerCenterX, enemy.getSpeed() * (0.60f + enemy.getAlertness() * 0.55f), delta);
            } else if (absDx < preferredDistance - 28f) {
                enemy.moveBy(-Math.signum(dx) * enemy.getSpeed() * 0.58f * delta);
            } else {
                enemy.moveBy(enemy.getStrafeDirection() * enemy.getSpeed() * 0.22f * delta);
            }
        } else {
            float patrolTarget = enemy.getHomeX() + MathUtils.sin(time * 1.4f + enemy.getHomeX() * 0.02f) * 135f;
            enemy.moveToward(patrolTarget, enemy.getSpeed() * 0.40f, delta);
        }

        enemy.moveTowardY(hoverTargetY, FLYING_ENEMY_VERTICAL_SPEED, delta);
        enemy.clampX(STREET_ENEMY_MIN_X, STREET_ENEMY_MAX_X);
        enemy.clampY(Constants.GROUND_Y + 120f, Constants.WORLD_HEIGHT - 160f);
        applyEnemySeparation(enemy, delta);

        if (canSeePlayer && enemy.canAttack() && player.isAlive()) {
            spawnFlyingEnemyProjectile(enemy, playerCenterX, playerCenterY);
            enemy.triggerAttack();
        }
    }

    private void updateFlyingSlasherAi(Enemy enemy, float delta) {
        float playerCenterX = player.getBounds().x + player.getBounds().width / 2f;
        float playerCenterY = player.getBounds().y + player.getBounds().height / 2f;
        float enemyCenterX = enemy.getBounds().x + enemy.getBounds().width / 2f;
        float enemyCenterY = enemy.getBounds().y + enemy.getBounds().height / 2f;
        float dx = playerCenterX - enemyCenterX;
        float dy = playerCenterY - enemyCenterY;
        float absDx = Math.abs(dx);
        float absDy = Math.abs(dy);
        boolean canSeePlayer = absDx < FLYING_SLASHER_ENGAGE_X_RANGE && absDy < FLYING_SLASHER_ENGAGE_Y_RANGE;

        if (enemy.shouldPickNewDecision()) {
            float direction = MathUtils.randomBoolean() ? 1f : -1f;
            enemy.pickDecision(MathUtils.random(0.45f, 0.95f), direction);
        }

        if (canSeePlayer) {
            enemy.noticePlayer(delta * 1.25f);
            enemy.face(playerCenterX);
        } else {
            enemy.calmDown(delta * 0.7f);
        }

        float hoverTargetY = enemy.getHoverBaseY()
                + MathUtils.sin(time * 2.9f + enemy.getHomeX() * 0.014f) * enemy.getHoverAmplitude();
        if (canSeePlayer) {
            hoverTargetY = MathUtils.lerp(hoverTargetY, playerCenterY + 18f, 0.55f);
        }

        if (canSeePlayer) {
            float diveSpeed = enemy.getSpeed() * (0.70f + enemy.getAlertness() * 0.55f);
            if (absDx > enemy.getAttackRange() + 18f) {
                enemy.moveToward(playerCenterX, diveSpeed, delta);
            } else {
                enemy.moveBy(enemy.getStrafeDirection() * enemy.getSpeed() * 0.18f * delta);
            }
        } else {
            float patrolTarget = enemy.getHomeX() + MathUtils.sin(time * 1.6f + enemy.getHomeX() * 0.018f) * 118f;
            enemy.moveToward(patrolTarget, enemy.getSpeed() * 0.34f, delta);
        }

        if (enemy.isAttacking()) {
            enemy.moveToward(playerCenterX, enemy.getSpeed() * 1.15f, delta);
            hoverTargetY = MathUtils.lerp(hoverTargetY, playerCenterY - 6f, 0.7f);
        }

        enemy.moveTowardY(hoverTargetY, FLYING_SLASHER_VERTICAL_SPEED, delta);
        enemy.clampX(STREET_ENEMY_MIN_X, STREET_ENEMY_MAX_X);
        enemy.clampY(Constants.GROUND_Y + 120f, Constants.WORLD_HEIGHT - 150f);
        applyEnemySeparation(enemy, delta);

        boolean inSlashRange = absDx <= enemy.getAttackRange() + 12f && absDy <= 56f;
        if (canSeePlayer && inSlashRange && enemy.canAttack() && player.isAlive()) {
            enemy.triggerAttack();
            enemy.moveToward(playerCenterX, enemy.getSpeed() * 1.35f, delta);
            if (player.registerParrySuccess()) {
                enemy.moveBy(dx > 0f ? -96f : 96f);
            } else {
                player.takeDamage(enemy.getDamage());
            }
        }
    }

    private void updateEnemyAi(Enemy enemy, float delta) {
            float playerCenterX = player.getBounds().x + player.getBounds().width / 2f;
            float enemyCenterX = enemy.getBounds().x + enemy.getBounds().width / 2f;
            float dx = playerCenterX - enemyCenterX;
            float absDx = Math.abs(dx);
            float playerCenterY = player.getBounds().y + player.getBounds().height / 2f;
            float enemyCenterY = enemy.getBounds().y + enemy.getBounds().height / 2f;
            boolean closeEnoughOnY = Math.abs(playerCenterY - enemyCenterY) < 82f;
            boolean canSeePlayer = closeEnoughOnY && absDx < Constants.ENEMY_AGGRO_RANGE + 230f;

            if (enemy.shouldPickNewDecision()) {
                float direction = MathUtils.randomBoolean() ? 1f : -1f;
                enemy.pickDecision(MathUtils.random(0.55f, 1.25f), direction);
            }

            if (canSeePlayer) {
                enemy.noticePlayer(delta);
                enemy.face(playerCenterX);
            } else {
                enemy.calmDown(delta);
            }

            float desiredDistance = enemy.getAttackRange() + 28f + enemy.getAlertness() * 30f;
            if (enemy.getAlertness() > 0.08f && closeEnoughOnY) {
                if (absDx > desiredDistance) {
                    float pressureSpeed = enemy.getSpeed() * (0.55f + enemy.getAlertness() * 0.75f);
                    enemy.moveToward(playerCenterX, pressureSpeed, delta);
                } else if (absDx < desiredDistance * 0.72f) {
                    float backStep = -Math.signum(dx) * enemy.getSpeed() * 0.55f * delta;
                    enemy.moveBy(backStep);
                } else {
                    enemy.moveBy(enemy.getStrafeDirection() * enemy.getSpeed() * 0.22f * delta);
                }
            } else {
                float patrolTarget = enemy.getHomeX() + MathUtils.sin(time * 1.2f + enemy.getHomeX() * 0.02f) * 115f;
                enemy.moveToward(patrolTarget, enemy.getSpeed() * 0.32f, delta);
            }

            applyEnemySeparation(enemy, delta);
            if (locationIndex == ShantyLocation.INDEX) {
                enemy.clampX(SHANTY_LEFT_WALL + 120f, SHANTY_RIGHT_WALL - 120f);
            } else {
                enemy.clampX(STREET_RETURN_DOOR.x + 115f, Constants.WORLD_WIDTH - 120f);
            }

            if (closeEnoughOnY && absDx <= enemy.getAttackRange() + 14f && enemy.canAttack() && player.isAlive()) {
                enemy.triggerAttack();
                float lungeTarget = playerCenterX + (dx > 0f ? -30f : 30f);
                enemy.moveToward(lungeTarget, enemy.getSpeed() * enemy.getLungePower(), delta);
                if (absDx <= enemy.getAttackRange()) {
                    if (player.registerParrySuccess()) {
                        enemy.moveBy(dx > 0f ? -82f : 82f);
                    } else {
                        player.takeDamage(enemy.getDamage());
                    }
                }
            }
    }

    private void applyEnemySeparation(Enemy enemy, float delta) {
        float push = 0f;
        float enemyCenterX = enemy.getBounds().x + enemy.getBounds().width / 2f;
        for (int i = 0; i < dungeonEnemies.size; i++) {
            Enemy other = dungeonEnemies.get(i);
            if (other == enemy || !other.isAlive()) {
                continue;
            }

            float otherCenterX = other.getBounds().x + other.getBounds().width / 2f;
            float distance = enemyCenterX - otherCenterX;
            float absDistance = Math.abs(distance);
            float minimumDistance = enemy.isFlyingShooter() || other.isFlyingShooter()
                    || enemy.isFlyingSlasher() || other.isFlyingSlasher()
                    ? 90f : 58f;
            if (absDistance > 0f && absDistance < minimumDistance) {
                push += Math.signum(distance) * (minimumDistance - absDistance);
            }
        }

        if (push != 0f) {
            enemy.moveBy(MathUtils.clamp(push, -70f, 70f) * delta * 4.2f);
        }
    }

    private void spawnFlyingEnemyProjectile(Enemy enemy, float targetX, float targetY) {
        float spawnX = enemy.getBounds().x + enemy.getBounds().width * 0.5f;
        float spawnY = enemy.getBounds().y + enemy.getBounds().height * 0.5f;
        float dx = targetX - spawnX;
        float dy = targetY - spawnY;
        float distance = Math.max(1f, (float)Math.sqrt(dx * dx + dy * dy));
        float velocity = enemy.getProjectileSpeed();
        enemyProjectiles.add(new EnemyProjectile(
                spawnX,
                spawnY,
                dx / distance * velocity,
                dy / distance * velocity,
                enemy.getDamage()
        ));
    }

    private void updateEnemyProjectiles(float delta) {
        for (int i = enemyProjectiles.size - 1; i >= 0; i--) {
            EnemyProjectile projectile = enemyProjectiles.get(i);
            projectile.update(delta);

            if (projectile.reflected) {
                boolean hitEnemy = false;
                for (Enemy enemy : dungeonEnemies) {
                    if (enemy.isAlive() && projectile.bounds.overlaps(enemy.getBounds())) {
                        int damage = Math.max(4, projectile.damage);
                        enemy.takeDamage(damage);
                        spawnDamageText(enemy.getBounds().x + enemy.getBounds().width * 0.5f,
                                enemy.getBounds().y + enemy.getBounds().height * 0.74f,
                                damage, false);
                        rewardEnemyKill(enemy);
                        enemyProjectiles.removeIndex(i);
                        hitEnemy = true;
                        break;
                    }
                }
                if (hitEnemy) {
                    continue;
                }
            }

            if (projectile.bounds.overlaps(player.getBounds()) && player.isAlive()) {
                if (!projectile.reflected && player.registerParrySuccess()) {
                    reflectProjectile(projectile);
                    continue;
                }
                player.takeDamage(projectile.damage);
                enemyProjectiles.removeIndex(i);
                continue;
            }

            if (projectile.lifeTimer <= 0f
                    || projectile.bounds.x < STREET_ENEMY_MIN_X - 120f
                    || projectile.bounds.x > Constants.WORLD_WIDTH + 120f
                    || projectile.bounds.y < Constants.GROUND_Y - 40f
                    || projectile.bounds.y > Constants.WORLD_HEIGHT + 40f) {
                enemyProjectiles.removeIndex(i);
            }
        }
    }

    private void reflectProjectile(EnemyProjectile projectile) {
        float targetX = findNearestLivingEnemyCenterX(projectile.bounds.x + projectile.bounds.width * 0.5f);
        float centerX = projectile.bounds.x + projectile.bounds.width * 0.5f;
        float centerY = projectile.bounds.y + projectile.bounds.height * 0.5f;
        float dx = targetX - centerX;
        float dy = Constants.GROUND_Y + Constants.GROUND_HEIGHT + 270f - centerY;
        float distance = Math.max(1f, (float)Math.sqrt(dx * dx + dy * dy));
        float speed = Math.max(420f, (float)Math.sqrt(projectile.velocityX * projectile.velocityX + projectile.velocityY * projectile.velocityY) * 1.35f);
        projectile.velocityX = dx / distance * speed;
        projectile.velocityY = dy / distance * speed;
        projectile.rotation = (float)Math.toDegrees(Math.atan2(projectile.velocityY, projectile.velocityX));
        projectile.reflected = true;
        projectile.lifeTimer = FLYING_ENEMY_PROJECTILE_LIFETIME;
    }

    private float findNearestLivingEnemyCenterX(float x) {
        float nearestX = player.isFacingRight() ? x + 480f : x - 480f;
        float nearestDistance = Float.MAX_VALUE;
        for (Enemy enemy : dungeonEnemies) {
            if (!enemy.isAlive()) {
                continue;
            }
            float enemyCenterX = enemy.getBounds().x + enemy.getBounds().width * 0.5f;
            float distance = Math.abs(enemyCenterX - x);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestX = enemyCenterX;
            }
        }
        return nearestX;
    }

    private void resolveEnemyGrounding(Enemy enemy) {
        enemy.setY(Constants.GROUND_Y + Constants.GROUND_HEIGHT);
    }

    private void spawnDungeonEnemies() {
        float groundY = Constants.GROUND_Y + Constants.GROUND_HEIGHT;
        dungeonEnemies.clear();
        enemyProjectiles.clear();
        dungeonEnemies.add(Enemy.createFlyingShooter(980f, groundY + 240f + SHANTY_ENEMY_VERTICAL_OFFSET));
        dungeonEnemies.add(Enemy.createFlyingSlasher(1110f, groundY + 206f + SHANTY_ENEMY_VERTICAL_OFFSET));
        dungeonEnemies.add(Enemy.createFlyingShooter(1280f, groundY + 280f + SHANTY_ENEMY_VERTICAL_OFFSET));
        dungeonEnemies.add(Enemy.createFlyingShooter(1560f, groundY + 300f + SHANTY_ENEMY_VERTICAL_OFFSET));
        dungeonEnemies.add(Enemy.createFlyingSlasher(1960f, groundY + 224f + SHANTY_ENEMY_VERTICAL_OFFSET));
        dungeonEnemies.add(Enemy.createFlyingShooter(2260f, groundY + 260f + SHANTY_ENEMY_VERTICAL_OFFSET));
        dungeonEnemies.add(Enemy.createFlyingShooter(2620f, groundY + 320f + SHANTY_ENEMY_VERTICAL_OFFSET));
    }

    private void respawnAtCurrentDoor() {
        player.healToFull();
        if (locationIndex == ShantyLocation.INDEX) {
            player.setX(SHANTY_PLAYER_ENTRY_X);
        } else {
            player.setX(STREET_RETURN_DOOR.x + RESPAWN_X_OFFSET);
        }
        player.setY(getCurrentGroundTop());
        player.setVelocityY(0f);
        knifeSwingTimer = 0f;
        knifeDamageApplied = false;
        floatingDamageTexts.clear();
        enemyProjectiles.clear();
        prepareShantyEncounter(true);
        camera.position.x = player.getX();
        camera.update();
    }

    private void renderStreetKnifePickup() {
        if (knifeCollected) {
            return;
        }

        float pulse = 0.5f + 0.5f * MathUtils.sin(time * 4.6f);
        float x = STREET_KNIFE_PICKUP.x + 26f;
        float y = STREET_GROUND_TOP + 8f;
        shapeRenderer.setColor(0.010f, 0.012f, 0.014f, 0.82f);
        shapeRenderer.ellipse(x - 12f, y - 4f, 62f, 16f);
        shapeRenderer.setColor(0.10f, 0.12f, 0.14f, 1f);
        shapeRenderer.rectLine(x, y + 10f, x + 34f, y + 28f, 6f);
        shapeRenderer.setColor(0.72f, 0.80f, 0.90f, 1f);
        shapeRenderer.rectLine(x + 14f, y + 8f, x + 50f, y + 40f, 4f);
        shapeRenderer.setColor(0.90f, 0.96f, 1f, 0.72f + pulse * 0.18f);
        shapeRenderer.rectLine(x + 24f, y + 19f, x + 48f, y + 37f, 1.8f);
        shapeRenderer.setColor(0.18f, 0.82f, 0.96f, 0.18f + pulse * 0.14f);
        shapeRenderer.circle(x + 48f, y + 38f, 8f + pulse * 4f, 20);
    }

    private void renderDungeonEnemies() {
        for (Enemy enemy : dungeonEnemies) {
            if (!enemy.isAlive() || enemy.isFlyingShooter() || enemy.isFlyingSlasher()) {
                continue;
            }
            renderHumanEnemy(enemy);
        }
    }

    private void renderDungeonEnemySprites() {
        if (locationIndex != ShantyLocation.INDEX) {
            return;
        }

        for (Enemy enemy : dungeonEnemies) {
            if (!enemy.isAlive()) {
                continue;
            }

            float hitFlash = enemy.isRecentlyDamaged()
                    ? MathUtils.clamp(enemy.getDamageFlashTimer() / 0.22f, 0f, 1f)
                    : 0f;
            if (enemy.isFlyingShooter() && flyingEnemyFrames != null) {
                int frameIndex = ((int)(time * FLYING_ENEMY_FLAP_SPEED + enemy.getHomeX() * 0.01f)) % flyingEnemyFrames.length;
                TextureRegion frame = flyingEnemyFrames[frameIndex];
                float pulse = 0.94f + 0.06f * MathUtils.sin(time * 7f + enemy.getHomeX() * 0.02f);
                float drawWidth = FLYING_ENEMY_DRAW_WIDTH * (0.98f + 0.03f * MathUtils.sin(time * 6f + enemy.getX() * 0.02f));
                float drawHeight = FLYING_ENEMY_DRAW_HEIGHT * pulse;
                float drawX = enemy.getBounds().x + enemy.getBounds().width * 0.5f - drawWidth * 0.5f;
                float drawY = enemy.getBounds().y + enemy.getBounds().height * 0.5f - drawHeight * 0.5f + 6f;
                batch.setColor(1f, 1f - hitFlash * 0.45f, 1f - hitFlash * 0.55f, 1f);
                batch.draw(frame.getTexture(),
                        drawX,
                        drawY,
                        drawWidth,
                        drawHeight,
                        frame.getRegionX(),
                        frame.getRegionY(),
                        frame.getRegionWidth(),
                        frame.getRegionHeight(),
                        !enemy.isFacingRight(),
                        false);
            } else if (enemy.isFlyingSlasher() && flyingSlasherFrames != null) {
                int idleFrameOffset = ((int)(time * FLYING_SLASHER_IDLE_ANIM_SPEED + enemy.getHomeX() * 0.01f))
                        % FLYING_SLASHER_IDLE_FRAMES.length;
                int frameIndex = FLYING_SLASHER_IDLE_FRAMES[idleFrameOffset];
                if (enemy.isAttacking()) {
                    frameIndex = Math.min(
                            flyingSlasherFrames.length - 1,
                            (int)(enemy.getAttackProgress() * flyingSlasherFrames.length));
                }
                TextureRegion frame = flyingSlasherFrames[frameIndex];
                float pulse = 0.92f + 0.08f * MathUtils.sin(time * 8.4f + enemy.getHomeX() * 0.025f);
                float attackStretch = enemy.isAttacking() ? 1.08f : 1f;
                float drawWidth = FLYING_SLASHER_DRAW_WIDTH * pulse * attackStretch;
                float drawHeight = FLYING_SLASHER_DRAW_HEIGHT * (1.02f - pulse * 0.06f);
                float drawX = enemy.getBounds().x + enemy.getBounds().width * 0.5f - drawWidth * 0.5f;
                float drawY = enemy.getBounds().y + enemy.getBounds().height * 0.5f - drawHeight * 0.5f + 4f;
                batch.setColor(1f, 1f - hitFlash * 0.40f, 1f - hitFlash * 0.52f, 1f);
                batch.draw(frame.getTexture(),
                        drawX,
                        drawY,
                        drawWidth,
                        drawHeight,
                        frame.getRegionX(),
                        frame.getRegionY(),
                        frame.getRegionWidth(),
                        frame.getRegionHeight(),
                        !enemy.isFacingRight(),
                        false);
            }
        }

        batch.setColor(Color.WHITE);
    }

    private void renderEnemyProjectiles() {
        if (locationIndex != ShantyLocation.INDEX || flyingEnemyProjectileTexture == null) {
            return;
        }

        for (EnemyProjectile projectile : enemyProjectiles) {
            batch.setColor(1f, 1f, 1f, Math.min(1f, 0.7f + projectile.lifeTimer * 0.08f));
            batch.draw(flyingEnemyProjectileTexture,
                    projectile.bounds.x,
                    projectile.bounds.y,
                    FLYING_ENEMY_PROJECTILE_DRAW_WIDTH * 0.5f,
                    FLYING_ENEMY_PROJECTILE_DRAW_HEIGHT * 0.5f,
                    FLYING_ENEMY_PROJECTILE_DRAW_WIDTH,
                    FLYING_ENEMY_PROJECTILE_DRAW_HEIGHT,
                    1f,
                    1f,
                    projectile.rotation,
                    0,
                    0,
                    flyingEnemyProjectileTexture.getWidth(),
                    flyingEnemyProjectileTexture.getHeight(),
                    projectile.velocityX < 0f,
                    false);
        }

        batch.setColor(Color.WHITE);
    }

    private void renderEnemyProjectileGlow() {
        for (EnemyProjectile projectile : enemyProjectiles) {
            float alpha = Math.min(0.42f, 0.18f + projectile.lifeTimer * 0.04f);
            float centerX = projectile.bounds.x + projectile.bounds.width * 0.5f;
            float centerY = projectile.bounds.y + projectile.bounds.height * 0.5f;
            if (projectile.reflected) {
                shapeRenderer.setColor(0.18f, 0.72f, 0.88f, alpha);
            } else {
                shapeRenderer.setColor(0.68f, 0.12f, 0.10f, alpha);
            }
            shapeRenderer.circle(centerX, centerY, FLYING_ENEMY_PROJECTILE_GLOW, 18);
            if (projectile.reflected) {
                shapeRenderer.setColor(0.82f, 0.98f, 1f, alpha * 0.75f);
            } else {
                shapeRenderer.setColor(1f, 0.40f, 0.30f, alpha * 0.75f);
            }
            shapeRenderer.circle(centerX, centerY, FLYING_ENEMY_PROJECTILE_GLOW * 0.55f, 16);
        }
    }

    private void renderHumanEnemy(Enemy enemy) {
        float walk = MathUtils.sin(time * 10f + enemy.getX() * 0.03f) * 4f;
        float breathe = MathUtils.sin(time * 3.4f + enemy.getX() * 0.01f) * 1.4f;
        float dir = enemy.isFacingRight() ? 1f : -1f;
        Rectangle b = enemy.getBounds();
        float x = b.x;
        float y = b.y;
        float attackProgress = enemy.isAttacking() ? enemy.getAttackProgress() : 0f;
        boolean heavy = enemy.getAttackStyle() == Enemy.AttackStyle.HEAVY;
        boolean lunge = enemy.getAttackStyle() == Enemy.AttackStyle.LUNGE;
        float hitFlash = enemy.isRecentlyDamaged() ? MathUtils.clamp(enemy.getDamageFlashTimer() / 0.22f, 0f, 1f) : 0f;

        shapeRenderer.setColor(
                MathUtils.lerp(enemy.getSkinR(), 1f, hitFlash * 0.85f),
                MathUtils.lerp(enemy.getSkinG(), 0.18f, hitFlash * 0.85f),
                MathUtils.lerp(enemy.getSkinB(), 0.12f, hitFlash * 0.85f),
                1f);
        shapeRenderer.rect(x + 7f, y + 39f + breathe, 18f, 18f);
        shapeRenderer.setColor(
                MathUtils.lerp(enemy.getCoatR(), 0.92f, hitFlash),
                MathUtils.lerp(enemy.getCoatG(), 0.05f, hitFlash),
                MathUtils.lerp(enemy.getCoatB(), 0.04f, hitFlash),
                1f);
        shapeRenderer.rect(x + 1f, y + 8f, 30f, 36f + breathe * 0.3f);
        shapeRenderer.setColor(0.025f, 0.025f, 0.030f, 1f);
        shapeRenderer.rect(x + 4f, y + 47f + breathe, 26f, 7f);
        shapeRenderer.setColor(0.88f, 0.78f, 0.64f, 1f);
        shapeRenderer.rect(x + 9f, y + 48f + breathe, 5f, 3f);
        shapeRenderer.rect(x + 21f, y + 48f + breathe, 5f, 3f);

        float armReach = enemy.isAttacking() ? (heavy ? 9f : lunge ? 28f : 16f) * attackProgress : 0f;
        shapeRenderer.setColor(enemy.getCoatR() * 0.75f, enemy.getCoatG() * 0.75f, enemy.getCoatB() * 0.75f, 1f);
        shapeRenderer.rect(x + (dir > 0 ? 26f : -4f) + dir * armReach, y + 27f, 8f, heavy ? 30f : 22f);
        shapeRenderer.rect(x + (dir > 0 ? -2f : 30f), y + 26f, 8f, 20f);
        if (heavy) {
            shapeRenderer.setColor(0.08f, 0.07f, 0.06f, 1f);
            shapeRenderer.rectLine(x + 16f, y + 48f, x + 16f + dir * (28f + 14f * attackProgress), y + 20f - 8f * attackProgress, 7f);
        } else if (lunge) {
            shapeRenderer.setColor(0.72f, 0.74f, 0.70f, 1f);
            shapeRenderer.rectLine(x + 16f, y + 34f, x + 16f + dir * (48f + 22f * attackProgress), y + 32f, 4f);
        } else if (enemy.isAttacking()) {
            shapeRenderer.setColor(0.70f, 0.66f, 0.58f, 1f);
            shapeRenderer.rectLine(x + 16f, y + 35f, x + 16f + dir * (34f + 8f * attackProgress), y + 42f - 20f * attackProgress, 3f);
        }
        shapeRenderer.setColor(0.055f, 0.058f, 0.064f, 1f);
        shapeRenderer.rect(x + 4f + walk, y, 9f, 13f);
        shapeRenderer.rect(x + 20f - walk, y, 9f, 13f);

        if (hitFlash > 0f) {
            shapeRenderer.setColor(1f, 0.04f, 0.03f, 0.82f * hitFlash);
            shapeRenderer.rectLine(x - 5f, y + 63f, x + 37f, y + 72f, 5f);
            shapeRenderer.rectLine(x + 37f, y + 63f, x - 5f, y + 72f, 4f);
            shapeRenderer.setColor(1f, 0.72f, 0.58f, 0.92f * hitFlash);
            shapeRenderer.rectLine(x + 2f, y + 68f, x + 30f, y + 68f, 2.5f);
        }
    }

    private void renderKnifeSlash() {
        WeaponType weapon = inventorySystem.getSelectedWeapon();
        if (knifeSwingTimer <= 0f || weapon == null) {
            return;
        }
        float progress = 1f - knifeSwingTimer / weapon.getSwingDuration();
        float dir = player.isFacingRight() ? 1f : -1f;
        float centerX = player.getBounds().x + player.getBounds().width * 0.5f;
        float centerY = player.getBounds().y + player.getBounds().height * 0.58f;
        float range = weapon.getRange();
        float forwardRange = range + KNIFE_FORWARD_RANGE_BONUS;
        float fade = MathUtils.sin(progress * MathUtils.PI);

        if (attackDirection == AttackDirection.UP) {
            renderVerticalKnifeSlash(centerX + dir * 40f, centerY + 26f, centerY + range + 92f, fade, dir, false);
            return;
        } else if (attackDirection == AttackDirection.DOWN) {
            renderVerticalKnifeSlash(centerX + dir * 30f, centerY - 10f, player.getBounds().y - range - 84f, fade, dir, true);
            return;
        } else {
            float arcCenterX = centerX + dir * 20f;
            float arcCenterY = centerY + MathUtils.lerp(-4f, 5f, progress);
            float radiusX = forwardRange + 16f;
            float radiusY = 31f;
            float startAngle = -1.05f + progress * 0.42f;
            float endAngle = 1.05f + progress * 0.42f;
            float previousX = 0f;
            float previousY = 0f;

            for (int i = 0; i <= 10; i++) {
                float t = i / 10f;
                float angle = MathUtils.lerp(startAngle, endAngle, t);
                float px = arcCenterX + dir * (24f + MathUtils.cos(angle) * radiusX);
                float py = arcCenterY + MathUtils.sin(angle) * radiusY;
                if (i > 0) {
                    shapeRenderer.setColor(0.02f, 0.28f, 0.34f, 0.44f * fade);
                    shapeRenderer.rectLine(previousX, previousY, px, py, 14f);
                    shapeRenderer.setColor(0.10f, 0.80f, 0.94f, 0.82f * fade);
                    shapeRenderer.rectLine(previousX, previousY, px, py, 8f);
                    shapeRenderer.setColor(0.78f, 0.98f, 1f, 0.94f * fade);
                    shapeRenderer.rectLine(previousX + dir * 1.5f, previousY + 1.5f, px + dir * 1.5f, py + 1.5f, 2.8f);
                }
                previousX = px;
                previousY = py;
            }

            shapeRenderer.setColor(0.70f, 0.96f, 1f, 0.42f * fade);
            shapeRenderer.triangle(
                    arcCenterX + dir * 30f, arcCenterY - 24f,
                    arcCenterX + dir * (forwardRange + 54f), arcCenterY,
                    arcCenterX + dir * 30f, arcCenterY + 26f);
            return;
        }
    }

    private void renderVerticalKnifeSlash(float originX, float startY, float endY, float fade, float dir, boolean downward) {
        float previousX = 0f;
        float previousY = 0f;
        float sweep = downward ? -1f : 1f;
        float slashLength = Math.abs(endY - startY);
        float bodyAvoidance = downward ? 0f : 14f;

        for (int i = 0; i <= 15; i++) {
            float t = i / 15f;
            float curve = MathUtils.sin(t * MathUtils.PI) * (18f + slashLength * 0.045f);
            float px = originX + dir * (curve + MathUtils.lerp(-8f, 22f, t)) + dir * bodyAvoidance * (1f - t * 0.55f);
            float py = MathUtils.lerp(startY, endY, t) + sweep * MathUtils.sin(t * MathUtils.PI * 1.5f) * 12f;
            if (i > 0) {
                float widthBoost = 1f - t * 0.35f;
                shapeRenderer.setColor(0.01f, 0.18f, 0.24f, 0.28f * fade);
                shapeRenderer.rectLine(previousX - dir * 5f, previousY, px - dir * 5f, py, 20f * widthBoost);
                shapeRenderer.setColor(0.05f, 0.70f, 0.92f, 0.72f * fade);
                shapeRenderer.rectLine(previousX, previousY, px, py, 11.5f * widthBoost);
                shapeRenderer.setColor(0.80f, 0.99f, 1f, 0.98f * fade);
                shapeRenderer.rectLine(previousX + dir * 1.8f, previousY + sweep * 1.2f, px + dir * 1.8f, py + sweep * 1.2f, 4f * widthBoost);
            }
            previousX = px;
            previousY = py;
        }

        float flareY = downward ? startY - 12f : startY + 12f;
        float tipY = downward ? endY - 24f : endY + 24f;
        shapeRenderer.setColor(0.60f, 0.96f, 1f, 0.34f * fade);
        shapeRenderer.triangle(originX - 22f, flareY, originX + 22f, flareY, originX + dir * 10f, tipY);

        shapeRenderer.setColor(0.88f, 1f, 1f, 0.28f * fade);
        shapeRenderer.rectLine(originX - dir * 10f, startY + sweep * 8f, originX + dir * 18f, endY - sweep * 18f, 2.4f);
        shapeRenderer.rectLine(originX + dir * 14f, startY + sweep * 2f, originX - dir * 7f, endY - sweep * 26f, 1.8f);
    }

    private void renderParryEffect() {
        float cooldown = player.getParryCooldownProgress();
        if (!player.isParrying() && !player.isParrySuccessVisible() && cooldown <= 0f) {
            return;
        }

        Rectangle b = player.getBounds();
        float centerX = b.x + b.width * 0.5f + (player.isFacingRight() ? 34f : -34f);
        float centerY = b.y + b.height * 0.72f;
        float dir = player.isFacingRight() ? 1f : -1f;
        float success = MathUtils.clamp(player.getParrySuccessTimer() / 0.24f, 0f, 1f);
        float guardAlpha = player.isParrying() ? 0.48f : 0.12f * success;

        if (cooldown > 0f && !player.isParrying()) {
            float ringX = b.x + b.width * 0.5f;
            float ringY = b.y + b.height + 18f;
            shapeRenderer.setColor(0.05f, 0.08f, 0.09f, 0.48f);
            shapeRenderer.circle(ringX, ringY, 13f, 20);
            shapeRenderer.setColor(0.24f, 0.70f, 0.78f, 0.58f);
            shapeRenderer.arc(ringX, ringY, 15f, 90f, -360f * cooldown, 22);
        }

        shapeRenderer.setColor(0.22f, 0.68f, 0.82f, guardAlpha);
        shapeRenderer.arc(centerX - dir * 20f, centerY, 58f, player.isFacingRight() ? -48f : 112f, 96f, 18);
        shapeRenderer.setColor(0.82f, 0.97f, 1f, guardAlpha * 0.9f);
        shapeRenderer.arc(centerX - dir * 20f, centerY, 43f, player.isFacingRight() ? -38f : 122f, 76f, 16);

        if (player.isParrying()) {
            shapeRenderer.setColor(0.75f, 0.98f, 1f, 0.54f);
            shapeRenderer.rectLine(
                    b.x + b.width * 0.5f - dir * 4f,
                    centerY - 8f,
                    centerX + dir * 34f,
                    centerY + 12f,
                    6f);
            shapeRenderer.setColor(0.16f, 0.72f, 0.82f, 0.34f);
            shapeRenderer.triangle(
                    b.x + b.width * 0.5f,
                    centerY - 20f,
                    centerX + dir * 56f,
                    centerY + 4f,
                    b.x + b.width * 0.5f,
                    centerY + 24f);
        }

        if (success <= 0f) {
            return;
        }

        for (int i = 0; i < 10; i++) {
            float angle = -58f + i * 13f;
            if (!player.isFacingRight()) {
                angle = 180f - angle;
            }
            float radians = angle * MathUtils.degreesToRadians;
            float startX = centerX + MathUtils.cos(radians) * (18f + i % 3 * 4f);
            float startY = centerY + MathUtils.sin(radians) * (10f + i % 2 * 5f);
            float endX = startX + MathUtils.cos(radians) * (34f + success * 22f);
            float endY = startY + MathUtils.sin(radians) * (18f + success * 16f);
            shapeRenderer.setColor(1f, 0.78f, 0.28f, 0.72f * success);
            shapeRenderer.rectLine(startX, startY, endX, endY, 2.6f);
            shapeRenderer.setColor(0.70f, 0.96f, 1f, 0.50f * success);
            shapeRenderer.circle(endX, endY, 3.2f, 8);
        }
    }

    private void renderPrologueScene() {
        float w = uiCamera.viewportWidth;
        float h = uiCamera.viewportHeight;
        float pulse = 0.5f + 0.5f * MathUtils.sin(time * 2.1f);

        shapeRenderer.setColor(0.010f, 0.006f, 0.008f, 1f);
        shapeRenderer.rect(0f, 0f, w, h);
        shapeRenderer.setColor(0.18f, 0.020f, 0.026f, 1f);
        shapeRenderer.rect(0f, 0f, w * 0.5f, h);
        shapeRenderer.setColor(0.018f, 0.080f, 0.088f, 1f);
        shapeRenderer.rect(w * 0.5f, 0f, w * 0.5f, h);
        shapeRenderer.setColor(0.012f, 0.006f, 0.008f, 1f);
        shapeRenderer.rect(w * 0.5f - 18f, 0f, 36f, h);

        for (int i = 0; i < 12; i++) {
            float x = i * 120f - 20f;
            shapeRenderer.setColor(0.36f, 0.020f, 0.032f, 0.72f);
            shapeRenderer.rect(x, 0f, 14f, h);
            shapeRenderer.setColor(0.06f, 0.30f, 0.33f, 0.72f);
            shapeRenderer.rect(x + 72f, 0f, 10f, h);
        }

        shapeRenderer.setColor(0.020f, 0.012f, 0.014f, 1f);
        shapeRenderer.rect(w / 2f - 330f, h / 2f - 190f, 660f, 360f);
        shapeRenderer.setColor(0.08f, 0.010f, 0.014f, 1f);
        shapeRenderer.rect(w / 2f - 316f, h / 2f - 176f, 632f, 332f);
        shapeRenderer.setColor(0.58f, 0.055f, 0.050f, 1f);
        shapeRenderer.rect(w / 2f - 316f, h / 2f + 146f, 632f, 8f);
        shapeRenderer.setColor(0.12f, 0.72f, 0.72f, 1f);
        shapeRenderer.rect(w / 2f - 316f, h / 2f - 176f, 632f, 5f);

        drawPrologueStage(w, h, pulse);
    }

    private void drawPrologueStage(float w, float h, float pulse) {
        float centerX = w / 2f;
        float baseY = h / 2f - 112f;
        switch (dialogueLine) {
            case 0:
                drawForbiddenStudy(centerX, baseY, pulse);
                break;
            case 1:
                drawLuciferDeal(centerX, baseY, pulse);
                break;
            case 2:
                drawAngelCapture(centerX, baseY, pulse);
                break;
            case 3:
                drawAngelEngineBirth(centerX, baseY, pulse);
                break;
            case 4:
                drawFamineStage(centerX, baseY);
                break;
            default:
                drawProphecyStage(centerX, baseY, pulse);
                break;
        }
    }

    private void drawForbiddenStudy(float centerX, float baseY, float pulse) {
        drawFloor(centerX, baseY);
        shapeRenderer.setColor(0.18f, 0.030f, 0.030f, 1f);
        shapeRenderer.rect(centerX - 260f, baseY + 30f, 520f, 34f);
        shapeRenderer.setColor(0.08f, 0.040f, 0.030f, 1f);
        shapeRenderer.rect(centerX - 150f, baseY + 64f, 300f, 72f);
        shapeRenderer.setColor(0.74f, 0.64f, 0.44f, 1f);
        shapeRenderer.rect(centerX - 56f, baseY + 92f, 112f, 34f);
        shapeRenderer.setColor(0.95f, 0.20f + pulse * 0.12f, 0.10f, 1f);
        shapeRenderer.rect(centerX - 4f, baseY + 86f, 8f, 48f);
        drawScientist(centerX + 130f, baseY + 66f, false);
    }

    private void drawLuciferDeal(float centerX, float baseY, float pulse) {
        drawFloor(centerX, baseY);
        drawScientist(centerX + 170f, baseY + 64f, false);
        drawLucifer(centerX - 165f, baseY + 38f, pulse);
        shapeRenderer.setColor(0.86f, 0.04f, 0.05f, 1f);
        shapeRenderer.rect(centerX - 84f, baseY + 128f, 168f, 8f);
        shapeRenderer.setColor(0.18f, 0.82f, 0.78f, 1f);
        shapeRenderer.rect(centerX + 64f, baseY + 102f, 88f, 6f);
    }

    private void drawAngelCapture(float centerX, float baseY, float pulse) {
        drawFloor(centerX, baseY);
        float angelX = centerX - 80f + MathUtils.sin(dialogueTypeTimer * 3f) * 8f;
        drawLucifer(centerX - 240f, baseY + 34f, pulse);
        drawScientist(centerX + 225f, baseY + 62f, true);
        drawCapsule(centerX + 88f, baseY + 42f, pulse, false);
        drawAngel(angelX, baseY + 82f, true);
        shapeRenderer.setColor(0.92f, 0.04f, 0.05f, 1f);
        shapeRenderer.rect(centerX - 166f, baseY + 170f, 318f, 6f);
        shapeRenderer.rect(centerX - 142f, baseY + 142f, 288f, 5f);
    }

    private void drawAngelEngineBirth(float centerX, float baseY, float pulse) {
        drawFloor(centerX, baseY);
        drawCapsule(centerX, baseY + 34f, pulse, true);
        shapeRenderer.setColor(0.92f, 0.05f + pulse * 0.12f, 0.05f, 1f);
        shapeRenderer.rect(centerX - 250f, baseY + 96f, 210f, 12f);
        shapeRenderer.rect(centerX + 40f, baseY + 96f, 210f, 12f);
        shapeRenderer.setColor(0.18f, 0.84f, 0.78f, 1f);
        shapeRenderer.rect(centerX - 232f, baseY + 124f, 188f, 8f);
        shapeRenderer.rect(centerX + 44f, baseY + 124f, 188f, 8f);
        for (int i = 0; i < 5; i++) {
            shapeRenderer.setColor(0.22f, 0.09f, 0.05f, 1f);
            shapeRenderer.rect(centerX - 230f + i * 118f, baseY + 40f, 34f, 54f);
            shapeRenderer.setColor(0.92f, 0.72f, 0.18f, 1f);
            shapeRenderer.rect(centerX - 224f + i * 118f, baseY + 74f, 22f, 7f);
        }
    }

    private void drawFamineStage(float centerX, float baseY) {
        shapeRenderer.setColor(0.035f, 0.020f, 0.018f, 1f);
        shapeRenderer.rect(centerX - 280f, baseY + 18f, 560f, 216f);
        for (int i = 0; i < 8; i++) {
            float x = centerX - 250f + i * 72f;
            shapeRenderer.setColor(0.10f, 0.016f, 0.018f, 1f);
            shapeRenderer.rect(x, baseY + 48f, 34f, 106f - i * 5f);
            shapeRenderer.setColor(0.42f, 0.030f, 0.030f, 1f);
            shapeRenderer.rect(x + 6f, baseY + 132f - i * 4f, 22f, 5f);
        }
        shapeRenderer.setColor(0.11f, 0.09f, 0.08f, 1f);
        shapeRenderer.rect(centerX - 72f, baseY + 74f, 144f, 20f);
        shapeRenderer.setColor(0.86f, 0.78f, 0.56f, 1f);
        shapeRenderer.rect(centerX - 16f, baseY + 96f, 32f, 54f);
        shapeRenderer.setColor(0.020f, 0.020f, 0.024f, 1f);
        shapeRenderer.rect(centerX - 20f, baseY + 132f, 40f, 8f);
    }

    private void drawFloor(float centerX, float baseY) {
        shapeRenderer.setColor(0.030f, 0.018f, 0.018f, 1f);
        shapeRenderer.rect(centerX - 280f, baseY + 28f, 560f, 24f);
        shapeRenderer.setColor(0.42f, 0.030f, 0.030f, 1f);
        shapeRenderer.rect(centerX - 280f, baseY + 50f, 560f, 5f);
    }

    private void drawScientist(float x, float y, boolean reaching) {
        shapeRenderer.setColor(0.10f, 0.12f, 0.12f, 1f);
        shapeRenderer.rect(x - 18f, y, 36f, 92f);
        shapeRenderer.setColor(0.62f, 0.78f, 0.72f, 1f);
        shapeRenderer.rect(x - 14f, y + 72f, 28f, 22f);
        shapeRenderer.setColor(0.86f, 0.78f, 0.54f, 1f);
        shapeRenderer.rect(x - 36f, y + (reaching ? 56f : 34f), 54f, 8f);
        shapeRenderer.setColor(0.18f, 0.82f, 0.78f, 1f);
        shapeRenderer.rect(x + 12f, y + 24f, 5f, 58f);
    }

    private void drawLucifer(float x, float y, float pulse) {
        shapeRenderer.setColor(0.045f, 0.010f, 0.014f, 1f);
        shapeRenderer.rect(x - 26f, y, 52f, 132f);
        shapeRenderer.setColor(0.82f, 0.04f + pulse * 0.10f, 0.05f, 1f);
        shapeRenderer.rect(x - 48f, y + 106f, 96f, 22f);
        shapeRenderer.rect(x - 34f, y + 70f, 68f, 14f);
        shapeRenderer.setColor(0.020f, 0.020f, 0.026f, 1f);
        shapeRenderer.rect(x - 18f, y + 84f, 36f, 38f);
        shapeRenderer.setColor(0.92f, 0.10f, 0.08f, 1f);
        shapeRenderer.rect(x - 8f, y + 100f, 5f, 5f);
        shapeRenderer.rect(x + 4f, y + 100f, 5f, 5f);
    }

    private void drawAngel(float x, float y, boolean bound) {
        shapeRenderer.setColor(0.92f, 0.87f, 0.70f, 1f);
        shapeRenderer.rect(x - 24f, y + 34f, 48f, 76f);
        shapeRenderer.setColor(0.94f, 0.94f, 0.88f, 1f);
        shapeRenderer.rect(x - 118f, y + 20f, 86f, 104f);
        shapeRenderer.rect(x + 32f, y + 20f, 86f, 104f);
        shapeRenderer.setColor(0.030f, 0.030f, 0.036f, 1f);
        shapeRenderer.rect(x - 15f, y + 78f, 30f, 8f);
        if (bound) {
            shapeRenderer.setColor(0.86f, 0.04f, 0.05f, 1f);
            shapeRenderer.rect(x - 124f, y + 80f, 248f, 6f);
            shapeRenderer.rect(x - 70f, y + 52f, 140f, 5f);
        }
    }

    private void drawCapsule(float x, float y, float pulse, boolean active) {
        shapeRenderer.setColor(0.80f, 0.055f, 0.055f, 1f);
        shapeRenderer.rect(x - 46f, y, 92f, 184f);
        shapeRenderer.setColor(0.020f, 0.018f, 0.020f, 1f);
        shapeRenderer.rect(x - 36f, y + 12f, 72f, 160f);
        shapeRenderer.setColor(0.18f, 0.82f + pulse * 0.10f, 0.78f, 1f);
        shapeRenderer.rect(x - 8f, y + 24f, 16f, 136f);
        if (active) {
            drawAngel(x, y + 30f, false);
            shapeRenderer.setColor(0.90f, 0.06f + pulse * 0.12f, 0.05f, 1f);
            shapeRenderer.rect(x - 130f, y + 62f, 260f, 8f);
            shapeRenderer.rect(x - 130f, y + 124f, 260f, 8f);
        }
    }

    private void drawProphecyStage(float centerX, float baseY, float pulse) {
        shapeRenderer.setColor(0.08f, 0.006f, 0.010f, 1f);
        shapeRenderer.rect(centerX - 250f, baseY + 16f, 500f, 210f);
        shapeRenderer.setColor(0.24f, 0.018f, 0.024f, 1f);
        shapeRenderer.rect(centerX - 236f, baseY + 30f, 486f, 182f);

        shapeRenderer.setColor(0.92f, 0.86f, 0.70f, 1f);
        shapeRenderer.rect(centerX - 170f, baseY + 54f, 138f, 136f);
        shapeRenderer.setColor(0.020f, 0.020f, 0.024f, 1f);
        shapeRenderer.rect(centerX - 101f, baseY + 54f, 69f, 136f);
        shapeRenderer.setColor(0.90f, 0.08f + pulse * 0.16f, 0.08f, 1f);
        shapeRenderer.rect(centerX - 36f, baseY + 54f, 8f, 136f);

        shapeRenderer.setColor(0.94f, 0.94f, 0.88f, 1f);
        shapeRenderer.rect(centerX - 228f, baseY + 90f, 54f, 72f);
        shapeRenderer.rect(centerX - 18f, baseY + 90f, 54f, 72f);
        shapeRenderer.setColor(0.030f, 0.030f, 0.036f, 1f);
        shapeRenderer.rect(centerX + 82f, baseY + 70f, 80f, 116f);
        shapeRenderer.setColor(0.020f, 0.020f, 0.026f, 1f);
        shapeRenderer.rect(centerX + 98f, baseY + 138f, 50f, 18f);
        shapeRenderer.setColor(0.86f, 0.10f, 0.08f, 1f);
        shapeRenderer.rect(centerX + 68f, baseY + 58f, 112f, 8f);
        shapeRenderer.rect(centerX + 68f, baseY + 190f, 112f, 8f);
    }

    private BitmapFont createGameFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.absolute("C:/Windows/Fonts/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 22;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS
                + "\u0410\u0411\u0412\u0413\u0414\u0415\u0401\u0416\u0417\u0418\u0419\u041a\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042a\u042b\u042c\u042d\u042e\u042f"
                + "\u0430\u0431\u0432\u0433\u0434\u0435\u0451\u0436\u0437\u0438\u0439\u043a\u043b\u043c\u043d\u043e\u043f\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044a\u044b\u044c\u044d\u044e\u044f"
                + "\u00ab\u00bb\u2116\u2026\u2014\u2013";
        BitmapFont generatedFont = generator.generateFont(parameter);
        generator.dispose();
        return generatedFont;
    }

    private void loadHeroSpriteSheet() {
        heroTexture = new Texture(Gdx.files.internal("player/hero_sheet.png"));
        heroTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        heroFrames = new TextureRegion[HERO_FRAME_COUNT];
        for (int i = 0; i < heroFrames.length; i++) {
            heroFrames[i] = new TextureRegion(heroTexture, i * HERO_FRAME_SIZE, 0, HERO_FRAME_SIZE, HERO_FRAME_SIZE);
        }
    }

    private void loadBedroomTexture() {
        bedroomTexture = new Texture(Gdx.files.internal("room_intro.png"));
        bedroomTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    private void loadBedroomTvStaticFrames() {
        bedroomTvStaticFrames = new Texture[BEDROOM_TV_STATIC_FRAME_COUNT];
        for (int frame = 0; frame < bedroomTvStaticFrames.length; frame++) {
            Pixmap pixmap = new Pixmap(BEDROOM_TV_STATIC_FRAME_WIDTH, BEDROOM_TV_STATIC_FRAME_HEIGHT, Pixmap.Format.RGBA8888);
            for (int y = 0; y < BEDROOM_TV_STATIC_FRAME_HEIGHT; y += 2) {
                for (int x = 0; x < BEDROOM_TV_STATIC_FRAME_WIDTH; x += 2) {
                    int noise = pseudoNoise(x, y, frame);
                    float shade = 0.18f + (noise & 127) / 165f;
                    if ((noise & 31) < 5) {
                        shade += 0.25f;
                    } else if ((noise & 63) > 56) {
                        shade *= 0.42f;
                    }
                    shade = MathUtils.clamp(shade, 0.06f, 0.92f);
                    pixmap.setColor(shade, shade, shade, 1f);
                    pixmap.fillRectangle(x, y, 2, 2);
                }
            }
            Texture texture = new Texture(pixmap);
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            bedroomTvStaticFrames[frame] = texture;
            pixmap.dispose();
        }
    }

    private int pseudoNoise(int x, int y, int frame) {
        int value = x * 734287 + y * 912271 + frame * 438289;
        value ^= value << 13;
        value ^= value >>> 17;
        value ^= value << 5;
        return value & 0x7fffffff;
    }

    private void loadHallwayTexture() {
        hallwayTexture = new Texture(Gdx.files.internal("hallway_second_room.png"));
        hallwayTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    private void loadStreetTexture() {
        streetTexture = new Texture(Gdx.files.internal("street_third_room_v2.png"));
        streetTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    private void loadShantyTexture() {
        shantyTexture = new Texture(Gdx.files.internal("shanty_fourth_room.png"));
        shantyTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        fogPixelTexture = new Texture(pixmap);
        pixmap.dispose();
        shantyFogTexture = createFogTexture();
    }

    private Texture createFogTexture() {
        int width = 256;
        int height = 96;
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        for (int y = 0; y < height; y++) {
            float vertical = 1f - Math.abs((y - height * 0.5f) / (height * 0.5f));
            for (int x = 0; x < width; x++) {
                float wave = 0.5f + 0.5f * MathUtils.sin(x * 0.035f + y * 0.07f);
                float wave2 = 0.5f + 0.5f * MathUtils.sin(x * 0.083f - y * 0.045f + 1.7f);
                float edge = MathUtils.clamp(Math.min(x / 28f, (width - 1 - x) / 28f), 0f, 1f);
                float alpha = MathUtils.clamp((vertical * 0.62f + wave * 0.24f + wave2 * 0.18f - 0.36f) * edge, 0f, 1f);
                pixmap.setColor(1f, 1f, 1f, alpha * 0.74f);
                pixmap.drawPixel(x, y);
            }
        }
        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }

    private void loadBossTexture() {
        bossTexture = new Texture(Gdx.files.internal("boss_fourth_room.png"));
        bossTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    private void loadFlyingEnemyTextures() {
        if (Gdx.files.internal(FLYING_ENEMY_ASSET).exists()) {
            flyingEnemyTexture = new Texture(Gdx.files.internal(FLYING_ENEMY_ASSET));
            flyingEnemyTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            flyingEnemyFrames = new TextureRegion[FLYING_ENEMY_FRAME_X.length];
            for (int i = 0; i < flyingEnemyFrames.length; i++) {
                flyingEnemyFrames[i] = new TextureRegion(
                        flyingEnemyTexture,
                        FLYING_ENEMY_FRAME_X[i],
                        0,
                        FLYING_ENEMY_FRAME_WIDTH[i],
                        flyingEnemyTexture.getHeight());
            }
        }

        if (Gdx.files.internal(FLYING_SLASHER_ASSET).exists()) {
            flyingSlasherTexture = new Texture(Gdx.files.internal(FLYING_SLASHER_ASSET));
        } else {
            flyingSlasherTexture = createFlyingSlasherFallbackTexture();
        }
        if (flyingSlasherTexture != null) {
            flyingSlasherTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            flyingSlasherFrames = new TextureRegion[FLYING_SLASHER_FRAME_COUNT];
            int slasherFrameWidth = flyingSlasherTexture.getWidth() / FLYING_SLASHER_FRAME_COUNT;
            int slasherFrameHeight = flyingSlasherTexture.getHeight();
            for (int i = 0; i < FLYING_SLASHER_FRAME_COUNT; i++) {
                flyingSlasherFrames[i] = new TextureRegion(
                        flyingSlasherTexture,
                        i * slasherFrameWidth,
                        0,
                        slasherFrameWidth,
                        slasherFrameHeight);
            }
        }

        if (Gdx.files.internal(FLYING_ENEMY_PROJECTILE_ASSET).exists()) {
            flyingEnemyProjectileTexture = new Texture(Gdx.files.internal(FLYING_ENEMY_PROJECTILE_ASSET));
            flyingEnemyProjectileTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
    }

    private Texture createFlyingSlasherFallbackTexture() {
        Pixmap pixmap = new Pixmap(FLYING_SLASHER_FRAME_WIDTH * FLYING_SLASHER_FRAME_COUNT, FLYING_SLASHER_FRAME_HEIGHT, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();

        for (int frame = 0; frame < FLYING_SLASHER_FRAME_COUNT; frame++) {
            int offsetX = frame * FLYING_SLASHER_FRAME_WIDTH;
            int sway = frame == 0 ? -18 : frame == 1 ? 6 : frame == 2 ? 20 : 4;
            int slashLift = frame == 0 ? 30 : frame == 1 ? -8 : frame == 2 ? -18 : 16;

            pixmap.setColor(0.08f, 0.02f, 0.03f, 0.32f);
            pixmap.fillCircle(offsetX + 92, 58, 30 + frame * 2);
            pixmap.setColor(0.12f, 0.02f, 0.03f, 0.50f);
            pixmap.fillTriangle(offsetX + 72 + sway, 78, offsetX + 118 + sway, 74, offsetX + 96 + sway, 18);

            pixmap.setColor(0.08f, 0.02f, 0.03f, 0.96f);
            pixmap.fillCircle(offsetX + 92 + sway, 102, 18);
            pixmap.fillTriangle(offsetX + 86 + sway, 94, offsetX + 128 + sway, 118, offsetX + 92 + sway, 34);
            pixmap.fillTriangle(offsetX + 102 + sway, 96, offsetX + 74 + sway, 132, offsetX + 92 + sway, 40);
            pixmap.fillTriangle(offsetX + 98 + sway, 88, offsetX + 146 + sway, 74, offsetX + 116 + sway, 30);
            pixmap.fillTriangle(offsetX + 90 + sway, 86, offsetX + 52 + sway, 72, offsetX + 74 + sway, 28);

            pixmap.setColor(0.18f, 0.03f, 0.05f, 0.98f);
            pixmap.fillRectangle(offsetX + 87 + sway, 62, 10, 42);
            pixmap.fillTriangle(offsetX + 92 + sway, 70, offsetX + 78 + sway, 40, offsetX + 84 + sway, 66);
            pixmap.fillTriangle(offsetX + 96 + sway, 70, offsetX + 110 + sway, 40, offsetX + 100 + sway, 64);

            pixmap.setColor(0.34f, 0.02f, 0.04f, 1f);
            pixmap.fillCircle(offsetX + 102 + sway, 112, 6);
            pixmap.fillCircle(offsetX + 82 + sway, 112, 4);

            pixmap.setColor(0.86f, 0.10f, 0.14f, 0.88f);
            pixmap.fillTriangle(offsetX + 96 + sway, 100 + slashLift, offsetX + 164 + sway, 126 + slashLift, offsetX + 126 + sway, 34 + slashLift);
            pixmap.setColor(1f, 0.26f, 0.20f, 0.92f);
            pixmap.drawLine(offsetX + 98 + sway, 100 + slashLift, offsetX + 156 + sway, 120 + slashLift);
            pixmap.drawLine(offsetX + 100 + sway, 96 + slashLift, offsetX + 146 + sway, 58 + slashLift);
            pixmap.drawLine(offsetX + 102 + sway, 92 + slashLift, offsetX + 134 + sway, 36 + slashLift);

            pixmap.setColor(0.96f, 0.88f, 0.86f, 0.82f);
            pixmap.fillCircle(offsetX + 92 + sway, 106, 2);
            pixmap.fillCircle(offsetX + 100 + sway, 104, 2);
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void startNewGameInBedroom() {
        locationIndex = 1;
        gameMap = baseMap;
        prologueActive = false;
        dialogueVisible = false;
        watchedBedroomTv = false;
        shantyCleared = false;
        enterBedroomAfterPrologue();
    }

    private void renderWorldTextures() {
        if (locationIndex == 1 && bedroomTexture != null) {
            batch.draw(bedroomTexture, 0f, BEDROOM_BACKGROUND_Y, BEDROOM_RIGHT_WALL, BEDROOM_BACKGROUND_HEIGHT);
            return;
        }
        if (locationIndex == 2 && hallwayTexture != null) {
            batch.draw(hallwayTexture, HALLWAY_BACKGROUND_X, HALLWAY_BACKGROUND_Y, HALLWAY_BACKGROUND_WIDTH, HALLWAY_BACKGROUND_HEIGHT);
            return;
        }
        if (locationIndex == 3 && streetTexture != null) {
            batch.draw(streetTexture, STREET_BACKGROUND_X, STREET_BACKGROUND_Y, STREET_BACKGROUND_WIDTH, STREET_BACKGROUND_HEIGHT);
            return;
        }
        if (locationIndex == ShantyLocation.INDEX && shantyTexture != null) {
            batch.draw(shantyTexture, SHANTY_BACKGROUND_X, SHANTY_BACKGROUND_Y, SHANTY_BACKGROUND_WIDTH, SHANTY_BACKGROUND_HEIGHT);
            renderShantyFogBatch();
            return;
        }
        if (locationIndex == BossLocation.INDEX && bossTexture != null) {
            batch.draw(bossTexture, BOSS_BACKGROUND_X, BOSS_BACKGROUND_Y, BOSS_BACKGROUND_WIDTH, BOSS_BACKGROUND_HEIGHT);
        }
    }

    private void renderShantyFogBatch() {
        if (fogPixelTexture == null || shantyFogTexture == null) {
            return;
        }

        batch.setColor(0.12f, 0.16f, 0.17f, 0.055f);
        batch.draw(fogPixelTexture, SHANTY_BACKGROUND_X, 300f, SHANTY_BACKGROUND_WIDTH, 470f);
        batch.setColor(0.06f, 0.07f, 0.075f, 0.06f);
        batch.draw(fogPixelTexture, SHANTY_BACKGROUND_X, 770f, SHANTY_BACKGROUND_WIDTH, 210f);

        float drift = MathUtils.sin(time * 0.18f) * 42f;
        for (int i = 0; i < 18; i++) {
            float x = Math.floorMod((int)(i * 205f + drift + MathUtils.sin(i * 1.9f) * 80f), (int)SHANTY_BACKGROUND_WIDTH) - 190f;
            float y = 315f + (i % 5) * 82f + MathUtils.sin(time * 0.24f + i * 0.7f) * 24f;
            float width = 330f + (i % 4) * 96f;
            float height = 78f + (i % 3) * 26f;
            float alpha = 0.09f + (i % 4) * 0.018f;
            batch.setColor(0.64f, 0.76f, 0.78f, alpha);
            batch.draw(shantyFogTexture, x, y, width, height);
        }

        for (int i = 0; i < 7; i++) {
            float x = 130f + i * 430f + MathUtils.sin(time * 0.12f + i) * 28f;
            float y = 580f + (i % 2) * 76f;
            batch.setColor(0.70f, 0.82f, 0.84f, 0.07f);
            batch.draw(shantyFogTexture, x, y, 520f, 140f);
        }

        batch.setColor(Color.WHITE);
    }

    private void renderBedroomTvStatic() {
        if (locationIndex != 1 || bedroomTvStaticFrames == null || bedroomTvStaticFrames.length == 0) {
            return;
        }

        int frameIndex = (int)(time * 18f + MathUtils.sin(time * 11.4f) * 1.7f);
        frameIndex = Math.floorMod(frameIndex, bedroomTvStaticFrames.length);
        Texture frame = bedroomTvStaticFrames[frameIndex];
        float flash = 0.82f + 0.16f * MathUtils.sin(time * 29f);

        batch.setColor(flash, flash, flash, 1f);
        batch.draw(frame, BEDROOM_TV_SCREEN_X, BEDROOM_TV_SCREEN_Y, BEDROOM_TV_SCREEN_WIDTH, BEDROOM_TV_SCREEN_HEIGHT);

        int tearFrame = Math.floorMod(frameIndex + 4, bedroomTvStaticFrames.length);
        Texture tearTexture = bedroomTvStaticFrames[tearFrame];
        int stripHeight = 6;
        int stripY = Math.floorMod((int)(time * 93f), BEDROOM_TV_STATIC_FRAME_HEIGHT - stripHeight);
        float bandY = BEDROOM_TV_SCREEN_Y + Math.floorMod((int)(time * 41f), (int)(BEDROOM_TV_SCREEN_HEIGHT - 10f));
        float bandHeight = 8f;
        float bandJitter = MathUtils.sin(time * 67f) * 3f;
        batch.setColor(1f, 1f, 1f, 0.88f);
        batch.draw(tearTexture,
                BEDROOM_TV_SCREEN_X + 2f + bandJitter,
                bandY,
                BEDROOM_TV_SCREEN_WIDTH - 4f - Math.abs(bandJitter),
                bandHeight,
                0,
                stripY,
                BEDROOM_TV_STATIC_FRAME_WIDTH,
                stripHeight,
                false,
                false);
        batch.setColor(Color.WHITE);
    }

    private void renderRuinedChapelStation() {
        shapeRenderer.setColor(0.010f, 0.010f, 0.012f, 1f);
        shapeRenderer.rect(0f, 0f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        shapeRenderer.rect(0f, 0f, HALLWAY_BACKGROUND_X, Constants.WORLD_HEIGHT);
        shapeRenderer.rect(HALLWAY_BACKGROUND_X + HALLWAY_BACKGROUND_WIDTH, 0f,
                Constants.WORLD_WIDTH - (HALLWAY_BACKGROUND_X + HALLWAY_BACKGROUND_WIDTH), Constants.WORLD_HEIGHT);
        shapeRenderer.setColor(0.010f, 0.010f, 0.012f, 0.94f);
        shapeRenderer.rect(0f, 860f, Constants.WORLD_WIDTH, 140f);
        shapeRenderer.rect(0f, 0f, Constants.WORLD_WIDTH, 58f);
    }

    private void renderAngelEngineLine() {
        drawBackdrop(0.012f, 0.010f, 0.014f, 0.105f, 0.015f, 0.025f);
        float pulse = 0.5f + 0.5f * MathUtils.sin(time * 4f);

        shapeRenderer.setColor(0.09f, 0.018f, 0.026f, 1f);
        shapeRenderer.rect(720f, 270f, 280f, 180f);
        shapeRenderer.setColor(0.90f, 0.08f + pulse * 0.16f, 0.08f, 1f);
        shapeRenderer.rect(848f, 300f, 24f, 122f);
        shapeRenderer.rect(793f, 350f, 134f, 14f);
        shapeRenderer.setColor(0.20f, 0.75f, 0.72f, 1f);
        shapeRenderer.rect(858f, 292f, 5f, 140f);
        shapeRenderer.setColor(0.34f, 0.18f, 0.12f, 1f);
        shapeRenderer.rect(770f, 250f, 190f, 24f);

        for (int i = 0; i < 5; i++) {
            float x = i * 540f + 100f;
            shapeRenderer.setColor(0.09f, 0.02f, 0.028f, 1f);
            shapeRenderer.rect(x, 430f, 260f, 22f);
            shapeRenderer.setColor(0.80f, 0.07f, 0.07f, 1f);
            shapeRenderer.rect(x + 18f, 436f, 46f, 7f);
            shapeRenderer.setColor(0.12f, 0.72f, 0.76f, 1f);
            shapeRenderer.rect(x + 88f, 436f, 46f, 7f);
        }

        drawPillars(0.060f, 0.018f, 0.026f);
        drawRails(0.34f, 0.030f, 0.035f);
        drawGround(0.080f, 0.026f, 0.030f, 0.020f, 0.012f, 0.018f);
    }

    private void renderBossLaboratory() {
        shapeRenderer.setColor(0.008f, 0.010f, 0.012f, 1f);
        shapeRenderer.rect(0f, 0f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        shapeRenderer.rect(0f, 0f, BOSS_BACKGROUND_X, Constants.WORLD_HEIGHT);
        shapeRenderer.rect(BOSS_BACKGROUND_X + BOSS_BACKGROUND_WIDTH, 0f,
                Constants.WORLD_WIDTH - (BOSS_BACKGROUND_X + BOSS_BACKGROUND_WIDTH), Constants.WORLD_HEIGHT);
        shapeRenderer.setColor(0.004f, 0.006f, 0.008f, 0.95f);
        shapeRenderer.rect(0f, 0f, Constants.WORLD_WIDTH, 86f);
        shapeRenderer.rect(0f, 944f, Constants.WORLD_WIDTH, 56f);
        shapeRenderer.setColor(0.040f, 0.045f, 0.050f, 1f);
        shapeRenderer.rect(BOSS_LEFT_WALL - 34f, 0f, 34f, Constants.WORLD_HEIGHT);
        shapeRenderer.rect(BOSS_RIGHT_WALL, 0f, Constants.WORLD_WIDTH - BOSS_RIGHT_WALL, Constants.WORLD_HEIGHT);

        float pulse = 0.5f + 0.5f * MathUtils.sin(time * 2.6f);
        shapeRenderer.setColor(0.16f, 0.38f + pulse * 0.14f, 0.28f + pulse * 0.10f, 0.20f);
        shapeRenderer.rect(BOSS_ARENA_MIN_X, 170f, BOSS_ARENA_MAX_X - BOSS_ARENA_MIN_X, 12f);
    }

    private void renderShantyCity() {
        shapeRenderer.setColor(0.006f, 0.008f, 0.010f, 1f);
        shapeRenderer.rect(0f, 0f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        drawShantyWalkway();
    }

    private void drawShantyWalkway() {
        shapeRenderer.setColor(0.012f, 0.011f, 0.012f, 0.92f);
        shapeRenderer.rect(0f, 0f, Constants.WORLD_WIDTH, SHANTY_GROUND_TOP + 12f);
        shapeRenderer.setColor(0.032f, 0.026f, 0.025f, 0.78f);
        shapeRenderer.rect(0f, SHANTY_GROUND_TOP - 5f, Constants.WORLD_WIDTH, 12f);
        shapeRenderer.setColor(0.070f, 0.044f, 0.036f, 0.38f);
        shapeRenderer.rect(0f, SHANTY_GROUND_TOP + 4f, Constants.WORLD_WIDTH, 3f);

        for (int i = 0; i < 22; i++) {
            float x = i * 142f + MathUtils.sin(i * 1.7f) * 18f;
            float y = 9f + MathUtils.sin(i * 2.3f) * 5f;
            float width = 44f + (i % 5) * 12f;
            shapeRenderer.setColor(0.050f, 0.044f, 0.044f, 0.38f);
            shapeRenderer.ellipse(x, y, width, 8f + (i % 3) * 3f);
        }

        for (int i = 0; i < 16; i++) {
            float x = i * 196f + 34f;
            shapeRenderer.setColor(0.090f, 0.050f, 0.040f, 0.30f);
            shapeRenderer.rect(x, SHANTY_GROUND_TOP - 11f + (i % 2) * 3f, 72f, 2f);
        }
    }

    private void renderDungeonDepths() {
        float pulse = 0.5f + 0.5f * MathUtils.sin(time * 3.2f);
        drawBackdrop(0.010f, 0.010f, 0.014f, 0.038f, 0.022f, 0.032f);

        shapeRenderer.setColor(0.014f, 0.018f, 0.022f, 1f);
        shapeRenderer.rect(0f, 170f, Constants.WORLD_WIDTH, 520f);
        shapeRenderer.setColor(0.060f, 0.022f, 0.028f, 1f);
        shapeRenderer.rect(0f, 690f, Constants.WORLD_WIDTH, 12f);
        shapeRenderer.setColor(0.018f, 0.020f, 0.024f, 1f);
        shapeRenderer.rect(0f, 702f, Constants.WORLD_WIDTH, 230f);
        shapeRenderer.setColor(0.032f, 0.012f, 0.018f, 0.65f);
        shapeRenderer.rect(0f, 820f, Constants.WORLD_WIDTH, 90f);

        for (int i = 0; i < 8; i++) {
            float x = i * 370f + 70f;
            shapeRenderer.setColor(0.050f, 0.018f, 0.022f, 1f);
            shapeRenderer.rect(x, 180f, 58f, 520f);
            shapeRenderer.rect(x - 16f, 682f, 90f, 24f);
        }

        float streetTop = Constants.GROUND_Y + Constants.GROUND_HEIGHT;
        drawCityBlock(40f, streetTop, 120f, 380f, 0.11f, 0.70f, 0.74f, pulse);
        drawCityBlock(190f, streetTop, 76f, 534f, 0.72f, 0.10f, 0.10f, pulse);
        drawCityBlock(300f, streetTop, 210f, 350f, 0.76f, 0.62f, 0.18f, pulse);
        drawCityBlock(560f, streetTop, 126f, 474f, 0.12f, 0.72f, 0.78f, pulse);
        drawCityBlock(720f, streetTop, 92f, 608f, 0.72f, 0.10f, 0.10f, pulse);
        drawCityBlock(850f, streetTop, 160f, 416f, 0.76f, 0.62f, 0.18f, pulse);
        drawCityBlock(1040f, streetTop, 118f, 570f, 0.12f, 0.72f, 0.78f, pulse);
        drawCityBlock(1190f, streetTop, 78f, 376f, 0.74f, 0.12f, 0.12f, pulse);
        drawCityBlock(1290f, streetTop, 190f, 650f, 0.76f, 0.62f, 0.18f, pulse);
        drawCityBlock(1520f, streetTop, 98f, 408f, 0.12f, 0.72f, 0.78f, pulse);
        drawCityBlock(1650f, streetTop, 110f, 682f, 0.74f, 0.12f, 0.12f, pulse);
        drawCityBlock(1790f, streetTop, 220f, 450f, 0.76f, 0.62f, 0.18f, pulse);
        drawCityBlock(2050f, streetTop, 120f, 584f, 0.12f, 0.72f, 0.78f, pulse);
        drawCityBlock(2190f, streetTop, 82f, 718f, 0.74f, 0.12f, 0.12f, pulse);
        drawCityBlock(2310f, streetTop, 240f, 420f, 0.76f, 0.62f, 0.18f, pulse);
        drawCityBlock(2590f, streetTop, 104f, 542f, 0.12f, 0.72f, 0.78f, pulse);
        drawCityBlock(2730f, streetTop, 190f, 380f, 0.76f, 0.62f, 0.18f, pulse);

        for (int i = 0; i < 12; i++) {
            float x = 32f + i * 250f;
            shapeRenderer.setColor(0.70f, 0.08f + pulse * 0.06f, 0.08f, 0.42f);
            shapeRenderer.rect(x, 650f, 92f, 4f);
            shapeRenderer.setColor(0.12f, 0.70f, 0.74f, 0.38f);
            shapeRenderer.rect(x + 48f, 580f, 4f, 146f);
        }

        drawNeonBillboard(394f, 308f, 250f, 132f, 0.12f, 0.72f, 0.78f, pulse);
        drawNeonBillboard(1442f, 342f, 290f, 144f, 0.72f, 0.12f, 0.12f, pulse);
        drawNeonBillboard(2260f, 304f, 210f, 122f, 0.76f, 0.64f, 0.20f, pulse);

        shapeRenderer.setColor(0.030f, 0.016f, 0.014f, 1f);
        shapeRenderer.rect(0f, 164f, Constants.WORLD_WIDTH, 26f);
        shapeRenderer.setColor(0.72f, 0.10f + pulse * 0.06f, 0.08f, 0.74f);
        shapeRenderer.rect(0f, 180f, Constants.WORLD_WIDTH, 4f);

        shapeRenderer.setColor(0.010f, 0.008f, 0.010f, 0.38f);
        shapeRenderer.rect(0f, 0f, Constants.WORLD_WIDTH, 150f);
        shapeRenderer.setColor(0.010f, 0.008f, 0.010f, 0.62f);
        shapeRenderer.rect(0f, 0f, Constants.WORLD_WIDTH, 88f);
        shapeRenderer.setColor(0.006f, 0.005f, 0.006f, 0.86f);
        shapeRenderer.rect(0f, 0f, Constants.WORLD_WIDTH, 42f);

        renderCityBalconies(pulse);
        drawGround(0.14f, 0.06f, 0.05f, 0.025f, 0.010f, 0.012f);
    }

    private void renderStreetExterior() {
        shapeRenderer.setColor(0.010f, 0.010f, 0.012f, 1f);
        shapeRenderer.rect(0f, 0f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        shapeRenderer.rect(0f, 0f, STREET_BACKGROUND_X, Constants.WORLD_HEIGHT);
        shapeRenderer.rect(STREET_BACKGROUND_X + STREET_BACKGROUND_WIDTH, 0f,
                Constants.WORLD_WIDTH - (STREET_BACKGROUND_X + STREET_BACKGROUND_WIDTH), Constants.WORLD_HEIGHT);
        shapeRenderer.setColor(0.010f, 0.010f, 0.012f, 0.88f);
        shapeRenderer.rect(0f, 0f, Constants.WORLD_WIDTH, 72f);
        shapeRenderer.rect(0f, 860f, Constants.WORLD_WIDTH, 140f);
    }

    private void drawCityBlock(float x, float y, float width, float height, float neonR, float neonG, float neonB, float pulse) {
        shapeRenderer.setColor(0.026f, 0.030f, 0.036f, 1f);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.setColor(0.014f, 0.016f, 0.020f, 1f);
        shapeRenderer.rect(x, y, width, 14f);
        shapeRenderer.setColor(0.070f, 0.016f, 0.018f, 1f);
        shapeRenderer.rect(x + 10f, y + height * 0.60f, width - 20f, 8f);
        shapeRenderer.setColor(neonR, neonG, neonB, 0.84f);
        shapeRenderer.rect(x + 16f, y + 28f, 8f, height - 60f);
        if (width > 110f) {
            shapeRenderer.rect(x + width - 24f, y + 50f, 6f, height - 94f);
        }
        shapeRenderer.setColor(neonR * (0.72f + pulse * 0.28f), neonG, neonB, 0.88f);
        shapeRenderer.rect(x + width * 0.22f, y + height - 24f, width * 0.46f, 7f);
        shapeRenderer.setColor(0.84f, 0.88f, 0.84f, 0.18f);
        shapeRenderer.rect(x + width * 0.34f, y + height * 0.34f, width * 0.18f, 6f);
        int windowRows = Math.max(2, (int) (height / 92f));
        int windowColumns = Math.max(1, (int) (width / 62f));
        for (int row = 0; row < windowRows; row++) {
            for (int column = 0; column < windowColumns; column++) {
                float windowX = x + 34f + column * 54f;
                float windowY = y + 64f + row * 82f;
                if (windowX + 18f < x + width - 18f && windowY + 24f < y + height - 40f) {
                    float flicker = ((row + column) % 3 == 0) ? pulse * 0.15f : 0f;
                    shapeRenderer.setColor(neonR * 0.45f + flicker, neonG * 0.45f, neonB * 0.45f, 0.54f);
                    shapeRenderer.rect(windowX, windowY, 18f, 24f);
                    shapeRenderer.setColor(0.010f, 0.012f, 0.016f, 0.56f);
                    shapeRenderer.rect(windowX + 7f, windowY, 4f, 24f);
                }
            }
        }
        if (height > 420f) {
            shapeRenderer.setColor(0.020f, 0.024f, 0.028f, 1f);
            shapeRenderer.rect(x + width * 0.38f, y + height, width * 0.22f, 54f);
            shapeRenderer.setColor(neonR, neonG, neonB, 0.72f);
            shapeRenderer.rect(x + width * 0.47f, y + height + 10f, width * 0.04f, 70f);
        }
    }

    private void renderCityBalconies(float pulse) {
        for (int i = 0; i < gameMap.getPlatforms().size; i++) {
            Platform platform = gameMap.getPlatforms().get(i);
            Rectangle bounds = platform.getBounds();
            float light = 0.66f + pulse * 0.12f;

            shapeRenderer.setColor(0.006f, 0.006f, 0.008f, 0.58f);
            shapeRenderer.rect(bounds.x + 16f, bounds.y - 16f, bounds.width, bounds.height + 16f);
            shapeRenderer.setColor(0.050f, 0.056f, 0.064f, 1f);
            shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            shapeRenderer.setColor(0.14f, 0.16f, 0.17f, 1f);
            shapeRenderer.rect(bounds.x, bounds.y + bounds.height - 5f, bounds.width, 5f);
            shapeRenderer.setColor(0.58f * light, 0.72f * light, 0.72f, 0.92f);
            shapeRenderer.rect(bounds.x + 14f, bounds.y + bounds.height + 22f, bounds.width - 28f, 5f);
            shapeRenderer.rect(bounds.x + 14f, bounds.y + bounds.height + 42f, bounds.width - 28f, 4f);

            int posts = Math.max(3, (int) (bounds.width / 48f));
            for (int post = 0; post <= posts; post++) {
                float postX = bounds.x + 14f + post * ((bounds.width - 28f) / posts);
                shapeRenderer.setColor(0.38f, 0.46f, 0.47f, 1f);
                shapeRenderer.rect(postX, bounds.y + bounds.height + 10f, 5f, 42f);
            }

            shapeRenderer.setColor(0.034f, 0.038f, 0.044f, 1f);
            shapeRenderer.rect(bounds.x + 18f, bounds.y - 62f, 12f, 62f);
            shapeRenderer.rect(bounds.x + bounds.width - 30f, bounds.y - 62f, 12f, 62f);
            shapeRenderer.setColor(0.080f, 0.088f, 0.092f, 1f);
            shapeRenderer.rect(bounds.x + 34f, bounds.y - 34f, bounds.width - 68f, 8f);
        }
    }

    private void drawNeonBillboard(float x, float y, float width, float height, float neonR, float neonG, float neonB, float pulse) {
        shapeRenderer.setColor(0.040f, 0.050f, 0.072f, 1f);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.setColor(neonR * (0.75f + pulse * 0.25f), neonG, neonB, 0.92f);
        shapeRenderer.rect(x + 20f, y + height - 22f, width - 40f, 8f);
        shapeRenderer.rect(x + 24f, y + 26f, 14f, height - 56f);
        shapeRenderer.rect(x + width - 38f, y + 42f, 10f, height - 78f);
        shapeRenderer.setColor(0.84f, 0.90f, 0.95f, 0.92f);
        shapeRenderer.rect(x + 56f, y + 56f, width - 112f, 14f);
        shapeRenderer.rect(x + 56f, y + 88f, width * 0.45f, 10f);
    }

    private void renderBedroom() {
        shapeRenderer.setColor(0.010f, 0.010f, 0.012f, 1f);
        shapeRenderer.rect(0f, 0f, BEDROOM_RIGHT_WALL, Constants.WORLD_HEIGHT);
        shapeRenderer.rect(0f, BEDROOM_BACKGROUND_Y + BEDROOM_BACKGROUND_HEIGHT, BEDROOM_RIGHT_WALL, Constants.WORLD_HEIGHT);
        shapeRenderer.rect(0f, 0f, 44f, Constants.WORLD_HEIGHT);
        shapeRenderer.rect(BEDROOM_RIGHT_WALL - 44f, 0f, 44f, Constants.WORLD_HEIGHT);
        shapeRenderer.setColor(0.010f, 0.010f, 0.012f, 1f);
        shapeRenderer.rect(BEDROOM_RIGHT_WALL, 0f, Constants.WORLD_WIDTH - BEDROOM_RIGHT_WALL, Constants.WORLD_HEIGHT);
        shapeRenderer.setColor(0.05f, 0.04f, 0.035f, 0.55f);
        shapeRenderer.rect(0f, 0f, BEDROOM_RIGHT_WALL, 118f);
        shapeRenderer.setColor(0.010f, 0.010f, 0.012f, 1f);
        shapeRenderer.rect(BEDROOM_RIGHT_WALL, 0f, Constants.WORLD_WIDTH - BEDROOM_RIGHT_WALL, Constants.WORLD_HEIGHT);
    }

    private void renderBed() {
        float bedY = BEDROOM_BED.y;
        shapeRenderer.setColor(0.045f, 0.032f, 0.030f, 1f);
        shapeRenderer.rect(BEDROOM_BED.x - 16f, bedY - 18f, BEDROOM_BED.width + 32f, 22f);
        shapeRenderer.setColor(0.16f, 0.10f, 0.08f, 1f);
        shapeRenderer.rect(BEDROOM_BED.x, bedY, BEDROOM_BED.width, BEDROOM_BED.height);
        shapeRenderer.setColor(0.54f, 0.13f, 0.12f, 1f);
        shapeRenderer.rect(BEDROOM_BED.x + 22f, bedY + 14f, BEDROOM_BED.width - 44f, 28f);
        shapeRenderer.setColor(0.78f, 0.72f, 0.62f, 1f);
        shapeRenderer.rect(BEDROOM_BED.x + 18f, bedY + 36f, 70f, 26f);
        shapeRenderer.setColor(0.07f, 0.055f, 0.055f, 1f);
        shapeRenderer.rect(BEDROOM_BED.x + 6f, bedY + 56f, 16f, 92f);
        shapeRenderer.rect(BEDROOM_BED.x + BEDROOM_BED.width - 22f, bedY + 4f, 16f, 58f);
    }

    private void drawBackdrop(float r1, float g1, float b1, float r2, float g2, float b2) {
        shapeRenderer.setColor(r1, g1, b1, 1f);
        shapeRenderer.rect(0f, 0f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        shapeRenderer.setColor(r2, g2, b2, 1f);
        shapeRenderer.rect(0f, 360f, Constants.WORLD_WIDTH, 380f);
        shapeRenderer.setColor(0.018f, 0.023f, 0.028f, 1f);
        shapeRenderer.rect(0f, 720f, Constants.WORLD_WIDTH, 280f);
    }

    private void drawHolyWindows(float x, float y, float r, float g, float b) {
        shapeRenderer.setColor(r, g, b, 1f);
        shapeRenderer.rect(x, y, 170f, 18f);
        shapeRenderer.setColor(0.12f, 0.20f, 0.22f, 1f);
        shapeRenderer.rect(x + 10f, y - 92f, 150f, 92f);
        shapeRenderer.setColor(r + 0.12f, g + 0.12f, b + 0.10f, 1f);
        shapeRenderer.rect(x + 26f, y - 72f, 34f, 52f);
        shapeRenderer.rect(x + 88f, y - 72f, 34f, 52f);
        shapeRenderer.rect(x + 70f, y - 84f, 10f, 72f);
    }

    private void drawHangingScript() {
        shapeRenderer.setColor(0.72f, 0.07f, 0.07f, 1f);
        for (int i = 0; i < 10; i++) {
            float x = 170f + i * 245f;
            shapeRenderer.rect(x, 620f, 70f, 4f);
            shapeRenderer.rect(x + 8f, 602f, 50f, 3f);
            shapeRenderer.rect(x + 5f, 584f, 58f, 3f);
        }
    }

    private void drawPillars(float r, float g, float b) {
        shapeRenderer.setColor(r, g, b, 1f);
        for (int i = 0; i < 9; i++) {
            float x = i * 360f + 80f;
            shapeRenderer.rect(x, 150f, 54f, 500f);
            shapeRenderer.rect(x - 14f, 620f, 82f, 24f);
        }
    }

    private void drawRails(float r, float g, float b) {
        shapeRenderer.setColor(r, g, b, 1f);
        shapeRenderer.rect(0f, 205f, Constants.WORLD_WIDTH, 8f);
        shapeRenderer.rect(0f, 260f, Constants.WORLD_WIDTH, 6f);
        for (int i = 0; i < 42; i++) {
            shapeRenderer.rect(i * 78f, 205f, 8f, 60f);
        }
    }

    private void drawGround(float r, float g, float b, float sr, float sg, float sb) {
        shapeRenderer.setColor(r, g, b, 1f);
        shapeRenderer.rect(gameMap.getGround().x, gameMap.getGround().y, gameMap.getGround().width, gameMap.getGround().height);
        shapeRenderer.setColor(sr, sg, sb, 1f);
        shapeRenderer.rect(0f, 0f, Constants.WORLD_WIDTH, Constants.GROUND_Y);
        shapeRenderer.setColor(0.27f, 0.30f, 0.28f, 1f);
        for (int i = 0; i < 65; i++) {
            shapeRenderer.rect(i * 48f, Constants.GROUND_Y + Constants.GROUND_HEIGHT - 8f, 24f, 4f);
        }
    }

    private void renderPlatforms(float baseR, float baseG, float baseB, float accentR, float accentG, float accentB) {
        for (int i = 0; i < gameMap.getPlatforms().size; i++) {
            Platform platform = gameMap.getPlatforms().get(i);
            Rectangle bounds = platform.getBounds();
            shapeRenderer.setColor(0.04f, 0.015f, 0.015f, 0.65f);
            shapeRenderer.rect(bounds.x + 12f, bounds.y - 12f, bounds.width, bounds.height + 12f);

            float bodyR = (i % 3 == 0) ? 0.42f : (i % 3 == 1 ? baseR : 0.34f);
            float bodyG = (i % 3 == 0) ? 0.28f : (i % 3 == 1 ? baseG : 0.24f);
            float bodyB = (i % 3 == 0) ? 0.20f : (i % 3 == 1 ? baseB : 0.14f);
            float topInset = (i % 2 == 0) ? 14f : 28f;
            float crackWidth = Math.min(36f, bounds.width * 0.18f);

            shapeRenderer.setColor(bodyR, bodyG, bodyB, 1f);
            shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            shapeRenderer.setColor(accentR, accentG, accentB, 1f);
            shapeRenderer.rect(bounds.x + topInset, bounds.y + bounds.height - 4f, Math.max(18f, bounds.width - topInset * 1.5f), 4f);
            shapeRenderer.setColor(0.24f, 0.20f, 0.16f, 1f);
            shapeRenderer.rect(bounds.x + 6f, bounds.y + 4f, bounds.width - 12f, bounds.height - 10f);

            if (i % 2 == 0) {
                shapeRenderer.setColor(0.16f, 0.08f, 0.04f, 0.95f);
                shapeRenderer.rect(bounds.x + bounds.width * 0.48f, bounds.y + 3f, crackWidth, bounds.height - 6f);
            } else {
                shapeRenderer.setColor(0.62f, 0.26f, 0.10f, 0.90f);
                shapeRenderer.rect(bounds.x + 10f, bounds.y + 3f, 20f, bounds.height - 6f);
                shapeRenderer.rect(bounds.x + bounds.width - 30f, bounds.y + 3f, 14f, bounds.height - 6f);
            }
        }
    }

    private void renderScholar() {
        float bob = MathUtils.sin(time * 2.3f) * 3f;
        shapeRenderer.setColor(0.14f, 0.13f, 0.15f, 1f);
        shapeRenderer.rect(SCHOLAR_BOUNDS.x, SCHOLAR_BOUNDS.y + bob, SCHOLAR_BOUNDS.width, SCHOLAR_BOUNDS.height);
        shapeRenderer.setColor(0.68f, 0.78f, 0.72f, 1f);
        shapeRenderer.rect(SCHOLAR_BOUNDS.x + 8f, SCHOLAR_BOUNDS.y + 48f + bob, 26f, 22f);
        shapeRenderer.setColor(0.84f, 0.88f, 0.76f, 1f);
        shapeRenderer.rect(SCHOLAR_BOUNDS.x + 15f, SCHOLAR_BOUNDS.y + 58f + bob, 5f, 5f);
        shapeRenderer.rect(SCHOLAR_BOUNDS.x + 26f, SCHOLAR_BOUNDS.y + 58f + bob, 5f, 5f);
        shapeRenderer.setColor(0.50f, 0.70f, 0.64f, 1f);
        shapeRenderer.rect(SCHOLAR_BOUNDS.x - 8f, SCHOLAR_BOUNDS.y + 20f + bob, 10f, 46f);
    }

    private void renderDoor(Rectangle door, float r, float g, float b) {
        shapeRenderer.setColor(0.025f, 0.030f, 0.033f, 1f);
        shapeRenderer.rect(door.x - 14f, door.y - 6f, door.width + 28f, door.height + 22f);
        shapeRenderer.setColor(r, g, b, 1f);
        shapeRenderer.rect(door.x, door.y, door.width, door.height);
        shapeRenderer.setColor(0.78f, 0.89f, 0.80f, 1f);
        shapeRenderer.rect(door.x + door.width - 22f, door.y + 68f, 7f, 18f);
    }

    private void renderPlayer() {
        drawHero(player.getBounds().x, player.getBounds().y, 1f, player.getVelocityX(), true);
    }

    private void renderPlayerSprite() {
        if (heroFrames == null || heroFrames.length == 0) {
            return;
        }

        int frameIndex = getHeroFrameIndex();
        TextureRegion frame = heroFrames[frameIndex];
        boolean attackFrame = frameIndex >= 11;
        float drawWidth = attackFrame ? HERO_ATTACK_DRAW_WIDTH : HERO_BODY_DRAW_WIDTH;
        float drawHeight = HERO_DRAW_HEIGHT;

        Rectangle b = player.getBounds();
        float x = b.x + b.width * 0.5f - drawWidth * 0.5f;
        if (attackFrame) {
            x += player.isFacingRight() ? 18f : -18f;
        }
        float y = b.y - 4f;
        if (locationIndex >= 1 && locationIndex <= BossLocation.INDEX) {
            float locationScale = getLocationPlayerScale();
            drawWidth *= locationScale;
            drawHeight *= locationScale;
            x = b.x + b.width * 0.5f - drawWidth * 0.5f;
            if (attackFrame) {
                x += player.isFacingRight() ? 20f : -20f;
            }
            y = b.y - 4f;
        }
        boolean flip = player.isFacingRight();
        batch.draw(frame, flip ? x + drawWidth : x, y, flip ? -drawWidth : drawWidth, drawHeight);
    }

    private void renderInventoryHeroSprite() {
        if (!inventoryVisible || heroFrames == null || heroFrames.length == 0) {
            return;
        }

        float scale = getInventoryPanelScale();
        float panelX = getInventoryPanelX();
        float panelY = getInventoryPanelY();
        float areaX = panelX + INVENTORY_CHARACTER_LEFT * scale;
        float areaY = panelY + INVENTORY_CHARACTER_BOTTOM * scale;
        float areaWidth = INVENTORY_CHARACTER_WIDTH * scale;
        float areaHeight = INVENTORY_CHARACTER_HEIGHT * scale;

        int frameIndex = inventorySystem.getSelectedWeapon() != null ? 8 : 1;
        TextureRegion frame = heroFrames[frameIndex];
        boolean flip = player.isFacingRight();

        float drawHeight = areaHeight * 0.86f;
        float drawWidth = drawHeight * (frame.getRegionWidth() / (float) frame.getRegionHeight());
        float maxWidth = areaWidth * 0.78f;
        if (drawWidth > maxWidth) {
            float fitScale = maxWidth / drawWidth;
            drawWidth *= fitScale;
            drawHeight *= fitScale;
        }

        float drawX = areaX + areaWidth * 0.5f - drawWidth * 0.5f;
        float drawY = areaY + areaHeight * 0.06f;
        batch.setColor(1f, 1f, 1f, 0.96f);
        batch.draw(frame, flip ? drawX + drawWidth : drawX, drawY, flip ? -drawWidth : drawWidth, drawHeight);
        batch.setColor(Color.WHITE);
    }

    private int getHeroFrameIndex() {
        WeaponType weapon = inventorySystem.getSelectedWeapon();
        if (weapon != null && knifeSwingTimer > 0f) {
            float progress = 1f - knifeSwingTimer / weapon.getSwingDuration();
            return 11 + MathUtils.clamp((int)(progress * 6f), 0, 5);
        }

        if (!player.isOnGround()) {
            return Math.abs(player.getVelocityX()) > 5f ? 4 : (weapon != null ? 8 : 1);
        }

        if (Math.abs(player.getVelocityX()) > 5f) {
            int step = (int)(player.getAnimationTime() * 0.86f) % 4;
            return 4 + step;
        }

        if (weapon != null) {
            return 8 + (int)(player.getAnimationTime() * 0.45f) % 3;
        }
        return 1;
    }

    private void drawHero(float x, float y, float scale, float motion, boolean worldScale) {
        float speedRatio = MathUtils.clamp(Math.abs(motion) / Constants.PLAYER_SPEED, 0f, 1f);
        float animTime = worldScale ? player.getAnimationTime() : time * 4f;
        float walk = MathUtils.sin(animTime * 2.7f) * speedRatio;
        float counterWalk = MathUtils.sin(animTime * 2.7f + MathUtils.PI) * speedRatio;
        float breathe = MathUtils.sin(animTime * 0.9f) * (worldScale ? 1.0f : 2.2f);
        float jumpPose = worldScale && !player.isOnGround() ? MathUtils.clamp(player.getVelocityY() / Constants.PLAYER_JUMP_POWER, -1f, 1f) : 0f;
        float dir = player.isFacingRight() ? 1f : -1f;
        float s = scale;
        float hurtTint = worldScale && player.isRecentlyDamaged() ? 0.22f : 0f;
        WeaponType activeWeapon = inventorySystem.getSelectedWeapon();
        float attackPose = worldScale && activeWeapon != null && knifeSwingTimer > 0f
                ? 1f - knifeSwingTimer / activeWeapon.getSwingDuration()
                : 0f;
        float headY = y + 40f * s + breathe + jumpPose * 2f;
        float torsoY = y + 17f * s + breathe * 0.25f;

        shapeRenderer.setColor(0.010f + hurtTint * 0.5f, 0.012f, 0.014f, 1f);
        shapeRenderer.rect(x + 7f * s, torsoY + 5f * s, 26f * s, 27f * s);
        shapeRenderer.setColor(0.055f + hurtTint, 0.060f, 0.066f, 1f);
        shapeRenderer.rect(x + 10f * s, torsoY + 8f * s, 20f * s, 24f * s);
        shapeRenderer.setColor(0.15f, 0.17f, 0.18f, 1f);
        shapeRenderer.rect(x + 14f * s, torsoY + 13f * s, 5f * s, 10f * s);
        shapeRenderer.rect(x + 22f * s, torsoY + 13f * s, 5f * s, 10f * s);
        shapeRenderer.setColor(0.020f, 0.022f, 0.026f, 1f);
        shapeRenderer.rect(x + 7f * s, torsoY + 28f * s, 27f * s, 8f * s);
        shapeRenderer.rect(x + 5f * s, torsoY + 22f * s, 31f * s, 7f * s);

        shapeRenderer.setColor(0.090f, 0.095f, 0.105f, 1f);
        float attackLift = attackDirection == AttackDirection.UP ? 11f * attackPose : attackDirection == AttackDirection.DOWN ? -9f * attackPose : 0f;
        shapeRenderer.rect(x + (dir > 0 ? 2f : 31f) * s + dir * 7f * attackPose, y + (24f + walk * 3f + attackLift) * s, 7f * s, 20f * s);
        shapeRenderer.rect(x + (dir > 0 ? 31f : 2f) * s, y + (24f - walk * 2f) * s, 7f * s, 18f * s);
        shapeRenderer.setColor(0.88f, 0.78f, 0.63f, 1f);
        shapeRenderer.rect(x + (dir > 0 ? 3f : 32f) * s + dir * 11f * attackPose, y + (20f + walk * 3f + attackLift) * s, 6f * s, 5f * s);
        shapeRenderer.rect(x + (dir > 0 ? 32f : 3f) * s, y + (20f - walk * 2f) * s, 6f * s, 5f * s);

        shapeRenderer.setColor(0.070f, 0.075f, 0.082f, 1f);
        shapeRenderer.rect(x + (8f + walk * 7f) * s, y + (3f + jumpPose * 4f) * s, 9f * s, 16f * s);
        shapeRenderer.rect(x + (23f + counterWalk * 7f) * s, y + (3f - jumpPose * 2f) * s, 9f * s, 16f * s);
        shapeRenderer.setColor(0.030f, 0.034f, 0.038f, 1f);
        shapeRenderer.rect(x + (6f + walk * 7f) * s, y, 13f * s, 6f * s);
        shapeRenderer.rect(x + (21f + counterWalk * 7f) * s, y, 13f * s, 6f * s);

        shapeRenderer.setColor(0.92f + hurtTint, 0.80f - hurtTint * 0.25f, 0.62f - hurtTint * 0.25f, 1f);
        shapeRenderer.rect(x + 11f * s, headY, 19f * s, 16f * s);
        shapeRenderer.setColor(0.95f, 0.80f, 0.22f, 1f);
        shapeRenderer.rect(x + 8f * s, headY + 11f * s, 25f * s, 10f * s);
        shapeRenderer.rect(x + 6f * s, headY + 6f * s, 9f * s, 11f * s);
        shapeRenderer.rect(x + 26f * s, headY + 6f * s, 8f * s, 9f * s);
        shapeRenderer.setColor(0.70f, 0.54f, 0.12f, 1f);
        shapeRenderer.rect(x + (dir > 0 ? 27f : 9f) * s, headY + 16f * s, 7f * s, 4f * s);
        shapeRenderer.rect(x + (dir > 0 ? 9f : 27f) * s, headY + 13f * s, 5f * s, 4f * s);
        shapeRenderer.setColor(0.020f, 0.022f, 0.026f, 1f);
        shapeRenderer.rect(x + 10f * s, headY - 1f * s, 22f * s, 5f * s);
        shapeRenderer.setColor(0.54f, 0.82f, 0.92f, 1f);
        shapeRenderer.rect(x + (dir > 0 ? 23f : 13f) * s, headY + 6f * s, 4f * s, 4f * s);
        shapeRenderer.setColor(0.015f, 0.017f, 0.020f, 1f);
        shapeRenderer.rect(x + (dir > 0 ? 21f : 17f) * s, headY + 5f * s, 2f * s, 5f * s);
    }

    private void renderHud() {
        font.setColor(Color.WHITE);
        String place = LocationId.fromIndex(locationIndex).getDisplayName();
        font.draw(batch, place, 22, uiCamera.viewportHeight - 18f);
        font.draw(batch, "A/D \u0445\u043e\u0434\u044c\u0431\u0430 | SPACE \u043f\u0440\u044b\u0436\u043e\u043a | E \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u0435 | TAB \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044c", 22, uiCamera.viewportHeight - 44f);
        font.draw(batch, "W/S + \u041b\u041a\u041c: \u0443\u0434\u0430\u0440 \u0432\u0432\u0435\u0440\u0445/\u0432\u043d\u0438\u0437 | F/\u041f\u041a\u041c: \u043f\u0430\u0440\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u0435 | 1-2: \u043e\u0440\u0443\u0436\u0438\u0435", 22, uiCamera.viewportHeight - 70f);
        WeaponType selectedWeapon = inventorySystem.getSelectedWeapon();
        if (selectedWeapon != null) {
            font.draw(batch, "\u0412 \u0440\u0443\u043a\u0430\u0445: " + selectedWeapon.getDisplayName() + " | \u041b\u041a\u041c: \u0443\u0434\u0430\u0440", 22, uiCamera.viewportHeight - 96f);
        } else if (!inventorySystem.isEmpty()) {
            font.draw(batch, "\u0420\u0443\u043a\u0438 \u043f\u0443\u0441\u0442\u044b: \u0432\u044b\u0431\u0435\u0440\u0438 \u043e\u0440\u0443\u0436\u0438\u0435 \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435", 22, uiCamera.viewportHeight - 96f);
        }

        if (!dialogueVisible) {
            String prompt = null;
            if (locationIndex == 1 && isNear(player.getBounds(), BEDROOM_DOOR, 70f)) {
                prompt = !watchedBedroomTv
                        ? "\u0421\u043d\u0430\u0447\u0430\u043b\u0430 \u043f\u043e\u0441\u043c\u043e\u0442\u0440\u0438 \u0442\u0435\u043b\u0435\u0432\u0438\u0437\u043e\u0440"
                        : doorLockedUntilPlayerMoves
                        ? "\u041e\u0442\u043e\u0439\u0434\u0438 \u043e\u0442 \u0434\u0432\u0435\u0440\u0438 \u0438 \u0432\u0435\u0440\u043d\u0438\u0441\u044c"
                        : "\u041d\u0430\u0436\u043c\u0438 E, \u0447\u0442\u043e\u0431\u044b \u0432\u043e\u0439\u0442\u0438";
            }
            if (prompt == null && locationIndex == 1 && isNear(player.getBounds(), BEDROOM_TV, 52f)) {
                prompt = watchedBedroomTv
                        ? "\u041d\u0430\u0436\u043c\u0438 E, \u0447\u0442\u043e\u0431\u044b \u0432\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u0442\u0435\u043b\u0435\u0432\u0438\u0437\u043e\u0440"
                        : "\u041d\u0430\u0436\u043c\u0438 E, \u0447\u0442\u043e\u0431\u044b \u043f\u043e\u0441\u043c\u043e\u0442\u0440\u0435\u0442\u044c \u0442\u0435\u043b\u0435\u0432\u0438\u0437\u043e\u0440";
            }
            if (prompt == null && locationIndex == 3 && !knifeCollected && isNear(player.getBounds(), STREET_KNIFE_PICKUP, KNIFE_PICKUP_PROMPT_RANGE)) {
                prompt = "\u041d\u0430\u0436\u043c\u0438 E, \u0447\u0442\u043e\u0431\u044b \u043f\u043e\u0434\u043e\u0431\u0440\u0430\u0442\u044c \u043d\u043e\u0436";
            }
            if (prompt == null && locationIndex == 3 && player.getX() > STREET_RIGHT_WALL - 170f) {
                prompt = "\u0418\u0434\u0438 \u0432\u043f\u0440\u0430\u0432\u043e, \u0447\u0442\u043e\u0431\u044b \u0432\u043e\u0439\u0442\u0438 \u0432 \u041d\u0438\u0436\u043d\u0438\u0439 \u0433\u043e\u0440\u043e\u0434";
            }
            if (prompt == null && locationIndex == ShantyLocation.INDEX && player.getX() < SHANTY_LEFT_WALL + 180f) {
                prompt = hasAliveDungeonEnemies()
                        ? "\u0421\u043d\u0430\u0447\u0430\u043b\u0430 \u0443\u0431\u0435\u0439 \u0432\u0441\u0435\u0445 \u0432\u0440\u0430\u0433\u043e\u0432"
                        : "\u0418\u0434\u0438 \u0432\u043b\u0435\u0432\u043e, \u0447\u0442\u043e\u0431\u044b \u0432\u0435\u0440\u043d\u0443\u0442\u044c\u0441\u044f \u043d\u0430 \u0443\u043b\u0438\u0446\u0443";
            }
            if (prompt == null && locationIndex == ShantyLocation.INDEX && player.getX() > SHANTY_RIGHT_WALL - 180f) {
                prompt = hasAliveDungeonEnemies()
                        ? "\u0421\u043d\u0430\u0447\u0430\u043b\u0430 \u0443\u0431\u0435\u0439 \u0432\u0441\u0435\u0445 \u0432\u0440\u0430\u0433\u043e\u0432"
                        : "\u0418\u0434\u0438 \u0432\u043f\u0440\u0430\u0432\u043e, \u0447\u0442\u043e\u0431\u044b \u0432\u043e\u0439\u0442\u0438 \u0432 \u043b\u0430\u0431\u043e\u0440\u0430\u0442\u043e\u0440\u0438\u044e";
            }
            if (prompt == null && locationIndex == BossLocation.INDEX && player.getX() < BOSS_LEFT_WALL + 160f) {
                prompt = "\u041d\u0430\u0437\u0430\u0434 \u043f\u0443\u0442\u0438 \u043d\u0435\u0442";
            }
            if (prompt == null && (locationIndex == 1 || locationIndex == 2 || locationIndex == 3)) {
                Rectangle door = getActiveDoor();
                float promptRange = locationIndex == 3 ? 96f : 70f;
                if (isNear(player.getBounds(), door, promptRange)) {
                    prompt = doorLockedUntilPlayerMoves
                            ? "\u041e\u0442\u043e\u0439\u0434\u0438 \u043e\u0442 \u0434\u0432\u0435\u0440\u0438 \u0438 \u0432\u0435\u0440\u043d\u0438\u0441\u044c"
                            : "\u041d\u0430\u0436\u043c\u0438 E, \u0447\u0442\u043e\u0431\u044b \u0432\u043e\u0439\u0442\u0438";
                }
            }
            if (prompt != null) {
                glyphLayout.setText(font, prompt);
                font.draw(batch, prompt, uiCamera.viewportWidth * 0.5f - glyphLayout.width * 0.5f, BEDROOM_PROMPT_Y);
            }
        }
    }

    private void renderHealthBarPanel() {
        float barWidth = 260f;
        float barHeight = 28f;
        float x = uiCamera.viewportWidth - barWidth - 34f;
        float y = 54f;
        float healthPercent = MathUtils.clamp(player.getHealth() / (float) Constants.PLAYER_MAX_HEALTH, 0f, 1f);

        shapeRenderer.setColor(0.010f, 0.012f, 0.014f, 1f);
        shapeRenderer.rect(x - 6f, y - 6f, barWidth + 12f, barHeight + 12f);
        shapeRenderer.setColor(0.66f, 0.72f, 0.68f, 1f);
        shapeRenderer.rect(x - 3f, y - 3f, barWidth + 6f, barHeight + 6f);
        shapeRenderer.setColor(0.030f, 0.020f, 0.022f, 1f);
        shapeRenderer.rect(x, y, barWidth, barHeight);
        shapeRenderer.setColor(0.70f, 0.06f, 0.06f, 1f);
        shapeRenderer.rect(x + 3f, y + 3f, (barWidth - 6f) * healthPercent, barHeight - 6f);
        shapeRenderer.setColor(0.95f, 0.22f, 0.18f, 1f);
        shapeRenderer.rect(x + 3f, y + barHeight - 8f, (barWidth - 6f) * healthPercent, 4f);
    }

    private void renderHealthBar() {
        if (locationIndex != BossLocation.INDEX || finalBoss == null || !finalBoss.isAlive()) {
            return;
        }
        float barWidth = 420f;
        float x = uiCamera.viewportWidth * 0.5f - barWidth * 0.5f;
        font.setColor(0.90f, 0.96f, 0.90f, 1f);
        font.draw(batch, "\u0424\u0438\u043d\u0430\u043b\u044c\u043d\u044b\u0439 \u0431\u043e\u0441\u0441", x + 130f, uiCamera.viewportHeight - 22f);
        font.draw(batch, finalBoss.getHealth() + " / " + finalBoss.getMaxHealth(), x + 165f, uiCamera.viewportHeight - 64f);
    }

    private void renderBossHealthBarPanel() {
        if (locationIndex != BossLocation.INDEX || finalBoss == null || !finalBoss.isAlive()) {
            return;
        }

        float barWidth = 420f;
        float x = uiCamera.viewportWidth * 0.5f - barWidth * 0.5f;
        float y = uiCamera.viewportHeight - 56f;
        float healthPercent = MathUtils.clamp(finalBoss.getHealth() / (float) finalBoss.getMaxHealth(), 0f, 1f);

        shapeRenderer.setColor(0.010f, 0.012f, 0.014f, 0.92f);
        shapeRenderer.rect(x - 10f, y - 34f, barWidth + 20f, 28f);
        shapeRenderer.setColor(0.18f, 0.20f, 0.22f, 1f);
        shapeRenderer.rect(x, y - 28f, barWidth, 16f);
        shapeRenderer.setColor(0.74f, 0.10f, 0.10f, 1f);
        shapeRenderer.rect(x + 2f, y - 26f, (barWidth - 4f) * healthPercent, 12f);
        shapeRenderer.setColor(0.98f, 0.38f, 0.26f, 1f);
        shapeRenderer.rect(x + 2f, y - 18f, (barWidth - 4f) * healthPercent, 2f);
    }

    private void renderPrologueTitle() {
        if (!prologueActive || dialogueLine >= DialogueScripts.PROLOGUE_TITLES.length) {
            return;
        }

        font.setColor(0.96f, 0.86f, 0.72f, 1f);
        font.draw(batch, DialogueScripts.PROLOGUE_TITLES[dialogueLine], uiCamera.viewportWidth / 2f - 150f, uiCamera.viewportHeight - 54f);
        font.setColor(0.86f, 0.14f, 0.12f, 1f);
        font.draw(batch, "E - \u0434\u0430\u043b\u044c\u0448\u0435", uiCamera.viewportWidth - 132f, uiCamera.viewportHeight - 54f);
    }

    private void renderUiPanels() {
        if (!prologueActive) {
            renderHealthBarPanel();
            renderBossHealthBarPanel();
        }

        if (dialogueVisible) {
            float pulse = 0.5f + 0.5f * MathUtils.sin(time * 5f);
            shapeRenderer.setColor(0.018f, 0.022f, 0.024f, 1f);
            shapeRenderer.rect(64f, 48f, uiCamera.viewportWidth - 128f, 128f);
            shapeRenderer.setColor(0.42f, 0.59f, 0.54f, 1f);
            shapeRenderer.rect(64f, 170f, uiCamera.viewportWidth - 128f, 6f);
            renderDialoguePortraits(pulse);
        }

        if (tvScreenVisible) {
            renderTvOverlay();
        }
    }

    private void renderTvOverlay() {
        float panelWidth = uiCamera.viewportWidth - 200f;
        float panelHeight = uiCamera.viewportHeight - 140f;
        float x = (uiCamera.viewportWidth - panelWidth) * 0.5f;
        float y = (uiCamera.viewportHeight - panelHeight) * 0.5f;
        float staticPulse = 0.45f + 0.2f * MathUtils.sin(time * 22f);

        shapeRenderer.setColor(0f, 0f, 0f, 0.72f);
        shapeRenderer.rect(0f, 0f, uiCamera.viewportWidth, uiCamera.viewportHeight);
        shapeRenderer.setColor(0.12f, 0.12f, 0.12f, 1f);
        shapeRenderer.rect(x - 14f, y - 14f, panelWidth + 28f, panelHeight + 28f);
        shapeRenderer.setColor(0.72f, 0.72f, 0.72f, 1f);
        shapeRenderer.rect(x, y, panelWidth, panelHeight);
        shapeRenderer.setColor(staticPulse, staticPulse, staticPulse, 1f);
        shapeRenderer.rect(x + 8f, y + 8f, panelWidth - 16f, panelHeight - 16f);
        shapeRenderer.setColor(0.22f, 0.22f, 0.22f, 0.45f);
        for (int i = 0; i < 18; i++) {
            float lineY = y + 18f + i * ((panelHeight - 36f) / 18f);
            shapeRenderer.rect(x + 8f, lineY, panelWidth - 16f, 3f);
        }
        float progressWidth = panelWidth - 120f;
        float progress = MathUtils.clamp(tvWatchTimer / TV_REQUIRED_WATCH_TIME, 0f, 1f);
        shapeRenderer.setColor(0.08f, 0.08f, 0.08f, 1f);
        shapeRenderer.rect(x + 60f, y + 22f, progressWidth, 16f);
        shapeRenderer.setColor(0.58f, 0.80f, 0.72f, 1f);
        shapeRenderer.rect(x + 60f, y + 22f, progressWidth * progress, 16f);
    }

    private void renderPauseMenuPanels() {
        if (!pauseMenuVisible) {
            return;
        }

        float uiWidth = uiCamera.viewportWidth;
        float uiHeight = uiCamera.viewportHeight;
        float panelX = uiWidth * 0.5f - 230f;
        float panelY = uiHeight * 0.5f - 190f;
        float pulse = 0.5f + 0.5f * MathUtils.sin(time * 3.2f);

        shapeRenderer.setColor(0.004f, 0.004f, 0.006f, 0.48f);
        shapeRenderer.rect(0f, 0f, uiWidth, uiHeight);
        shapeRenderer.setColor(0.018f, 0.022f, 0.026f, 0.78f);
        shapeRenderer.rect(panelX, panelY, 460f, 420f);
        shapeRenderer.setColor(0.48f, 0.68f, 0.62f, 0.82f);
        shapeRenderer.rect(panelX, panelY + 410f, 460f, 6f);
        shapeRenderer.setColor(0.68f, 0.12f + pulse * 0.08f, 0.12f, 0.74f);
        shapeRenderer.rect(panelX, panelY, 460f, 4f);

        for (int i = 0; i < pauseButtonBounds.length; i++) {
            Rectangle bounds = pauseButtonBounds[i];
            boolean hovered = i == hoveredPauseButton;
            boolean selected = (i == 1 && pausePanel == PausePanel.ACHIEVEMENTS)
                    || (i == 2 && pausePanel == PausePanel.SETTINGS);

            shapeRenderer.setColor(0.004f, 0.005f, 0.006f, 0.52f);
            shapeRenderer.rect(bounds.x - 4f, bounds.y - 4f, bounds.width + 8f, bounds.height + 8f);
            if (hovered) {
                shapeRenderer.setColor(0.62f, 0.18f, 0.16f, 0.88f);
            } else if (selected) {
                shapeRenderer.setColor(0.20f, 0.46f, 0.42f, 0.86f);
            } else {
                shapeRenderer.setColor(0.075f, 0.085f, 0.090f, 0.76f);
            }
            shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            shapeRenderer.setColor(hovered ? 0.96f : 0.36f, hovered ? 0.78f : 0.58f, hovered ? 0.66f : 0.54f, 0.92f);
            shapeRenderer.rect(bounds.x, bounds.y + bounds.height - 5f, bounds.width, 5f);
        }

        if (pausePanel != PausePanel.NONE) {
            shapeRenderer.setColor(0.030f, 0.038f, 0.040f, 0.76f);
            shapeRenderer.rect(panelX + 48f, panelY + 22f, 364f, 94f);
            shapeRenderer.setColor(0.15f, 0.30f, 0.28f, 0.84f);
            shapeRenderer.rect(panelX + 48f, panelY + 110f, 364f, 4f);
        }
    }

    private void renderPauseMenuText() {
        if (!pauseMenuVisible) {
            return;
        }

        float uiWidth = uiCamera.viewportWidth;
        float uiHeight = uiCamera.viewportHeight;
        font.setColor(0.94f, 0.90f, 0.82f, 1f);
        font.draw(batch, "\u041f\u0430\u0443\u0437\u0430", uiWidth * 0.5f - 34f, uiHeight * 0.5f + 204f);

        for (int i = 0; i < pauseButtonBounds.length; i++) {
            Rectangle bounds = pauseButtonBounds[i];
            font.setColor(i == hoveredPauseButton ? Color.WHITE : new Color(0.86f, 0.90f, 0.86f, 1f));
            font.draw(batch, getPauseButtonLabel(i), bounds.x + 34f, bounds.y + 35f);
        }

        if (pausePanel == PausePanel.ACHIEVEMENTS) {
            font.setColor(0.90f, 0.94f, 0.88f, 1f);
            font.draw(batch, "\u0414\u043e\u0441\u0442\u0438\u0436\u0435\u043d\u0438\u044f", uiWidth * 0.5f - 78f, uiHeight * 0.5f - 94f);
            font.setColor(0.70f, 0.78f, 0.74f, 1f);
            font.draw(batch, "\u041f\u0430\u043b\u043e\u043c\u043d\u0438\u043a | \u041e\u0441\u043a\u043e\u043b\u043e\u043a \u0441\u0432\u0435\u0442\u0430 | \u041f\u043e\u0434\u0437\u0435\u043c\u043d\u044b\u0439 \u0437\u043e\u0432", uiWidth * 0.5f - 184f, uiHeight * 0.5f - 126f);
            font.draw(batch, "\u041f\u0440\u043e\u0433\u0440\u0435\u0441\u0441 \u0431\u0443\u0434\u0435\u0442 \u043e\u0442\u043c\u0435\u0447\u0430\u0442\u044c\u0441\u044f \u043f\u043e \u0445\u043e\u0434\u0443 \u0438\u0433\u0440\u044b.", uiWidth * 0.5f - 168f, uiHeight * 0.5f - 154f);
        } else if (pausePanel == PausePanel.SETTINGS) {
            font.setColor(0.90f, 0.94f, 0.88f, 1f);
            font.draw(batch, "\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438", uiWidth * 0.5f - 68f, uiHeight * 0.5f - 94f);
            font.setColor(0.70f, 0.78f, 0.74f, 1f);
            font.draw(batch, "\u0420\u0430\u0437\u0434\u0435\u043b \u0432 \u0440\u0430\u0437\u0440\u0430\u0431\u043e\u0442\u043a\u0435.", uiWidth * 0.5f - 102f, uiHeight * 0.5f - 126f);
            font.draw(batch, "\u041d\u0430\u0436\u043c\u0438\u0442\u0435 Escape \u0438\u043b\u0438 \u041f\u0440\u043e\u0434\u043e\u043b\u0436\u0438\u0442\u044c, \u0447\u0442\u043e\u0431\u044b \u0432\u0435\u0440\u043d\u0443\u0442\u044c\u0441\u044f.", uiWidth * 0.5f - 208f, uiHeight * 0.5f - 154f);
        }
    }

    private String getPauseButtonLabel(int index) {
        if (index == 3) {
            return "\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c";
        }
        if (index == 4) {
            return "\u041f\u0440\u043e\u0434\u043e\u043b\u0436\u0438\u0442\u044c";
        }
        return PAUSE_BUTTON_LABELS[index];
    }

    private void renderInventoryPanelTextures() {
        WeaponType[] handWeapons = inventorySystem.getWeaponSlots();
        for (int i = 0; i < handWeapons.length; i++) {
            if (handWeapons[i] != null) {
                Rectangle slot = getHandSlotBounds(i);
                float iconSize = Math.min(slot.width, slot.height) * 0.74f;
                float iconX = slot.x + (slot.width - iconSize) * 0.5f;
                float iconY = slot.y + (slot.height - iconSize) * 0.5f;
                drawWeaponTextureIcon(handWeapons[i], iconX, iconY, iconSize);
            }
        }

        WeaponType[] bagWeapons = inventorySystem.getBagSlots();
        for (int i = 0; i < bagWeapons.length; i++) {
            if (bagWeapons[i] != null) {
                Rectangle slot = getBagSlotBounds(i);
                float iconSize = slot.width * 0.68f;
                float iconX = slot.x + (slot.width - iconSize) * 0.5f;
                float iconY = slot.y + (slot.height - iconSize) * 0.5f;
                drawWeaponTextureIcon(bagWeapons[i], iconX, iconY, iconSize);
            }
        }

        if (draggedWeapon != null) {
            pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
            hudViewport.unproject(pointer);
            drawWeaponTextureIcon(draggedWeapon, pointer.x - 24f, pointer.y - 24f, 48f);
        }
    }

    private void renderInventoryPanelFallback() {
        WeaponType[] handWeapons = inventorySystem.getWeaponSlots();
        for (int i = 0; i < handWeapons.length; i++) {
            if (handWeapons[i] != null && getWeaponIconTexture(handWeapons[i]) == null) {
                Rectangle slot = getHandSlotBounds(i);
                float iconSize = Math.min(slot.width, slot.height) * 0.74f;
                float iconX = slot.x + (slot.width - iconSize) * 0.5f;
                float iconY = slot.y + (slot.height - iconSize) * 0.5f;
                drawWeaponIcon(handWeapons[i], iconX, iconY, iconSize, true);
            }
        }

        WeaponType[] bagWeapons = inventorySystem.getBagSlots();
        for (int i = 0; i < bagWeapons.length; i++) {
            if (bagWeapons[i] != null && getWeaponIconTexture(bagWeapons[i]) == null) {
                Rectangle slot = getBagSlotBounds(i);
                float iconSize = slot.width * 0.68f;
                float iconX = slot.x + (slot.width - iconSize) * 0.5f;
                float iconY = slot.y + (slot.height - iconSize) * 0.5f;
                drawWeaponIcon(bagWeapons[i], iconX, iconY, iconSize, false);
            }
        }

        if (draggedWeapon != null && getWeaponIconTexture(draggedWeapon) == null) {
            pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
            hudViewport.unproject(pointer);
            drawWeaponIcon(draggedWeapon, pointer.x - 24f, pointer.y - 24f, 48f, true);
        }
    }

    private void drawWeaponTextureIcon(WeaponType weapon, float x, float y, float size) {
        Texture texture = getWeaponIconTexture(weapon);
        if (texture == null) {
            return;
        }

        float sourceX = 0f;
        float sourceY = 0f;
        float sourceWidth = texture.getWidth();
        float sourceHeight = texture.getHeight();
        float drawScale = 0.92f;
        float offsetX = 0f;
        float offsetY = 0f;
        float rotation = 0f;

        if (weapon == WeaponType.BASIC_KNIFE) {
            // Center the visible knife sprite exactly inside the slot and let it nearly fill the cell.
            sourceX = 10f;
            sourceY = 8f;
            sourceWidth = 44f;
            sourceHeight = 48f;
            drawScale = 0.96f;
            offsetX = 0f;
            offsetY = 0f;
            rotation = 0f;
        }

        float drawBoxSize = size * drawScale;
        float scale = Math.min(drawBoxSize / sourceWidth, drawBoxSize / sourceHeight);
        float drawWidth = sourceWidth * scale;
        float drawHeight = sourceHeight * scale;
        float drawX = x + (size - drawWidth) * 0.5f + offsetX;
        float drawY = y + (size - drawHeight) * 0.5f + offsetY;
        batch.setColor(Color.WHITE);
        batch.draw(texture,
                drawX,
                drawY,
                drawWidth * 0.5f,
                drawHeight * 0.5f,
                drawWidth,
                drawHeight,
                1f,
                1f,
                rotation,
                (int) sourceX,
                (int) sourceY,
                (int) sourceWidth,
                (int) sourceHeight,
                false,
                false);
    }

    private Texture getWeaponIconTexture(WeaponType weapon) {
        if (weapon == null || missingWeaponIconTextures.contains(weapon)) {
            return null;
        }

        Texture texture = weaponIconTextures.get(weapon);
        if (texture != null) {
            return texture;
        }

        if (!Gdx.files.internal(weapon.getIconAssetPath()).exists()) {
            missingWeaponIconTextures.add(weapon);
            return null;
        }

        texture = new Texture(Gdx.files.internal(weapon.getIconAssetPath()));
        weaponIconTextures.put(weapon, texture);
        return texture;
    }

    private void drawWeaponIcon(WeaponType weapon, float x, float y, float size, boolean bright) {
        float glow = bright ? 1f : 0.72f;
        float bladeR = weapon.getColorR() * glow;
        float bladeG = weapon.getColorG() * glow;
        float bladeB = weapon.getColorB() * glow;
        float handleR = 0.16f;
        float handleG = 0.10f;
        float handleB = 0.08f;
        float accentAlpha = bright ? 0.86f : 0.62f;

        switch (weapon) {
            case BASIC_KNIFE:
                shapeRenderer.setColor(handleR, handleG, handleB, 1f);
                shapeRenderer.rectLine(x + size * 0.16f, y + size * 0.18f, x + size * 0.34f, y + size * 0.34f, size * 0.15f);
                shapeRenderer.setColor(bladeR, bladeG, bladeB, 1f);
                shapeRenderer.rectLine(x + size * 0.30f, y + size * 0.30f, x + size * 0.74f, y + size * 0.78f, size * 0.10f);
                shapeRenderer.setColor(0.94f, 0.96f, 0.88f, accentAlpha);
                shapeRenderer.rectLine(x + size * 0.40f, y + size * 0.40f, x + size * 0.67f, y + size * 0.70f, size * 0.028f);
                break;
            case RUSTY_SWORD:
            case IRON_SWORD:
                shapeRenderer.setColor(handleR, handleG, handleB, 1f);
                shapeRenderer.rectLine(x + size * 0.18f, y + size * 0.18f, x + size * 0.34f, y + size * 0.34f, size * 0.11f);
                shapeRenderer.rectLine(x + size * 0.24f, y + size * 0.27f, x + size * 0.42f, y + size * 0.21f, size * 0.08f);
                shapeRenderer.setColor(bladeR, bladeG, bladeB, 1f);
                shapeRenderer.rectLine(x + size * 0.32f, y + size * 0.32f, x + size * 0.79f, y + size * 0.80f,
                        weapon == WeaponType.IRON_SWORD ? size * 0.14f : size * 0.12f);
                shapeRenderer.setColor(0.94f, 0.96f, 0.88f, accentAlpha);
                shapeRenderer.rectLine(x + size * 0.40f, y + size * 0.40f, x + size * 0.72f, y + size * 0.72f, size * 0.035f);
                break;
            case HEAVY_AXE:
                shapeRenderer.setColor(handleR, handleG, handleB, 1f);
                shapeRenderer.rectLine(x + size * 0.24f, y + size * 0.16f, x + size * 0.70f, y + size * 0.72f, size * 0.10f);
                shapeRenderer.setColor(bladeR, bladeG, bladeB, 1f);
                shapeRenderer.triangle(
                        x + size * 0.50f, y + size * 0.58f,
                        x + size * 0.86f, y + size * 0.84f,
                        x + size * 0.66f, y + size * 0.42f);
                shapeRenderer.setColor(0.94f, 0.96f, 0.88f, accentAlpha);
                shapeRenderer.rectLine(x + size * 0.61f, y + size * 0.61f, x + size * 0.76f, y + size * 0.72f, size * 0.03f);
                break;
            case SPEAR:
                shapeRenderer.setColor(handleR * 1.15f, handleG * 1.15f, handleB * 1.15f, 1f);
                shapeRenderer.rectLine(x + size * 0.16f, y + size * 0.20f, x + size * 0.78f, y + size * 0.82f, size * 0.065f);
                shapeRenderer.setColor(bladeR, bladeG, bladeB, 1f);
                shapeRenderer.triangle(
                        x + size * 0.68f, y + size * 0.72f,
                        x + size * 0.90f, y + size * 0.92f,
                        x + size * 0.78f, y + size * 0.60f);
                shapeRenderer.setColor(0.94f, 0.96f, 0.88f, accentAlpha);
                shapeRenderer.rectLine(x + size * 0.72f, y + size * 0.74f, x + size * 0.84f, y + size * 0.84f, size * 0.026f);
                break;
            default:
                shapeRenderer.setColor(bladeR, bladeG, bladeB, 1f);
                shapeRenderer.rectLine(x + 7f, y + 6f, x + size - 5f, y + size - 8f, 5f);
                shapeRenderer.setColor(handleR, handleG, handleB, 1f);
                shapeRenderer.rectLine(x + 2f, y + 3f, x + 12f, y + 13f, 6f);
                shapeRenderer.setColor(0.94f, 0.96f, 0.88f, accentAlpha);
                shapeRenderer.circle(x + size - 5f, y + size - 8f, 3f, 10);
                break;
        }
    }

    private void renderDialoguePortraits(float pulse) {
        boolean adamSpeaking = "???".equals(currentSpeakers[dialogueLine]);
        boolean bookSpeaking = "\u0421\u0432\u044f\u0449\u0435\u043d\u043d\u0430\u044f \u043a\u043d\u0438\u0433\u0430".equals(currentSpeakers[dialogueLine]);
        boolean engineSpeaking = "\u0410\u043d\u0433\u0435\u043b\u044c\u0441\u043a\u0438\u0439 \u0434\u0432\u0438\u0433\u0430\u0442\u0435\u043b\u044c".equals(currentSpeakers[dialogueLine]);
        float leftLift = adamSpeaking ? 8f + pulse * 4f : 0f;
        float rightLift = !adamSpeaking ? 8f + pulse * 4f : 0f;

        shapeRenderer.setColor(adamSpeaking ? 0.16f : 0.08f, adamSpeaking ? 0.22f : 0.10f, adamSpeaking ? 0.21f : 0.10f, 1f);
        shapeRenderer.rect(82f, 66f + leftLift, 74f, 88f);
        drawHero(100f, 80f + leftLift, 1.25f, adamSpeaking ? 45f : 0f, false);

        shapeRenderer.setColor(adamSpeaking ? 0.08f : 0.16f, adamSpeaking ? 0.10f : 0.22f, adamSpeaking ? 0.10f : 0.21f, 1f);
        shapeRenderer.rect(uiCamera.viewportWidth - 156f, 66f + rightLift, 74f, 88f);
        if (bookSpeaking) {
            drawBookPortrait(uiCamera.viewportWidth - 140f, 84f + rightLift);
        } else if (engineSpeaking) {
            drawEnginePortrait(uiCamera.viewportWidth - 140f, 82f + rightLift, pulse);
        } else {
            drawScholarPortrait(uiCamera.viewportWidth - 140f, 82f + rightLift);
        }
    }

    private void drawBookPortrait(float x, float y) {
        shapeRenderer.setColor(0.22f, 0.20f, 0.16f, 1f);
        shapeRenderer.rect(x, y, 42f, 58f);
        shapeRenderer.setColor(0.65f, 0.76f, 0.68f, 1f);
        shapeRenderer.rect(x + 18f, y + 8f, 6f, 42f);
        shapeRenderer.rect(x + 8f, y + 27f, 26f, 5f);
    }

    private void drawEnginePortrait(float x, float y, float pulse) {
        shapeRenderer.setColor(0.10f, 0.15f, 0.15f, 1f);
        shapeRenderer.rect(x + 2f, y, 38f, 62f);
        shapeRenderer.setColor(0.54f + pulse * 0.18f, 0.78f, 0.72f, 1f);
        shapeRenderer.rect(x + 18f, y + 8f, 6f, 46f);
        shapeRenderer.rect(x + 8f, y + 30f, 26f, 5f);
    }

    private void drawScholarPortrait(float x, float y) {
        shapeRenderer.setColor(0.13f, 0.16f, 0.17f, 1f);
        shapeRenderer.rect(x + 4f, y, 34f, 58f);
        shapeRenderer.setColor(0.62f, 0.76f, 0.70f, 1f);
        shapeRenderer.rect(x + 10f, y + 40f, 22f, 18f);
        shapeRenderer.setColor(0.86f, 0.91f, 0.82f, 1f);
        shapeRenderer.rect(x + 16f, y + 50f, 4f, 4f);
        shapeRenderer.rect(x + 26f, y + 50f, 4f, 4f);
    }

    private void renderDialogue() {
        if (!dialogueVisible) {
            return;
        }

        float width = uiCamera.viewportWidth;
        String speaker = currentSpeakers[dialogueLine];
        String visibleText = currentText[dialogueLine].substring(0, getVisibleDialogueCharacters());
        font.setColor(0.90f, 0.94f, 0.88f, 1f);
        font.draw(batch, speaker + ":", 176f, 146f);
        drawWrappedText(visibleText, 176f, 116f, width - 350f, 26f, 4);
        font.setColor(0.62f, 0.78f, 0.72f, 1f);
        font.draw(batch, "E", width - 116f, 76f);
        font.draw(batch, getVisibleDialogueCharacters() < currentText[dialogueLine].length() ? "\u043f\u0440\u043e\u043f\u0443\u0441\u043a" : "\u0434\u0430\u043b\u044c\u0448\u0435", width - 96f, 76f);
    }

    private void renderTvOverlayText() {
        if (!tvScreenVisible) {
            return;
        }

        float centerX = uiCamera.viewportWidth * 0.5f;
        float centerY = uiCamera.viewportHeight * 0.5f;
        font.setColor(0.08f, 0.08f, 0.08f, 1f);
        font.draw(batch, "\u0421\u0442\u0430\u0442\u0438\u043a\u0430...", centerX - 56f, centerY + 8f);
        if (tvWatchTimer < TV_REQUIRED_WATCH_TIME) {
            font.draw(batch, "\u0421\u043c\u043e\u0442\u0440\u0438 \u044d\u043a\u0440\u0430\u043d", centerX - 78f, centerY - 28f);
        } else {
            font.draw(batch, "\u041f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0435\u043d\u043e. \u041d\u0430\u0436\u043c\u0438 E", centerX - 114f, centerY - 28f);
        }
    }

    private void drawWrappedText(String text, float x, float y, float maxWidth, float lineHeight, int maxLines) {
        String[] words = text.split(" ");
        StringBuilder lineText = new StringBuilder();
        int line = 0;

        for (String word : words) {
            String candidate = lineText.length() == 0 ? word : lineText + " " + word;
            glyphLayout.setText(font, candidate);
            if (glyphLayout.width > maxWidth && lineText.length() > 0) {
                font.draw(batch, lineText.toString(), x, y - line * lineHeight);
                line++;
                if (line >= maxLines) {
                    return;
                }
                lineText.setLength(0);
                lineText.append(word);
            } else {
                lineText.setLength(0);
                lineText.append(candidate);
            }
        }

        if (lineText.length() > 0 && line < maxLines) {
            font.draw(batch, lineText.toString(), x, y - line * lineHeight);
        }
    }

    private int getVisibleDialogueCharacters() {
        return Math.min(currentText[dialogueLine].length(), (int)(dialogueTypeTimer * TYPE_SPEED));
    }

    private boolean isNear(Rectangle a, Rectangle b, float padding) {
        return a.x + a.width > b.x - padding &&
                a.x < b.x + b.width + padding &&
                a.y + a.height > b.y - padding &&
                a.y < b.y + b.height + padding;
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height, false);
        hudViewport.update(width, height, true);
        setupPauseMenuBounds();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
        if (inventoryUI != null) {
            inventoryUI.dispose();
        }
        if (streetTexture != null) {
            streetTexture.dispose();
        }
        if (shantyTexture != null) {
            shantyTexture.dispose();
        }
        if (fogPixelTexture != null) {
            fogPixelTexture.dispose();
        }
        if (shantyFogTexture != null) {
            shantyFogTexture.dispose();
        }
        if (bossTexture != null) {
            bossTexture.dispose();
        }
        if (hallwayTexture != null) {
            hallwayTexture.dispose();
        }
        if (bedroomTexture != null) {
            bedroomTexture.dispose();
        }
        if (bedroomTvStaticFrames != null) {
            for (Texture tvStaticFrame : bedroomTvStaticFrames) {
                if (tvStaticFrame != null) {
                    tvStaticFrame.dispose();
                }
            }
            bedroomTvStaticFrames = null;
        }
        if (heroTexture != null) {
            heroTexture.dispose();
        }
        if (flyingEnemyTexture != null) {
            flyingEnemyTexture.dispose();
        }
        if (flyingSlasherTexture != null) {
            flyingSlasherTexture.dispose();
        }
        if (flyingEnemyProjectileTexture != null) {
            flyingEnemyProjectileTexture.dispose();
        }
        for (Texture weaponIconTexture : weaponIconTextures.values()) {
            if (weaponIconTexture != null) {
                weaponIconTexture.dispose();
            }
        }
        weaponIconTextures.clear();
        missingWeaponIconTextures.clear();
        FinalBoss.disposeAssets();
    }

    private static final class EnemyProjectile {
        private final Rectangle bounds = new Rectangle();
        private float velocityX;
        private float velocityY;
        private final int damage;
        private float rotation;
        private boolean reflected;
        private float lifeTimer = FLYING_ENEMY_PROJECTILE_LIFETIME;

        private EnemyProjectile(float centerX, float centerY, float velocityX, float velocityY, int damage) {
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.damage = damage;
            this.rotation = (float)Math.toDegrees(Math.atan2(velocityY, velocityX));
            this.bounds.set(
                    centerX - FLYING_ENEMY_PROJECTILE_DRAW_WIDTH * 0.5f,
                    centerY - FLYING_ENEMY_PROJECTILE_DRAW_HEIGHT * 0.5f,
                    FLYING_ENEMY_PROJECTILE_DRAW_WIDTH,
                    FLYING_ENEMY_PROJECTILE_DRAW_HEIGHT
            );
        }

        private void update(float delta) {
            bounds.x += velocityX * delta;
            bounds.y += velocityY * delta;
            lifeTimer -= delta;
        }
    }

    private static final class FloatingDamageText {
        private final String value;
        private final boolean critical;
        private float x;
        private float y;
        private float timer = DAMAGE_TEXT_LIFETIME;
        private final float driftX;

        private FloatingDamageText(float x, float y, int damage, boolean critical, int index) {
            this.x = x;
            this.y = y;
            this.value = Integer.toString(damage);
            this.critical = critical;
            this.driftX = ((index & 1) == 0 ? -1f : 1f) * (critical ? 16f : 10f);
        }

        private void update(float delta) {
            timer = Math.max(0f, timer - delta);
            float normalized = 1f - timer / DAMAGE_TEXT_LIFETIME;
            x += driftX * delta;
            y += (critical ? 78f : 60f) * delta + MathUtils.sin(normalized * MathUtils.PI) * 10f * delta;
        }

        private float getAlpha() {
            return MathUtils.clamp(timer / DAMAGE_TEXT_LIFETIME, 0f, 1f);
        }

        private float getPop() {
            return MathUtils.sin((1f - timer / DAMAGE_TEXT_LIFETIME) * MathUtils.PI);
        }

        private boolean isFinished() {
            return timer <= 0f;
        }
    }

    private enum PausePanel {
        NONE,
        ACHIEVEMENTS,
        SETTINGS
    }

    private enum InventoryDragSource {
        NONE,
        HAND,
        BAG
    }

    private enum AttackDirection {
        FORWARD,
        UP,
        DOWN
    }
}
