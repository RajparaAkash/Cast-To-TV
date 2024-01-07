package org.fourthline.cling.registry.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;


public interface Phase {

    public static final AnnotationLiteral<Alive> ALIVE = new AnnotationLiteral<Alive>() {
    };
    public static final AnnotationLiteral<Complete> COMPLETE = new AnnotationLiteral<Complete>() {
    };
    public static final AnnotationLiteral<Byebye> BYEBYE = new AnnotationLiteral<Byebye>() {
    };
    public static final AnnotationLiteral<Updated> UPDATED = new AnnotationLiteral<Updated>() {
    };

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)

    public @interface Alive {
    }

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)

    public @interface Byebye {
    }

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)

    public @interface Complete {
    }

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)

    public @interface Updated {
    }
}
