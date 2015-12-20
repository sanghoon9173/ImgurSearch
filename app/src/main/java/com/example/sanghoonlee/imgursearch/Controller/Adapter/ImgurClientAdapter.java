package com.example.sanghoonlee.imgursearch.Controller.Adapter;

import com.example.sanghoonlee.imgursearch.Model.Imgur.ImageData;

import java.util.List;

/**
 * Created by sanghoonlee on 2015-12-19.
 */
public interface ImgurClientAdapter {
    void resetImageData(List<ImageData> models);
    void addImageData(List<ImageData> models);
}
