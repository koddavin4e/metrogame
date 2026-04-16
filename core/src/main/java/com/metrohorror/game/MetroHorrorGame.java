package com.metrohorror.game;

import com.badlogic.gdx.Game;
import com.metrohorror.game.screen.FirstScreen;

public class MetroHorrorGame extends Game {
    @Override
    public void create() {
        setScreen(new FirstScreen());
    }
}   