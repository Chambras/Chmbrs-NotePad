package com.chmbrs.apps.notepad;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

public class NotePadAlarm extends BroadcastReceiver 
{
	private static final String TAG ="NotePadAlarm";
	
	private NotificationManager mNotificationManager;
	
	private NotePadApplication app;
	
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		app = (NotePadApplication)context.getApplicationContext();
		Log.i(TAG, "alarm notification " +intent.getData() );
		
		String notificationService = context.NOTIFICATION_SERVICE;
		mNotificationManager= (NotificationManager) context.getSystemService(notificationService);
		
		int icon = android.R.drawable.stat_notify_more;
		CharSequence tickerText = context.getResources().getString(R.string.notificationTitle);
		long when = System.currentTimeMillis();
		CharSequence notificatioTitle = intent.getExtras().getString("noteTitle");
		CharSequence notificationBody = intent.getExtras().getString("noteBody");
		Intent noteReminderIntent = new Intent(Intent.ACTION_EDIT, intent.getData());
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contetIntent = PendingIntent.getActivity(context, 0, noteReminderIntent, 0);
		
		Notification notification = new Notification(icon, tickerText, when);
		//notification.defaults |= Notification.DEFAULT_SOUND;
		//notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.sound = Uri.parse(app.getRingTone());
		notification.ledARGB= app.getLEDColor();
		notification.ledOnMS = 300;
	    notification.ledOffMS = 1000;
		if(app.getVibrate())
		{
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}
		
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		
		notification.setLatestEventInfo(context, notificatioTitle, notificationBody, contetIntent);
		
		mNotificationManager.notify(1, notification);
	}
}
