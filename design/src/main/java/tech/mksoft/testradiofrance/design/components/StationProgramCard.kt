package tech.mksoft.testradiofrance.design.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import tech.mksoft.testradiofrance.core.domain.model.StationProgram
import tech.mksoft.testradiofrance.design.theme.TestRadioFranceTheme
import tech.mksoft.testradiofrance.design.theme.Typography

@Composable
fun StationProgramCard(stationProgram: StationProgram) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            ProgramTitle(title = stationProgram.title)

            stationProgram.description?.let {
                Spacer(modifier = Modifier.height(10.dp))
                ProgramDescription(description = it)
            }
        }
    }
}

@Composable
private fun ProgramTitle(title: String) {
    Text(
        text = title,
        style = Typography.titleMedium,
    )
}

@Composable
private fun ProgramDescription(description: String) {
    Text(
        text = description,
        style = Typography.bodyMedium,
    )
}

// region Previews
@PreviewLightDark
@Composable
private fun MakePreviewWithDescription() {
    TestRadioFranceTheme {
        StationProgramCard(
            stationProgram = StationProgram(
                id = "id",
                cursor = "Cursor",
                title = "Les défis franceinfo",
                description = "Tout l'été, pendant une semaine entière, un reporter de franceinfo relève " +
                        "un challenge pour une série de reportages décalés"
            )
        )
    }
}

@PreviewLightDark
@Composable
private fun MakePreviewWithoutDescription() {
    TestRadioFranceTheme {
        StationProgramCard(
            stationProgram = StationProgram(
                id = "id",
                cursor = "Cursor",
                title = "Les défis franceinfo",
                description = null,
            )
        )
    }
}
// endregion Previews