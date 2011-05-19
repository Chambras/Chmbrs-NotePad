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

	@Override
	protected void onPause() 
	{
		super.onPause();
		
	}
}
