package com.epam.dmitriy_korobeinikov.graphickeyview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by Dmitriy_Korobeinikov on 2/19/2016.
 */
public class GraphicKeyView extends RelativeLayout {

    private int startX;
    private int startY;
    private int endX;
    private int endY;

    private ArrayList<float[]> mLines;

    private Paint mPaint;
    private GraphicKeyNode mNodeUnderTap;

    public GraphicKeyView(Context context) {
        super(context);
    }

    public GraphicKeyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mLines = new ArrayList<>();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5f);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(startX, startY, endX, endY, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isTapInsideNode(event)) {
                    int[] center = mNodeUnderTap.getCenterCoordinates();
                    startX = endX = center[0];
                    startY = endY = center[1];
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mNodeUnderTap != null) {
                    endX = (int) event.getX();
                    endY = (int) event.getY();
                    if (isTapInsideNode(event)) {

                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mNodeUnderTap = null;
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    private boolean isTapInsideNode(MotionEvent event) {
        int childCount = getChildCount();
        View view;
        Rect rect = new Rect();
        for (int i = 0; i < childCount; i++) {
            view = getChildAt(i);
            view.getHitRect(rect);
            if (rect.contains((int) event.getX(), (int) event.getY())) {
                mNodeUnderTap = (GraphicKeyNode) view;
                return true;
            }
        }
        return false;
    }
}
