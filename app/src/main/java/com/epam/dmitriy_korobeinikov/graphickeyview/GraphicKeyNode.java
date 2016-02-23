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

    public static final int STATE_DEFAULT = 0;
    public static final int STATE_CHECKED = 1;
    public static final int STATE_WRONG_KEY = 2;

    private boolean isChecked;

    @IntDef({STATE_DEFAULT, STATE_CHECKED, STATE_WRONG_KEY})
    public @interface KeyNodeState {

    }

    public GraphicKeyNode(Context context) {
        super(context);
    }

    public GraphicKeyNode(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Point getCenter() {
        int x = (int) (getX() + getWidth() / 2);
        int y = (int) (getY() + getHeight() / 2);
        return new Point(x, y);
    }

    public void updateState(@KeyNodeState int state) {
        Drawable drawable = null;
        switch (state) {
            case STATE_DEFAULT:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.key_node_default);
                isChecked = false;
                break;
            case STATE_CHECKED:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.key_node_checked);
                isChecked = true;
                break;
            case STATE_WRONG_KEY:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.key_node_wrong);
                break;
        }
        setBackground(drawable);
    }

    public boolean isChecked() {
        return isChecked;
    }
}
