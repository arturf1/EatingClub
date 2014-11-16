package com.parse.starter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class FriendSearch extends Activity implements OnItemClickListener{

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendsearch);
		ParseAnalytics.trackAppOpened(getIntent());
		listFriends(searchFriend(FriendPage.searchQuery));
		
		//* *EDIT* * 
        ListView listview = (ListView) findViewById(R.id.listView);
        listview.setOnItemClickListener(this);
        
	}
	
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
            // Then you start a new Activity via Intent
            //Intent intent = new Intent();
            //intent.setClass(this, ListItemDetail.class);
            //intent.putExtra("position", position);
            // Or / And
            //intent.putExtra("id", id);
            //startActivity(intent);
        	
        String n = (String) l.getItemAtPosition(position);
        String email = n.split("\n")[1];
        
		Context context = getApplicationContext();
	    int duration = Toast.LENGTH_LONG;
	    Toast toast;

        if (addFriendRequest(email)) {
    		toast = Toast.makeText(context, "Friend Request Sent.", duration);
    		//l.removeView(l.getChildAt(position));
        }
        else {
    		toast = Toast.makeText(context, "Friend Request Failed.", duration);
        }
		toast.show();


    }
	
	// return false if (probably) request to this username already exists
	// return true on success
	private boolean addFriendRequest(final String username) {
		// adds my name into usernameís friend request array
		// first checks whether request already exists

		boolean succccess = false; 
		final ParseObject result; 

		ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
		query.whereMatches("username", username);
		try {
			result = query.getFirst();
			List<String> requests = result.getList("friendRequests");
			if (requests.contains(InitialPage.EMAIL_ADDRESS)) {
				return succccess;
			}
			else {
				ParseQuery<ParseObject> query2 = ParseQuery.getQuery("_User");
				// Retrieve the object by id
				String id = result.getObjectId();
				/*ParseObject userToUpdate = query2.get(id);
				List<String> n = userToUpdate.getList("friendRequests");
				n.add(username);
				userToUpdate.put("friendRequests", n);
				userToUpdate.save();*/
				query2.getInBackground(id, new GetCallback<ParseObject>() {
					public void done(ParseObject user, ParseException e) {
						if (e == null) {
							final List<String> n = user.getList("friendRequests");
							n.add(username);
							//Object[] examineThis = n.toArray();
							user.put("friendRequests", n);
							user.saveInBackground();
						}
					}
				});
				succccess = true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return succccess;
	}

	
	private String[] searchFriend(String username) {
		// max 15
		// query search with username subsets
		// do not include those who are already friends

		List<ParseObject> friendResults;

		ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
		query.whereStartsWith("name", username);
		String n = ParseUser.getCurrentUser().getObjectId();
		
		ParseQuery<ParseObject> query2 = ParseQuery.getQuery("_User");
		query2.whereEqualTo("objectId", n);
		ParseObject person;
		String[] namesAndEmails = {};
		try {
			person = query2.getFirst();
			query.whereNotContainedIn("username", person.getList("friends"));
			//query.whereNotContainedIn("email", n);
			query.setLimit(15);
			try {
				friendResults = query.find();
				int numResults = friendResults.size();
				namesAndEmails = new String[numResults*2]; 
				int i = 0;
				for(ParseObject result : friendResults) {
					namesAndEmails[2*i] = result.getString("name");
					namesAndEmails[2*i + 1] = result.getString("email");
					i++;
					if (!(i < numResults))
					{
						break;
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		
		return namesAndEmails;
	}
	
	private void listFriends(String[] results) {
			
		List<String> names = new ArrayList<String>();
		int n = results.length; 
		
		for (int i = 0; i < n/2; i++) {
			String name = results[2*i];
			String email = results[2*i + 1];
			names.add(name + "\n" + email);
		}

		// Find the ListView resource.
		ListView mainListView = (ListView) findViewById(R.id.listView);

		// Create ArrayAdapter using the planet list.
		ArrayAdapter<String> listAdapter;
		listAdapter = new ArrayAdapter<String>(this, R.layout.listrow, names);
		// Set the ArrayAdapter as the ListView's adapter.
		mainListView.setAdapter(listAdapter);

	}
		
}
