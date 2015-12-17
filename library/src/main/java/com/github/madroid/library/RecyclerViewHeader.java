package com.github.madroid.library;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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
    }

    public void attachTo(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        ViewGroup parent = (ViewGroup) mRecyclerView.getParent();
        parent.removeView(recyclerView);
        this.addView(mRecyclerView);
        parent.addView(this);

        mHeaderView = (ViewGroup) this.getChildAt(0);
        //setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_blue_light));
    }

    int startY = 0;
    int startX = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        boolean isIntercept = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "[ -- onInterceptTouchEvent ACTION_DOWN -- ]");
                isIntercept = false;
                startX = (int) event.getX() ;
                startY = (int) event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "[--- onInterceptTouchEvent ACTION_MOVE -- ]");
                int dy = (int) event.getY() - startY;
                int dx = (int) event.getX() - startX ;
                if (isInRecyclerView(event.getX(), event.getY()) && mRecyclerView.getTop() > 0) {
                    isIntercept = true;
                    mDragHelper.captureChildView(mRecyclerView, 0);
                } else if (isInRecyclerView(event.getX(), event.getY()) && isScrollToTop() && dy > 0) {
                    isIntercept = true;
                    mDragHelper.captureChildView(mRecyclerView, 0);
                } else if (!isInRecyclerView(event.getX(), event.getY()) && Math.abs(dy) > Math.abs(dx)){
                    mDragHelper.captureChildView(mHeaderView, 0);
                    isIntercept = true;
                }else {
                    isIntercept = false ;
                }

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.i(TAG, "[ -- onInterceptTouchEvent ACTION_UP -- ]");
                mDragHelper.cancel();
                isIntercept = false;
                break;

            default:
                break;
        }

        return isIntercept || mDragHelper.shouldInterceptTouchEvent(event);
    }

    private boolean isScrollToTop() {
        boolean isTop = false;
        RecyclerView.LayoutManager manager = mRecyclerView.getLayoutManager();
        if (manager instanceof LinearLayoutManager) {
            isTop = ((LinearLayoutManager) manager).findFirstCompletelyVisibleItemPosition() == 0;
        }else if (manager instanceof StaggeredGridLayoutManager) {
            int spanCount = ((StaggeredGridLayoutManager) manager).getSpanCount() ;
            int into[] = new int[spanCount] ;
            int result[] = ((StaggeredGridLayoutManager) manager).findFirstCompletelyVisibleItemPositions(into) ;
            for (int i = 0; i < spanCount; i++) {
                if (i != result[i]) {
                    return false ;
                }else {
                    isTop = true ;
                }
            }
        }
        Log.i(TAG, "isScrollToTop : " + isTop) ;
        return isTop;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
            //Log.i(TAG, "tryCaptureView view:" + child.getId() + "; pointerId:" + pointerId);

            return true;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            //Log.i(TAG, "onViewCaptured view:" + capturedChild.getId() + "; pointerId:" + activePointerId);

        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            //Log.i(TAG, "clampViewPositionVertical child :" + child.getId() + ", top:" + top + ", dy:" + dy);
            int newTop;

            if (child == mHeaderView) {
                if (top >= 0) {
                    newTop = 0;
                } else {
                    newTop = top;
                }

            } else if (child == mRecyclerView) {
                if (top >= 0) {
                    if (top >= mHeaderView.getMeasuredHeight()) {
                        newTop = mHeaderView.getMeasuredHeight();
                    } else {
                        newTop = top;
                    }
                } else {
                    newTop = 0;
                }
            } else {
                newTop = top;
            }

            return newTop;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            Log.i(TAG, "onViewPositionChanged top:" + top + ", dy:" + dy);
            if (changedView == mHeaderView) {
                if (top > 0) {
                    mDragHeight = 0;
                } else {
                    mDragHeight = top;
                }
            } else if (changedView == mRecyclerView) {
                if (top > 0) {
                    mDragHeight = top - mHeaderView.getMeasuredHeight();
                } else {
                    mDragHeight = 0 - mHeaderView.getMeasuredHeight();
                }
            }
            requestLayout();
        }


        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            //mDragHelper.settleCapturedViewAt(0, 0);
            Log.i(TAG, "releasedChild : " + releasedChild.getId());
            if (releasedChild == mHeaderView) {
                mDragHelper.flingCapturedView(0, -mHeaderView.getMeasuredHeight(), 0, 0);
            } else if (releasedChild == mRecyclerView) {
                mDragHelper.flingCapturedView(0, 0, 0, mHeaderView.getMeasuredHeight());
            }
            ViewCompat.postInvalidateOnAnimation(RecyclerViewHeader.this);
        }

    };
}
