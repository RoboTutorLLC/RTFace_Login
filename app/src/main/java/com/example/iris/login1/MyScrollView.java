package com.example.iris.login1;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Iris on 16/7/20.
 */

public class MyScrollView extends ScrollView implements View.OnClickListener {

    //invoked when user scrolls the pictures
    public interface CurrentImageChangeListener {
        void onCurrentImgChanged(int position, View viewIndicator);
    }

    //invoked when user tap on the pictures in gallery
    public interface OnItemClickListener {
        void onClick(View view, int pos);
    }

    private CurrentImageChangeListener mListener;
    private OnItemClickListener mOnClickListener;
    private static final String TAG = "MyHorizontalScrollView";
    private LinearLayout mContainer;     //LinearLayout in ListView

    private int mChildWidth;
    private int mChildHeight;
    private int mCurrentIndex;
    public int mFristIndex;
    private View mFirstView;
    private ScrollViewAdapter mAdapter;
    public int mCountOneScreen;
    public int mScreenHeight;
    private Map<View, Integer> mViewPos = new HashMap<View, Integer>();

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;
        System.out.println(mScreenHeight+"");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mContainer = (LinearLayout) getChildAt(0);
    }

    protected void loadNextImg() {
        if (mCurrentIndex == mAdapter.getCount() - 1) return;

        scrollTo(0, 0);
        mViewPos.remove(mContainer.getChildAt(0));
        mContainer.removeViewAt(0);

        View view = mAdapter.getView(++mCurrentIndex, null, mContainer);   //get the next picture
        view.setOnClickListener(this);
        mContainer.addView(view);
        mViewPos.put(view, mCurrentIndex);

        mFristIndex++;
        if (mListener != null) notifyCurrentImgChanged();

    }

    protected void loadPreImg() {
        //TODO: need to add icon of child to the screen! (currently only displays photo)
        //already the first img
        if (mFristIndex == 0)
            return;
        int index = mCurrentIndex - mCountOneScreen;
        if (index >= 0) {
//			mContainer = (LinearLayout) getChildAt(0);
            int oldViewPos = mContainer.getChildCount() - 1;
            mViewPos.remove(mContainer.getChildAt(oldViewPos));
            mContainer.removeViewAt(oldViewPos);

            View view = mAdapter.getView(index, null, mContainer);
            mViewPos.put(view, index);
            mContainer.addView(view, 0);
            view.setOnClickListener(this);
            scrollTo(0, mChildHeight);
            mCurrentIndex--;
            mFristIndex--;
            if (mListener != null)
                notifyCurrentImgChanged();
        }
    }

    public void notifyCurrentImgChanged() {
        System.out.println("sizeall"+mContainer.getChildCount());
        for (int i = 0; i < mContainer.getChildCount(); i++)
            mContainer.getChildAt(i).setBackgroundColor(Color.WHITE);

        mListener.onCurrentImgChanged(mFristIndex, mContainer.getChildAt(0));
    }

    public void initDatas(ScrollViewAdapter mAdapter, Boolean needConfirm) {
        this.mAdapter = mAdapter;
        mContainer = (LinearLayout) getChildAt(0);
        final View view = mAdapter.getCount() > 0 ? mAdapter.getView(0, null, mContainer) : null;
        if (view == null) {
            mCountOneScreen = 0;
            mChildHeight = 0;
            mChildWidth = 0;
        } else {
            mContainer.addView(view);
            if (mChildWidth == 0 && mChildHeight == 0) {
                int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                view.measure(w, h);
                mChildHeight = view.getMeasuredHeight();
                mChildWidth = view.getMeasuredWidth();
                Log.e(TAG, view.getMeasuredWidth() + "," + view.getMeasuredHeight());

                mCountOneScreen = (mScreenHeight / mChildHeight == 0) ? mScreenHeight / mChildHeight + 1 : mScreenHeight / mChildHeight + 2;
                Log.e(TAG, "mCountOneScreen = " + mCountOneScreen + " ,mChildWidth = " + mChildWidth);
            }
        }
        initFirstScreenChildren(mCountOneScreen, needConfirm);
    }

    private void initFirstScreenChildren(int mCountOneScreen, Boolean needConfirm) {
        mContainer = (LinearLayout) getChildAt(0);
        mContainer.removeAllViews();
        mViewPos.clear();

        for (int i = 0; i < mAdapter.accountNumber; i++) {
            View view = mAdapter.getView(i, null, mContainer);
            view.setOnClickListener(this);
            mContainer.addView(view);
            mViewPos.put(view, i);
            mCurrentIndex = i;
        }

        if (mListener != null)
            if (mCountOneScreen > 0 || needConfirm)
                notifyCurrentImgChanged();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_MOVE:
//			Log.e(TAG, getScrollX() + "");

                int scrollY = getScrollY();

                // only do this when there are profiles there, else will crash
                if(mContainer.getChildCount() > 0) {
                    if (scrollY >= mChildHeight) {
                        loadNextImg();
                    }
                    if (scrollY == 0) {
                        loadPreImg();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        if (mOnClickListener != null) {
            for (int i = 0; i < mContainer.getChildCount(); i++)
                mContainer.getChildAt(i).setBackgroundColor(Color.WHITE);

            mOnClickListener.onClick(v, mViewPos.get(v));
            mAdapter.accountChosen = mViewPos.get(v);
        }
    }

    public void setOnItemClickListener(OnItemClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnClickListener;
    }

    public void setCurrentImageChangeListener(CurrentImageChangeListener mListener) {
        this.mListener = mListener;
    }

    public void shrinkPicture(ScrollViewAdapter mAdapter, int surfaceWidth, int surfaceHeight) {
        View view = mContainer.getChildAt(0);
        view.bringToFront();
        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        mChildHeight = view.getMeasuredHeight();
        mChildWidth = view.getMeasuredWidth();

        ObjectAnimator scaleX, scaleY, animX, animY;

        scaleX = ObjectAnimator.ofFloat(view, "scaleX", surfaceWidth / mChildWidth, 1);
        scaleY = ObjectAnimator.ofFloat(view, "scaleY", surfaceHeight / mChildHeight, 1);
        animX = ObjectAnimator.ofFloat(view, "translationX", view.getTranslationX() + surfaceWidth, view.getTranslationX());
        animY = ObjectAnimator.ofFloat(view, "translationY", view.getTranslationY() + surfaceHeight, view.getTranslationY());

        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(scaleX, scaleY, animX, animY);
        animSet.setDuration(5000);
        animSet.start();
    }

    public boolean exceedScreen(int numOfPictures) {
        return (numOfPictures - 1) * mChildHeight > mScreenHeight? true : false;
    }

    public void clearAllBackground() {
        for (int i = 0; i < mContainer.getChildCount(); i++)
            mContainer.getChildAt(i).setBackgroundColor(Color.WHITE);
    }

    public void emphasizeNewPicture() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        RelativeLayout rl = (RelativeLayout)mContainer.getChildAt(0);
        ImageView iv = (ImageView)rl.getChildAt(0);
        iv.setLayoutParams(lp);
    }
}
