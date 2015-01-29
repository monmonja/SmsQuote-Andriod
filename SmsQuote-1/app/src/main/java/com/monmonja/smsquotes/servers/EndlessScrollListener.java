package com.monmonja.smsquotes.servers;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by almondjoseph on 8/6/14.
 */
public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {
    private final LinearLayoutManager mLayoutManager;
    private String mCursor;

    private ArrayList<String> mLoadedCursor;
    private boolean mIsLoading;

    public EndlessScrollListener(LinearLayoutManager layoutManager) {
        mLayoutManager = layoutManager;
        mLoadedCursor = new ArrayList<String>();
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int visibleItemCount = mLayoutManager.getChildCount();
        int totalItemCount = mLayoutManager.getItemCount();
        int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
        boolean loadMore = (visibleItemCount + pastVisibleItems) >= totalItemCount;

        if(totalItemCount > 0 && loadMore) {
            onLoadMore(mCursor);
        }
    }

    public abstract void onLoadMore(String cursor);

    public void reset() {
        mLoadedCursor = new ArrayList<String>();
        mCursor = null;
    }

    public void setCursor(String mCursor) {
        mIsLoading = false;
        this.mCursor = mCursor;
    }


    /**
     * Check if we have loaded the same cursor or we are currently loading
     */
    public boolean cursorLoadedOrLoading(String cursor) {
        boolean toReturn = false;
        if (mIsLoading) {
            toReturn = true;
        }
        if (mLoadedCursor.contains(cursor)) {
            toReturn = true;
        }

        if (!toReturn) {
            mIsLoading = true;
            mLoadedCursor.add(cursor);

            return false;
        } else {
            return true;
        }

    }

    public void setLoading(boolean isLoading) {
        this.mIsLoading = isLoading;
    }
}
