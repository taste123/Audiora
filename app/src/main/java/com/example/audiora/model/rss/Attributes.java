package com.example.audiora.model.rss;

import com.google.gson.annotations.SerializedName;

public class Attributes{

	@SerializedName("label")
	private String label;

	@SerializedName("scheme")
	private String scheme;

	@SerializedName("term")
	private String term;

	@SerializedName("im:id")
	private String imId;

	@SerializedName("amount")
	private String amount;

	@SerializedName("currency")
	private String currency;

	@SerializedName("rel")
	private String rel;

	@SerializedName("href")
	private String href;

	@SerializedName("type")
	private String type;

	@SerializedName("height")
	private String height;

	@SerializedName("im:assetType")
	private String imAssetType;

	@SerializedName("title")
	private String title;

	public String getLabel(){
		return label;
	}

	public String getScheme(){
		return scheme;
	}

	public String getTerm(){
		return term;
	}

	public String getImId(){
		return imId;
	}

	public String getAmount(){
		return amount;
	}

	public String getCurrency(){
		return currency;
	}

	public String getRel(){
		return rel;
	}

	public String getHref(){
		return href;
	}

	public String getType(){
		return type;
	}

	public String getHeight(){
		return height;
	}

	public String getImAssetType(){
		return imAssetType;
	}

	public String getTitle(){
		return title;
	}
}
