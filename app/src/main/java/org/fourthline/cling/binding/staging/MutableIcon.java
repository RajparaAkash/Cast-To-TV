package org.fourthline.cling.binding.staging;

import org.fourthline.cling.model.meta.Icon;

import java.net.URI;


public class MutableIcon {
    public int depth;
    public int height;
    public String mimeType;
    public URI uri;
    public int width;

    public Icon build() {
        return new Icon(this.mimeType, this.width, this.height, this.depth, this.uri);
    }
}
