package com.example.sanghoonlee.imgursearch.Controller.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.sanghoonlee.imgursearch.Model.Imgur.ImageData;
import com.example.sanghoonlee.imgursearch.R;

public class FullScreenImageActivity extends AppCompatActivity {
    public static final String IMAGE_URL_EXTRA = "IMAGEURL";

    public static Intent getIntent(Context context, ImageData image) {
        Intent intent = new Intent(context, FullScreenImageActivity.class);
        intent.putExtra(IMAGE_URL_EXTRA, image.url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        ImageView fullscreenView = (ImageView) findViewById(R.id.fullScreen_img_view);
        String imageData = getIntent().getStringExtra(IMAGE_URL_EXTRA);
        Glide.with(this)
                .load(imageData)
                .fitCenter()
                .into(fullscreenView);
    }

}
