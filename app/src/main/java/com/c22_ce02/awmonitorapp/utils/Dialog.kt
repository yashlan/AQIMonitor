package com.c22_ce02.awmonitorapp.utils

import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.c22_ce02.awmonitorapp.R


private lateinit var loadingDialog: AlertDialog

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

fun Fragment.createCustomAlertDialog(
    title: String,
    message: String,
    actionPositiveButton: () -> Unit
) {
    val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
    val view = LayoutInflater.from(requireContext())
        .inflate(
            R.layout.dialog_custom,
            requireActivity().findViewById<CardView>(R.id.layoutDialogContainer)
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

fun AppCompatActivity.showLoadingDialog() {
    val view = LayoutInflater.from(this)
        .inflate(
            R.layout.loading_layout,
            findViewById<ConstraintLayout>(R.id.loading_layout)
        )
    val loading = view.findViewById<ImageView>(R.id.imgLoading)
    loadImageViaGlide(R.drawable.loading_bar, loading)
    loadingDialog = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        .setView(view)
        .setCancelable(false)
        .create()
    loadingDialog.show()
}

fun hideLoadingDialog() {
    loadingDialog.hide()
    loadingDialog.dismiss()
}