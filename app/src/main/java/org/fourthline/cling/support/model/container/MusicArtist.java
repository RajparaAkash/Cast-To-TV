package org.fourthline.cling.support.model.container;

import java.net.URI;
import java.util.List;


public class MusicArtist extends PersonContainer {
    public static final Class CLASS = new Class("object.container.person.musicArtist");

    public MusicArtist() {
        setClazz(CLASS);
    }

    public MusicArtist(Container container) {
        super(container);
    }

    public MusicArtist(String str, Container container, String str2, String str3, Integer num) {
        this(str, container.getId(), str2, str3, num);
    }

    public MusicArtist(String str, String str2, String str3, String str4, Integer num) {
        super(str, str2, str3, str4, num);
        setClazz(CLASS);
    }

    public String getFirstGenre() {
        return (String) getFirstPropertyValue(Property.UPNP.GENRE.class);
    }

    public String[] getGenres() {
        List propertyValues = getPropertyValues(Property.UPNP.GENRE.class);
        return (String[]) propertyValues.toArray(new String[propertyValues.size()]);
    }

    public MusicArtist setGenres(String[] strArr) {
        removeProperties(Property.UPNP.GENRE.class);
        for (String str : strArr) {
            addProperty(new Property.UPNP.GENRE(str));
        }
        return this;
    }

    public URI getArtistDiscographyURI() {
        return (URI) getFirstPropertyValue(Property.UPNP.ARTIST_DISCO_URI.class);
    }

    public MusicArtist setArtistDiscographyURI(URI uri) {
        replaceFirstProperty(new Property.UPNP.ARTIST_DISCO_URI(uri));
        return this;
    }
}
