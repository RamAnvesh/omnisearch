package com.sudran.omnisearch.android.framework;

import java.util.LinkedList;
import java.util.List;

public abstract class BaseSearchProvider<T extends ISearchableElement> {
	protected List<T> searchableElements = new LinkedList<T>();;
	

	protected abstract void load();
	
	public List<T> getSearchableElements() {
		return searchableElements;
	}
}
