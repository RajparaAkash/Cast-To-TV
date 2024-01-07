package com.example.chromecastone.Model;

import java.util.List;


public class MediaFolderData {
    private String folderName;
    private String folderPath;
    private long lastModified;
    private double length;
    private List<MediaFileModel> mediaFileList;

    public String getFolderName() {
        return this.folderName;
    }

    public void setFolderName(String str) {
        this.folderName = str;
    }

    public List<MediaFileModel> getMediaFileList() {
        return this.mediaFileList;
    }

    public void setMediaFileList(List<MediaFileModel> list) {
        this.mediaFileList = list;
    }

    public double getLength() {
        return this.length;
    }

    public void setLength(double d) {
        this.length = d;
    }

    public String getFolderPath() {
        return this.folderPath;
    }

    public void setFolderPath(String str) {
        this.folderPath = str;
    }

    public long getLastModified() {
        return this.lastModified;
    }

    public void setLastModified(long j) {
        this.lastModified = j;
    }
}
