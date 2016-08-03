package com.example.iris.login1;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Iris on 16/7/20.
 */

public class MyScrollView extends ScrollView implements View.OnClickListener {

    public interface CurrentImageChangeListener      //图片滚动时的回调接口
    {
        void onCurrentImgChanged(int position, View viewIndicator);
    }

    public interface OnItemClickListener          //条目点击时的回调
    {
        void onClick(View view, int pos);
    }

    private CurrentImageChangeListener mListener;
    private OnItemClickListener mOnClickListener;
    private static final String TAG = "MyHorizontalScrollView";
    private LinearLayout mContainer;     //ListView中的LinearLayout

    private int mChildWidth;
    private int mChildHeight;
    private int mCurrentIndex;
    public int mFristIndex;
    private View mFirstView;
    private ScrollViewAdapter mAdapter;
    private int mCountOneScreen;
    private int mScreenHeight;
    private Map<View, Integer> mViewPos = new HashMap<View, Integer>();

    public MyScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        // 获得屏幕高度
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mContainer = (LinearLayout) getChildAt(0);
    }

    protected void loadNextImg()      //加载下一张图片
    {
        // 数组边界值计算
        if (mCurrentIndex == mAdapter.getCount() - 1)
        {
            return;
        }
        //移除第一张图片，且将水平滚动位置置0
        scrollTo(0, 0);
        mViewPos.remove(mContainer.getChildAt(0));
        mContainer.removeViewAt(0);

        //获取下一张图片，并且设置onclick事件，且加入容器中
        View view = mAdapter.getView(++mCurrentIndex, null, mContainer);
        view.setOnClickListener(this);
        mContainer.addView(view);
        mViewPos.put(view, mCurrentIndex);

        //当前第一张图片小标
        mFristIndex++;
        //如果设置了滚动监听则触发
        if (mListener != null)
        {
            notifyCurrentImgChanged();
        }
    }

    protected void loadPreImg()    //加载前一张图片
    {
        //如果当前已经是第一张，则返回
        if (mFristIndex == 0)
            return;
        //获得当前应该显示为第一张图片的下标
        int index = mCurrentIndex - mCountOneScreen;
        if (index >= 0)
        {
//			mContainer = (LinearLayout) getChildAt(0);
            //移除最后一张
            int oldViewPos = mContainer.getChildCount() - 1;
            mViewPos.remove(mContainer.getChildAt(oldViewPos));
            mContainer.removeViewAt(oldViewPos);

            //将此View放入第一个位置
            View view = mAdapter.getView(index, null, mContainer);
            mViewPos.put(view, index);
            mContainer.addView(view, 0);
            view.setOnClickListener(this);
            //水平滚动位置向左移动view的宽度个像素
            scrollTo(0, mChildHeight);
            //当前位置--，当前第一个显示的下标--
            mCurrentIndex--;
            mFristIndex--;
            //回调
            if (mListener != null)
            {
                notifyCurrentImgChanged();
            }
        }
    }

    public void notifyCurrentImgChanged()     //滚动时的回调
    {
        //先清除所有的背景色，点击时会设置为蓝色
        System.out.println("sizeall"+mContainer.getChildCount());
        for (int i = 0; i < mContainer.getChildCount(); i++)
        {
            mContainer.getChildAt(i).setBackgroundColor(Color.WHITE);
        }
        mListener.onCurrentImgChanged(mFristIndex, mContainer.getChildAt(0));
    }

    public void initDatas(ScrollViewAdapter mAdapter)    //初始化数据，设置数据适配器
    {
        this.mAdapter = mAdapter;
        mContainer = (LinearLayout) getChildAt(0);
        // 获得适配器中第一个View
        final View view = mAdapter.getView(0, null, mContainer);
        if(view==null)
            System.out.println("view=null");
        mContainer.addView(view);

        // 强制计算当前View的宽和高
        if (mChildWidth == 0 && mChildHeight == 0)
        {
            int w = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
            int h = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
            view.measure(w, h);
            mChildHeight = view.getMeasuredHeight();
            mChildWidth = view.getMeasuredWidth();
            Log.e(TAG, view.getMeasuredWidth() + "," + view.getMeasuredHeight());
            mChildHeight = view.getMeasuredHeight();
            // 计算每次加载多少个View
            mCountOneScreen = (mScreenHeight / mChildHeight == 0)?mScreenHeight / mChildHeight+1:mScreenHeight / mChildHeight+2;

            Log.e(TAG, "mCountOneScreen = " + mCountOneScreen
                    + " ,mChildWidth = " + mChildWidth);
        }
        //初始化第一屏幕的元素
        initFirstScreenChildren(mCountOneScreen);
    }

    public void initFirstScreenChildren(int mCountOneScreen)  //加载第一屏的View
    {
        mContainer = (LinearLayout) getChildAt(0);
        mContainer.removeAllViews();
        mViewPos.clear();

        for (int i = 0; i < mCountOneScreen; i++)
        {
            if(i >= mAdapter.accountNumber)
                break;
            View view = mAdapter.getView(i, null, mContainer);
            view.setOnClickListener(this);
            mContainer.addView(view);
            mViewPos.put(view, i);
            mCurrentIndex = i;
        }
        if (mListener != null)
        {
            notifyCurrentImgChanged();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_MOVE:
//			Log.e(TAG, getScrollX() + "");

                int scrollY = getScrollY();
                // 如果当前scrollX为view的宽度，加载下一张，移除第一张
                if (scrollY >= mChildHeight)
                {
                    loadNextImg();
                }
                // 如果当前scrollX = 0， 往前设置一张，移除最后一张
                if (scrollY == 0)
                {
                    loadPreImg();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }
    @Override
    public void onClick(View v)
    {
        if (mOnClickListener != null)
        {
            for (int i = 0; i < mContainer.getChildCount(); i++)
            {
                mContainer.getChildAt(i).setBackgroundColor(Color.WHITE);
            }
            mOnClickListener.onClick(v, mViewPos.get(v));
            mAdapter.accountChosen = mViewPos.get(v);
        }
    }

    public void setOnItemClickListener(OnItemClickListener mOnClickListener)
    {
        this.mOnClickListener = mOnClickListener;
    }

    public void setCurrentImageChangeListener(CurrentImageChangeListener mListener)
    {
        this.mListener = mListener;
    }
}
