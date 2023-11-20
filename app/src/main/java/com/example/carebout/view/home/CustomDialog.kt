package com.example.carebout.view.home

import android.app.AlertDialog
import android.content.Context
import android.view.View
import com.example.carebout.R

class CustomDialog(context: Context) {
    private val con = context

    private val builder: AlertDialog.Builder by lazy {
        AlertDialog.Builder(context).setView(view)
    }

    private val view: View by lazy {
        View.inflate(context, R.layout.custom_dialog, null)
    }

    private var dialog: AlertDialog? = null


}