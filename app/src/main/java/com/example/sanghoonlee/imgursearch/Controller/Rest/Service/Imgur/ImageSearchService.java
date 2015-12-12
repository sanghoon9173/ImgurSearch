package com.example.sanghoonlee.imgursearch.Controller.Rest.Service.Imgur;

import com.example.sanghoonlee.imgursearch.Controller.Rest.RestConfig;
import com.example.sanghoonlee.imgursearch.Model.Imgur.ImageData;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by sanghoonlee on 2015-12-10.
 */
public interface ImageSearchService {
    //sorted by upload date
    @Headers({"Authorization: Client-ID "+ RestConfig.IMGUR_CLIENT_ID})
    @GET("/3/gallery/search/{page}")
    Call<List<ImageData>> listDefaultImageData(@Path("page") int pagenumber, @Query("q") String search);

    //sorted by view count
    @Headers({"Authorization: Client-ID "+ RestConfig.IMGUR_CLIENT_ID})
    @GET("/3/gallery/search/top/{page}")
    Call<List<ImageData>> listTopImageData(@Path("page") int pagenumber, @Query("q") String search);

    //osrted by viral content
    @Headers({"Authorization: Client-ID "+ RestConfig.IMGUR_CLIENT_ID})
    @GET("/3/gallery/search/viral{page}")
    Call<List<ImageData>> listViralImageData(@Path("page") int pagenumber, @Query("q") String search);

}
