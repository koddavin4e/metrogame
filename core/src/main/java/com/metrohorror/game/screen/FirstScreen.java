package com.metrohorror.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.metrohorror.game.MetroHorrorGame;
import com.metrohorror.game.entities.Enemy;
import com.metrohorror.game.entities.Player;
import com.metrohorror.game.entities.WeaponType;
import com.metrohorror.game.screen.dialogue.DialogueScripts;
import com.metrohorror.game.screen.location.LocationId;
import com.metrohorror.game.systems.CameraSystem;
import com.metrohorror.game.systems.InventorySystem;
import com.metrohorror.game.ui.InventoryUI;
import com.metrohorror.game.util.Constants;
import com.metrohorror.game.world.DungeonMap;
import com.metrohorror.game.world.GameMap;
import com.metrohorror.game.world.Platform;

public class FirstScreen implements Screen {
    private static final float VIRTUAL_WIDTH = 1280f;
    private static final float VIRTUAL_HEIGHT = 720f;
    private static final Rectangle SCHOLAR_BOUNDS = new Rectangle(610f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 42f, 72f);
    private static final Rectangle CHAPEL_BEDROOM_DOOR = new Rectangle(82f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 78f, 140f);
    private static final Rectangle CHAPEL_DOOR = new Rectangle(2380f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 78f, 140f);
    private static final Rectangle ENGINE_RETURN_DOOR = new Rectangle(140f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 78f, 140f);
    private static final Rectangle ENGINE_DUNGEON_DOOR = new Rectangle(2650f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 78f, 140f);
    private static final Rectangle DUNGEON_RETURN_DOOR = new Rectangle(140f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 78f, 140f);
    private static final Rectangle TRASH_PILE = new Rectangle(2460f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 112f, 58f);
    private static final Rectangle BEDROOM_BED = new Rectangle(135f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 230f, 58f);
    private static final Rectangle BEDROOM_DOOR = new Rectangle(1088f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 78f, 140f);
    private static final float BEDROOM_RIGHT_WALL = 1240f;
    private static final float BEDROOM_PLAYER_X = 235f;
    private static final float TYPE_SPEED = 42f;
    private static final int KNIFE_DAMAGE = 2;
    private static final int KILL_HEAL_AMOUNT = 5;
    private static final float DOWN_ATTACK_BOUNCE = 520f;
    private static final float RESPAWN_X_OFFSET = 120f;
    private static final float PAUSE_MENU_WIDTH = 360f;
    private static final float PAUSE_BUTTON_HEIGHT = 54f;
    private static final float PAUSE_BUTTON_GAP = 14f;
    private static final float INVENTORY_PANEL_WIDTH = 700f;
    private static final float INVENTORY_PANEL_HEIGHT = 456f;
    private static final float INVENTORY_SLOT_SIZE = 50f;
    private static final float INVENTORY_SLOT_GAP = 10f;
    private static final int INVENTORY_BAG_COLUMNS = 4;
    private static final int HERO_FRAME_SIZE = 128;
    private static final int HERO_FRAME_COUNT = 17;
    private static final float HERO_BODY_DRAW_WIDTH = 88f;
    private static final float HERO_ATTACK_DRAW_WIDTH = 136f;
    private static final float HERO_DRAW_HEIGHT = 96f;
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
    private TextureRegion[] heroFrames;
    private final MetroHorrorGame game;
    private final int loadSlot;
    private final Rectangle[] pauseButtonBounds = new Rectangle[5];
    private final Vector3 pointer = new Vector3();

    private Player player;
    private Array<Enemy> dungeonEnemies;
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
    private boolean dialogueVisible = true;
    private boolean prologueActive = true;
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
    private boolean lyingInBed;
    private boolean pauseMenuVisible;
    private int hoveredPauseButton = -1;
    private PausePanel pausePanel = PausePanel.NONE;

