package com.sudran.appman;

import java.util.regex.Pattern;

import com.sudran.appman.utils.ReusableThread;

public class ReusableSearchThread extends ReusableThread{
	private long timeStamp;
	private String searchString;
	private boolean restartSearch;
	private Pattern searchRegex;
	private ThreadCreator threadCreator;
	
	public ReusableSearchThread(UniSearchHome uniSearchHomeActivity) {
		super(true);
		threadCreator = new ThreadCreator(uniSearchHomeActivity);
		threadCreator.setSearchThread(this);
		setThreadCreator(threadCreator);
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
	
	public boolean isRestartSearch() {
		return restartSearch;
	}
	
	private static class ThreadCreator implements IThreadCreator{
		
		private ReusableSearchThread searchThread;
		private UniSearchHome uniSearchHomeActivity;

		public ThreadCreator(UniSearchHome uniSearchHomeActivity) {
			this.uniSearchHomeActivity = uniSearchHomeActivity;
		}

		public void setSearchThread(ReusableSearchThread searchThread) {
			this.searchThread = searchThread;
		}
		
		@Override
		public Thread createNewThread() {
			return new TemporaryThread(uniSearchHomeActivity, this.searchThread);
		}
	}

	synchronized void setRestartSearch(boolean b) {
		restartSearch = b;
	}
}
