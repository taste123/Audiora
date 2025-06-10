package com.example.audiora.model.rss;

import com.google.gson.annotations.SerializedName;

public class Link{

	@SerializedName("attributes")
	private Attributes attributes;

	public Attributes getAttributes(){
		return attributes;
	}
}
