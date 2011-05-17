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


import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NotePadTitleEditor extends Activity
{
	public static final String EDIT_TITLE_ACTION = "com.chmbrs.apps.notepad.notes.action.EDIT_TITLE";
	
	private static final String[] PROJECTION = new String[]{
		Notes._ID,
		Notes.TITLE,
	};
	
	private static final int COLUMN_INDEX_TITLE = 1;
	
	private EditText titleEdit;
	private Cursor titleCursor;
	private Uri titleUri;
	
	private GoogleAnalyticsTracker tracker;
	private NotePadApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notetitle);
		app = ((NotePadApplication) NotePadTitleEditor.this.getApplication());
		if (tracker == null)
		{
	        tracker = app.getTracker();			
		}
		
		titleUri = getIntent().getData();
		titleCursor = managedQuery(titleUri, PROJECTION, null, null, null);
		
		titleEdit = (EditText)findViewById(R.id.editTextTitle);
		
		Button saveTitle = (Button)findViewById(R.id.updateNoteTitle);
		saveTitle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				tracker.trackEvent(
		                "Edit Note Title",
		                "Button",
		                "Save Note Title",
		                 0);
				finish();
			}
		});
	}
	
	@Override
	protected void onPause() 
	{
		super.onPause();
		if(titleCursor != null)
		{
			ContentValues values = new ContentValues();
			values.put(Notes.TITLE, titleEdit.getText().toString());
			values.put(Notes.MODIFIED_DATE, System.currentTimeMillis());
			getContentResolver().update(titleUri, values, null, null);
		}
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		if(titleCursor != null)
		{
			titleCursor.moveToFirst();
			titleEdit.setText(titleCursor.getString(COLUMN_INDEX_TITLE));
		}
	}
}
