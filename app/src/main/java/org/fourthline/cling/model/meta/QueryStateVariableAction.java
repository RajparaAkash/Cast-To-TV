package org.fourthline.cling.model.meta;

import org.fourthline.cling.model.ValidationError;

import java.util.Collections;
import java.util.List;


public class QueryStateVariableAction<S extends Service> extends Action<S> {
    public static final String ACTION_NAME = "QueryStateVariable";
    public static final String INPUT_ARG_VAR_NAME = "varName";
    public static final String OUTPUT_ARG_RETURN = "return";
    public static final String VIRTUAL_STATEVARIABLE_INPUT = "VirtualQueryActionInput";
    public static final String VIRTUAL_STATEVARIABLE_OUTPUT = "VirtualQueryActionOutput";

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    public QueryStateVariableAction() {
        this(null);
    }

    public QueryStateVariableAction(S s) {
        super(ACTION_NAME, new ActionArgument[]{new ActionArgument(INPUT_ARG_VAR_NAME, VIRTUAL_STATEVARIABLE_INPUT, ActionArgument.Direction.IN), new ActionArgument(OUTPUT_ARG_RETURN, VIRTUAL_STATEVARIABLE_OUTPUT, ActionArgument.Direction.OUT)});
        setService(s);
    }

    @Override
    public List<ValidationError> validate() {
        return Collections.EMPTY_LIST;
    }
}
