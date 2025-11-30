package com.example.wegoo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PaymentActivity extends AppCompatActivity {

    private PaymentSheet paymentSheet;
    private String clientSecret;
    private MaterialButton btnPay;
    private ProgressBar progressBar;
    private long amount;
    private String bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Get vehicle price from intent
        Intent intent = getIntent();
        double vehiclePrice = intent.getDoubleExtra("vehiclePrice", 0.0);
        bookingId = intent.getStringExtra("bookingId");
        amount = (long) (vehiclePrice * 100); // Convert to cents

        TextView tvAmount = findViewById(R.id.tvAmount);
        tvAmount.setText(String.format("Amount: RM%.2f", vehiclePrice));

        // Initialize Stripe
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51SOHDGHFc48fE60VrTjtkJKoHftiF8BpeI9U4J26zeaEMii8rH2S5hg8RqSV0ewSQNcANeHkXpFUzMX2PawGHfwN00jUHCqiSw"
        );

        btnPay = findViewById(R.id.btnPay);
        progressBar = findViewById(R.id.progressBar);

        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            progressBar.setVisibility(View.GONE);
            if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
                Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
                Intent successIntent = new Intent(PaymentActivity.this, BookingSuccessActivity.class);
                successIntent.putExtra("bookingId", bookingId);
                startActivity(successIntent);
                finish();
            } else {
                Toast.makeText(this, "Payment canceled or failed!", Toast.LENGTH_SHORT).show();
            }
        });

        btnPay.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            createPaymentIntent();
        });
    }

    private void createPaymentIntent() {
        new Thread(() -> {
            try {
                URL url = new URL("https://unstuccoed-nonfamilial-almeta.ngrok-free.dev/create-payment-intent"); // your PC IP
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonInputString = "{\"amount\": " + amount + "}"; // Use dynamic amount
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(jsonInputString.getBytes("utf-8"));
                }

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8")
                );
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                JSONObject json = new JSONObject(response.toString());
                clientSecret = json.getString("clientSecret");

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    paymentSheet.presentWithPaymentIntent(
                            clientSecret,
                            new PaymentSheet.Configuration("WeGoo App")
                    );
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
}
