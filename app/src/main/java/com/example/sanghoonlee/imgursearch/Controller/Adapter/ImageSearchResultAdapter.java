package com.example.sanghoonlee.imgursearch.Controller.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sanghoonlee.imgursearch.Controller.ImgurSearchable;
import com.example.sanghoonlee.imgursearch.Model.Imgur.ImageData;
import com.example.sanghoonlee.imgursearch.R;
import com.example.sanghoonlee.imgursearch.View.SquareImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanghoonlee on 2015-12-10.
 */

public class ImageSearchResultAdapter extends RecyclerView.Adapter<ImageSearchResultAdapter.ViewHolder>
                                                                    implements ImgurClientAdapter{

    public static final String TAG  = "SearchResultAdapter";

    private List<ImageData> mImageDatas;
    private Context mContext;
    private ImgurSearchable mImgurSearchable;

    public ImageSearchResultAdapter(Context context, ImgurSearchable searchable) {
        this.mImageDatas = new ArrayList<>();
        mContext = context;
        mImgurSearchable = searchable;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_grid_view, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public synchronized void addImageData(List<ImageData> models) {
        mImageDatas.addAll(models);
        notifyDataSetChanged();
        if(models.isEmpty()) {
            mImgurSearchable.onNoMoreResult();
        }
    }

    @Override
    public synchronized void resetImageData(List<ImageData> models) {
        mImageDatas.clear();
        mImageDatas.addAll(models);
        notifyDataSetChanged();
        if(mImageDatas.isEmpty()) {
            mImgurSearchable.onNoResultFound();
        } else {
            mImgurSearchable.onResultFound();
        }
    }

    public ImageData getItemAt(int position) {
        return mImageDatas.get(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        //TODO: fix dimension for different size screens
        ImageData imageData = mImageDatas.get(position);
        Glide.with(viewHolder.itemView.getContext())
                .load(imageData.url)
                .asBitmap()
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .centerCrop()
                .into(viewHolder.mThumbnail);
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
