package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tvQuestion;
    ImageView imgResult;
    Button btnTrue, btnFalse, btnNext, btnReturn;
    Button btnChoiceA, btnChoiceB, btnChoiceC, btnChoiceD;
    LinearLayout layoutMCQ, layoutTF;

    // Soalan (campuran True/False + Multiple Choice)
    String[] questions = new String[]{
            "What planet is known as the Red Planet?",
            "Semua mamalia hidup di darat?",
            "Google pada mulanya dipanggil BackRub.",
            "The color of blood is red.",
            "Java is a type of operating system.",
            "Android Studio is used for mobile app development.",
            "The Sun is a planet.",
            "Cats are mammals.",
            "The Great Wall of China is in Japan.",
            "Rukun Iman ada 5 perkara."
    };

    boolean[] isMultipleChoice = {
            true, false, false, false, false, false, false, false, false, false
    };

    boolean[] tfAnswers = {
            false, // Placeholder for MCQ
            false, true, true, false, true, false, true, false, false
    };

    String[][] choices = {
            {"A) Earth", "B) Mars", "C) Venus", "D) Jupiter"},
            {}, {}, {}, {}, {}, {}, {}, {}, {}
    };

    String[] correctChoices = {
            "B) Mars", "", "", "", "", "", "", "", "", ""
    };

    int currentIndex = 0;
    int score = 0;
    boolean answered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ Inisialisasi komponen
        tvQuestion = findViewById(R.id.tvQuestion);
        imgResult = findViewById(R.id.imgResult);
        btnTrue = findViewById(R.id.btnTrue);
        btnFalse = findViewById(R.id.btnFalse);
        btnNext = findViewById(R.id.btnNext);
        btnReturn = findViewById(R.id.btnReturn);
        btnChoiceA = findViewById(R.id.btnChoiceA);
        btnChoiceB = findViewById(R.id.btnChoiceB);
        btnChoiceC = findViewById(R.id.btnChoiceC);
        btnChoiceD = findViewById(R.id.btnChoiceD);

        // ✅ Guna ID sebenar dari layout XML
        layoutMCQ = findViewById(R.id.mcqContainer);
        layoutTF = findViewById(R.id.trueFalseContainer);

        // 🟣 Event listener untuk True/False
        btnTrue.setOnClickListener(v -> checkTFAnswer(true, v));
        btnFalse.setOnClickListener(v -> checkTFAnswer(false, v));

        // 🟣 Event listener untuk MCQ
        btnChoiceA.setOnClickListener(v -> checkMCQAnswer(((Button) v).getText().toString(), v));
        btnChoiceB.setOnClickListener(v -> checkMCQAnswer(((Button) v).getText().toString(), v));
        btnChoiceC.setOnClickListener(v -> checkMCQAnswer(((Button) v).getText().toString(), v));
        btnChoiceD.setOnClickListener(v -> checkMCQAnswer(((Button) v).getText().toString(), v));

        // 🟣 Button Next
        btnNext.setOnClickListener(v -> {
            if (currentIndex < questions.length - 1) {
                currentIndex++;
                updateQuestion();
            } else {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("SCORE", score);
                intent.putExtra("TOTAL", questions.length);
                startActivity(intent);
                finish();
            }
        });

        // 🟣 Button Return
        btnReturn.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                updateQuestion();
            } else {
                Snackbar.make(v, "This is the first question!", Snackbar.LENGTH_SHORT).show();
            }
        });

        // Papar soalan pertama
        updateQuestion();
    }

    private void updateQuestion() {
        tvQuestion.setText(questions[currentIndex]);
        imgResult.setImageResource(R.drawable.question);
        answered = false;

        // ✨ Animasi fade & resize automatik
        LinearLayout questionContainer = findViewById(R.id.questionContainer);
        questionContainer.animate()
                .alpha(0f)
                .setDuration(150)
                .withEndAction(() -> {
                    if (isMultipleChoice[currentIndex]) {
                        layoutMCQ.setVisibility(View.VISIBLE);
                        layoutTF.setVisibility(View.GONE);

                        btnChoiceA.setText(choices[currentIndex][0]);
                        btnChoiceB.setText(choices[currentIndex][1]);
                        btnChoiceC.setText(choices[currentIndex][2]);
                        btnChoiceD.setText(choices[currentIndex][3]);
                    } else {
                        layoutMCQ.setVisibility(View.GONE);
                        layoutTF.setVisibility(View.VISIBLE);
                    }

                    float targetScale = isMultipleChoice[currentIndex] ? 1.1f : 0.9f;
                    questionContainer.setScaleY(targetScale);

                    questionContainer.animate()
                            .alpha(1f)
                            .scaleY(1f)
                            .setDuration(300)
                            .start();

                    questionContainer.requestLayout();
                })
                .start();
    }

    private void checkTFAnswer(boolean userAnswer, View view) {
        if (answered || isMultipleChoice[currentIndex]) return;
        boolean correct = tfAnswers[currentIndex];
        if (userAnswer == correct) {
            imgResult.setImageResource(R.drawable.ic_correct);
            Snackbar.make(view, "Correct Answer!", Snackbar.LENGTH_SHORT).show();
            score++;
        } else {
            imgResult.setImageResource(R.drawable.ic_incorrect);
            Snackbar.make(view, "Wrong Answer!", Snackbar.LENGTH_SHORT).show();
        }
        answered = true;
    }

    private void checkMCQAnswer(String selectedChoice, View view) {
        if (answered || !isMultipleChoice[currentIndex]) return;
        String correct = correctChoices[currentIndex];
        if (selectedChoice.equals(correct)) {
            imgResult.setImageResource(R.drawable.ic_correct);
            Snackbar.make(view, "Correct Answer!", Snackbar.LENGTH_SHORT).show();
            score++;
        } else {
            imgResult.setImageResource(R.drawable.ic_incorrect);
            Snackbar.make(view, "Wrong Answer!", Snackbar.LENGTH_SHORT).show();
        }
        answered = true;
    }
}
