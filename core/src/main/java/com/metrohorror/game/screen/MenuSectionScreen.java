package com.metrohorror.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.metrohorror.game.MetroHorrorGame;

public class MenuSectionScreen implements Screen {
    public enum Section {
        SETTINGS,
        ACHIEVEMENTS
    }

    private static final Rectangle BACK_BUTTON = new Rectangle(0f, 0f, 360f, 54f);
    private static final Rectangle VOLUME_SLIDER = new Rectangle(0f, 0f, 300f, 12f);
    private static final Rectangle MUSIC_TOGGLE = new Rectangle(0f, 0f, 40f, 40f);
    private static final Rectangle FULLSCREEN_TOGGLE = new Rectangle(0f, 0f, 40f, 40f);

    private final MetroHorrorGame game;
    private final Section section;
    private final Screen returnScreen;
    private final Vector3 pointer = new Vector3();

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont textFont;
    private GlyphLayout glyphLayout;
    private Preferences preferences;

    private boolean hoveredBackButton;
    private boolean hoveredVolumeSlider;
    private boolean hoveredMusicToggle;
    private boolean hoveredFullscreenToggle;
    private boolean draggingVolume;
    private float volume;
    private boolean musicEnabled;
    private boolean fullscreenEnabled;
    private int windowedWidth;
    private int windowedHeight;

    public MenuSectionScreen(MetroHorrorGame game, Section section, Screen returnScreen) {
        this.game = game;
        this.section = section;
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
        loadSettings();
    }

    private void updateLayout() {
        float centerX = camera.viewportWidth * 0.5f;
        float centerY = camera.viewportHeight * 0.5f;
        BACK_BUTTON.setPosition(centerX - BACK_BUTTON.width * 0.5f, centerY - 210f);
        VOLUME_SLIDER.setPosition(centerX - 80f, centerY + 22f);
        MUSIC_TOGGLE.setPosition(centerX + 180f, centerY - 46f);
        FULLSCREEN_TOGGLE.setPosition(centerX + 180f, centerY - 110f);
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

        hoveredBackButton = BACK_BUTTON.contains(pointer.x, pointer.y);
        hoveredVolumeSlider = isSettingsScreen() && VOLUME_SLIDER.contains(pointer.x, pointer.y);
        hoveredMusicToggle = isSettingsScreen() && MUSIC_TOGGLE.contains(pointer.x, pointer.y);
        hoveredFullscreenToggle = isSettingsScreen() && FULLSCREEN_TOGGLE.contains(pointer.x, pointer.y);

        if (isSettingsScreen()) {
            updateSettingsInput();
        }

        if ((Gdx.input.justTouched() && hoveredBackButton) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            goBack();
        }
    }

