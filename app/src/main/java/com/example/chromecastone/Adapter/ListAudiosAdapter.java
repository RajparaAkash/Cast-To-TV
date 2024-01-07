package com.example.chromecastone.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chromecastone.Interface.ItemOnClickListener;
import com.example.chromecastone.Model.MediaFileModel;
import com.example.chromecastone.databinding.ItemviewListAudiosBinding;

import java.util.ArrayList;
import java.util.List;


public class ListAudiosAdapter extends RecyclerView.Adapter<ListAudiosAdapter.MusicListViewHolder> {

    List<MediaFileModel> folderDataList = new ArrayList();
    Context mContext;
    ItemOnClickListener onClickListener;

    public ListAudiosAdapter(Context context) {
        this.mContext = context;
    }

    public void setImageDataList(List<MediaFileModel> list) {
        this.folderDataList = list;
        notifyDataSetChanged();
    }

    public void setOnItemClickListner(ItemOnClickListener itemOnClickListener) {
        this.onClickListener = itemOnClickListener;
    }

    @Override
    public MusicListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MusicListViewHolder(ItemviewListAudiosBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(MusicListViewHolder musicListViewHolder, @SuppressLint("RecyclerView") final int i) {
        MediaFileModel mediaFileModel = this.folderDataList.get(i);
        musicListViewHolder.binding.tvAlbumName.setText(mediaFileModel.getSongAlbum());
        musicListViewHolder.binding.tvFileName.setText(mediaFileModel.getFileName());
        musicListViewHolder.binding.clickMainAudioslist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                laaNext(i);
            }
        });
    }


    public void laaNext(int i) {
        ItemOnClickListener itemOnClickListener = this.onClickListener;
        if (itemOnClickListener != null) {
            itemOnClickListener.onItemClick(i);
        }
    }

    @Override
    public int getItemCount() {
        return this.folderDataList.size();
    }


    public class MusicListViewHolder extends RecyclerView.ViewHolder {
        private ItemviewListAudiosBinding binding;

        public MusicListViewHolder(ItemviewListAudiosBinding itemMusicBinding) {
            super(itemMusicBinding.getRoot());
            this.binding = itemMusicBinding;
        }
    }
}
