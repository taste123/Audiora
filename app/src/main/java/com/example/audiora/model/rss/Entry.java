package com.example.audiora.model.rss;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Entry{

	@SerializedName("im:artist")
	private ImArtist imArtist;

	@SerializedName("im:name")
	private ImName imName;

	@SerializedName("im:contentType")
	private ImContentType imContentType;

	@SerializedName("im:image")
	private List<ImImageItem> imImage;

	@SerializedName("im:collection")
	private ImCollection imCollection;

	@SerializedName("rights")
	private Rights rights;

	@SerializedName("im:price")
	private ImPrice imPrice;

	@SerializedName("link")
	private List<LinkItem> link;

	@SerializedName("id")
	private Id id;

	@SerializedName("title")
	private Title title;

	@SerializedName("category")
	private Category category;

	@SerializedName("im:releaseDate")
	private ImReleaseDate imReleaseDate;

	public ImArtist getImArtist(){
		return imArtist;
	}

	public ImName getImName(){
		return imName;
	}

	public ImContentType getImContentType(){
		return imContentType;
	}

	public List<ImImageItem> getImImage(){
		return imImage;
	}

	public ImCollection getImCollection(){
		return imCollection;
	}

	public Rights getRights(){
		return rights;
	}

	public ImPrice getImPrice(){
		return imPrice;
	}

	public List<LinkItem> getLink(){
		return link;
	}

	public Id getId(){
		return id;
	}

	public Title getTitle(){
		return title;
	}

	public Category getCategory(){
		return category;
	}

	public ImReleaseDate getImReleaseDate(){
		return imReleaseDate;
	}
}
