package com.c22_ce02.awmonitorapp.utils

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan


object Text {
    fun spannableStringBuilder(
        text: String,
        startChar: Char,
        reduceBy: Float
    ): SpannableStringBuilder {
        val smallSizeText = RelativeSizeSpan(reduceBy)
        val ssBuilder = SpannableStringBuilder(text)
        ssBuilder.setSpan(
            smallSizeText,
            text.indexOf(startChar),
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return ssBuilder
    }
}