package com.aresid.simplepasswordgeneratorapp.thumbprogressseekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;

import com.aresid.simplepasswordgeneratorapp.R;

/**
 * Created on: 01/04/2020
 * For Project: pass13
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
public class ThumbProgressSeekBar
		extends androidx.appcompat.widget.AppCompatSeekBar {
	private static final String    TAG = "ThumbProgressSeekBar";
	private              int       mThumbSize;
	private              TextPaint mTextPaint;
	private              Rect      mBounds;
	
	public ThumbProgressSeekBar(Context context) {
		this(context, null);
	}
	
	public ThumbProgressSeekBar(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.seekBarStyle);
	}
	
	public ThumbProgressSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mThumbSize = getResources().getDimensionPixelSize(R.dimen.thumb_size);
		mTextPaint = new TextPaint();
		mTextPaint.setColor(Color.BLACK);
		mTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.thumb_text_size));
		mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mBounds = new Rect();
	}
	
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		Log.d(TAG, "onDraw: called");
		super.onDraw(canvas);
		String progressText = String.valueOf(getProgress());
		mTextPaint.getTextBounds(progressText, 0, progressText.length(), mBounds);
		int leftPadding = getPaddingLeft() - getThumbOffset();
		int rightPadding = getPaddingRight() - getThumbOffset();
		int width = getWidth() - leftPadding - rightPadding;
		float progressRatio = (float) getProgress() / getMax();
		float thumbOffset = mThumbSize * (.5f - progressRatio);
		float thumbX = progressRatio * width + leftPadding + thumbOffset;
		float thumbY = getHeight() / 2f + mBounds.height() / 2f;
		canvas.drawText(progressText, thumbX, thumbY, mTextPaint);
	}
}
