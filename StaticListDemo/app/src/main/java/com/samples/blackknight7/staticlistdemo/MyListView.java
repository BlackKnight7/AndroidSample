package com.samples.blackknight7.staticlistdemo;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * Created by nicolashe on 1/25/2016.
 */
public class MyListView extends ListView implements AbsListView.OnScrollListener {
    private static final String TAG = "RefreshListView";
    private int firstVisibleItemPosition; // 屏幕显示在第一个的item的索引
    private int downY; // 按下时y轴的偏移量
    private int headerViewHeight; // 头布局的高度
    private View headerView; // 头布局的对象

    private final int DOWN_PULL_REFRESH = 0; // 下拉刷新状态
    private final int RELEASE_REFRESH = 1; // 松开刷新
    private final int REFRESHING = 2; // 正在刷新中
    private int currentState = DOWN_PULL_REFRESH; // 头布局的状态: 默认为下拉刷新状态

    private Animation upAnimation; // 向上旋转的动画
    private Animation downAnimation; // 向下旋转的动画
    private AnimationSet loadingAnimation;

    //    private ImageView ivArrow; // 头布局的剪头
//    private ProgressBar mProgressBar; // 头布局的进度条
    private View loadingView;

    private TextView tvState; // 头布局的状态
    private TextView tvLastUpdateTime; // 头布局的最后更新时间

