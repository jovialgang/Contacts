package com.miroshnikov.contacts

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun hasContactsPermission(context: Context) =
    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) ==
        PackageManager.PERMISSION_GRANTED

@Composable
fun ContactsApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var granted by remember { mutableStateOf(hasContactsPermission(context)) }
    var asked by rememberSaveable { mutableStateOf(false) }
    var contacts by remember { mutableStateOf<List<Contact>?>(null) }
    var reloadKey by remember { mutableIntStateOf(0) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        granted = hasContactsPermission(context)
    }

    LifecycleResumeEffect(Unit) {
        granted = hasContactsPermission(context)
        reloadKey++
        onPauseOrDispose { }
    }

    LaunchedEffect(granted) {
        if (!granted && !asked) {
            asked = true
            launcher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    LaunchedEffect(granted, reloadKey) {
        if (granted) {
            contacts = withContext(Dispatchers.IO) { context.fetchAllContacts() }
        }
    }

    fun onGrant() {
        val activity = context as Activity
        if (activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
            launcher.launch(Manifest.permission.READ_CONTACTS)
        } else {
            val uri = Uri.fromParts("package", context.packageName, null)
            context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri))
        }
    }

    val list = contacts
    when {
        !granted && asked -> PermissionDenied(onGrant = { onGrant() }, modifier = modifier)
        !granted || list == null -> Loading(modifier)
        list.isEmpty() -> EmptyContacts(modifier)
        else -> ContactsList(list, onClick = { context.dial(it.phoneNumber) }, modifier = modifier)
    }
}

@Composable
fun ContactsList(contacts: List<Contact>, onClick: (Contact) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier.fillMaxSize().testTag("contacts_list")) {
        items(contacts) { contact ->
            ListItem(
                headlineContent = { Text(contact.name, Modifier.testTag("contact_name")) },
                supportingContent = { Text(contact.phoneNumber, Modifier.testTag("contact_phone")) },
                modifier = Modifier.clickable { onClick(contact) }.testTag("contact_item")
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun EmptyContacts(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(stringResource(R.string.no_contacts), Modifier.testTag("empty"))
    }
}

@Composable
fun Loading(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(Modifier.testTag("loading"))
    }
}

@Composable
fun PermissionDenied(onGrant: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().padding(16.dp).testTag("permission_denied"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.permission_rationale), textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onGrant, modifier = Modifier.testTag("grant_permission")) {
            Text(stringResource(R.string.grant_permission))
        }
    }
}
