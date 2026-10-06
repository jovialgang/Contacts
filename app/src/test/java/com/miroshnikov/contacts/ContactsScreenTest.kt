package com.miroshnikov.contacts

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val contacts = listOf(Contact("Alice", "111"), Contact("Bob", "222"))

    @Test
    fun showsContacts() {
        composeRule.setContent { ContactsList(contacts, onClick = {}) }

        composeRule.onNodeWithTag("contacts_list").assertIsDisplayed()
        composeRule.onAllNodesWithTag("contact_item").assertCountEquals(2)
        val names = composeRule.onAllNodesWithTag("contact_name", useUnmergedTree = true)
        names[0].assertTextEquals("Alice")
        names[1].assertTextEquals("Bob")
        val phones = composeRule.onAllNodesWithTag("contact_phone", useUnmergedTree = true)
        phones[0].assertTextEquals("111")
        phones[1].assertTextEquals("222")
    }

    @Test
    fun clickReturnsContact() {
        var clicked: Contact? = null
        composeRule.setContent { ContactsList(contacts, onClick = { clicked = it }) }

        composeRule.onAllNodesWithTag("contact_item")[1].performClick()

        assertEquals(Contact("Bob", "222"), clicked)
    }

    @Test
    fun showsEmptyState() {
        composeRule.setContent { EmptyContacts() }

        composeRule.onNodeWithTag("empty").assertIsDisplayed()
    }

    @Test
    fun showsLoading() {
        composeRule.setContent { Loading() }

        composeRule.onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    fun grantButtonCallsBack() {
        var pressed = false
        composeRule.setContent { PermissionDenied(onGrant = { pressed = true }) }

        composeRule.onNodeWithTag("permission_denied").assertIsDisplayed()
        composeRule.onNodeWithTag("grant_permission").performClick()

        assertTrue(pressed)
    }
}
