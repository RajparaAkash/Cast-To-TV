package org.fourthline.cling.support.model.container;


public class MusicGenre extends GenreContainer {
    public static final Class CLASS = new Class("object.container.genre.musicGenre");

    public MusicGenre() {
        setClazz(CLASS);
    }

    public MusicGenre(Container container) {
        super(container);
    }

    public MusicGenre(String str, Container container, String str2, String str3, Integer num) {
        this(str, container.getId(), str2, str3, num);
    }

    public MusicGenre(String str, String str2, String str3, String str4, Integer num) {
        super(str, str2, str3, str4, num);
        setClazz(CLASS);
    }
}
