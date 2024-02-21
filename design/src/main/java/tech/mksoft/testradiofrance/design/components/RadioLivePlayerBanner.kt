package tech.mksoft.testradiofrance.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import tech.mksoft.testradiofrance.design.R
import tech.mksoft.testradiofrance.design.components.LivePlayerState.INITIALIZING
import tech.mksoft.testradiofrance.design.components.LivePlayerState.PAUSED
import tech.mksoft.testradiofrance.design.components.LivePlayerState.PLAYING
import tech.mksoft.testradiofrance.design.theme.TestRadioFranceTheme

@Composable
fun RadioLivePlayerBanner(
    playingStationName: String,
    state: LivePlayerState,
    onMediaButtonClicked: (MediaButton) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        shape = RoundedCornerShape(
            topStart = 10.dp,
            topEnd = 10.dp,
        ),
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(PLAYER_HEIGHT.dp),
        ) {
            IconButton(onClick = { onMediaButtonClicked.invoke(MediaButton.STOP) }) {
                Icon(imageVector = Icons.Filled.Stop, contentDescription = stringResource(id = R.string.live_player_stop_play_button))
            }
            PlayingStationName(stationName = playingStationName)
            Spacer(modifier = Modifier.weight(1f))
            when (state) {
                INITIALIZING -> LoadingAnimation()
                PLAYING -> MediaIconButton(isPlaying = true, onMediaButtonClicked)
                PAUSED -> MediaIconButton(isPlaying = false, onMediaButtonClicked)
                else -> Unit
            }
        }
    }
}

@Composable
private fun PlayingStationName(stationName: String) {
    val contentDescription = stringResource(id = R.string.live_player_playing_station_label, stationName)
    Text(
        text = stationName,
        modifier = Modifier.semantics {
            this.contentDescription = contentDescription
        }
    )
}

@Composable
private fun LoadingAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.media_loading_lottie_animation))
    val progress by animateLottieCompositionAsState(composition)

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(48.dp),
    )
}

@Composable
private fun MediaIconButton(isPlaying: Boolean, onMediaButtonClicked: (MediaButton) -> Unit) {
    IconButton(onClick = { onMediaButtonClicked.invoke(MediaButton.PLAY_PAUSE) }) {
        Icon(
            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            contentDescription = stringResource(
                id = if (isPlaying)
                    R.string.live_player_pause_play_button
                else
                    R.string.live_player_resume_play_button
            ),
        )
    }
}

const val PLAYER_HEIGHT = 40

val LocalLivePlayerPlaying = compositionLocalOf { 0.dp }

enum class LivePlayerState {
    INITIALIZING,
    READY,
    PLAYING,
    PAUSED,
    NONE,
}

enum class MediaButton {
    PLAY_PAUSE,
    STOP,
}

// region Previews
@PreviewLightDark
@Composable
private fun MakePreviews() {
    TestRadioFranceTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 20.dp,
                        start = 8.dp,
                        end = 8.dp,
                    ),
            ) {
                LivePlayerState.entries.forEach { state ->
                    RadioLivePlayerBanner(
                        playingStationName = "Franceinfo",
                        state = state,
                        onMediaButtonClicked = {}, // nothing to do here
                    )
                }
            }
        }
    }
}
// endregion Previews