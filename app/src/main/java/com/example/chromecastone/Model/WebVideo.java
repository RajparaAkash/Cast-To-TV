package com.example.chromecastone.Model;

import android.os.Parcel;
import android.os.Parcelable;


public class WebVideo implements Parcelable {
    public static final Creator<WebVideo> CREATOR = new Creator<WebVideo>() {

        @Override
        public WebVideo createFromParcel(Parcel parcel) {
            return new WebVideo(parcel);
        }

        
        @Override
        public WebVideo[] newArray(int i) {
            return new WebVideo[i];
        }
    };
    boolean checked;
    boolean chunked;
    String details;
    boolean expanded;
    String height;
    String link;
    String name;
    String page;
    String size;
    String thumbnail_url;
    String type;
    String website;
    String width;

    @Override
    public int describeContents() {
        return 0;
    }

    public WebVideo(String str, String str2, String str3, String str4, String str5, String str6, String str7, boolean z, boolean z2, boolean z3, String str8, String str9, String str10) {
        this.size = str;
        this.type = str2;
        this.link = str3;
        this.name = str4;
        this.page = str5;
        this.website = str6;
        this.details = str7;
        this.chunked = z;
        this.checked = z2;
        this.expanded = z3;
        this.width = str8;
        this.height = str9;
        this.thumbnail_url = str10;
    }

    protected WebVideo(Parcel parcel) {
        this.chunked = false;
        this.checked = false;
        this.expanded = false;
        this.size = parcel.readString();
        this.type = parcel.readString();
        this.link = parcel.readString();
        this.name = parcel.readString();
        this.page = parcel.readString();
        this.website = parcel.readString();
        this.details = parcel.readString();
        this.width = parcel.readString();
        this.height = parcel.readString();
        this.chunked = parcel.readByte() != 0;
        this.checked = parcel.readByte() != 0;
        this.expanded = parcel.readByte() != 0;
        this.thumbnail_url = parcel.readString();
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String str) {
        this.size = str;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String str) {
        this.type = str;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String str) {
        this.link = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getPage() {
        return this.page;
    }

    public void setPage(String str) {
        this.page = str;
    }

    public String getWebsite() {
        return this.website;
    }

    public void setWebsite(String str) {
        this.website = str;
    }

    public String getDetails() {
        return this.details;
    }

    public void setDetails(String str) {
        this.details = str;
    }

    public boolean isChunked() {
        return this.chunked;
    }

    public void setChunked(boolean z) {
        this.chunked = z;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(boolean z) {
        this.checked = z;
    }

    public boolean isExpanded() {
        return this.expanded;
    }

    public void setExpanded(boolean z) {
        this.expanded = z;
    }

    public String getWidth() {
        return this.width;
    }

    public void setWidth(String str) {
        this.width = str;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String str) {
        this.height = str;
    }

    public String getThumbnail_url() {
        return this.thumbnail_url;
    }

    public void setThumbnail_url(String str) {
        this.thumbnail_url = str;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.size);
        parcel.writeString(this.type);
        parcel.writeString(this.link);
        parcel.writeString(this.name);
        parcel.writeString(this.page);
        parcel.writeString(this.website);
        parcel.writeString(this.details);
        parcel.writeString(this.width);
        parcel.writeString(this.height);
        parcel.writeByte(this.chunked ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.checked ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.expanded ? (byte) 1 : (byte) 0);
        parcel.writeString(this.thumbnail_url);
    }
}
