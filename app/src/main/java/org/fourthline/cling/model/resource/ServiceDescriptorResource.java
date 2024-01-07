package org.fourthline.cling.model.resource;

import org.fourthline.cling.model.meta.LocalService;

import java.net.URI;


public class ServiceDescriptorResource extends Resource<LocalService> {
    public ServiceDescriptorResource(URI uri, LocalService localService) {
        super(uri, localService);
    }
}
