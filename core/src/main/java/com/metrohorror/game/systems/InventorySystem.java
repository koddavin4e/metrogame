package com.metrohorror.game.systems;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import com.metrohorror.game.entities.WeaponType;

public class InventorySystem {
    public static final int WEAPON_SLOT_COUNT = 4;

    private final Map<String, Integer> items = new LinkedHashMap<>();
    private final WeaponType[] weaponSlots = new WeaponType[WEAPON_SLOT_COUNT];
    private int selectedWeaponSlot;

    public InventorySystem() {
        // Start as a clean prototype: no weapons, no loot, just an empty bag.
    }

    public void addItem(String name, int amount) {
        items.put(name, items.getOrDefault(name, 0) + amount);
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public WeaponType[] getWeaponSlots() {
        return Arrays.copyOf(weaponSlots, weaponSlots.length);
    }

    public WeaponType getWeaponInSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= weaponSlots.length) {
            return null;
        }
        return weaponSlots[slotIndex];
    }

    public void equipWeapon(int slotIndex, WeaponType weaponType) {
        if (slotIndex < 0 || slotIndex >= weaponSlots.length) {
            return;
        }
        weaponSlots[slotIndex] = weaponType;
    }

    public void selectWeaponSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= weaponSlots.length) {
            return;
        }
        selectedWeaponSlot = slotIndex;
    }

    public int getSelectedWeaponSlot() {
        return selectedWeaponSlot;
    }

    public WeaponType getSelectedWeapon() {
        return weaponSlots[selectedWeaponSlot];
    }
}
