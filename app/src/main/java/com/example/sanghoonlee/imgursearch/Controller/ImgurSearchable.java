package com.example.sanghoonlee.imgursearch.Controller;

/**
 * Created by sanghoonlee on 2015-12-28.
 * This interface can be used to do some UI work for activities or fragments that uses ImgurClient
 */
public interface ImgurSearchable {
    void onNoResultFound();
    void onResultFound();
    void onNoMoreResult();
    void onLoading();
    void onFinishLoading();
}
