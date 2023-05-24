package com.taewon.mygallag.sprites;


import android.content.Context;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.taewon.mygallag.MainActivity;
import com.taewon.mygallag.R;
import com.taewon.mygallag.SpaceInvadersView;
import com.taewon.mygallag.items.HealitemSprite;
import com.taewon.mygallag.items.PowerItemSprite;
import com.taewon.mygallag.items.SpeedItemSprite;

import java.util.ArrayList;

public class StarshipSprite extends Sprite{
    Context context;
    SpaceInvadersView game;
    public float speed;
    private int bullets, life = 3, powerLevel;
    private int specialShotCount;
    private boolean isSpecialShooting;
    private static ArrayList<Integer> bulletSprites = new ArrayList<>();
    private final static float MAX_SPEED = 3.5f;
    private final static int MAX_HEART = 3;
    private boolean isReloading = false;
//    private RectF rectF;

    public StarshipSprite(Context context, SpaceInvadersView game, int resID, int x, int y, float speed) {
        super(context, resID, x, y);
        this.context = context;
        this.game = game;
        this.speed = speed;
        init();
    }

    public void init() {
        dx = dy = 0;
        bullets = 30;
        life = 3;
        specialShotCount = 3;
        powerLevel = 0;
        Integer[] shots = {R.drawable.shot_001, R.drawable.shot_002, R.drawable.shot_003, R.drawable.shot_004,
                R.drawable.shot_005, R.drawable.shot_006, R.drawable.shot_007};

        for (int i = 0; i < shots.length; i++) {
            bulletSprites.add(shots[i]);
        }
    }

    @Override
    public void move() {
        if ((dx < 0) && (x < 120)) return;
        if ((dx > 0) && (x > game.screenW - 120)) return;
        if ((dy < 0) && (y < 120)) return;
        if ((dy > 0) && (y > game.screenH - 120)) return;
        super.move(); // super class 가서 x, y 위치 다시 지정
    }

    // 총알수
    public int getBulletsCount() {
        return bullets;
    }

    // 이동
    public void moveRight(double force) {
        setDx((float) (1 * force * speed));
    }

    public void moveLeft(double force) {
        setDx((float) (-1 * force * speed));
    }

    public void moveDown(double force) {
        setDy((float) (1 * force * speed));
    }

    public void moveUp(double force) {
        setDy((float) (-1 * force * speed));
    }

    public void resetDx() {
        setDx(0);
    }

    public void resetDy() {
        setDy(0);
    }

    public void plusSpeed(float speed) {
        this.speed += speed;
    }

    public void fire() {
        if (isReloading | isSpecialShooting) {
            return;
        }

        MainActivity.effectSound(MainActivity.PLAYER_SHOT);
        // 생성자 구현
        ShotSprite shot = new ShotSprite(context, game, bulletSprites.get(powerLevel), getX() + 10, getY() - 30, -16);

        //SpaceInvadersView getSprites() 구현
        game.getSprites().add(shot);
        bullets--;

        MainActivity.bulletCount.setText(bullets + "/30");
//        Log.d("bullets", bullets + "/30");

        if (bullets == 0) {
            reloadBullets();
            return;
        }
    }


    public void reloadBullets() {
        isReloading = true;
        MainActivity.effectSound(MainActivity.PLAYER_RELOAD);
        MainActivity.btnFire.setEnabled(false);
        MainActivity.btnReload.setEnabled(false);

        // Thread sleep 사용하지 않고 delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bullets = 30;
                MainActivity.btnFire.setEnabled(true);
                MainActivity.btnReload.setEnabled(true);
                MainActivity.bulletCount.setText(bullets + "/30");
                MainActivity.bulletCount.invalidate(); // 화면 새로 고침
                isReloading = false;
            }
        }, 2000);
    }

    public void specialShot() {
        specialShotCount--;

        //specialShotSprite 구현
        SpecialshotSprite shot = new SpecialshotSprite(context, game, R.drawable.laser, getRect().right = getRect().left, 0);

        // game -> spaceInvadersView 의 getSprites(): sprite 에 shot 추가
        game.getSprites().add(shot);
    }

    public int getSpecialShotCount() {
        return specialShotCount;
    }

    public boolean isSpecialShooting() {
        return isSpecialShooting;
    }

    public void setSpecialShooting(boolean specialShooting) {
        isSpecialShooting = specialShooting;
    }

    public  int getLife() {
        return life;
    }

    public void hurt() {
        life--;
        if (life <= 0) {
            ((ImageView) MainActivity.lifeFrame.getChildAt(life)).setImageResource((R.drawable.ic_baseline_favorite_border_24));

            // spaceInvadersView 의 endGame() 에서 game 종료
            game.endGame();
            return;
        }

        Log.d("hurt", Integer.toString(life));
        ((ImageView) MainActivity.lifeFrame.getChildAt(life)).setImageResource(R.drawable.ic_baseline_favorite_border_24);
    }

    public void powerUp() {
        if (powerLevel >= bulletSprites.size() - 1) {
            game.setScore(game.getScore() + 1);
            MainActivity.tvScore.setText(Integer.toString(game.getScore()));
            return;
        }

        powerLevel++;
        MainActivity.btnFire.setImageResource(bulletSprites.get(powerLevel));
        MainActivity.btnFire.setBackgroundResource(R.drawable.round_button_shape);
    }

    // 생명 얻었을 때
    public void heal() {
        Log.d("heal", Integer.toString(life));
        if (life + 1 > MAX_HEART) {
            game.setScore(game.getScore() + 1);
            MainActivity.tvScore.setText(Integer.toString(game.getScore()));
            return;
        }

        ((ImageView) MainActivity.lifeFrame.getChildAt(life)).setImageResource(R.drawable.ic_baseline_favorite_border_24);
        life++;
    }

    // 속도 올리기
    private void speedUp() {
        if (MAX_SPEED >= speed + 0.2f) {
            plusSpeed(0.2f);
        } else {
            game.setScore(game.getScore() + 1);
            MainActivity.tvScore.setText(Integer.toString(game.getScore()));
        }
    }

    // Sprite 의 handleCollision() -> 충돌 처리
    @Override
    public void handleCollision(Sprite other) {
        if (other instanceof AlienSprite) {
            // Alien 아이템이면
            game.removeSprite(other);
            MainActivity.effectSound(MainActivity.PLAYER_HURT);
            hurt();
        }

        if (other instanceof SpeedItemSprite) {
            // speed 아이템이면
            game.removeSprite(other);
            MainActivity.effectSound(MainActivity.PLAYER_GET_ITEM);
            speedUp();
        }

        if (other instanceof AlienShotSprite) {
            // 총알을 맞으면
            MainActivity.effectSound(MainActivity.PLAYER_HURT);
            game.removeSprite(other);
            hurt();
        }

        if (other instanceof PowerItemSprite) {
            // 파워업 아이템을 먹으면
            MainActivity.effectSound(MainActivity.PLAYER_GET_ITEM);
            powerUp();
            game.removeSprite(other);
        }

        if (other instanceof HealitemSprite) {
            // 생명 아이템을 먹으면
            MainActivity.effectSound(MainActivity.PLAYER_GET_ITEM);
            game.removeSprite(other);
            heal();
        }
    }

    public int getPowerLevel() {
        return powerLevel;
    }
}
