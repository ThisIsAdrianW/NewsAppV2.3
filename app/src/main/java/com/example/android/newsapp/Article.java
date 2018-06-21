package com.example.android.newsapp;

public class Article {
    private String aName;
    private String aUrl;
    private String aDate;
    private String aSection;
    private String mAuthor;

    //Public constructor for this class
    public Article(String articleName, String articleUrl, String articleDate, String articleSection, String articleAuthor) {
        aName = articleName;
        aUrl = articleUrl;
        aDate = articleDate;
        aSection = articleSection;
        mAuthor = articleAuthor;
    }

    //Getters for all variables
    public String getaName() {
        return aName;
    }

    public String getaUrl() {
        return aUrl;
    }

    public String getaDate() {
        return aDate;
    }

    public String getSection() {
        return aSection;
    }

    public String getmAuthor() {
        return mAuthor;
    }

}
