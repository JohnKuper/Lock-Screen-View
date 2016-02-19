package com.epam.dmitriy_korobeinikov.graphickeyview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Created by Dmitriy_Korobeinikov on 2/19/2016.
 */
public class GraphicKeyView extends ViewGroup {

    private Paint mPaint;
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    public GraphicKeyView(Context context) {
        super(context);
    }

    public GraphicKeyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5f);
        setWillNotDraw(false);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(startX, startY, endX, endY, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                startY = (int) event.getY();
                endX = (int) event.getX();
                endY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                endX = (int) event.getX();
                endY = (int) event.getY();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }
}
