package tech.mksoft.testradiofrance.design.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import tech.mksoft.testradiofrance.design.R
import tech.mksoft.testradiofrance.design.theme.TestRadioFranceTheme
import tech.mksoft.testradiofrance.design.theme.Typography

@Composable
fun BoxScope.ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    doOnRetry: (() -> Unit)? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
            .align(Alignment.Center)
            .padding(16.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.illustration_error),
            contentDescription = null,
            modifier = Modifier.height(250.dp)
        )
        Text(
            text = message,
            style = Typography.titleLarge,
            textAlign = TextAlign.Center,
        )

        doOnRetry?.let {
            Button(onClick = doOnRetry) {
                Text(text = stringResource(id = R.string.error_state_retry_button))
            }
        }
    }
}

// region Previews
@PreviewLightDark
@Composable
private fun MakePreview() {
    TestRadioFranceTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                ErrorState(message = "No Network Access", doOnRetry = {})
            }
        }
    }
}
// endregion Previews