package com.sudran.omnisearch.android.app;

import java.util.regex.Pattern;

import com.sudran.omnisearch.android.framework.ISearchKey;
import com.sudran.omnisearch.android.framework.ISearchableElement;
import com.sudran.omnisearch.android.framework.Match;
import com.sudran.omnisearch.android.framework.ViewDetails;

public class SearchableContact implements ISearchableElement {

	private static final long serialVersionUID = -1689145649170975868L;

	@Override
	public ISearchKey getPrimarySearchKey() {
		return null;
	}

	@Override
	public CharSequence getPrimarySearchContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ViewDetails getViewDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Match performSearch(Pattern regex) {
		// TODO Auto-generated method stub
		return null;
	}

}
