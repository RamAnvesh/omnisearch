package com.sudran.appman;

import java.util.List;
import java.util.regex.Pattern;

import android.widget.LinearLayout;

class TemporaryThread extends Thread{
	
	private ReusableSearchThread searchThread;
	private UniSearchHome uniSearchHomeActivity;

	public TemporaryThread(UniSearchHome uniSearchHomeActivity, ReusableSearchThread reusableThread) {
		this.uniSearchHomeActivity = uniSearchHomeActivity;
		this.searchThread = reusableThread;
	}

	public void run() {
		String searchString = UniSearchHome.WHITE_SPACE_PATTERN.matcher(this.searchThread.getSearchString()).replaceAll("");
		StringBuilder searchRegexBuilder = new StringBuilder();
		for (int i = 0; i < searchString.length(); i++) {
			char charAt = searchString.charAt(i);
			searchRegexBuilder.append(charAt);
			searchRegexBuilder.append("\\s*");
			if(searchThread.isRestartSearch()){
				searchThread.setRestartSearch(false);
				run();
				return;
			}
		}
		Pattern regex = Pattern.compile(searchRegexBuilder.toString(), Pattern.CASE_INSENSITIVE);
		searchThread.setSearchRegex(regex);
		int index =0;
		final LinearLayout appListView = (LinearLayout) uniSearchHomeActivity.findViewById(R.id.appList);
		List<SearchableApplication> appList = uniSearchHomeActivity.getAppList();
		if(appList == null)
			return;
		while(index < appList.size()){
			final SearchableApplication app = appList.get(index);
			if(searchThread.isRestartSearch()){
				searchThread.setRestartSearch(false);
				run();
				return;
			}
			uniSearchHomeActivity.addOrRemoveAppView(app, appListView, null);
			index++;
		}
	};

}
