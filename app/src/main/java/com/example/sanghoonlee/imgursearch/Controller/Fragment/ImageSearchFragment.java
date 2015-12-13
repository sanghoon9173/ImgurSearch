package com.example.sanghoonlee.imgursearch.Controller.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.sanghoonlee.imgursearch.Controller.Adapter.ImageSearchResultAdapter;
import com.example.sanghoonlee.imgursearch.Controller.Adapter.SearchHistoryAdapter;
import com.example.sanghoonlee.imgursearch.Controller.ImgurClient;
import com.example.sanghoonlee.imgursearch.Controller.Listener.RecyclerItemClickListener;
import com.example.sanghoonlee.imgursearch.Model.Imgur.ImageData;
import com.example.sanghoonlee.imgursearch.R;
import com.example.sanghoonlee.imgursearch.Controller.PersistenceManager;
import com.example.sanghoonlee.imgursearch.Util.Util;

import java.util.ArrayList;

public class ImageSearchFragment extends Fragment {

    public static final String TAG = "ImageSearchFragment";

    private EditText        mSearchInput;
    private Button          mSearchButton;
    private RecyclerView    mRecyclerView;
    private ListView        mHistoryListView;

    private ImgurClient     mImgur;
    private View            mLayout;
    private ImageSearchResultAdapter mAdapter;
    private String          mCurrentSearchString;
    private OnImageItemSelectedListener mImageSelectedCallBack;
    private SearchHistoryAdapter mHistoryAdapter;
    private PersistenceManager mPersistenceManager;


    public ImageSearchFragment() {
        // Required empty public constructor
    }

    public static ImageSearchFragment newInstance() {
        ImageSearchFragment fragment = new ImageSearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.fragment_image_search, container, false);
        return mLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        initOp();
    }

    @Override
    public void onPause() {
        super.onPause();
        //stop loading the image
        mRecyclerView.setAdapter(null);
    }

    private void initView() {
        mSearchInput = (EditText)mLayout.findViewById(R.id.search_input);
        mSearchButton = (Button) mLayout.findViewById(R.id.search_button);
        mHistoryListView = (ListView) mLayout.findViewById(R.id.search_history);
        mRecyclerView = (RecyclerView) mLayout.findViewById(R.id.search_result);
    }

    private void initOp() {
        //init all the recycler view related operations
        initRecyclerViewOp();
        //createimgur client
        mImgur = new ImgurClient(getActivity(), mAdapter);
        initSearchAreaOp();
        //PersistenceManager for reading and writing search history
        mPersistenceManager = new PersistenceManager(getActivity().getApplicationContext());
        initSpinnerOp();
    }

    private void initSearchAreaOp() {
        //listen for buttom click for search
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch(v);
            }
        });

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
                mHistoryAdapter.refreshHistory(mPersistenceManager.getSearchHistory(queryString));
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

    private void initSpinnerOp(){
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
                mPersistenceManager.getSearchHistory(), mHistoryListView);
        mHistoryListView.setAdapter(mHistoryAdapter);
    }

    public void initRecyclerViewOp() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ImageSearchResultAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        //listen for scroll event to load more images
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //fetch more data when scrolling near the end
                int totalItemCount = mRecyclerView.getLayoutManager().getItemCount();
                int lastVisibleItem = ((GridLayoutManager) mRecyclerView.getLayoutManager())
                        .findLastVisibleItemPosition();
                if (!mImgur.isLoading && totalItemCount <= (lastVisibleItem + 10)) {
                    mImgur.searchImage(mCurrentSearchString);
                }
            }
        });

        //set on item click listener
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mImageSelectedCallBack.onImageSelected(mAdapter.getItemAt(position));
                    }
                })
        );
    }

    private void performSearch(View v) {
        Util.hideKeyboardIfOpen(v, getActivity());
        mCurrentSearchString = mSearchInput.getText().toString();
        mImgur.searchImage(mCurrentSearchString);
        mPersistenceManager.addSearchHistory(mCurrentSearchString);
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

}
