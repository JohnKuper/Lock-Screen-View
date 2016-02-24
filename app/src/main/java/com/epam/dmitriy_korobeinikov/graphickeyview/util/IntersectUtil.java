package com.epam.dmitriy_korobeinikov.graphickeyview.util;

/**
 * Created by Dmitriy_Korobeinikov on 2/24/2016.
 */
public final class IntersectUtil {

    private IntersectUtil() {
    }

    public static boolean isLineIntersectRect(float x1, float y1, float x2, float y2, float left, float top, float right, float bottom) {
        if ((x1 <= left && x2 <= left) || (y1 <= top && y2 <= top) || (x1 >= right && x2 >= right) || (y1 >= bottom && y2 >= bottom)) return false;

        float m = (y2 - y1) / (x2 - x1);

        float y = m * (left - x1) + y1;
        if (y >= top && y <= bottom) return true;

        y = m * (right - x1) + y1;
        if (y > top && y < bottom) return true;

        float x = (top - y1) / m + x1;
        if (x > left && x < right) return true;

        x = (bottom - y1) / m + x1;
        return x > left && x < right;
    }
}
