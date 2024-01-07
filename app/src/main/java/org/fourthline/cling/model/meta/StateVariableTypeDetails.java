package org.fourthline.cling.model.meta;

import org.fourthline.cling.model.Validatable;
import org.fourthline.cling.model.ValidationError;
import org.fourthline.cling.model.types.Datatype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


public class StateVariableTypeDetails implements Validatable {
    private static final Logger log = Logger.getLogger(StateVariableTypeDetails.class.getName());
    private final StateVariableAllowedValueRange allowedValueRange;
    private final String[] allowedValues;
    private final Datatype datatype;
    private final String defaultValue;

    public StateVariableTypeDetails(Datatype datatype) {
        this(datatype, null, null, null);
    }

    public StateVariableTypeDetails(Datatype datatype, String str) {
        this(datatype, str, null, null);
    }

    public StateVariableTypeDetails(Datatype datatype, String str, String[] strArr, StateVariableAllowedValueRange stateVariableAllowedValueRange) {
        this.datatype = datatype;
        this.defaultValue = str;
        this.allowedValues = strArr;
        this.allowedValueRange = stateVariableAllowedValueRange;
    }

    public Datatype getDatatype() {
        return this.datatype;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public String[] getAllowedValues() {
        if (!foundDefaultInAllowedValues(this.defaultValue, this.allowedValues)) {
            ArrayList arrayList = new ArrayList(Arrays.asList(this.allowedValues));
            arrayList.add(getDefaultValue());
            return (String[]) arrayList.toArray(new String[arrayList.size()]);
        }
        return this.allowedValues;
    }

    public StateVariableAllowedValueRange getAllowedValueRange() {
        return this.allowedValueRange;
    }

    protected boolean foundDefaultInAllowedValues(String str, String[] strArr) {
        if (str == null || strArr == null) {
            return true;
        }
        for (String str2 : strArr) {
            if (str2.equals(str)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ValidationError> validate() {
        String[] allowedValues;
        ArrayList arrayList = new ArrayList();
        if (getDatatype() == null) {
            arrayList.add(new ValidationError(getClass(), "datatype", "Service state variable has no datatype"));
        }
        if (getAllowedValues() != null) {
            if (getAllowedValueRange() != null) {
                arrayList.add(new ValidationError(getClass(), "allowedValues", "Allowed value list of state variable can not also be restricted with allowed value range"));
            }
            if (!Datatype.Builtin.STRING.equals(getDatatype().getBuiltin())) {
                arrayList.add(new ValidationError(getClass(), "allowedValues", "Allowed value list of state variable only available for string datatype, not: " + getDatatype()));
            }
            for (String str : getAllowedValues()) {
                if (str.length() > 31) {
                    log.warning("UPnP specification violation, allowed value string must be less than 32 chars: " + str);
                }
            }
            if (!foundDefaultInAllowedValues(this.defaultValue, this.allowedValues)) {
                log.warning("UPnP specification violation, allowed string values don't contain default value: " + this.defaultValue);
            }
        }
        if (getAllowedValueRange() != null) {
            arrayList.addAll(getAllowedValueRange().validate());
        }
        return arrayList;
    }
}
