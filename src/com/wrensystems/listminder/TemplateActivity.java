package com.wrensystems.listminder;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class TemplateActivity extends SingleFragmentActivity {
	private long mTemplateId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mTemplateId = getIntent().getLongExtra(TemplateFragment.EXTRA_TEMPLATE_ID, 0);
		super.onCreate(savedInstanceState);		
	}

	@Override
	protected Fragment createFragment() {
		return TemplateFragment.newInstance(mTemplateId);
	}

}
