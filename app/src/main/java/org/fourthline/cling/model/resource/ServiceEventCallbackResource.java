package org.fourthline.cling.model.resource;

import org.fourthline.cling.model.meta.RemoteService;

import java.net.URI;


public class ServiceEventCallbackResource extends Resource<RemoteService> {
    public ServiceEventCallbackResource(URI uri, RemoteService remoteService) {
        super(uri, remoteService);
    }
}
