package com.samples.blackknight7.staticlistdemo;

/**
 * Created by nicolashe on 1/25/2016.
 */
public interface OnRefreshListener {

    /**
     * 下拉刷新
     */
    void onDownPullRefresh();

    /**
     * 上拉加载更多
     */
    void onLoadingMore();
}