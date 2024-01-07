package org.fourthline.cling.support.model.container;


public class StorageFolder extends Container {
    public static final Class CLASS = new Class("object.container.storageFolder");

    public StorageFolder() {
        setClazz(CLASS);
    }

    public StorageFolder(Container container) {
        super(container);
    }

    public StorageFolder(String str, Container container, String str2, String str3, Integer num, Long l) {
        this(str, container.getId(), str2, str3, num, l);
    }

    public StorageFolder(String str, String str2, String str3, String str4, Integer num, Long l) {
        super(str, str2, str3, str4, CLASS, num);
        if (l != null) {
            setStorageUsed(l);
        }
    }

    public Long getStorageUsed() {
        return (Long) getFirstPropertyValue(Property.UPNP.STORAGE_USED.class);
    }

    public StorageFolder setStorageUsed(Long l) {
        replaceFirstProperty(new Property.UPNP.STORAGE_USED(l));
        return this;
    }
}
