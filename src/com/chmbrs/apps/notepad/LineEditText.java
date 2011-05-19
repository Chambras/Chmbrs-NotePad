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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

public class LineEditText extends EditText {
	private static final String TAG = "LineEditText";
	private Paint editPaint;
	private NotePadApplication app;

	public LineEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		app = (NotePadApplication) context.getApplicationContext();
		editPaint = new Paint();
		editPaint.setStyle(Paint.Style.STROKE);
		
		//adding these parameters to preferences
		//editPaint.setARGB(255, 102, 205, 170);
		editPaint.setColor(app.getLineColor());
		setBackgroundColor(app.getBackgroundColor());
		setTextColor(app.getTextColor());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (app.getGuideLines()) {

			int count = getLineCount();
			int height = this.getMeasuredHeight();
			int line_height = this.getLineHeight();
			int page_size = height / line_height + 1;

			if (count < page_size) {
				count = page_size;
			}

			int posY = getCompoundPaddingTop();

			for (int i = 1; i < count; i++) {
				posY += line_height;
				canvas.drawLine(0, posY, getRight(), posY, editPaint);
				canvas.save();
			}
		}
		super.onDraw(canvas);
		canvas.restore();
	}
}
