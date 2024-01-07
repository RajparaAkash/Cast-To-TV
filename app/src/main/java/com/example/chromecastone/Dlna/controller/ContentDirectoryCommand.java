package com.example.chromecastone.Dlna.controller;

import android.util.Log;

import com.example.chromecastone.Activity.CastDeviceListActivity;
import com.example.chromecastone.Dlna.model.CDevice;
import com.example.chromecastone.Dlna.model.didl.ClingAudioItem;
import com.example.chromecastone.Dlna.model.didl.ClingDIDLContainer;
import com.example.chromecastone.Dlna.model.didl.ClingDIDLItem;
import com.example.chromecastone.Dlna.model.didl.ClingDIDLParentContainer;
import com.example.chromecastone.Dlna.model.didl.ClingImageItem;
import com.example.chromecastone.Dlna.model.didl.ClingVideoItem;
import com.example.chromecastone.Dlna.model.didl.IDIDLObject;
import com.example.chromecastone.Dlna.model.upnp.IContentDirectoryCommand;
import com.example.chromecastone.Dlna.view.DIDLObjectDisplay;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.AudioItem;
import org.fourthline.cling.support.model.item.ImageItem;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.item.VideoItem;

import java.util.ArrayList;


public class ContentDirectoryCommand implements IContentDirectoryCommand {
    private static final String TAG = "ContentDirectoryCommand";
    private final ControlPoint controlPoint;

    public ContentDirectoryCommand(ControlPoint controlPoint) {
        this.controlPoint = controlPoint;
    }

    private Service getMediaReceiverRegistarService() {
        if (CastDeviceListActivity.upnpServiceController.getSelectedContentDirectory() == null) {
            return null;
        }
        return ((CDevice) CastDeviceListActivity.upnpServiceController.getSelectedContentDirectory()).getDevice().findService(new UDAServiceType("X_MS_MediaReceiverRegistar"));
    }

    private Service getContentDirectoryService() {
        if (CastDeviceListActivity.upnpServiceController.getSelectedContentDirectory() == null) {
            return null;
        }
        return ((CDevice) CastDeviceListActivity.upnpServiceController.getSelectedContentDirectory()).getDevice().findService(new UDAServiceType("ContentDirectory"));
    }

    private ArrayList<DIDLObjectDisplay> buildContentList(String str, DIDLContent dIDLContent) {
        IDIDLObject clingDIDLItem;
        ArrayList<DIDLObjectDisplay> arrayList = new ArrayList<>();
        if (str != null) {
            arrayList.add(new DIDLObjectDisplay(new ClingDIDLParentContainer(str)));
        }
        for (Container container : dIDLContent.getContainers()) {
            arrayList.add(new DIDLObjectDisplay(new ClingDIDLContainer(container)));
            Log.v(TAG, "Add container : " + container.getTitle());
        }
        for (Item item : dIDLContent.getItems()) {
            if (item instanceof VideoItem) {
                clingDIDLItem = new ClingVideoItem((VideoItem) item);
            } else if (item instanceof AudioItem) {
                clingDIDLItem = new ClingAudioItem((AudioItem) item);
            } else if (item instanceof ImageItem) {
                clingDIDLItem = new ClingImageItem((ImageItem) item);
            } else {
                clingDIDLItem = new ClingDIDLItem(item);
            }
            arrayList.add(new DIDLObjectDisplay(clingDIDLItem));
            Log.v(TAG, "Add item : " + item.getTitle());
            for (DIDLObject.Property property : item.getProperties()) {
                Log.v(TAG, property.getDescriptorName() + " " + property.toString());
            }
        }
        return arrayList;
    }

    @Override
    public boolean isSearchAvailable() {
        getContentDirectoryService();
        return false;
    }
}
