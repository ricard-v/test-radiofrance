@file:OptIn(ExperimentalMaterial3Api::class)

package tech.mksoft.testradiofrance.presentation.stationprograms.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import org.koin.androidx.compose.koinViewModel
import tech.mksoft.testradiofrance.core.domain.model.StationProgram
import tech.mksoft.testradiofrance.design.components.AppScaffold
import tech.mksoft.testradiofrance.design.components.ErrorState
import tech.mksoft.testradiofrance.design.components.LoadingState
import tech.mksoft.testradiofrance.design.components.NavigationAction
import tech.mksoft.testradiofrance.design.components.StationProgramCard
import tech.mksoft.testradiofrance.design.tools.plus
import tech.mksoft.testradiofrance.presentation.stationprograms.StationProgramsViewModel
import tech.mksoft.testradiofrance.presentation.stationprograms.model.StationProgramsUiState

@Composable
fun StationProgramsUi(stationId: String, onBackArrowClicked: () -> Unit) {
    val viewModel = koinViewModel<StationProgramsViewModel>()
    val state by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    AppScaffold(
        pageTitle = stationId,
        navigationAction = NavigationAction(
            icon = Icons.AutoMirrored.Outlined.ArrowBack,
            onClicked = onBackArrowClicked,
        )
    ) { contentPadding ->
        Crossfade(
            targetState = state,
            label = "StationProgramsUi - Cross Fade Animator",
        ) { currentState ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when (currentState) {
                    StationProgramsUiState.Empty -> {
                        LaunchedEffect(Unit) {
                            viewModel.fetchProgramsForStation(stationId)
                        }
                    }

                    is StationProgramsUiState.Error -> ErrorState(message = currentState.errorMessage, modifier = Modifier.padding(contentPadding))
                    StationProgramsUiState.Loading -> LoadingState(modifier = Modifier.padding(contentPadding))
                    is StationProgramsUiState.Success -> StationProgramsList(
                        programs = currentState.programs,
                        onRefreshRequested = { viewModel.fetchProgramsForStation(stationId) },
                        contentPadding = contentPadding,
                    )
                }
            }
        }
    }
}

@Composable
private fun StationProgramsList(
    programs: ImmutableList<StationProgram>,
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
            items(programs) { programItem: StationProgram ->
                StationProgramCard(stationProgram = programItem)
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