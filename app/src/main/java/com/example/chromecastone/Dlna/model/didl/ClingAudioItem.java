package com.example.chromecastone.Dlna.model.didl;

import com.example.chromecastone.R;

import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.AudioItem;
import org.fourthline.cling.support.model.item.MusicTrack;

import java.util.List;


public class ClingAudioItem extends ClingDIDLItem {
    @Override
    public String getDataType() {
        return "audio/*";
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_audio;
    }

    public ClingAudioItem(AudioItem audioItem) {
        super(audioItem);
    }

    @Override
    public String getDescription() {
        if (this.item instanceof MusicTrack) {
            MusicTrack musicTrack = (MusicTrack) this.item;
            StringBuilder sb = new StringBuilder();
            String str = "";
            sb.append((musicTrack.getFirstArtist() == null || musicTrack.getFirstArtist().getName() == null) ? "" : musicTrack.getFirstArtist().getName());
            if (musicTrack.getAlbum() != null) {
                str = " - " + musicTrack.getAlbum();
            }
            sb.append(str);
            return sb.toString();
        }
        return ((AudioItem) this.item).getDescription();
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
