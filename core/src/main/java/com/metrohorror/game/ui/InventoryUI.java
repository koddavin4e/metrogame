package com.metrohorror.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.metrohorror.game.systems.InventorySystem;

public class InventoryUI {
    public void render(SpriteBatch batch, BitmapFont font, InventorySystem inventory, boolean visible, float viewportWidth, float viewportHeight) {
        if (!visible) return;

        float panelX = viewportWidth / 2f - 320f;
        float panelY = viewportHeight / 2f - 215f;

        font.setColor(Color.WHITE);
        font.draw(batch, "ИНВЕНТАРЬ", panelX + 248f, panelY + 386f);
        font.draw(batch, "Пусто", panelX + 96f, panelY + 68f);
        font.draw(batch, "Оружия и вещей пока нет.", panelX + 350f, panelY + 68f);

        font.setColor(0.70f, 0.80f, 0.74f, 1f);
        font.draw(batch, "Персонаж", panelX + 78f, panelY + 328f);
        font.draw(batch, "Сумка", panelX + 365f, panelY + 328f);
    }
}
