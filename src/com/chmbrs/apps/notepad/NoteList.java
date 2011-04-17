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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class NoteList extends ListActivity 
{
	private static final String TAG = "NoteList";
    /**
     * The columns we are interested in from the database
     */
    private static final String[] PROJECTION = new String[] {
            Notes._ID, // 0
            Notes.TITLE, // 1
            Notes.NOTE,
    };

    /** The index of the title column */
    private static final int COLUMN_INDEX_TITLE = 1;
    
    private GoogleAnalyticsTracker tracker;
    
    protected AccountManager  accountManager;
    
    private static final String exportFolderName = "chmbrs_exported_notes";
    
    private static final String GOOGLENALYTICSACCOUNT = "UA-20712505-2";
    
    private NotePadApplication app;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notelist);
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

        
        Log.i(TAG, "starting the main activity");
        
        app = ((NotePadApplication) NoteList.this.getApplication());

        Intent intent = getIntent();
        handleIntent(intent);
    }

	@Override
	protected void onNewIntent(Intent intent) 
	{
		setIntent(intent);
		Log.i(TAG, "NEW INTENT");
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) 
	{
		if (intent.getData() == null) 
        {
            intent.setData(Notes.CONTENT_URI);
            Log.i(TAG, "no data in the intent...");
        }
		if(Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
			String query = intent.getStringExtra(SearchManager.QUERY);
			setTitle(getString(R.string.searchResultsTitle) + " " + query);
        	Log.i(TAG, "searching... " + query);
        	showResults(query);
        }
		else if(Intent.ACTION_MAIN.equals(intent.getAction()))
		{   
//	        tracker = GoogleAnalyticsTracker.getInstance();
			if (tracker == null)
			{
		        tracker = app.getTracker();
		        tracker.start(GOOGLENALYTICSACCOUNT, this);				
			}
			
	        getListView().setOnCreateContextMenuListener(this);
	        Log.i(TAG, "starting the app " + getIntent().getData());
	        
	        Cursor cursor = managedQuery(getIntent().getData(), PROJECTION, null, null, null);
	        String [] from = new String[] {Notes.TITLE, Notes.NOTE}; 
	        int [] to = new int[] {R.id.text1, R.id.text2};
	        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.noterow, cursor, from, to);
	        setListAdapter(adapter);
	        //getListView().setFastScrollEnabled(true);
	        Log.i(TAG, "loading notes.... " + cursor.getCount());
		}
		/*accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType("com.google");
		//ContentResolver.setIsSyncable(accounts[0], "com.chmbrs.apps.notepad", 1);*/
	}

	private void showResults(String query) 
	{
		Log.i(TAG, "displaing results: " );
		Cursor searchCursor = managedQuery(Notes.CONTENT_URI, null, "note like '%"+query+"%'", null, null);
		if (searchCursor != null)
		{
			String [] from = new String[] {Notes.TITLE}; 
	        int [] to = new int[] {R.id.text1};
	        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.noterow, searchCursor, from, to);
	        setListAdapter(adapter);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
	{
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menu info ", e);
			return;
		}
		Cursor cursor = (Cursor)getListAdapter().getItem(info.position);
		if(cursor == null)
		{
			Log.w(TAG, "the item is not available anymore.");
			return;
		}
		menu.setHeaderTitle(cursor.getString(COLUMN_INDEX_TITLE));
		getMenuInflater().inflate(R.menu.listnotescontextmenu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) 
	{
		AdapterView.AdapterContextMenuInfo info = null;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			
		} catch (ClassCastException e) {
			Log.e(TAG, "error getting context menu info " + e);
		}
		Uri noteUri = ContentUris.withAppendedId(getIntent().getData(), info.id);
		TextView noteTitle = (TextView) info.targetView.findViewById(R.id.text1);
		TextView  noteContent = (TextView) info.targetView.findViewById(R.id.text2);
		
		switch (item.getItemId()) 
		{
			case R.id.contextItemOpenNote:
				//Uri uri = ContentUris.withAppendedId(getIntent().getData(), info.id);
				startActivity(new Intent(Intent.ACTION_EDIT, noteUri));
				Log.i(TAG, "abriendo nota");
				break;
			case R.id.contextItemDeleteNote:
				//Uri noteUri = ContentUris.withAppendedId(getIntent().getData(), info.id);
				getContentResolver().delete(noteUri, null, null);
				Log.i(TAG, "borrando nota");
				break;
			case R.id.contextItemShareNote:
				Log.i(TAG, "compartiendo nota " + info.id);
				shareNote(noteContent.getText().toString());
				break;
			case R.id.contextItemPinNote:
				Log.i(TAG,"pin note to home screen " + noteTitle.getText());
				setupShortCut(info.id, noteTitle.getText().toString());
				break;
			case R.id.contextItemSendToCalendar:
				sendToCalendar(noteTitle.getText().toString(), noteContent.getText().toString());
				break;
//			case R.id.contextItemOpenAppNote:
//				Log.i(TAG, "abriendo app nota");
//				break;
			case R.id.contextItemExportToTextFile:
				Log.i(TAG, "exportando nota");
				try 
				{
					exportNoteToSDCard(noteTitle.getText().toString(), noteContent.getText().toString());
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
				break;
			default:
				break;
		}
		return false;
	}

	private boolean	checkExternalMedia()
	{ 
		boolean mExternalStorageAvailable = false; 
		boolean mExternalStorageWriteable = false; 
		String state = Environment.getExternalStorageState(); 

		if (Environment.MEDIA_MOUNTED.equals(state)) 
		{ 
			// We can read and write the media 
			mExternalStorageAvailable = mExternalStorageWriteable = true; 
		} 
		else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) 
		{ 
			// We can only read the media 
			mExternalStorageAvailable = true; 
			mExternalStorageWriteable = false; 
		} 
		else 
		{ 
			// Something else is wrong. It may be one of many other states, but all we need 
			//  to know is we can neither read nor write 
			Log.i(TAG,"State="+state+" Not good"); 
			mExternalStorageAvailable = mExternalStorageWriteable = false; 
		} 
		Log.i(TAG,"Available="+mExternalStorageAvailable + " Writeable="+mExternalStorageWriteable+" State "+state); 
		return (mExternalStorageAvailable && mExternalStorageWriteable); 
	}
	
	private void exportNoteToSDCard(String title, String note) throws IOException 
	{
		if(checkExternalMedia())
		{
			String dateFolder = new SimpleDateFormat("dd-MM-yyyy").format(new Date()); //"25-03-2011";
			File directory = new File (Environment.getExternalStorageDirectory().getPath()+"/" + exportFolderName); 

			if (!directory.exists()) 
			{ 
				directory.mkdir(); 
			} 
			
			File noteDirectory = new File (Environment.getExternalStorageDirectory().getPath()+"/" + exportFolderName +"/" + dateFolder);
			if (!noteDirectory.exists()) 
			{ 
				noteDirectory.mkdir(); 
			} 

			File file = new File(noteDirectory.getPath()+"/"+title + ".txt"); 
			if (!file.exists() && noteDirectory.exists())
			{ 
				try 
				{ 
					file.createNewFile(); 
				} catch (IOException e) 
				{ 
					Log.d(TAG,"File creation failed for " + file); 
				} 
			} 
			if (file.exists() && file.canWrite())
			{ 
				FileWriter history_writer = new FileWriter(file, false);
                BufferedWriter out = new BufferedWriter(history_writer);
                
                out.write(note);
                out.close();
				Log.e(TAG, "duplicated");
			} 
			else 
			{ 
				Log.e(TAG, "Failed to write the file to SDCard"); 
			} 
			Toast.makeText(this, R.string.noteExported, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(this, R.string.noSDCard, Toast.LENGTH_SHORT).show();
			Log.i(TAG, "SD card not mounted");
		}
	}

	private void sendToCalendar(String title, String description) 
	{
		Intent calendarIntent = new Intent(Intent.ACTION_EDIT);
		calendarIntent.setType("vnd.android.cursor.item/event");
		calendarIntent.putExtra("title", title);
		calendarIntent.putExtra("description", description);
	
		startActivity(calendarIntent);
	}

	private void shareNote(String noteContent) 
	{
			Intent share = new Intent(android.content.Intent.ACTION_SEND);
			share.setType("text/plain");
			share.putExtra(Intent.EXTRA_TEXT, noteContent);
			startActivity(Intent.createChooser(share, "sharing my note"));
	}
	
	private void setupShortCut(long id, String shortcutName)
	{
		Log.i(TAG,"creando el shorcut...");
		Uri noteUri = ContentUris.withAppendedId(getIntent().getData(), id);
		Intent shortcutIntent = new Intent(Intent.ACTION_EDIT, noteUri);
		
		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
		Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this, android.R.drawable.ic_menu_upload);
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		sendBroadcast(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.noteslistmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
			case R.id.itemAddNote:
				startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
				Log.i(TAG, "creando una nueva nota...");
				tracker.trackPageView("/Add New Note");
				break;
			case R.id.itemSearch:
				onSearchRequested();
		        tracker.trackEvent(
		                "Clicks",  // Category
		                "Button",  // Action
		                "search", // Label
		                77);
				Log.i(TAG, "search notes: ");
				break;
			case R.id.itemCategories:
		        tracker.trackEvent(
		                "Clicks",  // Category
		                "Button",  // Action
		                "categories", // Label
		                66);       // Value
//				Log.i(TAG, "loading categories");
				break;
			case R.id.itemSettings:
				//tracker.setCustomVar(1, "Navigation Type", "Button click", 3);
				startActivity(new Intent(this, NotePadPreferences.class));
				break;
		}
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
		Log.i(TAG, "editing note " + id);
		//Log.i(TAG, "editing note " + uri + " with action " + action);
		startActivity(new Intent(Intent.ACTION_EDIT, uri));
	}
	
	@Override
	protected void onResume() 
	{
		refreshNotes();
		super.onResume();
	}

	private void refreshNotes() 
	{
		Cursor cursor = managedQuery(getIntent().getData(), PROJECTION, null, null, app.getSortType());
        
        String [] from = new String[] {Notes.TITLE, Notes.NOTE}; 
        int [] to = new int[] {R.id.text1, R.id.text2};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.noterow, cursor, from, to);
        setListAdapter(adapter);
	}

	protected void postNoteChanges() 
	{
		Log.i(TAG, "new note has been created...");
	}

	@Override
	protected void onPause() 
	{
		super.onPause();
		tracker.dispatch();
		Log.i(TAG, "pausing the app");
	}

	//@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		tracker.stop();
	}
}