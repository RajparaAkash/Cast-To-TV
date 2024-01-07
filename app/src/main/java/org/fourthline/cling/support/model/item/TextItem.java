package org.fourthline.cling.support.model.item;

import org.fourthline.cling.support.model.Person;
import org.fourthline.cling.support.model.PersonWithRole;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.container.Container;

import java.net.URI;
import java.util.Arrays;
import java.util.List;


public class TextItem extends Item {
    public static final Class CLASS = new Class("object.item.textItem");

    public TextItem() {
        setClazz(CLASS);
    }

    public TextItem(Item item) {
        super(item);
    }

    public TextItem(String str, Container container, String str2, String str3, Res... resArr) {
        this(str, container.getId(), str2, str3, resArr);
    }

    public TextItem(String str, String str2, String str3, String str4, Res... resArr) {
        super(str, str2, str3, str4, CLASS);
        if (resArr != null) {
            getResources().addAll(Arrays.asList(resArr));
        }
    }

    public PersonWithRole getFirstAuthor() {
        return (PersonWithRole) getFirstPropertyValue(Property.UPNP.AUTHOR.class);
    }

    public PersonWithRole[] getAuthors() {
        List propertyValues = getPropertyValues(Property.UPNP.AUTHOR.class);
        return (PersonWithRole[]) propertyValues.toArray(new PersonWithRole[propertyValues.size()]);
    }

    public TextItem setAuthors(PersonWithRole[] personWithRoleArr) {
        removeProperties(Property.UPNP.AUTHOR.class);
        for (PersonWithRole personWithRole : personWithRoleArr) {
            addProperty(new Property.UPNP.AUTHOR(personWithRole));
        }
        return this;
    }

    public String getDescription() {
        return (String) getFirstPropertyValue(Property.DC.DESCRIPTION.class);
    }

    public TextItem setDescription(String str) {
        replaceFirstProperty(new Property.DC.DESCRIPTION(str));
        return this;
    }

    public String getLongDescription() {
        return (String) getFirstPropertyValue(Property.UPNP.LONG_DESCRIPTION.class);
    }

    public TextItem setLongDescription(String str) {
        replaceFirstProperty(new Property.UPNP.LONG_DESCRIPTION(str));
        return this;
    }

    public String getLanguage() {
        return (String) getFirstPropertyValue(Property.DC.LANGUAGE.class);
    }

    public TextItem setLanguage(String str) {
        replaceFirstProperty(new Property.DC.LANGUAGE(str));
        return this;
    }

    public StorageMedium getStorageMedium() {
        return (StorageMedium) getFirstPropertyValue(Property.UPNP.STORAGE_MEDIUM.class);
    }

    public TextItem setStorageMedium(StorageMedium storageMedium) {
        replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }

    public String getDate() {
        return (String) getFirstPropertyValue(Property.DC.DATE.class);
    }

    public TextItem setDate(String str) {
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

    public TextItem setRelations(URI[] uriArr) {
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

    public TextItem setRights(String[] strArr) {
        removeProperties(Property.DC.RIGHTS.class);
        for (String str : strArr) {
            addProperty(new Property.DC.RIGHTS(str));
        }
        return this;
    }

    public String getRating() {
        return (String) getFirstPropertyValue(Property.UPNP.RATING.class);
    }

    public TextItem setRating(String str) {
        replaceFirstProperty(new Property.UPNP.RATING(str));
        return this;
    }

    public Person getFirstContributor() {
        return (Person) getFirstPropertyValue(Property.DC.CONTRIBUTOR.class);
    }

    public Person[] getContributors() {
        List propertyValues = getPropertyValues(Property.DC.CONTRIBUTOR.class);
        return (Person[]) propertyValues.toArray(new Person[propertyValues.size()]);
    }

    public TextItem setContributors(Person[] personArr) {
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

    public TextItem setPublishers(Person[] personArr) {
        removeProperties(Property.DC.PUBLISHER.class);
        for (Person person : personArr) {
            addProperty(new Property.DC.PUBLISHER(person));
        }
        return this;
    }
}
