package com.example.wegoo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.opencv.android.OpenCVLoader;

public class OpenCVDetectionActivity extends AppCompatActivity {

    private static final String TAG = "OpenCVDetectionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv_detection);

        Button btnOpenCameraTest = findViewById(R.id.btnOpenCameraTest);

        // Initialize OpenCV
        if (OpenCVLoader.initDebug()) {
            Log.i(TAG, "✅ OpenCV loaded successfully");
            Toast.makeText(this, "OpenCV loaded successfully", Toast.LENGTH_SHORT).show();

            // Bila dah load, enable button test kamera
            btnOpenCameraTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OpenCVDetectionActivity.this, CameraTestActivity.class);
                    startActivity(intent);
                }
            });

        } else {
            Log.e(TAG, "❌ OpenCV initialization failed!");
            Toast.makeText(this, "Failed to load OpenCV", Toast.LENGTH_LONG).show();

            // Disable butang kalau OpenCV gagal load
            btnOpenCameraTest.setEnabled(false);
        }
    }
}
