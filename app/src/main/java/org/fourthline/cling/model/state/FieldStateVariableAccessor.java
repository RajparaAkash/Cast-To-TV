package org.fourthline.cling.model.state;

import org.seamless.util.Reflections;

import java.lang.reflect.Field;


public class FieldStateVariableAccessor extends StateVariableAccessor {
    protected Field field;

    public FieldStateVariableAccessor(Field field) {
        this.field = field;
    }

    public Field getField() {
        return this.field;
    }

    @Override
    public Class<?> getReturnType() {
        return getField().getType();
    }

    @Override
    public Object read(Object obj) throws Exception {
        return Reflections.get(this.field, obj);
    }

    @Override
    public String toString() {
        return super.toString() + " Field: " + getField();
    }
}
