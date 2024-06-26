package org.idp.wallet.verifiable_credentials_library.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoadingScreen() {
  Box(
      modifier = Modifier.fillMaxWidth().fillMaxHeight(),
      contentAlignment = Alignment.Center,
  ) {
    CircularProgressIndicator()
  }
}
