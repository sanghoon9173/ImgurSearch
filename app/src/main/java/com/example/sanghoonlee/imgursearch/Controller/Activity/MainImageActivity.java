package com.example.sanghoonlee.imgursearch.Controller.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.sanghoonlee.imgursearch.R;

public class MainImageActivity extends AppCompatActivity {
    public static final String IMAGE_URL_EXTRA = "IMAGEURL";

    private ImageView mMainImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_image);
        Intent intent = getIntent();
        String url = intent.getStringExtra(IMAGE_URL_EXTRA);
        mMainImage = (ImageView)findViewById(R.id.main_image);
        Glide.with(this).load(url).into(mMainImage);
    }

}
