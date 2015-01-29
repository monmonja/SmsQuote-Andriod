package com.monmonja.smsquotes.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.monmonja.smsquotes.R;
import com.monmonja.smsquotes.adapters.QuoteAdapter;
import com.monmonja.smsquotes.models.Quote;
import com.monmonja.smsquotes.servers.EndlessScrollListener;
import com.monmonja.smsquotes.servers.JsonRpcRequest;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {
    @InjectView(R.id.recycler_view) RecyclerView mRecyclerView;
    @InjectView(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.toolbar) Toolbar mToolbar;


    private OkHttpClient mClient = new OkHttpClient();
    private QuoteAdapter mQuoteAdapter;
    private EndlessScrollListener mEndlessScrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);


        setupRecyclerLayout();
    }


    private void setupRecyclerLayout () {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mEndlessScrollListener = new EndlessScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(String cursor) {
                listQuotes(cursor);
            }
        };
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                                              @Override
                                              public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                                  mEndlessScrollListener.onScrolled(recyclerView, dx, dy);
                                                  int topRowVerticalPosition = recyclerView.findViewHolderForPosition(0) == null ? -1: recyclerView.findViewHolderForPosition(0).getPosition();
                                                  mSwipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
                                              }
                                          });

        setupPullRefresh();
        listQuotes(null);
    }

    public void listQuotes(String cursor) {
        if (mEndlessScrollListener.cursorLoadedOrLoading(cursor)) {
            return;
        }
        mSwipeRefreshLayout.setRefreshing(true);

        // create params
        JSONObject jsonObject = new JSONObject();
        if (cursor != null) {
            try {
                jsonObject.put("cursor", cursor);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        new JsonRpcRequest<Quote[]>("list_quote", Quote[].class, jsonObject).run(mClient, new JsonRpcRequest.Success() {
            @Override
            public void success(Object clazz) {
                final Quote[] quotes = (Quote[]) clazz;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // create new adapter only when we dont have adapter yet
                        if (mQuoteAdapter == null) {
                            mQuoteAdapter = new QuoteAdapter(getActivity(), quotes);
                            mRecyclerView.setAdapter(mQuoteAdapter);
                        } else {
                            mQuoteAdapter.addItems(quotes);
                        }

                        // get the cursor to load
                        if (quotes.length > 0) {
                            mEndlessScrollListener.setCursor(quotes[quotes.length - 1].cursor);
                        }
                        mEndlessScrollListener.setLoading(false);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
        });
    }

    private Activity getActivity () {
        return this;
    }

    protected void setupPullRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mQuoteAdapter.reset();
                mEndlessScrollListener.reset();
                listQuotes(null);
            }
        });
        mSwipeRefreshLayout.setColorScheme(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3,
                R.color.refresh_progress_4);
    }



}
