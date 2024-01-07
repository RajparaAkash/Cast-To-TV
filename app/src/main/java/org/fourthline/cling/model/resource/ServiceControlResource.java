package org.fourthline.cling.model.resource;

import org.fourthline.cling.model.meta.LocalService;

import java.net.URI;


public class ServiceControlResource extends Resource<LocalService> {
    public ServiceControlResource(URI uri, LocalService localService) {
        super(uri, localService);
    }
}
