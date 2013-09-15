package com.sudran.omnisearch.android.framework;

public class OnTextChangeEvent {

	private CharSequence charSequence;
	private int start;
	private int before;
	private int count;
	private long timeOfEventInNano;

	/**
     * Within <code>charSequence</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * have just replaced old text that had length <code>before</code>.
     * 
	 * @param charSequence
	 * @param start
	 * @param before
	 * @param count
	 */
	public OnTextChangeEvent(CharSequence charSequence, int start, int before, int count, long timeOfEventInNano) {
		this.charSequence = charSequence;
		this.start = start;
		this.before = before;
		this.count = count;
		this.timeOfEventInNano = timeOfEventInNano;
	}

	public CharSequence getCharSequence() {
		return charSequence == null? "" : charSequence;
	}

	public int getStart() {
		return start;
	}

	public int getBefore() {
		return before;
	}

	public int getCount() {
		return count;
	}
	
	public long getTimeOfEventInNano() {
		return timeOfEventInNano;
	}

}
