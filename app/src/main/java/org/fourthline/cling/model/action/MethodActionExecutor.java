package org.fourthline.cling.model.action;

import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.profile.RemoteClientInfo;
import org.fourthline.cling.model.state.StateVariableAccessor;
import org.fourthline.cling.model.types.ErrorCode;
import org.seamless.util.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;


public class MethodActionExecutor extends AbstractActionExecutor {
    private static Logger log = Logger.getLogger(MethodActionExecutor.class.getName());
    protected Method method;

    public MethodActionExecutor(Method method) {
        this.method = method;
    }

    public MethodActionExecutor(Map<ActionArgument<LocalService>, StateVariableAccessor> map, Method method) {
        super(map);
        this.method = method;
    }

    public Method getMethod() {
        return this.method;
    }

    @Override
    protected void execute(ActionInvocation<LocalService> actionInvocation, Object obj) throws Exception {
        Object invoke;
        boolean z;
        ActionArgument<LocalService>[] outputArguments;
        Object[] createInputArgumentValues = createInputArgumentValues(actionInvocation, this.method);
        if (!actionInvocation.getAction().hasOutputArguments()) {
            Logger logger = log;
            logger.fine("Calling local service method with no output arguments: " + this.method);
            Reflections.invoke(this.method, obj, createInputArgumentValues);
            return;
        }
        boolean equals = this.method.getReturnType().equals(Void.TYPE);
        Logger logger2 = log;
        logger2.fine("Calling local service method with output arguments: " + this.method);
        if (equals) {
            log.fine("Action method is void, calling declared accessors(s) on service instance to retrieve ouput argument(s)");
            Reflections.invoke(this.method, obj, createInputArgumentValues);
            invoke = readOutputArgumentValues(actionInvocation.getAction(), obj);
        } else if (isUseOutputArgumentAccessors(actionInvocation)) {
            log.fine("Action method is not void, calling declared accessor(s) on returned instance to retrieve ouput argument(s)");
            invoke = readOutputArgumentValues(actionInvocation.getAction(), Reflections.invoke(this.method, obj, createInputArgumentValues));
        } else {
            log.fine("Action method is not void, using returned value as (single) output argument");
            invoke = Reflections.invoke(this.method, obj, createInputArgumentValues);
            z = false;
            outputArguments = actionInvocation.getAction().getOutputArguments();
            if (!z && (invoke instanceof Object[])) {
                Object[] objArr = (Object[]) invoke;
                Logger logger3 = log;
                logger3.fine("Accessors returned Object[], setting output argument values: " + objArr.length);
                for (int i = 0; i < outputArguments.length; i++) {
                    setOutputArgumentValue(actionInvocation, outputArguments[i], objArr[i]);
                }
                return;
            } else if (outputArguments.length != 1) {
                setOutputArgumentValue(actionInvocation, outputArguments[0], invoke);
                return;
            } else {
                ErrorCode errorCode = ErrorCode.ACTION_FAILED;
                throw new ActionException(errorCode, "Method return does not match required number of output arguments: " + outputArguments.length);
            }
        }
        z = true;
        outputArguments = actionInvocation.getAction().getOutputArguments();
        if (!z) {
        }
        if (outputArguments.length != 1) {
        }
    }

    protected boolean isUseOutputArgumentAccessors(ActionInvocation<LocalService> actionInvocation) {
        for (ActionArgument<LocalService> actionArgument : actionInvocation.getAction().getOutputArguments()) {
            if (getOutputArgumentAccessors().get(actionArgument) != null) {
                return true;
            }
        }
        return false;
    }

    protected Object[] createInputArgumentValues(ActionInvocation<LocalService> actionInvocation, Method method) throws ActionException {
        LocalService service = actionInvocation.getAction().getService();
        ArrayList arrayList = new ArrayList();
        ActionArgument<LocalService>[] inputArguments = actionInvocation.getAction().getInputArguments();
        int length = inputArguments.length;
        char c = 0;
        int i = 0;
        int i2 = 0;
        while (i < length) {
            ActionArgument<LocalService> actionArgument = inputArguments[i];
            Class<?> cls = method.getParameterTypes()[i2];
            ActionArgumentValue<LocalService> input = actionInvocation.getInput(actionArgument);
            if (cls.isPrimitive() && (input == null || input.toString().length() == 0)) {
                ErrorCode errorCode = ErrorCode.ARGUMENT_VALUE_INVALID;
                throw new ActionException(errorCode, "Primitive action method argument '" + actionArgument.getName() + "' requires input value, can't be null or empty string");
            }
            if (input == null) {
                arrayList.add(i2, null);
                i2++;
            } else {
                String actionArgumentValue = input.toString();
                if (actionArgumentValue.length() > 0 && service.isStringConvertibleType((Class) cls) && !cls.isEnum()) {
                    try {
                        Class<?>[] clsArr = new Class[1];
                        clsArr[c] = String.class;
                        Constructor<?> constructor = cls.getConstructor(clsArr);
                        Logger logger = log;
                        logger.finer("Creating new input argument value instance with String.class constructor of type: " + cls);
                        Object[] objArr = {actionArgumentValue};
                        int i3 = i2 + 1;
                        arrayList.add(i2, constructor.newInstance(objArr));
                        i2 = i3;
                    } catch (Exception e) {
                        Logger logger2 = log;
                        logger2.warning("Error preparing action method call: " + method);
                        Logger logger3 = log;
                        logger3.warning("Can't convert input argument string to desired type of '" + actionArgument.getName() + "': " + e);
                        ErrorCode errorCode2 = ErrorCode.ARGUMENT_VALUE_INVALID;
                        throw new ActionException(errorCode2, "Can't convert input argument string to desired type of '" + actionArgument.getName() + "': " + e);
                    }
                } else {
                    arrayList.add(i2, input.getValue());
                    i2++;
                }
            }
            i++;
            c = 0;
        }
        if (method.getParameterTypes().length > 0 && RemoteClientInfo.class.isAssignableFrom(method.getParameterTypes()[method.getParameterTypes().length - 1])) {
            if (actionInvocation instanceof RemoteActionInvocation) {
                RemoteActionInvocation remoteActionInvocation = (RemoteActionInvocation) actionInvocation;
                if (remoteActionInvocation.getRemoteClientInfo() != null) {
                    Logger logger4 = log;
                    logger4.finer("Providing remote client info as last action method input argument: " + method);
                    arrayList.add(i2, remoteActionInvocation.getRemoteClientInfo());
                }
            }
            arrayList.add(i2, null);
        }
        return arrayList.toArray(new Object[arrayList.size()]);
    }
}
