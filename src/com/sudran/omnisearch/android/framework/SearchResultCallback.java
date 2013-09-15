package com.sudran.omnisearch.android.framework;


public abstract class SearchResultCallback {
	public abstract boolean isCallbackExpired();
	
	public abstract void newSearchResult(ISearchableElement searchableElement, Match match);
}
