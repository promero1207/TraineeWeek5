package com.app.paul.newsapp;

/**
 * class base for new
 */
public class News {
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
}
