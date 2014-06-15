package com.wrensystems.listminder;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class SelectTemplateActivity extends SingleFragmentActivity {
	private long mListId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mListId = getIntent().getLongExtra(SelectTemplateFragment.EXTRA_LIST_ID, 0);
		super.onCreate(savedInstanceState);		
	}

	@Override
	protected Fragment createFragment() {
		return SelectTemplateFragment.newInstance(mListId);
	}

}
