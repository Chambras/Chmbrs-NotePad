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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.LiveFolders;

public class NotePadLiveFolder extends Activity
{
	public static final Uri CONTENT_URI = Uri.parse("content://"+ Notes.PROVIDER_NAME+"/live_folders/notes");
	public static final Uri NOTE_URI = Uri.parse("content://"+ Notes.PROVIDER_NAME+"/notes/#");
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		final Intent intent = getIntent();
		final String action = intent.getAction();
		
		if(LiveFolders.ACTION_CREATE_LIVE_FOLDER.equals(action))
		{
			setResult(RESULT_OK, createLiveFolder(this, CONTENT_URI, getString(R.string.defaultLiveFolderName), android.R.drawable.ic_menu_agenda));
		}
		else
		{
			setResult(RESULT_CANCELED);
		}
		finish();
	}
	private static Intent createLiveFolder(Context context, Uri contentUri, String folderName, int icon) 
	{
		final Intent intent = new Intent();
		intent.setData(CONTENT_URI);
		intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_NAME, folderName);
		intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_ICON, Intent.ShortcutIconResource.fromContext(context, icon));
		intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_DISPLAY_MODE, LiveFolders.DISPLAY_MODE_LIST);
		intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_BASE_INTENT, new Intent(Intent.ACTION_EDIT, NOTE_URI));
		return intent;
	}
}