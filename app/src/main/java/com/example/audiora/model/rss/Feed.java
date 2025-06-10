package com.example.audiora.model.rss;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Feed {

	@SerializedName("author")
	private Author author;

	@SerializedName("entry")
	private List<Entry> entry; // <<< THIS IS THE CRUCIAL CHANGE: It's now a List

	@SerializedName("updated")
	private Updated updated;

	@SerializedName("rights")
	private Rights rights;

	@SerializedName("title")
	private Title title;

	@SerializedName("icon")
	private Icon icon;

	@SerializedName("link")
	private List<LinkItem> link;

	@SerializedName("id")
	private Id id;

	// Getter methods
	public Author getAuthor() {
		return author;
	}

	public List<Entry> getEntry() { // <<< THIS NOW CORRECTLY RETURNS A LIST
		return entry;
	}

	public Updated getUpdated() {
		return updated;
	}

	public Rights getRights() {
		return rights;
	}

	public Title getTitle() {
		return title;
	}

	public Icon getIcon() {
		return icon;
	}

	public List<LinkItem> getLink() {
		return link;
	}

	public Id getId() {
		return id;
	}
}