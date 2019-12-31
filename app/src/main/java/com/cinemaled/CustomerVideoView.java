package com.cinemaled;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by lijingnan on 12/04/2017.
 */
public class CustomerVideoView extends VideoView {

    public CustomerVideoView(Context context) {
        super(context);
    }

    public CustomerVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomerVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 其实就是在这里做了一些处理。
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
}