    private String[] currentSpeakers = DialogueScripts.PROLOGUE_SPEAKERS;
    private String[] currentText = DialogueScripts.PROLOGUE_TEXT;

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
        worldViewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        uiCamera = new OrthographicCamera();
        hudViewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, uiCamera);
        worldViewport.apply(true);
        hudViewport.apply(true);

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = createGameFont();
        glyphLayout = new GlyphLayout();
        loadHeroSpriteSheet();
        setupPauseMenuBounds();

        player = new Player(140, 240);
        player.healToFull();
        dungeonEnemies = new Array<>();
        spawnDungeonEnemies();
        baseMap = new GameMap();
        dungeonMap = new DungeonMap();
        gameMap = baseMap;
        inventorySystem = new InventorySystem();
        cameraSystem = new CameraSystem();
        inventoryUI = new InventoryUI();
        if (isValidSaveSlot(loadSlot) && hasSave(loadSlot)) {
            loadFromSlot(loadSlot);
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.015f, 0.010f, 0.012f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!prologueActive) {
            worldViewport.apply();
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            renderWorld();
            shapeRenderer.end();

            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            renderPlayerSprite();
            batch.end();
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
        if (dialogueVisible) {
            dialogueTypeTimer += delta;
        }

        if (prologueActive) {
            uiCamera.update();
            return;
        }

        player.applyGravity(delta);
        player.update(delta);
        resolveWorldCollisions();
        updateDoorLock();
        checkTrashPileDiscovery();
        if (locationIndex == 2) {
            updateDungeonEnemies(delta);
            updateKnifeAttack();
            if (!player.isAlive()) {
                respawnAtCurrentDoor();
            }
        }
        cameraSystem.follow(camera, player, delta);
        uiCamera.update();
    }

    private void handleInput() {
        player.stopX();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
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

        if (!dialogueVisible && !inventoryVisible && !prologueActive && !lyingInBed) {
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                player.moveLeft();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                player.moveRight();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                player.jump();
            }
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && inventorySystem.getSelectedWeapon() != null) {
                startKnifeAttack();
            }
        }

        if (!prologueActive && Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            inventoryVisible = !inventoryVisible;
            cancelInventoryDrag();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            handleAction();
        }
    }

    private void setupPauseMenuBounds() {
        float buttonX = VIRTUAL_WIDTH / 2f - PAUSE_MENU_WIDTH / 2f;
        float buttonY = VIRTUAL_HEIGHT / 2f + 88f;
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
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            inventorySystem.selectWeaponSlot(2);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            inventorySystem.selectWeaponSlot(3);
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

    private int getHandSlotAt(float x, float y) {
        for (int i = 0; i < InventorySystem.WEAPON_SLOT_COUNT; i++) {
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
        return new Rectangle(
                panelX + 134f + slot * (INVENTORY_SLOT_SIZE + INVENTORY_SLOT_GAP),
                panelY + 34f,
                INVENTORY_SLOT_SIZE,
                INVENTORY_SLOT_SIZE
        );
    }

    private Rectangle getBagSlotBounds(int slot) {
        float panelX = getInventoryPanelX();
        float panelY = getInventoryPanelY();
        int col = slot % INVENTORY_BAG_COLUMNS;
        int row = slot / INVENTORY_BAG_COLUMNS;
        return new Rectangle(
                panelX + 416f + col * (INVENTORY_SLOT_SIZE + INVENTORY_SLOT_GAP),
                panelY + 256f - row * (INVENTORY_SLOT_SIZE + INVENTORY_SLOT_GAP),
                INVENTORY_SLOT_SIZE,
                INVENTORY_SLOT_SIZE
        );
    }

    private float getInventoryPanelX() {
        return uiCamera.viewportWidth / 2f - INVENTORY_PANEL_WIDTH / 2f;
    }

    private float getInventoryPanelY() {
        return uiCamera.viewportHeight / 2f - INVENTORY_PANEL_HEIGHT / 2f;
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
        preferences.putBoolean(prefix + "lyingInBed", lyingInBed);
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

        locationIndex = preferences.getInteger(prefix + "locationIndex", 0);
        gameMap = locationIndex == 2 ? dungeonMap : baseMap;
        player.setX(preferences.getFloat(prefix + "playerX", 140f));
        player.setY(preferences.getFloat(prefix + "playerY", Constants.GROUND_Y + Constants.GROUND_HEIGHT));
        player.setVelocityY(0f);
        player.healToFull();
        int savedHealth = MathUtils.clamp(preferences.getInteger(prefix + "health", Constants.PLAYER_MAX_HEALTH), 1, Constants.PLAYER_MAX_HEALTH);
        player.takeDamage(Constants.PLAYER_MAX_HEALTH - savedHealth);

        knifeCollected = preferences.getBoolean(prefix + "knifeCollected", false);
        lyingInBed = preferences.getBoolean(prefix + "lyingInBed", false);
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
        camera.position.x = player.getX();
        camera.update();
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
        if (currentText == DialogueScripts.ENGINE_TEXT) {
            return "engine";
        }
        if (currentText == DialogueScripts.TRASH_TEXT) {
            return "trash";
        }
        return "chapel";
    }

    private void setCurrentDialogue(String dialogueKey) {
        if ("prologue".equals(dialogueKey)) {
            currentSpeakers = DialogueScripts.PROLOGUE_SPEAKERS;
            currentText = DialogueScripts.PROLOGUE_TEXT;
        } else if ("engine".equals(dialogueKey)) {
            currentSpeakers = DialogueScripts.ENGINE_SPEAKERS;
            currentText = DialogueScripts.ENGINE_TEXT;
        } else if ("trash".equals(dialogueKey)) {
            currentSpeakers = DialogueScripts.TRASH_SPEAKERS;
            currentText = DialogueScripts.TRASH_TEXT;
        } else {
            currentSpeakers = DialogueScripts.CHAPEL_SPEAKERS;
            currentText = DialogueScripts.CHAPEL_TEXT;
        }
    }

    private void handleAction() {
        if (dialogueVisible) {
            if (getVisibleDialogueCharacters() < currentText[dialogueLine].length()) {
                dialogueTypeTimer = currentText[dialogueLine].length() / TYPE_SPEED;
                return;
            }

            dialogueLine++;
            dialogueTypeTimer = 0f;
            if (dialogueLine >= currentText.length) {
                if (currentText == DialogueScripts.TRASH_TEXT && !knifeCollected) {
                    collectKnife();
                }
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

        if (locationIndex == -1) {
            if (isNear(player.getBounds(), BEDROOM_BED, 80f)) {
                lyingInBed = !lyingInBed;
                player.setX(BEDROOM_PLAYER_X);
                player.setY(Constants.GROUND_Y + Constants.GROUND_HEIGHT);
                player.setVelocityY(0f);
                return;
            }
            if (!lyingInBed && isNear(player.getBounds(), BEDROOM_DOOR, 70f)) {
                tryChangeLocation();
            }
            return;
        }

        if (locationIndex == 0 && isNear(player.getBounds(), SCHOLAR_BOUNDS, 80f)) {
            startDialogue(DialogueScripts.CHAPEL_SPEAKERS, DialogueScripts.CHAPEL_TEXT);
            return;
        }

        if (locationIndex == 0 && isNear(player.getBounds(), CHAPEL_BEDROOM_DOOR, 70f)) {
            tryChangeLocation();
            return;
        }

        if (locationIndex == 1 && !knifeCollected && isNear(player.getBounds(), ENGINE_DUNGEON_DOOR, 120f)) {
            startDialogue(DialogueScripts.TRASH_SPEAKERS, DialogueScripts.TRASH_TEXT);
            return;
        }

        Rectangle door = getActiveDoor();
        if (isNear(player.getBounds(), door, 70f)) {
            tryChangeLocation();
        }
    }

    private void tryChangeLocation() {
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
        locationIndex = -1;
        gameMap = baseMap;
        lyingInBed = true;
        dialogueVisible = false;
        dialogueLine = 0;
        dialogueTypeTimer = 0f;
        inventoryVisible = false;
        player.setX(BEDROOM_PLAYER_X);
        player.setY(Constants.GROUND_Y + Constants.GROUND_HEIGHT);
        player.setVelocityY(0f);
        doorLockedUntilPlayerMoves = true;
        camera.position.x = player.getX();
        camera.update();
    }

    private void changeLocation() {
        boolean startChapelDialogue = locationIndex == -1;
        if (locationIndex == -1) {
            locationIndex = 0;
            lyingInBed = false;
            player.setX(140f);
        } else if (locationIndex == 0 && isNear(player.getBounds(), CHAPEL_BEDROOM_DOOR, 70f)) {
            locationIndex = -1;
            lyingInBed = false;
            player.setX(BEDROOM_DOOR.x - 110f);
        } else if (locationIndex == 0) {
            locationIndex = 1;
            player.setX(ENGINE_RETURN_DOOR.x + 120f);
        } else if (locationIndex == 1 && isNear(player.getBounds(), ENGINE_DUNGEON_DOOR, 70f)) {
            locationIndex = 2;
            player.healToFull();
            player.setX(DUNGEON_RETURN_DOOR.x + 120f);
        } else if (locationIndex == 1) {
            locationIndex = 0;
            player.setX(CHAPEL_DOOR.x - 110f);
        } else {
            locationIndex = 1;
            player.setX(ENGINE_DUNGEON_DOOR.x - 110f);
        }

        gameMap = locationIndex == 2 ? dungeonMap : baseMap;
        dialogueVisible = false;
        dialogueTypeTimer = 0f;
        inventoryVisible = false;
        doorLockedUntilPlayerMoves = true;
        player.setY(Constants.GROUND_Y + Constants.GROUND_HEIGHT);
        player.setVelocityY(0f);
        camera.position.x = player.getX();
        camera.update();

        if (startChapelDialogue && !chapelIntroSeen) {
            chapelIntroSeen = true;
            startDialogue(DialogueScripts.CHAPEL_SPEAKERS, DialogueScripts.CHAPEL_TEXT);
        } else if (locationIndex == 1) {
            if (!engineIntroSeen) {
                engineIntroSeen = true;
                startDialogue(DialogueScripts.ENGINE_SPEAKERS, DialogueScripts.ENGINE_TEXT);
            }
        }
    }

    private Rectangle getActiveDoor() {
        if (locationIndex == -1) {
            return BEDROOM_DOOR;
        }
        if (locationIndex == 0) {
            return player.getX() < Constants.WORLD_WIDTH * 0.5f ? CHAPEL_BEDROOM_DOOR : CHAPEL_DOOR;
        }
        if (locationIndex == 1) {
            return player.getX() > Constants.WORLD_WIDTH * 0.5f ? ENGINE_DUNGEON_DOOR : ENGINE_RETURN_DOOR;
        }
        return DUNGEON_RETURN_DOOR;
    }

    private void resolveWorldCollisions() {
        boolean landed = false;
        float groundTop = gameMap.getGround().y + gameMap.getGround().height;
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
        if (player.getX() < 0) player.setX(0);
        if (locationIndex == -1 && player.getX() > BEDROOM_RIGHT_WALL - Constants.PLAYER_WIDTH) {
            player.setX(BEDROOM_RIGHT_WALL - Constants.PLAYER_WIDTH);
            return;
        }
        if (player.getX() > Constants.WORLD_WIDTH - Constants.PLAYER_WIDTH) {
            player.setX(Constants.WORLD_WIDTH - Constants.PLAYER_WIDTH);
        }
    }

    private void renderWorld() {
        if (locationIndex == -1) {
            renderBedroom();
            renderDoor(BEDROOM_DOOR, 0.18f, 0.24f, 0.26f);
        } else if (locationIndex == 0) {
            renderRuinedChapelStation();
            renderScholar();
            renderDoor(CHAPEL_BEDROOM_DOOR, 0.20f, 0.18f, 0.16f);
            renderDoor(CHAPEL_DOOR, 0.12f, 0.38f, 0.45f);
        } else if (locationIndex == 1) {
            renderAngelEngineLine();
            if (!knifeCollected) {
                renderTrashPile();
            }
            renderDoor(ENGINE_RETURN_DOOR, 0.36f, 0.18f, 0.12f);
            renderDoor(ENGINE_DUNGEON_DOOR, 0.14f, 0.42f, 0.28f);
        } else {
            renderDungeonDepths();
            renderDoor(DUNGEON_RETURN_DOOR, 0.34f, 0.19f, 0.12f);
            renderDungeonEnemies();
        }

    }

    private void checkTrashPileDiscovery() {
        if (knifeCollected || locationIndex != 1 || dialogueVisible || inventoryVisible) {
            return;
        }
        if (isNear(player.getBounds(), TRASH_PILE, 42f)) {
            startDialogue(DialogueScripts.TRASH_SPEAKERS, DialogueScripts.TRASH_TEXT);
        }
    }

    private void collectKnife() {
        knifeCollected = true;
        inventorySystem.equipWeapon(0, WeaponType.BASIC_KNIFE);
        inventorySystem.selectWeaponSlot(0);
        inventorySystem.addItem("\u041e\u0441\u043a\u043e\u043b\u043a\u0438 \u0441\u0442\u0435\u043a\u043b\u0430", 3);
        inventorySystem.addItem("\u0422\u043a\u0430\u043d\u0435\u0432\u0430\u044f \u043f\u043e\u0432\u044f\u0437\u043a\u0430", 1);
        inventorySystem.addWeaponToBag(WeaponType.RUSTY_SWORD);
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
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            return AttackDirection.DOWN;
        }
        return AttackDirection.FORWARD;
    }

    private Rectangle getKnifeAttackBounds() {
        WeaponType weapon = inventorySystem.getSelectedWeapon();
        float range = weapon == null ? 68f : weapon.getRange() + 18f;
        Rectangle playerBounds = player.getBounds();
        if (attackDirection == AttackDirection.UP) {
            return new Rectangle(playerBounds.x - 10f, playerBounds.y + playerBounds.height - 4f,
                    playerBounds.width + 20f, range);
        }
        if (attackDirection == AttackDirection.DOWN) {
            return new Rectangle(playerBounds.x - 10f, playerBounds.y - range + 8f,
                    playerBounds.width + 20f, range);
        }

        float width = range;
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
                enemy.takeDamage(Math.max(KNIFE_DAMAGE, weapon.getDamage()));
                rewardEnemyKill(enemy);
                if (attackDirection == AttackDirection.DOWN) {
                    player.bounceFromDownAttack(DOWN_ATTACK_BOUNCE);
                }
                knifeDamageApplied = true;
                return;
            }
        }
    }

    private void rewardEnemyKill(Enemy enemy) {
        if (!enemy.isAlive() && !enemy.isLootDropped()) {
            player.heal(KILL_HEAL_AMOUNT);
            enemy.markLootDropped();
        }
    }

    private void updateDungeonEnemies(float delta) {
        for (int i = 0; i < dungeonEnemies.size; i++) {
            Enemy enemy = dungeonEnemies.get(i);
            if (!enemy.isAlive()) {
                enemy.update(delta);
                continue;
            }

            resolveEnemyGrounding(enemy);
            updateEnemyAi(enemy, delta);
            enemy.update(delta);
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
            enemy.clampX(DUNGEON_RETURN_DOOR.x + 115f, Constants.WORLD_WIDTH - 120f);

            if (closeEnoughOnY && absDx <= enemy.getAttackRange() + 14f && enemy.canAttack() && player.isAlive()) {
                enemy.triggerAttack();
                float lungeTarget = playerCenterX + (dx > 0f ? -30f : 30f);
                enemy.moveToward(lungeTarget, enemy.getSpeed() * enemy.getLungePower(), delta);
                if (absDx <= enemy.getAttackRange()) {
                    player.takeDamage(enemy.getDamage());
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
            if (absDistance > 0f && absDistance < 58f) {
                push += Math.signum(distance) * (58f - absDistance);
            }
        }

        if (push != 0f) {
            enemy.moveBy(MathUtils.clamp(push, -70f, 70f) * delta * 4.2f);
        }
    }

    private void resolveEnemyGrounding(Enemy enemy) {
        enemy.setY(Constants.GROUND_Y + Constants.GROUND_HEIGHT);
    }

    private void spawnDungeonEnemies() {
        float groundY = Constants.GROUND_Y + Constants.GROUND_HEIGHT;
        dungeonEnemies.clear();
        dungeonEnemies.add(new Enemy(520f, groundY, 8, 116f, 6, Enemy.AttackStyle.CLAW, 0.16f, 0.18f, 0.22f, 0.74f, 0.62f, 0.50f));
        dungeonEnemies.add(new Enemy(820f, groundY, 7, 142f, 5, Enemy.AttackStyle.LUNGE, 0.34f, 0.10f, 0.12f, 0.86f, 0.70f, 0.56f));
        dungeonEnemies.add(new Enemy(1130f, groundY, 11, 104f, 8, Enemy.AttackStyle.HEAVY, 0.12f, 0.28f, 0.22f, 0.62f, 0.48f, 0.38f));
        dungeonEnemies.add(new Enemy(1480f, groundY, 8, 126f, 7, Enemy.AttackStyle.CLAW, 0.28f, 0.16f, 0.34f, 0.82f, 0.66f, 0.48f));
        dungeonEnemies.add(new Enemy(1840f, groundY, 7, 150f, 5, Enemy.AttackStyle.LUNGE, 0.42f, 0.30f, 0.10f, 0.70f, 0.54f, 0.42f));
        dungeonEnemies.add(new Enemy(2160f, groundY, 12, 108f, 8, Enemy.AttackStyle.HEAVY, 0.10f, 0.20f, 0.36f, 0.88f, 0.72f, 0.58f));
        dungeonEnemies.add(new Enemy(2470f, groundY, 8, 132f, 7, Enemy.AttackStyle.CLAW, 0.30f, 0.08f, 0.08f, 0.66f, 0.48f, 0.36f));
        dungeonEnemies.add(new Enemy(2760f, groundY, 13, 98f, 9, Enemy.AttackStyle.HEAVY, 0.08f, 0.26f, 0.30f, 0.78f, 0.60f, 0.46f));
    }

    private void respawnAtCurrentDoor() {
        player.healToFull();
        player.setX(DUNGEON_RETURN_DOOR.x + RESPAWN_X_OFFSET);
        player.setY(Constants.GROUND_Y + Constants.GROUND_HEIGHT);
        player.setVelocityY(0f);
        knifeSwingTimer = 0f;
        knifeDamageApplied = false;
        spawnDungeonEnemies();
        camera.position.x = player.getX();
        camera.update();
    }

    private void renderTrashPile() {
        shapeRenderer.setColor(0.08f, 0.07f, 0.06f, 1f);
        shapeRenderer.rect(TRASH_PILE.x, TRASH_PILE.y, TRASH_PILE.width, 22f);
        shapeRenderer.setColor(0.14f, 0.12f, 0.10f, 1f);
        shapeRenderer.rect(TRASH_PILE.x + 14f, TRASH_PILE.y + 20f, 76f, 24f);
        shapeRenderer.setColor(0.23f, 0.20f, 0.16f, 1f);
        shapeRenderer.rect(TRASH_PILE.x + 34f, TRASH_PILE.y + 42f, 44f, 16f);
        shapeRenderer.setColor(0.72f, 0.85f, 0.88f, 0.82f);
        shapeRenderer.rect(TRASH_PILE.x + 82f, TRASH_PILE.y + 24f, 30f, 5f);
        shapeRenderer.rect(TRASH_PILE.x + 8f, TRASH_PILE.y + 38f, 24f, 5f);
        shapeRenderer.setColor(0.82f, 0.84f, 0.78f, 1f);
        shapeRenderer.rect(TRASH_PILE.x + 58f, TRASH_PILE.y + 34f, 34f, 6f);
    }

    private void renderDungeonEnemies() {
        for (Enemy enemy : dungeonEnemies) {
            if (!enemy.isAlive()) {
                continue;
            }
            renderHumanEnemy(enemy);
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

        shapeRenderer.setColor(enemy.getSkinR(), enemy.getSkinG(), enemy.getSkinB(), 1f);
        shapeRenderer.rect(x + 7f, y + 39f + breathe, 18f, 18f);
        shapeRenderer.setColor(enemy.getCoatR(), enemy.getCoatG(), enemy.getCoatB(), 1f);
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
        float fade = MathUtils.sin(progress * MathUtils.PI);

        float startX = centerX + dir * 10f;
        float startY = centerY;
        float endX;
        float endY;
        if (attackDirection == AttackDirection.UP) {
            endX = centerX + MathUtils.sin(progress * MathUtils.PI) * 16f * dir;
            endY = centerY + range;
        } else if (attackDirection == AttackDirection.DOWN) {
            endX = centerX + MathUtils.sin(progress * MathUtils.PI) * 16f * dir;
            endY = player.getBounds().y - range * 0.62f;
            startY = player.getBounds().y + 18f;
        } else {
            endX = centerX + dir * (range + 18f);
            endY = centerY + MathUtils.lerp(-18f, 18f, progress);
        }

        shapeRenderer.setColor(0.18f, 0.10f, 0.08f, fade);
        shapeRenderer.rectLine(startX - dir * 8f, startY - 6f, startX + dir * 4f, startY + 5f, 7f);
        shapeRenderer.setColor(0.82f, 0.82f, 0.78f, 0.92f * fade);
        shapeRenderer.rectLine(startX, startY, endX, endY, weapon.getThickness());
        shapeRenderer.setColor(0.98f, 0.96f, 0.88f, 0.78f * fade);
        shapeRenderer.rectLine(startX + dir * 4f, startY + 2f, endX, endY, Math.max(2.5f, weapon.getThickness() * 0.35f));
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
        heroTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        heroFrames = new TextureRegion[HERO_FRAME_COUNT];
        for (int i = 0; i < heroFrames.length; i++) {
            heroFrames[i] = new TextureRegion(heroTexture, i * HERO_FRAME_SIZE, 0, HERO_FRAME_SIZE, HERO_FRAME_SIZE);
        }
    }

    private void renderRuinedChapelStation() {
        drawBackdrop(0.018f, 0.010f, 0.014f, 0.120f, 0.020f, 0.030f);
        drawHolyWindows(260f, 515f, 0.76f, 0.08f, 0.08f);
        drawHolyWindows(790f, 555f, 0.16f, 0.74f, 0.78f);
        drawHangingScript();
        drawPillars(0.080f, 0.020f, 0.026f);
        drawRails(0.32f, 0.030f, 0.032f);
        drawGround(0.11f, 0.028f, 0.030f, 0.025f, 0.012f, 0.016f);
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
        float pulse = 0.5f + 0.5f * MathUtils.sin(time * 2.4f);
        shapeRenderer.setColor(0.050f, 0.052f, 0.052f, 1f);
        shapeRenderer.rect(0f, 0f, BEDROOM_RIGHT_WALL, Constants.WORLD_HEIGHT);
        shapeRenderer.setColor(0.105f, 0.096f, 0.090f, 1f);
        shapeRenderer.rect(0f, 150f, BEDROOM_RIGHT_WALL, 470f);
        shapeRenderer.setColor(0.070f, 0.052f, 0.052f, 1f);
        shapeRenderer.rect(0f, 616f, BEDROOM_RIGHT_WALL, 18f);
        shapeRenderer.setColor(0.118f, 0.105f, 0.094f, 1f);
        shapeRenderer.rect(0f, 634f, BEDROOM_RIGHT_WALL, 118f);
        shapeRenderer.setColor(0.032f, 0.034f, 0.036f, 1f);
        shapeRenderer.rect(BEDROOM_RIGHT_WALL, 0f, Constants.WORLD_WIDTH - BEDROOM_RIGHT_WALL, Constants.WORLD_HEIGHT);

        shapeRenderer.setColor(0.052f, 0.060f, 0.064f, 1f);
        shapeRenderer.rect(480f, 340f, 340f, 220f);
        shapeRenderer.setColor(0.12f, 0.18f, 0.19f, 1f);
        shapeRenderer.rect(498f, 358f, 304f, 184f);
        shapeRenderer.setColor(0.58f, 0.78f + pulse * 0.08f, 0.78f, 0.55f);
        shapeRenderer.rect(520f, 380f, 118f, 140f);
        shapeRenderer.rect(662f, 380f, 118f, 140f);
        shapeRenderer.setColor(0.035f, 0.044f, 0.048f, 1f);
        shapeRenderer.rect(646f, 358f, 12f, 184f);
        shapeRenderer.rect(498f, 445f, 304f, 12f);

        renderBed();

        shapeRenderer.setColor(0.040f, 0.038f, 0.036f, 1f);
        shapeRenderer.rect(930f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 92f, 128f);
        shapeRenderer.setColor(0.16f, 0.13f, 0.10f, 1f);
        shapeRenderer.rect(942f, Constants.GROUND_Y + Constants.GROUND_HEIGHT + 98f, 68f, 9f);
        shapeRenderer.setColor(0.68f, 0.50f, 0.24f, 0.72f);
        shapeRenderer.rect(960f, Constants.GROUND_Y + Constants.GROUND_HEIGHT + 58f, 30f, 30f);

        drawGround(0.18f, 0.125f, 0.095f, 0.060f, 0.044f, 0.042f);
        shapeRenderer.setColor(0.032f, 0.034f, 0.036f, 1f);
        shapeRenderer.rect(BEDROOM_RIGHT_WALL, 0f, Constants.WORLD_WIDTH - BEDROOM_RIGHT_WALL, Constants.WORLD_HEIGHT);
        shapeRenderer.setColor(0.070f, 0.052f, 0.052f, 1f);
        shapeRenderer.rect(BEDROOM_RIGHT_WALL - 16f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 16f, 500f);
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
        if (locationIndex == -1 && lyingInBed) {
            renderLyingPlayer();
            return;
        }
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

        if (locationIndex == -1 && lyingInBed) {
            frame = heroFrames[0];
            drawWidth = 126f;
            drawHeight = 86f;
            batch.draw(frame, BEDROOM_BED.x + 72f, BEDROOM_BED.y + 22f, drawWidth, drawHeight);
            return;
        }

        Rectangle b = player.getBounds();
        float x = b.x + b.width * 0.5f - drawWidth * 0.5f;
        if (attackFrame) {
            x += player.isFacingRight() ? 18f : -18f;
        }
        float y = b.y - 4f;
        boolean flip = player.isFacingRight();
        batch.draw(frame, flip ? x + drawWidth : x, y, flip ? -drawWidth : drawWidth, drawHeight);
    }

    private void renderInventoryHeroSprite() {
        if (!inventoryVisible || heroFrames == null || heroFrames.length == 0) {
            return;
        }
        float panelX = getInventoryPanelX();
        float panelY = getInventoryPanelY();
        int frameIndex = 8;
        batch.draw(heroFrames[frameIndex], panelX + 76f, panelY + 134f, 174f, 174f);
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
            return 8;
        }
        return 1;
    }

    private void renderLyingPlayer() {
        float x = BEDROOM_BED.x + 76f;
        float y = BEDROOM_BED.y + 34f;
        float breathe = MathUtils.sin(time * 2.2f) * 1.4f;

        shapeRenderer.setColor(0.030f, 0.035f, 0.040f, 1f);
        shapeRenderer.rect(x + 24f, y + breathe, 104f, 24f);
        shapeRenderer.setColor(0.92f, 0.80f, 0.62f, 1f);
        shapeRenderer.rect(x + 8f, y + 3f + breathe, 24f, 20f);
        shapeRenderer.setColor(0.92f, 0.78f, 0.20f, 1f);
        shapeRenderer.rect(x + 4f, y + 19f + breathe, 34f, 10f);
        shapeRenderer.setColor(0.04f, 0.045f, 0.052f, 1f);
        shapeRenderer.rect(x + 38f, y + 18f + breathe, 68f, 16f);
        shapeRenderer.setColor(0.09f, 0.10f, 0.11f, 1f);
        shapeRenderer.rect(x + 112f, y + 2f + breathe, 28f, 7f);
        shapeRenderer.rect(x + 114f, y + 18f + breathe, 24f, 7f);
        shapeRenderer.setColor(0.35f, 0.39f, 0.42f, 1f);
        shapeRenderer.rect(x + 42f, y + 25f + breathe, 58f, 4f);
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
        font.draw(batch, "W/S + \u041b\u041a\u041c: \u0443\u0434\u0430\u0440 \u0432\u0432\u0435\u0440\u0445/\u0432\u043d\u0438\u0437 | 1-4: \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u0432 \u0440\u0443\u043a\u0430\u0445", 22, uiCamera.viewportHeight - 70f);
        WeaponType selectedWeapon = inventorySystem.getSelectedWeapon();
        if (selectedWeapon != null) {
            font.draw(batch, "\u0412 \u0440\u0443\u043a\u0430\u0445: " + selectedWeapon.getDisplayName() + " | \u041b\u041a\u041c: \u0443\u0434\u0430\u0440", 22, uiCamera.viewportHeight - 96f);
        } else if (!inventorySystem.isEmpty()) {
            font.draw(batch, "\u0420\u0443\u043a\u0438 \u043f\u0443\u0441\u0442\u044b: \u0432\u044b\u0431\u0435\u0440\u0438 \u043e\u0440\u0443\u0436\u0438\u0435 \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435", 22, uiCamera.viewportHeight - 96f);
        }

        if (!dialogueVisible) {
            if (locationIndex == -1 && isNear(player.getBounds(), BEDROOM_BED, 80f)) {
                font.draw(batch, lyingInBed ? "\u041d\u0430\u0436\u043c\u0438 E, \u0447\u0442\u043e\u0431\u044b \u0432\u0441\u0442\u0430\u0442\u044c" : "\u041d\u0430\u0436\u043c\u0438 E, \u0447\u0442\u043e\u0431\u044b \u043b\u0435\u0447\u044c", uiCamera.viewportWidth / 2f - 112f, 116f);
            }
            if (locationIndex == 0 && isNear(player.getBounds(), SCHOLAR_BOUNDS, 80f)) {
                font.draw(batch, "\u041d\u0430\u0436\u043c\u0438 E", uiCamera.viewportWidth / 2f - 42f, 116f);
            }
            Rectangle door = getActiveDoor();
            if (!lyingInBed && isNear(player.getBounds(), door, 70f)) {
                font.draw(batch, doorLockedUntilPlayerMoves ? "\u041e\u0442\u043e\u0439\u0434\u0438 \u043e\u0442 \u0434\u0432\u0435\u0440\u0438 \u0438 \u0432\u0435\u0440\u043d\u0438\u0441\u044c" : "\u041d\u0430\u0436\u043c\u0438 E, \u0447\u0442\u043e\u0431\u044b \u0432\u043e\u0439\u0442\u0438", uiCamera.viewportWidth / 2f - 112f, 92f);
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
        if (locationIndex != 2) {
            return;
        }
        float barWidth = 260f;
        float x = uiCamera.viewportWidth - barWidth - 34f;
        font.setColor(0.90f, 0.94f, 0.88f, 1f);
        font.draw(batch, "HP: " + player.getHealth() + " / " + Constants.PLAYER_MAX_HEALTH, x + 78f, 38f);
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
        if (!prologueActive && locationIndex == 2) {
            renderHealthBarPanel();
        }

        if (inventoryVisible) {
            renderInventoryPanel();
        }

        if (dialogueVisible) {
            float pulse = 0.5f + 0.5f * MathUtils.sin(time * 5f);
            shapeRenderer.setColor(0.018f, 0.022f, 0.024f, 1f);
            shapeRenderer.rect(64f, 48f, uiCamera.viewportWidth - 128f, 128f);
            shapeRenderer.setColor(0.42f, 0.59f, 0.54f, 1f);
            shapeRenderer.rect(64f, 170f, uiCamera.viewportWidth - 128f, 6f);
            renderDialoguePortraits(pulse);
        }
    }

    private void renderPauseMenuPanels() {
        if (!pauseMenuVisible) {
            return;
        }

        float panelX = VIRTUAL_WIDTH / 2f - 230f;
        float panelY = VIRTUAL_HEIGHT / 2f - 190f;
        float pulse = 0.5f + 0.5f * MathUtils.sin(time * 3.2f);

        shapeRenderer.setColor(0.004f, 0.004f, 0.006f, 0.48f);
        shapeRenderer.rect(0f, 0f, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
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

        font.setColor(0.94f, 0.90f, 0.82f, 1f);
        font.draw(batch, "\u041f\u0430\u0443\u0437\u0430", VIRTUAL_WIDTH / 2f - 34f, VIRTUAL_HEIGHT / 2f + 204f);

        for (int i = 0; i < pauseButtonBounds.length; i++) {
            Rectangle bounds = pauseButtonBounds[i];
            font.setColor(i == hoveredPauseButton ? Color.WHITE : new Color(0.86f, 0.90f, 0.86f, 1f));
            font.draw(batch, getPauseButtonLabel(i), bounds.x + 34f, bounds.y + 35f);
        }

        if (pausePanel == PausePanel.ACHIEVEMENTS) {
            font.setColor(0.90f, 0.94f, 0.88f, 1f);
            font.draw(batch, "\u0414\u043e\u0441\u0442\u0438\u0436\u0435\u043d\u0438\u044f", VIRTUAL_WIDTH / 2f - 78f, VIRTUAL_HEIGHT / 2f - 94f);
            font.setColor(0.70f, 0.78f, 0.74f, 1f);
            font.draw(batch, "\u041f\u0430\u043b\u043e\u043c\u043d\u0438\u043a | \u041e\u0441\u043a\u043e\u043b\u043e\u043a \u0441\u0432\u0435\u0442\u0430 | \u041f\u043e\u0434\u0437\u0435\u043c\u043d\u044b\u0439 \u0437\u043e\u0432", VIRTUAL_WIDTH / 2f - 184f, VIRTUAL_HEIGHT / 2f - 126f);
            font.draw(batch, "\u041f\u0440\u043e\u0433\u0440\u0435\u0441\u0441 \u0431\u0443\u0434\u0435\u0442 \u043e\u0442\u043c\u0435\u0447\u0430\u0442\u044c\u0441\u044f \u043f\u043e \u0445\u043e\u0434\u0443 \u0438\u0433\u0440\u044b.", VIRTUAL_WIDTH / 2f - 168f, VIRTUAL_HEIGHT / 2f - 154f);
        } else if (pausePanel == PausePanel.SETTINGS) {
            font.setColor(0.90f, 0.94f, 0.88f, 1f);
            font.draw(batch, "\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438", VIRTUAL_WIDTH / 2f - 68f, VIRTUAL_HEIGHT / 2f - 94f);
            font.setColor(0.70f, 0.78f, 0.74f, 1f);
            font.draw(batch, "\u0420\u0430\u0437\u0434\u0435\u043b \u0432 \u0440\u0430\u0437\u0440\u0430\u0431\u043e\u0442\u043a\u0435.", VIRTUAL_WIDTH / 2f - 102f, VIRTUAL_HEIGHT / 2f - 126f);
            font.draw(batch, "\u041d\u0430\u0436\u043c\u0438\u0442\u0435 Escape \u0438\u043b\u0438 \u041f\u0440\u043e\u0434\u043e\u043b\u0436\u0438\u0442\u044c, \u0447\u0442\u043e\u0431\u044b \u0432\u0435\u0440\u043d\u0443\u0442\u044c\u0441\u044f.", VIRTUAL_WIDTH / 2f - 208f, VIRTUAL_HEIGHT / 2f - 154f);
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

    private void renderInventoryPanel() {
        float panelX = getInventoryPanelX();
        float panelY = getInventoryPanelY();

        shapeRenderer.setColor(0.004f, 0.006f, 0.008f, 0.62f);
        shapeRenderer.rect(panelX - 14f, panelY - 14f, INVENTORY_PANEL_WIDTH + 28f, INVENTORY_PANEL_HEIGHT + 28f);
        shapeRenderer.setColor(0.018f, 0.022f, 0.024f, 0.98f);
        shapeRenderer.rect(panelX, panelY, INVENTORY_PANEL_WIDTH, INVENTORY_PANEL_HEIGHT);
        shapeRenderer.setColor(0.42f, 0.54f, 0.49f, 1f);
        shapeRenderer.rect(panelX, panelY + INVENTORY_PANEL_HEIGHT - 38f, INVENTORY_PANEL_WIDTH, 8f);
        shapeRenderer.setColor(0.58f, 0.12f, 0.10f, 0.92f);
        shapeRenderer.rect(panelX, panelY + 10f, INVENTORY_PANEL_WIDTH, 4f);
        shapeRenderer.setColor(0.075f, 0.087f, 0.087f, 1f);
        shapeRenderer.rect(panelX + 42f, panelY + 122f, 222f, 246f);
        shapeRenderer.setColor(0.25f, 0.36f, 0.33f, 1f);
        shapeRenderer.rect(panelX + 56f, panelY + 136f, 194f, 218f);

        for (int i = 0; i < InventorySystem.BAG_SLOT_COUNT; i++) {
            Rectangle slot = getBagSlotBounds(i);
            drawSlot(slot.x, slot.y, false, false);
        }

        for (int i = 0; i < InventorySystem.WEAPON_SLOT_COUNT; i++) {
            Rectangle slot = getHandSlotBounds(i);
            drawSlot(slot.x, slot.y, i == inventorySystem.getSelectedWeaponSlot(), true);
        }

        WeaponType[] handWeapons = inventorySystem.getWeaponSlots();
        for (int i = 0; i < handWeapons.length; i++) {
            if (handWeapons[i] != null) {
                Rectangle slot = getHandSlotBounds(i);
                drawWeaponIcon(handWeapons[i], slot.x + 8f, slot.y + 10f, 34f, true);
            }
        }

        WeaponType[] bagWeapons = inventorySystem.getBagSlots();
        for (int i = 0; i < bagWeapons.length; i++) {
            if (bagWeapons[i] != null) {
                Rectangle slot = getBagSlotBounds(i);
                drawWeaponIcon(bagWeapons[i], slot.x + 8f, slot.y + 10f, 34f, false);
            }
        }

        if (draggedWeapon != null) {
            pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
            hudViewport.unproject(pointer);
            drawWeaponIcon(draggedWeapon, pointer.x - 18f, pointer.y - 18f, 38f, true);
        }
    }

    private void drawSlot(float x, float y, boolean selected, boolean handSlot) {
        if (selected) {
            shapeRenderer.setColor(0.82f, 0.72f, 0.44f, 1f);
        } else if (handSlot) {
            shapeRenderer.setColor(0.32f, 0.44f, 0.42f, 1f);
        } else {
            shapeRenderer.setColor(0.055f, 0.065f, 0.065f, 1f);
        }
        shapeRenderer.rect(x, y, INVENTORY_SLOT_SIZE, INVENTORY_SLOT_SIZE);
        shapeRenderer.setColor(selected ? 0.18f : 0.15f, selected ? 0.16f : 0.18f, selected ? 0.10f : 0.17f, 1f);
        shapeRenderer.rect(x + 5f, y + 5f, INVENTORY_SLOT_SIZE - 10f, INVENTORY_SLOT_SIZE - 10f);
        shapeRenderer.setColor(0.88f, 0.95f, 0.88f, selected ? 0.34f : 0.12f);
        shapeRenderer.rect(x + 6f, y + INVENTORY_SLOT_SIZE - 10f, INVENTORY_SLOT_SIZE - 12f, 3f);
    }

    private void drawWeaponIcon(WeaponType weapon, float x, float y, float size, boolean bright) {
        float glow = bright ? 1f : 0.72f;
        shapeRenderer.setColor(weapon.getColorR() * glow, weapon.getColorG() * glow, weapon.getColorB() * glow, 1f);
        shapeRenderer.rectLine(x + 7f, y + 6f, x + size - 5f, y + size - 8f, 5f);
        shapeRenderer.setColor(0.16f, 0.10f, 0.08f, 1f);
        shapeRenderer.rectLine(x + 2f, y + 3f, x + 12f, y + 13f, 6f);
        shapeRenderer.setColor(0.94f, 0.96f, 0.88f, 0.86f);
        shapeRenderer.circle(x + size - 5f, y + size - 8f, 3f, 10);
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
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
        if (heroTexture != null) {
            heroTexture.dispose();
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

