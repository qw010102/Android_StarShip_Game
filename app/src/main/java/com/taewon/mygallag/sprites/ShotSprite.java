package com.taewon.mygallag.sprites;


import android.content.Context;

import com.taewon.mygallag.SpaceInvadersView;

public class ShotSprite extends Sprite{
    private SpaceInvadersView game;

    public ShotSprite(Context context, SpaceInvadersView game, int resourceId, float x, float y, int dy) {
        super(context, resourceId, x, y);
        this.game = game;
        setDy(dy);
    }
}
