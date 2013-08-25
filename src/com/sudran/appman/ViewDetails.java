package com.sudran.appman;

import android.view.View;
import android.widget.TextView;

public class ViewDetails {
	private int index;
	private View searchResult;
	private TextView primaryTextDisplay;
	
	public ViewDetails(View view, TextView primaryTextDisplay, int index) {
		this.searchResult = view;
		this.primaryTextDisplay = primaryTextDisplay;
		this.index = index;
	}
	
	public TextView getPrimaryTextDisplay() {
		return primaryTextDisplay;
	}

	public View getSearchResultView(){
		return searchResult;
	}
	
	public int getIndexInParent(){
		return index;
	}
	
	public void setIndexInParent(int index) {
		this.index = index;
	}
	
}
