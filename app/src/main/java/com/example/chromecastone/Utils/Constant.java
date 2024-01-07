package com.example.chromecastone.Utils;

import java.util.ArrayList;
import java.util.List;


public class Constant {
    public static final String BACK_AFTER_CONNECTING = "come back after connecting";
    public static final String BROADCAST_ACTION = "com.fftools.service";
    public static String CAST_HTC = "com.htc.wifidisplay.CONFIGURE_MODE_NORMAL";
    public static String CAST_SETTING = "android.settings.CAST_SETTINGS";
    public static String EXTRA_LIST_MEDIA_FILE_MODEL = "extra list media file model";
    public static String EXTRA_POSITION_DATA = "extra position data";
    public static String EXTRA_POSITION_RESULT = "extra position data result";
    public static String EXTRA_POSITION_RESULT_BACK = "extra position data result back";
    public static String IS_STOP_STREAMING = "is stop stream";
    public static String IS_STREAMING = "streaming";
    public static String LAUNCH_WFD_PICKER_DLG = "com.samsung.wfd.LAUNCH_WFD_PICKER_DLG";
    public static final String NOTIFICATION_CHANNEL_ID = "channel service fftools";
    public static final String NOTIFICATION_CHANNEL_NAME = "FFtools App";
    public static String NOTIFICATION_CONTENT = "notification content";
    public static String NOTIFICATION_TITLE = "notification title";
    public static int NOT_DATA = -100;
    public static String VIRTUAL_DISPLAY_NAME = "ScreenStreamVirtualDisplayFFtools";
    public static String WIFI_DISPLAY_SETTING = "android.settings.WIFI_DISPLAY_SETTINGS";
    public static boolean isChromeCastConnected = false;
    public static boolean isConnected = false;
    public static boolean isDLNACastConnected = false;
    public static Object SELECTED_DEVICE_POSITION = "";
    public static List<Object> listCastDevices = new ArrayList();
}
