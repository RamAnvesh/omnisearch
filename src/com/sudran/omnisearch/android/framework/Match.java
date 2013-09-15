package com.sudran.omnisearch.android.framework;

import java.util.regex.Pattern;


public class Match {
	private boolean matches;
	private CharSequence matchedSpan;
	/*
	 * Regex for which this match occured
	 */
	private Pattern matchingRegex;

	public Match(boolean matches, CharSequence matchedSpan, Pattern matchingRegex) {
		this.matches = matches;
		this.matchedSpan = matchedSpan;
		this.matchingRegex = matchingRegex;
	}

	public boolean isMatches() {
		return matches;
	}
	
	public CharSequence getMatchedSpan() {
		return matchedSpan;
	}
	
	public Pattern getMatchingRegex() {
		return matchingRegex;
	}
}
