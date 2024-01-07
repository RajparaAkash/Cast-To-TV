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
import com.example.chromecastone.Model.MediaFolderData;
import com.example.chromecastone.R;
import com.example.chromecastone.databinding.ItemviewFolderPhotosBinding;

import java.util.ArrayList;
import java.util.List;


public class AlbumPhotosAdapter extends RecyclerView.Adapter<AlbumPhotosAdapter.AllFolderViewHolder> {

    private List<MediaFolderData> listFolderData = new ArrayList();
    private Context mContext;
    private ItemOnClickListener onClickListener;

    public AlbumPhotosAdapter(Context context) {
        this.mContext = context;
    }

    public void setFolderDataList(List<MediaFolderData> list) {
        this.listFolderData = list;
        notifyDataSetChanged();
    }

    public void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
        this.onClickListener = itemOnClickListener;
    }

    @Override
    public AllFolderViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new AllFolderViewHolder(ItemviewFolderPhotosBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(AllFolderViewHolder allFolderViewHolder, @SuppressLint("RecyclerView") final int i) {
        MediaFolderData mediaFolderData = this.listFolderData.get(i);
        if (mediaFolderData == null) {
            return;
        }
        allFolderViewHolder.binding.tvFolderName.setText(mediaFolderData.getFolderName());
//        TextView textView = allFolderViewHolder.binding.tvImageCount;
//        textView.setText(mediaFolderData.getMediaFileList().size() + " " + this.mContext.getString(R.string.text_image_item));
        if (mediaFolderData.getMediaFileList().size() == 0) {
            return;
        }
        Glide.with(this.mContext).load(mediaFolderData.getMediaFileList().get(0).getFilePath()).apply((BaseRequestOptions<?>) new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ic_image_placeholder)).into(allFolderViewHolder.binding.ivFolderImage);
        allFolderViewHolder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apaNext(i);
            }
        });
    }

    
    public void apaNext(int i) {
        ItemOnClickListener itemOnClickListener = this.onClickListener;
        if (itemOnClickListener != null) {
            itemOnClickListener.onItemClick(i);
        }
    }

    @Override
    public int getItemCount() {
        if (this.listFolderData.size() != 0) {
            return this.listFolderData.size();
        }
        return 0;
    }

    public static class AllFolderViewHolder extends RecyclerView.ViewHolder {
        private ItemviewFolderPhotosBinding binding;

        public AllFolderViewHolder(ItemviewFolderPhotosBinding itemFolderPhotosBinding) {
            super(itemFolderPhotosBinding.getRoot());
            this.binding = itemFolderPhotosBinding;
        }
    }
}
