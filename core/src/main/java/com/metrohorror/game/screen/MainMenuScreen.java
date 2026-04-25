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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.metrohorror.game.MetroHorrorGame;

public class MainMenuScreen implements Screen {
    private final MetroHorrorGame game;
    private final Rectangle[] buttonBounds = new Rectangle[5];
    private final String[] buttonLabels = {
            "\u041d\u043e\u0432\u0430\u044f \u0438\u0433\u0440\u0430",
            "\u041f\u0440\u043e\u0434\u043e\u043b\u0436\u0438\u0442\u044c",
            "\u0414\u043e\u0441\u0442\u0438\u0436\u0435\u043d\u0438\u044f",
            "\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438",
            "\u0412\u044b\u0445\u043e\u0434"
    };
    private final Vector3 pointer = new Vector3();

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont menuFont;
    private GlyphLayout glyphLayout;
    private int hoveredButton = -1;
    private boolean startNewGameRequested;

    public MainMenuScreen(MetroHorrorGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.apply(true);

        batch = new SpriteBatch();
        titleFont = createFont(46);
        menuFont = createFont(24);
        glyphLayout = new GlyphLayout();
        updateLayout();
    }

    private void updateLayout() {
        float buttonWidth = 360f;
        float buttonHeight = 48f;
        float buttonGap = 18f;
        float buttonX = camera.viewportWidth * 0.5f - buttonWidth * 0.5f;
        float buttonY = camera.viewportHeight * 0.5f + 30f;
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
            } else if (hoveredButton == 4) {
                Gdx.app.exit();
            }
        }
    }

    private void renderText() {
        titleFont.setColor(0.96f, 0.90f, 0.82f, 1f);
        drawCentered(titleFont, "Paradise Disappear", camera.viewportWidth * 0.5f, camera.viewportHeight * 0.5f + 200f);

        for (int i = 0; i < buttonBounds.length; i++) {
            Rectangle bounds = buttonBounds[i];
            menuFont.setColor(i == hoveredButton ? Color.WHITE : new Color(0.72f, 0.74f, 0.72f, 1f));
            drawCentered(menuFont, buttonLabels[i], bounds.x + bounds.width * 0.5f, bounds.y + 34f);
        }
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
                + "\u00ab\u00bb\u2116\u2026.,!?-:()";
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
