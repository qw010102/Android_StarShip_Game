package com.taewon.mygallag.items;


import android.content.Context;

import com.taewon.mygallag.R;
import com.taewon.mygallag.SpaceInvadersView;
import com.taewon.mygallag.sprites.Sprite;

import java.util.Timer;
import java.util.TimerTask;

public class SpeedItemSprite extends Sprite {
    SpaceInvadersView game;

    public SpeedItemSprite(Context context, SpaceInvadersView game, float x, float y, int dx, int dy) {
        super(context, R.drawable.speed_item, x, y);
        this.game = game;
        this.dx = dx;
        this.dy = dy;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                autoRemove();
            }
        }, 15000);
    }

    private void autoRemove() {
        game.removeSprite(this);
    }

    public void move() {
        // 반대로 가야하니 -로 변경
        if ((dx < 0) && (x < 120)) {
            dx *= -1;
            return;
        }

        if ((dx > 0) && (x > game.screenW - 120)) {
            dx *= -1;
            return;
        }

        if ((dy < 0) && (y < 120)) {
            dy *= -1;
            return;
        }

        if ((dy > 0) && (y > game.screenH - 120)) {
            dy *= -1;
            return;
        }

        super.move();
    }
}
