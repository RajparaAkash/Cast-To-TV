package org.fourthline.cling.model.types;

import java.lang.reflect.ParameterizedType;


public abstract class AbstractDatatype<V> implements Datatype<V> {
    private Builtin builtin;

    @Override
    public V valueOf(String str) throws InvalidValueException {
        return null;
    }

    protected Class<V> getValueType() {
        return (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public boolean isHandlingJavaType(Class cls) {
        return getValueType().isAssignableFrom(cls);
    }

    @Override
    public Builtin getBuiltin() {
        return this.builtin;
    }

    public void setBuiltin(Builtin builtin) {
        this.builtin = builtin;
    }

    @Override
    public String getString(V v) throws InvalidValueException {
        if (v == null) {
            return "";
        }
        if (!isValid(v)) {
            throw new InvalidValueException("Value is not valid: " + v);
        }
        return v.toString();
    }

    @Override
    public boolean isValid(V v) {
        return v == null || getValueType().isAssignableFrom(v.getClass());
    }

    public String toString() {
        return "(" + getClass().getSimpleName() + ")";
    }

    @Override
    public String getDisplayString() {
        if (this instanceof CustomDatatype) {
            return ((CustomDatatype) this).getName();
        }
        if (getBuiltin() != null) {
            return getBuiltin().getDescriptorName();
        }
        return getValueType().getSimpleName();
    }
}
