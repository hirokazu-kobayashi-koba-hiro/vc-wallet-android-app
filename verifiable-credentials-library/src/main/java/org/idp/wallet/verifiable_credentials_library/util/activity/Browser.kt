package org.idp.wallet.verifiable_credentials_library.util.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.browser.customtabs.CustomTabsIntent

fun open(context: Context, uri: String) {
  val customTabsIntent = CustomTabsIntent.Builder().build()
  customTabsIntent.intent.setData(Uri.parse(uri))
  customTabsIntent.launchUrl(context, Uri.parse(uri))
}

fun open(launcher: ActivityResultLauncher<Intent>, uri: String) {
  val customTabsIntent = CustomTabsIntent.Builder().build()
  customTabsIntent.intent.setData(Uri.parse(uri))
  launcher.launch(customTabsIntent.intent)
}
