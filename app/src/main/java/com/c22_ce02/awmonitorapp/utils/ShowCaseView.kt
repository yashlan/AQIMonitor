package com.c22_ce02.awmonitorapp.utils

import android.content.Context
import android.view.View
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity


fun Context.showGuide(
    targetView: View,
    content: String,
    onDismiss: () -> Unit
) {
    GuideView.Builder(this)
        .setContentText(content)
        .setGravity(Gravity.center)
        .setDismissType(DismissType.targetView)
        .setTargetView(targetView)
        .setContentTextSize(15)
        .setGuideListener {
            onDismiss.invoke()
        }
        .build()
        .show()
}