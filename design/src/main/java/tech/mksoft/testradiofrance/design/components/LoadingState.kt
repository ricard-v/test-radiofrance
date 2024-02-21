package tech.mksoft.testradiofrance.design.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import tech.mksoft.testradiofrance.design.R
import tech.mksoft.testradiofrance.design.theme.TestRadioFranceTheme

@Composable
fun BoxScope.LoadingState(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_lottie_animation))
    val progress by animateLottieCompositionAsState(composition)

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
            .height(150.dp)
            .align(Alignment.Center),
    )
}

// region Previews
@PreviewLightDark
@Composable
private fun MakePreview() {
    TestRadioFranceTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                LoadingState()
            }
        }
    }
}
// endregion Previews