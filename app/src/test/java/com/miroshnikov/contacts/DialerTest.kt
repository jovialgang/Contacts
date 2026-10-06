package com.miroshnikov.contacts

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DialerTest {
    @Test
    fun buildsDialIntent() {
        val intent = dialIntent("+7 999 123-45-67")

        assertEquals(Intent.ACTION_DIAL, intent.action)
        assertEquals("tel", intent.data?.scheme)
        assertEquals("+7 999 123-45-67", intent.data?.schemeSpecificPart)
    }

    @Test
    fun keepsSpecialCharacters() {
        val intent = dialIntent("*#06#")

        assertEquals("*#06#", intent.data?.schemeSpecificPart)
    }
}
