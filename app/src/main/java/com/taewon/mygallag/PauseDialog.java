package com.taewon.mygallag;


import android.app.Dialog;
import android.content.Context;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

public class PauseDialog extends Dialog {
    RadioGroup bgMusicOnOff;        // 배경음악 설정 버튼을 가진 RadioGroup
    RadioGroup effectSoundOnOff;    // 효과음 설정 버튼을 가진 RadioGroup

    public PauseDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.pause_dialog);

        bgMusicOnOff = findViewById(R.id.bgMusicOnOff);
        effectSoundOnOff = findViewById(R.id.effectSoundOnOff);
        init();
    }

    public void init() {
        bgMusicOnOff.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {      // RadioGroup Check 값 변경 될 때
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.bgMusicOn:
                        MainActivity.bgMusic.setVolume(1, 1);       // 배경음악 소리 크기 1
                        break;
                    case R.id.bgMusicOff:
                        MainActivity.bgMusic.setVolume(0, 0);       // 배경음악 소리 크기 0
                        break;
                }
            }
        });

        effectSoundOnOff.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.effectSoundOn:
                        MainActivity.effectVolume = 1.0f;       // 효과음 소리 크기 1
                        break;
                    case R.id.effectSoundOff:
                        MainActivity.effectVolume = 0;          // 효과음 소리 크기 0
                        break;
                }
            }
        });
        /* Dialog 종료
         cancel() : 사용자가 Dialog 를 취소한 것(뒤로 가기 버튼)
         cancel() 메소드로 종료 시 onCancelListener() 가 호출되나 이 프로젝트에서는 사용하지 않음
         dismiss() : Dialog 를 강제 종료, 화면에서 제거, 메모리에서도 해제함
         dismiss() 메소드로 종료 후 MainActivity => OnDismissListener() 메소드 실행
         */
        findViewById(R.id.btnDialogCancel).setOnClickListener(v -> dismiss());
        findViewById(R.id.btnDialogOk).setOnClickListener(v -> dismiss());
    }
}
