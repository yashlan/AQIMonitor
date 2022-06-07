package com.c22_ce02.awmonitorapp.utils

import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.c22_ce02.awmonitorapp.R
import com.shashank.sony.fancygifdialoglib.FancyGifDialog


private lateinit var loadingDialog: AlertDialog

fun AppCompatActivity.createCustomAlertDialog(
    title: String,
    message: String,
    gifRes: Int,
    actionPositiveButton: () -> Unit
) {
    FancyGifDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setTitleTextColor(R.color.black)
        .setDescriptionTextColor(R.color.black)
        .setNegativeBtnText("Tidak")
        .setNegativeBtnBackground(R.color.warna_baik)
        .setPositiveBtnText("Ya")
        .setPositiveBtnBackground(R.color.warna_baik)
        .setGifResource(gifRes)
        .isCancellable(false)
        .OnPositiveClicked {
            actionPositiveButton.invoke()
        }
        .OnNegativeClicked {

        }
        .build()
}

fun Fragment.createCustomAlertDialog(
    title: String,
    message: String,
    gifRes: Int,
    actionPositiveButton: () -> Unit
) {
    FancyGifDialog.Builder(requireActivity())
        .setTitle(title)
        .setMessage(message)
        .setTitleTextColor(R.color.black)
        .setDescriptionTextColor(R.color.black)
        .setNegativeBtnText("Tidak")
        .setNegativeBtnBackground(R.color.warna_baik)
        .setPositiveBtnText("Ya")
        .setPositiveBtnBackground(R.color.warna_baik)
        .setGifResource(gifRes)
        .isCancellable(false)
        .OnPositiveClicked {
            actionPositiveButton.invoke()
        }
        .OnNegativeClicked {

        }
        .build()
}

fun AppCompatActivity.showLoadingDialog() {
    val view = LayoutInflater.from(this)
        .inflate(
            R.layout.loading_dialog,
            findViewById<ConstraintLayout>(R.id.loading_layout)
        )
    loadingDialog = AlertDialog.Builder(this, R.style.AlertDialogLoadingTheme)
        .setView(view)
        .setCancelable(false)
        .create()
    loadingDialog.show()
}

fun hideLoadingDialog() {
    loadingDialog.dismiss()
}