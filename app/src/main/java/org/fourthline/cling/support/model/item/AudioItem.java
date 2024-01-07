package org.fourthline.cling.support.model.item;

import org.fourthline.cling.support.model.Person;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;

import java.net.URI;
import java.util.Arrays;
import java.util.List;


public class AudioItem extends Item {
    public static final Class CLASS = new Class("object.item.audioItem");

    public AudioItem() {
        setClazz(CLASS);
    }

    public AudioItem(Item item) {
        super(item);
    }

    public AudioItem(String str, Container container, String str2, String str3, Res... resArr) {
        this(str, container.getId(), str2, str3, resArr);
    }

    public AudioItem(String str, String str2, String str3, String str4, Res... resArr) {
        super(str, str2, str3, str4, CLASS);
        if (resArr != null) {
            getResources().addAll(Arrays.asList(resArr));
        }
    }

    public String getFirstGenre() {
        return (String) getFirstPropertyValue(Property.UPNP.GENRE.class);
    }

    public String[] getGenres() {
        List propertyValues = getPropertyValues(Property.UPNP.GENRE.class);
        return (String[]) propertyValues.toArray(new String[propertyValues.size()]);
    }

    public AudioItem setGenres(String[] strArr) {
        removeProperties(Property.UPNP.GENRE.class);
        for (String str : strArr) {
            addProperty(new Property.UPNP.GENRE(str));
        }
        return this;
    }

    public String getDescription() {
        return (String) getFirstPropertyValue(Property.DC.DESCRIPTION.class);
    }

    public AudioItem setDescription(String str) {
        replaceFirstProperty(new Property.DC.DESCRIPTION(str));
        return this;
    }

    public String getLongDescription() {
        return (String) getFirstPropertyValue(Property.UPNP.LONG_DESCRIPTION.class);
    }

    public AudioItem setLongDescription(String str) {
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

    public AudioItem setPublishers(Person[] personArr) {
        removeProperties(Property.DC.PUBLISHER.class);
        for (Person person : personArr) {
            addProperty(new Property.DC.PUBLISHER(person));
        }
        return this;
    }

    public URI getFirstRelation() {
        return (URI) getFirstPropertyValue(Property.DC.RELATION.class);
    }

    public URI[] getRelations() {
        List propertyValues = getPropertyValues(Property.DC.RELATION.class);
        return (URI[]) propertyValues.toArray(new URI[propertyValues.size()]);
    }

    public AudioItem setRelations(URI[] uriArr) {
        removeProperties(Property.DC.RELATION.class);
        for (URI uri : uriArr) {
            addProperty(new Property.DC.RELATION(uri));
        }
        return this;
    }

    public String getLanguage() {
        return (String) getFirstPropertyValue(Property.DC.LANGUAGE.class);
    }

    public AudioItem setLanguage(String str) {
        replaceFirstProperty(new Property.DC.LANGUAGE(str));
        return this;
    }

    public String getFirstRights() {
        return (String) getFirstPropertyValue(Property.DC.RIGHTS.class);
    }

    public String[] getRights() {
        List propertyValues = getPropertyValues(Property.DC.RIGHTS.class);
        return (String[]) propertyValues.toArray(new String[propertyValues.size()]);
    }

    public AudioItem setRights(String[] strArr) {
        removeProperties(Property.DC.RIGHTS.class);
        for (String str : strArr) {
            addProperty(new Property.DC.RIGHTS(str));
        }
        return this;
    }
}
