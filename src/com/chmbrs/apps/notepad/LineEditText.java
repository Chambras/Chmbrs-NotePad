package com.chmbrs.apps.notepad;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

public class LineEditText extends EditText 
{
	private static final String TAG = "LineEditText";
	private Paint editPaint;

	public LineEditText(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		editPaint = new Paint();
		editPaint.setStyle(Paint.Style.STROKE);
		editPaint.setARGB(255, 102,205,170);
	}

	@Override
	protected void onDraw(Canvas canvas) 
	{
		int count = getLineCount();
        int height = this.getMeasuredHeight();
        int line_height = this.getLineHeight();
        int page_size = height / line_height + 1;

        if (count < page_size) 
        {
            count = page_size;
        }

        int posY = getCompoundPaddingTop(); 

        for (int i = 1; i < count; i++) 
        {
            posY +=  line_height;
            canvas.drawLine(0, posY, getRight(), posY, editPaint);
            canvas.save();
        }
      super.onDraw(canvas);
      canvas.restore();
	}
}
