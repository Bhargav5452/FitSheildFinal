package com.example.fitsheild

import android.app.AlertDialog
import android.content.Context


class BuildAlertDialog(private val context: Context) {

    fun create(
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String? = null,
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null
    ) {
        val builder = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { _, _ ->
                onPositiveClick?.invoke()
            }
            .apply {
                if (negativeButtonText != null) {
                    setNegativeButton(negativeButtonText) { _, _ ->
                        onNegativeClick?.invoke()
                    }
                }
            }
            .setCancelable(false)

        builder.create().show()
    }
}

