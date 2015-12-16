package com.github.madroid.library;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * created by madroid at 2015-12-04
 */
public class RecyclerViewHeader extends ViewGroup {
    private static final String TAG = "RecyclerViewHeader";

    private RecyclerView mRecyclerView;
    private ViewGroup mHeaderView;
    private Context mContext;
    private ViewDragHelper mDragHelper;
    private int mTouchSlop;
    private int mDragHeight;

    public RecyclerViewHeader(Context context) {
        this(context, null);
    }

    public RecyclerViewHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public static RecyclerViewHeader fromXml(Context context, int ResLayout) {
        RecyclerViewHeader header = new RecyclerViewHeader(context);
        View.inflate(context, ResLayout, header);
        return header;
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this, 1.0f, mDragCallback);
        mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
    }

    public void attachTo(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        ViewGroup parent = (ViewGroup) mRecyclerView.getParent();
        parent.removeView(recyclerView);
        this.addView(mRecyclerView);
        parent.addView(this);

        mHeaderView = (ViewGroup) this.getChildAt(0);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean isIntercept = false;
        Log.i(TAG, "is recycler " + isInRecyclerView(ev.getX(), ev.getY()));

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isIntercept = false;
                break;

            case MotionEvent.ACTION_MOVE:
                isIntercept = mDragHelper.shouldInterceptTouchEvent(ev);

                break;

            case MotionEvent.ACTION_UP:
                isIntercept = false;
                break;

            default:
                break;
        }

        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean onTouch;
        mDragHelper.processTouchEvent(event);
        onTouch = true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:

                break;

            case MotionEvent.ACTION_UP:
                break;

            default:
                break;
        }
        return onTouch;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG, "onMeasure");
        int height = getPaddingTop() + getPaddingBottom();
        int width = getPaddingLeft() + getPaddingRight();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            //测量子控件
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            //获得子控件的高度和宽度
            int childHeight = child.getMeasuredHeight();
            int childWidth = child.getMeasuredWidth();
            //得到最大宽度，并且累加高度
            height += childHeight;
            width = Math.max(childWidth, width);
        }
        height = View.resolveSize(height, heightMeasureSpec);
        width = View.resolveSize(width, widthMeasureSpec);

        setMeasuredDimension(width, height);
    }


    /**
     * final 标识符 ， 不能被重载 ， 参数为每个视图位于父视图的坐标轴
     *
     * @param changed
     * @param l       Left position, relative to parent
     * @param t       Top position, relative to parent
     * @param r       Right position, relative to parent
     * @param b       Bottom position, relative to parent
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(TAG, "onLayout");
        int headerHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.layout(l, t + mDragHeight, r, b);
        mRecyclerView.layout(l, t + headerHeight + mDragHeight, r, b);
    }

    private boolean isInRecyclerView(float downX, float downY) {
        int left = mRecyclerView.getLeft();
        int top = mRecyclerView.getTop();
        int right = mRecyclerView.getRight();
        int bottom = mRecyclerView.getBottom();

        return (left <= downX && downX <= right && top <= downY && downY <= bottom);
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }

    }

    private ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            Log.i("madroid", "view:" + child.getId() + "; pointerId:" + pointerId);

            return true;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            Log.i(TAG, "clampViewPositionVertical child :" + child.getId() + ", top:" + top + ", dy:" + dy);
            int newTop;

            if (child == mHeaderView) {
                if (top >= 0) {
                    newTop = 0;
                } else {
                    newTop = top;
                }

            } else {
                newTop = 0;
            }

            return newTop;
        }


        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            //mDragHelper.settleCapturedViewAt(0, 0);
            mDragHelper.flingCapturedView(0, - mHeaderView.getMeasuredHeight(), 0, 0);
            postInvalidate();
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            Log.i(TAG, "onViewPositionChanged top:" + top + ", dy:" + dy);
            //mRecyclerView.setTop(mHeaderView.getMeasuredHeight() + top);
            if (changedView == mHeaderView) {
                if (top > 0) {
                    mDragHeight = 0 ;
                }else {
                    mDragHeight = top ;
                }
            }
            //mDragHeight = top;
            mRecyclerView.requestLayout();
        }
    };
}
