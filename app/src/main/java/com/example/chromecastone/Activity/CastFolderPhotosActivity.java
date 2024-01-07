package com.example.chromecastone.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.chromecastone.Adapter.AlbumPhotosAdapter;
import com.example.chromecastone.Interface.DeviceConnectListener;
import com.example.chromecastone.Interface.ItemOnClickListener;
import com.example.chromecastone.Model.MediaFileModel;
import com.example.chromecastone.Model.MediaFolderData;
import com.example.chromecastone.R;
import com.example.chromecastone.Utils.Constant;
import com.example.chromecastone.Utils.Utils;
import com.example.chromecastone.databinding.ActivityCastFolderPhotosBinding;

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


public class CastFolderPhotosActivity extends AppCompatActivity implements ItemOnClickListener, DeviceConnectListener {

    private AlbumPhotosAdapter adapterPhotosAlbum;
    private ActivityCastFolderPhotosBinding binding;
    private LinkedHashMap<String, ArrayList<MediaFileModel>> bucketImagesDataHashMap;
    private List<MediaFolderData> listFolderData;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActivityCastFolderPhotosBinding inflate = ActivityCastFolderPhotosBinding.inflate(getLayoutInflater());
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
                new CastDeviceListActivity().setDeviceConnectListener(CastFolderPhotosActivity.this);
                startActivityForResult(new Intent(CastFolderPhotosActivity.this, CastDeviceListActivity.class), 100);
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
        getAlbumImages();
    }

    private void initData() {
        this.listFolderData = new ArrayList();
        this.bucketImagesDataHashMap = new LinkedHashMap<>();
        AlbumPhotosAdapter albumPhotosAdapter = new AlbumPhotosAdapter(this);
        this.adapterPhotosAlbum = albumPhotosAdapter;
        albumPhotosAdapter.setItemOnClickListener(this);
    }

    private void setRecycleView() {
        this.binding.rvFolderlistPhotos.setLayoutManager(new GridLayoutManager(this, 3));
        this.binding.rvFolderlistPhotos.setAdapter(this.adapterPhotosAlbum);
    }

    public void getAlbumImages() {
        Observable.fromCallable(new Callable() {
            @Override
            public Object call() throws Exception {
                return CastFolderPhotosActivity.this.m158x1aa9d23d();
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Consumer() {
            @Override
            public void accept(Object obj) throws Throwable {
                CastFolderPhotosActivity.this.m160xef08e27b((Boolean) obj);
            }
        });
    }


    public Boolean m158x1aa9d23d() throws Exception {
        getAlbumImagesList();
        return true;
    }


    public void m160xef08e27b(Boolean bool) throws Throwable {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CastFolderPhotosActivity.this.m159x84d95a5c();
            }
        });
    }


    public void m159x84d95a5c() {
        this.binding.pbLoad.setVisibility(View.GONE);
        if (this.listFolderData.size() != 0) {
            this.binding.ivEmpty.setVisibility(View.GONE);
            this.binding.rvFolderlistPhotos.setVisibility(View.VISIBLE);
            this.adapterPhotosAlbum.setFolderDataList(this.listFolderData);
            return;
        }
        this.binding.ivEmpty.setVisibility(View.VISIBLE);
        this.binding.rvFolderlistPhotos.setVisibility(View.GONE);
    }

    public void getAlbumImagesList() {
        this.listFolderData.clear();
        String[] strArr = {"_data", "title", "bucket_display_name", "_id"};
        Cursor query = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, strArr, null, null, "LOWER(date_modified) DESC");
        if (query != null) {
            if (query.moveToFirst()) {
                do {
                    String string = query.getString(query.getColumnIndexOrThrow("_data"));
                    String string2 = query.getString(query.getColumnIndex(strArr[1]));
                    if (string2 == null) {
                        string2 = "";
                    }
                    String string3 = query.getString(query.getColumnIndex(strArr[2]));
                    String string4 = query.getString(3);
                    new MediaFileModel().setFilePath(string);
                    MediaFileModel mediaFileModel = new MediaFileModel();
                    mediaFileModel.setFilePath(string);
                    mediaFileModel.setFileName(string2);
                    mediaFileModel.setType(1);
                    mediaFileModel.setFolderName(string3);
                    mediaFileModel.setId(string4);
                    if (this.bucketImagesDataHashMap.containsKey(string3)) {
                        ArrayList<MediaFileModel> arrayList = this.bucketImagesDataHashMap.get(string3);
                        arrayList.add(mediaFileModel);
                        this.bucketImagesDataHashMap.put(string3, arrayList);
                    } else {
                        ArrayList<MediaFileModel> arrayList2 = new ArrayList<>();
                        arrayList2.add(mediaFileModel);
                        this.bucketImagesDataHashMap.put(string3, arrayList2);
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
            File file = new File(Utils.getParentPath(arrayList4.get(0).getFilePath()));
            mediaFolderData.setFolderPath(Utils.getParentPath(arrayList4.get(0).getFilePath()));
            double d = 0.0d;
            Iterator<MediaFileModel> it = arrayList4.iterator();
            while (it.hasNext()) {
                d += it.next().getLength();
            }
            mediaFolderData.setLength(d);
            mediaFolderData.setLastModified(file.lastModified());
            this.listFolderData.add(mediaFolderData);
        }
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        invalidateOptionsMenu();
    }

    @Override
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
    }

    @Override
    public void onItemClick(final int i) {
        CastFolderPhotosActivity.this.m161x8e512c0f(i);
    }


    public void m161x8e512c0f(int i) {
        Intent intent = new Intent(this, ListPhotosActivity.class);
        intent.putParcelableArrayListExtra(Constant.EXTRA_LIST_MEDIA_FILE_MODEL, (ArrayList) this.listFolderData.get(i).getMediaFileList());
        startActivity(intent);
    }

    @Override
    public void onDeviceConnect(boolean z) {
        Constant.isConnected = z;
        invalidateOptionsMenu();
    }
}
