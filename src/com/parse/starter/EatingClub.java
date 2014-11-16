package com.parse.starter;

import android.app.Application;
import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;
import com.parse.ParseACL;
import com.parse.ParseUser;
import com.parse.PushService;

public class EatingClub extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// Add your initialization code here
		Parse.initialize(this, "H71CQB2s5TvKZkUYPAvlnAOUFg953qcY8ipvcOpA",
				"gnzyfpH0w5mFsBOeReXXR4ZmKYw0UrsFfvbUFqJZ");
		// Also, specify a default Activity to handle push notifications in this
		// method as well
		PushService.setDefaultPushCallback(this, InitialPage.class);

		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();

		// If you would like all objects to be private by default, remove this
		// line.
		defaultACL.setPublicReadAccess(true);

		ParseACL.setDefaultACL(defaultACL, true);

		// Save the current Installation to Parse.
		ParseInstallation.getCurrentInstallation().saveInBackground();

		ParsePush.subscribeInBackground("", new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					Log.d("com.parse.push",
							"successfully subscribed to the broadcast channel.");
				} else {
					Log.e("com.parse.push", "failed to subscribe for push", e);
				}
			}
		});
	}
}