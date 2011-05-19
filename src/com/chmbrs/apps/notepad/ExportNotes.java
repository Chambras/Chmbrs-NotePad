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

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class ExportNotes extends Activity
{
	private static final String TAG ="ExportNotes";
	private TextView exportStatus;
    private static final String exportFolderName = "chmbrs_exported_notes";
	
    private static final String[] PROJECTION = new String[] {
        Notes._ID, // 0
        Notes.TITLE, // 1
        Notes.NOTE,
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.exportnotes);
		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_menu_upload);
		exportStatus = (TextView)findViewById(R.id.textViewExportStatus);
		
		if(checkExternalMedia())
		{
			exportNotes();
		}
		else
		{
			exportStatus.setText(R.string.noSDCard);
		}
		
	}
	
	private boolean	checkExternalMedia()
	{ 
		boolean mExternalStorageAvailable = false; 
		boolean mExternalStorageWriteable = false; 
		String state = Environment.getExternalStorageState(); 

		if (Environment.MEDIA_MOUNTED.equals(state)) 
		{ 
			mExternalStorageAvailable = mExternalStorageWriteable = true; 
		} 
		else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) 
		{ 
			mExternalStorageAvailable = true; 
			mExternalStorageWriteable = false; 
		} 
		else 
		{ 
			//Log.i(TAG,"State="+state+" Not good"); 
			mExternalStorageAvailable = mExternalStorageWriteable = false; 
		} 
		//Log.i(TAG,"Available="+mExternalStorageAvailable + " Writeable="+mExternalStorageWriteable+" State "+state); 
		return (mExternalStorageAvailable && mExternalStorageWriteable); 
	}
	
	private void exportNotes()
	{
		Cursor cursor = managedQuery(Notes.CONTENT_URI, PROJECTION, null, null, null);
		final int title = cursor.getColumnIndex(Notes.TITLE);
		final int note = cursor.getColumnIndex(Notes.NOTE);
		//Log.i(TAG, "notes to be exported: " + cursor.getCount());
		while(cursor.moveToNext())
		{
			try 
			{
				exportNoteToSDCard(cursor.getString(title),cursor.getString(note));
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
			exportStatus.setText(cursor.getString(title));
		}
		Toast.makeText(this, R.string.notesExported, Toast.LENGTH_SHORT).show();
		finish();

	}
	
	private void exportNoteToSDCard(String title, String note) throws IOException 
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
	}
}
