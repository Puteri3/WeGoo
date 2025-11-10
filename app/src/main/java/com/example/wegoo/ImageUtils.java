package com.example.wegoo;

import android.media.Image;

import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;

public class ImageUtils {

    @androidx.camera.core.ExperimentalGetImage
    @OptIn(markerClass = ExperimentalGetImage.class)
    public static Mat imageToMat(ImageProxy image) {
        Image img = image.getImage();
        if (img == null) return null;

        // Ambil 3 plane (Y, U, V)
        Image.Plane[] planes = img.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];

        // Susun semula ke format NV21 (Y + VU)
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        // Buat Mat dari NV21
        Mat yuv = new Mat(image.getHeight() + image.getHeight() / 2, image.getWidth(), CvType.CV_8UC1);
        yuv.put(0, 0, nv21);

        // Convert YUV → RGBA
        Mat rgba = new Mat();
        Imgproc.cvtColor(yuv, rgba, Imgproc.COLOR_YUV2RGBA_NV21);

        yuv.release();
        return rgba;
    }
}
