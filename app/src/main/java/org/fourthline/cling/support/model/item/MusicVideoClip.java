package org.fourthline.cling.support.model.item;

import org.fourthline.cling.support.model.Person;
import org.fourthline.cling.support.model.PersonWithRole;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.container.Container;

import java.util.List;


public class MusicVideoClip extends VideoItem {
    public static final Class CLASS = new Class("object.item.videoItem.musicVideoClip");

    public MusicVideoClip() {
        setClazz(CLASS);
    }

    public MusicVideoClip(Item item) {
        super(item);
    }

    public MusicVideoClip(String str, Container container, String str2, String str3, Res... resArr) {
        this(str, container.getId(), str2, str3, resArr);
    }

    public MusicVideoClip(String str, String str2, String str3, String str4, Res... resArr) {
        super(str, str2, str3, str4, resArr);
        setClazz(CLASS);
    }

    public PersonWithRole getFirstArtist() {
        return (PersonWithRole) getFirstPropertyValue(Property.UPNP.ARTIST.class);
    }

    public PersonWithRole[] getArtists() {
        List propertyValues = getPropertyValues(Property.UPNP.ARTIST.class);
        return (PersonWithRole[]) propertyValues.toArray(new PersonWithRole[propertyValues.size()]);
    }

    public MusicVideoClip setArtists(PersonWithRole[] personWithRoleArr) {
        removeProperties(Property.UPNP.ARTIST.class);
        for (PersonWithRole personWithRole : personWithRoleArr) {
            addProperty(new Property.UPNP.ARTIST(personWithRole));
        }
        return this;
    }

    public StorageMedium getStorageMedium() {
        return (StorageMedium) getFirstPropertyValue(Property.UPNP.STORAGE_MEDIUM.class);
    }

    public MusicVideoClip setStorageMedium(StorageMedium storageMedium) {
        replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }

    public String getAlbum() {
        return (String) getFirstPropertyValue(Property.UPNP.ALBUM.class);
    }

    public MusicVideoClip setAlbum(String str) {
        replaceFirstProperty(new Property.UPNP.ALBUM(str));
        return this;
    }

    public String getFirstScheduledStartTime() {
        return (String) getFirstPropertyValue(Property.UPNP.SCHEDULED_START_TIME.class);
    }

    public String[] getScheduledStartTimes() {
        List propertyValues = getPropertyValues(Property.UPNP.SCHEDULED_START_TIME.class);
        return (String[]) propertyValues.toArray(new String[propertyValues.size()]);
    }

    public MusicVideoClip setScheduledStartTimes(String[] strArr) {
        removeProperties(Property.UPNP.SCHEDULED_START_TIME.class);
        for (String str : strArr) {
            addProperty(new Property.UPNP.SCHEDULED_START_TIME(str));
        }
        return this;
    }

    public String getFirstScheduledEndTime() {
        return (String) getFirstPropertyValue(Property.UPNP.SCHEDULED_END_TIME.class);
    }

    public String[] getScheduledEndTimes() {
        List propertyValues = getPropertyValues(Property.UPNP.SCHEDULED_END_TIME.class);
        return (String[]) propertyValues.toArray(new String[propertyValues.size()]);
    }

    public MusicVideoClip setScheduledEndTimes(String[] strArr) {
        removeProperties(Property.UPNP.SCHEDULED_END_TIME.class);
        for (String str : strArr) {
            addProperty(new Property.UPNP.SCHEDULED_END_TIME(str));
        }
        return this;
    }

    public Person getFirstContributor() {
        return (Person) getFirstPropertyValue(Property.DC.CONTRIBUTOR.class);
    }

    public Person[] getContributors() {
        List propertyValues = getPropertyValues(Property.DC.CONTRIBUTOR.class);
        return (Person[]) propertyValues.toArray(new Person[propertyValues.size()]);
    }

    public MusicVideoClip setContributors(Person[] personArr) {
        removeProperties(Property.DC.CONTRIBUTOR.class);
        for (Person person : personArr) {
            addProperty(new Property.DC.CONTRIBUTOR(person));
        }
        return this;
    }

    public String getDate() {
        return (String) getFirstPropertyValue(Property.DC.DATE.class);
    }

    public MusicVideoClip setDate(String str) {
        replaceFirstProperty(new Property.DC.DATE(str));
        return this;
    }
}
