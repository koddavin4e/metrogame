package com.metrohorror.game.systems;

import java.util.Iterator;

import com.badlogic.gdx.utils.Array;
import com.metrohorror.game.entities.Enemy;
import com.metrohorror.game.entities.Loot;
import com.metrohorror.game.entities.Player;

public class LootSystem {
    private final Array<Loot> drops = new Array<>();

    public void spawnLootFromEnemy(Enemy enemy) {
        drops.add(new Loot("Dark Crystal", 1, enemy.getX() + 10, enemy.getY() + 10));
        drops.add(new Loot("Bone Fragment", 2, enemy.getX() + 30, enemy.getY() + 10));
    }

    public void collectLoot(Player player, InventorySystem inventory) {
        Iterator<Loot> iterator = drops.iterator();
        while (iterator.hasNext()) {
            Loot loot = iterator.next();
            if (!loot.isCollected() && loot.getBounds().overlaps(player.getBounds())) {
                inventory.addItem(loot.getName(), loot.getAmount());
                loot.collect();
                iterator.remove();
            }
        }
    }

    public Array<Loot> getDrops() {
        return drops;
    }
}