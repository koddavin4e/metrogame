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
<<<<<<< HEAD
import com.badlogic.gdx.utils.viewport.FitViewport;
=======
import com.badlogic.gdx.utils.viewport.ScreenViewport;
>>>>>>> f29aecc (Полный экран, масштабируемость)
import com.badlogic.gdx.utils.viewport.Viewport;
import com.metrohorror.game.MetroHorrorGame;

public class MainMenuScreen implements Screen {
<<<<<<< HEAD
    private static final float VIRTUAL_WIDTH = 1280f;
    private static final float VIRTUAL_HEIGHT = 720f;

=======
>>>>>>> f29aecc (Полный экран, масштабируемость)
    private final MetroHorrorGame game;
    private final Rectangle[] buttonBounds = new Rectangle[4];
    private final String[] buttonLabels = {
            "Новая игра",
            "Продолжить",
            "Достижения",
            "Настройки"
    };
<<<<<<< HEAD
=======
    private final Vector3 pointer = new Vector3();
>>>>>>> f29aecc (Полный экран, масштабируемость)

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont menuFont;
    private GlyphLayout glyphLayout;
<<<<<<< HEAD
    private final Vector3 pointer = new Vector3();

=======
>>>>>>> f29aecc (Полный экран, масштабируемость)
    private int hoveredButton = -1;
    private boolean startNewGameRequested;

    public MainMenuScreen(MetroHorrorGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
<<<<<<< HEAD
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
=======
        viewport = new ScreenViewport(camera);
>>>>>>> f29aecc (Полный экран, масштабируемость)
        viewport.apply(true);

        batch = new SpriteBatch();
        titleFont = createFont(46);
        menuFont = createFont(24);
        glyphLayout = new GlyphLayout();
<<<<<<< HEAD

        float buttonWidth = 360f;
        float buttonHeight = 48f;
        float buttonGap = 18f;
        float buttonX = VIRTUAL_WIDTH / 2f - buttonWidth / 2f;
        float buttonY = 390f;
=======
        updateLayout();
    }

    private void updateLayout() {
        float buttonWidth = 360f;
        float buttonHeight = 48f;
        float buttonGap = 18f;
        float buttonX = camera.viewportWidth * 0.5f - buttonWidth * 0.5f;
        float buttonY = camera.viewportHeight * 0.5f + 30f;
>>>>>>> f29aecc (Полный экран, масштабируемость)
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
<<<<<<< HEAD
        drawCentered(titleFont, "Paradise Disappear", VIRTUAL_WIDTH / 2f, 560f);
=======
        drawCentered(titleFont, "Paradise Disappear", camera.viewportWidth * 0.5f, camera.viewportHeight * 0.5f + 200f);
>>>>>>> f29aecc (Полный экран, масштабируемость)

        for (int i = 0; i < buttonBounds.length; i++) {
            Rectangle bounds = buttonBounds[i];
            menuFont.setColor(i == hoveredButton ? Color.WHITE : new Color(0.72f, 0.74f, 0.72f, 1f));
<<<<<<< HEAD
            drawCentered(menuFont, buttonLabels[i], bounds.x + bounds.width / 2f, bounds.y + 34f);
=======
            drawCentered(menuFont, buttonLabels[i], bounds.x + bounds.width * 0.5f, bounds.y + 34f);
>>>>>>> f29aecc (Полный экран, масштабируемость)
        }
    }

    private void drawCentered(BitmapFont font, String text, float centerX, float y) {
        glyphLayout.setText(font, text);
<<<<<<< HEAD
        font.draw(batch, text, centerX - glyphLayout.width / 2f, y);
=======
        font.draw(batch, text, centerX - glyphLayout.width * 0.5f, y);
>>>>>>> f29aecc (Полный экран, масштабируемость)
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
<<<<<<< HEAD
=======
        updateLayout();
>>>>>>> f29aecc (Полный экран, масштабируемость)
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
