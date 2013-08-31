package com.sudran.omnisearch.android.views;

import java.util.Collection;
import java.util.LinkedList;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ObservableScrollView extends ScrollView {

	private Collection<OnScrollChangedListener> onScrollChangedListeners = new LinkedList<ObservableScrollView.OnScrollChangedListener>();

	public ObservableScrollView(Context context) {
		super(context);
	}
	
	public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		for (OnScrollChangedListener listener : onScrollChangedListeners) {
			listener.onScrollChanged(this, l, t, oldl, oldt);
		}
	}
	
	public void addOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
		onScrollChangedListeners.add(onScrollChangedListener);
	}
	
	public static interface OnScrollChangedListener{
		boolean onScrollChanged(ObservableScrollView observableScrollView, int l, int t, int oldl, int oldt);
	}

}
