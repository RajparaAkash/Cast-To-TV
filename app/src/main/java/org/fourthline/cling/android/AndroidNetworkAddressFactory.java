package org.fourthline.cling.android;

import org.fourthline.cling.transport.impl.NetworkAddressFactoryImpl;
import org.fourthline.cling.transport.spi.InitializationException;

import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AndroidNetworkAddressFactory extends NetworkAddressFactoryImpl {
    private static final Logger log = Logger.getLogger(AndroidUpnpServiceConfiguration.class.getName());

    @Override
    protected boolean requiresNetworkInterface() {
        return false;
    }

    public AndroidNetworkAddressFactory(int i) {
        super(i);
    }

    
    @Override
    public boolean isUsableAddress(NetworkInterface networkInterface, InetAddress inetAddress) {
        Field declaredField;
        Object obj;
        boolean isUsableAddress = super.isUsableAddress(networkInterface, inetAddress);
        if (isUsableAddress) {
            String hostAddress = inetAddress.getHostAddress();
            try {
                try {
                    Field declaredField2 = InetAddress.class.getDeclaredField("holder");
                    declaredField2.setAccessible(true);
                    obj = declaredField2.get(inetAddress);
                    declaredField = obj.getClass().getDeclaredField("hostName");
                } catch (NoSuchFieldException unused) {
                    declaredField = InetAddress.class.getDeclaredField("hostName");
                    obj = inetAddress;
                }
                if (declaredField == null || obj == null || hostAddress == null) {
                    return false;
                }
                declaredField.setAccessible(true);
                declaredField.set(obj, hostAddress);
            } catch (Exception e) {
                Logger logger = log;
                Level level = Level.SEVERE;
                logger.log(level, "Failed injecting hostName to work around Android InetAddress DNS bug: " + inetAddress, (Throwable) e);
                return false;
            }
        }
        return isUsableAddress;
    }

    @Override
    public InetAddress getLocalAddress(NetworkInterface networkInterface, boolean z, InetAddress inetAddress) {
        for (InetAddress inetAddress2 : getInetAddresses(networkInterface)) {
            if (z && (inetAddress2 instanceof Inet6Address)) {
                return inetAddress2;
            }
            if (!z && (inetAddress2 instanceof Inet4Address)) {
                return inetAddress2;
            }
        }
        throw new IllegalStateException("Can't find any IPv4 or IPv6 address on interface: " + networkInterface.getDisplayName());
    }

    
    @Override
    public void discoverNetworkInterfaces() throws InitializationException {
        try {
            super.discoverNetworkInterfaces();
        } catch (Exception e) {
            Logger logger = log;
            logger.warning("Exception while enumerating network interfaces, trying once more: " + e);
            super.discoverNetworkInterfaces();
        }
    }
}
