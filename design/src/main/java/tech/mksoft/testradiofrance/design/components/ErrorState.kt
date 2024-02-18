package tech.mksoft.testradiofrance.design.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun BoxScope.ErrorState(message: String, modifier: Modifier = Modifier) {
    Text(text = message, modifier = modifier.align(Alignment.Center))
}