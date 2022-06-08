package com.c22_ce02.awmonitorapp.utils

import android.graphics.Color
import android.view.View
import androidx.core.app.ActivityCompat
import com.c22_ce02.awmonitorapp.R
import com.google.android.material.snackbar.Snackbar

fun showSnackBar(
    layout: View,
    msg: Int,
    titleAction: Int,
    onClickOkAction: () -> Unit
) {
    Snackbar.make(
        layout,
        msg,
        Snackbar.LENGTH_INDEFINITE
    )
        .setAction(titleAction) {
            onClickOkAction.invoke()
        }
        .setActionTextColor(Color.WHITE)
        .setBackgroundTint(ActivityCompat.getColor(layout.context, R.color.colorPrimary))
        .setTextColor(Color.WHITE)
        .show()
}