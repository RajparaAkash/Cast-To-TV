package org.fourthline.cling.model.resource;

import org.fourthline.cling.model.meta.Icon;

import java.net.URI;


public class IconResource extends Resource<Icon> {
    public IconResource(URI uri, Icon icon) {
        super(uri, icon);
    }
}
