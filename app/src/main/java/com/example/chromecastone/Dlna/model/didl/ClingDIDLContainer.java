package com.example.chromecastone.Dlna.model.didl;

import com.example.chromecastone.R;

import org.fourthline.cling.support.model.container.Container;


public class ClingDIDLContainer extends ClingDIDLObject implements IDIDLContainer {
    @Override
    public int getIcon() {
        return R.drawable.ic_chrome_cast;
    }

    public ClingDIDLContainer(Container container) {
        super(container);
    }

    @Override
    public String getCount() {
        return "" + getChildCount();
    }

    @Override
    public int getChildCount() {
        Integer childCount;
        if (this.item == null || !(this.item instanceof Container) || (childCount = ((Container) this.item).getChildCount()) == null) {
            return 0;
        }
        return childCount.intValue();
    }
}
