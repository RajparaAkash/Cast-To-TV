package org.fourthline.cling.support.model.item;

import org.fourthline.cling.support.model.Person;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.container.Container;

import java.util.Arrays;
import java.util.List;


public class ImageItem extends Item {
    public static final Class CLASS = new Class("object.item.imageItem");

    public ImageItem() {
        setClazz(CLASS);
    }

    public ImageItem(Item item) {
        super(item);
    }

    public ImageItem(String str, Container container, String str2, String str3, Res... resArr) {
        this(str, container.getId(), str2, str3, resArr);
    }

    public ImageItem(String str, String str2, String str3, String str4, Res... resArr) {
        super(str, str2, str3, str4, CLASS);
        if (resArr != null) {
            getResources().addAll(Arrays.asList(resArr));
        }
    }

    public String getDescription() {
        return (String) getFirstPropertyValue(Property.DC.DESCRIPTION.class);
    }

    public ImageItem setDescription(String str) {
        replaceFirstProperty(new Property.DC.DESCRIPTION(str));
        return this;
    }

    public String getLongDescription() {
        return (String) getFirstPropertyValue(Property.UPNP.LONG_DESCRIPTION.class);
    }

    public ImageItem setLongDescription(String str) {
        replaceFirstProperty(new Property.UPNP.LONG_DESCRIPTION(str));
        return this;
    }

    public Person getFirstPublisher() {
        return (Person) getFirstPropertyValue(Property.DC.PUBLISHER.class);
    }

    public Person[] getPublishers() {
        List propertyValues = getPropertyValues(Property.DC.PUBLISHER.class);
        return (Person[]) propertyValues.toArray(new Person[propertyValues.size()]);
    }

    public ImageItem setPublishers(Person[] personArr) {
        removeProperties(Property.DC.PUBLISHER.class);
        for (Person person : personArr) {
            addProperty(new Property.DC.PUBLISHER(person));
        }
        return this;
    }

    public StorageMedium getStorageMedium() {
        return (StorageMedium) getFirstPropertyValue(Property.UPNP.STORAGE_MEDIUM.class);
    }

    public ImageItem setStorageMedium(StorageMedium storageMedium) {
        replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }

    public String getRating() {
        return (String) getFirstPropertyValue(Property.UPNP.RATING.class);
    }

    public ImageItem setRating(String str) {
        replaceFirstProperty(new Property.UPNP.RATING(str));
        return this;
    }

    public String getDate() {
        return (String) getFirstPropertyValue(Property.DC.DATE.class);
    }

    public ImageItem setDate(String str) {
        replaceFirstProperty(new Property.DC.DATE(str));
        return this;
    }

    public String getFirstRights() {
        return (String) getFirstPropertyValue(Property.DC.RIGHTS.class);
    }

    public String[] getRights() {
        List propertyValues = getPropertyValues(Property.DC.RIGHTS.class);
        return (String[]) propertyValues.toArray(new String[propertyValues.size()]);
    }

    public ImageItem setRights(String[] strArr) {
        removeProperties(Property.DC.RIGHTS.class);
        for (String str : strArr) {
            addProperty(new Property.DC.RIGHTS(str));
        }
        return this;
    }
}
