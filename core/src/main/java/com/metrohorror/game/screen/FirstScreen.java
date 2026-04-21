package com.metrohorror.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private static final float KNIFE_SWING_TIME = 0.24f;
    private static final int KNIFE_DAMAGE = 2;
    private static final int STOMP_DAMAGE = 2;
    private static final int KILL_HEAL_AMOUNT = 5;
    private static final float RESPAWN_X_OFFSET = 120f;
    private static final float PAUSE_MENU_WIDTH = 360f;
    private static final float PAUSE_BUTTON_HEIGHT = 54f;
    private static final float PAUSE_BUTTON_GAP = 14f;
    private static final int SAVE_SLOT_COUNT = 3;
    private static final String SAVE_PREFERENCES = "metrohorror-saves";
    private static final String[] PAUSE_BUTTON_LABELS = {
            "Выйти в меню",
            "Достижения",
            "Настройки",
            "Продолжить"
    };

    private static final String[] PROLOGUE_SPEAKERS = {
            "100 лет назад",
            "Люцифер",
            "Ученый",
            "Ангельский двигатель",
            "100 лет спустя",
            "Священная книга"
    };
    private static final String[] PROLOGUE_TITLES = {
            "Запретная просьба",
            "Сделка с Люцифером",
            "Похищение ангела",
            "Рождение двигателя",
            "Сто лет голода",
            "Пророчество"
    };
    private static final String[] PROLOGUE_TEXT = {
            "Мир умирал от голода. Один ученый нашел запретную книгу и попросил у нее пищу, свет и бесконечную энергию.",
            "Из красной тени вышел Люцифер. Он не дал чудо бесплатно: за спасение людей ученый должен был помочь украсть ангела.",
            "Ночью они поднялись к небесным вратам, сорвали ангела с цепи света и увели его в подземную лабораторию.",
            "Ангела закрыли в капсуле. Его крылья стали топливом, а молитвы стали проводами. Так появился Ангельский двигатель.",
            "Сто лет двигатель кормил города. Потом свет погас, машины остановились, и голод вернулся еще страшнее.",
            "В ту ночь родился слепой мальчик из пророчества. Его имя скрыли, потому что имя может открыть дорогу искушению."
    };

    private static final String[] CHAPEL_SPEAKERS = {
            "Ученый",
            "???",
            "Ученый",
            "Ученый"
    };
    private static final String[] CHAPEL_TEXT = {
            "Просыпайся. Повязку не снимай: твои глаза видят то, что не должен видеть ни один человек.",
            "Во сне я слышал крылья под рельсами. Кто-то бился в клетке и звал меня по имени... но я его не помню.",
            "Это ангел внутри двигателя. Когда-то я помог Люциферу украсть его, а теперь двигатель умирает вместе с миром.",
            "Иди к запертой двери. На нижней линии ты увидишь клетку, из-за которой небо и ад начали охоту."
    };

    private static final String[] ENGINE_SPEAKERS = {
            "Ангельский двигатель",
            "???",
            "Ангельский двигатель"
    };
    private static final String[] ENGINE_TEXT = {
            "Слепой ребенок пророчества... я не враг. Я тот, кого превратили в сердце этой машины.",
            "Это ты говорил со мной во сне?",
            "Да. Иди по красным рельсам. Люцифер знает твое лицо, но пока не знает твое имя."
    };

    private static final String[] TRASH_SPEAKERS = {
            "???"
    };
    private static final String[] TRASH_TEXT = {
            "Следуя указанию свыше, вы внезапно спотыкаетесь о груду мусора и падаете прямо на осколки стекла. Рука натыкается на что-то острое — предмет, похожий на нож. Вы решаете взять его с собой."
    };

    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;
    private Viewport worldViewport;
    private Viewport hudViewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
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
    private boolean dialogueVisible = true;
    private boolean prologueActive = true;
    private int dialogueLine;
    private int locationIndex;
    private float time;
    private float dialogueTypeTimer;
    private boolean knifeCollected;
    private float knifeSwingTimer;
    private boolean knifeDamageApplied;
    private boolean lyingInBed;
    private boolean pauseMenuVisible;
    private int hoveredPauseButton = -1;
    private PausePanel pausePanel = PausePanel.NONE;

    private String[] currentSpeakers = PROLOGUE_SPEAKERS;
    private String[] currentText = PROLOGUE_TEXT;

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
        checkTrashPileDiscovery();
        if (locationIndex == 2) {
            handleEnemyStomps();
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
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && knifeCollected) {
                startKnifeAttack();
            }
        }

        if (!prologueActive && Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            inventoryVisible = !inventoryVisible;
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
            return "Пусто";
        }

        Preferences preferences = Gdx.app.getPreferences(SAVE_PREFERENCES);
        String prefix = getSavePrefix(slot);
        int location = preferences.getInteger(prefix + "locationIndex", 0);
        String place = location == -1 ? "Комната" : location == 0 ? "Часовня" : location == 1 ? "Двигатель" : "Подземелье";
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
        if (currentText == PROLOGUE_TEXT) {
            return "prologue";
        }
        if (currentText == ENGINE_TEXT) {
            return "engine";
        }
        if (currentText == TRASH_TEXT) {
            return "trash";
        }
        return "chapel";
    }

    private void setCurrentDialogue(String dialogueKey) {
        if ("prologue".equals(dialogueKey)) {
            currentSpeakers = PROLOGUE_SPEAKERS;
            currentText = PROLOGUE_TEXT;
        } else if ("engine".equals(dialogueKey)) {
            currentSpeakers = ENGINE_SPEAKERS;
            currentText = ENGINE_TEXT;
        } else if ("trash".equals(dialogueKey)) {
            currentSpeakers = TRASH_SPEAKERS;
            currentText = TRASH_TEXT;
        } else {
            currentSpeakers = CHAPEL_SPEAKERS;
            currentText = CHAPEL_TEXT;
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
                if (currentText == TRASH_TEXT && !knifeCollected) {
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
                changeLocation();
            }
            return;
        }

        if (locationIndex == 0 && isNear(player.getBounds(), SCHOLAR_BOUNDS, 80f)) {
            startDialogue(CHAPEL_SPEAKERS, CHAPEL_TEXT);
            return;
        }

        if (locationIndex == 0 && isNear(player.getBounds(), CHAPEL_BEDROOM_DOOR, 70f)) {
            changeLocation();
            return;
        }

        if (locationIndex == 1 && !knifeCollected && isNear(player.getBounds(), ENGINE_DUNGEON_DOOR, 120f)) {
            startDialogue(TRASH_SPEAKERS, TRASH_TEXT);
            return;
        }

        Rectangle door = getActiveDoor();
        if (isNear(player.getBounds(), door, 70f)) {
            changeLocation();
        }
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
        camera.position.x = player.getX();
        camera.update();
    }

    private void changeLocation() {
        boolean startChapelDialogue = locationIndex == -1;
        if (locationIndex == -1) {
            locationIndex = 0;
            lyingInBed = false;
            player.setX(140f);
            startDialogue(CHAPEL_SPEAKERS, CHAPEL_TEXT);
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
        player.setY(Constants.GROUND_Y + Constants.GROUND_HEIGHT);
        player.setVelocityY(0f);
        camera.position.x = player.getX();
        camera.update();

        if (startChapelDialogue) {
            startDialogue(CHAPEL_SPEAKERS, CHAPEL_TEXT);
        } else if (locationIndex == 1) {
            startDialogue(ENGINE_SPEAKERS, ENGINE_TEXT);
        }
    }

    private Rectangle getActiveDoor() {
        if (locationIndex == -1) {
            return BEDROOM_DOOR;
        }
        String place;
        if (locationIndex == -1) {
            place = "Комната";
        } else if (locationIndex == 0) {
            return CHAPEL_DOOR;
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

        renderPlayer();
        renderKnifeSlash();
    }

    private void checkTrashPileDiscovery() {
        if (knifeCollected || locationIndex != 1 || dialogueVisible || inventoryVisible) {
            return;
        }
        if (isNear(player.getBounds(), TRASH_PILE, 42f)) {
            startDialogue(TRASH_SPEAKERS, TRASH_TEXT);
        }
    }

    private void collectKnife() {
        knifeCollected = true;
        inventorySystem.equipWeapon(0, WeaponType.BASIC_KNIFE);
        inventorySystem.selectWeaponSlot(0);
    }

    private void startKnifeAttack() {
        if (knifeSwingTimer > 0f) {
            return;
        }
        knifeSwingTimer = KNIFE_SWING_TIME;
        knifeDamageApplied = false;
    }

    private Rectangle getKnifeAttackBounds() {
        float width = 68f;
        float height = 54f;
        float x = player.isFacingRight()
                ? player.getBounds().x + player.getBounds().width - 2f
                : player.getBounds().x - width + 2f;
        float y = player.getBounds().y + 12f;
        return new Rectangle(x, y, width, height);
    }

    private void updateKnifeAttack() {
        if (knifeSwingTimer <= 0f || knifeDamageApplied) {
            return;
        }

        Rectangle attackBounds = getKnifeAttackBounds();
        for (Enemy enemy : dungeonEnemies) {
            if (enemy.isAlive() && attackBounds.overlaps(enemy.getBounds())) {
                enemy.takeDamage(KNIFE_DAMAGE);
                rewardEnemyKill(enemy);
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

    private void handleEnemyStomps() {
        if (player.getVelocityY() > 0f) {
            return;
        }

        for (Enemy enemy : dungeonEnemies) {
            if (!enemy.isAlive() || !player.getBounds().overlaps(enemy.getBounds())) {
                continue;
            }

            float playerBottom = player.getBounds().y;
            float stompLine = enemy.getBounds().y + enemy.getBounds().height * 0.55f;
            if (playerBottom >= stompLine) {
                enemy.takeDamage(STOMP_DAMAGE);
                rewardEnemyKill(enemy);
                player.bounceFromEnemy();
                return;
            }
        }
    }

    private void updateDungeonEnemies(float delta) {
        for (Enemy enemy : dungeonEnemies) {
            if (!enemy.isAlive()) {
                enemy.update(delta);
                continue;
            }

            resolveEnemyGrounding(enemy);
            float dx = player.getX() - enemy.getX();
            float absDx = Math.abs(dx);
            float playerCenterY = player.getBounds().y + player.getBounds().height / 2f;
            float enemyCenterY = enemy.getBounds().y + enemy.getBounds().height / 2f;
            boolean closeEnoughOnY = Math.abs(playerCenterY - enemyCenterY) < 82f;
            float distanceFromDoor = enemy.getX() - DUNGEON_RETURN_DOOR.x;

            if (closeEnoughOnY && absDx < 540f && absDx > 70f) {
                float pressureSpeed = enemy.getSpeed() * (absDx < 190f ? 1.35f : 1f);
                enemy.moveToward(player.getX(), pressureSpeed, delta);
            } else if (absDx >= 540f && enemy.canAttack()) {
                float patrolTarget = enemy.getX() + MathUtils.sin(time * 1.4f + enemy.getX() * 0.02f) * 120f;
                if (distanceFromDoor > 120f && distanceFromDoor < Constants.WORLD_WIDTH - 260f) {
                    enemy.moveToward(patrolTarget, enemy.getSpeed() * 0.35f, delta);
                }
            }
            if (closeEnoughOnY && absDx <= 96f && enemy.canAttack() && player.isAlive()) {
                enemy.triggerAttack();
                float lungeTarget = player.getX() + (dx > 0f ? -24f : 24f);
                enemy.moveToward(lungeTarget, enemy.getSpeed() * 2.3f, delta);
                if (absDx <= 64f) {
                    player.takeDamage(enemy.getDamage());
                }
            }
            enemy.update(delta);
        }
    }

    private void resolveEnemyGrounding(Enemy enemy) {
        enemy.setY(Constants.GROUND_Y + Constants.GROUND_HEIGHT);
    }

    private void spawnDungeonEnemies() {
        float groundY = Constants.GROUND_Y + Constants.GROUND_HEIGHT;
        dungeonEnemies.clear();
        dungeonEnemies.add(new Enemy(520f, groundY, 5, 150f, 6, 0.16f, 0.18f, 0.22f, 0.74f, 0.62f, 0.50f));
        dungeonEnemies.add(new Enemy(820f, groundY, 4, 190f, 5, 0.34f, 0.10f, 0.12f, 0.86f, 0.70f, 0.56f));
        dungeonEnemies.add(new Enemy(1130f, groundY, 7, 132f, 8, 0.12f, 0.28f, 0.22f, 0.62f, 0.48f, 0.38f));
        dungeonEnemies.add(new Enemy(1480f, groundY, 5, 165f, 7, 0.28f, 0.16f, 0.34f, 0.82f, 0.66f, 0.48f));
        dungeonEnemies.add(new Enemy(1840f, groundY, 4, 205f, 5, 0.42f, 0.30f, 0.10f, 0.70f, 0.54f, 0.42f));
        dungeonEnemies.add(new Enemy(2160f, groundY, 7, 140f, 8, 0.10f, 0.20f, 0.36f, 0.88f, 0.72f, 0.58f));
        dungeonEnemies.add(new Enemy(2470f, groundY, 5, 175f, 7, 0.30f, 0.08f, 0.08f, 0.66f, 0.48f, 0.36f));
        dungeonEnemies.add(new Enemy(2760f, groundY, 9, 122f, 9, 0.08f, 0.26f, 0.30f, 0.78f, 0.60f, 0.46f));
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
        float walk = MathUtils.sin(time * 13f + enemy.getX() * 0.03f) * 5f;
        float breathe = MathUtils.sin(time * 3.4f + enemy.getX() * 0.01f) * 1.4f;
        float dir = enemy.isFacingRight() ? 1f : -1f;
        Rectangle b = enemy.getBounds();
        float x = b.x;
        float y = b.y;

        shapeRenderer.setColor(enemy.getSkinR(), enemy.getSkinG(), enemy.getSkinB(), 1f);
        shapeRenderer.rect(x + 11f, y + 42f + breathe, 18f, 18f);
        shapeRenderer.setColor(enemy.getCoatR(), enemy.getCoatG(), enemy.getCoatB(), 1f);
        shapeRenderer.rect(x + 5f, y + 8f, 30f, 40f + breathe * 0.3f);
        shapeRenderer.setColor(0.025f, 0.025f, 0.030f, 1f);
        shapeRenderer.rect(x + 8f, y + 50f + breathe, 26f, 7f);
        shapeRenderer.setColor(0.88f, 0.78f, 0.64f, 1f);
        shapeRenderer.rect(x + 13f, y + 51f + breathe, 5f, 3f);
        shapeRenderer.rect(x + 25f, y + 51f + breathe, 5f, 3f);

        float armReach = enemy.isAttacking() ? 14f * enemy.getAttackProgress() : 0f;
        shapeRenderer.setColor(enemy.getCoatR() * 0.75f, enemy.getCoatG() * 0.75f, enemy.getCoatB() * 0.75f, 1f);
        shapeRenderer.rect(x + (dir > 0 ? 30f : -4f) + dir * armReach, y + 30f, 8f, 24f);
        shapeRenderer.rect(x + (dir > 0 ? 2f : 34f), y + 28f, 8f, 22f);
        shapeRenderer.setColor(0.055f, 0.058f, 0.064f, 1f);
        shapeRenderer.rect(x + 7f + walk, y, 9f, 14f);
        shapeRenderer.rect(x + 24f - walk, y, 9f, 14f);
    }

    private void renderKnifeSlash() {
        if (knifeSwingTimer <= 0f || !knifeCollected) {
            return;
        }
        float progress = 1f - knifeSwingTimer / KNIFE_SWING_TIME;
        float dir = player.isFacingRight() ? 1f : -1f;
        float centerX = player.getBounds().x + player.getBounds().width * 0.5f + dir * 16f;
        float centerY = player.getBounds().y + player.getBounds().height * 0.58f;
        float sweepStart = player.isFacingRight() ? -62f : 242f;
        float sweepEnd = player.isFacingRight() ? 62f : 118f;
        float headAngle = MathUtils.lerp(sweepStart, sweepEnd, progress);
        float fade = MathUtils.sin(progress * MathUtils.PI);

        for (int i = 0; i < 7; i++) {
            float t = i / 6f;
            float angle = headAngle - dir * t * 58f;
            float radians = angle * MathUtils.degreesToRadians;
            float innerRadius = 22f + t * 5f;
            float outerRadius = 64f - t * 6f;
            float innerX = centerX + MathUtils.cos(radians) * innerRadius;
            float innerY = centerY + MathUtils.sin(radians) * (innerRadius * 0.72f);
            float outerX = centerX + MathUtils.cos(radians) * outerRadius;
            float outerY = centerY + MathUtils.sin(radians) * (outerRadius * 0.72f);
            float alpha = fade * (1f - t * 0.12f);
            float thickness = 10f - t * 1.05f;

            shapeRenderer.setColor(0.10f, 0.92f, 1f, 0.16f * alpha);
            shapeRenderer.rectLine(innerX - dir * 4f, innerY - 2f, outerX + dir * 6f, outerY + 2f, thickness + 10f);
            shapeRenderer.setColor(0.78f, 0.96f, 1f, 0.52f * alpha);
            shapeRenderer.rectLine(innerX, innerY, outerX, outerY, thickness);
        }

        float tipRadians = headAngle * MathUtils.degreesToRadians;
        float tipX = centerX + MathUtils.cos(tipRadians) * 68f;
        float tipY = centerY + MathUtils.sin(tipRadians) * 48f;
        shapeRenderer.setColor(1f, 1f, 1f, 0.86f * fade);
        shapeRenderer.circle(tipX, tipY, 5f, 12);
        shapeRenderer.setColor(0.96f, 0.98f, 1f, 0.78f * fade);
        shapeRenderer.rectLine(centerX + dir * 4f, centerY, tipX, tipY, 3.5f);
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
                + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"
                + "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"
                + "«»№…—";
        BitmapFont generatedFont = generator.generateFont(parameter);
        generator.dispose();
        return generatedFont;
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

    private void renderLyingPlayer() {
        float x = BEDROOM_BED.x + 76f;
        float y = BEDROOM_BED.y + 34f;
        float breathe = MathUtils.sin(time * 2.2f) * 1.4f;

        shapeRenderer.setColor(0.10f, 0.018f, 0.024f, 1f);
        shapeRenderer.rect(x + 24f, y + breathe, 104f, 24f);
        shapeRenderer.setColor(0.90f, 0.82f, 0.70f, 1f);
        shapeRenderer.rect(x + 8f, y + 3f + breathe, 24f, 20f);
        shapeRenderer.setColor(0.02f, 0.02f, 0.025f, 1f);
        shapeRenderer.rect(x + 6f, y + 18f + breathe, 30f, 8f);
        shapeRenderer.setColor(0.92f, 0.90f, 0.80f, 1f);
        shapeRenderer.rect(x + 12f, y + 20f + breathe, 20f, 3f);
        shapeRenderer.setColor(0.19f, 0.04f, 0.05f, 1f);
        shapeRenderer.rect(x + 112f, y + 2f + breathe, 28f, 7f);
        shapeRenderer.rect(x + 114f, y + 18f + breathe, 24f, 7f);
        shapeRenderer.setColor(0.80f, 0.08f, 0.08f, 1f);
        shapeRenderer.rect(x + 36f, y + 22f + breathe, 76f, 5f);
    }

    private void drawHero(float x, float y, float scale, float motion, boolean worldScale) {
        float walk = MathUtils.sin(time * 12f) * MathUtils.clamp(Math.abs(motion) / Constants.PLAYER_SPEED, 0f, 1f);
        float breathe = MathUtils.sin(time * 3f) * (worldScale ? 1.5f : 3.5f);
        float dir = player.isFacingRight() ? 1f : -1f;
        float s = scale;

        shapeRenderer.setColor(0.90f, 0.82f, 0.70f, 1f);
        shapeRenderer.rect(x + 11f * s, y + (42f * s) + breathe, 18f * s, 18f * s);
        shapeRenderer.setColor(0.10f, 0.018f, 0.024f, 1f);
        shapeRenderer.rect(x + 5f * s, y + 8f * s, 30f * s, (40f * s) + breathe * 0.35f);
        shapeRenderer.setColor(0.80f, 0.08f, 0.08f, 1f);
        shapeRenderer.rect(x + 7f * s, y + (50f * s) + breathe, 26f * s, 5f * s);
        shapeRenderer.setColor(0.02f, 0.02f, 0.025f, 1f);
        shapeRenderer.rect(x + 8f * s, y + (48f * s) + breathe, 28f * s, 8f * s);
        shapeRenderer.setColor(0.92f, 0.90f, 0.80f, 1f);
        shapeRenderer.rect(x + 11f * s, y + (51f * s) + breathe, 22f * s, 3f * s);
        shapeRenderer.setColor(0.74f, 0.83f, 0.76f, 1f);
        shapeRenderer.rect(x + (dir > 0 ? 25f : 10f) * s, y + (48f * s) + breathe, 5f * s, 4f * s);
        shapeRenderer.setColor(0.19f, 0.04f, 0.05f, 1f);
        shapeRenderer.rect(x + (2f + walk * 3f) * s, y + 27f * s, 7f * s, 20f * s);
        shapeRenderer.rect(x + (31f - walk * 3f) * s, y + 27f * s, 7f * s, 20f * s);
        shapeRenderer.setColor(0.05f, 0.06f, 0.065f, 1f);
        shapeRenderer.rect(x + (7f + walk * 7f) * s, y, 9f * s, 12f * s);
        shapeRenderer.rect(x + (24f - walk * 7f) * s, y, 9f * s, 12f * s);
    }

    private void renderHud() {
        font.setColor(Color.WHITE);
        String place;
        if (locationIndex == -1) {
            place = "Комната";
        } else
        if (locationIndex == 0) {
            place = "Часовня голода";
        } else if (locationIndex == 1) {
            place = "Ангельский двигатель";
        } else {
            place = "Подземные кельи";
        }
        font.draw(batch, place, 22, uiCamera.viewportHeight - 18f);
        font.draw(batch, "A/D ходьба | SPACE прыжок | E говорить/использовать | TAB инвентарь", 22, uiCamera.viewportHeight - 44f);
        if (knifeCollected) {
            font.draw(batch, "ЛКМ - удар ножом", 22, uiCamera.viewportHeight - 70f);
        }

        if (!dialogueVisible) {
            if (locationIndex == -1 && isNear(player.getBounds(), BEDROOM_BED, 80f)) {
                font.draw(batch, lyingInBed ? "Нажми E, чтобы встать" : "Нажми E, чтобы лечь", uiCamera.viewportWidth / 2f - 112f, 116f);
            }
            if (locationIndex == 0 && isNear(player.getBounds(), SCHOLAR_BOUNDS, 80f)) {
                font.draw(batch, "Нажми E", uiCamera.viewportWidth / 2f - 42f, 116f);
            }
            if (locationIndex == 0 && isNear(player.getBounds(), CHAPEL_BEDROOM_DOOR, 70f)) {
                font.draw(batch, "Нажми E, чтобы вернуться", uiCamera.viewportWidth / 2f - 112f, 92f);
            }
            Rectangle door = getActiveDoor();
            if (!lyingInBed && isNear(player.getBounds(), door, 70f)) {
                font.draw(batch, "Нажми E, чтобы войти", uiCamera.viewportWidth / 2f - 96f, 92f);
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
        if (!prologueActive || dialogueLine >= PROLOGUE_TITLES.length) {
            return;
        }

        font.setColor(0.96f, 0.86f, 0.72f, 1f);
        font.draw(batch, PROLOGUE_TITLES[dialogueLine], uiCamera.viewportWidth / 2f - 150f, uiCamera.viewportHeight - 54f);
        font.setColor(0.86f, 0.14f, 0.12f, 1f);
        font.draw(batch, "E - дальше", uiCamera.viewportWidth - 132f, uiCamera.viewportHeight - 54f);
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
        font.draw(batch, "Пауза", VIRTUAL_WIDTH / 2f - 34f, VIRTUAL_HEIGHT / 2f + 204f);

        for (int i = 0; i < pauseButtonBounds.length; i++) {
            Rectangle bounds = pauseButtonBounds[i];
            font.setColor(i == hoveredPauseButton ? Color.WHITE : new Color(0.86f, 0.90f, 0.86f, 1f));
            font.draw(batch, getPauseButtonLabel(i), bounds.x + 34f, bounds.y + 35f);
        }

        if (pausePanel == PausePanel.ACHIEVEMENTS) {
            font.setColor(0.90f, 0.94f, 0.88f, 1f);
            font.draw(batch, "Достижения", VIRTUAL_WIDTH / 2f - 78f, VIRTUAL_HEIGHT / 2f - 94f);
            font.setColor(0.70f, 0.78f, 0.74f, 1f);
            font.draw(batch, "Паломник | Осколок света | Подземный зов", VIRTUAL_WIDTH / 2f - 184f, VIRTUAL_HEIGHT / 2f - 126f);
            font.draw(batch, "Прогресс будет отмечаться по ходу игры.", VIRTUAL_WIDTH / 2f - 168f, VIRTUAL_HEIGHT / 2f - 154f);
        } else if (pausePanel == PausePanel.SETTINGS) {
            font.setColor(0.90f, 0.94f, 0.88f, 1f);
            font.draw(batch, "Настройки", VIRTUAL_WIDTH / 2f - 68f, VIRTUAL_HEIGHT / 2f - 94f);
            font.setColor(0.70f, 0.78f, 0.74f, 1f);
            font.draw(batch, "Раздел в разработке.", VIRTUAL_WIDTH / 2f - 102f, VIRTUAL_HEIGHT / 2f - 126f);
            font.draw(batch, "Нажмите Escape или Продолжить, чтобы вернуться.", VIRTUAL_WIDTH / 2f - 208f, VIRTUAL_HEIGHT / 2f - 154f);
        }
    }

    private String getPauseButtonLabel(int index) {
        if (index == 3) {
            return "Сохранить";
        }
        if (index == 4) {
            return "Продолжить";
        }
        return PAUSE_BUTTON_LABELS[index];
    }

    private void renderInventoryPanel() {
        float panelX = uiCamera.viewportWidth / 2f - 320f;
        float panelY = uiCamera.viewportHeight / 2f - 215f;

        shapeRenderer.setColor(0.018f, 0.022f, 0.024f, 1f);
        shapeRenderer.rect(panelX, panelY, 640f, 430f);
        shapeRenderer.setColor(0.42f, 0.54f, 0.49f, 1f);
        shapeRenderer.rect(panelX, panelY + 392f, 640f, 8f);
        shapeRenderer.setColor(0.075f, 0.087f, 0.087f, 1f);
        shapeRenderer.rect(panelX + 46f, panelY + 92f, 190f, 250f);
        shapeRenderer.setColor(0.30f, 0.43f, 0.38f, 1f);
        shapeRenderer.rect(panelX + 58f, panelY + 104f, 166f, 226f);

        drawHero(panelX + 124f, panelY + 144f, 2.2f, MathUtils.sin(time * 4f) * 80f, false);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                drawSlot(panelX + 340f + col * 54f, panelY + 210f - row * 54f);
            }
        }

        for (int col = 0; col < 6; col++) {
            drawSlot(panelX + 164f + col * 54f, panelY + 28f);
        }
    }

    private void drawSlot(float x, float y) {
        shapeRenderer.setColor(0.055f, 0.065f, 0.065f, 1f);
        shapeRenderer.rect(x, y, 46f, 46f);
        shapeRenderer.setColor(0.15f, 0.18f, 0.17f, 1f);
        shapeRenderer.rect(x + 4f, y + 4f, 38f, 38f);
    }

    private void renderDialoguePortraits(float pulse) {
        boolean adamSpeaking = "???".equals(currentSpeakers[dialogueLine]);
        boolean bookSpeaking = "Священная книга".equals(currentSpeakers[dialogueLine]);
        boolean engineSpeaking = "Ангельский двигатель".equals(currentSpeakers[dialogueLine]);
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
        drawWrappedText(visibleText, 176f, 116f, 70, 26f);
        font.setColor(0.62f, 0.78f, 0.72f, 1f);
        font.draw(batch, "E", width - 116f, 76f);
        font.draw(batch, getVisibleDialogueCharacters() < currentText[dialogueLine].length() ? "пропуск" : "дальше", width - 96f, 76f);
    }

    private void drawWrappedText(String text, float x, float y, int maxChars, float lineHeight) {
        String remaining = text;
        int line = 0;
        while (remaining.length() > maxChars && line < 3) {
            int splitAt = remaining.lastIndexOf(' ', maxChars);
            if (splitAt <= 0) {
                splitAt = maxChars;
            }
            font.draw(batch, remaining.substring(0, splitAt), x, y - line * lineHeight);
            remaining = remaining.substring(splitAt).trim();
            line++;
        }
        if (!remaining.isEmpty() && line < 4) {
            font.draw(batch, remaining, x, y - line * lineHeight);
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
    }

    private enum PausePanel {
        NONE,
        ACHIEVEMENTS,
        SETTINGS
    }
}
