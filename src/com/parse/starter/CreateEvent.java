package com.parse.starter;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class CreateEvent extends Activity {
	private int startHour = -1;
	private int startMinute = -1;
	private int endHour = -1;
	private int endMinute = -1;
	public static String location = "";
	private Date eventDate;
	private Spinner spinner;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createevent);
		
		addListenerOnSpinnerItemSelection();
	}

	public void addListenerOnSpinnerItemSelection() {
		spinner = (Spinner) findViewById(R.id.spinner1);
		spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	}
	
	public class TimePickerFragmentS extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			startHour = hourOfDay;
			startMinute = minute;
			Button button = (Button) findViewById(R.id.startTime);
			
			Date startTime = new Date();
			startTime.setHours(hourOfDay);
			startTime.setMinutes(minute);
			
			String time = new SimpleDateFormat("hh:mm a").format(startTime);
			button.setText(time);
		}
	}

	public void showTimePickerDialogS(View v) {
		DialogFragment newFragment = new TimePickerFragmentS();
		newFragment.show(getFragmentManager(), "timePicker");
	}

	public class TimePickerFragmentE extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		@SuppressWarnings("deprecation")
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			endHour = hourOfDay;
			endMinute = minute;
			Button button = (Button) findViewById(R.id.endTime);
			
			Date endTime = new Date();
			endTime.setHours(hourOfDay);
			endTime.setMinutes(minute);
			
			String time = new SimpleDateFormat("hh:mm a").format(endTime);
			button.setText(time);
		}
	}
	
	public void showTimePickerDialogE(View v) {
	    DialogFragment newFragment = new TimePickerFragmentE();
	    newFragment.show(getFragmentManager(), "timePicker");
	}
	  
	public class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			Button button = (Button) findViewById(R.id.dateEvent);
			button.setText("" + (month+1) + "/" + day + "/" + year);
			eventDate = new Date(year, month, day);
		}
	}
	
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	
	public void createEventButtonClicked(View v) {
		Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        
        if (startHour == -1 || endHour == -1 || eventDate == null || location == "") {
			Toast toast = Toast.makeText(context, "Please fill out all fields.", duration);
			toast.show();
			return;
		}
		
		Date startTime = new Date();
		startTime.setYear(eventDate.getYear());
		startTime.setMonth(eventDate.getMonth());
		startTime.setDate(eventDate.getDate());
		startTime.setHours(startHour);
		startTime.setMinutes(startMinute);
		
		Date endTime = new Date();
		endTime.setYear(eventDate.getYear());
		endTime.setMonth(eventDate.getMonth());
		endTime.setDate(eventDate.getDate());
		endTime.setHours(endHour);
		endTime.setMinutes(endMinute);

		if (startTime.compareTo(endTime) > 0) {
	        Toast toast = Toast.makeText(context, "Get your life in order.", duration);
			toast.show();
			return;
		}
		
		if (endTime.compareTo(new Date()) < 0) {
			Toast toast = Toast.makeText(context, "You don't got no time machine.", duration);
			toast.show();
			return;
		}
		
		if (createEvent(location, startTime, endTime, "")) {
			Toast toast = Toast.makeText(context, "Event Submitted!.", duration);
			toast.show();
			gotoMainPage();
		}
		else {
			Toast toast = Toast.makeText(context, "Event submit failed :(.", duration);
			toast.show();
		}
	}
	
	private void gotoMainPage() {
		Class targetActivity = null;
		targetActivity = MainPage.class;
		Intent intent = new Intent(this, targetActivity);
		startActivity(intent);
		finish();
	}
	
	// returns true if successful
	// returns false if failed
	private boolean createEvent(String location, Date startTime, Date endTime, String description) {
		ParseObject event = new ParseObject("events");
		event.put("location", location);
		event.put("startTime", startTime);
		event.put("endTime", endTime);
		event.put("description", description);
		event.put("creator", InitialPage.NAME);
		event.put("creatorEmail", InitialPage.EMAIL_ADDRESS);
		try {
			event.save();
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	// return true if successfully deleted
	private boolean removeEvent(ParseObject event) {
		try {
			event.delete();
		} catch (ParseException e) {
			return false;
		}
		return true;
	}
}
