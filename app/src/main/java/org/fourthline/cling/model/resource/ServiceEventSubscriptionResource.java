package org.fourthline.cling.model.resource;

import org.fourthline.cling.model.meta.LocalService;

import java.net.URI;


public class ServiceEventSubscriptionResource extends Resource<LocalService> {
    public ServiceEventSubscriptionResource(URI uri, LocalService localService) {
        super(uri, localService);
    }
}
