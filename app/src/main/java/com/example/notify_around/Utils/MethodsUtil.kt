package com.example.notify_around.Utils

import android.content.Context
import android.widget.Toast

object MethodsUtils {
    fun makeToast(context: Context?, text: String?) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

}