package com.example.audiora.model;

public class ChartCategory {
    private final String title;
    private final String country;
    private final String feedType;
    private final int limit;
    private final int coverImageResource;

    public ChartCategory(String title, String country, String feedType, int limit, int coverImageResource) {
        this.title = title;
        this.country = country;
        this.feedType = feedType;
        this.limit = limit;
        this.coverImageResource = coverImageResource;
    }

    public String getTitle() { return title; }
    public String getCountry() { return country; }
    public String getFeedType() { return feedType; }
    public int getLimit() { return limit; }
    public int getCoverImageResource() { return coverImageResource; }
}