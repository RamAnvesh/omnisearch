package com.sudran.appman;

import java.util.List;
import java.util.regex.Pattern;

import android.os.Process;
import android.widget.LinearLayout;

public class SearchThread extends Thread{
	private long timeStamp;
	private String searchString;
	private boolean restartSearch;
	private Pattern searchRegex;
	private UniSearchHome uniSearchHomeActivity;
	private Object objectLock = new Object();
	
	public SearchThread(UniSearchHome uniSearchHomeActivity) {
		this.uniSearchHomeActivity = uniSearchHomeActivity;
		setPriority(Process.THREAD_PRIORITY_BACKGROUND);
	}
	
	public synchronized Pattern getSearchRegex() {
		return searchRegex;
	}
	
	public synchronized void setSearchRegex(Pattern searchRegex) {
		this.searchRegex = searchRegex;
	}
	
	synchronized String getSearchString() {
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
			String searchString = UniSearchHome.WHITE_SPACE_PATTERN.matcher(rawSearchString).replaceAll("");
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
			final LinearLayout appListView = (LinearLayout) uniSearchHomeActivity.findViewById(R.id.appList);
			List<? extends ISearchableElement> appList = uniSearchHomeActivity.getAppList();
			if(appList == null)
				return;
			while(index < appList.size()){
				final ISearchableElement app = appList.get(index);
				if(isRestartSearch()){
					continue mainLoop;
				}
				uniSearchHomeActivity.addOrRemoveAppView(app, appListView, null);
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
}