    private void updateSettingsInput() {
        if (Gdx.input.justTouched()) {
            if (hoveredVolumeSlider) {
                draggingVolume = true;
                updateVolumeFromPointer();
            } else if (hoveredMusicToggle) {
                musicEnabled = !musicEnabled;
                saveSettings();
            } else if (hoveredFullscreenToggle) {
                toggleFullscreen();
            }
        }

        if (draggingVolume && Gdx.input.isTouched()) {
            updateVolumeFromPointer();
        } else if (!Gdx.input.isTouched()) {
            draggingVolume = false;
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
        float panelY = camera.viewportHeight * 0.5f - 170f;

        shapeRenderer.setColor(0.030f, 0.035f, 0.038f, 0.92f);
        shapeRenderer.rect(panelX, panelY, 680f, 330f);
        shapeRenderer.setColor(0.48f, 0.68f, 0.62f, 0.86f);
        shapeRenderer.rect(panelX, panelY + 322f, 680f, 6f);
        shapeRenderer.setColor(0.58f, 0.14f, 0.13f, 0.80f);
        shapeRenderer.rect(panelX, panelY, 680f, 4f);

        if (isSettingsScreen()) {
            renderSettingsControls();
        }

        renderButton(BACK_BUTTON, hoveredBackButton);
    }

    private void renderButton(Rectangle bounds, boolean hovered) {
        shapeRenderer.setColor(0.004f, 0.005f, 0.006f, 0.52f);
        shapeRenderer.rect(bounds.x - 4f, bounds.y - 4f, bounds.width + 8f, bounds.height + 8f);
        if (hovered) {
            shapeRenderer.setColor(0.62f, 0.18f, 0.16f, 0.88f);
        } else {
            shapeRenderer.setColor(0.075f, 0.085f, 0.090f, 0.76f);
        }
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.setColor(hovered ? 0.96f : 0.36f, hovered ? 0.78f : 0.58f, hovered ? 0.66f : 0.54f, 0.92f);
        shapeRenderer.rect(bounds.x, bounds.y + bounds.height - 5f, bounds.width, 5f);
    }

    private void renderSettingsControls() {
        float filledWidth = VOLUME_SLIDER.width * volume;
        float knobX = VOLUME_SLIDER.x + filledWidth;

        shapeRenderer.setColor(0.004f, 0.005f, 0.006f, 0.52f);
        shapeRenderer.rect(VOLUME_SLIDER.x - 4f, VOLUME_SLIDER.y - 8f, VOLUME_SLIDER.width + 8f, VOLUME_SLIDER.height + 16f);
        shapeRenderer.setColor(0.075f, 0.085f, 0.090f, 0.86f);
        shapeRenderer.rect(VOLUME_SLIDER.x, VOLUME_SLIDER.y, VOLUME_SLIDER.width, VOLUME_SLIDER.height);
        shapeRenderer.setColor(0.20f, 0.46f, 0.42f, 0.92f);
        shapeRenderer.rect(VOLUME_SLIDER.x, VOLUME_SLIDER.y, filledWidth, VOLUME_SLIDER.height);
        shapeRenderer.setColor(hoveredVolumeSlider || draggingVolume ? 0.96f : 0.62f, hoveredVolumeSlider || draggingVolume ? 0.78f : 0.68f, hoveredVolumeSlider || draggingVolume ? 0.66f : 0.62f, 1f);
        shapeRenderer.rect(knobX - 7f, VOLUME_SLIDER.y - 12f, 14f, 36f);

        renderToggle(MUSIC_TOGGLE, musicEnabled, hoveredMusicToggle);
        renderToggle(FULLSCREEN_TOGGLE, fullscreenEnabled, hoveredFullscreenToggle);
    }

    private void renderToggle(Rectangle bounds, boolean enabled, boolean hovered) {
        shapeRenderer.setColor(0.004f, 0.005f, 0.006f, 0.52f);
        shapeRenderer.rect(bounds.x - 4f, bounds.y - 4f, bounds.width + 8f, bounds.height + 8f);
        if (enabled) {
            shapeRenderer.setColor(hovered ? 0.24f : 0.20f, hovered ? 0.58f : 0.46f, hovered ? 0.52f : 0.42f, 0.92f);
        } else {
            shapeRenderer.setColor(hovered ? 0.62f : 0.075f, hovered ? 0.18f : 0.085f, hovered ? 0.16f : 0.090f, 0.86f);
        }
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.setColor(0.86f, 0.90f, 0.86f, enabled ? 0.95f : 0.35f);
        shapeRenderer.rect(bounds.x + 9f, bounds.y + 18f, 8f, 8f);
        shapeRenderer.rect(bounds.x + 17f, bounds.y + 10f, 14f, 8f);
    }

    private void renderText() {
        float centerX = camera.viewportWidth * 0.5f;
        float centerY = camera.viewportHeight * 0.5f;

        titleFont.setColor(0.96f, 0.90f, 0.82f, 1f);
        drawCentered(titleFont, getTitle(), centerX, centerY + 102f);

        if (isSettingsScreen()) {
            renderSettingsText();
        } else {
            textFont.setColor(0.70f, 0.78f, 0.74f, 1f);
            drawCentered(textFont, "\u0417\u0434\u0435\u0441\u044c \u043f\u043e\u0437\u0436\u0435 \u043f\u043e\u044f\u0432\u0438\u0442\u0441\u044f \u0441\u043f\u0438\u0441\u043e\u043a \u0434\u043e\u0441\u0442\u0438\u0436\u0435\u043d\u0438\u0439.", centerX, centerY);
            drawCentered(textFont, "\u041d\u0430\u0436\u043c\u0438\u0442\u0435 Escape \u0438\u043b\u0438 \u043a\u043d\u043e\u043f\u043a\u0443 \u043d\u0438\u0436\u0435, \u0447\u0442\u043e\u0431\u044b \u0432\u0435\u0440\u043d\u0443\u0442\u044c\u0441\u044f.", centerX, centerY - 44f);
        }

        textFont.setColor(hoveredBackButton ? Color.WHITE : new Color(0.86f, 0.90f, 0.86f, 1f));
        drawCentered(textFont, "\u041d\u0430\u0437\u0430\u0434", BACK_BUTTON.x + BACK_BUTTON.width * 0.5f, BACK_BUTTON.y + 36f);
    }

    private void renderSettingsText() {
        float centerX = camera.viewportWidth * 0.5f;
        float centerY = camera.viewportHeight * 0.5f;

        textFont.setColor(0.86f, 0.90f, 0.86f, 1f);
        textFont.draw(batch, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c", centerX - 220f, centerY + 44f);
        textFont.draw(batch, Math.round(volume * 100f) + "%", centerX + 250f, centerY + 44f);
        textFont.draw(batch, "\u041c\u0443\u0437\u044b\u043a\u0430", centerX - 220f, centerY - 16f);
        textFont.draw(batch, musicEnabled ? "\u0412\u043a\u043b\u044e\u0447\u0435\u043d\u0430" : "\u0412\u044b\u043a\u043b\u044e\u0447\u0435\u043d\u0430", centerX - 80f, centerY - 16f);
        textFont.draw(batch, "\u041f\u043e\u043b\u043d\u044b\u0439 \u044d\u043a\u0440\u0430\u043d", centerX - 220f, centerY - 80f);
        textFont.draw(batch, fullscreenEnabled ? "\u0412\u043a\u043b\u044e\u0447\u0435\u043d" : "\u0412\u044b\u043a\u043b\u044e\u0447\u0435\u043d", centerX - 20f, centerY - 80f);

        textFont.setColor(0.70f, 0.78f, 0.74f, 1f);
        drawCentered(textFont, "\u041d\u0430\u0436\u043c\u0438\u0442\u0435 Escape \u0438\u043b\u0438 \u043a\u043d\u043e\u043f\u043a\u0443 \u043d\u0438\u0436\u0435, \u0447\u0442\u043e\u0431\u044b \u0432\u0435\u0440\u043d\u0443\u0442\u044c\u0441\u044f.", centerX, centerY - 132f);
    }

    private String getTitle() {
        if (section == Section.ACHIEVEMENTS) {
            return "\u0414\u043e\u0441\u0442\u0438\u0436\u0435\u043d\u0438\u044f";
        }
        return "\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438";
    }

    private boolean isSettingsScreen() {
        return section == Section.SETTINGS;
    }

    private void loadSettings() {
        preferences = Gdx.app.getPreferences("metrohorror-settings");
        volume = preferences.getFloat("volume", 0.8f);
        musicEnabled = preferences.getBoolean("musicEnabled", true);
        fullscreenEnabled = preferences.getBoolean("fullscreen", Gdx.graphics.isFullscreen());
        windowedWidth = preferences.getInteger("windowedWidth", 1280);
        windowedHeight = preferences.getInteger("windowedHeight", 720);

        if (fullscreenEnabled && !Gdx.graphics.isFullscreen()) {
            applyFullscreen();
        }
    }

    private void saveSettings() {
        if (preferences == null) {
            return;
        }
        preferences.putFloat("volume", volume);
        preferences.putBoolean("musicEnabled", musicEnabled);
        preferences.putBoolean("fullscreen", fullscreenEnabled);
        preferences.putInteger("windowedWidth", windowedWidth);
        preferences.putInteger("windowedHeight", windowedHeight);
        preferences.flush();
    }

    private void updateVolumeFromPointer() {
        volume = MathUtils.clamp((pointer.x - VOLUME_SLIDER.x) / VOLUME_SLIDER.width, 0f, 1f);
        saveSettings();
    }

    private void toggleFullscreen() {
        fullscreenEnabled = !fullscreenEnabled;
        if (fullscreenEnabled) {
            windowedWidth = Gdx.graphics.getWidth();
            windowedHeight = Gdx.graphics.getHeight();
            applyFullscreen();
        } else {
            Gdx.graphics.setWindowedMode(windowedWidth, windowedHeight);
        }
        saveSettings();
    }

    private void applyFullscreen() {
        Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
        Gdx.graphics.setFullscreenMode(displayMode);
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
                + "\u0410\u0411\u0412\u0413\u0414\u0415\u0401\u0416\u0417\u0418\u0419\u041a\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042a\u042b\u042c\u042d\u042e\u042f"
                + "\u0430\u0431\u0432\u0433\u0434\u0435\u0451\u0436\u0437\u0438\u0439\u043a\u043b\u043c\u043d\u043e\u043f\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044a\u044b\u044c\u044d\u044e\u044f"
                + "\u00ab\u00bb\u2116\u2026.,!?-:()|/";
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
