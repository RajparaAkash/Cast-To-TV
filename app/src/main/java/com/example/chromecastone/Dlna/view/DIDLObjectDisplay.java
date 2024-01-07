package com.example.chromecastone.Dlna.view;

import com.example.chromecastone.Dlna.model.didl.IDIDLContainer;
import com.example.chromecastone.Dlna.model.didl.IDIDLObject;


public class DIDLObjectDisplay {
    protected static final String TAG = "DIDLContentDisplay";
    private final IDIDLObject didl;

    public DIDLObjectDisplay(IDIDLObject iDIDLObject) {
        this.didl = iDIDLObject;
    }

    public IDIDLObject getDIDLObject() {
        return this.didl;
    }

    public String getTitle() {
        return this.didl.getTitle();
    }

    public String getDescription() {
        return this.didl.getDescription();
    }

    public String getCount() {
        return this.didl.getCount();
    }

    public int getIcon() {
        return this.didl.getIcon();
    }

    public String toString() {
        IDIDLObject iDIDLObject = this.didl;
        if (iDIDLObject instanceof IDIDLContainer) {
            return this.didl.getTitle() + " (" + ((IDIDLContainer) this.didl).getChildCount() + ")";
        }
        return iDIDLObject.getTitle();
    }
}
