package com.wrensystems.listminder;

import android.support.v4.app.Fragment;

public class ListIndexActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new ListIndexFragment();
	}

}
