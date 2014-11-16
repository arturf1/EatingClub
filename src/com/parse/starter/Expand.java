package com.parse.starter;

import java.lang.reflect.Method;
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
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Expand extends Activity {
	public static Animation expand(final View v, final boolean expand) {
		try {
			Method m = v.getClass().getDeclaredMethod("onMeasure", int.class, int.class);
			m.setAccessible(true);
			m.invoke(v,MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(
		((View) v.getParent()).getMeasuredWidth(),
		MeasureSpec.AT_MOST));
		} catch (Exception e) {
			e.printStackTrace();
		}
		final int initialHeight = v.getMeasuredHeight();
		if (expand) {
			v.getLayoutParams().height = 0;
		} else {
			v.getLayoutParams().height = initialHeight;
		}
		v.setVisibility(View.VISIBLE);
		Animation a = new Animation() {
			protected void applyTransformation(float interpolatedTime) {
				int newHeight = 0;
				if (expand) {
					newHeight = (int) (initialHeight * interpolatedTime);
				} else {
					newHeight = (int) (initialHeight * (1 - interpolatedTime));
				}
				v.getLayoutParams().height = newHeight;
				v.requestLayout();
				if (interpolatedTime == 1 && !expand)
					v.setVisibility(View.GONE);
			}

			public boolean willChangeBounds() {
				return true;
			}
		};
		a.setDuration(100);
		return a;
	}
	
	public void clickButton() {
		Animation anim = expand(findViewById(R.id.title1),true);
		findViewById(R.id.title1).startAnimation(anim);
	}
}
