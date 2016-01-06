package com.example.sanghoonlee.imgursearch.Controller.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanghoonlee.imgursearch.Controller.Adapter.ImageSearchResultAdapter;
import com.example.sanghoonlee.imgursearch.Controller.Adapter.SearchHistoryAdapter;
import com.example.sanghoonlee.imgursearch.Controller.ImgurClient;
import com.example.sanghoonlee.imgursearch.Controller.ImgurSearchable;
import com.example.sanghoonlee.imgursearch.Controller.Listener.RecyclerItemClickListener;
import com.example.sanghoonlee.imgursearch.Controller.Storage.SearchHistoryDBAdapter;
import com.example.sanghoonlee.imgursearch.R;
import com.example.sanghoonlee.imgursearch.Util.Util;
import com.example.sanghoonlee.imgursearch.View.AutofitRecyclerView;
import com.example.sanghoonlee.imgursearch.View.MarginDecoration;

public class ImageSearchActivity extends AppCompatActivity implements ImgurSearchable {

    public static final String TAG = "ImageSearchActivity";
    private static final String STATE_SEARCH_STRING = "search_string";

    private EditText mSearchInput;
    private AutofitRecyclerView mRecyclerView;
    private ListView mHistoryListView;
    private TextView mNoResultTextView;

    private ImgurClient mImgur;
    private ImageSearchResultAdapter mAdapter;
    private String mCurrentSearchString;
    private SearchHistoryAdapter mHistoryAdapter;
    private SearchHistoryDBAdapter mHistoryDBAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);

        //create db manager
        mHistoryDBAdapter = new SearchHistoryDBAdapter(this);
        mHistoryDBAdapter.open();
        //create imgur client
        mImgur = new ImgurClient(this, this);
        initView();
        initOp();
        if (savedInstanceState != null) {
            String savedSearchString = savedInstanceState.getString(STATE_SEARCH_STRING);
            if (!TextUtils.isEmpty(savedSearchString)) {
                mSearchInput.setText(savedSearchString);
                mSearchInput.setSelection(savedSearchString.length());
                performSearch(mSearchInput);
            }
        }
    }

    private void initView() {
        mSearchInput        = (EditText) findViewById(R.id.search_input);
        mHistoryListView    = (ListView) findViewById(R.id.search_history);
        mRecyclerView       = (AutofitRecyclerView) findViewById(R.id.search_result);
        mNoResultTextView   = (TextView) findViewById(R.id.no_result);
    }

    private void initOp() {
        initRecyclerViewOp();
        initHistoryListOp();
        initSearchAreaOp();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHistoryDBAdapter.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(mCurrentSearchString)) {
            outState.putString(STATE_SEARCH_STRING, mCurrentSearchString);
        }
    }


    private void initSearchAreaOp() {
        //add text watcher for filtering spinner
        mSearchInput.addTextChangedListener(new TextWatcher() {
            private String queryString;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                queryString = s.toString();
                mHistoryAdapter.refreshHistory(mHistoryDBAdapter.getSearchHistory(queryString));
            }
        });

        //add action listener for search soft key press
        mSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(v);
                    return true;
                }
                return false;
            }
        });

        mSearchInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mHistoryListView.setVisibility(View.VISIBLE);
                } else {
                    mHistoryListView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initHistoryListOp(){
        //set item selected event for search history
        mHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //set the currently selected search term to the search input field
                String selectedString = mHistoryAdapter.getItem(position).toString();
                mSearchInput.setText(selectedString);
                mSearchInput.setSelection(selectedString.length());
            }
        });
        mHistoryAdapter = new SearchHistoryAdapter(this, mHistoryDBAdapter.getSearchHistory(),
                                                                            mHistoryListView);
        mHistoryListView.setAdapter(mHistoryAdapter);
    }

    public void initRecyclerViewOp() {
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new ImageSearchResultAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
        mImgur.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MarginDecoration(this));
        //listen for scroll event to load more images
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //fetch more data when scrolling near the end
                int totalItemCount = mRecyclerView.getLayoutManager().getItemCount();
                int lastVisibleItem = ((GridLayoutManager) mRecyclerView.getLayoutManager())
                        .findLastVisibleItemPosition();
                int firstVisibleItem = ((GridLayoutManager) mRecyclerView.getLayoutManager())
                        .findFirstVisibleItemPosition();
                if (!mImgur.mIsLoading && totalItemCount <= (lastVisibleItem + 1) &&
                        firstVisibleItem!=0) {
                    mImgur.searchImage(mCurrentSearchString);
                }
            }
        });

        //adjust progress bar span
        ((GridLayoutManager) mRecyclerView.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int maxSpan = ((GridLayoutManager) mRecyclerView.getLayoutManager()).getSpanCount();
                switch (mAdapter.getItemViewType(position)) {
                    case ImageSearchResultAdapter.VIEW_TYPE_ITEM:
                        return 1;
                    case ImageSearchResultAdapter.VIEW_TYPE_PROGRESSBAR:
                        return maxSpan;
                    default:
                        return -1;
                }
            }
        });

        //set on item click listener
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = FullScreenImageActivity.getIntent(
                                ImageSearchActivity.this, mAdapter.getItemAt(position));
                        startActivity(intent);
                    }
                }));

    }

    private void performSearch(View v) {
        Util.hideKeyboardIfOpen(v, this);
        mCurrentSearchString = mSearchInput.getText().toString();
        mImgur.searchImage(mCurrentSearchString);
        mHistoryDBAdapter.addSearchHistory(mCurrentSearchString);
        mSearchInput.clearFocus();
    }

    @Override
    public void onNoMoreResult() {
        //show toast when there is no more images to load
        Toast.makeText(this, R.string.imgur_no_more_images, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResultFound() {
        mNoResultTextView.setVisibility(View.GONE);
    }

    @Override
    public void onNoResultFound() {
        mNoResultTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoading() {
        mAdapter.enableFooter(true);
    }

    @Override
    public void onFinishLoading() {
        mAdapter.enableFooter(false);
    }

}
