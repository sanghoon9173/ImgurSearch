package com.example.sanghoonlee.imgursearch.Controller;

import android.content.Context;
import android.util.Log;

import com.example.sanghoonlee.imgursearch.Controller.Adapter.ImageHistoryAdapter;
import com.example.sanghoonlee.imgursearch.Controller.Adapter.ImageSearchResultAdapter;
import com.example.sanghoonlee.imgursearch.Controller.Rest.RestConfig;
import com.example.sanghoonlee.imgursearch.Controller.Rest.Service.Imgur.ImageSearchService;
import com.example.sanghoonlee.imgursearch.Controller.Rest.ServiceGenerator;
import com.example.sanghoonlee.imgursearch.Model.Imgur.ImageData;
import com.example.sanghoonlee.imgursearch.Util.DialogFactory;
import com.example.sanghoonlee.imgursearch.Util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by sanghoonlee on 2015-12-11.
 */
public class ImgurClient {

    public  boolean mIsLoading;
    private boolean mIsNewSearch;
    private boolean mHasReachedLast;
    private int mPageNumber;
    private Context mContext;
    private ImageSearchResultAdapter mAdapter;
    private String mCurrentSearchString;
    private ImgurSearchable mImgurSearchable;
    private ImageHistoryAdapter mImageDB;
    private boolean mIsOnline;


    public ImgurClient(Context context, ImgurSearchable searchable) {
        mContext = context;
        mImgurSearchable = searchable;
        mIsLoading = false;
        mHasReachedLast = false;
        mPageNumber = -1;
        mImageDB = new ImageHistoryAdapter(mContext);
    }

    public void setAdapter(ImageSearchResultAdapter adapter) {
        mAdapter = adapter;
    }

    public void searchImage(String searchString) {
        mIsOnline = Util.isOnline(mContext);
        if (canSearch(searchString) && !mIsLoading) {
            onstartSearch();
            mCurrentSearchString = searchString;
            //fetch data from disk when offline
            if(!mIsOnline) {
                List<ImageData> url = new ArrayList<>();
                mImageDB.open();
                for(String imageUrl:mImageDB.getSearchHistory(mCurrentSearchString)){
                    url.add(new ImageData(imageUrl, false));
                }
                mImageDB.close();
                mIsLoading = false;
                mImgurSearchable.onFinishLoading();
                refreshAdapter(url);
            }
            //call api if online
            else {
                ServiceGenerator.createService(ImageSearchService.class, RestConfig.IMGUR_API)
                        .listDefaultImageData(++mPageNumber, "jpg", searchString, "small").enqueue(new Callback<List<ImageData>>() {
                    @Override
                    public void onResponse(Response<List<ImageData>> response, Retrofit retrofit) {
                        if (response.body() == null) {
                            return;
                        }
                        refreshAdapter(response.body());
                        if (response.body().size() == 0) {
                            mHasReachedLast = true;
                        }
                        mIsLoading = false;
                        mImgurSearchable.onFinishLoading();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        //TODO: Handle other errors
                        Log.i("restCall failure", t.toString());
                        DialogFactory.createDialog(mContext, DialogFactory.NETWORK_ERROR_DIALOG).show();
                        mIsLoading = false;
                        mImgurSearchable.onFinishLoading();
                    }
                });
            }
        }
    }

    private void onstartSearch() {
        mIsLoading = true;
        if(mIsNewSearch) {
            mAdapter.resetImageData();
        }
        mImgurSearchable.onLoading();
    }

    private boolean canSearch(String searchString) {
        mIsNewSearch= (mCurrentSearchString==null || mCurrentSearchString!=searchString);

        //if new search then reset the flags
        if(mIsNewSearch){
            mPageNumber=-1;
            mHasReachedLast = false;
            return true;
        }

        if(!mIsOnline) {
            return false;
        }

        //if the same search but can load more images then return true
        if(!mHasReachedLast) {
            return true;
        }
        //if the same search but already received all the images then return false
        else {
            return false;
        }
    }

    public void cleanUp(){
        mAdapter.cleanUp();
    }

    private void refreshAdapter(List<ImageData> imageDatas) {
        Iterator<ImageData> it = imageDatas.iterator();
        while(it.hasNext()) {
            ImageData data = it.next();
            if(data.isAlbum)
                it.remove();
        }
        mAdapter.addImageData(imageDatas, mCurrentSearchString);

    }
}
