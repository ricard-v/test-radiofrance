package tech.mksoft.testradiofrance.design.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import tech.mksoft.testradiofrance.core.domain.model.RadioStation
import tech.mksoft.testradiofrance.design.theme.TestRadioFranceTheme
import tech.mksoft.testradiofrance.design.theme.Typography

@Composable
fun RadioStationCard(radioStation: RadioStation, onSeeAllProgramsClicked: () -> Unit) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            StationName(name = radioStation.name)

            radioStation.pitch?.let {
                Spacer(modifier = Modifier.height(10.dp))
                StationPitch(pitch = it)
            }

            radioStation.description?.let {
                Spacer(modifier = Modifier.height(12.dp))
                StationDescription(description = it)
            }

            Spacer(modifier = Modifier.height(16.dp))
            SeeAllProgramsButton(onClicked = onSeeAllProgramsClicked)
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
private fun ColumnScope.SeeAllProgramsButton(onClicked: () -> Unit) {
    TextButton(
        onClick = onClicked,
        modifier = Modifier.align(Alignment.End),
    ) {
        Text(text = "Voir tous les programmes")
        Spacer(modifier = Modifier.width(8.dp))
        Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null)
    }
}

@PreviewLightDark
@Composable
private fun MakePreviewFullContent() {
    TestRadioFranceTheme {
        RadioStationCard(
            radioStation = RadioStation(
                id = "id",
                name = "franceinfo",
                pitch = "Et tout est plus clair",
                description = "L'actualité en direct et en continu avec le média global du service public",
            )
        ) {
            // nothing to do here
        }
    }
}

@PreviewLightDark
@Composable
private fun MakePreviewMisingSomeContent() {
    TestRadioFranceTheme {
        RadioStationCard(
            radioStation = RadioStation(
                id = "id",
                name = "franceinfo",
                pitch = null,
                description = null,
            )
        ) {
            // nothing to do here
        }
    }
}