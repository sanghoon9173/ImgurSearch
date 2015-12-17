package com.example.sanghoonlee.imgursearch.Controller.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.sanghoonlee.imgursearch.Model.Imgur.ImageData;
import com.example.sanghoonlee.imgursearch.R;
import com.example.sanghoonlee.imgursearch.View.SquareImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanghoonlee on 2015-12-10.
 */

public class ImageSearchResultAdapter extends RecyclerView.Adapter<ImageSearchResultAdapter.ViewHolder> {

    public static final String TAG  = "SearchResultAdapter";

    //used to resume, pause, cancel picasso request
    public static final String IMAGE_RESULT_TAG = "ImageResultTag";

    private List<ImageData> mImageDatas;
    private Context mContext;

    public ImageSearchResultAdapter(Context context) {
        this.mImageDatas = new ArrayList<>();
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_grid_view, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public synchronized void addImageData(List<ImageData> models) {
        mImageDatas.addAll(models);
        notifyDataSetChanged();
    }

    public synchronized void resetImageData(List<ImageData> models) {
        mImageDatas.clear();
        mImageDatas.addAll(models);
        notifyDataSetChanged();

    }

    public ImageData getItemAt(int position) {
        return mImageDatas.get(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        //TODO: fix dimension for different size screens
        ImageData imageData = mImageDatas.get(position);
        Picasso.with(mContext)
                .load(imageData.url)
                .fit()
                .tag(IMAGE_RESULT_TAG)
                .centerCrop()
                .into(viewHolder.mThumbnail, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Log.i(TAG,"picasso image loading failed");
                        //may want to do some UI work to show error
                    }
                });
    }

    @Override
    public int getItemCount() {
        return (null != mImageDatas ? mImageDatas.size() : 0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SquareImageView mThumbnail;
        public ViewHolder(View parent) {
            super(parent);
            mThumbnail = (SquareImageView) parent.findViewById(R.id.img_thumbnail);
        }
    }
}
