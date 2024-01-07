package org.fourthline.cling.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.transport.RouterImpl;
import org.fourthline.cling.transport.spi.InitializationException;
import org.seamless.util.Exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.reactivex.rxjava3.annotations.SchedulerSupport;


public class AndroidRouter extends RouterImpl {
    private static final Logger log = Logger.getLogger(Router.class.getName());
    protected BroadcastReceiver broadcastReceiver;
    private final Context context;
    protected WifiManager.MulticastLock multicastLock;
    protected NetworkInfo networkInfo;
    protected WifiManager.WifiLock wifiLock;
    private final WifiManager wifiManager;

    @Override
    protected int getLockTimeoutMillis() {
        return 15000;
    }

    public AndroidRouter(UpnpServiceConfiguration upnpServiceConfiguration, ProtocolFactory protocolFactory, Context context) throws InitializationException {
        super(upnpServiceConfiguration, protocolFactory);
        this.context = context;
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.networkInfo = NetworkUtils.getConnectedNetworkInfo(context);
        if (ModelUtil.ANDROID_EMULATOR) {
            return;
        }
        BroadcastReceiver createConnectivityBroadcastReceiver = createConnectivityBroadcastReceiver();
        this.broadcastReceiver = createConnectivityBroadcastReceiver;
        context.registerReceiver(createConnectivityBroadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    protected BroadcastReceiver createConnectivityBroadcastReceiver() {
        return new ConnectivityBroadcastReceiver();
    }

    @Override
    public void shutdown() throws RouterException {
        super.shutdown();
        unregisterBroadcastReceiver();
    }

    @Override
    public boolean enable() throws RouterException {
        lock(this.writeLock);
        try {
            boolean enable = super.enable();
            if (enable && isWifi()) {
                setWiFiMulticastLock(true);
                setWifiLock(true);
            }
            return enable;
        } finally {
            unlock(this.writeLock);
        }
    }

    @Override
    public boolean disable() throws RouterException {
        lock(this.writeLock);
        try {
            if (isWifi()) {
                setWiFiMulticastLock(false);
                setWifiLock(false);
            }
            return super.disable();
        } finally {
            unlock(this.writeLock);
        }
    }

    public NetworkInfo getNetworkInfo() {
        return this.networkInfo;
    }

    public boolean isMobile() {
        return NetworkUtils.isMobile(this.networkInfo);
    }

    public boolean isWifi() {
        return NetworkUtils.isWifi(this.networkInfo);
    }

    public boolean isEthernet() {
        return NetworkUtils.isEthernet(this.networkInfo);
    }

    public boolean enableWiFi() {
        log.info("Enabling WiFi...");
        try {
            return this.wifiManager.setWifiEnabled(true);
        } catch (Throwable th) {
            log.log(Level.WARNING, "SetWifiEnabled failed", th);
            return false;
        }
    }

    public void unregisterBroadcastReceiver() {
        BroadcastReceiver broadcastReceiver = this.broadcastReceiver;
        if (broadcastReceiver != null) {
            this.context.unregisterReceiver(broadcastReceiver);
            this.broadcastReceiver = null;
        }
    }

    protected void setWiFiMulticastLock(boolean z) {
        if (this.multicastLock == null) {
            this.multicastLock = this.wifiManager.createMulticastLock(getClass().getSimpleName());
        }
        if (z) {
            if (this.multicastLock.isHeld()) {
                log.warning("WiFi multicast lock already acquired");
                return;
            }
            log.info("WiFi multicast lock acquired");
            this.multicastLock.acquire();
        } else if (this.multicastLock.isHeld()) {
            log.info("WiFi multicast lock released");
            this.multicastLock.release();
        } else {
            log.warning("WiFi multicast lock already released");
        }
    }

    protected void setWifiLock(boolean z) {
        if (this.wifiLock == null) {
            this.wifiLock = this.wifiManager.createWifiLock(3, getClass().getSimpleName());
        }
        if (z) {
            if (this.wifiLock.isHeld()) {
                log.warning("WiFi lock already acquired");
                return;
            }
            log.info("WiFi lock acquired");
            this.wifiLock.acquire();
        } else if (this.wifiLock.isHeld()) {
            log.info("WiFi lock released");
            this.wifiLock.release();
        } else {
            log.warning("WiFi lock already released");
        }
    }

    protected void onNetworkTypeChange(NetworkInfo networkInfo, NetworkInfo networkInfo2) throws RouterException {
        Logger logger = log;
        Object[] objArr = new Object[2];
        objArr[0] = networkInfo == null ? "" : networkInfo.getTypeName();
        String str = "NONE";
        objArr[1] = networkInfo2 == null ? "NONE" : networkInfo2.getTypeName();
        logger.info(String.format("Network type changed %s => %s", objArr));
        if (disable()) {
            Object[] objArr2 = new Object[1];
            objArr2[0] = networkInfo == null ? "NONE" : networkInfo.getTypeName();
            logger.info(String.format("Disabled router on network type change (old network: %s)", objArr2));
        }
        this.networkInfo = networkInfo2;
        if (enable()) {
            Object[] objArr3 = new Object[1];
            if (networkInfo2 != null) {
                str = networkInfo2.getTypeName();
            }
            objArr3[0] = str;
            logger.info(String.format("Enabled router on network type change (new network: %s)", objArr3));
        }
    }

    protected void handleRouterExceptionOnNetworkTypeChange(RouterException routerException) {
        Throwable unwrap = Exceptions.unwrap(routerException);
        if (unwrap instanceof InterruptedException) {
            Logger logger = log;
            Level level = Level.INFO;
            logger.log(level, "Router was interrupted: " + routerException, unwrap);
            return;
        }
        Logger logger2 = log;
        Level level2 = Level.WARNING;
        logger2.log(level2, "Router error on network change: " + routerException, (Throwable) routerException);
    }

    
    
    public class ConnectivityBroadcastReceiver extends BroadcastReceiver {
        ConnectivityBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                displayIntentInfo(intent);
                NetworkInfo connectedNetworkInfo = NetworkUtils.getConnectedNetworkInfo(context);
                if (AndroidRouter.this.networkInfo != null && connectedNetworkInfo == null) {
                    for (int i = 1; i <= 3; i++) {
                        try {
                            Thread.sleep(1000L);
                            AndroidRouter.log.warning(String.format("%s => NONE network transition, waiting for new network... retry #%d", AndroidRouter.this.networkInfo.getTypeName(), Integer.valueOf(i)));
                            connectedNetworkInfo = NetworkUtils.getConnectedNetworkInfo(context);
                            if (connectedNetworkInfo != null) {
                                break;
                            }
                        } catch (InterruptedException unused) {
                            return;
                        }
                    }
                }
                if (isSameNetworkType(AndroidRouter.this.networkInfo, connectedNetworkInfo)) {
                    AndroidRouter.log.info("No actual network change... ignoring event!");
                    return;
                }
                try {
                    AndroidRouter androidRouter = AndroidRouter.this;
                    androidRouter.onNetworkTypeChange(androidRouter.networkInfo, connectedNetworkInfo);
                } catch (RouterException e) {
                    AndroidRouter.this.handleRouterExceptionOnNetworkTypeChange(e);
                }
            }
        }

        protected boolean isSameNetworkType(NetworkInfo networkInfo, NetworkInfo networkInfo2) {
            if (networkInfo == null && networkInfo2 == null) {
                return true;
            }
            return (networkInfo == null || networkInfo2 == null || networkInfo.getType() != networkInfo2.getType()) ? false : true;
        }

        protected void displayIntentInfo(Intent intent) {
            boolean booleanExtra = intent.getBooleanExtra("noConnectivity", false);
            String stringExtra = intent.getStringExtra("reason");
            boolean booleanExtra2 = intent.getBooleanExtra("isFailover", false);
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
            NetworkInfo networkInfo2 = (NetworkInfo) intent.getParcelableExtra("otherNetwork");
            AndroidRouter.log.info("Connectivity change detected...");
            Logger logger = AndroidRouter.log;
            logger.info("EXTRA_NO_CONNECTIVITY: " + booleanExtra);
            Logger logger2 = AndroidRouter.log;
            logger2.info("EXTRA_REASON: " + stringExtra);
            Logger logger3 = AndroidRouter.log;
            logger3.info("EXTRA_IS_FAILOVER: " + booleanExtra2);
            Logger logger4 = AndroidRouter.log;
            StringBuilder sb = new StringBuilder();
            sb.append("EXTRA_NETWORK_INFO: ");
            if (networkInfo == null) {
                networkInfo = null;
            }
            sb.append(networkInfo);
            logger4.info(sb.toString());
            Logger logger5 = AndroidRouter.log;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("EXTRA_OTHER_NETWORK_INFO: ");
            if (networkInfo2 == null) {
                networkInfo2 = null;
            }
            sb2.append(networkInfo2);
            logger5.info(sb2.toString());
            Logger logger6 = AndroidRouter.log;
            logger6.info("EXTRA_EXTRA_INFO: " + intent.getStringExtra("extraInfo"));
        }
    }
}
