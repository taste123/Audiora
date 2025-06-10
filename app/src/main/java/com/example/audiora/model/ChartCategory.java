package com.example.audiora.model;

public class ChartCategory {
    private String title;
    private String country;
    private String feedType;
    private int limit;
    private int defaultCoverResId;
    private String coverImageUrl;

    public ChartCategory(String title, String country, String feedType, int limit, int defaultCoverResId) {
        this.title = title;
        this.country = country;
        this.feedType = feedType;
        this.limit = limit;
        this.defaultCoverResId = defaultCoverResId;
    }

    public String getTitle() {
        return title;
    }

    public String getCountry() {
        return country;
    }

    public String getFeedType() {
        return feedType;
    }

    public int getLimit() {
        return limit;
    }

    public int getDefaultCoverResId() {
        return defaultCoverResId;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
}