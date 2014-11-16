package com.parse.starter;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class FriendRequests extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendrequest);
		ParseAnalytics.trackAppOpened(getIntent());
		
		String s[] = new String[0];
		
		final ListView lv = (ListView) findViewById(R.id.requestList);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, 
                android.R.layout.simple_list_item_1, s);
        lv.setAdapter(arrayAdapter); 
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			 
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
	 
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getApplicationContext());
				
				final String listItem = (String) lv.getItemAtPosition(position);
	 
				// set title
				alertDialogBuilder.setTitle("Friend Request");
	 
				// set dialog message
				alertDialogBuilder
					.setMessage("Click yes accept request")
					.setCancelable(false)
					.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							// if this button is clicked, close
							// current activity
							approveFriendRequest(listItem);
							dialog.dismiss();
						}
					  })
					.setNegativeButton("No",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							// if this button is clicked, just close
							// the dialog box and do nothing
							dialog.cancel();
						}
					});
	 
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
	 
					// show it
					alertDialog.show();
				}
			});
		populateFriendRequests();
	}
	
	public void backButtonClicked(View v)
	{
		finish();
	}
	
	private void populateFriendRequests()
	{
		ListView lv = (ListView) findViewById(R.id.requestList);

        // Instanciating an array list (you don't need to do this, 
        // you already have yours).
        String[] friends = getFriendRequests();
        Log.d("friend Requests", "" + friends.length);
        if(friends == null || friends.length == 0 || friends[0] == null)
        	return;

        // This is the array adapter, it takes the context of the activity as a 
        // first parameter, the type of list view as a second parameter and your 
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, 
                android.R.layout.simple_list_item_1, friends );
        Log.d("friend Requests", friends[0]);
        lv.setAdapter(arrayAdapter); 
	}
	
	private String[] getFriendRequests() {
		// query search with username subsets
		ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
		query.whereEqualTo("username", InitialPage.EMAIL_ADDRESS);
		List<ParseObject> findFriends;
		Object[] myFriendsObjects = null;
		String[] myFriends = new String[0];
		try {
			findFriends = query.find();
			myFriendsObjects = new String[findFriends.size()];
			ParseObject p = findFriends.get(0);
			if (p.getList("friendRequests") != null)
				myFriendsObjects = p.getList("friendRequests").toArray();
			myFriends = Arrays.copyOf(myFriendsObjects, myFriendsObjects.length, String[].class);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return myFriends;
	}

	private boolean hasFriendRequests() {
		return (getFriendRequests().length > 1);
	}
	
	private void approveFriendRequest(String username) {
		// add username into my list of friends
		// add my username into their list of friends
		ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
		query.whereEqualTo("username", InitialPage.EMAIL_ADDRESS);
		try {
			ParseObject myParseObject = query.getFirst();
			String myObjectId = myParseObject.getString("objectId");
			final List<Object> myFriends = myParseObject.getList("friends");
			myFriends.add((Object) username);
			final List<Object> friendRequests = myParseObject.getList("friendRequests");
			friendRequests.remove((Object) username);				

			ParseQuery<ParseObject> query2 = ParseQuery.getQuery("_User");
			
			// Retrieve the object by id
			query2.getInBackground(myObjectId, new GetCallback<ParseObject>() {
				public void done(ParseObject user, ParseException e) {
					if (e == null) {
						user.put("friends", myFriends);
						user.put("friendRequests", friendRequests);
						user.saveInBackground();
					}
				}
			});
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		ParseQuery<ParseObject> query3 = ParseQuery.getQuery("_User");
		query.whereEqualTo("username", username);
		try {
			ParseObject myParseObject = query3.getFirst();
			String myObjectId = myParseObject.getString("objectId");
			final List<Object> myFriends = myParseObject.getList("friends");
			myFriends.add((Object) InitialPage.EMAIL_ADDRESS);

			ParseQuery<ParseObject> query4 = ParseQuery.getQuery("_User");
			
			// Retrieve the object by id
			query4.getInBackground(myObjectId, new GetCallback<ParseObject>() {
				public void done(ParseObject user, ParseException e) {
					if (e == null) {
						user.put("friends", myFriends);
						user.saveInBackground();
					}
				}
			});
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
