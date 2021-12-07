package com.example.notify_around.Utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity

object MethodsUtils {
    fun makeShortToast(context: Context?, text: String?) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun makeLongToast(context: Context?, text: String?) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    fun goToActivity(context: Context?, activity: AppCompatActivity) {
        startActivity(context!!, Intent(context, activity::class.java), null);

    }
}