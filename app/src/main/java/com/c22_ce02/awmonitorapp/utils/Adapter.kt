package com.c22_ce02.awmonitorapp.utils

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun Fragment.setupAdapter(recycleView: RecyclerView, isVertical: Boolean, addAdapterValue: () -> Unit) {
    recycleView.setHasFixedSize(true)
    if (isVertical) {
        recycleView.layoutManager = LinearLayoutManager(requireContext())
    } else {
        recycleView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }
    addAdapterValue.invoke()
}