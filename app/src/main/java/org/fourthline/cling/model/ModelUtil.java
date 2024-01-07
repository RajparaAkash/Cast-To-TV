package org.fourthline.cling.model;

import com.example.chromecastone.CastServer.CastServerService;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;


public class ModelUtil {
    public static boolean ANDROID_EMULATOR;
    public static boolean ANDROID_RUNTIME;

    static {
        boolean z;
        String str;
        boolean z2 = true;
        boolean z3 = false;
        try {
            if (Thread.currentThread().getContextClassLoader().loadClass("android.os.Build").getField("ID").get(null) != null) {
                z = true;
                ANDROID_RUNTIME = z;
                str = (String) Thread.currentThread().getContextClassLoader().loadClass("android.os.Build").getField("PRODUCT").get(null);
                if (!"google_sdk".equals(str)) {
                    if (!"sdk".equals(str)) {
                        z2 = false;
                    }
                }
                z3 = z2;
                ANDROID_EMULATOR = z3;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        z = false;
        ANDROID_RUNTIME = z;
        try {
            str = (String) Thread.currentThread().getContextClassLoader().loadClass("android.os.Build").getField("PRODUCT").get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        if (!"google_sdk".equals(str)) {
//        }
        z3 = z2;
        ANDROID_EMULATOR = z3;
    }

    public static boolean isStringConvertibleType(Set<Class> set, Class cls) {
        if (cls.isEnum()) {
            return true;
        }
        for (Class cls2 : set) {
            if (cls2.isAssignableFrom(cls)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidUDAName(String str) {
        return ANDROID_RUNTIME ? (str == null || str.length() == 0) ? false : true : (str == null || str.length() == 0 || str.toLowerCase(Locale.ROOT).startsWith("xml") || !str.matches(Constants.REGEX_UDA_NAME)) ? false : true;
    }

    public static InetAddress getInetAddressByName(String str) {
        try {
            return InetAddress.getByName(str);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toCommaSeparatedList(Object[] objArr) {
        return toCommaSeparatedList(objArr, true, false);
    }

    public static String toCommaSeparatedList(Object[] objArr, boolean z, boolean z2) {
        if (objArr == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object obj : objArr) {
            String replaceAll = obj.toString().replaceAll("\\\\", "\\\\\\\\");
            if (z) {
                replaceAll = replaceAll.replaceAll(",", "\\\\,");
            }
            if (z2) {
                replaceAll = replaceAll.replaceAll("\"", "\\\"");
            }
            sb.append(replaceAll);
            sb.append(",");
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String[] fromCommaSeparatedList(String str) {
        return fromCommaSeparatedList(str, true);
    }

    public static String[] fromCommaSeparatedList(String str, boolean z) {
        if (str == null || str.length() == 0) {
            return null;
        }
        if (z) {
            str = str.replaceAll("\\\\,", "XXX1122334455XXX");
        }
        String[] split = str.split(",");
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].replaceAll("XXX1122334455XXX", ",");
            split[i] = split[i].replaceAll("\\\\\\\\", "\\\\");
        }
        return split;
    }

    public static String toTimeString(long j) {
        long j2 = j / 3600;
        long j3 = j % 3600;
        long j4 = j3 / 60;
        long j5 = j3 % 60;
        StringBuilder sb = new StringBuilder();
        sb.append(j2 < 10 ? "0" : "");
        sb.append(j2);
        sb.append(":");
        sb.append(j4 < 10 ? "0" : "");
        sb.append(j4);
        sb.append(":");
        sb.append(j5 >= 10 ? "" : "0");
        sb.append(j5);
        return sb.toString();
    }

    public static long fromTimeString(String str) {
        if (str.lastIndexOf(CastServerService.ROOT_DIR) != -1) {
            str = str.substring(0, str.lastIndexOf(CastServerService.ROOT_DIR));
        }
        String[] split = str.split(":");
        if (split.length != 3) {
            throw new IllegalArgumentException("Can't parse time string: " + str);
        }
        return (Long.parseLong(split[0]) * 3600) + (Long.parseLong(split[1]) * 60) + Long.parseLong(split[2]);
    }

    public static String commaToNewline(String str) {
        StringBuilder sb = new StringBuilder();
        for (String str2 : str.split(",")) {
            sb.append(str2);
            sb.append(",");
            sb.append("\n");
        }
        if (sb.length() > 2) {
            sb.deleteCharAt(sb.length() - 2);
        }
        return sb.toString();
    }

    public static String getLocalHostName(boolean z) {
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            return (z || hostName.indexOf(CastServerService.ROOT_DIR) == -1) ? hostName : hostName.substring(0, hostName.indexOf(CastServerService.ROOT_DIR));
        } catch (Exception unused) {
            return "UNKNOWN HOST";
        }
    }

    public static byte[] getFirstNetworkInterfaceHardwareAddress() {
        try {
            Iterator it = Collections.list(NetworkInterface.getNetworkInterfaces()).iterator();
            while (it.hasNext()) {
                NetworkInterface networkInterface = (NetworkInterface) it.next();
                if (!networkInterface.isLoopback() && networkInterface.isUp() && networkInterface.getHardwareAddress() != null) {
                    return networkInterface.getHardwareAddress();
                }
            }
            throw new RuntimeException("Could not discover first network interface hardware address");
        } catch (Exception unused) {
            throw new RuntimeException("Could not discover first network interface hardware address");
        }
    }
}
