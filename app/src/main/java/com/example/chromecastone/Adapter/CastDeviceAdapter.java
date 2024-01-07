package com.example.chromecastone.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.mediarouter.media.MediaRouter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chromecastone.Dlna.model.upnp.IUpnpDevice;
import com.example.chromecastone.Interface.ItemOnClickListener;
import com.example.chromecastone.Utils.Constant;
import com.example.chromecastone.databinding.ItemviewCastDeviceBinding;
import com.google.android.gms.cast.CastDevice;

import java.util.ArrayList;
import java.util.List;


public class CastDeviceAdapter extends RecyclerView.Adapter<CastDeviceAdapter.CastDeviceViewHolder> {
    private List<Object> listCastDevice = new ArrayList();
    private Context mContext;
    private ItemOnClickListener onClickListener;

    public CastDeviceAdapter(Context context) {
        this.mContext = context;
    }

    public void setFolderDataList(List<Object> list) {
        this.listCastDevice.clear();
        this.listCastDevice = list;
        notifyDataSetChanged();
    }

    public void setItemOnClickItemListener(ItemOnClickListener itemOnClickListener) {
        this.onClickListener = itemOnClickListener;
    }

    @Override
    public CastDeviceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new CastDeviceViewHolder(ItemviewCastDeviceBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(CastDeviceViewHolder castDeviceViewHolder, @SuppressLint("RecyclerView") final int i) {
        Object obj = this.listCastDevice.get(i);
        if (obj instanceof MediaRouter.RouteInfo) {
            MediaRouter.RouteInfo routeInfo = (MediaRouter.RouteInfo) obj;
            CastDevice fromBundle = CastDevice.getFromBundle(routeInfo.getExtras());
            TextView textView = castDeviceViewHolder.binding.tvDeviceName;
            textView.setText(routeInfo.getName() + " (" + fromBundle.getInetAddress().getHostAddress() + ")");
            castDeviceViewHolder.binding.tvSubTitle.setText("Chromecast");
            if (Constant.SELECTED_DEVICE_POSITION instanceof MediaRouter.RouteInfo) {
                if (fromBundle.getFriendlyName().equals(CastDevice.getFromBundle(((MediaRouter.RouteInfo) Constant.SELECTED_DEVICE_POSITION).getExtras()).getFriendlyName())) {
                    castDeviceViewHolder.binding.ivSelected.setVisibility(View.VISIBLE);
                }
            } else {
                castDeviceViewHolder.binding.ivSelected.setVisibility(View.GONE);
            }
        } else if (obj instanceof IUpnpDevice) {
            IUpnpDevice iUpnpDevice = (IUpnpDevice) obj;
            if (Constant.SELECTED_DEVICE_POSITION instanceof IUpnpDevice) {
                if (iUpnpDevice.getFriendlyName().equals(((IUpnpDevice) Constant.SELECTED_DEVICE_POSITION).getFriendlyName())) {
                    castDeviceViewHolder.binding.ivSelected.setVisibility(View.VISIBLE);
                }
            } else {
                castDeviceViewHolder.binding.ivSelected.setVisibility(View.GONE);
            }
            castDeviceViewHolder.binding.tvDeviceName.setText(iUpnpDevice.getFriendlyName());
            castDeviceViewHolder.binding.tvSubTitle.setText("DLNA");
        }
        castDeviceViewHolder.binding.clItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                CastDeviceAdapter.this.m69x6fe2d4f7(i, view);
            }
        });
    }

    
    public void m69x6fe2d4f7(int i, View view) {
        if (this.onClickListener != null) {
            Object obj = this.listCastDevice.get(i);
            if (Constant.SELECTED_DEVICE_POSITION instanceof IUpnpDevice) {
                if (((IUpnpDevice) obj).getFriendlyName().equals(((IUpnpDevice) Constant.SELECTED_DEVICE_POSITION).getFriendlyName())) {
                    return;
                }
            } else if (Constant.SELECTED_DEVICE_POSITION instanceof MediaRouter.RouteInfo) {
                if (CastDevice.getFromBundle(((MediaRouter.RouteInfo) obj).getExtras()).getFriendlyName().equals(CastDevice.getFromBundle(((MediaRouter.RouteInfo) Constant.SELECTED_DEVICE_POSITION).getExtras()).getFriendlyName())) {
                    return;
                }
            }
            Constant.SELECTED_DEVICE_POSITION = this.listCastDevice.get(i);
            this.onClickListener.onItemClick(i);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        if (this.listCastDevice.size() != 0) {
            return this.listCastDevice.size();
        }
        return 0;
    }

    
    public class CastDeviceViewHolder extends RecyclerView.ViewHolder {
        private ItemviewCastDeviceBinding binding;

        public CastDeviceViewHolder(ItemviewCastDeviceBinding itemCastDeviceBinding) {
            super(itemCastDeviceBinding.getRoot());
            this.binding = itemCastDeviceBinding;
        }
    }
}
