package com.miroshnikov.contacts

import android.Manifest
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmokeTest {
    @get:Rule(order = 0)
    val permission: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.READ_CONTACTS)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun showsListOrEmpty() {
        composeRule.waitUntil(5000) {
            composeRule.onAllNodesWithTag("contacts_list").fetchSemanticsNodes().isNotEmpty() ||
                composeRule.onAllNodesWithTag("empty").fetchSemanticsNodes().isNotEmpty()
        }
    }
}
