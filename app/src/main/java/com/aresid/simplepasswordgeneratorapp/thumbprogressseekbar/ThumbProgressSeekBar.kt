package com.aresid.simplepasswordgeneratorapp.thumbprogressseekbar

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatSeekBar
import com.aresid.simplepasswordgeneratorapp.R

/**
 * Created on: 01/04/2020
 * For Project: pass13
 * Author: René Spies
 * Copyright: © 2020 ARES ID
 */
class ThumbProgressSeekBar @JvmOverloads constructor(
	context: Context?,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = android.R.attr.seekBarStyle
): AppCompatSeekBar(
	context!!,
	attrs,
	defStyleAttr
) {
	
	private val mThumbSize: Int
	private val mTextPaint: TextPaint
	private val mBounds: Rect
	
	@Synchronized
	override fun onDraw(canvas: Canvas) {
		Log.d(
			TAG,
			"onDraw: called"
		)
		super.onDraw(canvas)
		val progressText = progress.toString()
		mTextPaint.getTextBounds(
			progressText,
			0,
			progressText.length,
			mBounds
		)
		val leftPadding = paddingLeft - thumbOffset
		val rightPadding = paddingRight - thumbOffset
		val width = width - leftPadding - rightPadding
		val progressRatio = progress.toFloat() / max
		val thumbOffset = mThumbSize * (.5f - progressRatio)
		val thumbX = progressRatio * width + leftPadding + thumbOffset
		val thumbY = height / 2f + mBounds.height() / 2f
		canvas.drawText(
			progressText,
			thumbX,
			thumbY,
			mTextPaint
		)
	}
	
	companion object {
		private const val TAG = "ThumbProgressSeekBar"
	}
	
	init {
		mThumbSize = resources.getDimensionPixelSize(R.dimen.thumb_size)
		mTextPaint = TextPaint()
		mTextPaint.isAntiAlias = true
		mTextPaint.color = Color.BLACK
		mTextPaint.textSize = resources.getDimensionPixelSize(R.dimen.thumb_text_size).toFloat()
		mTextPaint.typeface = Typeface.DEFAULT_BOLD
		mTextPaint.textAlign = Paint.Align.CENTER
		mBounds = Rect()
	}
}