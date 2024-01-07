package org.fourthline.cling.support.model.container;

import org.fourthline.cling.support.model.Person;
import org.fourthline.cling.support.model.StorageMedium;

import java.net.URI;
import java.util.List;


public class Album extends Container {
    public static final Class CLASS = new Class("object.container.album");

    public Album() {
        setClazz(CLASS);
    }

    public Album(Container container) {
        super(container);
    }

    public Album(String str, Container container, String str2, String str3, Integer num) {
        this(str, container.getId(), str2, str3, num);
    }

    public Album(String str, String str2, String str3, String str4, Integer num) {
        super(str, str2, str3, str4, CLASS, num);
    }

    public String getDescription() {
        return (String) getFirstPropertyValue(Property.DC.DESCRIPTION.class);
    }

    public Album setDescription(String str) {
        replaceFirstProperty(new Property.DC.DESCRIPTION(str));
        return this;
    }

    public String getLongDescription() {
        return (String) getFirstPropertyValue(Property.UPNP.LONG_DESCRIPTION.class);
    }

    public Album setLongDescription(String str) {
        replaceFirstProperty(new Property.UPNP.LONG_DESCRIPTION(str));
        return this;
    }

    public StorageMedium getStorageMedium() {
        return (StorageMedium) getFirstPropertyValue(Property.UPNP.STORAGE_MEDIUM.class);
    }

    public Album setStorageMedium(StorageMedium storageMedium) {
        replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }

    public String getDate() {
        return (String) getFirstPropertyValue(Property.DC.DATE.class);
    }

    public Album setDate(String str) {
        replaceFirstProperty(new Property.DC.DATE(str));
        return this;
    }

    public URI getFirstRelation() {
        return (URI) getFirstPropertyValue(Property.DC.RELATION.class);
    }

    public URI[] getRelations() {
        List propertyValues = getPropertyValues(Property.DC.RELATION.class);
        return (URI[]) propertyValues.toArray(new URI[propertyValues.size()]);
    }

    public Album setRelations(URI[] uriArr) {
        removeProperties(Property.DC.RELATION.class);
        for (URI uri : uriArr) {
            addProperty(new Property.DC.RELATION(uri));
        }
        return this;
    }

    public String getFirstRights() {
        return (String) getFirstPropertyValue(Property.DC.RIGHTS.class);
    }

    public String[] getRights() {
        List propertyValues = getPropertyValues(Property.DC.RIGHTS.class);
        return (String[]) propertyValues.toArray(new String[propertyValues.size()]);
    }

    public Album setRights(String[] strArr) {
        removeProperties(Property.DC.RIGHTS.class);
        for (String str : strArr) {
            addProperty(new Property.DC.RIGHTS(str));
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

    public Album setContributors(Person[] personArr) {
        removeProperties(Property.DC.CONTRIBUTOR.class);
        for (Person person : personArr) {
            addProperty(new Property.DC.CONTRIBUTOR(person));
        }
        return this;
    }

    public Person getFirstPublisher() {
        return (Person) getFirstPropertyValue(Property.DC.PUBLISHER.class);
    }

    public Person[] getPublishers() {
        List propertyValues = getPropertyValues(Property.DC.PUBLISHER.class);
        return (Person[]) propertyValues.toArray(new Person[propertyValues.size()]);
    }

    public Album setPublishers(Person[] personArr) {
        removeProperties(Property.DC.PUBLISHER.class);
        for (Person person : personArr) {
            addProperty(new Property.DC.PUBLISHER(person));
        }
        return this;
    }
}
