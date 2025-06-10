package com.example.audiora.model.rss;

import com.google.gson.annotations.SerializedName;

public class Title{

	@SerializedName("label")
	private String label;

	public String getLabel(){
		return label;
	}
}
