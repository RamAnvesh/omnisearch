package com.sudran.appman;

import java.io.Serializable;

public interface ISearchableElement extends Serializable{
	ISearchKey getPrimarySearchKey();
	CharSequence getPrimarySearchContent();
	ViewDetails getViewDetails();
}
