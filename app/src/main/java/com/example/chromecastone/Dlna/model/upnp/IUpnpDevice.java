package com.example.chromecastone.Dlna.model.upnp;


public interface IUpnpDevice {
    boolean asService(String str);

    boolean equals(IUpnpDevice iUpnpDevice);

    String getDisplayString();

    String getExtendedInformation();

    String getFriendlyName();

    String getManufacturer();

    String getManufacturerURL();

    String getModelDesc();

    String getModelName();

    String getModelNumber();

    String getModelURL();

    String getPresentationURL();

    String getSerialNumber();

    String getUDN();

    String getUID();

    String getXMLURL();

    boolean isFullyHydrated();

    void printService();

    String toString();
}
