package com.example.audiora.model.rss;

import com.google.gson.annotations.SerializedName;

public class LinkItem{

	@SerializedName("attributes")
	private Attributes attributes;

	@SerializedName("im:duration")
	private ImDuration imDuration;

	public Attributes getAttributes(){
		return attributes;
	}

	public ImDuration getImDuration(){
		return imDuration;
	}
}
