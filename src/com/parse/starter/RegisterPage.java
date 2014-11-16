package com.parse.starter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;

public class RegisterPage extends Activity {
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registerpage);
		EditText passwordField = (EditText) findViewById(R.id.registerPassword2);
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
		                    registerButtonClicked(v);
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
	
	public void registerButtonClicked(View v) {
		
		EditText passwordField1 = (EditText) findViewById(R.id.registerPassword1);
		String password1 = passwordField1.getText().toString();
		EditText passwordField2 = (EditText) findViewById(R.id.registerPassword2);
		String password2 = passwordField2.getText().toString();
		if (!password1.equals(password2))
		{
			passwordField1.setText("");
			passwordField2.setText("");
			Context context = getApplicationContext();
	        int duration = Toast.LENGTH_LONG;
	        Toast toast = Toast.makeText(context, "Passwords do not match", duration);
	        toast.show();
	        return;
		}
		
		EditText nameField = (EditText) findViewById(R.id.registerName);
		String name = nameField.getText().toString();
		EditText emailField = (EditText) findViewById(R.id.registerEmail);
		String email = emailField.getText().toString();
		boolean registered = registerUser(email, password1, name);
		if (registered)
		{
			InitialPage.NAME = name;
			InitialPage.EMAIL_ADDRESS = email;
			saveData();
			Context context = getApplicationContext();
	        int duration = Toast.LENGTH_LONG;
	        Toast toast = Toast.makeText(context, "Successfully Registered", duration);
	        toast.show();
	        gotoMainPage();
		}
		else
		{
			passwordField1.setText("");
			passwordField2.setText("");
			Context context = getApplicationContext();
	        int duration = Toast.LENGTH_LONG;
	        Toast toast = Toast.makeText(context, "E-mail Address Invalid", duration);
	        toast.show();
		}
	}
	
	public void saveData()
	{
		File file = new File(getApplicationContext().getFilesDir(), "userInfo");
		FileOutputStream outputStream;

		try {
		  outputStream = new FileOutputStream(file, false);
		  String str = InitialPage.NAME + "\n" + InitialPage.EMAIL_ADDRESS;
		  outputStream.write(str.getBytes());
		  outputStream.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
	
	private boolean registerUser(String email, String password, String name) {
		ParseUser user = new ParseUser();
		user.setEmail(email);
		user.setUsername(email);
		user.setPassword(password);
		user.put("name", name);
		
		try {
			user.signUp();
			ParseUser.logIn(email, password);
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


}
