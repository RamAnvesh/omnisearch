package com.sudran.omnisearch.android.app;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sudran.appman.R;
import com.sudran.omnisearch.android.framework.BaseSearchProvider;
import com.sudran.omnisearch.android.framework.Match;
import com.sudran.omnisearch.android.framework.SearchResultCallback;
import com.sudran.omnisearch.android.framework.ViewDetails;
import com.sudran.omnisearch.android.utils.MatchingUtils;

public class ApplicationSearchProvider extends BaseSearchProvider<SearchableApplication> {
	protected List<SearchableApplication> searchableElements = new ArrayList<SearchableApplication>();
	private PackageManager packageManager;
	private Activity searchActivity;

	public ApplicationSearchProvider(PackageManager packageManager, Activity searchActivity) {
		this.packageManager = packageManager;
		this.searchActivity = searchActivity;
	}
	
	@Override
	public void load() {
		long currentTimeMillis = System.currentTimeMillis();
		List<ApplicationInfo> applications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
		int index = 0;
		final List<Runnable> longRunningOperations = new LinkedList<Runnable>();
		for (final ApplicationInfo appInfo : applications) {
			final SearchableApplication app = new SearchableApplication(packageManager, appInfo);
			searchableElements.add(app);
			makeUiElement(index++, app);
			Runnable runnable = new Runnable(){
				public void run() {
					try {
						//android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);
						Drawable drawable = packageManager.getApplicationIcon(app.getAndroidPackageName().toString());
						app.getViewDetails().getIconImageVIew().setImageDrawable(drawable);
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
				}
			};
			longRunningOperations.add(runnable);
		}
		Thread loadIconsThread = new Thread(){
			@Override
			public void run() {
				for (Runnable runnable : longRunningOperations) {
					runnable.run();
				}
			}
		};
		loadIconsThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
		loadIconsThread.start();
		System.err.println("Loading app list: " + (System.currentTimeMillis() - currentTimeMillis));
	}

	private void makeUiElement(int index, final SearchableApplication app) {
		final CharSequence applicationLabel = app.getApplicationLabel() + "\n";
		final LinearLayout appDetailsLinearLayout = new LinearLayout(searchActivity);
		final LayoutParams appDetailsLayoutParams = 
				new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		appDetailsLinearLayout.setLayoutParams(appDetailsLayoutParams);
		final TextView textView = new TextView(searchActivity);
		final LayoutParams appNameLayoutParams = 
				new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		textView.setLayoutParams(appNameLayoutParams);
		textView.setText(applicationLabel);
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				app.openElement(packageManager, searchActivity);
			}
		});
		int side = (int) searchActivity.getResources().
				getDimension(R.dimen.preferred_app_detail_height);
		final ImageView appIcon = new ImageView(searchActivity);
		final LayoutParams appIconLayoutParams = new LayoutParams(side, side);
		appIcon.setLayoutParams(appIconLayoutParams);
		appDetailsLinearLayout.addView(appIcon);
		appDetailsLinearLayout.addView(textView);
		app.setViewDetails(new ViewDetails(appDetailsLinearLayout,textView, appIcon, index));
	}
	
	public void searchFor(Pattern regex, SearchResultCallback searchResultCallback){
		List<SearchableApplication> searchableElements = getSearchableElements();
		if(searchableElements == null)
			return;
		int index =0;
		while(index < searchableElements.size()){
			if(searchResultCallback.isCallbackExpired()){
				return;
			}
			final SearchableApplication searchableElement = searchableElements.get(index);
			Match match = performSearch(searchableElement, regex) ;
			searchResultCallback.newSearchResult(searchableElement, match);
			index++;
		}
	}

	private Match performSearch(SearchableApplication searchableElement, Pattern regex) {
		return regex != null? MatchingUtils.checkForMatch(regex,searchableElement.getApplicationLabel()) 
				: new Match(false, searchableElement.getPrimarySearchContent(), null);
	}
	
	public List<SearchableApplication> getSearchableElements() {
		return searchableElements;
	}
}
