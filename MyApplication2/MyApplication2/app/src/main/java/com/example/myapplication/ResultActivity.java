package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    TextView tvScore, tvMessage;
    Button btnRestart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvScore = findViewById(R.id.tvScore);
        tvMessage = findViewById(R.id.tvMessage);
        btnRestart = findViewById(R.id.btnRestart);

        int score = getIntent().getIntExtra("SCORE", 0);
        int total = getIntent().getIntExtra("TOTAL", 0);

        tvScore.setText("Your Score: " + score + " / " + total);

        if (score == total) {
            tvMessage.setText("Excellent! 🎉");
        } else if (score >= total / 2) {
            tvMessage.setText("Good job! 👍");
        } else {
            tvMessage.setText("Try again next time 😅");
        }

        btnRestart.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
