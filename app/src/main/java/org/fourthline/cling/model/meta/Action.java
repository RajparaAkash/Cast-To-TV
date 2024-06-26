package org.fourthline.cling.model.meta;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.Validatable;
import org.fourthline.cling.model.ValidationError;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class Action<S extends Service> implements Validatable {
    private static final Logger log = Logger.getLogger(Action.class.getName());
    private final ActionArgument[] arguments;
    private final ActionArgument[] inputArguments;
    private final String name;
    private final ActionArgument[] outputArguments;
    private S service;

    public Action(String str, ActionArgument[] actionArgumentArr) {
        this.name = str;
        if (actionArgumentArr != null) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            for (ActionArgument actionArgument : actionArgumentArr) {
                actionArgument.setAction(this);
                if (actionArgument.getDirection().equals(ActionArgument.Direction.IN)) {
                    arrayList.add(actionArgument);
                }
                if (actionArgument.getDirection().equals(ActionArgument.Direction.OUT)) {
                    arrayList2.add(actionArgument);
                }
            }
            this.arguments = actionArgumentArr;
            this.inputArguments = (ActionArgument[]) arrayList.toArray(new ActionArgument[arrayList.size()]);
            this.outputArguments = (ActionArgument[]) arrayList2.toArray(new ActionArgument[arrayList2.size()]);
            return;
        }
        this.arguments = new ActionArgument[0];
        this.inputArguments = new ActionArgument[0];
        this.outputArguments = new ActionArgument[0];
    }

    public String getName() {
        return this.name;
    }

    public boolean hasArguments() {
        return getArguments() != null && getArguments().length > 0;
    }

    public ActionArgument[] getArguments() {
        return this.arguments;
    }

    public S getService() {
        return this.service;
    }

    
    public void setService(S s) {
        if (this.service != null) {
            throw new IllegalStateException("Final value has been set already, model is immutable");
        }
        this.service = s;
    }

    public ActionArgument<S> getFirstInputArgument() {
        if (!hasInputArguments()) {
            throw new IllegalStateException("No input arguments: " + this);
        }
        return getInputArguments()[0];
    }

    public ActionArgument<S> getFirstOutputArgument() {
        if (!hasOutputArguments()) {
            throw new IllegalStateException("No output arguments: " + this);
        }
        return getOutputArguments()[0];
    }

    public ActionArgument<S>[] getInputArguments() {
        return this.inputArguments;
    }

    public ActionArgument<S> getInputArgument(String str) {
        ActionArgument<S>[] inputArguments;
        for (ActionArgument<S> actionArgument : getInputArguments()) {
            if (actionArgument.isNameOrAlias(str)) {
                return actionArgument;
            }
        }
        return null;
    }

    public ActionArgument<S>[] getOutputArguments() {
        return this.outputArguments;
    }

    public ActionArgument<S> getOutputArgument(String str) {
        ActionArgument<S>[] outputArguments;
        for (ActionArgument<S> actionArgument : getOutputArguments()) {
            if (actionArgument.getName().equals(str)) {
                return actionArgument;
            }
        }
        return null;
    }

    public boolean hasInputArguments() {
        return getInputArguments() != null && getInputArguments().length > 0;
    }

    public boolean hasOutputArguments() {
        return getOutputArguments() != null && getOutputArguments().length > 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(getClass().getSimpleName());
        sb.append(", Arguments: ");
        sb.append(getArguments() != null ? Integer.valueOf(getArguments().length) : "NO ARGS");
        sb.append(") ");
        sb.append(getName());
        return sb.toString();
    }

    @Override
    public List<ValidationError> validate() {
        ActionArgument[] arguments;
        ActionArgument[] arguments2;
        ArrayList arrayList = new ArrayList();
        if (getName() == null || getName().length() == 0) {
            arrayList.add(new ValidationError(getClass(), "name", "Action without name of: " + getService()));
        } else if (!ModelUtil.isValidUDAName(getName())) {
            Logger logger = log;
            logger.warning("UPnP specification violation of: " + getService().getDevice());
            logger.warning("Invalid action name: " + this);
        }
        for (ActionArgument actionArgument : getArguments()) {
            if (getService().getStateVariable(actionArgument.getRelatedStateVariableName()) == null) {
                arrayList.add(new ValidationError(getClass(), "arguments", "Action argument references an unknown state variable: " + actionArgument.getRelatedStateVariableName()));
            }
        }
        ActionArgument actionArgument2 = null;
        int i = 0;
        int i2 = 0;
        for (ActionArgument actionArgument3 : getArguments()) {
            if (actionArgument3.isReturnValue()) {
                if (actionArgument3.getDirection() == ActionArgument.Direction.IN) {
                    Logger logger2 = log;
                    logger2.warning("UPnP specification violation of :" + getService().getDevice());
                    logger2.warning("Input argument can not have <retval/>");
                } else {
                    if (actionArgument2 != null) {
                        Logger logger3 = log;
                        logger3.warning("UPnP specification violation of: " + getService().getDevice());
                        logger3.warning("Only one argument of action '" + getName() + "' can be <retval/>");
                    }
                    i2 = i;
                    actionArgument2 = actionArgument3;
                }
            }
            i++;
        }
        if (actionArgument2 != null) {
            for (int i3 = 0; i3 < i2; i3++) {
                if (getArguments()[i3].getDirection() == ActionArgument.Direction.OUT) {
                    Logger logger4 = log;
                    logger4.warning("UPnP specification violation of: " + getService().getDevice());
                    logger4.warning("Argument '" + actionArgument2.getName() + "' of action '" + getName() + "' is <retval/> but not the first OUT argument");
                }
            }
        }
        for (ActionArgument actionArgument4 : this.arguments) {
            arrayList.addAll(actionArgument4.validate());
        }
        return arrayList;
    }

    public Action<S> deepCopy() {
        ActionArgument[] actionArgumentArr = new ActionArgument[getArguments().length];
        for (int i = 0; i < getArguments().length; i++) {
            actionArgumentArr[i] = getArguments()[i].deepCopy();
        }
        return new Action<>(getName(), actionArgumentArr);
    }
}
