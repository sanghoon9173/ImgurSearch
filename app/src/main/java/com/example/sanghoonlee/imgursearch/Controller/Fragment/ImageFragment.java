package com.example.sanghoonlee.imgursearch.Controller.Fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.sanghoonlee.imgursearch.Model.Imgur.ImageData;
import com.example.sanghoonlee.imgursearch.R;
import com.squareup.picasso.Picasso;


public class ImageFragment extends Fragment {
    public static final String TAG = "ImageFragment";
    public static final String SINGLE_IMAGE_TAG = "DETAIL_IMAGE";

    private ImageView   mMainImage;
    private View        mView;
    private ImageData   mImageData;
    private Picasso     mPicasso;

    public static ImageFragment newInstance() {
        ImageFragment fragment = new ImageFragment();
        return fragment;
    }

    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPicasso = Picasso.with(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_image, container, false);
        init();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPicasso.resumeTag(SINGLE_IMAGE_TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPicasso.pauseTag(SINGLE_IMAGE_TAG);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPicasso.cancelTag(SINGLE_IMAGE_TAG);
    }

    public void init() {
        mMainImage = (ImageView) mView.findViewById(R.id.main_image);
        Picasso.with(getActivity().getApplicationContext())
                .load(mImageData.url)
                .fit()
                .tag(SINGLE_IMAGE_TAG)
                .priority(Picasso.Priority.HIGH)
                .into(mMainImage);
    }

    public void setImageData(ImageData data){
        mImageData = data;
    }

}
