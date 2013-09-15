package com.sudran.omnisearch.android.framework;

import java.io.Serializable;


public interface ISearchableElement extends Serializable{
	CharSequence getPrimarySearchContent();
	ViewDetails getViewDetails();
}
