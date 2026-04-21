package com.metrohorror.game.world;

public class DungeonMap extends GameMap {
    public DungeonMap() {
        getPlatforms().add(new Platform(230f, 218f, 250f, 18f));
        getPlatforms().add(new Platform(540f, 306f, 170f, 18f));
        getPlatforms().add(new Platform(780f, 382f, 230f, 18f));
        getPlatforms().add(new Platform(1090f, 474f, 170f, 18f));
        getPlatforms().add(new Platform(1340f, 356f, 260f, 18f));
        getPlatforms().add(new Platform(1670f, 462f, 190f, 18f));
        getPlatforms().add(new Platform(1930f, 568f, 250f, 18f));
        getPlatforms().add(new Platform(2250f, 654f, 190f, 18f));
        getPlatforms().add(new Platform(2520f, 560f, 250f, 18f));
    }
}
