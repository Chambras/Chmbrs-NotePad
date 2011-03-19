package com.chmbrs.apps.notepad;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NotePadSyncService extends Service
{
	private SyncAdapter noteSyncAdapter = null;

	@Override
	public void onCreate() 
	{
		if (noteSyncAdapter == null )
		{
			noteSyncAdapter = new SyncAdapter (getApplicationContext(), false);
		}
	}

	@Override
	public IBinder onBind(Intent intent) 
	{
		return noteSyncAdapter.getSyncAdapterBinder();
	}
}
