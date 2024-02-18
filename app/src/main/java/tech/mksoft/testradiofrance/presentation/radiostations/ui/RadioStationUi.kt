@file:OptIn(ExperimentalMaterial3Api::class)

package tech.mksoft.testradiofrance.presentation.radiostations.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import org.koin.androidx.compose.koinViewModel
import tech.mksoft.testradiofrance.R
import tech.mksoft.testradiofrance.core.domain.model.RadioStation
import tech.mksoft.testradiofrance.design.components.AppScaffold
import tech.mksoft.testradiofrance.design.components.ErrorState
import tech.mksoft.testradiofrance.design.components.LoadingState
import tech.mksoft.testradiofrance.design.components.RadioStationCard
import tech.mksoft.testradiofrance.design.tools.plus
import tech.mksoft.testradiofrance.presentation.radiostations.RadioStationsViewModel
import tech.mksoft.testradiofrance.presentation.radiostations.model.RadioStationsUiState

@Composable
fun RadioStationsUi() {
    val viewModel = koinViewModel<RadioStationsViewModel>()
    val state by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    AppScaffold(
        pageTitle = stringResource(id = R.string.app_name),
    ) { contentPadding ->
        Crossfade(
            targetState = state,
            label = "RadioStationsUi - Cross Fade Animator",
        ) { currentState ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when (currentState) {
                    is RadioStationsUiState.Empty -> LaunchedEffect(Unit) {
                        viewModel.fetchRadioStations()
                    }

                    is RadioStationsUiState.Loading -> LoadingState(modifier = Modifier.padding(contentPadding))
                    is RadioStationsUiState.Error -> ErrorState(message = currentState.errorMessage, modifier = Modifier.padding(contentPadding))
                    is RadioStationsUiState.Success -> RadioStationsList(
                        stations = currentState.stations,
                        onStationClicked = currentState.onStationClicked,
                        onRefreshRequested = { viewModel.fetchRadioStations() },
                        contentPadding = contentPadding,
                    )
                }
            }
        }
    }
}

@Composable
private fun RadioStationsList(
    stations: ImmutableList<RadioStation>,
    onStationClicked: (RadioStation) -> Unit,
    onRefreshRequested: () -> Unit,
    contentPadding: PaddingValues,
) {
    val pullRefreshState = rememberPullToRefreshState()
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(Unit) {
            onRefreshRequested.invoke()
            pullRefreshState.endRefresh()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(pullRefreshState.nestedScrollConnection),
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = contentPadding plus PaddingValues(
                start = 16.dp,
                top = 10.dp,
                bottom = 40.dp,
                end = 16.dp,
            ),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(stations) { stationItem: RadioStation ->
                RadioStationCard(radioStation = stationItem) {
                    onStationClicked.invoke(stationItem)
                }
            }
        }

        PullToRefreshContainer(
            state = pullRefreshState,
            modifier = Modifier
                .padding(top = contentPadding.calculateTopPadding())
                .align(Alignment.TopCenter),
        )
    }
}