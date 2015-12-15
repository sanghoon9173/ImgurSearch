package com.example.sanghoonlee.imgursearch.Controller.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
    private ImageFragment mImageFragment;

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
        mCurrentFragmentTag = ImageFragment.TAG;
        mImageFragment = ImageFragment.newInstance();
        mImageFragment.setImageData(data);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(R.id.main_container, mImageFragment, ImageFragment.TAG);
        transaction.addToBackStack(ImageFragment.TAG);
        transaction.commit();
    }

    public void popFragment() {
        mFragmentManager.popBackStack();
    }

    @Override
    public void onBackPressed() {
        switch(mCurrentFragmentTag) {
            case ImageFragment.TAG:
                popFragment();
                mCurrentFragmentTag = ImageSearchFragment.TAG;
                break;
            case ImageSearchFragment.TAG:
            default:
                super.onBackPressed();
                break;
        }
    }

}
