package org.fourthline.cling.support.model.item;

import org.fourthline.cling.support.model.Person;
import org.fourthline.cling.support.model.PersonWithRole;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;

import java.net.URI;
import java.util.Arrays;
import java.util.List;


public class VideoItem extends Item {
    public static final Class CLASS = new Class("object.item.videoItem");

    public VideoItem() {
        setClazz(CLASS);
    }

    public VideoItem(Item item) {
        super(item);
    }

    public VideoItem(String str, Container container, String str2, String str3, Res... resArr) {
        this(str, container.getId(), str2, str3, resArr);
    }

    public VideoItem(String str, String str2, String str3, String str4, Res... resArr) {
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

    public VideoItem setGenres(String[] strArr) {
        removeProperties(Property.UPNP.GENRE.class);
        for (String str : strArr) {
            addProperty(new Property.UPNP.GENRE(str));
        }
        return this;
    }

    public String getDescription() {
        return (String) getFirstPropertyValue(Property.DC.DESCRIPTION.class);
    }

    public VideoItem setDescription(String str) {
        replaceFirstProperty(new Property.DC.DESCRIPTION(str));
        return this;
    }

    public String getLongDescription() {
        return (String) getFirstPropertyValue(Property.UPNP.LONG_DESCRIPTION.class);
    }

    public VideoItem setLongDescription(String str) {
        replaceFirstProperty(new Property.UPNP.LONG_DESCRIPTION(str));
        return this;
    }

    public Person getFirstProducer() {
        return (Person) getFirstPropertyValue(Property.UPNP.PRODUCER.class);
    }

    public Person[] getProducers() {
        List propertyValues = getPropertyValues(Property.UPNP.PRODUCER.class);
        return (Person[]) propertyValues.toArray(new Person[propertyValues.size()]);
    }

    public VideoItem setProducers(Person[] personArr) {
        removeProperties(Property.UPNP.PRODUCER.class);
        for (Person person : personArr) {
            addProperty(new Property.UPNP.PRODUCER(person));
        }
        return this;
    }

    public String getRating() {
        return (String) getFirstPropertyValue(Property.UPNP.RATING.class);
    }

    public VideoItem setRating(String str) {
        replaceFirstProperty(new Property.UPNP.RATING(str));
        return this;
    }

    public PersonWithRole getFirstActor() {
        return (PersonWithRole) getFirstPropertyValue(Property.UPNP.ACTOR.class);
    }

    public PersonWithRole[] getActors() {
        List propertyValues = getPropertyValues(Property.UPNP.ACTOR.class);
        return (PersonWithRole[]) propertyValues.toArray(new PersonWithRole[propertyValues.size()]);
    }

    public VideoItem setActors(PersonWithRole[] personWithRoleArr) {
        removeProperties(Property.UPNP.ACTOR.class);
        for (PersonWithRole personWithRole : personWithRoleArr) {
            addProperty(new Property.UPNP.ACTOR(personWithRole));
        }
        return this;
    }

    public Person getFirstDirector() {
        return (Person) getFirstPropertyValue(Property.UPNP.DIRECTOR.class);
    }

    public Person[] getDirectors() {
        List propertyValues = getPropertyValues(Property.UPNP.DIRECTOR.class);
        return (Person[]) propertyValues.toArray(new Person[propertyValues.size()]);
    }

    public VideoItem setDirectors(Person[] personArr) {
        removeProperties(Property.UPNP.DIRECTOR.class);
        for (Person person : personArr) {
            addProperty(new Property.UPNP.DIRECTOR(person));
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

    public VideoItem setPublishers(Person[] personArr) {
        removeProperties(Property.DC.PUBLISHER.class);
        for (Person person : personArr) {
            addProperty(new Property.DC.PUBLISHER(person));
        }
        return this;
    }

    public String getLanguage() {
        return (String) getFirstPropertyValue(Property.DC.LANGUAGE.class);
    }

    public VideoItem setLanguage(String str) {
        replaceFirstProperty(new Property.DC.LANGUAGE(str));
        return this;
    }

    public URI getFirstRelation() {
        return (URI) getFirstPropertyValue(Property.DC.RELATION.class);
    }

    public URI[] getRelations() {
        List propertyValues = getPropertyValues(Property.DC.RELATION.class);
        return (URI[]) propertyValues.toArray(new URI[propertyValues.size()]);
    }

    public VideoItem setRelations(URI[] uriArr) {
        removeProperties(Property.DC.RELATION.class);
        for (URI uri : uriArr) {
            addProperty(new Property.DC.RELATION(uri));
        }
        return this;
    }
}
