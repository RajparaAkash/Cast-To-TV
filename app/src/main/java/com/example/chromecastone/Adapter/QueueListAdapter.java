package com.example.chromecastone.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.chromecastone.Interface.ItemOnClickListener;
import com.example.chromecastone.Model.MediaFileModel;
import com.example.chromecastone.R;
import com.example.chromecastone.databinding.ItemviewListQueueBinding;

import java.util.ArrayList;
import java.util.List;


public class QueueListAdapter extends RecyclerView.Adapter<QueueListAdapter.QueueListViewHolder> {

    private boolean isMusic;
    private List<MediaFileModel> listMediaQueue = new ArrayList();
    private Context mContext;
    private ItemOnClickListener onClickListener;

    public QueueListAdapter(Context context, boolean z) {
        this.mContext = context;
        this.isMusic = z;
    }

    public void setListMediaQueue(List<MediaFileModel> list) {
        this.listMediaQueue = list;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(ItemOnClickListener itemOnClickListener) {
        this.onClickListener = itemOnClickListener;
    }

    @Override
    public QueueListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new QueueListViewHolder(ItemviewListQueueBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(QueueListViewHolder queueListViewHolder, @SuppressLint("RecyclerView") final int i) {
        MediaFileModel mediaFileModel = this.listMediaQueue.get(i);
        if (mediaFileModel == null) {
            return;
        }

        if (this.isMusic) {
            RequestManager with = Glide.with(this.mContext);
            with.load(Uri.parse("content://media/external/audio/albumart/" + mediaFileModel.getAlbumId())).apply((BaseRequestOptions<?>) new RequestOptions().placeholder(R.drawable.ic_music_placeholder).diskCacheStrategy(DiskCacheStrategy.ALL)).into(queueListViewHolder.binding.icon);
        } else {
            Glide.with(this.mContext).load(mediaFileModel.getFilePath()).apply((BaseRequestOptions<?>) new RequestOptions().placeholder(R.drawable.ic_video_placeholder)).into(queueListViewHolder.binding.icon);
        }
        queueListViewHolder.binding.tvAlbumName.setVisibility(View.GONE);
        queueListViewHolder.binding.tvFileName.setText(mediaFileModel.getFileName());
        queueListViewHolder.binding.getRoot().setBackgroundColor(0);
        queueListViewHolder.binding.clickMainQueuelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                QueueListAdapter.this.m77x830ac583(i, view);
            }
        });
    }

    
    public void m77x830ac583(int i, View view) {
        ItemOnClickListener itemOnClickListener = this.onClickListener;
        if (itemOnClickListener != null) {
            itemOnClickListener.onItemClick(i);
        }
    }

    @Override
    public int getItemCount() {
        if (this.listMediaQueue.size() != 0) {
            return this.listMediaQueue.size();
        }
        return 0;
    }

    
    public class QueueListViewHolder extends RecyclerView.ViewHolder {
        private ItemviewListQueueBinding binding;

        public QueueListViewHolder(ItemviewListQueueBinding itemMusicBinding) {
            super(itemMusicBinding.getRoot());
            this.binding = itemMusicBinding;
        }
    }
}
