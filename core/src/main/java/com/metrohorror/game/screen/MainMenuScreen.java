package com.metrohorror.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.metrohorror.game.MetroHorrorGame;

public class MainMenuScreen implements Screen {
    private static final float VIRTUAL_WIDTH = 1280f;
    private static final float VIRTUAL_HEIGHT = 720f;

    private final MetroHorrorGame game;
    private final Rectangle[] buttonBounds = new Rectangle[4];
    private final String[] buttonLabels = {
            "Новая игра",
            "Продолжить",
            "Достижения",
            "Настройки"
    };

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont menuFont;
    private GlyphLayout glyphLayout;
    private final Vector3 pointer = new Vector3();

    private int hoveredButton = -1;
    private boolean startNewGameRequested;

    public MainMenuScreen(MetroHorrorGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply(true);

        batch = new SpriteBatch();
        titleFont = createFont(46);
        menuFont = createFont(24);
        glyphLayout = new GlyphLayout();

        float buttonWidth = 360f;
        float buttonHeight = 48f;
        float buttonGap = 18f;
        float buttonX = VIRTUAL_WIDTH / 2f - buttonWidth / 2f;
        float buttonY = 390f;
        for (int i = 0; i < buttonBounds.length; i++) {
            buttonBounds[i] = new Rectangle(buttonX, buttonY - i * (buttonHeight + buttonGap), buttonWidth, buttonHeight);
        }
    }

    @Override
    public void render(float delta) {
        updateInput();

        Gdx.gl.glClearColor(0.018f, 0.018f, 0.020f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderText();
        batch.end();

        if (startNewGameRequested) {
            startNewGameRequested = false;
            game.setScreen(new FirstScreen(game));
        }
    }

    private void updateInput() {
        pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        viewport.unproject(pointer);

        hoveredButton = -1;
        for (int i = 0; i < buttonBounds.length; i++) {
            if (buttonBounds[i].contains(pointer.x, pointer.y)) {
                hoveredButton = i;
                break;
            }
        }

        if (Gdx.input.justTouched()) {
            if (hoveredButton == 0) {
                startNewGameRequested = true;
            } else if (hoveredButton == 1) {
                game.setScreen(new SaveSlotScreen(game, SaveSlotScreen.Mode.LOAD, null));
            } else if (hoveredButton == 2) {
                game.setScreen(new MenuSectionScreen(game, MenuSectionScreen.Section.ACHIEVEMENTS, null));
            } else if (hoveredButton == 3) {
                game.setScreen(new MenuSectionScreen(game, MenuSectionScreen.Section.SETTINGS, null));
            }
        }
    }

    private void renderText() {
        titleFont.setColor(0.96f, 0.90f, 0.82f, 1f);
        drawCentered(titleFont, "Paradise Disappear", VIRTUAL_WIDTH / 2f, 560f);

        for (int i = 0; i < buttonBounds.length; i++) {
            Rectangle bounds = buttonBounds[i];
            menuFont.setColor(i == hoveredButton ? Color.WHITE : new Color(0.72f, 0.74f, 0.72f, 1f));
            drawCentered(menuFont, buttonLabels[i], bounds.x + bounds.width / 2f, bounds.y + 34f);
        }
    }

    private void drawCentered(BitmapFont font, String text, float centerX, float y) {
        glyphLayout.setText(font, text);
        font.draw(batch, text, centerX - glyphLayout.width / 2f, y);
    }

    private BitmapFont createFont(int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.absolute("C:/Windows/Fonts/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS
                + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"
                + "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"
                + "«»№….,!?-:()";
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (titleFont != null) {
            titleFont.dispose();
        }
        if (menuFont != null) {
            menuFont.dispose();
        }
    }
}
