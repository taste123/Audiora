package com.example.audiora.model.rss;

import com.google.gson.annotations.SerializedName;

public class Id{

	@SerializedName("label")
	private String label;

	@SerializedName("attributes")
	private Attributes attributes;

	public String getLabel(){
		return label;
	}

	public Attributes getAttributes(){
		return attributes;
	}
}
