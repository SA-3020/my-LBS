package com.example.notify_around.Utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object MethodsUtils {

    fun makeShortToast(context: Context?, text: String?) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun makeLongToast(context: Context?, text: String?) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    fun getCurrentFirebaseUser(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

}