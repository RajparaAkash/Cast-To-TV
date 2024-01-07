package com.example.chromecastone.Dlna.model.didl;

import com.example.chromecastone.R;

import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.VideoItem;

import java.util.List;


public class ClingVideoItem extends ClingDIDLItem {
    @Override
    public String getDataType() {
        return "video/*";
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_videos;
    }

    public ClingVideoItem(VideoItem videoItem) {
        super(videoItem);
    }

    @Override
    public String getDescription() {
        List<Res> resources = this.item.getResources();
        if (resources == null || resources.size() <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(resources.get(0).getResolution() != null ? resources.get(0).getResolution() : "");
        return sb.toString();
    }

    @Override
    public String getCount() {
        List<Res> resources = this.item.getResources();
        if (resources == null || resources.size() <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(resources.get(0).getDuration() != null ? resources.get(0).getDuration().split("\\.")[0] : "");
        return sb.toString();
    }
}
