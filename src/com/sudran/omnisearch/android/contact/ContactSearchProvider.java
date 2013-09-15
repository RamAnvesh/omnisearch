package com.sudran.omnisearch.android.contact;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sudran.appman.R;
import com.sudran.omnisearch.android.framework.BaseSearchProvider;
import com.sudran.omnisearch.android.framework.SearchResultCallback;
import com.sudran.omnisearch.android.framework.ViewDetails;

public class ContactSearchProvider extends BaseSearchProvider<SearchableContact> {
	
	private List<SearchableContact> contacts = new LinkedList<SearchableContact>();
	
	@SuppressLint("InlinedApi")
	private static final String[] PROJECTION =
        {
            /*
             * The detail data row ID. To make a ListView work,
             * this column is required.
             */
            Data._ID,
            // The primary display name
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    Data.DISPLAY_NAME_PRIMARY :
                    Data.DISPLAY_NAME,
            // The contact's _ID, to construct a content URI
            Data.CONTACT_ID,
            // The contact's LOOKUP_KEY, to construct a content URI
            Data.LOOKUP_KEY, //(a permanent link to the contact)
            PhoneLookup.NUMBER,
        };
	
	private Activity searchActivity;

	public ContactSearchProvider(Activity searchActivity) {
		this.searchActivity = searchActivity;
	}

	@Override
	protected void load() {
		ContentResolver contentResolver = searchActivity.getContentResolver();
		Cursor cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, null, null, null);
		if (cur.getCount() > 0) {
			int index = 0;
			final List<Runnable> longRunningOperations = new LinkedList<Runnable>();
            while (cur.moveToNext()) {
            	String id = cur.getString(cur.getColumnIndex(BaseColumns._ID));
            	String displayName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            	SearchableContact searchableContact = new SearchableContact(id, displayName);
            	contacts.add(searchableContact);
            	makeUiElement(index++, searchableContact);
            }
		}
	}

	@Override
	public void searchFor(Pattern regex, SearchResultCallback searchResultCallback) {
		
	}
	
	private void makeUiElement(int index, final SearchableContact searchableContact) {
		final CharSequence applicationLabel = searchableContact.getPrimarySearchContent() + "\n";
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
				//TODO
			}
		});
		int side = (int) searchActivity.getResources().
				getDimension(R.dimen.preferred_app_detail_height);
		final ImageView icon = new ImageView(searchActivity);
		final LayoutParams appIconLayoutParams = new LayoutParams(side, side);
		icon.setLayoutParams(appIconLayoutParams);
		appDetailsLinearLayout.addView(icon);
		appDetailsLinearLayout.addView(textView);
		searchableContact.setViewDetails(new ViewDetails(appDetailsLinearLayout,textView, icon, index));
	}

}
