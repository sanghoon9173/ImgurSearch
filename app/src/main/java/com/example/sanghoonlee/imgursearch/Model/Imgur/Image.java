package com.example.sanghoonlee.imgursearch.Model.Imgur;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sanghoonlee on 2015-12-10.
 */
public class Image {
    public String id;
    public String title;
    public String description;
    public String type;
    public int width;
    public int height;
    public int size;
    public int bandwidth;

    @SerializedName("datetime")
    public int uploadTime;

    @SerializedName("animated")
    public boolean isAnimated;

    @SerializedName("link")
    public String url;

    @SerializedName("views")
    public int viewCount;

}
