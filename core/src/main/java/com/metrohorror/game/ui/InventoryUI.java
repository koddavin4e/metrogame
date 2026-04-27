package com.metrohorror.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.metrohorror.game.entities.WeaponType;
import com.metrohorror.game.systems.InventorySystem;

public class InventoryUI {
    private static final float PANEL_WIDTH = 1300f;
    private static final float PANEL_HEIGHT = 800f;
    private static final float DESCRIPTION_WIDTH = 620f;
    private static final int DESCRIPTION_MAX_LINES = 5;

    private Texture layoutTexture;
    private final GlyphLayout glyphLayout = new GlyphLayout();

    public void renderBackdrop(SpriteBatch batch, float viewportWidth, float viewportHeight) {
        ensureLayoutTexture();

        batch.setColor(Color.WHITE);
        batch.draw(layoutTexture, getPanelX(viewportWidth), getPanelY(viewportHeight), PANEL_WIDTH, PANEL_HEIGHT);
    }

    public void render(SpriteBatch batch, BitmapFont font, InventorySystem inventory, boolean visible, float viewportWidth, float viewportHeight) {
        if (!visible) {
            return;
        }

        float panelX = getPanelX(viewportWidth);
        float panelY = getPanelY(viewportHeight);
        WeaponType selectedWeapon = inventory.getSelectedWeapon();

        float descriptionX = panelX + 590f;
        float descriptionY = panelY + 195f;
        float lineHeight = 30f;
        float descriptionWidth = DESCRIPTION_WIDTH;

        font.setColor(0.86f, 0.90f, 0.86f, 1f);
        if (selectedWeapon != null) {
            drawWrappedText(batch, font, selectedWeapon.getDisplayName(), descriptionX, descriptionY, descriptionWidth, lineHeight, 1);
            font.setColor(0.66f, 0.74f, 0.72f, 1f);
            drawWrappedText(batch, font, "\u0423\u0440\u043e\u043d: " + selectedWeapon.getDamage(), descriptionX, descriptionY - lineHeight, descriptionWidth, lineHeight, 1);
            drawWrappedText(batch, font, "\u0414\u0430\u043b\u044c\u043d\u043e\u0441\u0442\u044c: " + Math.round(selectedWeapon.getRange()), descriptionX, descriptionY - lineHeight * 2f, descriptionWidth, lineHeight, 1);
            drawWrappedText(batch, font, "\u0410\u043a\u0442\u0438\u0432\u043d\u044b\u0439 \u0441\u043b\u043e\u0442: " + (inventory.getSelectedWeaponSlot() + 1), descriptionX, descriptionY - lineHeight * 3f, descriptionWidth, lineHeight, 1);
            drawWrappedText(batch, font, "\u041b\u041a\u041c - \u043f\u0435\u0440\u0435\u0442\u0430\u0449\u0438\u0442\u044c, 1-2 - \u0432\u044b\u0431\u0440\u0430\u0442\u044c", descriptionX, descriptionY - lineHeight * 4f, descriptionWidth, lineHeight, 2);
        } else {
            drawWrappedText(batch, font, "\u041e\u0440\u0443\u0436\u0438\u0435 \u043d\u0435 \u0432\u044b\u0431\u0440\u0430\u043d\u043e", descriptionX, descriptionY, descriptionWidth, lineHeight, 1);
            font.setColor(0.66f, 0.74f, 0.72f, 1f);
            drawWrappedText(batch, font,
                    "\u041f\u0435\u0440\u0435\u0442\u0430\u0449\u0438\u0442\u0435 \u043d\u043e\u0436 \u0438\u043b\u0438 \u0434\u0440\u0443\u0433\u043e\u0435 \u043e\u0440\u0443\u0436\u0438\u0435 \u0432 \u0432\u0435\u0440\u0445\u043d\u0438\u0435 \u0441\u043b\u043e\u0442\u044b.",
                    descriptionX,
                    descriptionY - lineHeight,
                    descriptionWidth,
                    lineHeight,
                    DESCRIPTION_MAX_LINES);
        }
    }

    public void dispose() {
        if (layoutTexture != null) {
            layoutTexture.dispose();
            layoutTexture = null;
        }
    }

    private void ensureLayoutTexture() {
        if (layoutTexture == null) {
            layoutTexture = new Texture(Gdx.files.internal("ui/inventory-layout.png"));
        }
    }

    private float getPanelX(float viewportWidth) {
        return viewportWidth * 0.5f - PANEL_WIDTH * 0.5f;
    }

    private float getPanelY(float viewportHeight) {
        return viewportHeight * 0.5f - PANEL_HEIGHT * 0.5f;
    }

    private void drawWrappedText(SpriteBatch batch, BitmapFont font, String text, float x, float y, float maxWidth, float lineHeight, int maxLines) {
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

        if (line < maxLines && lineText.length() > 0) {
            font.draw(batch, lineText.toString(), x, y - line * lineHeight);
        }
    }
}
