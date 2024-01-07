package com.example.chromecastone.Dlna.model.didl;

import android.util.Log;

import com.example.chromecastone.R;

import org.fourthline.cling.support.model.item.Item;


public class ClingDIDLItem extends ClingDIDLObject implements IDIDLItem {
    private static final String TAG = "ClingDIDLItem";

    @Override
    public int getIcon() {
        return R.drawable.ic_images;
    }

    public ClingDIDLItem(Item item) {
        super(item);
    }

    @Override
    public String getURI() {
        if (this.item != null) {
            Log.d(TAG, "Item : " + this.item.getFirstResource().getValue());
            if (this.item.getFirstResource() == null || this.item.getFirstResource().getValue() == null) {
                return null;
            }
            return this.item.getFirstResource().getValue();
        }
        return null;
    }
}
