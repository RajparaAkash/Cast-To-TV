package com.example.chromecastone.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chromecastone.Adapter.CastDeviceAdapter;
import com.example.chromecastone.Dlna.controller.Factory;
import com.example.chromecastone.Dlna.controller.IUpnpServiceController;
import com.example.chromecastone.Dlna.model.upnp.IDeviceDiscoveryObserver;
import com.example.chromecastone.Dlna.model.upnp.IFactory;
import com.example.chromecastone.Dlna.model.upnp.IRendererCommand;
import com.example.chromecastone.Dlna.model.upnp.IUpnpDevice;
import com.example.chromecastone.Interface.DeviceConnectListener;
import com.example.chromecastone.Interface.ItemOnClickListener;
import com.example.chromecastone.R;
import com.example.chromecastone.Utils.Constant;
import com.example.chromecastone.databinding.ActivityCastDeviceListBinding;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class CastDeviceListActivity extends AppCompatActivity implements ItemOnClickListener, IDeviceDiscoveryObserver, Observer {

    public static IFactory factory;
    public static IUpnpServiceController upnpServiceController;
    private ActivityCastDeviceListBinding binding;
    private CastDeviceAdapter castDeviceAdapter;
    private DeviceConnectListener deviceConnectListener;
    private boolean isBack;
    private List<Object> listCastDevices1;
    private CastContext mCastContext;
    private CastSession mCastSession;
    private MediaRouter mMediaRouter;
    private final SessionManagerListener<CastSession> mSessionManagerListener = new MySessionManagerListener();
    private MediaRouteSelector mediaRouteSelector;
    private int positionData;

    @Override
    public void onStart() {
        CastContext.getSharedInstance(this).getSessionManager().addSessionManagerListener(this.mSessionManagerListener, CastSession.class);
        super.onStart();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActivityCastDeviceListBinding inflate = ActivityCastDeviceListBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView(inflate.getRoot());
        getIntentData();
        initMain();

        findViewById(R.id.back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.cast_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.listCastDevices.clear();
                castDeviceAdapter.setFolderDataList(Constant.listCastDevices);
                binding.tvSearching.setVisibility(View.VISIBLE);
                binding.tvContent.setVisibility(View.VISIBLE);
                binding.tvContentGuide.setVisibility(View.VISIBLE);
                binding.castLoading.setVisibility(View.VISIBLE);
                displayCastDeviceList();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        IUpnpServiceController iUpnpServiceController = upnpServiceController;
        if (iUpnpServiceController != null) {
            iUpnpServiceController.resume(this);
        }
        if (Constant.isChromeCastConnected || Constant.isDLNACastConnected) {
            this.binding.btDisconnect.setVisibility(View.VISIBLE);
            this.binding.btStartMirror.setVisibility(View.VISIBLE);
        } else {
            this.binding.btDisconnect.setVisibility(View.GONE);
            this.binding.btStartMirror.setVisibility(View.GONE);
        }
        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                CastDeviceListActivity.this.displayCastDeviceList();
            }
        }, 500L);
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        CastContext.getSharedInstance(this).getSessionManager().removeSessionManagerListener(this.mSessionManagerListener, CastSession.class);
        super.onStop();
    }

    private void initMain() {
        this.listCastDevices1 = new ArrayList();
        CastDeviceAdapter castDeviceAdapter = new CastDeviceAdapter(this);
        this.castDeviceAdapter = castDeviceAdapter;
        castDeviceAdapter.setItemOnClickItemListener(this);
        this.mCastContext = CastContext.getSharedInstance(this);
        this.mMediaRouter = MediaRouter.getInstance(this);
        this.mediaRouteSelector = new MediaRouteSelector.Builder().addControlCategory(CastMediaControlIntent.categoryForCast(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID)).build();
        initView();
        displayCastDeviceList();
    }

    private void getIntentData() {
        this.isBack = getIntent().getBooleanExtra(Constant.BACK_AFTER_CONNECTING, false);
        this.positionData = getIntent().getIntExtra(Constant.EXTRA_POSITION_RESULT, Constant.NOT_DATA);
    }

    public void displayCastDeviceList() {
        this.listCastDevices1.clear();
        this.castDeviceAdapter.notifyDataSetChanged();
        getDevicesList();
        if (factory == null) {
            factory = new Factory();
        }
        if (upnpServiceController == null) {
            upnpServiceController = factory.createUpnpServiceController(this);
        }
        IUpnpServiceController iUpnpServiceController = upnpServiceController;
        if (iUpnpServiceController != null) {
            iUpnpServiceController.getRendererDiscovery().addObserver(this);
            upnpServiceController.addSelectedRendererObserver(this);
        }
    }

    public void setDeviceConnectListener(DeviceConnectListener deviceConnectListener) {
        this.deviceConnectListener = deviceConnectListener;
    }

    public void initView() {
        this.binding.rvDevices.setLayoutManager(new LinearLayoutManager(this));
        this.binding.rvDevices.setAdapter(this.castDeviceAdapter);
        this.castDeviceAdapter.setFolderDataList(this.listCastDevices1);
        this.binding.btDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.isChromeCastConnected) {
                    disconnectChromeCast();
                    Toast.makeText(CastDeviceListActivity.this, getString(R.string.text_disconnect_message), Toast.LENGTH_SHORT).show();
                } else if (Constant.isDLNACastConnected) {
                    disconnectDLNA();
                    Toast.makeText(CastDeviceListActivity.this, getString(R.string.text_disconnect_message), Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.binding.tvContentGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CastDeviceListActivity.this, HelpActivity.class));
            }
        });
        this.binding.btStartMirror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void getDevicesList() {
        startRouteScan(100000L, new ScanCallback() {
            @Override
            void onRouteUpdate(List<MediaRouter.RouteInfo> list) {
                MediaRouter.RouteInfo routeInfo;
                if (CastDeviceListActivity.this.getContext().getCastState() != 1) {
                    CastDeviceListActivity.this.stopRouteScan(this);
                    CastDeviceListActivity.this.getSessionManager().getCurrentCastSession();
                }
                if (list == null || list.size() <= 0) {
                    return;
                }
                for (MediaRouter.RouteInfo routeInfo2 : list) {
                    CastDevice fromBundle = CastDevice.getFromBundle(routeInfo2.getExtras());
                    if (fromBundle != null) {
                        boolean z = false;
                        int i = 0;
                        while (true) {
                            if (i >= CastDeviceListActivity.this.listCastDevices1.size()) {
                                break;
                            }
                            if (CastDeviceListActivity.this.listCastDevices1.get(i) != null && (CastDeviceListActivity.this.listCastDevices1.get(i) instanceof MediaRouter.RouteInfo) && (routeInfo = (MediaRouter.RouteInfo) CastDeviceListActivity.this.listCastDevices1.get(i)) != null && routeInfo.getName().equals(routeInfo2.getName())) {
                                z = true;
                                break;
                            }
                            i++;
                        }
                        if (!z && fromBundle != null) {
                            CastDeviceListActivity.this.binding.castLoading.setVisibility(View.GONE);
                            CastDeviceListActivity.this.binding.tvSearching.setVisibility(View.GONE);
                            CastDeviceListActivity.this.binding.tvContent.setVisibility(View.GONE);
                            CastDeviceListActivity.this.binding.tvContentGuide.setVisibility(View.GONE);
                            CastDeviceListActivity.this.listCastDevices1.add(routeInfo2);
                            Constant.listCastDevices.add(routeInfo2);
                            CastDeviceListActivity.this.castDeviceAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }, null);
    }

    public void disconnectDLNA() {
        Constant.isDLNACastConnected = false;
        Constant.SELECTED_DEVICE_POSITION = "";
        this.castDeviceAdapter.notifyDataSetChanged();
        IRendererCommand createRendererCommand = factory.createRendererCommand(factory.createRendererState());
        if (createRendererCommand != null) {
            createRendererCommand.commandStop();
            createRendererCommand.pause();
        }
        upnpServiceController.getServiceListener().getServiceConnexion().onServiceDisconnected(null);
        upnpServiceController.getRendererDiscovery().removeObserver(this);
        upnpServiceController.delSelectedRendererObserver(this);
        upnpServiceController.getServiceListener().clearListener();
        Constant.isConnected = false;
        Constant.isChromeCastConnected = false;
        DeviceConnectListener deviceConnectListener = this.deviceConnectListener;
        if (deviceConnectListener != null) {
            deviceConnectListener.onDeviceConnect(false);
        }
        this.binding.btDisconnect.setVisibility(View.GONE);
        this.binding.btStartMirror.setVisibility(View.GONE);
    }

    public void disconnectChromeCast() {
        Constant.isChromeCastConnected = false;
        this.mCastContext.getSessionManager().endCurrentSession(true);
        this.mMediaRouter.unselect(MediaRouter.UNSELECT_REASON_DISCONNECTED);
        Constant.isConnected = false;
        Constant.isChromeCastConnected = false;
        DeviceConnectListener deviceConnectListener = this.deviceConnectListener;
        if (deviceConnectListener != null) {
            deviceConnectListener.onDeviceConnect(false);
            Constant.isConnected = false;
        }
        Constant.SELECTED_DEVICE_POSITION = "";
        this.castDeviceAdapter.notifyDataSetChanged();
        this.binding.btDisconnect.setVisibility(View.GONE);
        this.binding.btStartMirror.setVisibility(View.GONE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (Constant.isConnected) {
            Intent intent = new Intent();
            intent.putExtra(Constant.EXTRA_POSITION_RESULT_BACK, this.positionData);
            setResult(-1, intent);
        } else {
            setResult(0);
        }
        super.onBackPressed();
    }

    @Override
    public void onItemClick(int i) {
        if (this.listCastDevices1 != null) {
            this.binding.castLoading.setVisibility(View.VISIBLE);
            if (this.listCastDevices1.get(i) instanceof MediaRouter.RouteInfo) {
                if (Constant.isDLNACastConnected) {
                    disconnectDLNA();
                }
                Constant.isDLNACastConnected = false;
                getMediaRouter().selectRoute((MediaRouter.RouteInfo) this.listCastDevices1.get(i));
            } else if (this.listCastDevices1.get(i) instanceof IUpnpDevice) {
                if (Constant.isChromeCastConnected) {
                    disconnectChromeCast();
                }
                Constant.isDLNACastConnected = true;
                Constant.isChromeCastConnected = false;
                upnpServiceController.setSelectedRenderer((IUpnpDevice) this.listCastDevices1.get(i), false);
                Constant.isConnected = true;
                DeviceConnectListener deviceConnectListener = this.deviceConnectListener;
                if (deviceConnectListener != null) {
                    deviceConnectListener.onDeviceConnect(true);
                }
                this.binding.castLoading.setVisibility(View.GONE);
                this.binding.btDisconnect.setVisibility(View.VISIBLE);
                this.binding.btStartMirror.setVisibility(View.VISIBLE);
                Toast.makeText(this, getString(R.string.text_connect_successfully), Toast.LENGTH_SHORT).show();
                if (this.isBack) {
                    Intent intent = new Intent();
                    intent.putExtra(Constant.EXTRA_POSITION_RESULT_BACK, this.positionData);
                    setResult(-1, intent);
                    finish();
                }
            }
        }
    }

    @Override
    public void addedDevice(final IUpnpDevice iUpnpDevice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                IUpnpDevice iUpnpDevice2;
                CastDeviceListActivity.this.binding.castLoading.setVisibility(View.GONE);
                CastDeviceListActivity.this.binding.tvSearching.setVisibility(View.GONE);
                CastDeviceListActivity.this.binding.tvContent.setVisibility(View.GONE);
                CastDeviceListActivity.this.binding.tvContentGuide.setVisibility(View.GONE);
                if (CastDeviceListActivity.this.listCastDevices1 == null || CastDeviceListActivity.this.listCastDevices1.size() <= 0) {
                    CastDeviceListActivity.this.listCastDevices1.add(iUpnpDevice);
                    Constant.listCastDevices.add(iUpnpDevice);
                    CastDeviceListActivity.this.castDeviceAdapter.notifyDataSetChanged();
                } else {
                    boolean z = false;
                    int i = 0;
                    while (true) {
                        if (i < CastDeviceListActivity.this.listCastDevices1.size()) {
                            if (CastDeviceListActivity.this.listCastDevices1.get(i) != null && (CastDeviceListActivity.this.listCastDevices1.get(i) instanceof IUpnpDevice) && (iUpnpDevice2 = (IUpnpDevice) CastDeviceListActivity.this.listCastDevices1.get(i)) != null && iUpnpDevice2.getFriendlyName().equalsIgnoreCase(iUpnpDevice.getFriendlyName())) {
                                z = true;
                                break;
                            }
                            i++;
                        } else {
                            break;
                        }
                    }
                    if (!z && iUpnpDevice != null) {
                        CastDeviceListActivity.this.listCastDevices1.add(iUpnpDevice);
                        Constant.listCastDevices.add(iUpnpDevice);
                        CastDeviceListActivity.this.castDeviceAdapter.notifyDataSetChanged();
                    }
                }
                Log.v("TAG", "New device detected : " + iUpnpDevice.getDisplayString());
            }
        });
    }

    @Override
    public void removedDevice(IUpnpDevice iUpnpDevice) {
        Log.e("TAG ------->", "Device removed : " + iUpnpDevice.getFriendlyName());
    }

    public void updateDeviceList(CastDevice castDevice) {
        for (Object obj : Constant.listCastDevices) {
            if (obj instanceof MediaRouter.RouteInfo) {
                MediaRouter.RouteInfo routeInfo = (MediaRouter.RouteInfo) obj;
                if (CastDevice.getFromBundle(routeInfo.getExtras()).getFriendlyName().equals(castDevice.getFriendlyName())) {
                    Constant.SELECTED_DEVICE_POSITION = routeInfo;
                    this.castDeviceAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void update(Observable observable, Object obj) {
        Log.v("TAG", "Device update : ");
    }


    private class MySessionManagerListener implements SessionManagerListener<CastSession> {
        @Override
        public void onSessionResumeFailed(CastSession castSession, int i) {
        }

        @Override
        public void onSessionResuming(CastSession castSession, String str) {
        }

        private MySessionManagerListener() {
        }

        @Override
        public void onSessionEnded(CastSession castSession, int i) {

            CastDeviceListActivity.this.binding.castLoading.setVisibility(View.GONE);
            if (castSession == CastDeviceListActivity.this.mCastSession) {
                CastDeviceListActivity.this.mCastSession = null;
            }
            CastDeviceListActivity.this.invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumed(CastSession castSession, boolean z) {
            Constant.isConnected = true;
            Constant.isChromeCastConnected = true;
            if (CastDeviceListActivity.this.deviceConnectListener != null) {
                CastDeviceListActivity.this.deviceConnectListener.onDeviceConnect(true);
            }
            CastDeviceListActivity.this.updateDeviceList(castSession.getCastDevice());
            CastDeviceListActivity.this.binding.btDisconnect.setVisibility(View.VISIBLE);
            CastDeviceListActivity.this.binding.btStartMirror.setVisibility(View.VISIBLE);
            CastDeviceListActivity.this.mCastSession = castSession;
            CastDeviceListActivity.this.invalidateOptionsMenu();
        }

        @Override
        public void onSessionStarted(CastSession castSession, String str) {
            Constant.isConnected = true;
            Constant.isChromeCastConnected = true;
            if (CastDeviceListActivity.this.deviceConnectListener != null) {
                CastDeviceListActivity.this.deviceConnectListener.onDeviceConnect(true);
            }
            CastDeviceListActivity.this.binding.btDisconnect.setVisibility(View.VISIBLE);
            CastDeviceListActivity.this.binding.btStartMirror.setVisibility(View.VISIBLE);
            CastDeviceListActivity.this.binding.castLoading.setVisibility(View.GONE);
            CastDeviceListActivity castDeviceListActivity = CastDeviceListActivity.this;
            Toast.makeText(castDeviceListActivity, castDeviceListActivity.getString(R.string.text_connect_successfully), Toast.LENGTH_SHORT).show();
            CastDeviceListActivity.this.mCastSession = castSession;
            CastDeviceListActivity.this.invalidateOptionsMenu();
            if (CastDeviceListActivity.this.isBack) {
                Intent intent = new Intent();
                intent.putExtra(Constant.EXTRA_POSITION_RESULT_BACK, CastDeviceListActivity.this.positionData);
                CastDeviceListActivity.this.setResult(-1, intent);
                CastDeviceListActivity.this.finish();
            }
        }

        @Override
        public void onSessionStarting(CastSession castSession) {
            Toast.makeText(CastDeviceListActivity.this, (int) R.string.text_connecting, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSessionStartFailed(CastSession castSession, int i) {
            Constant.SELECTED_DEVICE_POSITION = "";
            CastDeviceListActivity.this.castDeviceAdapter.notifyDataSetChanged();
        }

        @Override
        public void onSessionEnding(CastSession castSession) {
            Constant.SELECTED_DEVICE_POSITION = "";
            CastDeviceListActivity.this.castDeviceAdapter.notifyDataSetChanged();
        }

        @Override
        public void onSessionSuspended(CastSession castSession, int i) {
            Constant.SELECTED_DEVICE_POSITION = "";
            CastDeviceListActivity.this.castDeviceAdapter.notifyDataSetChanged();
        }
    }


    public static abstract class ScanCallback extends MediaRouter.Callback {
        private MediaRouter mediaRouter;
        private boolean stopped = false;

        abstract void onRouteUpdate(List<MediaRouter.RouteInfo> list);

        void setMediaRouter(MediaRouter mediaRouter) {
            this.mediaRouter = mediaRouter;
        }

        void stop() {
            this.stopped = true;
        }


        public void onFilteredRouteUpdate() {
            if (this.stopped || this.mediaRouter == null) {
                return;
            }
            ArrayList arrayList = new ArrayList();
            for (MediaRouter.RouteInfo routeInfo : this.mediaRouter.getRoutes()) {
                Bundle extras = routeInfo.getExtras();
                if (extras != null) {
                    CastDevice.getFromBundle(extras);
                    if (extras.getString("com.google.android.gms.cast.EXTRA_SESSION_ID") != null) {
                    }
                }
                if (routeInfo != null && routeInfo.getDescription() != null && !routeInfo.isDefault() && !routeInfo.getDescription().equals("Google Cast Multizone Member") && routeInfo.getPlaybackType() == 1) {
                    arrayList.add(routeInfo);
                }
            }
            onRouteUpdate(arrayList);
        }

        @Override
        public final void onRouteAdded(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
            onFilteredRouteUpdate();
        }

        @Override
        public final void onRouteChanged(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
            onFilteredRouteUpdate();
        }

        @Override
        public final void onRouteRemoved(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
            onFilteredRouteUpdate();
        }
    }

    public void startRouteScan(final Long l, final ScanCallback scanCallback, final Runnable runnable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scanCallback.setMediaRouter(CastDeviceListActivity.this.getMediaRouter());
                Long l2 = l;
                if (l2 != null && l2.longValue() == 0) {
                    scanCallback.onFilteredRouteUpdate();
                    return;
                }
                CastDeviceListActivity.this.getMediaRouter().addCallback(new MediaRouteSelector.Builder().addControlCategory(CastMediaControlIntent.categoryForCast(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID)).build(), scanCallback, 1);
                scanCallback.onFilteredRouteUpdate();
                if (l != null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            CastDeviceListActivity.this.getMediaRouter().removeCallback(scanCallback);
                            if (runnable != null) {
                                runnable.run();
                            }
                        }
                    }, l.longValue());
                }
            }
        });
    }

    public void stopRouteScan(final ScanCallback scanCallback) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scanCallback.stop();
                CastDeviceListActivity.this.getMediaRouter().removeCallback(scanCallback);
            }
        });
    }


    public MediaRouter getMediaRouter() {
        return MediaRouter.getInstance(this);
    }


    public CastContext getContext() {
        return CastContext.getSharedInstance(this);
    }


    public SessionManager getSessionManager() {
        return getContext().getSessionManager();
    }

    private static InetAddress getLocalIpAddressFromIntF(String str) {
        try {
            NetworkInterface byName = NetworkInterface.getByName(str);
            if (byName.isUp()) {
                Enumeration<InetAddress> inetAddresses = byName.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress nextElement = inetAddresses.nextElement();
                    if (!nextElement.isLoopbackAddress() && (nextElement instanceof Inet4Address)) {
                        return nextElement;
                    }
                }
                return null;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("TAG", "Unable to get ip adress for interface " + str);
            return null;
        }
    }

    public static InetAddress getLocalIpAddress(Context context) throws UnknownHostException {
        int ipAddress = ((WifiManager) context.getSystemService(WIFI_SERVICE)).getConnectionInfo().getIpAddress();
        if (ipAddress != 0) {
            return InetAddress.getByName(String.format("%d.%d.%d.%d", Integer.valueOf(ipAddress & 255), Integer.valueOf((ipAddress >> 8) & 255), Integer.valueOf((ipAddress >> 16) & 255), Integer.valueOf((ipAddress >> 24) & 255)));
        }
        InetAddress localIpAddressFromIntF = getLocalIpAddressFromIntF("wlan0");
        if (localIpAddressFromIntF != null) {
            return localIpAddressFromIntF;
        }
        InetAddress localIpAddressFromIntF2 = getLocalIpAddressFromIntF("usb0");
        return localIpAddressFromIntF2 != null ? localIpAddressFromIntF2 : InetAddress.getByName("0.0.0.0");
    }
}
