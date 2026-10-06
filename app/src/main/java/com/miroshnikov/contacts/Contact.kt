package com.miroshnikov.contacts

import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Phone

data class Contact(val name: String, val phoneNumber: String)

fun Context.fetchAllContacts(): List<Contact> {
    val cursor = contentResolver.query(
        Phone.CONTENT_URI,
        arrayOf(Phone.DISPLAY_NAME, Phone.NUMBER),
        null,
        null,
        null
    ) ?: return emptyList()

    val contacts = mutableListOf<Contact>()
    cursor.use {
        val nameIndex = it.getColumnIndexOrThrow(Phone.DISPLAY_NAME)
        val numberIndex = it.getColumnIndexOrThrow(Phone.NUMBER)
        while (it.moveToNext()) {
            val name = it.getString(nameIndex) ?: "N/A"
            val number = it.getString(numberIndex) ?: "N/A"
            contacts.add(Contact(name, number))
        }
    }
    return contacts.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
}
