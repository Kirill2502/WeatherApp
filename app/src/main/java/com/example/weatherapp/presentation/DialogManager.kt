package com.example.weatherapp.presentation

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText

object DialogManager {
    fun showGpsDisabledDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle("Уведомление геолокации")
        dialog.setMessage("GPS отключен на вашем устройстве, требуется подключение!")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Включить") { _, _ ->
            listener.onClick(null)
            dialog.dismiss()

        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена") { _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }
    fun showSearchDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val edName = EditText(context)
        builder.setView(edName)
        val dialog = builder.create()
        dialog.setTitle("Название города:")

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            listener.onClick(edName.text.toString())
            dialog.dismiss()

        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена") { _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }
    interface Listener{
        fun onClick(name: String?)
    }
}