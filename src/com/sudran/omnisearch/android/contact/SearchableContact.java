package com.sudran.omnisearch.android.contact;

import com.sudran.omnisearch.android.framework.BaseSearchableElement;

public class SearchableContact extends BaseSearchableElement {

	private static final long serialVersionUID = -1689145649170975868L;
	private String displayName;

	public SearchableContact(String id, String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public CharSequence getPrimarySearchContent() {
		return displayName;
	}

}
