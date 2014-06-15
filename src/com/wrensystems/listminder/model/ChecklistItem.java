package com.wrensystems.listminder.model;

public class ChecklistItem {
	private long mId;
	private String mName;
	private boolean mCompleted;
	
	public ChecklistItem() {
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

	public boolean isCompleted() {
		return mCompleted;
	}

	public void setCompleted(boolean completed) {
		mCompleted = completed;
	}

}
