package com.sudran.appman;

import java.lang.Thread.State;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Process;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.sudran.appman.views.ObservableScrollView;
import com.sudran.appman.views.ObservableScrollView.OnScrollChangedListener;

public class UniSearchHome extends Activity {

	private static final String SEARCH_STRING_KEY = "app_state:search_box:search_string";
	private static final String SEARCH_BOX_SELECTION_START_KEY = "app_state:search_box:selection_start";
	private static final String SEARCH_BOX_SELECTION_END_KEY = "app_state:search_box:selection_end";

	static Pattern WHITE_SPACE_PATTERN = Pattern.compile("\\s+");

	private List<SearchableApplication> appList = new LinkedList<SearchableApplication>();;

	private int searchResultCount;

	private TextView searchCountText;

	private EditText searchBox;
	
//	private StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
	
	private ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.BLUE);

	//private ReentrantLock appLoaderLock;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_man_home);
		restoreFromSavedState(savedInstanceState);
		searchCountText = (TextView) findViewById(R.id.searchCount);
		displayAppList_fork();
		addListeners_fork();
		ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.appListScrollView);
		scrollView.addOnScrollChangedListener(new OnScrollChangedListener() {

			@Override
			public boolean onScrollChanged(ObservableScrollView observableScrollView,
					int l, int t, int oldl, int oldt) {
				EditText textView = getSearchBox();
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
				return true;
			}
		});
	}

	private void displayAppList_fork() {
		Thread displayAppList = new Thread(){
			public void run() {
				//    			try {
				//					sleep(1000);
				//				} catch (InterruptedException e) {
				//
				//				}
				displayAppList();
			};
		};
		displayAppList.start();
	}

	private void displayAppList() {
		long currentTimeMillis = System.currentTimeMillis();
		final PackageManager packageManager = getPackageManager();
		List<ApplicationInfo> applications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
		final LinearLayout appListLayout = (LinearLayout) findViewById(R.id.appList);
		int index = 0;
		for (final ApplicationInfo appInfo : applications) {
			final SearchableApplication app = new SearchableApplication(packageManager, appInfo);
			appList.add(app);
			final CharSequence applicationLabel = app.getApplicationLabel() + "\n";
			final LayoutParams appDetailsLayoutParams = 
					new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			final LayoutParams appNameLayoutParams = 
					new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			final LinearLayout appDetailsLinearLayout = new LinearLayout(this);
			final TextView textView = new TextView(this);
			app.setViewDetails(new ViewDetails(appDetailsLinearLayout,textView, index++));
			textView.setText(applicationLabel);
			textView.setGravity(Gravity.CENTER_VERTICAL);
			textView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					app.openElement(packageManager, UniSearchHome.this);
				}
			});
			final ImageView appIcon = new ImageView(this);
			try {
				Drawable drawable = packageManager.getApplicationIcon(app.getAndroidPackageName().toString());
				appIcon.setImageDrawable(drawable);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			int side = (int) getResources().getDimension(R.dimen.preferred_app_detail_height);
			final LayoutParams appIconLayoutParams = new LayoutParams(side, side);
			addOrRemoveAppView(app, appListLayout, appDetailsLayoutParams);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					appDetailsLinearLayout.addView(appIcon, appIconLayoutParams);
					appDetailsLinearLayout.addView(textView, appNameLayoutParams);
				}
			});
		}
		System.err.println("displayAppList: " + (System.currentTimeMillis() - currentTimeMillis));
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		EditText searchBox = getSearchBox();
		outState.putCharSequence(SEARCH_STRING_KEY, searchBox.getText());
		outState.putInt(SEARCH_BOX_SELECTION_START_KEY, searchBox.getSelectionStart());
		outState.putInt(SEARCH_BOX_SELECTION_END_KEY, searchBox.getSelectionEnd());
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		restoreFromSavedState(savedInstanceState);
	}

	private void restoreFromSavedState(Bundle savedInstanceState) {
		if(savedInstanceState == null)
			return;
		CharSequence charSequence = savedInstanceState.getCharSequence(SEARCH_STRING_KEY);
		getSearchBox().setText(charSequence);
		int selectionStart = savedInstanceState.getInt(SEARCH_BOX_SELECTION_START_KEY);
		int selectionEnd = savedInstanceState.getInt(SEARCH_BOX_SELECTION_END_KEY);
		if(selectionStart != -1 && selectionEnd != -1)
			getSearchBox().setSelection(selectionStart, selectionEnd);
	}

	//	private void displayAppList() {
	//	long currentTimeMillis = System.currentTimeMillis();
	//		final PackageManager pm = getPackageManager();
	//    	//get a list of installed apps.
	//	long currentTimeMillis2 = System.currentTimeMillis();
	//    	List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
	//    System.err.println("getAppList: " + (System.currentTimeMillis() - currentTimeMillis2));
	//    	final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.appList);
	//    	for (final ApplicationInfo packageInfo : packages) {
	//    		final CharSequence applicationLabel = pm.getApplicationLabel(packageInfo) + "\n";
	//    		final LayoutParams appDetailsLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	//    		final LayoutParams appNameLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
	//    		final LinearLayout appDetailsLinearLayout = new LinearLayout(this);
	//    		final TextView textView = new TextView(this);
	//    		textView.setText(applicationLabel);
	//    		textView.setGravity(Gravity.CENTER_VERTICAL);
	//    		Drawable drawable = packageInfo.loadIcon(pm);
	//    		final ImageView appIcon = new ImageView(this);
	//    		appIcon.setImageDrawable(drawable);
	//    		int side = (int) getResources().getDimension(R.dimen.preferred_app_detail_height);
	//    		final LayoutParams appIconLayoutParams = new LayoutParams(side, side);
	//    		runOnUiThread(new Runnable() {
	//				@Override
	//				public void run() {
	//					linearLayout.addView(appDetailsLinearLayout, appDetailsLayoutParams);
	//					appDetailsLinearLayout.addView(appIcon, appIconLayoutParams);
	//					appDetailsLinearLayout.addView(textView, appNameLayoutParams);
	//				}
	//			});
	//    	} 
	//	System.err.println("displayAppList: " + (System.currentTimeMillis() - currentTimeMillis));
	//	}

	//	private List<SearchableApplication> getAppList() {
	//	long currentTimeMillis = System.currentTimeMillis();
	//		final PackageManager pm = getPackageManager();
	//    	//get a list of installed apps.
	//    	List<ApplicationInfo> appInfos= pm.getInstalledApplications(PackageManager.GET_META_DATA);
	//    	List<SearchableApplication> apps = new LinkedList<SearchableApplication>();
	//    	for (ApplicationInfo applicationInfo : appInfos) {
	//			apps.add(new SearchableApplication(pm, applicationInfo));
	//		}
	//	long currentTimeMillis2 = System.currentTimeMillis();
	//	System.err.println("getAppList: " + (currentTimeMillis2 - currentTimeMillis));
	//		return apps;
	//	}

	private void addListeners_fork() {
		EditText searchBox = getSearchBox();
		searchBox.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				OnTextChangeEvent textChangeEvent = new OnTextChangeEvent(s,start,before,count, System.nanoTime());
				startSearching_fork(textChangeEvent);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_app_man_home, menu);
		return true;
	}
	private SearchThread searchThread = new SearchThread(this);

	private void startSearching_fork(OnTextChangeEvent textChangeEvent) {
		System.out.println("User input "+ textChangeEvent.getCharSequence()+ " time: " + System.nanoTime() + " nanos");
		CharSequence searchString = textChangeEvent.getCharSequence();
		searchThread.setSearchString(searchString.toString(), textChangeEvent.getTimeOfEventInNano());
		State state = searchThread.getState();
		if(state == Thread.State.NEW){
			searchThread.start();
		} else{
			Thread thread = new Thread(){
				public void run() {
					Object objectLock = searchThread.getObjectLock();
					synchronized (objectLock) {
						objectLock.notifyAll();
					}
				};
			};
			thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
			thread.start();
		}
	}

	private Match checkForMatch(Pattern regex, ISearchableElement app) {
		CharSequence applicationLabel = app.getPrimarySearchContent();
		Matcher matcher = regex.matcher(applicationLabel);
		boolean matches = false;
		Spannable spannableApplabel = new SpannableString(applicationLabel);
		while(matcher.find()){
			matches = true;
			int start = matcher.start();
			int end = matcher.end();
			//spannableApplabel.setSpan(styleSpan, start, end, 0);
			spannableApplabel.setSpan(foregroundColorSpan, start, end, 0);
		}
		return new Match(matches, spannableApplabel);
	}

	private EditText getSearchBox(){
		if(searchBox == null)
			searchBox = (EditText)findViewById(R.id.searchBox);
		return searchBox;
	}

	/*
	 * Might be a long running function. Do not call on UI thread
	 */
	void addOrRemoveAppView(final ISearchableElement app, final ViewGroup appListLayout, final LayoutParams params) {
		final View appView = app.getViewDetails().getSearchResultView();
		Pattern searchRegex = searchThread.getSearchRegex();
		final Match match = searchRegex != null? checkForMatch(searchRegex, app) : new Match(true, app.getPrimarySearchContent());
		final boolean matches = match.isMatches();
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				if(matches){
					if(appView.getParent() == null){
						if(params == null)
							appListLayout.addView(appView);
						else
							appListLayout.addView(appView, params);
						searchResultCount++;
					}
				} else {
					if(appView.getParent() == appListLayout){
						appListLayout.removeView(appView);
						searchResultCount--;
					}
				}
				searchCountText.setText(String.valueOf(searchResultCount));
				ViewDetails viewDetails = app.getViewDetails();
				TextView primaryTextDisplay = viewDetails.getPrimaryTextDisplay();
				primaryTextDisplay.setText(match.getMatchedSpan(),BufferType.SPANNABLE);
			}
		});
	}

	List<SearchableApplication> getAppList() {
		return appList;
	}

}
