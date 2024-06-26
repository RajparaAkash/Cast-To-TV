package org.fourthline.cling.support.model.item;

import org.fourthline.cling.support.model.PersonWithRole;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.container.Container;

import java.util.Arrays;
import java.util.List;


public class PlaylistItem extends Item {
    public static final Class CLASS = new Class("object.item.playlistItem");

    public PlaylistItem() {
        setClazz(CLASS);
    }

    public PlaylistItem(Item item) {
        super(item);
    }

    public PlaylistItem(String str, Container container, String str2, String str3, Res... resArr) {
        this(str, container.getId(), str2, str3, resArr);
    }

    public PlaylistItem(String str, String str2, String str3, String str4, Res... resArr) {
        super(str, str2, str3, str4, CLASS);
        if (resArr != null) {
            getResources().addAll(Arrays.asList(resArr));
        }
    }

    public PersonWithRole getFirstArtist() {
        return (PersonWithRole) getFirstPropertyValue(Property.UPNP.ARTIST.class);
    }

    public PersonWithRole[] getArtists() {
        List propertyValues = getPropertyValues(Property.UPNP.ARTIST.class);
        return (PersonWithRole[]) propertyValues.toArray(new PersonWithRole[propertyValues.size()]);
    }

    public PlaylistItem setArtists(PersonWithRole[] personWithRoleArr) {
        removeProperties(Property.UPNP.ARTIST.class);
        for (PersonWithRole personWithRole : personWithRoleArr) {
            addProperty(new Property.UPNP.ARTIST(personWithRole));
        }
        return this;
    }

    public String getFirstGenre() {
        return (String) getFirstPropertyValue(Property.UPNP.GENRE.class);
    }

    public String[] getGenres() {
        List propertyValues = getPropertyValues(Property.UPNP.GENRE.class);
        return (String[]) propertyValues.toArray(new String[propertyValues.size()]);
    }

    public PlaylistItem setGenres(String[] strArr) {
        removeProperties(Property.UPNP.GENRE.class);
        for (String str : strArr) {
            addProperty(new Property.UPNP.GENRE(str));
        }
        return this;
    }

    public String getDescription() {
        return (String) getFirstPropertyValue(Property.DC.DESCRIPTION.class);
    }

    public PlaylistItem setDescription(String str) {
        replaceFirstProperty(new Property.DC.DESCRIPTION(str));
        return this;
    }

    public String getLongDescription() {
        return (String) getFirstPropertyValue(Property.UPNP.LONG_DESCRIPTION.class);
    }

    public PlaylistItem setLongDescription(String str) {
        replaceFirstProperty(new Property.UPNP.LONG_DESCRIPTION(str));
        return this;
    }

    public String getLanguage() {
        return (String) getFirstPropertyValue(Property.DC.LANGUAGE.class);
    }

    public PlaylistItem setLanguage(String str) {
        replaceFirstProperty(new Property.DC.LANGUAGE(str));
        return this;
    }

    public StorageMedium getStorageMedium() {
        return (StorageMedium) getFirstPropertyValue(Property.UPNP.STORAGE_MEDIUM.class);
    }

    public PlaylistItem setStorageMedium(StorageMedium storageMedium) {
        replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }

    public String getDate() {
        return (String) getFirstPropertyValue(Property.DC.DATE.class);
    }

    public PlaylistItem setDate(String str) {
        replaceFirstProperty(new Property.DC.DATE(str));
        return this;
    }
}
