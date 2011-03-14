package com.chmbrs.apps.notepad;


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

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notetitle);
		
		titleUri = getIntent().getData();
		titleCursor = managedQuery(titleUri, PROJECTION, null, null, null);
		
		titleEdit = (EditText)findViewById(R.id.editTextTitle);
		
		Button saveTitle = (Button)findViewById(R.id.updateNoteTitle);
		saveTitle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
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
