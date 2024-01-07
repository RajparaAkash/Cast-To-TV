package com.example.chromecastone.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.example.chromecastone.Interface.ItemOnClickListener;
import com.example.chromecastone.Model.MediaFileModel;
import com.example.chromecastone.databinding.ItemviewFullImageBinding;

import java.util.List;

public class FullImageViewPagerAdapter extends RecyclerView.Adapter<FullImageViewPagerAdapter.FullImageViewHolder> {

    private List<MediaFileModel> listFileModel;
    private ItemOnClickListener onClickListener;

    public FullImageViewPagerAdapter(List<MediaFileModel> list, ItemOnClickListener itemOnClickListener) {
        this.listFileModel = list;
        this.onClickListener = itemOnClickListener;
    }

    @Override
    public FullImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new FullImageViewHolder(ItemviewFullImageBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(FullImageViewHolder fullImageViewHolder, @SuppressLint("RecyclerView") final int i) {
        MediaFileModel mediaFileModel = this.listFileModel.get(i);
        if (mediaFileModel == null) {
            return;
        }
        fullImageViewHolder.binding.ivFullImage.setImage(ImageSource.uri(mediaFileModel.getFilePath()));
        fullImageViewHolder.binding.ivFullImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                FullImageViewPagerAdapter.this.m70xd0f1e25a(i, view);
            }
        });
    }

    
    public void m70xd0f1e25a(int i, View view) {
        ItemOnClickListener itemOnClickListener = this.onClickListener;
        if (itemOnClickListener != null) {
            itemOnClickListener.onItemClick(i);
        }
    }

    @Override
    public int getItemCount() {
        List<MediaFileModel> list = this.listFileModel;
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    
    public class FullImageViewHolder extends RecyclerView.ViewHolder {
        private ItemviewFullImageBinding binding;

        public FullImageViewHolder(ItemviewFullImageBinding itemFullImageBinding) {
            super(itemFullImageBinding.getRoot());
            this.binding = itemFullImageBinding;
        }
    }
}
