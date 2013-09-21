package com.sudran.omnisearch.android.contact;

import com.sudran.omnisearch.android.framework.BaseSearchableElement;

public class SearchableContact extends BaseSearchableElement {

	private static final long serialVersionUID = -1689145649170975868L;
	private String displayName;
	private String photoUri;

	public SearchableContact(String id, String displayName, String photoUri) {
		this.displayName = displayName;
		this.photoUri = photoUri;
	}
	
	public String getImageUri() {
		return photoUri;
	}
	
	@Override
	public CharSequence getPrimarySearchContent() {
		return displayName;
	}

}
