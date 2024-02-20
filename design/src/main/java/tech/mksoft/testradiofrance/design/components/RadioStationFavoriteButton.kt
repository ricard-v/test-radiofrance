package tech.mksoft.testradiofrance.design.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import tech.mksoft.testradiofrance.design.R
import tech.mksoft.testradiofrance.design.theme.TestRadioFranceTheme

@Composable
fun RadioStationFavoriteButton(
    isFavorite: Boolean,
    stationName: String,
    onFavoriteClicked: () -> Unit,
) {
    IconButton(
        onClick = onFavoriteClicked,
    ) {
        if (isFavorite) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = stringResource(id = R.string.radio_station_favorite_button, stationName),
            )
        } else {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = stringResource(id = R.string.radio_station_unfavorite_button, stationName),
            )
        }
    }
}

// region Previews
@PreviewLightDark
@Composable
private fun MakePreview() {
    TestRadioFranceTheme {
        var isFavorite by remember {
            mutableStateOf(false)
        }

        RadioStationFavoriteButton(
            isFavorite = isFavorite,
            stationName = "Franceinfo",
            onFavoriteClicked = {
                isFavorite = !isFavorite
            }
        )
    }
}
// endregion Previews