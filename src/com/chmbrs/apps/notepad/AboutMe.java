package com.chmbrs.apps.notepad;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class AboutMe extends Activity 
{
	private static final String TAG="AboutMe";
	
	private TextView currentVersion;
	
	private NotePadApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.aboutme);
		
		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_info);
		
		app = (NotePadApplication) AboutMe.this.getApplication();
		
		currentVersion = (TextView)findViewById(R.id.textViewCurrentVersion);
		currentVersion.setText(app.getCurrentApplicationVersionName());
	}
}
