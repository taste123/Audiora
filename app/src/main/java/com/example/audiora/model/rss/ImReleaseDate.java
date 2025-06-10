package com.example.audiora.model.rss;

import com.google.gson.annotations.SerializedName;

public class ImReleaseDate{

	@SerializedName("attributes")
	private Attributes attributes;

	@SerializedName("label")
	private String label;

	public Attributes getAttributes(){
		return attributes;
	}

	public String getLabel(){
		return label;
	}
}
