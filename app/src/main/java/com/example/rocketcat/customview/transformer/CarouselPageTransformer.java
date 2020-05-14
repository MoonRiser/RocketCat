package com.example.rocketcat.customview.transformer;


import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class CarouselPageTransformer implements ViewPager2.PageTransformer {


//    private int offscreenPageLimit;
//    private ViewPager2 viewPager2;

//    public CarouselPageTransformer(ViewPager2 viewPager2) {
//        this.viewPager2 = viewPager2;
//        offscreenPageLimit = viewPager2.getOffscreenPageLimit();
//    }

    private static final Matrix OFFSET_MATRIX = new Matrix();
    private static final Camera OFFSET_CAMERA = new Camera();
    private static final float[] OFFSET_TEMP_FLOAT = new float[2];
    private static final float CENTER_PAGE_SCALE = 0.7f;
    private static final int angle = 45;
    private static final float OFFSET_HORIZON = 0.45f;


    @Override
    public void transformPage(@NonNull View page, float position) {
//        float offset = 24 * position;
//        page.setTranslationX(offset);
        //page.width()返回的值是不变的；因为page是根布局不会变的，整个图像的变换都约束在page中进行
        int pagerWidth = page.getWidth();


        float rotation = -angle * position;
        float scaleFactor = CENTER_PAGE_SCALE - Math.abs(position) * 0.1f;
        float translation = -OFFSET_HORIZON * pagerWidth * position;
        page.setScaleX(scaleFactor);
        page.setScaleY(scaleFactor);

        if (position > 1 || position < -1) {
            page.setRotationY(angle * (position > 0 ? -1 : 1));
        } else {
            page.setRotationY(rotation);
        }

        page.setTranslationX(translation);


    }


    private static float getOffsetXForRotation(float degrees, int width, int height) {
        OFFSET_MATRIX.reset();
        OFFSET_CAMERA.save();
        OFFSET_CAMERA.rotateY(Math.abs(degrees));
        OFFSET_CAMERA.getMatrix(OFFSET_MATRIX);
        OFFSET_CAMERA.restore();
        OFFSET_MATRIX.preTranslate(-width * 0.5f, -height * 0.5f);
        OFFSET_MATRIX.postTranslate(width * 0.5f, height * 0.5f);
        OFFSET_TEMP_FLOAT[0] = width;
        OFFSET_TEMP_FLOAT[1] = height;
        OFFSET_MATRIX.mapPoints(OFFSET_TEMP_FLOAT);
        return (width - OFFSET_TEMP_FLOAT[0]) * (degrees > 0.0f ? 1.0f : -1.0f);
    }
}
