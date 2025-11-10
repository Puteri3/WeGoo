package com.example.wegoo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraTestActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 10;
    private static final String TAG = "CameraX_OpenCV";
    private PreviewView previewView;
    private ImageView edgeView;
    private ExecutorService cameraExecutor;

    private long lastDetectionTime = 0;
    private static final long DETECTION_INTERVAL_MS = 1000; // detect setiap 1s

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);

        previewView = findViewById(R.id.previewView);
        edgeView = findViewById(R.id.edgeView);
        cameraExecutor = Executors.newSingleThreadExecutor();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }

        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "❌ OpenCV not loaded");
        } else {
            Log.d(TAG, "✅ OpenCV loaded");
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeFrame);
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void analyzeFrame(ImageProxy image) {
        if (image == null || image.getImage() == null) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDetectionTime < DETECTION_INTERVAL_MS) return;
        lastDetectionTime = currentTime;

        try {
            Mat mat = ImageUtils.imageToMat(image);
            if (mat != null) {
                Mat gray = new Mat();
                Imgproc.cvtColor(mat, gray, Imgproc.COLOR_RGBA2GRAY);
                Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);

                Mat edges = new Mat();
                Imgproc.Canny(gray, edges, 50, 120);

                double edgeCount = Core.countNonZero(edges);
                if (edgeCount > 30000) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "⚠️ Scratch detected!", Toast.LENGTH_SHORT).show());
                }

                // 💡 Convert hasil Canny ke Bitmap supaya boleh nampak
                Bitmap bmp = Bitmap.createBitmap(edges.cols(), edges.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(edges, bmp);

                runOnUiThread(() -> edgeView.setImageBitmap(bmp));

                gray.release();
                edges.release();
                mat.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error analyzing frame: " + e.getMessage());
        } finally {
            image.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "Camera permission required!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
