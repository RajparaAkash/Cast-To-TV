package com.example.chromecastone.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.chromecastone.Interface.ItemOnClickListener;
import com.example.chromecastone.Model.MediaFileModel;
import com.example.chromecastone.R;
import com.example.chromecastone.Utils.Utils;
import com.example.chromecastone.databinding.ItemviewListVideosBinding;

import java.util.ArrayList;
import java.util.List;


public class ListVideosAdapter extends RecyclerView.Adapter<ListVideosAdapter.ImageListViewHolder> {

    private List<MediaFileModel> listImageData = new ArrayList();
    private Context mContext;
    private ItemOnClickListener onClickListener;

    public ListVideosAdapter(Context context) {
        this.mContext = context;
    }

    public void setListImageData(List<MediaFileModel> list) {
        this.listImageData = list;
    }

    public void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
        this.onClickListener = itemOnClickListener;
    }

    @Override
    public ImageListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ImageListViewHolder(ItemviewListVideosBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ImageListViewHolder imageListViewHolder, @SuppressLint("RecyclerView") final int i) {
        MediaFileModel mediaFileModel = this.listImageData.get(i);
        if (mediaFileModel == null) {
            return;
        }

        Glide.with(this.mContext).load(mediaFileModel.getFilePath()).apply((BaseRequestOptions<?>) new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ic_video_placeholder)).into(imageListViewHolder.binding.ivIcon);
        imageListViewHolder.binding.tvFileDuration.setVisibility(View.VISIBLE);
        if (mediaFileModel.getDuration() != null) {
            if (!mediaFileModel.getDuration().contains(":")) {
                imageListViewHolder.binding.tvFileDuration.setText(Utils.makeShortTimeString(this.mContext, Long.parseLong(mediaFileModel.getDuration()) / 1000));
            } else {
                imageListViewHolder.binding.tvFileDuration.setText(mediaFileModel.getDuration());
            }
        }
        imageListViewHolder.binding.clickMainVideoslist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m76xb4bee36d(i);
            }
        });
    }


    public void m76xb4bee36d(int i) {
        this.onClickListener.onItemClick(i);
    }

    @Override
    public int getItemCount() {
        if (this.listImageData.size() != 0) {
            return this.listImageData.size();
        }
        return 0;
    }

    public class ImageListViewHolder extends RecyclerView.ViewHolder {
        private ItemviewListVideosBinding binding;

        public ImageListViewHolder(ItemviewListVideosBinding itemImagesBinding) {
            super(itemImagesBinding.getRoot());
            this.binding = itemImagesBinding;
        }
    }
}
