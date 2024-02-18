package tech.mksoft.testradiofrance.design.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun BoxScope.LoadingState(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier.align(Alignment.Center)
    )
}