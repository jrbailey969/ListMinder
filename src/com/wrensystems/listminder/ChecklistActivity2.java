package com.wrensystems.listminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ChecklistActivity2 extends SingleFragmentActivity {
	private long mListId;
	private ChecklistFragment2 mFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mListId = getIntent().getLongExtra(ChecklistFragment2.EXTRA_LIST_ID, 0);
		super.onCreate(savedInstanceState);		
	}

	@Override
	protected Fragment createFragment() {
		mFragment = ChecklistFragment2.newInstance(mListId);
		return mFragment;
	}
	
    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
    	mFragment.onActivityResult(requestCode, responseCode, data);
    }
    
	

}
