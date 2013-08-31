package com.sudran.omnisearch.android.utils;

import java.lang.Thread.State;

public class ReusableThread {
	private Thread thread;
	private IThreadCreator threadCreator;
	private final boolean ignoreIllegalState;
	
	public static interface IThreadCreator{
		Thread createNewThread();
	}

	public ReusableThread(IThreadCreator threadCreator, boolean ignoreIllegalState) {
		this.ignoreIllegalState = ignoreIllegalState;
		if(threadCreator == null)
			throw new IllegalArgumentException("threadCreator cannot be null",new NullPointerException());
		this.threadCreator = threadCreator;
	}
	
	protected ReusableThread(boolean ignoreIllegalState) {
		this.ignoreIllegalState = ignoreIllegalState;
	}

	/**
	 * 
	 * @throws IllegalStateException if a waiting thread is started
	 */
	public synchronized void start() throws IllegalStateException{
		Thread thread = getThread();
		if(thread.getState() == Thread.State.TERMINATED)
			thread = createNewThread();
		switch (thread.getState()) {
		case NEW:
		case RUNNABLE:
			thread.start();
			break;
		default:
			if(!ignoreIllegalState)
				new IllegalStateException(ReusableThread.class.toString() + ".startTimerThread() : Trying to start a thread which is waiting");
			break;
		}
	}

	protected void setThreadCreator(IThreadCreator threadCreator) {
		this.threadCreator = threadCreator;
	}
	
	private Thread createNewThread() {
		return threadCreator.createNewThread();
	}
	
	public State getState() {
		return getThread().getState();
	}
	
	public void interrupt() {
		if(thread != null){
			thread.interrupt();
		}
	}
	
	public void sleep() {
		
	}
	
	private Thread getThread() {
		if(thread == null)
			thread = createNewThread();
		return thread;
	}
}
