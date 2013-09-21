package com.sudran.omnisearch.android.framework;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewDetails {
	private int index;
	private View searchResult;
	private TextView primaryTextDisplay;
	private ImageView icon;
	
	public ViewDetails(View view, TextView primaryTextDisplay, ImageView icon, int index) {
		this.searchResult = view;
		this.primaryTextDisplay = primaryTextDisplay;
		this.icon = icon;
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
	
	public ImageView getIconImageView() {
		return icon;
	}
	
}
