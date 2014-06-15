package com.wrensystems.listminder.model;

public class TemplateItem {
	private long mId;
	private String mName;
	
	public TemplateItem() {
		mId = -1;
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

}
