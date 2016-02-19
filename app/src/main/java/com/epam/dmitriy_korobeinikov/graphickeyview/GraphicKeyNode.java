package com.epam.dmitriy_korobeinikov.graphickeyview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

/**
 * Created by Dmitriy_Korobeinikov on 2/19/2016.
 */
public class GraphicKeyNode extends ImageButton {

    public static final int STATE_DEFAULT = 0;
    public static final int STATE_PRESSED = 1;
    public static final int STATE_WRONG_KEY = 2;

    @IntDef({STATE_DEFAULT, STATE_PRESSED, STATE_WRONG_KEY})
    public @interface KeyNodeState {

    }

    public GraphicKeyNode(Context context) {
        super(context);
    }

    public GraphicKeyNode(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Drawable drawable = getResources().getDrawable(R.drawable.key_node_pressed);
        setBackground(drawable);
        return false;
    }

    public int[] getCenterCoordinates() {
        int[] center = new int[2];
        center[0] = (int) (getX() + getWidth() / 2);
        center[1] = (int) (getY() + getHeight() / 2);
        return center;
    }

    public void updateStateDrawable(@KeyNodeState int state) {

    }
}
