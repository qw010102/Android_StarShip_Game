package com.taewon.mygallag;

import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {
    private Intent userIntent;
    ArrayList<Integer> bgMusicList;
    public static SoundPool effectSound;
    public static float effectVolume;

    ImageButton btnSpacialShot; // laser
    public static ImageButton btnFire, btnReload; // 공격, 재장전
    JoystickView joyStick;
    public static TextView tvScore;
    LinearLayout gameFrame; // spaceInvadersView 를 담는 container 역할
    ImageView btnPause;
    public static LinearLayout lifeFrame;
    SpaceInvadersView spaceInvadersView; // 게임 화면의 graphic & logic 담당
    public static MediaPlayer bgMusic;
    int bgMusicIndex;
    public static TextView bulletCount;
    private static ArrayList<Integer> effectSoundList;

    public static final int PLAYER_SHOT = 0;
    public static final int PLAYER_HURT = 1;
    public static final int PLAYER_RELOAD = 2;
    public static final int PLAYER_GET_ITEM = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userIntent = getIntent(); // intent 객체 가져오기 (from StartActivity)
        bgMusicIndex = 0; // 배경 음악 인덱스 = 0
        bgMusicList = new ArrayList<Integer>(); // 배경 음악 ID 를 저장할 ArrayList 제작
        bgMusicList.add(R.raw.main_game_bgm1); // 배경 음악 resource ID 들을 리스트에 추가
        bgMusicList.add(R.raw.main_game_bgm2);
        bgMusicList.add(R.raw.main_game_bgm3);

        effectSound = new SoundPool(5, AudioManager.USE_DEFAULT_STREAM_TYPE, 0); // (최대 5개 동시 재생, 기본 오디오 스트림 타입, 우선 순위 기본값 0)
        effectVolume = 1; // 효과음 불륨 = 1 (0~1)

        btnSpacialShot = findViewById(R.id.btnSpecialShot);
        joyStick = findViewById(R.id.joyStick);
        tvScore = findViewById(R.id.tvScore);
        btnFire = findViewById(R.id.btnFire);
        btnReload = findViewById(R.id.btnReload);
        gameFrame = findViewById(R.id.gameFrame);
        btnPause = findViewById(R.id.btnPause);
        lifeFrame = findViewById(R.id.lifeFrame);

        init();
        setBtnBehavior(); // 버튼 동작을 설정하는 메소드

    }

    @Override
    protected void onResume() { // Activity 가 화면에 표시될 때
        super.onResume();
        bgMusic.start(); // 배경 음악 재생
        spaceInvadersView.resume(); // 게임(SpaceInvadersView) 재개
    }

    @Override
    protected void onPause() { // Activity 가 백그라운드로 이동되거나 화면이 꺼질 때
        super.onPause();
        bgMusic.pause(); // 배경 음악 중지
        spaceInvadersView.pause(); // 게임(SpaceInvadersView) 중지
    }

    private void init() {
        // view 의 display 얻기
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point(); // display 의 사이즈를 담기 위한 변수
        display.getSize(size); // display 의 사이즈를 가져와 size 에 담음

        spaceInvadersView = new SpaceInvadersView(this, userIntent.getIntExtra("character", R.drawable.ship_0000), size.x, size.y);
        // SpaceInvadersView(Context, 내 캐릭터 이미지 Id, View 의 X 크기, View 의 Y 크기)
        gameFrame.addView(spaceInvadersView); // LinearLayout 에 View 를 추가

        changeBgMusic(); // 배경 음악 변경 메소드 호출
        bgMusic.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // 배경 음악이 끝나면
            @Override
            public void onCompletion(MediaPlayer mp) {
                changeBgMusic(); // 배경 음악 변경 메소드 한번 더 호출
            }
        });

        bulletCount = findViewById(R.id.bulletCount);

        bulletCount.setText(spaceInvadersView.getPlayer().getBulletsCount() + "/30"); // SpaceInvadersView 에서 현재 플레이어 객체를 찾은 후, 그 플레이어의 남은 총알 수 return 받음
        tvScore.setText(Integer.toString(spaceInvadersView.getScore())); // SpaceInvadersView 에서 현재 점수를 불러옴

        effectSoundList = new ArrayList<>(); // 효과음을 담을 ArrayList 생성
        effectSoundList.add(PLAYER_SHOT, effectSound.load(MainActivity.this, R.raw.player_shot_sound, 1));
        // (효과음의 인덱스, SoundPool.load(현재 액티비티의 인스턴스, 효과음 Id, 우선순위)
        effectSoundList.add(PLAYER_HURT, effectSound.load(MainActivity.this, R.raw.player_hurt_sound, 1));
        effectSoundList.add(PLAYER_RELOAD, effectSound.load(MainActivity.this, R.raw.reload_sound, 1));
        effectSoundList.add(PLAYER_GET_ITEM, effectSound.load(MainActivity.this, R.raw.player_get_item_sound, 1));
        bgMusic.start(); // 배경 음악 재생

    }

    private void changeBgMusic() { // 배경음악 변경 메소드
        bgMusic = MediaPlayer.create(this, bgMusicList.get(bgMusicIndex)); // 배경 음악 인덱스에 해당하는 배경 음악 재생
        bgMusic.start(); // 배경 음악 재생
        bgMusicIndex++; // 배경 음악 인덱스 1 증가
        bgMusicIndex = bgMusicIndex % bgMusicList.size(); // 배경 음악 인덱스가 리스트의 크기(3)를 넘어가면, 나머지 연산을 통해 다시 0부터 시작하도록 설정 (0~2)
    }

    public static void effectSound(int flag) { // 효과음 재생 메소드 ( flag(0~3) )
        // SoundPool 실행
        effectSound.play(effectSoundList.get(flag), effectVolume, effectVolume, 0, 0, 1.0f); // 효과음 리소스, 효과음 불륨, 우선 순위, 반복 여부, 재생 속도
    }

    private void setBtnBehavior() {
        joyStick.setAutoReCenterButton(true); // 조이스틱이 중앙으로 자동으로 돌아오게 하기

        joyStick.setOnKeyListener((v, i, event) -> {    // 키가 눌릴 때 호출될 콜백 등록
            Log.d("keycode", Integer.toString(i));
            return false;
        });

        joyStick.setOnMoveListener(new JoystickView.OnMoveListener() { // 조이스틱 움직임 이벤트 리스너
            @Override
            public void onMove(int angle, int strength) { // 움직임이 발생될 때 (각도, 강도)
                Log.d("angle", Integer.toString(angle));
                Log.d("strength", Integer.toString(strength));

                if (angle > 67.5 && angle < 112.5) { // 위쪽으로 움직이는 각도 범위
                    spaceInvadersView.getPlayer().moveUp(strength / 10); // 위쪽으로 이동 (강도에 따라 움직이는 거리 조절)
                    spaceInvadersView.getPlayer().resetDx(); // 플레이어의 x 축 이동 속도 초기화
                } else if (angle > 247.5 && angle < 292.5) {
                    // 아래
                    spaceInvadersView.getPlayer().moveDown(strength / 10);
                    spaceInvadersView.getPlayer().resetDx();
                } else if (angle > 112.5 && angle < 157.5) {
                    // 왼쪽 대각선 위
                    spaceInvadersView.getPlayer().moveUp(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveLeft(strength / 10 * 0.5);
                } else if (angle > 157.5 && angle < 202.5) {
                    // 왼쪽
                    spaceInvadersView.getPlayer().moveLeft(strength / 10);
                    spaceInvadersView.getPlayer().resetDy();
                } else if (angle > 202.5 && angle < 247.5) {
                    // 왼쪽 대각선 아래
                    spaceInvadersView.getPlayer().moveLeft(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveDown(strength / 10 * 0.5);
                } else if (angle > 22.5 && angle < 67.5) {
                    // 오른쪽 대각선 위
                    spaceInvadersView.getPlayer().moveUp(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveRight(strength / 10 * 0.5);
                } else if (angle > 337.5 || angle < 22.5) {
                    // 오른쪽
                    spaceInvadersView.getPlayer().moveRight(strength / 10);
                    spaceInvadersView.getPlayer().resetDy();
                } else if (angle > 292.5 && angle < 337.5) {
                    // 오른쪽 아래
                    spaceInvadersView.getPlayer().moveRight(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveDown(strength / 10 * 0.5);
                }
            }
        });

        btnFire.setOnClickListener(new View.OnClickListener() { // 총알 발사 버튼
            @Override
            public void onClick(View v) {
                spaceInvadersView.getPlayer().fire();
            }
        });

        btnReload.setOnClickListener(v -> { // 재장전 버튼
            spaceInvadersView.getPlayer().reloadBullets();
        });

        btnPause.setOnClickListener(v -> { // 일시 중지 버튼
            spaceInvadersView.pause(); // 게임 일시 중지
            PauseDialog pauseDialog = new PauseDialog(MainActivity.this); // custom dialog 생성
            pauseDialog.setOnDismissListener(dialogInterface -> { // dismiss() 실행 후
                spaceInvadersView.resume(); // 게임 재개
            });
            pauseDialog.show(); // pause_dialog 화면에 표시
        });

        btnSpacialShot.setOnClickListener(v-> { // 특수 공격 버튼
            if (spaceInvadersView.getPlayer().getSpecialShotCount() >= 0) { // 플레이어가 특수 공격 가능 횟수가 남아있으면
                spaceInvadersView.getPlayer().specialShot();
            }
        });
    }
}