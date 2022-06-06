package com.c22_ce02.awmonitorapp.utils.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.c22_ce02.awmonitorapp.R

class CustomEditText : AppCompatEditText {

    private var txtHintColor: Int = 0
    private var bgColor: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        val conn = super.onCreateInputConnection(outAttrs)
        outAttrs.imeOptions = outAttrs.imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION.inv()
        return conn
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setHintTextColor(txtHintColor)
        setBackgroundColor(bgColor)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        if (error != null) setTextColor(Color.RED) else setTextColor(
            ContextCompat.getColor(
                context,
                R.color.black
            )
        )
    }

    private fun init() {
        txtHintColor = ContextCompat.getColor(context, R.color.hint)
        bgColor = ContextCompat.getColor(context, R.color.white)
        doOnTextChanged { c, _, _, _ ->
            when (inputType) {
                INPUT_TYPE_EMAIL -> {
                    if (!isEmailValid(c)) {
                        setError(resources.getString(R.string.email_invalid), null)
                    }
                }
                INPUT_TYPE_PASSWORD -> {
                    if (!isPasswordValid(c)) {
                        setError(resources.getString(R.string.password_invalid), null)
                    }
                }
                INPUT_TYPE_NAME -> {
                    inputAlphabetCheck(c.toString(), onNotMatch = {
                        setError(resources.getString(R.string.name_invalid), null)
                    })
                }
            }
        }
    }

    fun hideSoftKeyboard() {
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.applicationWindowToken, 0)
    }

    fun isNotError(): Boolean = error == null

    private fun isEmailValid(email: CharSequence?): Boolean =
        email?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() } == true

    private fun inputAlphabetCheck(inputValue: String?, onNotMatch: (() -> Unit)?): Boolean {
        if (inputValue?.matches("[a-zA-Z]+".toRegex()) == true) {
            return true
        }
        onNotMatch?.invoke()
        return false
    }

    private fun isPasswordValid(c: CharSequence?): Boolean = c?.length!! > 5

    companion object {
        private const val INPUT_TYPE_EMAIL = 33
        private const val INPUT_TYPE_PASSWORD = 129
        private const val INPUT_TYPE_NAME = 97
    }
}