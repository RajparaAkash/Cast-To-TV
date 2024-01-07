package com.example.chromecastone.Dlna.model.upnp;

import java.util.concurrent.Callable;


public interface ICallableFilter extends Callable<Boolean> {
    void setDevice(IUpnpDevice iUpnpDevice);
}
