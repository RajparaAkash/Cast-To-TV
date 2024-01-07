package org.fourthline.cling.model.action;

import org.fourthline.cling.model.VariableValue;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.types.InvalidValueException;


public class ActionArgumentValue<S extends Service> extends VariableValue {

    final private ActionArgument<S> argument;

    public ActionArgumentValue(ActionArgument<S> argument, Object value) throws InvalidValueException {
        super(argument.getDatatype(), value != null && value.getClass().isEnum() ? value.toString() : value);
        this.argument = argument;
    }

    public ActionArgument<S> getArgument() {
        return argument;
    }

}