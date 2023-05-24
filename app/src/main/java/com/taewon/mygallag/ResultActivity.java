package com.taewon.mygallag;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over_dialog);

        init();
    }

    private void init() {
        findViewById(R.id.btnGoMain).setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        });

        ((TextView)findViewById(R.id.userFinalScoreText)).setText(getIntent().getIntExtra("score", 0) + "");
    }
}
