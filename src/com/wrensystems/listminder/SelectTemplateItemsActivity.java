package com.wrensystems.listminder;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class SelectTemplateItemsActivity extends SingleFragmentActivity {
	private long mTemplateId;
	private long mListId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mTemplateId = getIntent().getLongExtra(SelectTemplateItemsFragment.EXTRA_TEMPLATE_ID, 0);
		mListId = getIntent().getLongExtra(SelectTemplateItemsFragment.EXTRA_LIST_ID, 0);
		super.onCreate(savedInstanceState);		
	}

	@Override
	protected Fragment createFragment() {
		return SelectTemplateItemsFragment.newInstance(mTemplateId, mListId);
	}

}
