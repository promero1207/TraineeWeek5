package com.app.paul.newsapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * class base for new
 */
public class News implements Parcelable {
    //fields
    private String headline;
    private String section;
    private String thumnail;
    private String body;
    private String web;

    //constructor
    public News(String headline, String section, String thumnail, String body, String web) {
        this.headline = headline;
        this.section = section;
        this.thumnail = thumnail;
        this.body = body;
        this.web = web;
    }

    //setters and getters
    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getThumnail() {
        return thumnail;
    }


    public String getBody() {
        return body;
    }


    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    //parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.headline);
        dest.writeString(this.section);
        dest.writeString(this.thumnail);
        dest.writeString(this.body);
        dest.writeString(this.web);
    }

    protected News(Parcel in) {
        this.headline = in.readString();
        this.section = in.readString();
        this.thumnail = in.readString();
        this.body = in.readString();
        this.web = in.readString();
    }

    public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {
        @Override
        public News createFromParcel(Parcel source) {
            return new News(source);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };
}
