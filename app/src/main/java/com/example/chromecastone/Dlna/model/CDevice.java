package com.example.chromecastone.Dlna.model;

import android.util.Log;

import com.example.chromecastone.Dlna.model.upnp.IUpnpDevice;

import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDAServiceType;


public class CDevice implements IUpnpDevice {
    private static final String TAG = "ClingDevice";
    Device device;

    public CDevice(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return this.device;
    }

    @Override
    public String getDisplayString() {
        return this.device.getDisplayString();
    }

    @Override
    public String getFriendlyName() {
        return (this.device.getDetails() == null || this.device.getDetails().getFriendlyName() == null) ? getDisplayString() : this.device.getDetails().getFriendlyName();
    }

    @Override
    public boolean equals(IUpnpDevice iUpnpDevice) {
        return this.device.getIdentity().getUdn().equals(((CDevice) iUpnpDevice).getDevice().getIdentity().getUdn());
    }

    @Override
    public String getUID() {
        return this.device.getIdentity().getUdn().toString();
    }

    @Override
    public String getExtendedInformation() {
        ServiceType[] findServiceTypes;
        String str = "";
        if (this.device.findServiceTypes() != null) {
            for (ServiceType serviceType : this.device.findServiceTypes()) {
                str = str + "\n\t" + serviceType.getType() + " : " + serviceType.toFriendlyString();
            }
        }
        return str;
    }

    @Override
    public void printService() {
        Service[] findServices;
        for (Service service : this.device.findServices()) {
            Log.i(TAG, "\t Service : " + service);
            for (Action action : service.getActions()) {
                Log.i(TAG, "\t\t Action : " + action);
            }
        }
    }

    @Override
    public boolean asService(String str) {
        return this.device.findService(new UDAServiceType(str)) != null;
    }

    @Override
    public String getManufacturer() {
        return this.device.getDetails().getManufacturerDetails().getManufacturer();
    }

    @Override
    public String getManufacturerURL() {
        try {
            return this.device.getDetails().getManufacturerDetails().getManufacturerURI().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String getModelName() {
        try {
            return this.device.getDetails().getModelDetails().getModelName();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String getModelDesc() {
        try {
            return this.device.getDetails().getModelDetails().getModelDescription();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String getModelNumber() {
        try {
            return this.device.getDetails().getModelDetails().getModelNumber();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String getModelURL() {
        try {
            return this.device.getDetails().getModelDetails().getModelURI().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String getXMLURL() {
        try {
            return this.device.getDetails().getBaseURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String getPresentationURL() {
        try {
            return this.device.getDetails().getPresentationURI().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String getSerialNumber() {
        try {
            return this.device.getDetails().getSerialNumber();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String getUDN() {
        try {
            return this.device.getIdentity().getUdn().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public boolean isFullyHydrated() {
        return this.device.isFullyHydrated();
    }
}
