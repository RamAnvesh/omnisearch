package com.sudran.omnisearch.android.app;

import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sudran.appman.R;
import com.sudran.omnisearch.android.framework.BaseSearchProvider;
import com.sudran.omnisearch.android.framework.ViewDetails;

public class ApplicationSearchProvider extends BaseSearchProvider<SearchableApplication> {

	private PackageManager packageManager;
	private Activity searchActivity;

	public ApplicationSearchProvider(PackageManager packageManager, Activity searchActivity) {
		this.packageManager = packageManager;
		this.searchActivity = searchActivity;
	}
	
	@Override
	public void load() {
		long currentTimeMillis = System.currentTimeMillis();
	long packageGetStart = System.nanoTime();
		List<ApplicationInfo> applications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
	long packageGetEnd = System.nanoTime();
	System.err.println("package get time: " + (packageGetEnd - packageGetStart));
		//final LinearLayout appListLayout = (LinearLayout) findViewById(R.id.appList);
		int index = 0;
		for (final ApplicationInfo appInfo : applications) {
//			int appFlags = appInfo.flags;
//			if((appFlags & ApplicationInfo.FLAG_SYSTEM) == 0 || (appFlags & ApplicationInfo.FLAG_INSTALLED) == 0){
//					continue;
//			}
			final SearchableApplication app = new SearchableApplication(packageManager, appInfo);
			searchableElements.add(app);
			final CharSequence applicationLabel = app.getApplicationLabel() + "\n";
			final LinearLayout appDetailsLinearLayout = new LinearLayout(searchActivity);
			final LayoutParams appDetailsLayoutParams = 
					new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			appDetailsLinearLayout.setLayoutParams(appDetailsLayoutParams);
			final TextView textView = new TextView(searchActivity);
			final LayoutParams appNameLayoutParams = 
					new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			textView.setLayoutParams(appNameLayoutParams);
			app.setViewDetails(new ViewDetails(appDetailsLinearLayout,textView, index++));
			textView.setText(applicationLabel);
			textView.setGravity(Gravity.CENTER_VERTICAL);
			textView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					app.openElement(packageManager, searchActivity);
				}
			});
//			try {
//				Drawable drawable = packageManager.getApplicationIcon(app.getAndroidPackageName().toString());
//				times.add(System.nanoTime());
//				appIcon.setImageDrawable(drawable);
//				times.add(System.nanoTime());
//			} catch (NameNotFoundException e) {
//				e.printStackTrace();
//			}
			int side = (int) searchActivity.getResources().
					getDimension(R.dimen.preferred_app_detail_height);
			final ImageView appIcon = new ImageView(searchActivity);
			final LayoutParams appIconLayoutParams = new LayoutParams(side, side);
			appIcon.setLayoutParams(appIconLayoutParams);
			appDetailsLinearLayout.addView(appIcon);
			appDetailsLinearLayout.addView(textView);
		}
		System.err.println("displayAppList: " + (System.currentTimeMillis() - currentTimeMillis));
	}

}
