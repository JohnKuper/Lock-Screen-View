package com.epam.dmitriy_korobeinikov.graphickeyview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Dmitriy_Korobeinikov on 2/19/2016.
 */
public class GraphicKeyNode extends View {

    private Paint mPaint;

    public GraphicKeyNode(Context context) {
        super(context);
    }

    public GraphicKeyNode(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(3f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(20, 20, 10, mPaint);
    }
}