    private OnRefreshListener mOnRefershListener;
    private boolean isScrollToBottom; // 是否滑动到底部
    private View footerView; // 脚布局的对象
    private int footerViewHeight; // 脚布局的高度
    private boolean isLoadingMore = false; // 是否正在加载更多中

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
        initFooterView();
        this.setOnScrollListener(this);
    }

    /**
     * 初始化脚布局
     */
    private void initFooterView() {
        footerView = View.inflate(getContext(), R.layout.listview_footer, null);
        footerView.measure(0, 0);
        footerViewHeight = footerView.getMeasuredHeight();
        footerView.setPadding(0, -footerViewHeight, 0, 0);
        this.addFooterView(footerView);
    }


    /**
     * 初始化头布局
     */
    private void initHeaderView() {
        headerView = View.inflate(getContext(), R.layout.listview_header, null);
//        ivArrow = (ImageView) headerView
//                .findViewById(R.id.iv_listview_header_arrow);
//        mProgressBar = (ProgressBar) headerView
//                .findViewById(R.id.pb_listview_header);
        loadingView = headerView.findViewById(R.id.loading_view);

        tvState = (TextView) headerView
                .findViewById(R.id.tv_listview_header_state);
        tvLastUpdateTime = (TextView) headerView
                .findViewById(R.id.tv_listview_header_last_update_time);

        // 设置最后刷新时间
        tvLastUpdateTime.setText("最后刷新时间: " + getLastUpdateTime());

        headerView.measure(0, 0); // 系统会帮我们测量出headerView的高度
        headerViewHeight = headerView.getMeasuredHeight();
        headerView.setPadding(0, -headerViewHeight, 0, 0);
        this.addHeaderView(headerView); // 向ListView的顶部添加一个view对象
        initAnimation();
    }

    /**
     * 获得系统的最新时间
     *
     * @return
     */
    private String getLastUpdateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(System.currentTimeMillis());
    }


    private void initAnimation() {
        upAnimation = new RotateAnimation(0f, -180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        upAnimation.setDuration(500);
        upAnimation.setFillAfter(true);

        downAnimation = new RotateAnimation(-180f, -360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        downAnimation.setDuration(500);
        downAnimation.setFillAfter(true);

        loadingAnimation = new AnimationSet(true);

        ScaleAnimation loadingAnimation1 = new ScaleAnimation(0.5f, 1.0f, 1.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        loadingAnimation1.setDuration(500);

        ScaleAnimation loadingAnimation2 = new ScaleAnimation(1.0f, 0.5f, 1.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        loadingAnimation2.setDuration(500);
        loadingAnimation2.setStartOffset(500);

        loadingAnimation.addAnimation(loadingAnimation1);
        loadingAnimation.addAnimation(loadingAnimation2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) ev.getY();
                // 移动中的y - 按下的y = 间距.
                int diff = (moveY - downY) / 2;
                // -头布局的高度 + 间距 = paddingTop
                int paddingTop = -headerViewHeight + diff;
                // 如果: -头布局的高度 > paddingTop的值 执行super.onTouchEvent(ev);
                if (firstVisibleItemPosition == 0 && -headerViewHeight < paddingTop) {
                    if (paddingTop > 0 && currentState == DOWN_PULL_REFRESH) { // 完全显示了.
                        Log.i(TAG, "松开刷新");
                        currentState = RELEASE_REFRESH;
                        refreshHeaderView();
                    } else if (paddingTop < 0 && currentState == RELEASE_REFRESH) { // 没有显示完全
                        Log.i(TAG, "下拉刷新");
                        currentState = DOWN_PULL_REFRESH;
                        refreshHeaderView();
                    }
                    // 下拉头布局
                    headerView.setPadding(0, paddingTop, 0, 0);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                // 判断当前的状态是松开刷新还是下拉刷新
                if (currentState == RELEASE_REFRESH) {
                    Log.i(TAG, "刷新数据.");
                    // 把头布局设置为完全显示状态
                    headerView.setPadding(0, 0, 0, 0);
                    // 进入到正在刷新中状态
                    currentState = REFRESHING;
                    refreshHeaderView();

                    if (mOnRefershListener != null) {
                        mOnRefershListener.onDownPullRefresh(); // 调用使用者的监听方法
                    }
                } else if (currentState == DOWN_PULL_REFRESH) {
                    // 隐藏头布局
                    headerView.setPadding(0, -headerViewHeight, 0, 0);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 根据currentState刷新头布局的状态
     */
    private void refreshHeaderView() {
        switch (currentState) {
            case DOWN_PULL_REFRESH: // 下拉刷新状态
                tvState.setText("下拉刷新");
//                ivArrow.startAnimation(downAnimation); // 执行向下旋转
                break;
            case RELEASE_REFRESH: // 松开刷新状态
                tvState.setText("松开刷新");
//                ivArrow.startAnimation(upAnimation); // 执行向上旋转
                break;
            case REFRESHING: // 正在刷新中状态
//                ivArrow.clearAnimation();
//                ivArrow.setVisibility(View.GONE);
//                mProgressBar.setVisibility(View.VISIBLE);
                loadingView.startAnimation(loadingAnimation);
                tvState.setText("正在刷新中...");
                break;
            default:
                break;
        }
    }


    /**
     * 当滚动状态改变时回调
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {
            // 判断当前是否已经到了底部
            if (isScrollToBottom && !isLoadingMore) {
                isLoadingMore = true;
                // 当前到底部
                Log.i(TAG, "加载更多数据");
                footerView.setPadding(0, 0, 0, 0);
                this.setSelection(this.getCount());

                if (mOnRefershListener != null) {
                    mOnRefershListener.onLoadingMore();
                }
            }
        }

        if (scrollState == SCROLL_STATE_IDLE) {
            if (!isScrollToBottom && !isLoadingMore) {
                // 滑动停止时触发
                int first = getFirstVisiblePosition();
                int last = getLastVisiblePosition();
                Log.i("Item first:", String.valueOf(first));
                Log.i("Item last：", String.valueOf(last));

                for(int index = first; index<last; index++){
                    int childIndex = index - first;
                    View visibleView = getChildAt(childIndex);
                    refreshCellDynamicStatus(visibleView);
                }
            }
        }
    }

    private void refreshCellDynamicStatus(View lyricCell){
        View view = lyricCell.findViewById(R.id.create_date);

        TranslateAnimation animation = new TranslateAnimation(view.getWidth(), 0, 0, 0);
        animation.setStartOffset(1000);
        animation.setDuration(400);

        view.startAnimation(animation);

    }

    /**
     * 当滚动时调用
     *
     * @param firstVisibleItem 当前屏幕显示在顶部的item的position
     * @param visibleItemCount 当前屏幕显示了多少个条目的总数
     * @param totalItemCount   ListView的总条目的总数
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        firstVisibleItemPosition = firstVisibleItem;

        if (getLastVisiblePosition() == (totalItemCount - 1)) {
            isScrollToBottom = true;
        } else {
            isScrollToBottom = false;
        }
    }

    /**
     * 设置刷新监听事件
     *
     * @param listener
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefershListener = listener;
    }

    /**
     * 隐藏头布局
     */
    public void hideHeaderView() {
        headerView.setPadding(0, -headerViewHeight, 0, 0);
//        ivArrow.setVisibility(View.VISIBLE);
//        mProgressBar.setVisibility(View.GONE);
        tvState.setText("下拉刷新");
        tvLastUpdateTime.setText("最后刷新时间: " + getLastUpdateTime());
        currentState = DOWN_PULL_REFRESH;
    }

    /**
     * 隐藏脚布局
     */
    public void hideFooterView() {
        footerView.setPadding(0, -footerViewHeight, 0, 0);
        isLoadingMore = false;
    }
}
