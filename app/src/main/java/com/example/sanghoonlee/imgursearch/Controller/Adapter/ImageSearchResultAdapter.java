package com.example.sanghoonlee.imgursearch.Controller.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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

public class ImageSearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
                                                                    implements ImgurClientAdapter{

    public static final String  TAG          = "SearchResultAdapter";
    public static final int     VIEW_TYPE_PROGRESSBAR = 0;
    public static final int     VIEW_TYPE_ITEM        = 1;

    private List<ImageData> mImageDatas;
    private Context mContext;
    private ImgurSearchable mImgurSearchable;
    private boolean isFooterEnabled = false;

    public ImageSearchResultAdapter(Context context, ImgurSearchable searchable) {
        this.mImageDatas = new ArrayList<>();
        mContext = context;
        mImgurSearchable = searchable;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if(viewType== VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(
                    viewGroup.getContext()).inflate(R.layout.item_grid_view, null);
            viewHolder = new ImageViewHolder(view);
        }else {
            View view = LayoutInflater.from(
                    viewGroup.getContext()).inflate(R.layout.progress_bar, viewGroup, false);
            viewHolder = new ProgressViewHolder(view);
        }
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

    public void enableFooter(boolean isEnabled){
        this.isFooterEnabled = isEnabled;
        notifyDataSetChanged();
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ProgressViewHolder){
            ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        } else if(mImageDatas.size() > 0 && position < mImageDatas.size()) {
            ImageViewHolder viewHolder = ((ImageViewHolder)holder);
            ImageData imageData = mImageDatas.get(position);
            Glide.with(viewHolder.itemView.getContext())
                    .load(imageData.url)
                    .asBitmap()
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .centerCrop()
                    .into(viewHolder.mThumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return  (isFooterEnabled) ? mImageDatas.size() + 1 : mImageDatas.size();
    }
    @Override
    public int getItemViewType(int position) {
        return (isFooterEnabled && position >= mImageDatas.size() ) ? VIEW_TYPE_PROGRESSBAR : VIEW_TYPE_ITEM;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public SquareImageView mThumbnail;
        public ImageViewHolder(View parent) {
            super(parent);
            mThumbnail = (SquareImageView) parent.findViewById(R.id.img_thumbnail);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public ProgressViewHolder(View parent) {
            super(parent);
            progressBar = (ProgressBar) parent.findViewById(R.id.loading_progress_bar);
        }
    }
}
