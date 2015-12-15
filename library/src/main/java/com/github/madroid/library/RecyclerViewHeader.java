package com.github.madroid.library;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * created by madroid at 2015-12-04
 */
public class RecyclerViewHeader extends ViewGroup {
    private static final String TAG = "RecyclerViewHeader";

    private RecyclerView mRecyclerView;
    private ViewGroup mHeaderView;
    private Context mContext;
    private ViewDragHelper mDragHelper;
    private Scroller mScroller;
    private int mTouchSlop;
    private View mView;

    public RecyclerViewHeader(Context context) {
        this(context, null);
    }

    public RecyclerViewHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        //setOrientation(VERTICAL);
        init();
    }

    public static RecyclerViewHeader fromXml(Context context, int ResLayout) {
        RecyclerViewHeader header = new RecyclerViewHeader(context);
        //View.inflate(context, ResLayout, header);
        LayoutInflater.from(context).inflate(ResLayout, header);
        return header;
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this, 1.0f, mDragCallback);
        mScroller = new Scroller(mContext);
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
                //isIntercept = mDragHelper.shouldInterceptTouchEvent(ev) ;

                isHeaderViewVisiable();
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = 0;
        int width = 0;
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

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int headerHeight = mHeaderView.getMeasuredHeight();

        mHeaderView.layout(l, t, r, b);
        mRecyclerView.layout(l, t + headerHeight, r, b);
    }

    private boolean isInRecyclerView(float downX, float downY) {
        //Log.i("madroid", "isInRecyclerView x :" + downX + ", y:" + downY) ;
        int left = mRecyclerView.getLeft();
        int top = mRecyclerView.getTop();
        int right = mRecyclerView.getRight();
        int bottom = mRecyclerView.getBottom();

        return (left <= downX && downX <= right && top <= downY && downY <= bottom);
    }

    private boolean isHeaderViewVisiable() {
        int bottom = mHeaderView.getBottom();
        Log.i(TAG, "header view bottom:" + bottom);
        return bottom <= 0;
    }


    @Override
    public void computeScroll() {
//       if (mDragHelper.continueSettling(true)) {
//           ViewCompat.postInvalidateOnAnimation(this);
//       }

        super.computeScroll();
        // 判断Scroller是否执行完毕
        if (mScroller.computeScrollOffset()) {
            ((View) getParent()).scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            // 通过重绘来不断调用computeScroll
            invalidate();//很重要
        }
    }

    int lastX, lastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX = x - lastX;
                int offsetY = y - lastY;
                ((View) getParent()).scrollBy(-offsetX, -offsetY);
                break;
            case MotionEvent.ACTION_UP:
                // 手指离开时，执行滑动过程
                View viewGroup = ((View) getParent());
                mScroller.startScroll(viewGroup.getScrollX(), viewGroup.getScrollY(),
                        -viewGroup.getScrollX(), -viewGroup.getScrollY());
                invalidate();//很重要
                break;
        }
        return true;
    }

    private ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            Log.i("madroid", "view:" + child.getId() + "; pointerId:" + pointerId);
            boolean isCaptureView;
            if (child instanceof RecyclerView) {
                mRecyclerView.getAdapter().notifyDataSetChanged();
                isCaptureView = true;
            } else {
                isCaptureView = true;
            }

            return true;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            Log.i(TAG, "clampViewPositionVertical child :" + child.getId() + ", top:"
                    + top + ", dy:" + dy);
            return top;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            Log.i(TAG, "clampViewPositionVertical child :" + child.getId() + ", left:"
                    + left + ", dx:" + dx);
            return super.clampViewPositionHorizontal(child, left, dx);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }
    };
}
