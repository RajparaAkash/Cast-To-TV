package com.example.chromecastone.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chromecastone.Adapter.AlbumAudiosAdapter;
import com.example.chromecastone.CastServer.CastServerService;
import com.example.chromecastone.Interface.DeviceConnectListener;
import com.example.chromecastone.Interface.ItemOnClickListener;
import com.example.chromecastone.Model.MediaFileModel;
import com.example.chromecastone.Model.MediaFolderData;
import com.example.chromecastone.R;
import com.example.chromecastone.Utils.Constant;
import com.example.chromecastone.databinding.ActivityCastFolderAudiosBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CastFolderAudiosActivity extends AppCompatActivity implements ItemOnClickListener, DeviceConnectListener {

    private AlbumAudiosAdapter adapterMusicAlbum;
    private ActivityCastFolderAudiosBinding binding;
    private LinkedHashMap<String, ArrayList<MediaFileModel>> bucketImagesDataHashMap;
    private List<MediaFolderData> listFolderData;
    private List<MediaFileModel> listMusic;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActivityCastFolderAudiosBinding inflate = ActivityCastFolderAudiosBinding.inflate(getLayoutInflater());
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
                new CastDeviceListActivity().setDeviceConnectListener(CastFolderAudiosActivity.this);
                startActivityForResult(new Intent(CastFolderAudiosActivity.this, CastDeviceListActivity.class), 100);
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
        getAlbumMusic();
    }


    private void initData() {
        this.listFolderData = new ArrayList();
        this.listMusic = new ArrayList();
        this.bucketImagesDataHashMap = new LinkedHashMap<>();
        AlbumAudiosAdapter albumAudiosAdapter = new AlbumAudiosAdapter(this);
        this.adapterMusicAlbum = albumAudiosAdapter;
        albumAudiosAdapter.setItemOnClickListener(this);
    }

    private void setRecycleView() {
        this.binding.rvFolderlistAudios.setLayoutManager(new LinearLayoutManager(this));
        this.binding.rvFolderlistAudios.setAdapter(this.adapterMusicAlbum);
    }

    public void getAlbumMusic() {
        Observable.fromCallable(new Callable() {
            @Override
            public Object call() throws Exception {
                return m137xb075095a();
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Consumer() {
            @Override
            public void accept(Object obj) throws Throwable {
                m139x4bf3f95c((Boolean) obj);
            }
        });
    }


    public Boolean m137xb075095a() throws Exception {
        getMusicList();
        return true;
    }


    public void m139x4bf3f95c(Boolean bool) throws Throwable {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                m138xfe34815b();
            }
        });
    }


    public void m138xfe34815b() {
        this.binding.pbLoad.setVisibility(View.GONE);
        if (this.listFolderData.size() != 0) {
            this.binding.ivEmpty.setVisibility(View.GONE);
            this.binding.rvFolderlistAudios.setVisibility(View.VISIBLE);
            this.adapterMusicAlbum.setFolderDataList(this.listFolderData);
            return;
        }
        this.binding.ivEmpty.setVisibility(View.VISIBLE);
        this.binding.rvFolderlistAudios.setVisibility(View.GONE);
    }

    public void getMusicList() {
        this.listFolderData.clear();
        String[] strArr = {"_data", "_display_name", "date_modified", "duration", "_size", "album", "album_id", "title", "_id"};
        Cursor query = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, strArr, null, null, "LOWER(date_modified) DESC");
        char c = 0;
        if (query != null) {
            if (query.moveToFirst()) {
                File file = null;
                while (true) {
                    String string = query.getString(query.getColumnIndex(strArr[c]));
                    String string2 = query.getString(query.getColumnIndex(strArr[1]));
                    query.getLong(query.getColumnIndex(strArr[2]));
                    int i = query.getInt(query.getColumnIndex(strArr[3]));
                    long j = query.getLong(4);
                    String string3 = query.getString(5);
                    long j2 = query.getLong(6);
                    query.getString(7);
                    String string4 = query.getString(query.getColumnIndex(strArr[8]));
                    try {
                        file = new File(string);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    new MediaFileModel().setFilePath(string);
                    if (file != null && file.exists()) {
                        MediaFileModel mediaFileModel = new MediaFileModel();
                        mediaFileModel.setFilePath(string);
                        mediaFileModel.setFileName(string2);
                        mediaFileModel.setFileType(string.substring(string.lastIndexOf(CastServerService.ROOT_DIR) + 1));
                        mediaFileModel.setType(3);
                        mediaFileModel.setLength(j);
                        mediaFileModel.setDuration(String.valueOf(i));
                        mediaFileModel.setSongAlbum(string3);
                        mediaFileModel.setAlbumId(j2);
                        mediaFileModel.setId(string4);
                        this.listMusic.add(mediaFileModel);
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
                    if (!query.moveToNext()) {
                        break;
                    }
                    c = 0;
                }
            }
            query.close();
        }
        Set<String> keySet = this.bucketImagesDataHashMap.keySet();
        ArrayList arrayList3 = new ArrayList();
        arrayList3.addAll(keySet);
        for (int i2 = 0; i2 < arrayList3.size(); i2++) {
            ArrayList<MediaFileModel> arrayList4 = this.bucketImagesDataHashMap.get(arrayList3.get(i2));
            if (arrayList4 != null) {
                MediaFolderData mediaFolderData = new MediaFolderData();
                mediaFolderData.setFolderName((String) arrayList3.get(i2));
                mediaFolderData.setMediaFileList(arrayList4);
                this.listFolderData.add(mediaFolderData);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
    }

    @Override
    public void onItemClick(final int i) {
        m140xf1fcd1c7(i);
    }


    public void m140xf1fcd1c7(int i) {
        Intent intent = new Intent(this, ListAudiosActivity.class);
        intent.putParcelableArrayListExtra(Constant.EXTRA_LIST_MEDIA_FILE_MODEL, (ArrayList) this.listFolderData.get(i).getMediaFileList());
        startActivity(intent);
    }

    @Override
    public void onDeviceConnect(boolean z) {
        Constant.isConnected = z;
        invalidateOptionsMenu();
    }
}
