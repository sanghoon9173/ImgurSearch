package com.example.sanghoonlee.imgursearch.Controller;

import android.content.Context;
import android.util.Log;

import com.example.sanghoonlee.imgursearch.Controller.Adapter.ImageSearchResultAdapter;
import com.example.sanghoonlee.imgursearch.Controller.Rest.RestConfig;
import com.example.sanghoonlee.imgursearch.Controller.Rest.Service.Imgur.ImageSearchService;
import com.example.sanghoonlee.imgursearch.Controller.Rest.ServiceGenerator;
import com.example.sanghoonlee.imgursearch.Model.Imgur.ImageData;
import com.example.sanghoonlee.imgursearch.Util.DialogFactory;

import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by sanghoonlee on 2015-12-11.
 */
public class ImgurClient {

    public boolean isLoading;
    private boolean isNewSearch;
    private boolean hasReachedLast;
    private int pageNumber;
    private Context context;
    private ImageSearchResultAdapter adapter;
    private String currentSearchString;


    public ImgurClient(Context context, ImageSearchResultAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
        isLoading = false;
        hasReachedLast = false;
        pageNumber = -1;
    }

    public void searchImage(String searchString) {
        if (canSearch(searchString) && !isLoading) {
            isLoading = true;
            currentSearchString = searchString;
            ServiceGenerator.createService(ImageSearchService.class, RestConfig.IMGUR_API)
                    .listDefaultImageData(++pageNumber, searchString).enqueue(new Callback<List<ImageData>>() {
                @Override
                public void onResponse(Response<List<ImageData>> response, Retrofit retrofit) {
                    if(response.body()==null) {
                        return;
                    }
                    refreshAdapter(response.body());
                    if(response.body().size()==0) {
                        hasReachedLast=true;
                    }
                    isLoading = false;
                }

                @Override
                public void onFailure(Throwable t) {
                    //TODO: Handle other errors
                    Log.i("restCall failure", t.toString());
                    DialogFactory.createDialog(context, DialogFactory.NETWORK_ERROR_DIALOG).show();
                    isLoading = false;
                }
            });
        }
    }


    private boolean canSearch(String searchString) {
        isNewSearch= (currentSearchString==null || currentSearchString!=searchString);

        //if new search then reset the flags
        if(isNewSearch){
            pageNumber=-1;
            hasReachedLast = false;
            return true;
        }
        //if the same search but can load more images then return true
        else if(!hasReachedLast) {
            return true;
        }
        //if the same search but already received all the images then return false
        else {
            return false;
        }
    }

    private void refreshAdapter(List<ImageData> imageDatas) {
        Iterator<ImageData> it = imageDatas.iterator();
        while(it.hasNext()) {
            ImageData data = it.next();
            if(data.isAlbum)
                it.remove();
        }
        if(isNewSearch) {
            adapter.resetImageData(imageDatas);
        } else {
            adapter.addImageData(imageDatas);
        }
    }
}
