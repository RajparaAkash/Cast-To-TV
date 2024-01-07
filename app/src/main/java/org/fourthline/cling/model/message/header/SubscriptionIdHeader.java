package org.fourthline.cling.model.message.header;


public class SubscriptionIdHeader extends UpnpHeader<String> {
    public static final String PREFIX = "uuid:";

    public SubscriptionIdHeader() {
    }

    public SubscriptionIdHeader(String str) {
        setValue(str);
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        if (!str.startsWith("uuid:")) {
            throw new InvalidHeaderException("Invalid subscription ID header value, must start with 'uuid:': " + str);
        }
        setValue(str);
    }

    @Override
    public String getString() {
        return getValue();
    }
}
