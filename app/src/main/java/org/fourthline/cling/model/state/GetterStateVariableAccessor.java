package org.fourthline.cling.model.state;

import org.seamless.util.Reflections;

import java.lang.reflect.Method;


public class GetterStateVariableAccessor extends StateVariableAccessor {
    private Method getter;

    public GetterStateVariableAccessor(Method method) {
        this.getter = method;
    }

    public Method getGetter() {
        return this.getter;
    }

    @Override
    public Class<?> getReturnType() {
        return getGetter().getReturnType();
    }

    @Override
    public Object read(Object obj) throws Exception {
        return Reflections.invoke(getGetter(), obj, new Object[0]);
    }

    @Override
    public String toString() {
        return super.toString() + " Method: " + getGetter();
    }
}
