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
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Dmitriy_Korobeinikov on 2/19/2016.
 */
public class GraphicKeyLayout extends ViewGroup {

    private final static String CORRECT_PATH = "12369";
    private static final int NODES_IN_ROW = 3;

    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private int mHorizontalSpacing;
    private int mVerticalSpacing;

    private Paint mPaint;
    private GraphicKeyNode mStartNode;
    private GraphicKeyNode mEndNode;
    private ArrayList<float[]> mCompletedLines;
    private ArrayList<GraphicKeyNode> mNodes;
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
        mNodes = new ArrayList<>();

        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5f);
        setWillNotDraw(false);
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        mNodes.add((GraphicKeyNode) child);
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
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p.width, p.height);
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
                    mStartNode.updateState(GraphicKeyNode.STATE_CHECKED);
                    Point center = mStartNode.getCenter();
                    startNewLine(center);
                    mPath.append(mNodes.indexOf(mStartNode));
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mStartNode != null) {
                    endX = (int) event.getX();
                    endY = (int) event.getY();
                    mEndNode = getNodeUnderEvent(event);
                    if (mEndNode != null) {
                        mEndNode.updateState(GraphicKeyNode.STATE_CHECKED);
                        connectNodes();
                        mPath.append(mNodes.indexOf(mEndNode));
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mPath.toString().equals(CORRECT_PATH)) {
                    Toast.makeText(getContext(), "This key is correct", Toast.LENGTH_SHORT).show();
                    setupInitialState();
                }
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    private void startNewLine(Point center) {
        startX = endX = center.x;
        startY = endY = center.y;
    }

    private void connectNodes() {
        Point center = mEndNode.getCenter();
        float[] completeLine = {startX, startY, center.x, center.y};
        mCompletedLines.add(completeLine);
        startNewLine(center);
    }

    private void setupInitialState() {
        mStartNode = null;
        mEndNode = null;
        startX = startY = endX = endY = 0;
        mCompletedLines.clear();
    }

    public GraphicKeyNode getNodeUnderEvent(MotionEvent event) {
        GraphicKeyNode node;
        Rect rect = new Rect();
        for (int i = 0; i < mNodes.size(); i++) {
            node = mNodes.get(i);
            node.getHitRect(rect);
            if (!node.isChecked() && rect.contains((int) event.getX(), (int) event.getY())) {
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

        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }
}
