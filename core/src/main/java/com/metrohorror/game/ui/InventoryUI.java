package com.metrohorror.game.ui;

import java.util.Map;

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

    private final GlyphLayout glyphLayout = new GlyphLayout();
    private Texture panelTexture;

    public void renderBackdrop(SpriteBatch batch, float viewportWidth, float viewportHeight) {
        ensureTexture();
        float panelX = viewportWidth * 0.5f - PANEL_WIDTH * 0.5f;
        float panelY = viewportHeight * 0.5f - PANEL_HEIGHT * 0.5f;
        batch.setColor(1f, 1f, 1f, 0.98f);
        batch.draw(panelTexture, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);
        batch.setColor(Color.WHITE);
    }

    public void render(SpriteBatch batch, BitmapFont font, InventorySystem inventory, boolean visible, float viewportWidth, float viewportHeight) {
        if (!visible) {
            return;
        }

        float panelX = viewportWidth * 0.5f - PANEL_WIDTH * 0.5f;
        float panelY = viewportHeight * 0.5f - PANEL_HEIGHT * 0.5f;
        WeaponType selectedWeapon = inventory.getSelectedWeapon();

        font.setColor(0.94f, 0.96f, 0.92f, 1f);
        drawCentered(batch, font, "\u0418\u041d\u0412\u0415\u041d\u0422\u0410\u0420\u042c", panelX + PANEL_WIDTH * 0.5f, panelY + 772f);

        font.setColor(0.70f, 0.82f, 0.76f, 1f);
        font.draw(batch, "\u041f\u0435\u0440\u0441\u043e\u043d\u0430\u0436", panelX + 54f, panelY + 756f);
        font.draw(batch, "\u0411\u044b\u0441\u0442\u0440\u044b\u0435 \u0441\u043b\u043e\u0442\u044b", panelX + 240f, panelY + 756f);
        font.draw(batch, "\u041e\u0440\u0443\u0436\u0438\u0435", panelX + 906f, panelY + 756f);
        font.draw(batch, "\u0420\u044e\u043a\u0437\u0430\u043a", panelX + 56f, panelY + 410f);
        font.draw(batch, "\u041e\u043f\u0438\u0441\u0430\u043d\u0438\u0435", panelX + 540f, panelY + 214f);

        font.setColor(0.82f, 0.88f, 0.84f, 1f);
        font.draw(batch, "\u041b\u041a\u041c - \u043f\u0435\u0440\u0435\u0442\u0430\u0449\u0438\u0442\u044c | 1-4 - \u0432\u044b\u0431\u043e\u0440 \u043e\u0440\u0443\u0436\u0438\u044f", panelX + 66f, panelY + 40f);

        font.setColor(0.92f, 0.95f, 0.90f, 1f);
        if (selectedWeapon != null) {
            font.draw(batch, selectedWeapon.getDisplayName(), panelX + 560f, panelY + 168f);
            font.setColor(0.72f, 0.82f, 0.78f, 1f);
            font.draw(batch, "\u0423\u0440\u043e\u043d: " + selectedWeapon.getDamage(), panelX + 560f, panelY + 134f);
            font.draw(batch, "\u0414\u0430\u043b\u044c\u043d\u043e\u0441\u0442\u044c: " + Math.round(selectedWeapon.getRange()), panelX + 560f, panelY + 104f);
            font.draw(batch, "\u0422\u0438\u043f: \u043e\u0440\u0443\u0436\u0438\u0435 \u0431\u043b\u0438\u0436\u043d\u0435\u0433\u043e \u0431\u043e\u044f", panelX + 560f, panelY + 74f);
        } else {
            font.draw(batch, "\u041e\u0440\u0443\u0436\u0438\u0435 \u043d\u0435 \u0432\u044b\u0431\u0440\u0430\u043d\u043e", panelX + 560f, panelY + 168f);
            font.setColor(0.72f, 0.82f, 0.78f, 1f);
            font.draw(batch, "\u041f\u0435\u0440\u0435\u0442\u0430\u0449\u0438 \u043d\u043e\u0436 \u0438\u043b\u0438 \u0434\u0440\u0443\u0433\u043e\u0435 \u043e\u0440\u0443\u0436\u0438\u0435 \u0432 \u0431\u044b\u0441\u0442\u0440\u044b\u0439 \u0441\u043b\u043e\u0442.", panelX + 560f, panelY + 134f);
        }

        float itemY = panelY + 366f;
        font.setColor(0.84f, 0.90f, 0.86f, 1f);
        if (inventory.getItems().isEmpty()) {
            font.draw(batch, "\u041f\u043e\u043a\u0430 \u043f\u0443\u0441\u0442\u043e", panelX + 560f, itemY);
        } else {
            for (Map.Entry<String, Integer> entry : inventory.getItems().entrySet()) {
                font.draw(batch, entry.getKey() + " x" + entry.getValue(), panelX + 560f, itemY);
                itemY -= 24f;
            }
        }
    }

    public void dispose() {
        if (panelTexture != null) {
            panelTexture.dispose();
            panelTexture = null;
        }
    }

    private void ensureTexture() {
        if (panelTexture == null) {
            panelTexture = new Texture(Gdx.files.internal("ui/inventory-layout.png"));
        }
    }

    private void drawCentered(SpriteBatch batch, BitmapFont font, String text, float centerX, float y) {
        glyphLayout.setText(font, text);
        font.draw(batch, text, centerX - glyphLayout.width * 0.5f, y);
    }
}
