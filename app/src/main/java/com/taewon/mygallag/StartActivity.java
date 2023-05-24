package com.taewon.mygallag;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
    int characterID, effectId; // Activity 시작 화면에서 선택한 캐릭터의 아이디를 담는 변수 (img[?])
    ImageButton btnStart; // 캐릭터 선택시 화면 중앙에 뜨는 비행기 아이콘
    TextView tvGuide; // 화면 아래 "캐릭터를 선택하세요."
    MediaPlayer mediaPlayer; // 오디오 파일 재생 & 관리, 대형 오디오 파일을 재생할 때 사용 (mp3, wav, etc)
    ImageView imgView[] = new ImageView[8];
    Integer img_id[] = {R.id.ship_001, R.id.ship_002, R.id.ship_003, R.id.ship_004,
            R.id.ship_005, R.id.ship_006, R.id.ship_007, R.id.ship_008};
    Integer img[] = {R.drawable.ship_0000, R.drawable.ship_0001, R.drawable.ship_0002, R.drawable.ship_0003,
            R.drawable.ship_0004, R.drawable.ship_0005, R.drawable.ship_0006, R.drawable.ship_0007};
    SoundPool soundPool; // 짧은 시간의 오디오 파일을 재생할 때 사용, 메모리 효율이 높아 여러개의 소리 동시에 재생 가능 (wav)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mediaPlayer = MediaPlayer.create(this, R.raw.robby_bgm);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        soundPool = new SoundPool(5, AudioManager.USE_DEFAULT_STREAM_TYPE, 0);
        effectId = soundPool.load(this, R.raw.reload_sound, 1);

        btnStart = findViewById(R.id.btnStart);
        tvGuide = findViewById(R.id.tvGuide);

        for (int i = 0; i < imgView.length; i++) {
            imgView[i] = findViewById(img_id[i]); // ImageView 할당

            int index = i; // 선택한 이미지 번호 알기

            imgView[i].setOnClickListener(view -> {     // imgView(캐릭터 선택 시)
                characterID = img[index]; // 선택한 이미지의 resourceID 를 characterID 에 할당
                btnStart.setVisibility(View.VISIBLE); // 시작 버튼 보이게
                btnStart.setEnabled(true); // 시작 버튼 활성화
                btnStart.setImageResource(characterID); // 시작 버튼의 이미지를 선택한 우주선 이미지로 설정
                tvGuide.setVisibility(View.INVISIBLE); // 하단의 TextView("캐릭터를 선택하세요") 숨기기
                soundPool.play(effectId, 1, 1, 0, 0, 1.0f); // 효과음 재생
            });
        }

        init();
    }

    private void init() {
        findViewById(R.id.btnStart).setVisibility(View.GONE); // 시작 버튼을 화면에서 숨김 (버튼 위치는 남김)
        findViewById(R.id.btnStart).setEnabled(false); // 선택 안 되게 하기
        findViewById(R.id.btnStart).setOnClickListener(view -> {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            intent.putExtra("character", characterID); // 선택한 이미지를 "character" 란 이름으로 intent 에 추가
            startActivity(intent); // MainActivity로 이동
            finish(); // 현재 액티비티 (StartActivity) 종료
        });
    }

    protected void onDestroy() { // 액티비티 소멸 직전 호출: mediaPlayer 살아있으면 리소스 소멸
        super.onDestroy();

        if (mediaPlayer != null) {  // 배경음악이 재생 중인 경우
            mediaPlayer.release();  // 메모리 누수 방지
            mediaPlayer = null;
        }
    }
}
