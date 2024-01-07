package com.example.chromecastone.Dlna.model.didl;

import org.fourthline.cling.support.model.DIDLObject;


public class ClingDIDLObject implements IDIDLObject {
    private static final String TAG = "ClingDIDLObject";
    protected DIDLObject item;

    @Override
    public String getCount() {
        return "";
    }

    @Override
    public String getDataType() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public int getIcon() {
        return 17170445;
    }

    public ClingDIDLObject(DIDLObject dIDLObject) {
        this.item = dIDLObject;
    }

    public DIDLObject getObject() {
        return this.item;
    }

    @Override
    public String getTitle() {
        return this.item.getTitle();
    }

    @Override
    public String getParentID() {
        return this.item.getParentID();
    }

    @Override
    public String getId() {
        return this.item.getId();
    }
}
