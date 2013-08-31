package com.sudran.omnisearch.android.framework;

import java.io.Serializable;
import java.util.regex.Pattern;


public interface ISearchableElement extends Serializable{
	ISearchKey getPrimarySearchKey();
	CharSequence getPrimarySearchContent();
	ViewDetails getViewDetails();
	Match performSearch(Pattern regex);
}
