package com.metrohorror.game.ui;

import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.metrohorror.game.entities.WeaponType;
import com.metrohorror.game.systems.InventorySystem;

public class InventoryUI {
    public void render(SpriteBatch batch, BitmapFont font, InventorySystem inventory, boolean visible) {
        if (!visible) return;

        font.setColor(Color.WHITE);
        font.draw(batch, "=== INVENTORY ===", 40, 680);
        font.draw(batch, "Weapon slots (press 1-4):", 40, 650);

        WeaponType[] slots = inventory.getWeaponSlots();
        for (int i = 0; i < slots.length; i++) {
            WeaponType weapon = slots[i];
            boolean active = i == inventory.getSelectedWeaponSlot();
            String marker = active ? ">" : " ";
            String weaponName = weapon == null ? "Empty" : weapon.getDisplayName();
            font.draw(batch, marker + " [" + (i + 1) + "] " + weaponName, 40, 620 - i * 24);
        }

        if (inventory.isEmpty()) {
            font.draw(batch, "Loot bag: Empty", 40, 500);
            return;
        }

        int line = 0;
        font.draw(batch, "Loot bag:", 40, 500);
        for (Map.Entry<String, Integer> entry : inventory.getItems().entrySet()) {
            font.draw(batch, entry.getKey() + " x" + entry.getValue(), 40, 470 - line * 25);
            line++;
        }
    }
}
