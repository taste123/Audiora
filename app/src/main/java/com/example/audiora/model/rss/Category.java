package com.example.audiora.model.rss;

import com.google.gson.annotations.SerializedName;

public class Category{

	@SerializedName("attributes")
	private Attributes attributes;

	public Attributes getAttributes(){
		return attributes;
	}
}
