package com.example.chromecastone.Dlna.model.didl;

import com.example.chromecastone.R;

import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.ImageItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class ClingImageItem extends ClingDIDLItem {
    @Override
    public String getDataType() {
        return "image/*";
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_images;
    }

    public ClingImageItem(ImageItem imageItem) {
        super(imageItem);
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
        try {
            return DateFormat.getDateTimeInstance().format(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(((ImageItem) this.item).getDate()));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
