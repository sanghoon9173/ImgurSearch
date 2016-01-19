package com.example.sanghoonlee.imgursearch.Model.Imgur;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sanghoonlee on 2015-12-11.
 */
public class ImageData extends Image{
    public int ups;
    public int downs;
    public int points;
    public int score;

    public ImageData(String url, boolean isAlbum) {
        this.url = url;
        this.isAlbum = isAlbum;
    }

    @SerializedName("comment_count")
    public int commentCount;

    @SerializedName("images_count")
    public int imageCount;

    @SerializedName("is_album")
    public boolean isAlbum;

}
