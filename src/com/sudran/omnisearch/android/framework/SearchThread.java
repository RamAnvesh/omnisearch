package com.sudran.omnisearch.android.framework;

import java.util.regex.Pattern;

import android.os.Process;


public class SearchThread<SP extends BaseSearchProvider<? extends ISearchableElement>> extends Thread{
	private long timeStamp;
	private Pattern searchRegex;
	private boolean restartSearch;
	private OmniSearchHome uniSearchHomeActivity;
	private Object objectLock = new Object();
	private SP searchProvider;

	public SearchThread(OmniSearchHome uniSearchHomeActivity, SP searchProvider) {
		this.uniSearchHomeActivity = uniSearchHomeActivity;
		this.searchProvider = searchProvider;
		setPriority(Process.THREAD_PRIORITY_BACKGROUND);
	}

	synchronized void setSearchRegex(Pattern searchRegex, long timeStamp){
		long oldTimeStamp = this.timeStamp;
		if(oldTimeStamp < timeStamp){
			this.searchRegex = searchRegex;
			this.timeStamp = timeStamp;
			setRestart(true);
		}
	}
	
	private Pattern getSearchRegex() {
		return searchRegex;
	}

	private synchronized void setRestart(boolean b) {
		this.restartSearch = b;
	}

	private boolean isRestartSearch() {
		if(restartSearch)
			System.out.println("Search interupted by user input");
		return restartSearch;
	}

	void setRestartSearch(boolean b) {
		restartSearch = b;
	}

	@Override
	public void run() {
		mainLoop:
			while(true){
				setRestart(false);
				final Pattern searchRegex = getSearchRegex();
				String rawSearchString = searchRegex.pattern();
				if(isRestartSearch()){
					continue mainLoop;
				}
				final boolean isEmptySearchString;
				if(rawSearchString.length() == 0){
					isEmptySearchString = true;
				} else {
					isEmptySearchString = false;
				}
				final SearchResultCallback searchResultCallback = new SearchResultCallback(){
					@Override
					public void newSearchResult(ISearchableElement searchableElement, Match match) {

						if(!isRestartSearch()){
							uniSearchHomeActivity.addOrRemoveAppView(searchableElement, match);
						}
					}

					@Override
					public boolean isCallbackExpired() {
						return isRestartSearch();
					}
				};
				Thread thread = new Thread(){
					@Override
					public void run() {
						if(!isRestartSearch())
							searchProvider.searchFor(searchRegex, searchResultCallback);
					}
				};
				thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
				if(!isEmptySearchString)
					thread.start();
				synchronized (objectLock) {
					try {
						while(!restartSearch){
							objectLock.wait();
						}
					} catch (InterruptedException e) {

					}
				}
			}
	}

	public Object getObjectLock() {
		return objectLock;
	}

	public SP getSearchProvider() {
		return searchProvider;
	}
}
