package org.fourthline.cling.support.model.item;

import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;


public class Photo extends ImageItem {
    public static final Class CLASS = new Class("object.item.imageItem.photo");

    public Photo() {
        setClazz(CLASS);
    }

    public Photo(Item item) {
        super(item);
    }

    public Photo(String str, Container container, String str2, String str3, String str4, Res... resArr) {
        this(str, container.getId(), str2, str3, str4, resArr);
    }

    public Photo(String str, String str2, String str3, String str4, String str5, Res... resArr) {
        super(str, str2, str3, str4, resArr);
        setClazz(CLASS);
        if (str5 != null) {
            setAlbum(str5);
        }
    }

    public String getAlbum() {
        return (String) getFirstPropertyValue(Property.UPNP.ALBUM.class);
    }

    public Photo setAlbum(String str) {
        replaceFirstProperty(new Property.UPNP.ALBUM(str));
        return this;
    }
}
