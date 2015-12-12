package com.example.sanghoonlee.imgursearch.Controller.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sanghoonlee.imgursearch.Controller.Fragment.ImageFragment;
import com.example.sanghoonlee.imgursearch.Controller.Fragment.ImageSearchFragment;
import com.example.sanghoonlee.imgursearch.Model.Imgur.ImageData;
import com.example.sanghoonlee.imgursearch.R;

public class MainActivity extends AppCompatActivity implements
        ImageSearchFragment.OnImageItemSelectedListener {

    private ImageSearchFragment mImageSearchFragment;
    private FragmentManager mFragmentManager;
    private String mCurrentFragmentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init() {
        mCurrentFragmentTag =ImageSearchFragment.TAG;
        mImageSearchFragment = ImageSearchFragment.newInstance();
        mFragmentManager = getFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(R.id.main_container, mImageSearchFragment, ImageSearchFragment.TAG);
        transaction.addToBackStack(ImageSearchFragment.TAG);
        transaction.commit();
    }

    @Override
    protected void onResume(){
        super.onResume();

    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    public void onImageSelected(ImageData data) {
        Intent intent = new Intent(this, MainImageActivity.class);
        intent.putExtra(MainImageActivity.IMAGE_URL_EXTRA, data.url);
        startActivity(intent);
//        mCurrentFragmentTag = ImageFragment.TAG;
//        mImageFragment = ImageFragment.newInstance();
//        mImageFragment.setImageData(data);
//        FragmentTransaction transaction = mFragmentManager.beginTransaction();
//        transaction.add(R.id.main_container, mImageFragment, ImageFragment.TAG);
//        transaction.addToBackStack(ImageFragment.TAG);
//        transaction.commit();
    }

    public void popFragment() {
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
    }

    @Override
    public void onBackPressed() {
        switch(mCurrentFragmentTag) {
            case ImageFragment.TAG:
                popFragment();
                break;
            case ImageSearchFragment.TAG:
            default:
                super.onBackPressed();
                break;
        }
    }

}
