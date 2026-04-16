package com.metrohorror.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.metrohorror.game.util.Constants;

public class GameMap {
    private final Rectangle ground;
    private final Array<Platform> platforms;

    public GameMap() {
        ground = new Rectangle(0, Constants.GROUND_Y, Constants.WORLD_WIDTH, Constants.GROUND_HEIGHT);
        platforms = new Array<>();

        platforms.add(new Platform(350, 220, 180, 20));
        platforms.add(new Platform(700, 300, 180, 20));
        platforms.add(new Platform(1050, 250, 200, 20));
        platforms.add(new Platform(1450, 360, 220, 20));
        platforms.add(new Platform(1850, 280, 200, 20));
    }

    public Rectangle getGround() {
        return ground;
    }

    public Array<Platform> getPlatforms() {
        return platforms;
    }
}