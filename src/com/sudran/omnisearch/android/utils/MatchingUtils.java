package com.sudran.omnisearch.android.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.sudran.omnisearch.android.framework.Match;

public final class MatchingUtils {
	private static ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.BLUE);
	
	public static Match checkForMatch(Pattern regex, CharSequence searchable) {
		Matcher matcher = regex.matcher(searchable);
		boolean matches = false;
		Spannable spannableApplabel = new SpannableString(searchable);
		while(matcher.find()){
			matches = true;
			int start = matcher.start();
			int end = matcher.end();
			//spannableApplabel.setSpan(styleSpan, start, end, 0);
			spannableApplabel.setSpan(foregroundColorSpan, start, end, 0);
		}
		return new Match(matches, spannableApplabel, regex);
	}
}
