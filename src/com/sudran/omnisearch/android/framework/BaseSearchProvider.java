package com.sudran.omnisearch.android.framework;

import java.util.List;
import java.util.regex.Pattern;

import com.sudran.omnisearch.android.utils.MatchingUtils;

public abstract class BaseSearchProvider<T extends ISearchableElement> {

	protected abstract void load();
	
	public void searchFor(Pattern regex, SearchResultCallback searchResultCallback){
		List<? extends ISearchableElement> searchableElements = getSearchableElements();
		if(searchableElements == null)
			return;
		int index =0;
		while(index < searchableElements.size()){
			if(searchResultCallback.isCallbackExpired()){
				return;
			}
			final ISearchableElement searchableElement = searchableElements.get(index);
			Match match = performSearch(searchableElement, regex) ;
			searchResultCallback.newSearchResult(searchableElement, match);
			index++;
		}
	}
	
	protected abstract List<? extends ISearchableElement> getSearchableElements();

	protected Match performSearch(ISearchableElement searchableElement, Pattern regex) {
		CharSequence primarySearchContent = searchableElement.getPrimarySearchContent();
		return regex != null? MatchingUtils.checkForMatch(regex,primarySearchContent) 
				: new Match(false, primarySearchContent, null);
	}
}
