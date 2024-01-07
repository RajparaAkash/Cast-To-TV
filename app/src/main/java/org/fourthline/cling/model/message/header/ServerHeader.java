package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.ServerClientTokens;


public class ServerHeader extends UpnpHeader<ServerClientTokens> {
    public ServerHeader() {
        setValue(new ServerClientTokens());
    }

    public ServerHeader(ServerClientTokens serverClientTokens) {
        setValue(serverClientTokens);
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        String[] split;
        String[] split2;
        ServerClientTokens serverClientTokens = new ServerClientTokens();
        serverClientTokens.setOsName("UNKNOWN");
        serverClientTokens.setOsVersion("UNKNOWN");
        serverClientTokens.setProductName("UNKNOWN");
        serverClientTokens.setProductVersion("UNKNOWN");
        if (str.contains("UPnP/1.1")) {
            serverClientTokens.setMinorVersion(1);
        } else if (!str.contains("UPnP/1.")) {
            throw new InvalidHeaderException("Missing 'UPnP/1.' in server information: " + str);
        }
        int i = 0;
        for (int i2 = 0; i2 < str.length(); i2++) {
            try {
                if (str.charAt(i2) == ' ') {
                    i++;
                }
            } catch (Exception unused) {
                serverClientTokens.setOsName("UNKNOWN");
                serverClientTokens.setOsVersion("UNKNOWN");
                serverClientTokens.setProductName("UNKNOWN");
                serverClientTokens.setProductVersion("UNKNOWN");
            }
        }
        if (str.contains(",")) {
            String[] split3 = str.split(",");
            split = split3[0].split("/");
            split2 = split3[2].split("/");
        } else if (i > 2) {
            String trim = str.substring(0, str.indexOf("UPnP/1.")).trim();
            String trim2 = str.substring(str.indexOf("UPnP/1.") + 8).trim();
            split = trim.split("/");
            split2 = trim2.split("/");
        } else {
            String[] split4 = str.split(" ");
            split = split4[0].split("/");
            split2 = split4[2].split("/");
        }
        serverClientTokens.setOsName(split[0].trim());
        if (split.length > 1) {
            serverClientTokens.setOsVersion(split[1].trim());
        }
        serverClientTokens.setProductName(split2[0].trim());
        if (split2.length > 1) {
            serverClientTokens.setProductVersion(split2[1].trim());
        }
        setValue(serverClientTokens);
    }

    @Override
    public String getString() {
        return getValue().getHttpToken();
    }
}
