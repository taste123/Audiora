package com.example.audiora.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response{

	@SerializedName("resultCount")
	private int resultCount;

	@SerializedName("results")
	private List<ResultsItem> results;

	public int getResultCount(){
		return resultCount;
	}

	public List<ResultsItem> getResults(){
		return results;
	}
}
