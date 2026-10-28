package com.example.weatherapp.presentation

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import com.example.weatherapp.R

object DialogManager {
    fun showGpsDisabledDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(context.getString(R.string.gps_disabled_title))
        dialog.setMessage(context.getString(R.string.gps_disabled_message))
        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            context.getString(R.string.enable_gps)
        ) { _, _ ->
            listener.onClick(null)
            dialog.dismiss()

        }
        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            context.getString(R.string.cancel)
        ) { _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showSearchDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val edName = EditText(context)
        builder.setView(edName)
        val dialog = builder.create()
        dialog.setTitle(context.getString(R.string.search_city_title))

        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            context.getString(R.string.ok)
        ) { _, _ ->
            listener.onClick(edName.text.toString())
            dialog.dismiss()

        }
        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            context.getString(R.string.cancel)
        ) { _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    interface Listener {
        fun onClick(name: String?)
    }
}