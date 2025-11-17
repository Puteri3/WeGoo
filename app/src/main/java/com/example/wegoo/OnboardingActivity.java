package com.example.wegoo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private OnboardingAdapter adapter;
    private LinearLayout layoutIndicators;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase and App Check
        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        setContentView(R.layout.onboarding);

        layoutIndicators = findViewById(R.id.layoutIndicators);
        btnNext = findViewById(R.id.btnNext);
        viewPager = findViewById(R.id.viewPager);

        setupOnboardingItems();

        // Set up indicator dots
        setupIndicators();
        setCurrentIndicator(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
                if (position == adapter.getItemCount() - 1) {
                    btnNext.setText("Skip");
                } else {
                    btnNext.setText("Next");
                }
            }
        });

        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() + 1 < adapter.getItemCount()) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                goToLogin();
            }
        });
    }

    private void setupOnboardingItems() {
        List<OnboardingItem> items = new ArrayList<>();

        items.add(new OnboardingItem(
                R.drawable.welcome1,
                "Welcome to WeGoo",
                "Discover the easiest way to manage your order to drive-on!"
        ));
        adapter = new OnboardingAdapter(items);
        viewPager.setAdapter(adapter);
    }

    private void setupIndicators() {
        layoutIndicators.removeAllViews();

        TextView[] indicators = new TextView[adapter.getItemCount()];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new TextView(this);
            indicators[i].setText("•");
            indicators[i].setTextSize(30);
            indicators[i].setLayoutParams(params);
            indicators[i].setTextColor(getColor(android.R.color.darker_gray));
            layoutIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int childCount = layoutIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView indicator = (TextView) layoutIndicators.getChildAt(i);
            if (i == index) {
                indicator.setTextColor(getColor(R.color.black));
            } else {
                indicator.setTextColor(getColor(android.R.color.darker_gray));
            }
        }
    }

    private void goToLogin() {
        startActivity(new Intent(OnboardingActivity.this, LoginActivity.class));
        finish();
    }
}
