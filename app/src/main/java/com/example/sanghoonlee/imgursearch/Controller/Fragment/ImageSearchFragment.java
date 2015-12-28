package com.example.sanghoonlee.imgursearch.Controller.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.sanghoonlee.imgursearch.Model.Imgur.ImageData;
import com.example.sanghoonlee.imgursearch.R;
import com.example.sanghoonlee.imgursearch.Util.Util;
import com.example.sanghoonlee.imgursearch.View.AutofitRecyclerView;
import com.example.sanghoonlee.imgursearch.View.MarginDecoration;

import org.w3c.dom.Text;

public class ImageSearchFragment extends Fragment implements ImgurSearchable{

    public static final String TAG = "ImageSearchFragment";

    private EditText        mSearchInput;
    private AutofitRecyclerView mRecyclerView;
    private ListView        mHistoryListView;
    private TextView        mNoResultTextView;

    private ImgurClient     mImgur;
    private View            mLayout;
    private ImageSearchResultAdapter mAdapter;
    private String          mCurrentSearchString;
    private OnImageItemSelectedListener mImageSelectedCallBack;
    private RecyclerItemClickListener mImageClickListener;
    private SearchHistoryAdapter mHistoryAdapter;
    private SearchHistoryDBAdapter mHistoryDBAdapter;


    public ImageSearchFragment() {
        // Required empty public constructor
    }

    public static ImageSearchFragment newInstance() {
        return new ImageSearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create db manager
        mHistoryDBAdapter = new SearchHistoryDBAdapter(getActivity());
        mHistoryDBAdapter.open();
        //create imgur client
        mImgur = new ImgurClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.fragment_image_search, container, false);
        initView();
        initOp();
        return mLayout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHistoryDBAdapter.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initView() {
        mSearchInput        = (EditText) mLayout.findViewById(R.id.search_input);
        mHistoryListView    = (ListView) mLayout.findViewById(R.id.search_history);
        mRecyclerView       = (AutofitRecyclerView) mLayout.findViewById(R.id.search_result);
        mNoResultTextView   = (TextView) mLayout.findViewById(R.id.no_result);
    }

    private void initOp() {
        initRecyclerViewOp();
        initHistoryListOp();
        initSearchAreaOp();
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
                if(hasFocus) {
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
        mHistoryAdapter = new SearchHistoryAdapter(getActivity(),
                mHistoryDBAdapter.getSearchHistory(), mHistoryListView);
        mHistoryListView.setAdapter(mHistoryAdapter);
    }

    public void initRecyclerViewOp() {
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new ImageSearchResultAdapter(getActivity(), this);
        mRecyclerView.setAdapter(mAdapter);
        mImgur.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MarginDecoration(getActivity()));
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

        //set on item click listener
        mImageClickListener =   new RecyclerItemClickListener(
                                    getActivity(),
                                    new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                mImageSelectedCallBack.onImageSelected(mAdapter.getItemAt(position));
                            }
                         });
        mRecyclerView.addOnItemTouchListener(mImageClickListener);

    }

    private void performSearch(View v) {
        Util.hideKeyboardIfOpen(v, getActivity());
        mCurrentSearchString = mSearchInput.getText().toString();
        mImgur.searchImage(mCurrentSearchString);
        mHistoryDBAdapter.addSearchHistory(mCurrentSearchString);
        mSearchInput.clearFocus();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mImageSelectedCallBack = (OnImageItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mImageSelectedCallBack = null;
    }

    public interface OnImageItemSelectedListener {
        void onImageSelected(ImageData data);
    }

    @Override
    public void onNoMoreResult() {
        //show toast when there is no more images to load
        Toast.makeText(getActivity(), R.string.imgur_no_more_images, Toast.LENGTH_LONG).show();
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

    }

    @Override
    public void onFinishLoading() {

    }
}
