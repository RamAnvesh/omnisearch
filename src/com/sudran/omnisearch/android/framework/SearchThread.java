package com.sudran.omnisearch.android.framework;

import java.util.List;
import java.util.regex.Pattern;

import android.os.Process;


public class SearchThread<SP extends BaseSearchProvider<? extends ISearchableElement>> extends Thread{
	private long timeStamp;
	private String searchString;
	private boolean restartSearch;
	private Pattern searchRegex;
	private OmniSearchHome uniSearchHomeActivity;
	private Object objectLock = new Object();
	private SP searchProvider;
	
	public SearchThread(OmniSearchHome uniSearchHomeActivity, SP searchProvider) {
		this.uniSearchHomeActivity = uniSearchHomeActivity;
		this.searchProvider = searchProvider;
		setPriority(Process.THREAD_PRIORITY_BACKGROUND);
	}
	
	private Pattern getSearchRegex() {
		return searchRegex;
	}
	
	private void setSearchRegex(Pattern searchRegex) {
		this.searchRegex = searchRegex;
	}
	
	private synchronized String getSearchString() {
		return searchString;
	}

	synchronized void setSearchString(String searchString, long timeStamp){
		long oldTimeStamp = this.timeStamp;
		if(oldTimeStamp < timeStamp){
			this.searchString = searchString;
			this.timeStamp = timeStamp;
			setRestart(true);
		}
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
		long start = System.nanoTime();
			setRestart(false);
			String rawSearchString = getSearchString();
			if(isRestartSearch()){
				continue mainLoop;
			}
			String searchString = OmniSearchHome.WHITE_SPACE_PATTERN.
					matcher(rawSearchString).replaceAll("");
			StringBuilder searchRegexBuilder = new StringBuilder();
			for (int i = 0; i < searchString.length(); i++) {
				char charAt = searchString.charAt(i);
				searchRegexBuilder.append(charAt);
				searchRegexBuilder.append("\\s*");
				if(isRestartSearch()){
					continue mainLoop;
				}
			}
			Pattern regex = Pattern.compile(searchRegexBuilder.toString(), Pattern.CASE_INSENSITIVE);
			setSearchRegex(regex);
			int index =0;
			List<? extends ISearchableElement> appList = searchProvider.getSearchableElements();
			if(appList == null)
				return;
			while(index < appList.size()){
				final ISearchableElement searchableElement = appList.get(index);
				if(isRestartSearch()){
					continue mainLoop;
				}
				Match match = searchableElement.performSearch(getSearchRegex());
				uniSearchHomeActivity.addOrRemoveAppView(searchableElement, match);
				index++;
			}
		long end = System.nanoTime();
		System.out.println("One search took: " + (end - start) + " nanos");
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
