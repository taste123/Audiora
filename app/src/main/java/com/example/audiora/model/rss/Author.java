package com.example.audiora.model.rss;

import com.google.gson.annotations.SerializedName;

public class Author{

	@SerializedName("name")
	private Name name;

	@SerializedName("uri")
	private Uri uri;

	public Name getName(){
		return name;
	}

	public Uri getUri(){
		return uri;
	}
}
