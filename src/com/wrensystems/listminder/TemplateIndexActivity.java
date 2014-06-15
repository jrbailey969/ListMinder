package com.wrensystems.listminder;

import android.support.v4.app.Fragment;

public class TemplateIndexActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new TemplateIndexFragment();
	}

}
