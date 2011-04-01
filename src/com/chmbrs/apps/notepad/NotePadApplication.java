package com.chmbrs.apps.notepad;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;

public class NotePadApplication extends Application
{
	private static final String TAG ="NotePadApplication";
	private SharedPreferences preferences;
	private float fontSize;
	private boolean guideLines;
	private int typefaceValue;
	private Typeface typeface;
	private boolean autoLink;
	private String ringTone;
	private int LEDColor;
	private boolean vibrate;
	private int titleSize;
	private String sortType;
	private int backgroundColor;
	private int textColor;
	private int lineColor;
	
	private String currentApplicationVersionName;
	
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
		Log.i(TAG, "creating Application object");
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		try 
		{
			currentApplicationVersionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) 
		{
			e.printStackTrace();
		};
	}
	
	public String getCurrentApplicationVersionName()
	{
		return currentApplicationVersionName;
	}
	
	public float getTextSize()
	{
		fontSize = Float.parseFloat(preferences.getString("listPreferenceTextSize", ""));
		return fontSize;
	}
	
	public boolean getGuideLines()
	{
		guideLines = preferences.getBoolean("checkBoxGuideLines", true);
		return guideLines;
	}

	public Typeface getTypeface() 
	{
		typefaceValue = Integer.parseInt(preferences.getString("listPreferenceFontType", "0"));
		switch (typefaceValue) {
		case 0:
			typeface = Typeface.DEFAULT;
			break;
		case 1:
			typeface = Typeface.SERIF;
			break;
		case 2:
			typeface = Typeface.SANS_SERIF;
			break;
		case 3:
			typeface = Typeface.MONOSPACE;
			break;
		default:
			break;
		}
		return typeface;
	}
	
	public boolean getAutoLink()
	{
		autoLink = preferences.getBoolean("checkBoxAutoLink", true);
		return autoLink;
	}
	
	public String getRingTone()
	{
		ringTone = preferences.getString("ringtoneNotification", "DEFAULT_RINGTONE_URI");
		return ringTone;
	}
	
	public int getLEDColor()
	{
		LEDColor = Integer.parseInt( preferences.getString("listPreferenceLEDColor", "-16711936"));
		return LEDColor;
	}
	
	public boolean getVibrate()
	{
		vibrate = preferences.getBoolean("checkBoxVibrate", true);
		return vibrate;
	}
	
	public int getTitleSize()
	{
		titleSize = Integer.parseInt(preferences.getString("editTextPreferenceTitle", "25"));
		return titleSize;
	}
	
	public String getSortType()
	{
		sortType = preferences.getString("listPreferenceSortType", " title");
		return sortType;
	}
	
	public int getBackgroundColor()
	{
		backgroundColor = Integer.parseInt(preferences.getString("listPreferenceBackgroundColor", "-1"));
		return backgroundColor;
	}
	
	public int getTextColor() 
	{
		textColor = Integer.parseInt(preferences.getString("listPreferenceTextColor", "-16777216"));
		return textColor;
	}

	public int getLineColor() 
	{
		lineColor = Integer.parseInt(preferences.getString("listPreferenceLinesColor", "-10039894"));
		return lineColor;
	}
}
