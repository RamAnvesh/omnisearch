package com.sudran.omnisearch.android.app;

import com.sudran.omnisearch.android.framework.ISearchKey;

public enum AppSearchKeys implements ISearchKey{
	APP_LABEL("app_label"), 
	APP_PACKAGE_NAME("android_package_name");

	private CharSequence searchKeyName;

	private AppSearchKeys(CharSequence searchKeyName) {
		this.searchKeyName = searchKeyName;
	}
	
	@Override
	public CharSequence getSearchKeyName() {
		return searchKeyName;
	}
	
}
