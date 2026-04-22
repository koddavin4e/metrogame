package com.metrohorror.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.metrohorror.game.entities.WeaponType;
import com.metrohorror.game.systems.InventorySystem;

public class InventoryUI {
    private static final float PANEL_WIDTH = 700f;
    private static final float PANEL_HEIGHT = 456f;
    private static final float SLOT_SIZE = 50f;
    private static final float SLOT_GAP = 10f;
    private static final int BAG_COLUMNS = 4;

    private final GlyphLayout glyphLayout = new GlyphLayout();

    public void render(SpriteBatch batch, BitmapFont font, InventorySystem inventory, boolean visible, float viewportWidth, float viewportHeight) {
        if (!visible) return;

        float panelX = viewportWidth / 2f - PANEL_WIDTH / 2f;
        float panelY = viewportHeight / 2f - PANEL_HEIGHT / 2f;

        font.setColor(Color.WHITE);
        font.draw(batch, "ИНВЕНТАРЬ", panelX + 280f, panelY + 426f);

        font.setColor(0.70f, 0.80f, 0.74f, 1f);
        font.draw(batch, "Персонаж", panelX + 88f, panelY + 390f);
        font.draw(batch, "Сумка", panelX + 416f, panelY + 390f);
        font.draw(batch, "Руки: только 4 предмета. Выбор клавишами 1-4.", panelX + 48f, panelY + 106f);

        WeaponType[] weapons = inventory.getWeaponSlots();
        boolean hasWeapon = false;
        font.setColor(Color.WHITE);
        for (int i = 0; i < weapons.length; i++) {
            WeaponType weapon = weapons[i];
            float slotX = panelX + 134f + i * (SLOT_SIZE + SLOT_GAP);
            float slotY = panelY + 34f;
            font.setColor(i == inventory.getSelectedWeaponSlot() ? 0.98f : 0.78f, 0.90f, 0.76f, 1f);
            font.draw(batch, String.valueOf(i + 1), slotX + 5f, slotY + 46f);
            if (weapon != null) {
                hasWeapon = true;
                drawShortText(batch, font, weapon.getDisplayName(), slotX + 5f, slotY - 6f, 54f);
            }
        }

        WeaponType[] bagWeapons = inventory.getBagSlots();
        for (int i = 0; i < bagWeapons.length; i++) {
            WeaponType weapon = bagWeapons[i];
            if (weapon == null) {
                continue;
            }

            int col = i % BAG_COLUMNS;
            int row = i / BAG_COLUMNS;
            float slotX = panelX + 416f + col * (SLOT_SIZE + SLOT_GAP);
            float slotY = panelY + 256f - row * (SLOT_SIZE + SLOT_GAP);
            font.setColor(0.90f, 0.94f, 0.88f, 1f);
            drawShortText(batch, font, weapon.getDisplayName(), slotX + 4f, slotY - 6f, 58f);
        }

        float itemY = panelY + 330f;
        font.setColor(0.86f, 0.91f, 0.84f, 1f);
        if (inventory.getItems().isEmpty() && !hasWeapon) {
            font.draw(batch, "Пока пусто", panelX + 426f, itemY);
        } else {
            for (String item : inventory.getItems().keySet()) {
                font.draw(batch, item + " x" + inventory.getItems().get(item), panelX + 426f, itemY);
                itemY -= 24f;
            }
        }
    }

    private void drawShortText(SpriteBatch batch, BitmapFont font, String text, float x, float y, float maxWidth) {
        String result = text;
        glyphLayout.setText(font, result);
        while (result.length() > 3 && glyphLayout.width > maxWidth) {
            result = result.substring(0, result.length() - 2) + ".";
            glyphLayout.setText(font, result);
        }
        font.draw(batch, result, x, y);
    }
}
