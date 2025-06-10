package com.example.audiora.model.rss;

import com.google.gson.annotations.SerializedName;

public class ImCollection{

	@SerializedName("im:name")
	private ImName imName;

	@SerializedName("im:contentType")
	private ImContentType imContentType;

	@SerializedName("link")
	private Link link;

	public ImName getImName(){
		return imName;
	}

	public ImContentType getImContentType(){
		return imContentType;
	}

	public Link getLink(){
		return link;
	}
}
