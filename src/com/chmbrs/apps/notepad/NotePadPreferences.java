package com.chmbrs.apps.notepad;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class NotePadPreferences extends PreferenceActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.notepadpreferences);
	}
}
