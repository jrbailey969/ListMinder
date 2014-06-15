package com.wrensystems.listminder.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Template {
	private long mId;
	private String mName;
	private Date mLastUpdated;
	private ArrayList<TemplateItem> mItems;
	
	public Template() {
		mId = -1;
		mLastUpdated = new Date();
		mItems = new ArrayList<TemplateItem>();
	}	

	@Override
	public String toString() {
		return getName();
	}

	public long getId() {
		return mId;
	}
	
	public void setId(long id) {
		mId = id;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public Date getLastUpdated() {
		return mLastUpdated;
	}
	
	public void setLastUpdated(Date date) {
		mLastUpdated = date;
	}
	
	public ArrayList<TemplateItem> getItems() {
		return mItems;
	}

	public void setItems(ArrayList<TemplateItem> items) {
		mItems = items;
	}
	
}
