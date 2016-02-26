package com.epam.dmitriy_korobeinikov.graphickeyview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.DrawableRes;
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

    private float mArrowAngle;

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
                break;
            case STATE_CHECKED:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.key_node_checked);
                setPressed(true);
                break;
            case STATE_WRONG_KEY:
                Bitmap bitmap = createFromLayerList(R.drawable.key_node_wrong);
                drawable = rotateBitmap(bitmap);
                break;
        }
        setBackground(drawable);
    }

    public Bitmap createFromLayerList(@DrawableRes int res) {
        LayerDrawable layerDrawable = (LayerDrawable) ContextCompat.getDrawable(getContext(), res);
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);

        layerDrawable.setBounds(0, 0, getWidth(), getHeight());
        layerDrawable.draw(new Canvas(bitmap));
        return bitmap;
    }

    public BitmapDrawable rotateBitmap(Bitmap original) {
        Bitmap result = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);

        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        Canvas canvas = new Canvas(result);
        canvas.rotate(mArrowAngle, getWidth() / 2, getHeight() / 2);
        canvas.drawBitmap(original, 0, 0, paint);
        return new BitmapDrawable(getResources(), result);
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
