package com.example.audiora.model.rss;

import com.google.gson.annotations.SerializedName;

public class ImContentType{

	@SerializedName("im:contentType")
	private ImContentType imContentType;

	@SerializedName("attributes")
	private Attributes attributes;

	public ImContentType getImContentType(){
		return imContentType;
	}

	public Attributes getAttributes(){
		return attributes;
	}
}
