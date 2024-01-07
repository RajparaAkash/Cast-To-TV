package org.fourthline.cling.support.model.dlna;


public class DLNAProfileAttribute extends DLNAAttribute<DLNAProfiles> {
    public DLNAProfileAttribute() {
        setValue(DLNAProfiles.NONE);
    }

    public DLNAProfileAttribute(DLNAProfiles dLNAProfiles) {
        setValue(dLNAProfiles);
    }

    @Override
    public void setString(String str, String str2) throws InvalidDLNAProtocolAttributeException {
        DLNAProfiles valueOf = DLNAProfiles.valueOf(str, str2);
        if (valueOf == null) {
            throw new InvalidDLNAProtocolAttributeException("Can't parse DLNA profile from: " + str);
        }
        setValue(valueOf);
    }

    @Override
    public String getString() {
        return getValue().getCode();
    }
}
