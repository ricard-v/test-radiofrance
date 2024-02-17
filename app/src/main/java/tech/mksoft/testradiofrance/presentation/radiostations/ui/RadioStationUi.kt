package tech.mksoft.testradiofrance.presentation.radiostations.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import org.koin.androidx.compose.koinViewModel
import tech.mksoft.testradiofrance.core.domain.model.RadioStation
import tech.mksoft.testradiofrance.design.components.ErrorState
import tech.mksoft.testradiofrance.design.components.LoadingState
import tech.mksoft.testradiofrance.presentation.radiostations.RadioStationsState
import tech.mksoft.testradiofrance.presentation.radiostations.RadioStationsViewModel
import tech.mksoft.testradiofrance.ui.theme.Typography

@Composable
fun RadioStationsUi() {
    val viewModel = koinViewModel<RadioStationsViewModel>()
    val state by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val value = state) {
            is RadioStationsState.Empty -> LaunchedEffect(Unit) {
                viewModel.fetchRadioStations()
            }

            is RadioStationsState.Loading -> LoadingState()
            is RadioStationsState.Error -> ErrorState(message = value.errorMessage)
            is RadioStationsState.Success -> RadioStationsList(stations = value.stations)
        }
    }
}

@Composable
private fun RadioStationsList(stations: ImmutableList<RadioStation>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(stations) { item: RadioStation ->
            Text(text = item.name, style = Typography.bodyMedium)
        }
    }
}