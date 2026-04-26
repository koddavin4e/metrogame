package com.metrohorror.game.systems;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import com.metrohorror.game.entities.WeaponType;

public class InventorySystem {
    public static final int WEAPON_SLOT_COUNT = 4;
    public static final int BAG_SLOT_COUNT = 24;

    private final Map<String, Integer> items = new LinkedHashMap<>();
    private final WeaponType[] weaponSlots = new WeaponType[WEAPON_SLOT_COUNT];
    private final WeaponType[] bagSlots = new WeaponType[BAG_SLOT_COUNT];
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
        if (!items.isEmpty()) {
            return false;
        }
        for (WeaponType weapon : weaponSlots) {
            if (weapon != null) {
                return false;
            }
        }
        for (WeaponType weapon : bagSlots) {
            if (weapon != null) {
                return false;
            }
        }
        return true;
    }

    public WeaponType[] getWeaponSlots() {
        return Arrays.copyOf(weaponSlots, weaponSlots.length);
    }

    public WeaponType[] getBagSlots() {
        return Arrays.copyOf(bagSlots, bagSlots.length);
    }

    public WeaponType getWeaponInSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= weaponSlots.length) {
            return null;
        }
        return weaponSlots[slotIndex];
    }

    public WeaponType getBagWeaponInSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= bagSlots.length) {
            return null;
        }
        return bagSlots[slotIndex];
    }

    public void equipWeapon(int slotIndex, WeaponType weaponType) {
        if (slotIndex < 0 || slotIndex >= weaponSlots.length) {
            return;
        }
        weaponSlots[slotIndex] = weaponType;
    }

    public void putBagWeapon(int slotIndex, WeaponType weaponType) {
        if (slotIndex < 0 || slotIndex >= bagSlots.length) {
            return;
        }
        bagSlots[slotIndex] = weaponType;
    }

    public WeaponType takeWeaponFromHand(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= weaponSlots.length) {
            return null;
        }
        WeaponType weapon = weaponSlots[slotIndex];
        weaponSlots[slotIndex] = null;
        return weapon;
    }

    public WeaponType takeWeaponFromBag(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= bagSlots.length) {
            return null;
        }
        WeaponType weapon = bagSlots[slotIndex];
        bagSlots[slotIndex] = null;
        return weapon;
    }

    public void swapHandSlots(int firstSlot, int secondSlot) {
        if (!isValidHandSlot(firstSlot) || !isValidHandSlot(secondSlot)) {
            return;
        }
        WeaponType weapon = weaponSlots[firstSlot];
        weaponSlots[firstSlot] = weaponSlots[secondSlot];
        weaponSlots[secondSlot] = weapon;
    }

    public void swapBagSlots(int firstSlot, int secondSlot) {
        if (!isValidBagSlot(firstSlot) || !isValidBagSlot(secondSlot)) {
            return;
        }
        WeaponType weapon = bagSlots[firstSlot];
        bagSlots[firstSlot] = bagSlots[secondSlot];
        bagSlots[secondSlot] = weapon;
    }

    public void moveHandToBag(int handSlot, int bagSlot) {
        if (!isValidHandSlot(handSlot) || !isValidBagSlot(bagSlot)) {
            return;
        }
        WeaponType weapon = weaponSlots[handSlot];
        weaponSlots[handSlot] = bagSlots[bagSlot];
        bagSlots[bagSlot] = weapon;
    }

    public void moveBagToHand(int bagSlot, int handSlot) {
        if (!isValidBagSlot(bagSlot) || !isValidHandSlot(handSlot)) {
            return;
        }
        WeaponType weapon = bagSlots[bagSlot];
        bagSlots[bagSlot] = weaponSlots[handSlot];
        weaponSlots[handSlot] = weapon;
    }

    public boolean addWeaponToBag(WeaponType weaponType) {
        for (int i = 0; i < bagSlots.length; i++) {
            if (bagSlots[i] == null) {
                bagSlots[i] = weaponType;
                return true;
            }
        }
        return false;
    }

    public void selectWeaponSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= weaponSlots.length) {
            return;
        }
        selectedWeaponSlot = slotIndex;
    }

    public void selectNextWeaponSlot() {
        selectedWeaponSlot = (selectedWeaponSlot + 1) % weaponSlots.length;
    }

    public void selectPreviousWeaponSlot() {
        selectedWeaponSlot = (selectedWeaponSlot + weaponSlots.length - 1) % weaponSlots.length;
    }

    public int getSelectedWeaponSlot() {
        return selectedWeaponSlot;
    }

    public WeaponType getSelectedWeapon() {
        return weaponSlots[selectedWeaponSlot];
    }

    private boolean isValidHandSlot(int slotIndex) {
        return slotIndex >= 0 && slotIndex < weaponSlots.length;
    }

    private boolean isValidBagSlot(int slotIndex) {
        return slotIndex >= 0 && slotIndex < bagSlots.length;
    }
}
