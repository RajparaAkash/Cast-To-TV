package com.example.chromecastone.Model;

import android.os.Parcel;
import android.os.Parcelable;


public class MediaFileModel implements Parcelable {
    public static final Creator<MediaFileModel> CREATOR = new Creator<MediaFileModel>() {

        @Override
        public MediaFileModel createFromParcel(Parcel parcel) {
            return new MediaFileModel(parcel);
        }

        
        @Override
        public MediaFileModel[] newArray(int i) {
            return new MediaFileModel[i];
        }
    };
    private long albumId;
    private String apkType;
    private long appCacheSize;
    private String appPackageName;
    private String appVersionCode;
    private String appVersionName;
    private String duration;
    private String fileName;
    private String filePath;
    private String fileSize;
    private String fileType;
    public String folderName;
    private String id;
    private boolean isDirectory;
    private boolean isHidden;
    public boolean isSelected;
    private long lastModifyTime;
    public double length;
    private String mediaCastUrl;
    private int pid;
    private String songAlbum;
    private int type;

    @Override
    public int describeContents() {
        return 0;
    }

    public MediaFileModel() {
        this.isSelected = true;
        this.folderName = "";
    }

    protected MediaFileModel(Parcel parcel) {
        this.isSelected = true;
        this.folderName = "";
        this.length = parcel.readDouble();
        this.isSelected = parcel.readByte() != 0;
        this.folderName = parcel.readString();
        this.fileName = parcel.readString();
        this.filePath = parcel.readString();
        this.fileSize = parcel.readString();
        this.appPackageName = parcel.readString();
        this.appVersionCode = parcel.readString();
        this.appVersionName = parcel.readString();
        this.apkType = parcel.readString();
        this.fileType = parcel.readString();
        this.duration = parcel.readString();
        this.songAlbum = parcel.readString();
        this.id = parcel.readString();
        this.mediaCastUrl = parcel.readString();
        this.lastModifyTime = parcel.readLong();
        this.appCacheSize = parcel.readLong();
        this.albumId = parcel.readLong();
        this.pid = parcel.readInt();
        this.type = parcel.readInt();
        this.isDirectory = parcel.readByte() != 0;
        this.isHidden = parcel.readByte() != 0;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String str) {
        this.fileName = str;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String str) {
        this.filePath = str;
    }

    public String getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(String str) {
        this.fileSize = str;
    }

    public long getLastModifyTime() {
        return this.lastModifyTime;
    }

    public void setLastModifyTime(long j) {
        this.lastModifyTime = j;
    }

    public String getAppPackageName() {
        return this.appPackageName;
    }

    public void setAppPackageName(String str) {
        this.appPackageName = str;
    }

    public String getAppVersionCode() {
        return this.appVersionCode;
    }

    public void setAppVersionCode(String str) {
        this.appVersionCode = str;
    }

    public String getAppVersionName() {
        return this.appVersionName;
    }

    public void setAppVersionName(String str) {
        this.appVersionName = str;
    }

    public long getAppCacheSize() {
        return this.appCacheSize;
    }

    public void setAppCacheSize(long j) {
        this.appCacheSize = j;
    }

    public String getApkType() {
        return this.apkType;
    }

    public void setApkType(String str) {
        this.apkType = str;
    }

    public boolean isDirectory() {
        return this.isDirectory;
    }

    public void setDirectory(boolean z) {
        this.isDirectory = z;
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    public void setHidden(boolean z) {
        this.isHidden = z;
    }

    public double getLength() {
        return this.length;
    }

    public void setLength(double d) {
        this.length = d;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String str) {
        this.fileType = str;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String str) {
        this.duration = str;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean z) {
        this.isSelected = z;
    }

    public int getPid() {
        return this.pid;
    }

    public void setPid(int i) {
        this.pid = i;
    }

    public String getSongAlbum() {
        return this.songAlbum;
    }

    public void setSongAlbum(String str) {
        this.songAlbum = str;
    }

    public long getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(long j) {
        this.albumId = j;
    }

    public String getFolderName() {
        return this.folderName;
    }

    public void setFolderName(String str) {
        this.folderName = str;
    }

    public String getMediaCastUrl() {
        return this.mediaCastUrl;
    }

    public void setMediaCastUrl(String str) {
        this.mediaCastUrl = str;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(this.length);
        parcel.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        parcel.writeString(this.folderName);
        parcel.writeString(this.fileName);
        parcel.writeString(this.filePath);
        parcel.writeString(this.fileSize);
        parcel.writeString(this.appPackageName);
        parcel.writeString(this.appVersionCode);
        parcel.writeString(this.appVersionName);
        parcel.writeString(this.apkType);
        parcel.writeString(this.fileType);
        parcel.writeString(this.duration);
        parcel.writeString(this.songAlbum);
        parcel.writeString(this.id);
        parcel.writeString(this.mediaCastUrl);
        parcel.writeLong(this.lastModifyTime);
        parcel.writeLong(this.appCacheSize);
        parcel.writeLong(this.albumId);
        parcel.writeInt(this.pid);
        parcel.writeInt(this.type);
        parcel.writeByte(this.isDirectory ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.isHidden ? (byte) 1 : (byte) 0);
    }
}
