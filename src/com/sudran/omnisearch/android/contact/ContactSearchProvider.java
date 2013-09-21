package com.sudran.omnisearch.android.contact;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sudran.omnisearch.R;
import com.sudran.omnisearch.android.framework.BaseSearchProvider;
import com.sudran.omnisearch.android.framework.ISearchableElement;
import com.sudran.omnisearch.android.framework.ViewDetails;

public class ContactSearchProvider extends BaseSearchProvider<SearchableContact> {
	
	private List<SearchableContact> contacts = new LinkedList<SearchableContact>();
	
	@SuppressLint("InlinedApi")
	private static final String[] PROJECTION = new String[5];

	private static final int _ID_COLUMNINDEX = 0;
	private static final int DISPLAY_NAME_COLUMN_INDEX = 1;
	private static final int CONTACT_ID_COLUMN_INDEX = 2;
	private static final int LOOKUP_KEY_COLUMN_INDEX = 3;
	private static final int PHOTO_COLUMN_INDEX = 4;
	
	static {
		/*
         * The detail data row ID. To make a ListView work,
         * this column is required.
         */
		PROJECTION[_ID_COLUMNINDEX] = Data._ID;
		// The primary display name
		@SuppressLint("InlinedApi")
        String displayName = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                Data.DISPLAY_NAME_PRIMARY :
                Data.DISPLAY_NAME;
		PROJECTION[DISPLAY_NAME_COLUMN_INDEX] = displayName;
		// The contact's _ID, to construct a content URI
		PROJECTION[CONTACT_ID_COLUMN_INDEX] = Data._ID;
		// The contact's LOOKUP_KEY, to construct a content URI
		PROJECTION[LOOKUP_KEY_COLUMN_INDEX] = Data.LOOKUP_KEY; //(a permanent link to the contact)
		PROJECTION[PHOTO_COLUMN_INDEX] = ContactsContract.Contacts.PHOTO_URI;
	}
	
	
	private Activity searchActivity;

	private ContentResolver contentResolver;

	public ContactSearchProvider(Activity searchActivity) {
		this.searchActivity = searchActivity;
	}

	@Override
	protected void load() {
	long startTime = System.currentTimeMillis();
	System.out.println("Contacts loading started at: " + startTime);
		contentResolver = searchActivity.getContentResolver();
		Cursor cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, null, null, null);
		if (cur.getCount() > 0) {
			int index = 0;
			final List<Runnable> longRunningOperations = new LinkedList<Runnable>();
            while (cur.moveToNext()) {
            	String id = cur.getString(_ID_COLUMNINDEX);
            	final String displayName = cur.getString(DISPLAY_NAME_COLUMN_INDEX);
            	String photoUri = cur.getString(PHOTO_COLUMN_INDEX);
            	System.out.println("contact id: " + id + ". Displayname: " + displayName);
            	final SearchableContact searchableContact = new SearchableContact(id, displayName, photoUri);
            	contacts.add(searchableContact);
            	final ViewDetails viewDetails = makeUiElement(index++, searchableContact);
            	if(searchableContact.getImageUri() != null ){
            		Runnable runnable = new Runnable() {
            			@Override
            			public void run() {
            				InputStream inputStream;
            				try {
            					inputStream = contentResolver.openInputStream(Uri.parse(searchableContact.getImageUri()));
            					Drawable drawable = Drawable.createFromStream(inputStream, "Contact Photo of: " + displayName);
            					viewDetails.getIconImageView().setImageDrawable(drawable);
            				} catch (FileNotFoundException e) {
            					e.printStackTrace();
            				}
            			}
            		};
            		longRunningOperations.add(runnable);
            	}
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
		}
	long endTime = System.currentTimeMillis();
	System.out.println("Time taken for contacts to laod: " + (endTime -startTime));
	}
	
	private ViewDetails makeUiElement(int index, final SearchableContact searchableContact) {
		final CharSequence contactLabel = searchableContact.getPrimarySearchContent() + "\n";
		final LinearLayout contactDetailsLinearLayout = new LinearLayout(searchActivity);
		final LayoutParams contactDetailsLayoutParams = 
				new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		contactDetailsLinearLayout.setLayoutParams(contactDetailsLayoutParams);
		final TextView textView = new TextView(searchActivity);
		final LayoutParams contactNameLayoutParams = 
				new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		textView.setLayoutParams(contactNameLayoutParams);
		textView.setText(contactLabel);
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO
			}
		});
		int side = (int) searchActivity.getResources().
				getDimension(R.dimen.preferred_search_result_detail_height);
		final ImageView icon = new ImageView(searchActivity);
		final LayoutParams contactIconLayoutParams = new LayoutParams(side, side);
		icon.setLayoutParams(contactIconLayoutParams);
		contactDetailsLinearLayout.addView(icon);
		contactDetailsLinearLayout.addView(textView);
		ViewDetails viewDetails = new ViewDetails(contactDetailsLinearLayout,textView, icon, index);
		searchableContact.setViewDetails(viewDetails);
		return viewDetails;
	}

	@Override
	protected List<? extends ISearchableElement> getSearchableElements() {
		return contacts;
	}

}
