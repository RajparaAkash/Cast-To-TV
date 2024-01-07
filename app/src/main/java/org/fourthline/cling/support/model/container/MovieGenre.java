package org.fourthline.cling.support.model.container;


public class MovieGenre extends GenreContainer {
    public static final Class CLASS = new Class("object.container.genre.movieGenre");

    public MovieGenre() {
        setClazz(CLASS);
    }

    public MovieGenre(Container container) {
        super(container);
    }

    public MovieGenre(String str, Container container, String str2, String str3, Integer num) {
        this(str, container.getId(), str2, str3, num);
    }

    public MovieGenre(String str, String str2, String str3, String str4, Integer num) {
        super(str, str2, str3, str4, num);
        setClazz(CLASS);
    }
}
