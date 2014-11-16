package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainPage extends Activity {
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainpage);
		getEvents();
		ParseAnalytics.trackAppOpened(getIntent());
	}

	
	public void gotoCreateEvents(View v) {
		Class targetActivity = null;
		targetActivity = CreateEvent.class;
		Intent intent = new Intent(this, targetActivity);
		startActivity(intent);
	}

	public void refreshEvents(View v) {
		getEvents();
	}
	
	private void getEvents() {

		Date currentTime = new Date();
		// we want this to be the user's friend list
		String[] friends = { "annie@princeton.edu", "bob@princeton.edu",
				"artur@princeton.edu" };

		// TextView textElement = (TextView) findViewById(R.id.MainText);
		// SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		// String strDate = sdf.format(currentTime);
		// textElement.setText(strDate); //leave this line to assign a specific
		// text

		ParseQuery<ParseObject> query = ParseQuery.getQuery("events");
		//query.whereContainedIn("creatorEmail", Arrays.asList(friends));
		query.whereGreaterThan("endTime", currentTime);
		query.addAscendingOrder("endTime");
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> events, ParseException e) {
				if (e == null) {
					Collections.sort(events, byTime());
					listEvents(events);
				} else {
					// something went wrong
				}
			}
		});
	}

	public static Comparator<ParseObject> byTime() {
		return new byTime();
	}
	
	private static class byTime implements Comparator<ParseObject> {
		// compare both terms
		public int compare(ParseObject a, ParseObject b) {
			Date dateA = (Date) a.get("startTime");
			Date dateB = (Date) b.get("startTime");
			return dateA.compareTo(dateB);
		}
	}
	
	public static Comparator<ParseObject> byEatingLocation() {
		return new byEatingLocation();
	}

	private static class byEatingLocation implements Comparator<ParseObject> {
		// compare both terms
		public int compare(ParseObject a, ParseObject b) {
			return (a.getString("location")
					.compareTo((b.getString("location"))));
		}
	}

	private void listEvents(List<ParseObject> events) {
		List<String> names = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

		for (ParseObject event : events) {
			String creatorName = event.getString("creator");
			String location = event.getString("location");
			Date endTime = event.getDate("endTime");
			names.add(creatorName + "\t\t" + location + "\t\t"
					+ sdf.format(endTime));
		}

		// Find the ListView resource.
		ListView mainListView = (ListView) findViewById(R.id.listView);

		// Create ArrayAdapter using the planet list.
		ArrayAdapter<String> listAdapter;
		listAdapter = new ArrayAdapter<String>(this, R.layout.listrow, names);
		// Set the ArrayAdapter as the ListView's adapter.
		mainListView.setAdapter(listAdapter);

	}

	public void logOutButtonClicked(View v) {
		ParseUser.logOut();
		Context context = getApplicationContext();
		File file = new File(context.getFilesDir(), "userInfo");
		file.delete();
		Class targetActivity = null;
		targetActivity = InitialPage.class;
		Intent intent = new Intent(this, targetActivity);
		startActivity(intent);
		finish();
	}
	
	public void friendButtonClicked(View v) {
		Class targetActivity = null;
		targetActivity = FriendPage.class;
		Intent intent = new Intent(this, targetActivity);
		startActivity(intent);
	}
}
