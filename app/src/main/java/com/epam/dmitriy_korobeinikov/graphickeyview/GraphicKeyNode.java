package com.epam.dmitriy_korobeinikov.graphickeyview;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Dmitriy_Korobeinikov on 2/19/2016.
 */
public class GraphicKeyNode extends View {

    private static final int MAX_LEVEL = 10000;
    private static final float LEVELS_PER_DEGREE = MAX_LEVEL / 360f;
    public static final int STATE_DEFAULT = 0;
    public static final int STATE_CHECKED = 1;
    public static final int STATE_WRONG_KEY = 2;

    private float mArrowAngle = -1;

    @IntDef({STATE_DEFAULT, STATE_CHECKED, STATE_WRONG_KEY})
    public @interface KeyNodeState {

    }

    public GraphicKeyNode(Context context) {
        super(context);
    }

    public GraphicKeyNode(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void updateState(@KeyNodeState int state) {
        Drawable drawable = null;
        switch (state) {
            case STATE_DEFAULT:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.key_node_default);
                setPressed(false);
                mArrowAngle = -1;
                break;
            case STATE_CHECKED:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.key_node_checked);
                setPressed(true);
                break;
            case STATE_WRONG_KEY:
                if (mArrowAngle >= 0) {
                    drawable = ContextCompat.getDrawable(getContext(), R.drawable.rotate_node_wrong_with_arrow);
                    int level = Math.round(LEVELS_PER_DEGREE * mArrowAngle);
                    drawable.setLevel(level);
                } else {
                    drawable = ContextCompat.getDrawable(getContext(), R.drawable.key_node_wrong);
                }
                break;
        }
        setBackground(drawable);
    }

    public void setArrowAngle(float arrowAngle) {
        mArrowAngle = arrowAngle;
    }

    public Point getCenter() {
        return new Point(getCenterX(), getCenterY());
    }

    public int getCenterX() {
        return (int) (getX() + getWidth() / 2);
    }

    public int getCenterY() {
        return (int) (getY() + getHeight() / 2);
    }
}
