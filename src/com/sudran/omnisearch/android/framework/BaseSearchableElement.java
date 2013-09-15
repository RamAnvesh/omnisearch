package com.sudran.omnisearch.android.framework;

public abstract class BaseSearchableElement implements ISearchableElement {

	private static final long serialVersionUID = 2821529480888033456L;

	private transient ViewDetails viewDetails;

	@Override
	public ViewDetails getViewDetails() {
		return viewDetails;
	}
	
	public void setViewDetails(ViewDetails viewDetails) {
		this.viewDetails = viewDetails;
	}
}
