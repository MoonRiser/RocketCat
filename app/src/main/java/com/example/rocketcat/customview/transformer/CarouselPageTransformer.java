package com.example.rocketcat.customview.transformer;


import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class CarouselPageTransformer implements ViewPager2.PageTransformer {

    private static final float CENTER_PAGE_SCALE = 0.7f;
    private int offscreenPageLimit;
    private ViewPager2 viewPager2;

//    public CarouselPageTransformer(ViewPager2 viewPager2) {
//        this.viewPager2 = viewPager2;
//        offscreenPageLimit = viewPager2.getOffscreenPageLimit();
//    }

    private static final Matrix OFFSET_MATRIX = new Matrix();
    private static final Camera OFFSET_CAMERA = new Camera();
    private static final float[] OFFSET_TEMP_FLOAT = new float[2];
    private static final float minScale = 0.6f;

    @Override
    public void transformPage(@NonNull View page, float position) {
//        float offset = 24 * position;
//        page.setTranslationX(offset);
        //page.width()返回的值是不变的；因为page是根布局不会变的，整个图像的变换都约束在page中进行
        int pagerWidth = page.getWidth();
        float factor = (1 - CENTER_PAGE_SCALE) / 2 + (1 - CENTER_PAGE_SCALE + 0.1f) / 2;

//        final float rotation = (position < 0 ? 30f : -30f) * Math.abs(position);
//        page.setTranslationX(getOffsetXForRotation(rotation, page.getWidth(), page.getHeight()));
//        page.setPivotX(page.getWidth() * 0.5f);
//        page.setPivotY(0);
        //绕Y轴旋转的时候，旋转图形的宽度是占满page的width的，所以下面需要等比例缩放
        //page.setRotationY(rotation);
//        float scaleX = Math.max(1 - Math.abs(position), minScale);
//        //等比例缩放，而page的width不变，就会导致item之间间距观感上变大，于是需要上面的平移
//        page.setScaleX(scaleX);


        float scaleFactor = CENTER_PAGE_SCALE - Math.abs(position) * 0.1f;
        page.setScaleX(scaleFactor);
        page.setScaleY(scaleFactor);
        float rotation = -30f * position;
        if (position > 1 || position < -1) {
            page.setRotationY(80 * (position > 0 ? -1 : 1));
        } else {
            page.setRotationY(rotation);
        }

        page.setTranslationX(-0.6f * pagerWidth * position);
        //      page.setTranslationX(getOffsetXForRotation(rotation, page.getWidth(), page.getHeight()));
//


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
