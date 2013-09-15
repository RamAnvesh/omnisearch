package com.sudran.omnisearch.android.framework;

import java.util.regex.Pattern;

public abstract class BaseSearchProvider<T extends ISearchableElement> {

	protected abstract void load();
	
	public abstract void searchFor(Pattern regex, SearchResultCallback searchResultCallback);
}
