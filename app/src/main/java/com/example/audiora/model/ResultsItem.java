package com.example.audiora.model;

import com.google.gson.annotations.SerializedName;

public class ResultsItem{

	public ResultsItem(int trackId, String trackName, String artistName, String collectionName, String artworkUrl100, String previewUrl) {
		this.trackId = trackId;
		this.trackName = trackName;
		this.artistName = artistName;
		this.collectionName = collectionName;
		this.artworkUrl100 = artworkUrl100;
		this.previewUrl = previewUrl;
	}

	@SerializedName("artworkUrl100")
	private String artworkUrl100;

	@SerializedName("trackTimeMillis")
	private int trackTimeMillis;

	@SerializedName("country")
	private String country;

	@SerializedName("previewUrl")
	private String previewUrl;

	@SerializedName("artistId")
	private int artistId;

	@SerializedName("trackName")
	private String trackName;

	@SerializedName("collectionName")
	private String collectionName;

	@SerializedName("artistViewUrl")
	private String artistViewUrl;

	@SerializedName("discNumber")
	private int discNumber;

	@SerializedName("trackCount")
	private int trackCount;

	@SerializedName("artworkUrl30")
	private String artworkUrl30;

	@SerializedName("wrapperType")
	private String wrapperType;

	@SerializedName("currency")
	private String currency;

	@SerializedName("collectionId")
	private int collectionId;

	@SerializedName("isStreamable")
	private boolean isStreamable;

	@SerializedName("trackExplicitness")
	private String trackExplicitness;

	@SerializedName("collectionViewUrl")
	private String collectionViewUrl;

	@SerializedName("trackNumber")
	private int trackNumber;

	@SerializedName("releaseDate")
	private String releaseDate;

	@SerializedName("kind")
	private String kind;

	@SerializedName("trackId")
	private int trackId;

	@SerializedName("collectionPrice")
	private Object collectionPrice;

	@SerializedName("discCount")
	private int discCount;

	@SerializedName("primaryGenreName")
	private String primaryGenreName;

	@SerializedName("trackPrice")
	private Object trackPrice;

	@SerializedName("collectionExplicitness")
	private String collectionExplicitness;

	@SerializedName("trackViewUrl")
	private String trackViewUrl;

	@SerializedName("artworkUrl60")
	private String artworkUrl60;

	@SerializedName("trackCensoredName")
	private String trackCensoredName;

	@SerializedName("artistName")
	private String artistName;

	@SerializedName("collectionCensoredName")
	private String collectionCensoredName;

	public String getArtworkUrl100(){
		return artworkUrl100;
	}

	public int getTrackTimeMillis(){
		return trackTimeMillis;
	}

	public String getCountry(){
		return country;
	}

	public String getPreviewUrl(){
		return previewUrl;
	}

	public int getArtistId(){
		return artistId;
	}

	public String getTrackName(){
		return trackName;
	}

	public String getCollectionName(){
		return collectionName;
	}

	public String getArtistViewUrl(){
		return artistViewUrl;
	}

	public int getDiscNumber(){
		return discNumber;
	}

	public int getTrackCount(){
		return trackCount;
	}

	public String getArtworkUrl30(){
		return artworkUrl30;
	}

	public String getWrapperType(){
		return wrapperType;
	}

	public String getCurrency(){
		return currency;
	}

	public int getCollectionId(){
		return collectionId;
	}

	public boolean isIsStreamable(){
		return isStreamable;
	}

	public String getTrackExplicitness(){
		return trackExplicitness;
	}

	public String getCollectionViewUrl(){
		return collectionViewUrl;
	}

	public int getTrackNumber(){
		return trackNumber;
	}

	public String getReleaseDate(){
		return releaseDate;
	}

	public String getKind(){
		return kind;
	}

	public int getTrackId(){
		return trackId;
	}

	public Object getCollectionPrice(){
		return collectionPrice;
	}

	public int getDiscCount(){
		return discCount;
	}

	public String getPrimaryGenreName(){
		return primaryGenreName;
	}

	public Object getTrackPrice(){
		return trackPrice;
	}

	public String getCollectionExplicitness(){
		return collectionExplicitness;
	}

	public String getTrackViewUrl(){
		return trackViewUrl;
	}

	public String getArtworkUrl60(){
		return artworkUrl60;
	}

	public String getTrackCensoredName(){
		return trackCensoredName;
	}

	public String getArtistName(){
		return artistName;
	}

	public String getCollectionCensoredName(){
		return collectionCensoredName;
	}
}
