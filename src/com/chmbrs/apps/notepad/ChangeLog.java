package com.chmbrs.apps.notepad;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class ChangeLog extends Activity
{
	private static final String TAG ="ChangeLog";
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.changelog);
		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_info);
	}
}
