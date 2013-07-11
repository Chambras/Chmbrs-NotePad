/*
 
    Copyright 2011 Marcelo Zambrana Villarroel.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

package com.chmbrs.apps.notepad;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class NotePadWidgetProvider extends AppWidgetProvider
{
	private static final String TAG ="NotePadWidget";
	
	private static final String MOVE_NEXT = "com.chmbrs.apps.notepad.notes.action.MOVE_NEXT_NOTE";
	private static final String MOVE_BACK = "com.chmbrs.apps.notepad.notes.action.MOVE_PREV_NOTE";
	private static final String REFRESH_NOTES = "com.chmbrs.apps.notepad.notes.action.REFRESH_NOTES";
	private GoogleAnalyticsTracker tracker;
	private NotePadApplication app;
	
	@Override
	public void onEnabled(Context context) 
	{
		super.onEnabled(context);
		app = (NotePadApplication)context.getApplicationContext();
		tracker = app.getTracker();
		tracker.trackEvent(
                "Home Screen", 
                "Widget",
                "Create NotePad Widget",
                 0);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) 
	{
		super.onDeleted(context, appWidgetIds);
		app = (NotePadApplication)context.getApplicationContext();
		tracker = app.getTracker();
		tracker.trackEvent(
                "Home Screen", 
                "Widget",
                "Remove NotePad Widget",
                 0);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) 
	{
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		context.startService(new Intent(REFRESH_NOTES));
		//Log.i(TAG, "widget updated");
	}
	
	public static class NotePadUpdateService extends Service
	{
		private static Cursor cursorNotes; 
		private static Boolean hasNotes = false;
		
		private static String action ="";
		
		@Override
		public int onStartCommand(Intent intent, int flags, int startId) 
		{
			if( intent != null)
			{
				//Log.i(TAG, "starting service " + intent.getAction());
				action = intent.getAction();
			}
			
			ComponentName widgetName = new ComponentName(this, NotePadWidgetProvider.class);
			int [] ids = AppWidgetManager.getInstance(this).getAppWidgetIds(widgetName);
			//Log.i(TAG,"number of widgets: " + ids.length);
			
			if (ids.length > 0)
			{
				if(cursorNotes == null)
				{
					cursorNotes = this.getContentResolver().query(Notes.CONTENT_URI, null, null, null, Notes.DEFAULT_SORT_ORDER);
					if(cursorNotes.getCount() > 0)
					{
						hasNotes = true;
						cursorNotes.moveToFirst();
					}
					else
					{
						hasNotes = false;
					}
				}
				RemoteViews updateViews = buildUpdate(this);
				
				ComponentName notepadWidget = new ComponentName(this, NotePadWidgetProvider.class);
				AppWidgetManager manager = AppWidgetManager.getInstance(this);
				manager.updateAppWidget(notepadWidget, updateViews);
			}
			return START_STICKY;
		}

		/*@Override
		public void onStart(Intent intent, int startId) //deprecated method
		{
			Log.i(TAG, "starting service " + intent.getAction());
			action = intent.getAction();
			RemoteViews updateViews = buildUpdate(this);
			
			ComponentName notepadWidget = new ComponentName(this, NotePadWidgetProvider.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(this);
			manager.updateAppWidget(notepadWidget, updateViews);
		}*/

		private RemoteViews buildUpdate(Context context) 
		{
			if(action.equals(REFRESH_NOTES))
			{
				cursorNotes = context.getContentResolver().query(Notes.CONTENT_URI, null, null, null, Notes.DEFAULT_SORT_ORDER);
				if(cursorNotes.moveToFirst())
				{
					hasNotes = true;
				}
				else
				{
					hasNotes = false;
				}
			}
			else 
			{
				if(cursorNotes == null)
				{
					cursorNotes = context.getContentResolver().query(Notes.CONTENT_URI, null, null, null, Notes.DEFAULT_SORT_ORDER);
					if(cursorNotes.getCount() > 0)
					{
						hasNotes = true;
					}
					else
					{
						hasNotes = false;
					}
				}
				//Log.i(TAG, "count " + cursorNotes.getCount());
				if(action.equals(MOVE_NEXT))
				{
					cursorNotes.moveToNext();
				}
				else if(action.equals(MOVE_BACK))
				{
					cursorNotes.moveToPrevious();
				}
			}


			RemoteViews updateViews = null;
			
			//Log.i(TAG, "total notes: " + cursorNotes.getCount());
			CharSequence noteTitle = "";
			CharSequence noteContent = "";
			long noteID = 0;
				
			if(hasNotes)
			{
				noteTitle = cursorNotes.getString(cursorNotes.getColumnIndex(Notes.TITLE));
				noteContent = cursorNotes.getString(cursorNotes.getColumnIndex(Notes.NOTE));
				noteID = cursorNotes.getLong(cursorNotes.getColumnIndex(Notes._ID));
			}
			else
			{
				noteTitle = "";
				noteContent = getString(R.string.noNotes);
				noteID = 0;
			}
			
			
			Uri uri = ContentUris.withAppendedId(Notes.CONTENT_URI, noteID);
			Intent intentNewNote = new Intent(Intent.ACTION_INSERT, Notes.CONTENT_URI);
            PendingIntent pendingIntentNewNote = PendingIntent.getActivity(context, 0, intentNewNote, 0);
            
            Intent intentEditNote = new Intent(Intent.ACTION_EDIT, uri);
            PendingIntent pendingIntentEditNote = PendingIntent.getActivity(context, 0, intentEditNote, 0);
            
            Intent intentMoveNextNote = new Intent(MOVE_NEXT);
            PendingIntent pendingIntentMoveNextNote = PendingIntent.getService(context, 0, intentMoveNextNote, PendingIntent.FLAG_UPDATE_CURRENT);
			
            Intent intentMovePrevNote = new Intent(MOVE_BACK);
            PendingIntent pendingIntentMovePrevnote = PendingIntent.getService(context, 0, intentMovePrevNote, PendingIntent.FLAG_UPDATE_CURRENT);
            
			updateViews = new RemoteViews(context.getPackageName(), R.layout.notepadwidget);

			
			if(Integer.parseInt(Build.VERSION.SDK)<11)
			{
				//Log.i(TAG, "API: menor a honeycomb");
				//updateViews.setBoolean(R.id.textViewWidgetNoteContent, "setEnabled", hasNotes);

				if((cursorNotes.getPosition() == (cursorNotes.getCount() - 1)) || !hasNotes)
				{
					updateViews.setBoolean(R.id.buttonWidgetRight, "setEnabled", false);
				}
				else
				{
					updateViews.setBoolean(R.id.buttonWidgetRight, "setEnabled", true);
				}
				
				if(cursorNotes.getPosition() == 0)
				{
					updateViews.setBoolean(R.id.buttonWidgetLeft, "setEnabled", false);
				}
				else
				{
					updateViews.setBoolean(R.id.buttonWidgetLeft, "setEnabled", true);
				}
			}
			else
			{
				//Log.i(TAG, "API: mayor a honeycomb " + cursorNotes.getPosition() + " " + cursorNotes.getCount());
				if((cursorNotes.getPosition() == (cursorNotes.getCount() - 1)) || !hasNotes) 
				{
					updateViews.setViewVisibility (R.id.buttonWidgetRight, 4);
				}
				else
				{
					updateViews.setViewVisibility (R.id.buttonWidgetRight, 0);
				}
				
				if(cursorNotes.getPosition() == 0)
				{
					updateViews.setViewVisibility (R.id.buttonWidgetLeft, 4);
				}
				else
				{
					updateViews.setViewVisibility (R.id.buttonWidgetLeft, 0);
				}
			}
			
			updateViews.setTextViewText(R.id.textViewWidgetNoteTitle, noteTitle);
			updateViews.setTextViewText(R.id.textViewWidgetNoteContent, noteContent);
			
			updateViews.setOnClickPendingIntent(R.id.buttonWidgetNewNote, pendingIntentNewNote);
			if(hasNotes)
			{
				updateViews.setOnClickPendingIntent(R.id.textViewWidgetNoteContent, pendingIntentEditNote);
			}
			else
			{
				updateViews.setOnClickPendingIntent(R.id.textViewWidgetNoteContent, pendingIntentNewNote);
			}
			updateViews.setOnClickPendingIntent(R.id.buttonWidgetRight, pendingIntentMoveNextNote);
			updateViews.setOnClickPendingIntent(R.id.buttonWidgetLeft, pendingIntentMovePrevnote);
			return updateViews;
		}
		
		@Override
		public IBinder onBind(Intent intent) 
		{
			return null;
		}
	}
}
