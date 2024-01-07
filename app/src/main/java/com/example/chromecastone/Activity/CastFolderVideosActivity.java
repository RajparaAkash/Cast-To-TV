package com.example.chromecastone.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chromecastone.Adapter.AlbumVideosAdapter;
import com.example.chromecastone.CastServer.CastServerService;
import com.example.chromecastone.Interface.DeviceConnectListener;
import com.example.chromecastone.Interface.ItemOnClickListener;
import com.example.chromecastone.Model.MediaFileModel;
import com.example.chromecastone.Model.MediaFolderData;
import com.example.chromecastone.R;
import com.example.chromecastone.Utils.Constant;
import com.example.chromecastone.Utils.Utils;
import com.example.chromecastone.databinding.ActivityCastFolderVideosBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class CastFolderVideosActivity extends AppCompatActivity implements ItemOnClickListener, DeviceConnectListener {

    private AlbumVideosAdapter adapterVideoAlbum;
    private ActivityCastFolderVideosBinding binding;
    private LinkedHashMap<String, ArrayList<MediaFileModel>> bucketImagesDataHashMap;
    private List<MediaFolderData> listFolderData;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActivityCastFolderVideosBinding inflate = ActivityCastFolderVideosBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView(inflate.getRoot());

        initMain();

        findViewById(R.id.back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.cast_photos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CastDeviceListActivity().setDeviceConnectListener(CastFolderVideosActivity.this);
                startActivityForResult(new Intent(CastFolderVideosActivity.this, CastDeviceListActivity.class), 100);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (CastDeviceListActivity.upnpServiceController != null) {
            CastDeviceListActivity.upnpServiceController.resume(this);
        }
        invalidateOptionsMenu();
    }

    private void initMain() {
        initData();
        setRecycleView();
        getAlbumVideos();
    }


    private void initData() {
        this.listFolderData = new ArrayList();
        this.bucketImagesDataHashMap = new LinkedHashMap<>();
        AlbumVideosAdapter albumVideosAdapter = new AlbumVideosAdapter(this);
        this.adapterVideoAlbum = albumVideosAdapter;
        albumVideosAdapter.setItemOnClickListener(this);
    }

    private void setRecycleView() {
        this.binding.rvFolderlistVideos.setLayoutManager(new LinearLayoutManager(this));
        this.binding.rvFolderlistVideos.setAdapter(this.adapterVideoAlbum);
    }

    public void getAlbumVideos() {
        Observable.fromCallable(new Callable() {
            @Override
            public Object call() throws Exception {
                return CastFolderVideosActivity.this.m187xb9c34fe6();
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Consumer() {
            @Override
            public void accept(Object obj) throws Throwable {
                CastFolderVideosActivity.this.m189x8e226024((Boolean) obj);
            }
        });
    }


    public Boolean m187xb9c34fe6() throws Exception {
        getVideosList();
        return true;
    }


    public void m189x8e226024(Boolean bool) throws Throwable {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CastFolderVideosActivity.this.m188x23f2d805();
            }
        });
    }


    public void m188x23f2d805() {
        this.binding.pbLoad.setVisibility(View.GONE);
        if (this.listFolderData.size() != 0) {
            this.binding.ivEmpty.setVisibility(View.GONE);
            this.binding.rvFolderlistVideos.setVisibility(View.VISIBLE);
            this.adapterVideoAlbum.setFolderDataList(this.listFolderData);
            return;
        }
        this.binding.ivEmpty.setVisibility(View.VISIBLE);
        this.binding.rvFolderlistVideos.setVisibility(View.GONE);
    }

    public void getVideosList() {
        this.listFolderData.clear();
        String[] strArr = {"_data", "title", "date_modified", "bucket_display_name", "duration", "_size", "_id"};
        Cursor query = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, strArr, null, null, "LOWER(date_modified) DESC");
        if (query != null) {
            File file = null;
            if (query.moveToFirst()) {
                do {
                    String string = query.getString(query.getColumnIndex(strArr[0]));
                    String string2 = query.getString(query.getColumnIndex(strArr[1]));
                    query.getString(query.getColumnIndex(strArr[2]));
                    String string3 = query.getString(query.getColumnIndex(strArr[3]));
                    String string4 = query.getString(query.getColumnIndex(strArr[4]));
                    long j = query.getLong(5);
                    if (string4 == null) {
                        try {
                            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                            mediaMetadataRetriever.setDataSource(this, Uri.parse(string));
                            string4 = mediaMetadataRetriever.extractMetadata(9);
                            mediaMetadataRetriever.release();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    String string5 = query.getString(query.getColumnIndex(strArr[6]));
                    try {
                        file = new File(string);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    new MediaFileModel().setFilePath(string);
                    if (file != null && file.exists()) {
                        MediaFileModel mediaFileModel = new MediaFileModel();
                        mediaFileModel.setFilePath(string);
                        mediaFileModel.setFileName(string2);
                        mediaFileModel.setFileType(string.substring(string.lastIndexOf(CastServerService.ROOT_DIR) + 1));
                        mediaFileModel.setType(2);
                        mediaFileModel.setFolderName(string3);
                        mediaFileModel.setLength(j);
                        mediaFileModel.setDuration(string4);
                        mediaFileModel.setId(string5);
                        if (this.bucketImagesDataHashMap.containsKey(string3)) {
                            ArrayList<MediaFileModel> arrayList = this.bucketImagesDataHashMap.get(string3);
                            arrayList.add(mediaFileModel);
                            this.bucketImagesDataHashMap.put(string3, arrayList);
                        } else {
                            ArrayList<MediaFileModel> arrayList2 = new ArrayList<>();
                            arrayList2.add(mediaFileModel);
                            this.bucketImagesDataHashMap.put(string3, arrayList2);
                        }
                    }
                } while (query.moveToNext());
                query.close();
            } else {
                query.close();
            }
        }
        Set<String> keySet = this.bucketImagesDataHashMap.keySet();
        ArrayList arrayList3 = new ArrayList();
        arrayList3.addAll(keySet);
        for (int i = 0; i < arrayList3.size(); i++) {
            ArrayList<MediaFileModel> arrayList4 = this.bucketImagesDataHashMap.get(arrayList3.get(i));
            MediaFolderData mediaFolderData = new MediaFolderData();
            mediaFolderData.setFolderName((String) arrayList3.get(i));
            mediaFolderData.setMediaFileList(arrayList4);
            File file2 = new File(Utils.getParentPath(arrayList4.get(0).getFilePath()));
            mediaFolderData.setFolderPath(Utils.getParentPath(arrayList4.get(0).getFilePath()));
            double d = 0.0d;
            Iterator<MediaFileModel> it = arrayList4.iterator();
            while (it.hasNext()) {
                d += it.next().getLength();
            }
            mediaFolderData.setLength(d);
            mediaFolderData.setLastModified(file2.lastModified());
            this.listFolderData.add(mediaFolderData);
        }
    }

    @Override
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
    }

    @Override
    public void onItemClick(int i) {
        Intent intent = new Intent(this, ListVideosActivity.class);
        intent.putParcelableArrayListExtra(Constant.EXTRA_LIST_MEDIA_FILE_MODEL, (ArrayList) this.listFolderData.get(i).getMediaFileList());
        startActivity(intent);
    }

    @Override
    public void onDeviceConnect(boolean z) {
        Constant.isConnected = z;
        invalidateOptionsMenu();
    }
}
