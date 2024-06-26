package org.fourthline.cling.model.types;

import org.fourthline.cling.model.Constants;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ServiceType {
    private String namespace;
    private String type;
    private int version;
    private static final Logger log = Logger.getLogger(ServiceType.class.getName());
    public static final Pattern PATTERN = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):service:([a-zA-Z_0-9\\-]{1,64}):([0-9]+).*");
    public static final Pattern BROKEN_PATTERN = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):serviceId:([a-zA-Z_0-9\\-]{1,64}):([0-9]+).*");

    public ServiceType(String str, String str2) {
        this(str, str2, 1);
    }

    public ServiceType(String str, String str2, int i) {
        this.version = 1;
        if (str != null && !str.matches(Constants.REGEX_NAMESPACE)) {
            throw new IllegalArgumentException("Service type namespace contains illegal characters");
        }
        this.namespace = str;
        if (str2 != null && !str2.matches(Constants.REGEX_TYPE)) {
            throw new IllegalArgumentException("Service type suffix too long (64) or contains illegal characters");
        }
        this.type = str2;
        this.version = i;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getType() {
        return this.type;
    }

    public int getVersion() {
        return this.version;
    }

    public static ServiceType valueOf(String str) throws InvalidValueException {
        if (str == null) {
            throw new InvalidValueException("Can't parse null string");
        }
        UDAServiceType uDAServiceType = null;
        String replaceAll = str.replaceAll("\\s", "");
        try {
            uDAServiceType = UDAServiceType.valueOf(replaceAll);
        } catch (Exception unused) {
        }
        if (uDAServiceType != null) {
            return uDAServiceType;
        }
        try {
            Matcher matcher = PATTERN.matcher(replaceAll);
            if (matcher.matches() && matcher.groupCount() >= 3) {
                return new ServiceType(matcher.group(1), matcher.group(2), Integer.valueOf(matcher.group(3)).intValue());
            }
            Matcher matcher2 = BROKEN_PATTERN.matcher(replaceAll);
            if (matcher2.matches() && matcher2.groupCount() >= 3) {
                return new ServiceType(matcher2.group(1), matcher2.group(2), Integer.valueOf(matcher2.group(3)).intValue());
            }
            Matcher matcher3 = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):service:(.+?):([0-9]+).*").matcher(replaceAll);
            if (matcher3.matches() && matcher3.groupCount() >= 3) {
                String replaceAll2 = matcher3.group(2).replaceAll("[^a-zA-Z_0-9\\-]", "-");
                Logger logger = log;
                logger.warning("UPnP specification violation, replacing invalid service type token '" + matcher3.group(2) + "' with: " + replaceAll2);
                return new ServiceType(matcher3.group(1), replaceAll2, Integer.valueOf(matcher3.group(3)).intValue());
            }
            Matcher matcher4 = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):serviceId:(.+?):([0-9]+).*").matcher(replaceAll);
            if (matcher4.matches() && matcher4.groupCount() >= 3) {
                String replaceAll3 = matcher4.group(2).replaceAll("[^a-zA-Z_0-9\\-]", "-");
                Logger logger2 = log;
                logger2.warning("UPnP specification violation, replacing invalid service type token '" + matcher4.group(2) + "' with: " + replaceAll3);
                return new ServiceType(matcher4.group(1), replaceAll3, Integer.valueOf(matcher4.group(3)).intValue());
            }
            throw new InvalidValueException("Can't parse service type string (namespace/type/version): " + replaceAll);
        } catch (RuntimeException e) {
            throw new InvalidValueException(String.format("Can't parse service type string (namespace/type/version) '%s': %s", replaceAll, e.toString()));
        }
    }

    public boolean implementsVersion(ServiceType serviceType) {
        return serviceType != null && this.namespace.equals(serviceType.namespace) && this.type.equals(serviceType.type) && this.version >= serviceType.version;
    }

    public String toFriendlyString() {
        return getNamespace() + ":" + getType() + ":" + getVersion();
    }

    public String toString() {
        return "urn:" + getNamespace() + ":service:" + getType() + ":" + getVersion();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof ServiceType)) {
            return false;
        }
        ServiceType serviceType = (ServiceType) obj;
        return this.version == serviceType.version && this.namespace.equals(serviceType.namespace) && this.type.equals(serviceType.type);
    }

    public int hashCode() {
        return (((this.namespace.hashCode() * 31) + this.type.hashCode()) * 31) + this.version;
    }
}
