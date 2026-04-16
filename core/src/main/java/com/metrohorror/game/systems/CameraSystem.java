package com.metrohorror.game.systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.metrohorror.game.entities.Player;
import com.metrohorror.game.util.Constants;

public class CameraSystem {
    public void follow(OrthographicCamera camera, Player player, float delta) {
        float targetX = player.getX() + 20f;
        float targetY = player.getY() + 120f;

        camera.position.x += (targetX - camera.position.x) * Constants.CAMERA_LERP * delta;
        camera.position.y += (targetY - camera.position.y) * Constants.CAMERA_LERP * delta;

        float halfWidth = camera.viewportWidth / 2f;
        float halfHeight = camera.viewportHeight / 2f;

        camera.position.x = MathUtils.clamp(camera.position.x, halfWidth, Constants.WORLD_WIDTH - halfWidth);
        camera.position.y = MathUtils.clamp(camera.position.y, halfHeight, Constants.WORLD_HEIGHT - halfHeight);

        camera.update();
    }
}