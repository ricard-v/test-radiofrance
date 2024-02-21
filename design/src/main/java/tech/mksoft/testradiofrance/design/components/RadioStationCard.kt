package tech.mksoft.testradiofrance.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import tech.mksoft.testradiofrance.core.domain.model.RadioStation
import tech.mksoft.testradiofrance.design.R
import tech.mksoft.testradiofrance.design.theme.TestRadioFranceTheme
import tech.mksoft.testradiofrance.design.theme.Typography

@Composable
fun RadioStationCard(
    radioStation: RadioStation,
    onSeeAllProgramsClicked: () -> Unit,
    onFavoriteClicked: () -> Unit,
    onPlayLiveStreamClicked: (() -> Unit)?,
    isFavorite: Boolean,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 10.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                ),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                StationName(name = radioStation.name)
                RadioStationFavoriteButton(
                    isFavorite = isFavorite,
                    stationName = radioStation.name,
                    onFavoriteClicked = onFavoriteClicked,
                )
            }

            radioStation.pitch?.let {
                Spacer(modifier = Modifier.height(10.dp))
                StationPitch(pitch = it)
            }

            radioStation.description?.let {
                Spacer(modifier = Modifier.height(12.dp))
                StationDescription(description = it)
            }

            Spacer(modifier = Modifier.height(16.dp))
            onPlayLiveStreamClicked?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    PlayLiveStreamButton(onClicked = onPlayLiveStreamClicked, stationName = radioStation.name, isPlaying = isPlaying)
                    SeeAllProgramsButton(onClicked = onSeeAllProgramsClicked)
                }
            } ?: run {
                SeeAllProgramsButton(onClicked = onSeeAllProgramsClicked, modifier = Modifier.align(Alignment.End))
            }

        }
    }
}

@Composable
private fun StationName(name: String) {
    Text(
        text = name,
        style = Typography.titleMedium,
    )
}

@Composable
private fun StationPitch(pitch: String) {
    Text(
        text = pitch,
        style = Typography.labelMedium,
        fontStyle = FontStyle.Italic,
    )
}

@Composable
private fun StationDescription(description: String) {
    Text(
        text = description,
        style = Typography.bodyMedium,
    )
}

@Composable
private fun PlayLiveStreamButton(
    stationName: String,
    isPlaying: Boolean,
    onClicked: () -> Unit,
) {
    IconButton(onClick = onClicked) {
        val (icon, contentDescription) = if (isPlaying) {
            Icons.Filled.Stop to stringResource(id = R.string.radio_station_stop_livestream_button, stationName)
        } else {
            Icons.Filled.PlayArrow to stringResource(id = R.string.radio_station_play_livestream_button, stationName)
        }

        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
        )
    }
}

@Composable
private fun SeeAllProgramsButton(onClicked: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(
        onClick = onClicked,
        modifier = modifier,
    ) {
        Text(text = stringResource(id = R.string.radio_station_see_all_programs_button))
        Spacer(modifier = Modifier.width(8.dp))
        Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null)
    }
}

// region Previews
@PreviewLightDark
@Composable
private fun MakePreviewFullContent() {
    var isFavorite by remember {
        mutableStateOf(false)
    }
    var isPlaying by remember {
        mutableStateOf(false)
    }

    TestRadioFranceTheme {
        RadioStationCard(
            radioStation = RadioStation(
                id = "id",
                name = "franceinfo",
                pitch = "Et tout est plus clair",
                description = "L'actualité en direct et en continu avec le média global du service public",
                liveStreamUrl = "https://icecast.radiofrance.fr/franceinter-midfi.mp3?id=openapi",
            ),
            onSeeAllProgramsClicked = {}, // nothing to do here
            onPlayLiveStreamClicked = { isPlaying = !isPlaying },
            isFavorite = isFavorite,
            isPlaying = isPlaying,
            onFavoriteClicked = { isFavorite = !isFavorite }
        )
    }
}

@PreviewLightDark
@Composable
private fun MakePreviewMissingSomeContent() {
    var isFavorite by remember {
        mutableStateOf(false)
    }

    TestRadioFranceTheme {
        RadioStationCard(
            radioStation = RadioStation(
                id = "id",
                name = "franceinfo",
                pitch = null,
                description = null,
                liveStreamUrl = null,
            ),
            onSeeAllProgramsClicked = {}, // nothing to do here
            onPlayLiveStreamClicked = null,
            isFavorite = isFavorite,
            isPlaying = false,
            onFavoriteClicked = { isFavorite = !isFavorite },
        )
    }
}
// endregion Previews