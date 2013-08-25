package com.sudran.appman;


public class Match {
	private boolean matches;
	private CharSequence matchedSpan;

	public Match(boolean matches, CharSequence matchedSpan) {
		this.matches = matches;
		this.matchedSpan = matchedSpan;
	}

	public boolean isMatches() {
		return matches;
	}
	
	public CharSequence getMatchedSpan() {
		return matchedSpan;
	}
}
