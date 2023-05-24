package com.taewon.mygallag.sprites;


import android.content.Context;

import com.taewon.mygallag.SpaceInvadersView;

import java.util.Timer;
import java.util.TimerTask;

public class SpecialshotSprite extends Sprite{
    private SpaceInvadersView game;

    public SpecialshotSprite(Context context, SpaceInvadersView game,  int resourceId, float x, float y) {
        super(context, resourceId, x, y);
        this.game = game;
        game.getPlayer().setSpecialShooting(true);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                autoRemove();
            }
        }, 5000);
    }

    @Override
    public void move() {
        super.move();
        this.x = game.getPlayer().getX() - getWidth() + 240;
        this.y = game.getPlayer().getY() - getHeight();
    }

    public void autoRemove() {
        game.getPlayer().setSpecialShooting(false);
        game.removeSprite(this);
    }
}
