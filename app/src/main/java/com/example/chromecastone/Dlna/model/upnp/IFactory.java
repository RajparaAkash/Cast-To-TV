package com.example.chromecastone.Dlna.model.upnp;

import android.content.Context;

import com.example.chromecastone.Dlna.controller.IUpnpServiceController;


public interface IFactory {
    IContentDirectoryCommand createContentDirectoryCommand();

    IRendererCommand createRendererCommand(IRendererState iRendererState);

    ARendererState createRendererState();

    IUpnpServiceController createUpnpServiceController(Context context);
}
