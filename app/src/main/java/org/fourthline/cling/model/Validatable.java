package org.fourthline.cling.model;

import java.util.List;


public interface Validatable {
    List<ValidationError> validate();
}
