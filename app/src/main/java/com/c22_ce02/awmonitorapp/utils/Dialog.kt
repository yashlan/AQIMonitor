package com.c22_ce02.awmonitorapp.utils

import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.c22_ce02.awmonitorapp.R

fun AppCompatActivity.createCustomAlertDialog(
    title: String,
    message: String,
    actionPositiveButton: () -> Unit
) {
    val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
    val view = LayoutInflater.from(this)
        .inflate(
            R.layout.dialog_custom,
            findViewById<CardView>(R.id.layoutDialogContainer)
        )
    builder.setView(view)
    view.findViewById<TextView>(R.id.tvDialogTitle).text = title
    view.findViewById<TextView>(R.id.tvDialogMessage).text = message

    val alertDialog = builder.setCancelable(false).create()

    view.findViewById<Button>(R.id.buttonDialogNo).setOnClickListener {
        alertDialog.dismiss()
    }

    view.findViewById<Button>(R.id.buttonDialogYes).setOnClickListener {
        actionPositiveButton.invoke()
        alertDialog.dismiss()
    }

    alertDialog.show()
}