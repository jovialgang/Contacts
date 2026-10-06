package com.miroshnikov.contacts

import android.Manifest
import android.app.Application
import android.content.Intent
import android.provider.ContactsContract
import android.provider.Settings
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val composeRule = createEmptyComposeRule()

    private val app: Application = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp() {
        FakeContactsProvider.rows.clear()
        Robolectric.setupContentProvider(FakeContactsProvider::class.java, ContactsContract.AUTHORITY)
    }

    private fun waitForTag(tag: String) {
        composeRule.waitUntil(5000) {
            composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun showsContactsWhenGranted() {
        shadowOf(app).grantPermissions(Manifest.permission.READ_CONTACTS)
        FakeContactsProvider.rows.add(arrayOf("Alice", "111"))
        FakeContactsProvider.rows.add(arrayOf("Bob", "222"))

        ActivityScenario.launch(MainActivity::class.java).use {
            waitForTag("contact_item")
            composeRule.onNodeWithTag("contacts_list").assertIsDisplayed()
            assertEquals(2, composeRule.onAllNodesWithTag("contact_item").fetchSemanticsNodes().size)
        }
    }

    @Test
    fun showsEmptyWhenNoContacts() {
        shadowOf(app).grantPermissions(Manifest.permission.READ_CONTACTS)

        ActivityScenario.launch(MainActivity::class.java).use {
            waitForTag("empty")
            composeRule.onNodeWithTag("empty").assertIsDisplayed()
        }
    }

    @Test
    fun clickOpensDialer() {
        shadowOf(app).grantPermissions(Manifest.permission.READ_CONTACTS)
        FakeContactsProvider.rows.add(arrayOf("Alice", "111"))

        ActivityScenario.launch(MainActivity::class.java).use {
            waitForTag("contact_item")
            composeRule.onNodeWithTag("contact_item").performClick()

            val intent = shadowOf(app).nextStartedActivity
            assertEquals(Intent.ACTION_DIAL, intent.action)
            assertEquals("111", intent.data?.schemeSpecificPart)
        }
    }

    @Test
    fun deniedShowsButtonThatOpensSettings() {
        shadowOf(app).denyPermissions(Manifest.permission.READ_CONTACTS)
        shadowOf(app.packageManager)
            .setShouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS, false)

        ActivityScenario.launch(MainActivity::class.java).use {
            waitForTag("permission_denied")
            composeRule.onNodeWithTag("grant_permission").performClick()

            val intent = shadowOf(app).nextStartedActivity
            assertEquals(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, intent.action)
            assertEquals("package:com.miroshnikov.contacts", intent.data.toString())
        }
    }
}
