package com.c22_ce02.awmonitorapp.utils

import android.graphics.Color
import android.view.View
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
        .setBackgroundTint(Color.BLACK)
        .setTextColor(Color.WHITE)
        .show()
}