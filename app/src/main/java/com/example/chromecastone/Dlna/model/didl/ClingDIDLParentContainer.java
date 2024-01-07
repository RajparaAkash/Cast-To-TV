package com.example.chromecastone.Dlna.model.didl;

import org.fourthline.cling.support.model.container.Container;


public class ClingDIDLParentContainer extends ClingDIDLObject implements IDIDLParentContainer {
    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public String getTitle() {
        return "..";
    }

    public ClingDIDLParentContainer(String str) {
        super(new Container());
        this.item.setId(str);
    }
}
