package com.example.chromecastone.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chromecastone.Interface.ItemOnClickListener;
import com.example.chromecastone.Model.MediaFolderData;
import com.example.chromecastone.databinding.ItemviewFolderAudiosBinding;

import java.util.ArrayList;
import java.util.List;


public class AlbumAudiosAdapter extends RecyclerView.Adapter<AlbumAudiosAdapter.AllFolderViewHolder> {

    private List<MediaFolderData> listFolderData = new ArrayList();
    private Context mContext;
    private ItemOnClickListener onClickListener;

    public AlbumAudiosAdapter(Context context) {
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
        return new AllFolderViewHolder(ItemviewFolderAudiosBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(AllFolderViewHolder allFolderViewHolder, @SuppressLint("RecyclerView") final int i) {
        MediaFolderData mediaFolderData = this.listFolderData.get(i);
        if (mediaFolderData == null) {
            return;
        }
        allFolderViewHolder.binding.tvFolderName.setText(mediaFolderData.getFolderName());
//        TextView textView = allFolderViewHolder.binding.tvImageCount;
//        textView.setText(mediaFolderData.getMediaFileList().size() + " " + this.mContext.getString(R.string.text_song_item));
        if (mediaFolderData.getMediaFileList().size() == 0) {
            return;
        }
//        RequestManager with = Glide.with(this.mContext);
//        with.load(Uri.parse("content://media/external/audio/albumart/" + mediaFolderData.getMediaFileList().get(0).getAlbumId())).apply((BaseRequestOptions<?>) new RequestOptions().placeholder(R.drawable.ic_music_placeholder).diskCacheStrategy(DiskCacheStrategy.ALL)).into(allFolderViewHolder.binding.ivFolderImage);
        allFolderViewHolder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aaaNext(i);
            }
        });
    }

    
    public void aaaNext(int i) {
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
        private ItemviewFolderAudiosBinding binding;

        public AllFolderViewHolder(ItemviewFolderAudiosBinding itemFolderAudiosBinding) {
            super(itemFolderAudiosBinding.getRoot());
            this.binding = itemFolderAudiosBinding;
        }
    }
}
