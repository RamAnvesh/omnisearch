package com.sudran.omnisearch.android.framework;

import android.app.IntentService;
import android.content.Intent;

public class OmniSearchService extends IntentService {

	public OmniSearchService() {
		super(OmniSearchService.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		while (true) {
			
		}
	}

}
