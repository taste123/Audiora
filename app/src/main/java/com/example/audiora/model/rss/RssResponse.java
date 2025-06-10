package com.example.audiora.model.rss;

import com.google.gson.annotations.SerializedName;

public class RssResponse {

	@SerializedName("feed")
	private Feed feed;

	public Feed getFeed(){
		return feed;
	}
}
