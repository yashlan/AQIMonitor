package com.c22_ce02.awmonitorapp.utils

import android.animation.ValueAnimator
import android.widget.TextView


fun startIncrementTextAnimation(maxValue: Int, label: String, textView: TextView) {
    if (!textView.text.isNullOrEmpty() &&
        textView.text.toString().toInt() == maxValue
    ) return
    val animator = ValueAnimator()
    animator.setObjectValues(0, maxValue)
    animator.addUpdateListener { animation ->
        "${animation.animatedValue}$label".also { textView.text = it }
    }
    animator.duration = 1000
    animator.start()
}

fun startIncrementTextAnimation(maxValue: Int, textView: TextView) {
    if (!textView.text.isNullOrEmpty() &&
        textView.text.toString().toInt() == maxValue
    ) return
    val animator = ValueAnimator()
    animator.setObjectValues(0, maxValue)
    animator.addUpdateListener { animation ->
        textView.text = animation.animatedValue.toString()
    }
    animator.duration = 1000
    animator.start()
}
