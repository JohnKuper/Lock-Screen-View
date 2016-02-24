package com.epam.dmitriy_korobeinikov.graphickeyview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.epam.dmitriy_korobeinikov.graphickeyview.GraphicKeyNode.KeyNodeState;
import com.epam.dmitriy_korobeinikov.graphickeyview.util.IntersectUtil;

import java.util.ArrayList;

/**
 * Created by Dmitriy_Korobeinikov on 2/19/2016.
 */
public class GraphicKeyLayout extends ViewGroup {
    public static final String TAG = GraphicKeyLayout.class.getSimpleName();

    private static final String CORRECT_PATH = "01258";
    private static final int NODES_IN_ROW = 3;

    private final DelayedInitialState mDelayedInitialState = new DelayedInitialState();

    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private int mHorizontalSpacing;
    private int mVerticalSpacing;

    private Paint mPaint;
    private GraphicKeyNode mStartNode;
    private GraphicKeyNode mLastNode;
    private ArrayList<float[]> mCompletedLines;
    private StringBuilder mPath = new StringBuilder();

    public GraphicKeyLayout(Context context) {
        super(context);
    }

    public GraphicKeyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GraphicKeyLayout);
        mHorizontalSpacing = a.getDimensionPixelSize(R.styleable.GraphicKeyLayout_horizontalSpacing, 0);
        mVerticalSpacing = a.getDimensionPixelSize(R.styleable.GraphicKeyLayout_verticalSpacing, 0);
        a.recycle();

        init();
    }

    private void init() {
        mPaint = new Paint();
        mCompletedLines = new ArrayList<>();

        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5f);
        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int curWidth = getPaddingLeft();
        int curHeight = getPaddingTop();

        final int childCount = getChildCount();
        int ordinalNode = 0;
        for (int i = 0; i < childCount; i++) {
            ordinalNode++;
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            lp.x = curWidth;
            lp.y = curHeight;

            curWidth += child.getMeasuredWidth() + mHorizontalSpacing;

            if (ordinalNode % NODES_IN_ROW == 0) {
                curHeight += mVerticalSpacing + child.getMeasuredHeight();
                curWidth = getPaddingLeft();
            }
        }
        //TODO implement correct width and height
        setMeasuredDimension(resolveSize(widthSize, widthMeasureSpec), resolveSize(heightSize, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(), lp.y + child.getMeasuredHeight());
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
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
                this.removeCallbacks(mDelayedInitialState);
                setupInitialState();

                View startNode = getNodeUnderEvent(event);
                if (startNode != null) {
                    mStartNode = (GraphicKeyNode) startNode;
                    mStartNode.updateState(GraphicKeyNode.STATE_CHECKED);
                    Point center = mStartNode.getCenter();
                    startNewLine(center);
                    mPath.append(indexOfChild(startNode));
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mStartNode != null) {
                    endX = (int) event.getX();
                    endY = (int) event.getY();
                    View nextNode = getNodeUnderEvent(event);
                    if (nextNode != null) {
                        mLastNode = (GraphicKeyNode) nextNode;
                        mLastNode.updateState(GraphicKeyNode.STATE_CHECKED);
                        connectNodes();
                        mPath.append(indexOfChild(nextNode));
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mStartNode == null) {
                    break;
                }
                cutOffExcessEnd();
                checkGraphicKey();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    private void cutOffExcessEnd() {
        Point endCenter;
        if (mLastNode != null) {
            endCenter = mLastNode.getCenter();
        } else {
            endCenter = mStartNode.getCenter();
        }
        endX = endCenter.x;
        endY = endCenter.y;
    }

    private void checkGraphicKey() {
        if (mPath.toString().equals(CORRECT_PATH)) {
            setupInitialState();
        } else {
            showErrorKey();
            this.postDelayed(mDelayedInitialState, 2000);
        }
    }

    private void showErrorKey() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            GraphicKeyNode child = (GraphicKeyNode) getChildAt(i);
            if (child.isPressed()) {
                child.updateState(GraphicKeyNode.STATE_WRONG_KEY);
            }
        }
    }

    private void startNewLine(Point center) {
        startX = endX = center.x;
        startY = endY = center.y;
    }

    private void connectNodes() {
        Point lastNodeCenter = mLastNode.getCenter();
        float[] completeLine = {startX, startY, lastNodeCenter.x, lastNodeCenter.y};

        Rect hitRect = new Rect();
        View node;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            node = getChildAt(i);
            node.getHitRect(hitRect);
            if (!node.isPressed() && IntersectUtil.isLineIntersectRect(completeLine[0], completeLine[1], completeLine[2], completeLine[3],
                    hitRect.left, hitRect.top, hitRect.right, hitRect.bottom)) {
                ((GraphicKeyNode) node).updateState(GraphicKeyNode.STATE_CHECKED);
                mPath.append(indexOfChild(node));
            }
        }

        mCompletedLines.add(completeLine);
        startNewLine(lastNodeCenter);
    }



    private void setupInitialState() {
        mStartNode = null;
        mLastNode = null;
        mPath.setLength(0);
        startNewLine(new Point(0, 0));
        mCompletedLines.clear();
        updateAllNodesState(GraphicKeyNode.STATE_DEFAULT);
    }

    private void updateAllNodesState(@KeyNodeState int state) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            GraphicKeyNode child = (GraphicKeyNode) getChildAt(i);
            child.updateState(state);
        }
    }

    public View getNodeUnderEvent(MotionEvent event) {
        View node;
        Rect rect = new Rect();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            node = getChildAt(i);
            node.getHitRect(rect);
            if (!node.isPressed() && rect.contains((int) event.getX(), (int) event.getY())) {
                return node;
            }
        }
        return null;
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        private int x;
        private int y;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }
    }

    private final class DelayedInitialState implements Runnable {

        @Override
        public void run() {
            setupInitialState();
        }
    }
}
