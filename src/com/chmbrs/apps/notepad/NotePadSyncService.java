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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class NotePadSyncService extends Service
{
	private SyncAdapter noteSyncAdapter = null;

	@Override
	public void onCreate() 
	{
		if (noteSyncAdapter == null )
		{
			noteSyncAdapter = new SyncAdapter (getApplicationContext(), false);
			Log.i("CHAMBRAS","starting sync service");
		}
	}

	@Override
	public IBinder onBind(Intent intent) 
	{
		Log.i("CHAMBRAS","stop adapter");
		return noteSyncAdapter.getSyncAdapterBinder();
	}

	@Override
	public void onDestroy() 
	{
		Log.i("CHAMBRAS","stop adapter for ever...");
		super.onDestroy();
	}
}
