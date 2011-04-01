package com.chmbrs.apps.notepad;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

public class NotePadPreferences extends PreferenceActivity
{

	private final static String TAG ="PreferenceActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.notepadpreferences);
		Preference exportNotes = (Preference)findPreference("preferenceExportNotes");
		Preference aboutMe = (Preference)findPreference("preferenceAboutMe");
		Preference changeLog = (Preference)findPreference("preferenceChangeLog");
		Preference sendFeedBack = (Preference)findPreference("preferenceSendFeedBack");
		Preference moreInfo = (Preference)findPreference("preferenceMoreInfo");
		
		exportNotes.setOnPreferenceClickListener(listener);
		aboutMe.setOnPreferenceClickListener(listener);
		changeLog.setOnPreferenceClickListener(listener);
		sendFeedBack.setOnPreferenceClickListener(listener);
		moreInfo.setOnPreferenceClickListener(listener);
	}
	
	private OnPreferenceClickListener listener = new OnPreferenceClickListener() 
	{
		@Override
		public boolean onPreferenceClick(Preference preference) 
		{
			if(preference.getKey().equals("preferenceExportNotes"))
			{
				exportAllNotes();
			}
			else if(preference.getKey().equals("preferenceAboutMe"))
			{
				showAboutMe();
			}
			else if (preference.getKey().equals("preferenceChangeLog"))
			{
				showChangeLog();
			}
			else if (preference.getKey().equals("preferenceSendFeedBack"))
			{
				sendFeedBack();
			}
			else if (preference.getKey().equals("preferenceMoreInfo"))
			{
				openMyBlog();
			}
			return true;
		}
	};
	
	private void sendFeedBack()
	{
		String appVersion = "";
		final String device = "Device: " + Build.DEVICE;
		final String brand = "Brand: " + Build.BRAND;
		final String model = "Model: " + Build.MODEL;
		final String OSVersion = "OS Version: " + Build.VERSION.RELEASE;
		final String APILevel = "API level: " + Build.VERSION.SDK;
		final String buildNumber = "Build Number: " + Build.DISPLAY;
		
		try 
		{
			appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) 
		{
			e.printStackTrace();
		};
		
		final String messageBody = "\n --- \n" +device + "\n" + brand + "\n" + model +"\n"+  OSVersion + "\n" + APILevel + "\n" + buildNumber;
		Log.i(TAG, appVersion);
		Log.i(TAG, messageBody);
		
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.appEmail)});
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, appVersion);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, messageBody);
		startActivity(Intent.createChooser(emailIntent, getString(R.string.sendFeedBackTitle)));
	}

	protected void exportAllNotes() 
	{
		final Intent exportIntent = new Intent(this, ExportNotes.class);
		startActivity(exportIntent);
	}

	protected void openMyBlog() 
	{
		final Intent blogIntent = new Intent("android.intent.action.VIEW", Uri.parse(getString(R.string.appWebPage)));
		startActivity(blogIntent);  
	}

	protected void showChangeLog() 
	{
		startActivity(new Intent(this, ChangeLog.class));
	}

	protected void showAboutMe() 
	{
		startActivity(new Intent(this, AboutMe.class));
	}
}
