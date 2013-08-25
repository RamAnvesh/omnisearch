package com.sudran.appman;

import android.app.IntentService;
import android.content.Intent;

public class AppManService extends IntentService {

	public AppManService() {
		super(AppManService.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		while (true) {
			
		}
	}

}
