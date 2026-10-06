package com.miroshnikov.contacts

import android.content.Context
import android.provider.ContactsContract
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric

@RunWith(AndroidJUnit4::class)
class FetchContactsTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp() {
        FakeContactsProvider.rows.clear()
        Robolectric.setupContentProvider(FakeContactsProvider::class.java, ContactsContract.AUTHORITY)
    }

    @Test
    fun readsNamesAndNumbers() {
        FakeContactsProvider.rows.add(arrayOf("Alice", "+7 999 111-11-11"))
        FakeContactsProvider.rows.add(arrayOf("Bob", "222"))

        assertEquals(
            listOf(Contact("Alice", "+7 999 111-11-11"), Contact("Bob", "222")),
            context.fetchAllContacts()
        )
    }

    @Test
    fun sortsByNameIgnoringCase() {
        FakeContactsProvider.rows.add(arrayOf("bob", "1"))
        FakeContactsProvider.rows.add(arrayOf("Alice", "2"))
        FakeContactsProvider.rows.add(arrayOf("Carl", "3"))

        assertEquals(
            listOf("Alice", "bob", "Carl"),
            context.fetchAllContacts().map { it.name }
        )
    }

    @Test
    fun replacesMissingValuesWithNA() {
        FakeContactsProvider.rows.add(arrayOf(null, null))

        assertEquals(listOf(Contact("N/A", "N/A")), context.fetchAllContacts())
    }

    @Test
    fun returnsEmptyListWhenNoContacts() {
        assertEquals(emptyList<Contact>(), context.fetchAllContacts())
    }
}
