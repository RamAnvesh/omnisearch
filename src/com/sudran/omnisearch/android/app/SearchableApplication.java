package com.sudran.omnisearch.android.app;

import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.widget.Toast;

import com.sudran.omnisearch.android.framework.ISearchKey;
import com.sudran.omnisearch.android.framework.ISearchableElement;
import com.sudran.omnisearch.android.framework.Match;
import com.sudran.omnisearch.android.framework.ViewDetails;
import com.sudran.omnisearch.android.utils.MatchingUtils;

public class SearchableApplication implements ISearchableElement {
	private static final long serialVersionUID = 1401506236148902982L;

	//private Map<ISearchKey, CharSequence> searchables = new HashMap<ISearchKey, CharSequence>();

	private transient ViewDetails viewDetails;

	private CharSequence applicationLabel;

	private String appPackageName;

	public SearchableApplication(PackageManager packageManager, ApplicationInfo appInfo) {
		init(packageManager, appInfo);
	}

	private void init(final PackageManager packageManager, final ApplicationInfo appInfo) {
		applicationLabel = packageManager.getApplicationLabel(appInfo);
		appPackageName = appInfo.packageName;
		
		//searchables.put(AppSearchKeys.APP_LABEL, applicationLabel);
		//searchables.put(AppSearchKeys.APP_PACKAGE_NAME, packageName);
	}

	public CharSequence getApplicationLabel() {
		//return searchables.get(AppSearchKeys.APP_LABEL);
		return applicationLabel;
	}

	@Override
	public ISearchKey getPrimarySearchKey() {
		return AppSearchKeys.APP_LABEL;
	}

	@Override
	public CharSequence getPrimarySearchContent() {
		return getApplicationLabel();
	}
	
	CharSequence getAndroidPackageName() {
		//return searchables.get(AppSearchKeys.APP_PACKAGE_NAME);
		return appPackageName;
	}
	
	@Override
	public ViewDetails getViewDetails() {
		return viewDetails;
	}
	
	public void setViewDetails(ViewDetails viewDetails) {
		this.viewDetails = viewDetails;
	}
	
	public void openElement(final PackageManager packageManager, Activity activity) {
		try{
			String androidPackageName = getAndroidPackageName().toString();
			Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(androidPackageName);
			if(launchIntentForPackage == null){
				launchIntentForPackage = new Intent();
				launchIntentForPackage.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				launchIntentForPackage.setPackage(androidPackageName);
				List<ResolveInfo> resolveinfo_list = 
						packageManager.queryIntentActivities(launchIntentForPackage, 0);
				for(ResolveInfo info:resolveinfo_list){
					if(info.activityInfo.packageName.equalsIgnoreCase(androidPackageName)){
						Intent launch_intent = new Intent();
						//launch_intent.addCategory(Intent.CATEGORY_LAUNCHER);
						launch_intent.setComponent(
								new ComponentName(androidPackageName, info.activityInfo.name));
						launch_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						try {
							activity.startActivity(launch_intent);
						} catch (Exception e) {
							continue;
						}
						break;
					}
				}
			} else {
				launchIntentForPackage.addCategory(Intent.CATEGORY_LAUNCHER);
				activity.startActivity(launchIntentForPackage);
			}
		} catch (Exception e){
			Toast.makeText(activity, "Could not open Application: " + getApplicationLabel(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public Match performSearch(Pattern regex) {
		return regex != null? MatchingUtils.checkForMatch(regex, getApplicationLabel()) 
				: new Match(true, getPrimarySearchContent());
	}
	
}
