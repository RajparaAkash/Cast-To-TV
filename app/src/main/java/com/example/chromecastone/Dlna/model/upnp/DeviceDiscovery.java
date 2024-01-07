package com.example.chromecastone.Dlna.model.upnp;

import android.util.Log;

import com.example.chromecastone.Activity.CastDeviceListActivity;

import java.util.ArrayList;
import java.util.Iterator;


public abstract class DeviceDiscovery {
    protected static final String TAG = "DeviceDiscovery";
    private final BrowsingRegistryListener browsingRegistryListener;
    protected boolean extendedInformation;
    private final ArrayList<IDeviceDiscoveryObserver> observerList;

    protected abstract ICallableFilter getCallableFilter();

    protected abstract boolean isSelected(IUpnpDevice iUpnpDevice);

    protected abstract void removed(IUpnpDevice iUpnpDevice);

    protected abstract void select(IUpnpDevice iUpnpDevice);

    protected abstract void select(IUpnpDevice iUpnpDevice, boolean z);

    public DeviceDiscovery(IServiceListener iServiceListener, boolean z) {
        this.browsingRegistryListener = new BrowsingRegistryListener();
        this.extendedInformation = z;
        this.observerList = new ArrayList<>();
    }

    public DeviceDiscovery(IServiceListener iServiceListener) {
        this(iServiceListener, false);
    }

    public void resume(IServiceListener iServiceListener) {
        iServiceListener.addListener(this.browsingRegistryListener);
    }

    public void pause(IServiceListener iServiceListener) {
        iServiceListener.removeListener(this.browsingRegistryListener);
    }

    
    public class BrowsingRegistryListener implements IRegistryListener {
        public BrowsingRegistryListener() {
        }

        @Override
        public void deviceAdded(IUpnpDevice iUpnpDevice) {
            Log.v(DeviceDiscovery.TAG, "New device detected : " + iUpnpDevice.getDisplayString());
            if (iUpnpDevice.isFullyHydrated() && DeviceDiscovery.this.filter(iUpnpDevice)) {
                if (DeviceDiscovery.this.isSelected(iUpnpDevice)) {
                    Log.i(DeviceDiscovery.TAG, "Reselect device to refresh it");
                    DeviceDiscovery.this.select(iUpnpDevice, true);
                }
                DeviceDiscovery.this.notifyAdded(iUpnpDevice);
            }
        }

        @Override
        public void deviceRemoved(IUpnpDevice iUpnpDevice) {
            Log.v(DeviceDiscovery.TAG, "Device removed : " + iUpnpDevice.getFriendlyName());
            if (DeviceDiscovery.this.filter(iUpnpDevice)) {
                if (DeviceDiscovery.this.isSelected(iUpnpDevice)) {
                    Log.i(DeviceDiscovery.TAG, "Selected device have been removed");
                    DeviceDiscovery.this.removed(iUpnpDevice);
                }
                DeviceDiscovery.this.notifyRemoved(iUpnpDevice);
            }
        }
    }

    public void addObserver(IDeviceDiscoveryObserver iDeviceDiscoveryObserver) {
        this.observerList.add(iDeviceDiscoveryObserver);
        for (IUpnpDevice iUpnpDevice : CastDeviceListActivity.upnpServiceController.getServiceListener().getFilteredDeviceList(getCallableFilter())) {
            iDeviceDiscoveryObserver.addedDevice(iUpnpDevice);
        }
    }

    public void removeObserver(IDeviceDiscoveryObserver iDeviceDiscoveryObserver) {
        this.observerList.remove(iDeviceDiscoveryObserver);
    }

    public void notifyAdded(IUpnpDevice iUpnpDevice) {
        Iterator<IDeviceDiscoveryObserver> it = this.observerList.iterator();
        while (it.hasNext()) {
            it.next().addedDevice(iUpnpDevice);
        }
    }

    public void notifyRemoved(IUpnpDevice iUpnpDevice) {
        Iterator<IDeviceDiscoveryObserver> it = this.observerList.iterator();
        while (it.hasNext()) {
            it.next().removedDevice(iUpnpDevice);
        }
    }

    protected boolean filter(IUpnpDevice iUpnpDevice) {
        ICallableFilter callableFilter = getCallableFilter();
        callableFilter.setDevice(iUpnpDevice);
        try {
            return callableFilter.call().booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
