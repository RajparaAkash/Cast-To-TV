package org.fourthline.cling.support.model.dlna;

import org.seamless.util.Exceptions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class DLNAAttribute<T> {
    private static final Logger log = Logger.getLogger(DLNAAttribute.class.getName());
    private T value;

    public abstract String getString();

    public abstract void setString(String str, String str2) throws InvalidDLNAProtocolAttributeException;


    public enum Type {
        DLNA_ORG_PN("DLNA.ORG_PN", DLNAProfileAttribute.class),
        DLNA_ORG_OP("DLNA.ORG_OP", DLNAOperationsAttribute.class),
        DLNA_ORG_PS("DLNA.ORG_PS", DLNAPlaySpeedAttribute.class),
        DLNA_ORG_CI("DLNA.ORG_CI", DLNAConversionIndicatorAttribute.class),
        DLNA_ORG_FLAGS("DLNA.ORG_FLAGS", DLNAFlagsAttribute.class);
        
        private static Map<String, Type> byName = new HashMap<String, Type>() {
            {
                Type[] values;
                for (Type type : Type.values()) {
                    put(type.getAttributeName().toUpperCase(Locale.ROOT), type);
                }
            }
        };
        private String attributeName;
        private Class<? extends DLNAAttribute>[] attributeTypes;

        @SafeVarargs
        Type(String str, Class... clsArr) {
            this.attributeName = str;
            this.attributeTypes = clsArr;
        }

        public String getAttributeName() {
            return this.attributeName;
        }

        public Class<? extends DLNAAttribute>[] getAttributeTypes() {
            return this.attributeTypes;
        }

        public static Type valueOfAttributeName(String str) {
            if (str == null) {
                return null;
            }
            return byName.get(str.toUpperCase(Locale.ROOT));
        }
    }

    public void setValue(T t) {
        this.value = t;
    }

    public T getValue() {
        return this.value;
    }

    public static DLNAAttribute newInstance(Type type, String str, String str2) {
        DLNAAttribute dLNAAttribute;
        Exception e;
        DLNAAttribute dLNAAttribute2 = null;
        for (int i = 0; i < type.getAttributeTypes().length && dLNAAttribute2 == null; i++) {
            Class<? extends DLNAAttribute> cls = type.getAttributeTypes()[i];
            try {
                try {
                    log.finest("Trying to parse DLNA '" + type + "' with class: " + cls.getSimpleName());
                    dLNAAttribute = cls.newInstance();
                    if (str != null) {
                        try {
                            dLNAAttribute.setString(str, str2);
                        } catch (Exception e2) {
                            e = e2;
                            Logger logger = log;
                            logger.severe("Error instantiating DLNA attribute of type '" + type + "' with value: " + str);
                            logger.log(Level.SEVERE, "Exception root cause: ", Exceptions.unwrap(e));
                            dLNAAttribute2 = dLNAAttribute;
                        }
                    }
                } catch (Exception e3) {
                    dLNAAttribute = dLNAAttribute2;
                    e = e3;
                }
                dLNAAttribute2 = dLNAAttribute;
            } catch (InvalidDLNAProtocolAttributeException e4) {
                log.finest("Invalid DLNA attribute value for tested type: " + cls.getSimpleName() + " - " + e4.getMessage());
                dLNAAttribute2 = null;
            }
        }
        return dLNAAttribute2;
    }

    public String toString() {
        return "(" + getClass().getSimpleName() + ") '" + getValue() + "'";
    }
}
