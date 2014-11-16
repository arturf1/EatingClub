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

public class FriendList extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendlist);
		ParseAnalytics.trackAppOpened(getIntent());
		populateFriendList();
	}
	
	public void backButtonClicked(View v)
	{
		finish();
	}
	
	private void populateFriendList()
	{
		ListView lv = (ListView) findViewById(R.id.requestList);

        // Instanciating an array list (you don't need to do this, 
        // you already have yours).
        String[] friends = getFriends();Log.d("friend list", "" + friends.length);

        // This is the array adapter, it takes the context of the activity as a 
        // first parameter, the type of list view as a second parameter and your 
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, 
                android.R.layout.simple_dropdown_item_1line, friends );

        lv.setAdapter(arrayAdapter); 
	}
	
	
	private String[] getFriends() {
		// query search with username subsets
		ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
		query.whereEqualTo("username", InitialPage.EMAIL_ADDRESS);
		List<ParseObject> findFriends;
		String[] myFriends = new String[0];
		Object[] myFriendsObjects = null;
		try {
			findFriends = query.find();
			myFriends = new String[findFriends.size()];
			ParseObject p = findFriends.get(0);
			if (p.getList("friends").size() >= 1)
				myFriendsObjects = p.getList("friends").toArray();
			myFriends = Arrays.copyOf(myFriendsObjects, myFriendsObjects.length, String[].class);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return myFriends;
	}
}
