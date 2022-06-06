package com.c22_ce02.awmonitorapp.utils.customview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

class CustomScrollView : ScrollView {

    private var enableScrolling = true

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private fun isEnableScrolling(): Boolean = enableScrolling

    fun setEnableScrolling(enableScrolling: Boolean) {
        this.enableScrolling = enableScrolling
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return if (isEnableScrolling()) {
            super.onInterceptTouchEvent(ev)
        } else {
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return if (isEnableScrolling()) {
            super.onTouchEvent(ev)
        } else {
            false
        }
    }
}