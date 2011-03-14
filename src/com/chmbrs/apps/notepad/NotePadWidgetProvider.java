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
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class NotePadWidgetProvider extends AppWidgetProvider
{
	private static final String TAG ="NotePadWidget";
	
	private static final String MOVE_NEXT = "com.chmbrs.apps.notepad.notes.action.MOVE_NEXT_NOTE";
	private static final String MOVE_BACK = "com.chmbrs.apps.notepad.notes.action.MOVE_PREV_NOTE";
	private static final String REFRESH_NOTES = "com.chmbrs.apps.notepad.notes.action.REFRESH_NOTES";
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) 
	{
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		//Log.i(TAG, "widget updated");
		context.startService(new Intent(REFRESH_NOTES));
	}
	
	public static class NotePadUpdateService extends Service
	{
		private static Cursor cursorNotes; 
		private static Boolean hasNotes = false;
		
		private static String action ="";
		
		@Override
		public void onStart(Intent intent, int startId) 
		{
			//Log.i(TAG, "starting service " + intent.getAction());
			action = intent.getAction();
			RemoteViews updateViews = buildUpdate(this);
			
			ComponentName notepadWidget = new ComponentName(this, NotePadWidgetProvider.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(this);
			manager.updateAppWidget(notepadWidget, updateViews);
		}

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
			else if(action.equals(MOVE_NEXT))
			{
				//Log.i(TAG, "count " + cursorNotes.getCount());
				cursorNotes.moveToNext();
			}
			else if(action.equals(MOVE_BACK))
			{
				cursorNotes.moveToPrevious();
			}

			RemoteViews updateViews = null;
			
			//Log.i(TAG, "total notes: " + cursorNotes.getCount());
			
			CharSequence noteTitle = cursorNotes.getString(cursorNotes.getColumnIndex(Notes.TITLE));
			CharSequence noteContent = cursorNotes.getString(cursorNotes.getColumnIndex(Notes.NOTE));
			long noteID = cursorNotes.getLong(cursorNotes.getColumnIndex(Notes._ID));
			
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
			
			updateViews.setBoolean(R.id.textViewWidgetNoteContent, "setEnabled", hasNotes);
			if(cursorNotes.getPosition() == (cursorNotes.getCount() - 1))
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
			
			updateViews.setTextViewText(R.id.textViewWidgetNoteTitle, noteTitle);
			updateViews.setTextViewText(R.id.textViewWidgetNoteContent, noteContent);
			
			updateViews.setOnClickPendingIntent(R.id.buttonWidgetNewNote, pendingIntentNewNote);
			updateViews.setOnClickPendingIntent(R.id.textViewWidgetNoteContent, pendingIntentEditNote);
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
