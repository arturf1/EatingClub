package com.parse.starter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class InitialPage extends Activity {
	private static String filename = "userInfo";
	public static String NAME;
	public static String EMAIL_ADDRESS;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.initial);
		Context context = getApplicationContext();
		File file = new File(context.getFilesDir(), filename);
		if(file.exists())
		{
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				NAME = br.readLine();
			    EMAIL_ADDRESS = br.readLine();
			    br.close();
			    gotoMainPage();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		EditText passwordField = (EditText) findViewById(R.id.passwordField);
		passwordField.setOnKeyListener(new OnKeyListener()
		{
		    public boolean onKey(View v, int keyCode, KeyEvent event)
		    {
		        if (event.getAction() == KeyEvent.ACTION_DOWN)
		        {
		            switch (keyCode)
		            {
		                case KeyEvent.KEYCODE_DPAD_CENTER:
		                case KeyEvent.KEYCODE_ENTER:
		                    signInButtonClicked(v);
		                    return true;
		                default:
		                    break;
		            }
		        }
		        return false;
		    }
		});
		
		ParseAnalytics.trackAppOpened(getIntent());
	}
	
	public void signInButtonClicked(View v) {
		EditText usernameField = (EditText) findViewById(R.id.usernameField);
		String username = usernameField.getText().toString();
		EditText passwordField = (EditText) findViewById(R.id.passwordField);
		String password = passwordField.getText().toString();
		boolean signedIn = signIn(username, password);
		if (signedIn)
		{
			saveData();
			Context context = getApplicationContext();
	        int duration = Toast.LENGTH_LONG;
	        Toast toast = Toast.makeText(context, "Signed In", duration);
	        toast.show();
	        gotoMainPage();
		}
		else
		{
			passwordField.setText("");
			Context context = getApplicationContext();
	        int duration = Toast.LENGTH_LONG;
	        Toast toast = Toast.makeText(context, "Incorrect Username and Password", duration);
	        toast.show();
		}
	}
	
	public void registerButtonClicked(View v)
	{
		gotoRegisterPage();
	}
	
	public void saveData()
	{
		File file = new File(getApplicationContext().getFilesDir(), "userInfo");
		FileOutputStream outputStream;

		try {
		  outputStream = new FileOutputStream(file, false);
		  String str = NAME + "\n" + EMAIL_ADDRESS;
		  outputStream.write(str.getBytes());
		  outputStream.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
	
	private boolean signIn(String email, String password) {
		try {
			ParseUser.logIn(email, password);
			EMAIL_ADDRESS = email;
			
			ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
			query.whereEqualTo("email", email);
			query.selectKeys(Arrays.asList("name"));
			ParseObject matchingName;
			try {
				matchingName= query.getFirst();
				NAME = matchingName.getString("name");
			} catch (ParseException e) {
				Log.d("score", "Error: " + e.getMessage());
			}
			} catch (ParseException e) {
			return false;
		}
		return true;

	}
	
	private void gotoMainPage() {
		Class targetActivity = null;
		targetActivity = MainPage.class;
		Intent intent = new Intent(this, targetActivity);
		startActivity(intent);
		finish();
	}

	private void gotoRegisterPage() {
		Class targetActivity = null;
		targetActivity = RegisterPage.class;
		Intent intent = new Intent(this, targetActivity);
		startActivity(intent);
		finish();
	}
}
