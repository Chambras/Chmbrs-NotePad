package com.chmbrs.apps.c2dm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class C2DMessaging 
{
	private static final String TAG ="C2DM";
	
	//preferences keys to store registration information we store them in a priovate shared preferences instead of using a table. 
	public static final String REQUEST_REGISTRATION_INTENT = "com.google.android.c2dm.intent.REGISTER";
	public static final String PREFERENCE = "com.google.android.c2dm";
	public static final String LAST_REGISTRATION_CHANGE = "last_registration_change";
	public static final String BACKOFF = "backoff";
	public static final String REGISTRATION_ID = "c2dm_registration";
	public static final String GSF_PACKAGE = "com.google.android.gsf";
	
	//default time to wait in case we need to retry
	private static final long DEFAULT_BACKOFF = 30000;
	
	//registration extras 
	public static final String EXTRA_APPLICATION_PENDING_INTENT = "app";
	public static final String EXTRA_SENDER = "sender";

	public static void register(Context context, String senderID) 
	{
		// create a C2DM registration intent
		Intent registrationIntent = new Intent(REQUEST_REGISTRATION_INTENT);
		//registrationIntent.setPackage(GSF_PACKAGE);
		// add to it our application's "signature"
		registrationIntent.putExtra(EXTRA_APPLICATION_PENDING_INTENT, PendingIntent.getBroadcast(context, 0, new Intent(), 0));
		// role email that our app server will use
		// later to authenticate before it can use C2DM
		registrationIntent.putExtra(EXTRA_SENDER, senderID);
		// request C2DM registration (async operation) starts a c2dm service
		Log.i(TAG, "sending registration...");
		context.startService(registrationIntent);
	}
	
	public static void unregister(Context context, String senderID)
	{
		Intent registrationIntent = new Intent(REQUEST_REGISTRATION_INTENT);
        registrationIntent.putExtra(EXTRA_APPLICATION_PENDING_INTENT, PendingIntent.getBroadcast(context, 0, new Intent(), 0));
        registrationIntent.putExtra(EXTRA_SENDER, senderID);
        context.startService(registrationIntent);
	}

	public static String getRegistrationID(Context context)
	{
		final SharedPreferences prefs = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
		String registrationID = prefs.getString(REGISTRATION_ID, "");
		return registrationID;
	}
	
	public static long getLastRegistrationChange(Context context) 
	{
        final SharedPreferences prefs = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        return prefs.getLong(LAST_REGISTRATION_CHANGE, 0);
    }
	
	static long getBackOff(Context context) 
	{
		final SharedPreferences prefs = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
		return prefs.getLong(LAST_REGISTRATION_CHANGE, DEFAULT_BACKOFF );
	}

	static void setBackOffTime(Context context, long backoffTimeMs) 
	{
		final SharedPreferences prefs = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putLong(BACKOFF, backoffTimeMs);
	}
	
	static void clearRegistrationID(Context context) 
	{
		final SharedPreferences prefs = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(REGISTRATION_ID, "");
		editor.putLong(LAST_REGISTRATION_CHANGE , System.currentTimeMillis());
		editor.commit();
	}

	static void setRegistrationID(Context context, String registrationID) 
	{
		final SharedPreferences prefs = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
		Log.i(TAG, "Registration ID!!! " + registrationID);
		Editor editor = prefs.edit();
		editor.putString(REGISTRATION_ID, registrationID);
		editor.commit();
	}
}
