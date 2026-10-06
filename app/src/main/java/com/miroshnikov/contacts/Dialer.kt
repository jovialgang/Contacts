package com.miroshnikov.contacts

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

fun dialIntent(phone: String) = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))

fun Context.dial(phone: String) {
    try {
        startActivity(dialIntent(phone))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, R.string.no_dialer, Toast.LENGTH_SHORT).show()
    }
}
