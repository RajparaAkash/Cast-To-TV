package com.example.chromecastone.Dlna.controller;

import android.content.Context;

import com.example.chromecastone.Activity.CastDeviceListActivity;
import com.example.chromecastone.Dlna.model.RendererState;
import com.example.chromecastone.Dlna.model.upnp.ARendererState;
import com.example.chromecastone.Dlna.model.upnp.IContentDirectoryCommand;
import com.example.chromecastone.Dlna.model.upnp.IFactory;
import com.example.chromecastone.Dlna.model.upnp.IRendererCommand;
import com.example.chromecastone.Dlna.model.upnp.IRendererState;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.controlpoint.ControlPoint;


public class Factory implements IFactory {
    @Override
    public IContentDirectoryCommand createContentDirectoryCommand() {
        AndroidUpnpService upnpService = ((ServiceListener) CastDeviceListActivity.upnpServiceController.getServiceListener()).getUpnpService();
        ControlPoint controlPoint = upnpService != null ? upnpService.getControlPoint() : null;
        if (controlPoint != null) {
            return new ContentDirectoryCommand(controlPoint);
        }
        return null;
    }

    @Override
    public IRendererCommand createRendererCommand(IRendererState iRendererState) {
        AndroidUpnpService upnpService = ((ServiceListener) CastDeviceListActivity.upnpServiceController.getServiceListener()).getUpnpService();
        ControlPoint controlPoint = upnpService != null ? upnpService.getControlPoint() : null;
        if (controlPoint != null) {
            return new RendererCommand(controlPoint, (RendererState) iRendererState);
        }
        return null;
    }

    @Override
    public IUpnpServiceController createUpnpServiceController(Context context) {
        return new ServiceController(context);
    }

    @Override
    public ARendererState createRendererState() {
        return new RendererState();
    }
}
