package com.parse.starter;

import java.util.Arrays;
import java.util.List;

import android.R.drawable;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class FriendPage extends Activity {
	RelativeLayout friendRequestButton;
	LinearLayout friendRequestList;
	RelativeLayout allFriendsButton;
	LinearLayout allFriendsList;
	
	ValueAnimator requestAnimator;
	ValueAnimator allFriendAnimator;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendpage);
		
		EditText searchField = (EditText) findViewById(R.id.searchFriendField);
		searchField.setOnKeyListener(new OnKeyListener()
		{
		    public boolean onKey(View v, int keyCode, KeyEvent event)
		    {
		        if (event.getAction() == KeyEvent.ACTION_DOWN)
		        {
		            switch (keyCode)
		            {
		                case KeyEvent.KEYCODE_DPAD_CENTER:
		                case KeyEvent.KEYCODE_ENTER:
		                    backButtonClicked(v);
		                    return true;
		                default:
		                    break;
		            }
		        }
		        return false;
		    }
		});
	}
	
	public void friendListButtonClicked(View v) {
		Class targetActivity = null;
		targetActivity = FriendList.class;
		Intent intent = new Intent(this, targetActivity);
		startActivity(intent);
	}
	
	public void friendRequestButtonClicked(View v) {
		Class targetActivity = null;
		targetActivity = FriendRequests.class;
		Intent intent = new Intent(this, targetActivity);
		startActivity(intent);
	}
	
	public void backButtonClicked(View v) {
		finish();
	}

	private String[] searchFriend(String username) {
		// max 15
		// query search with username subsets
		// do not include those who are already friends

		List<ParseObject> friendResults;

		ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
		query.whereStartsWith("name", username);
		query.whereNotContainedIn(
				"email",
				Arrays.asList((String[]) ParseUser.getCurrentUser().get(
						"friends")));
		query.setLimit(15);
		try {
			friendResults = query.find();
			int numResults = friendResults.size();
			String[] namesAndEmails = new String[numResults * 2];
			int i = 0;
			for (ParseObject result : friendResults) {
				namesAndEmails[2 * i] = result.getString("name");
				namesAndEmails[2 * i + 1] = result.getString("email");
				i++;
				return namesAndEmails;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void addFriendButtonClicked(View v) {
		// addFriend Confirmation
	}

	public void addFriendConfirmation(View v) {
		// Yes / No popup
		// calls addFriendRequest
	}
	
	// return false if (probably) request to this username already exists
	// return true on success
	private boolean addFriendRequest(final String username) {
		// adds my name into username’s friend request array
		// first checks whether request already exists

		final ParseObject result; 

		ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
		query.whereMatches("username", username);
		try {
			result = query.getFirst();
			List<String> requests = result.getList("friendRequests");
			if (requests.contains(InitialPage.EMAIL_ADDRESS)) {
				return false;
			}
			else {
				ParseQuery<ParseObject> query2 = ParseQuery.getQuery("_User");
				// Retrieve the object by id
				query2.getInBackground(result.getString("objectId"), new GetCallback<ParseObject>() {
					public void done(ParseObject userToUpdate, ParseException e) {
						if (e == null) {
							// Now let's update it with some new data. In this case, only cheatMode and score
							// will get sent to the Parse Cloud. playerName hasn't changed.
							userToUpdate.put("friendRequests", result.getList("friendRequests").add(username));
							userToUpdate.saveInBackground();
						}
					}
				});
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}


	public void approveFriendButtonClicked(View v) {
		// calls approveFriendRequest
	}

}
