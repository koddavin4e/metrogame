package com.metrohorror.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.metrohorror.game.MetroHorrorGame;

public class SaveSlotScreen implements Screen {
    public enum Mode {
        SAVE,
        LOAD
    }

    private static final Rectangle BACK_BUTTON = new Rectangle(0f, 0f, 360f, 54f);

    private final MetroHorrorGame game;
    private final Mode mode;
    private final Screen returnScreen;
    private final Rectangle[] slotBounds = new Rectangle[FirstScreen.getSaveSlotCount()];
    private final Vector3 pointer = new Vector3();

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont textFont;
    private GlyphLayout glyphLayout;
    private int hoveredSlot = -1;
    private boolean hoveredBackButton;
    private String statusText = "";

    public SaveSlotScreen(MetroHorrorGame game, Mode mode, Screen returnScreen) {
        this.game = game;
        this.mode = mode;
        this.returnScreen = returnScreen;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.apply(true);

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        titleFont = createFont(42);
        textFont = createFont(24);
        glyphLayout = new GlyphLayout();
        updateLayout();
    }

    private void updateLayout() {
        float slotWidth = 520f;
        float slotHeight = 62f;
        float slotGap = 18f;
        float slotX = camera.viewportWidth * 0.5f - slotWidth * 0.5f;
        float slotY = camera.viewportHeight * 0.5f + 50f;
        for (int i = 0; i < slotBounds.length; i++) {
            slotBounds[i] = new Rectangle(slotX, slotY - i * (slotHeight + slotGap), slotWidth, slotHeight);
        }
        BACK_BUTTON.setPosition(camera.viewportWidth * 0.5f - BACK_BUTTON.width * 0.5f, camera.viewportHeight * 0.5f - 230f);
    }

    @Override
    public void render(float delta) {
        updateInput();

        Gdx.gl.glClearColor(0.018f, 0.018f, 0.020f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        camera.update();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderPanels();
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderText();
        batch.end();
    }

    private void updateInput() {
        pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        viewport.unproject(pointer);

        hoveredSlot = -1;
        for (int i = 0; i < slotBounds.length; i++) {
            if (slotBounds[i].contains(pointer.x, pointer.y)) {
                hoveredSlot = i;
                break;
            }
        }
        hoveredBackButton = BACK_BUTTON.contains(pointer.x, pointer.y);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || (Gdx.input.justTouched() && hoveredBackButton)) {
            goBack();
            return;
        }

        if (Gdx.input.justTouched() && hoveredSlot >= 0) {
            activateSlot(hoveredSlot);
        }
    }

    private void activateSlot(int slot) {
        if (mode == Mode.SAVE) {
            if (returnScreen instanceof FirstScreen) {
                ((FirstScreen) returnScreen).saveToSlot(slot);
                statusText = "РЎР»РѕС‚ " + (slot + 1) + " СЃРѕС…СЂР°РЅРµРЅ.";
            }
            return;
        }

        if (FirstScreen.hasSave(slot)) {
            game.setScreen(new FirstScreen(game, slot));
        } else {
            statusText = "Р­С‚РѕС‚ СЃР»РѕС‚ РїСѓСЃС‚.";
        }
    }

    private void goBack() {
        if (returnScreen != null) {
            game.setScreen(returnScreen);
        } else {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void renderPanels() {
        float panelX = camera.viewportWidth * 0.5f - 340f;
        float panelY = camera.viewportHeight * 0.5f - 190f;

        shapeRenderer.setColor(0.030f, 0.035f, 0.038f, 0.92f);
        shapeRenderer.rect(panelX, panelY, 680f, 350f);
        shapeRenderer.setColor(0.48f, 0.68f, 0.62f, 0.86f);
        shapeRenderer.rect(panelX, panelY + 342f, 680f, 6f);
        shapeRenderer.setColor(0.58f, 0.14f, 0.13f, 0.80f);
        shapeRenderer.rect(panelX, panelY, 680f, 4f);

        for (int i = 0; i < slotBounds.length; i++) {
            renderButton(slotBounds[i], i == hoveredSlot, FirstScreen.hasSave(i));
        }
        renderButton(BACK_BUTTON, hoveredBackButton, false);
    }

    private void renderButton(Rectangle bounds, boolean hovered, boolean filled) {
        shapeRenderer.setColor(0.004f, 0.005f, 0.006f, 0.52f);
        shapeRenderer.rect(bounds.x - 4f, bounds.y - 4f, bounds.width + 8f, bounds.height + 8f);
        if (hovered) {
            shapeRenderer.setColor(0.62f, 0.18f, 0.16f, 0.88f);
        } else if (filled) {
            shapeRenderer.setColor(0.20f, 0.46f, 0.42f, 0.78f);
        } else {
            shapeRenderer.setColor(0.075f, 0.085f, 0.090f, 0.76f);
        }
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.setColor(hovered ? 0.96f : 0.36f, hovered ? 0.78f : 0.58f, hovered ? 0.66f : 0.54f, 0.92f);
        shapeRenderer.rect(bounds.x, bounds.y + bounds.height - 5f, bounds.width, 5f);
    }

    private void renderText() {
        titleFont.setColor(0.96f, 0.90f, 0.82f, 1f);
        drawCentered(titleFont, mode == Mode.SAVE ? "РЎРѕС…СЂР°РЅРµРЅРёРµ" : "Р—Р°РіСЂСѓР·РєР°", camera.viewportWidth * 0.5f, camera.viewportHeight * 0.5f + 210f);

        for (int i = 0; i < slotBounds.length; i++) {
            Rectangle bounds = slotBounds[i];
            textFont.setColor(i == hoveredSlot ? Color.WHITE : new Color(0.86f, 0.90f, 0.86f, 1f));
            textFont.draw(batch, "РЎР»РѕС‚ " + (i + 1), bounds.x + 32f, bounds.y + 40f);
            textFont.setColor(new Color(0.70f, 0.78f, 0.74f, 1f));
            textFont.draw(batch, FirstScreen.getSaveSummary(i), bounds.x + 170f, bounds.y + 40f);
        }

        textFont.setColor(0.70f, 0.78f, 0.74f, 1f);
        drawCentered(textFont, statusText, camera.viewportWidth * 0.5f, camera.viewportHeight * 0.5f - 146f);

        textFont.setColor(hoveredBackButton ? Color.WHITE : new Color(0.86f, 0.90f, 0.86f, 1f));
        drawCentered(textFont, "РќР°Р·Р°Рґ", BACK_BUTTON.x + BACK_BUTTON.width * 0.5f, BACK_BUTTON.y + 36f);
    }

    private void drawCentered(BitmapFont font, String text, float centerX, float y) {
        glyphLayout.setText(font, text);
        font.draw(batch, text, centerX - glyphLayout.width * 0.5f, y);
    }

    private BitmapFont createFont(int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.absolute("C:/Windows/Fonts/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS
                + "РђР‘Р’Р“Р”Р•РЃР–Р—РР™РљР›РњРќРћРџР РЎРўРЈР¤РҐР¦Р§РЁР©РЄР«Р¬Р­Р®РЇ"
                + "Р°Р±РІРіРґРµС‘Р¶Р·РёР№РєР»РјРЅРѕРїСЂСЃС‚СѓС„С…С†С‡С€С‰СЉС‹СЊСЌСЋСЏ"
                + "В«В»в„–вЂ¦.,!?-:()|/";
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        updateLayout();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
        if (titleFont != null) {
            titleFont.dispose();
        }
        if (textFont != null) {
            textFont.dispose();
        }
    }
}
