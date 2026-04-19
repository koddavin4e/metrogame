package com.metrohorror.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.metrohorror.game.entities.Player;
import com.metrohorror.game.systems.CameraSystem;
import com.metrohorror.game.systems.InventorySystem;
import com.metrohorror.game.ui.InventoryUI;
import com.metrohorror.game.util.Constants;
import com.metrohorror.game.world.GameMap;
import com.metrohorror.game.world.Platform;

public class FirstScreen implements Screen {
    private static final Rectangle SCHOLAR_BOUNDS = new Rectangle(610f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 42f, 72f);
    private static final Rectangle FIRST_DOOR = new Rectangle(2380f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 78f, 140f);
    private static final Rectangle SECOND_DOOR = new Rectangle(140f, Constants.GROUND_Y + Constants.GROUND_HEIGHT, 78f, 140f);
    private static final float TYPE_SPEED = 42f;

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

    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    private Player player;
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

    private String[] currentSpeakers = PROLOGUE_SPEAKERS;
    private String[] currentText = PROLOGUE_TEXT;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, 1280, 720);

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = createGameFont();

        player = new Player(140, 240);
        gameMap = new GameMap();
        inventorySystem = new InventorySystem();
        cameraSystem = new CameraSystem();
        inventoryUI = new InventoryUI();
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.015f, 0.010f, 0.012f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!prologueActive) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            renderWorld();
            shapeRenderer.end();
        }

        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (prologueActive) {
            renderPrologueScene();
        }
        renderUiPanels();
        shapeRenderer.end();

        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        if (!prologueActive) {
            renderHud();
            inventoryUI.render(batch, font, inventorySystem, inventoryVisible, uiCamera.viewportWidth, uiCamera.viewportHeight);
        } else {
            renderPrologueTitle();
        }
        renderDialogue();
        batch.end();
    }

    private void update(float delta) {
        time += delta;
        if (dialogueVisible) {
            dialogueTypeTimer += delta;
        }

        handleInput();
        if (prologueActive) {
            uiCamera.update();
            return;
        }

        player.applyGravity(delta);
        player.update(delta);
        resolveWorldCollisions();
        cameraSystem.follow(camera, player, delta);
        uiCamera.update();
    }

    private void handleInput() {
        player.stopX();

        if (!dialogueVisible && !inventoryVisible && !prologueActive) {
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                player.moveLeft();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                player.moveRight();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                player.jump();
            }
        }

        if (!prologueActive && Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            inventoryVisible = !inventoryVisible;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            handleAction();
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
                dialogueVisible = false;
                dialogueLine = 0;
                dialogueTypeTimer = 0f;
                if (prologueActive) {
                    prologueActive = false;
                    startDialogue(CHAPEL_SPEAKERS, CHAPEL_TEXT);
                }
            }
            return;
        }

        if (locationIndex == 0 && isNear(player.getBounds(), SCHOLAR_BOUNDS, 80f)) {
            startDialogue(CHAPEL_SPEAKERS, CHAPEL_TEXT);
            return;
        }

        Rectangle door = locationIndex == 0 ? FIRST_DOOR : SECOND_DOOR;
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

    private void changeLocation() {
        locationIndex = locationIndex == 0 ? 1 : 0;
        dialogueVisible = false;
        dialogueTypeTimer = 0f;
        inventoryVisible = false;
        player.setX(locationIndex == 0 ? FIRST_DOOR.x - 110f : SECOND_DOOR.x + 120f);
        player.setY(Constants.GROUND_Y + Constants.GROUND_HEIGHT);
        player.setVelocityY(0f);
        camera.position.x = player.getX();
        camera.update();

        if (locationIndex == 1) {
            startDialogue(ENGINE_SPEAKERS, ENGINE_TEXT);
        }
    }

    private void resolveWorldCollisions() {
        boolean landed = false;
        float groundTop = gameMap.getGround().y + gameMap.getGround().height;
        if (player.getBounds().y <= groundTop) {
            player.setY(groundTop);
            player.setVelocityY(0);
            landed = true;
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
        if (player.getX() > Constants.WORLD_WIDTH - Constants.PLAYER_WIDTH) {
            player.setX(Constants.WORLD_WIDTH - Constants.PLAYER_WIDTH);
        }
    }

    private void renderWorld() {
        if (locationIndex == 0) {
            renderRuinedChapelStation();
            renderScholar();
            renderDoor(FIRST_DOOR, 0.12f, 0.38f, 0.45f);
        } else {
            renderAngelEngineLine();
            renderDoor(SECOND_DOOR, 0.36f, 0.18f, 0.12f);
        }

        renderPlayer();
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
                + "«»№…";
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
        String place = locationIndex == 0 ? "Часовня голода" : "Ангельский двигатель";
        font.draw(batch, place, 22, uiCamera.viewportHeight - 18f);
        font.draw(batch, "A/D ходьба | SPACE прыжок | E говорить/использовать | TAB инвентарь", 22, uiCamera.viewportHeight - 44f);

        if (!dialogueVisible) {
            if (locationIndex == 0 && isNear(player.getBounds(), SCHOLAR_BOUNDS, 80f)) {
                font.draw(batch, "Нажми E", uiCamera.viewportWidth / 2f - 42f, 116f);
            }
            Rectangle door = locationIndex == 0 ? FIRST_DOOR : SECOND_DOOR;
            if (isNear(player.getBounds(), door, 70f)) {
                font.draw(batch, "Нажми E, чтобы войти", uiCamera.viewportWidth / 2f - 96f, 92f);
            }
        }
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
