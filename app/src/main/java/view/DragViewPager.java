package view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Hexx on 2019/3/6 16:25
 * Desc：
 */
public class DragViewPager extends ViewPager {
    private int NORMAL = 1;
    private int MOVING = 2;
    private int mCurrentStatus = NORMAL;
    private static final int DRAG_MIN_Y = 100;


    private int mDownX;
    private int mDowny;

    private View mCurrentView;
    private View mBackgroundView;

    private Runnable mImageCloseCallback;

    public DragViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCurrentView(View currentView) {
        this.mCurrentView = currentView;
    }

    public void setBackgroundView(View backgroundView) {
        this.mBackgroundView = backgroundView;
    }

    public void setImageCloseCallback(Runnable imageCloseCallback) {
        this.mImageCloseCallback = imageCloseCallback;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) ev.getRawX();
                mDowny = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int difX = (int) (ev.getRawX() - mDownX);
                int difY = (int) (ev.getRawY() - mDowny);
                Log.v("drag_status", String.format("onInterceptTouchEvent difX is:%d  difY is:%d", difX, difY));
                if ((difY >= 0 && Math.abs(difY) >= DRAG_MIN_Y) || mCurrentStatus == MOVING) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                int finalX = (int) ev.getRawX();
                int finalY = (int) ev.getRawY();
                Log.v("drag_status", String.format("onInterceptTouchEvent finalX is:%d  finalY is:%d", finalX, finalY));
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) ev.getRawX();
                mDowny = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int difX = (int) (ev.getRawX() - mDownX);
                int difY = (int) (ev.getRawY() - mDowny);
                Log.v("drag_status", String.format("onTouchEvent difX is:%d  difY is:%d", difX, difY));
                if ((difY >= 0 && Math.abs(difY) >= DRAG_MIN_Y) || mCurrentStatus == MOVING) {
                    moveView((int) ev.getRawX(), (int) ev.getRawY());
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mCurrentStatus == NORMAL) {
                    return super.onTouchEvent(ev);
                }
                int finalX = (int) ev.getRawX();
                int finalY = (int) ev.getRawY();
                Log.v("drag_status", String.format("onTouchEvent finalX is:%d  finalY is:%d", finalX, finalY));
                if (finalY - mDowny >= 400) {
                    if (mImageCloseCallback != null) {
                        mImageCloseCallback.run();
                    }
                } else if (Math.abs(ev.getRawY() - mDowny) >= 0) {
                    resetView((int) ev.getRawX(), (int) ev.getRawY());
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void moveView(int movingX, int movingY) {
        if (mCurrentView != null && mBackgroundView != null) {
            mCurrentStatus = MOVING;
            mCurrentView.setTranslationY(movingY - mDowny);
            mCurrentView.setTranslationX(movingX - mDownX);
            mCurrentView.setScaleX(getScale(movingY));
            mCurrentView.setScaleY(getScale(movingY));
            mBackgroundView.setBackgroundColor(getColorWithAlpha(movingY));
            mCurrentView.invalidate();
        }
    }

    private float getScale(int movingY) {
        float percent = (movingY / (float) mDowny) - 1;
        percent = 1 - ((percent >= 1) ? 1f : (percent <= 0) ? 0f : percent);
        return percent <= 0.5f ? 0.5f : percent;
    }

    private int getColorWithAlpha(int movingY) {
        float percent = (movingY / (float) mDowny) - 1;
        percent = percent >= 1 ? 1f : percent <= 0 ? 0 : percent;
        return Color.argb((int) ((1 - percent) * 255), 0, 0, 0);
    }

    private void resetView(final int finalX, final int finalY) {
        if (mCurrentView != null) {
            ValueAnimator animator = ValueAnimator.ofInt(finalY, mDowny).setDuration(200);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCurrentStatus = NORMAL;
                }

            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int y = (int) animation.getAnimatedValue();
                    int x = (int) ((finalX - mDownX) * (((y - mDowny) / (float) (finalY - mDowny)))) + mDownX;
                    moveView(x, y);
                }
            });
            animator.start();
        }
    }
}
