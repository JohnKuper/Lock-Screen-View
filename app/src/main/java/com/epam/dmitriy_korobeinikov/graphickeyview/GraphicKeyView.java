package com.epam.dmitriy_korobeinikov.graphickeyview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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

    private ArrayList<float[]> mCompletedLines;

    private Paint mPaint;
    private GraphicKeyNode mStartNode;
    private GraphicKeyNode mEndNode;
    private ArrayList<GraphicKeyNode> mKeyNodes;

    public GraphicKeyView(Context context) {
        super(context);
    }

    public GraphicKeyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mCompletedLines = new ArrayList<>();
        mKeyNodes = new ArrayList<>();

        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5f);
        setWillNotDraw(false);
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        mKeyNodes.add((GraphicKeyNode) child);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (float[] line : mCompletedLines) {
            canvas.drawLines(line, mPaint);
        }
        canvas.drawLine(startX, startY, endX, endY, mPaint);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartNode = getNodeUnderEvent(event);
                if (mStartNode != null) {
                    mStartNode.updateState(GraphicKeyNode.STATE_PRESSED);
                    Point startCenter = mStartNode.getCenter();
                    startX = endX = startCenter.x;
                    startY = endY = startCenter.y;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mStartNode != null) {
                    endX = (int) event.getX();
                    endY = (int) event.getY();
                    mEndNode = getNodeUnderEvent(event);
                    if (mEndNode != null) {
                        mEndNode.updateState(GraphicKeyNode.STATE_PRESSED);
                        addCompleteLine();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                setupInitialState();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    private void setupInitialState() {
        mStartNode = null;
        mEndNode = null;
        startX = startY = endX = endY = 0;
        mCompletedLines.clear();
        for (GraphicKeyNode node : mKeyNodes) {
            node.updateState(GraphicKeyNode.STATE_DEFAULT);
        }
    }

    private void addCompleteLine() {
        Point endCenter = mEndNode.getCenter();
        endX = endCenter.x;
        endY = endCenter.y;
        float[] completeLine = {startX, startY, endX, endY};
        mCompletedLines.add(completeLine);

        startX = endCenter.x;
        startY = endCenter.y;
    }

    public GraphicKeyNode getNodeUnderEvent(MotionEvent event) {
        GraphicKeyNode node;
        Rect rect = new Rect();
        for (int i = 0; i < mKeyNodes.size(); i++) {
            node = mKeyNodes.get(i);
            node.getHitRect(rect);
            if (!node.isPressed() && rect.contains((int) event.getX(), (int) event.getY())) {
                return node;
            }
        }
        return null;
    }
}
