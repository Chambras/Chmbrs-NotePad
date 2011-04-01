package com.chmbrs.apps.notepad;

import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class NotePadReminder extends Activity 
{
	private static final String TAG = "NotePadReminder";
	
	private static final String SET_ALARM_ACTION = "com.chmbrs.apps.notepad.notes.action.NOTE_REMINDER";
	
	private AlarmManager noteAlarm;
	private String noteTitle ="";
	private String noteBody ="";

	private EditText minuteText;
	private EditText hourText;
	private EditText dayText;
	
	private RadioButton minuteRadioButton;
	private RadioButton hourRadioButton;
	private RadioButton dayRadioButton;
	
	private String timeIncrementType="minutes";
	private long incrementTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		final Intent intent = getIntent();
		noteTitle = intent.getExtras().getString("noteTitle");
		noteBody = intent.getExtras().getString("noteBody");
		
		setContentView(R.layout.remindersmenu);
		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_popup_reminder);
		
		minuteText = (EditText)findViewById(R.id.editTextMinutes);
		hourText = (EditText)findViewById(R.id.editTextHours);
		dayText = (EditText)findViewById(R.id.editTextDays);
		
		/**
		 * adding minutes, hours or days depending on the button
		 */
		Button plusMinuteButton = (Button)findViewById(R.id.buttonPlusMinute);
		Button plusHourButton = (Button)findViewById(R.id.buttonPlusHour);
		Button plusDayButton = (Button)findViewById(R.id.buttonPlusDay);
		
		plusMinuteButton.setOnClickListener(buttonListener);
		plusHourButton.setOnClickListener(buttonListener);
		plusDayButton.setOnClickListener(buttonListener);
		
		/**
		 * Subtracting minutes, hours or days depending on the button
		 */
		
		Button minusMinuteButton = (Button)findViewById(R.id.buttonMinusMinute);
		Button minusHourButton = (Button)findViewById(R.id.buttonMinusHour);
		Button minusDayButton = (Button)findViewById(R.id.buttonMinusDay);
		
		minusMinuteButton.setOnClickListener(buttonListener);
		minusHourButton.setOnClickListener(buttonListener);
		minusDayButton.setOnClickListener(buttonListener);
		
		Button cancelReminder = (Button) findViewById(R.id.reminderCancel);
		cancelReminder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
		
		Button setReminder = (Button) findViewById(R.id.reminderOK);
		setReminder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				setAlarmNewDate();
				setAlarm();
			}
		});
		
		minuteRadioButton = (RadioButton)findViewById(R.id.radioButtonMinutes);
		hourRadioButton = (RadioButton)findViewById(R.id.radioButtonHours);
		dayRadioButton = (RadioButton)findViewById(R.id.radioButtonDays);
		
		minuteRadioButton.setOnClickListener(radioButtomListener);
		hourRadioButton.setOnClickListener(radioButtomListener);
		dayRadioButton.setOnClickListener(radioButtomListener);
		
	}
	
	/*
	 * generic listener to handle buttons click events
	 */
	private OnClickListener buttonListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			switch (v.getId()) 
			{
				case R.id.buttonPlusMinute:
					addTime(minuteText);
					break;
				case R.id.buttonPlusHour:
					addTime(hourText);
					break;
				case R.id.buttonPlusDay:
					addTime(dayText);
					break;
				case R.id.buttonMinusMinute:
					subtactTime(minuteText);
					break;
				case R.id.buttonMinusHour:
					subtactTime(hourText);
					break;
				case R.id.buttonMinusDay:
					subtactTime(dayText);
					break;
				default:
					break;
			}
		}
	};
	
	/*
	 * Generic listener to handle radio buttone click event
	 */
	private OnClickListener radioButtomListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			RadioButton rb = (RadioButton) v;
			switch (rb.getId()) {
			case R.id.radioButtonMinutes:
				hourRadioButton.setChecked(false);
				dayRadioButton.setChecked(false);
				timeIncrementType="minutes";
				break;
			case R.id.radioButtonHours:
				dayRadioButton.setChecked(false);
				minuteRadioButton.setChecked(false);
				timeIncrementType="hours";
				break;
			case R.id.radioButtonDays:
				hourRadioButton.setChecked(false);
				minuteRadioButton.setChecked(false);
				timeIncrementType="days";
				break;
			default:
				break;
			}
		}
	};

	protected void subtactTime(EditText value) 
	{
		int actualValue = Integer.parseInt(value.getText().toString()) - 1;
		if(actualValue == 0)
		{
			actualValue = 1;
		}
		String newVale = Integer.toString(actualValue);
		value.setText(newVale);
		Log.i(TAG, "new value: " + newVale);
	}

	protected void addTime(EditText value) 
	{
		int actualValue = Integer.parseInt(value.getText().toString()) + 1;
		String newVale = Integer.toString(actualValue);
		value.setText(newVale);
		Log.i(TAG, "new value: " + newVale);
	}
	
	private void setAlarmNewDate()
	{
		int newTime = 0;
		if(timeIncrementType=="minutes")
		{
			newTime = Integer.parseInt(minuteText.getText().toString());
			incrementTime =newTime * 60000; 
		}
		else if(timeIncrementType == "hours")
		{
			newTime = Integer.parseInt(hourText.getText().toString());
			incrementTime =60*newTime * 60000; 
		}
		else if(timeIncrementType == "days")
		{
			newTime = Integer.parseInt(dayText.getText().toString());
			incrementTime = 24*60*newTime * 60000; 
		}
	}

	private void setAlarm()
	{
		noteAlarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent noteAlarmIntent = new Intent(SET_ALARM_ACTION, getIntent().getData());
		noteAlarmIntent.putExtra("noteTitle", noteTitle);
		noteAlarmIntent.putExtra("noteBody", noteBody);
		PendingIntent pendingNoteAlarmIntent = PendingIntent.getBroadcast(this, 0, noteAlarmIntent, PendingIntent.FLAG_ONE_SHOT);
		long triggerAtTime = System.currentTimeMillis() + incrementTime;
		noteAlarm.set(AlarmManager.RTC_WAKEUP, triggerAtTime , pendingNoteAlarmIntent);
		
		Date triggerDate = new Date(triggerAtTime);
		String notificationConfimation = triggerDate.toLocaleString();
		String reminderMessage = getText(R.string.notificationConfirmation) + " " + notificationConfimation;
		Toast.makeText(this, reminderMessage, Toast.LENGTH_LONG).show();
		Log.i(TAG, reminderMessage);
		finish();
	}
}