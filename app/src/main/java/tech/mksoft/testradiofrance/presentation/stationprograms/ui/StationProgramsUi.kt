@file:OptIn(ExperimentalMaterial3Api::class)

package tech.mksoft.testradiofrance.presentation.stationprograms.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import tech.mksoft.testradiofrance.presentation.stationprograms.model.LoadMorePrograms
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
        (state as? StationProgramsUiState.Success)?.let {
            StationProgramsList(
                programs = it.programs,
                loadMorePrograms = it.loadMorePrograms,
                onRefreshRequested = { viewModel.fetchProgramsForStation(stationId) },
                contentPadding = contentPadding,
            )
        } ?: run {
            Crossfade(
                targetState = state,
                label = "StationProgramsUi - Cross Fade Animator",
            ) { currentState ->
                Box(modifier = Modifier.fillMaxSize()) {
                    when (currentState) {
                        StationProgramsUiState.Empty -> {
                            LaunchedEffect(Unit) {
                                viewModel.fetchProgramsForStation(stationId)
                            }
                        }

                        is StationProgramsUiState.Error -> ErrorState(
                            message = currentState.errorMessage,
                            modifier = Modifier.padding(contentPadding),
                        )

                        StationProgramsUiState.Loading -> LoadingState(
                            modifier = Modifier.padding(contentPadding),
                        )

                        else -> Unit // Nothing to do here
                    }
                }
            }
        }
    }
}

@Composable
private fun StationProgramsList(
    programs: ImmutableList<StationProgram>,
    loadMorePrograms: LoadMorePrograms?,
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
            items(
                items = programs,
                key = { programItem -> programItem.id }
            ) { programItem: StationProgram ->
                StationProgramCard(stationProgram = programItem)
            }

            loadMorePrograms?.let {
                item { LoadMoreProgramsItem(it) }
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

@Composable
private fun LoadMoreProgramsItem(loadMorePrograms: LoadMorePrograms) {
    Crossfade(
        targetState = loadMorePrograms.isLoading,
        label = "LoadMoreProgramsItem - Cross Fade Animator",
    ) { isLoading ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            if (isLoading) {
                LoadingState()
            } else {
                OutlinedButton(
                    onClick = loadMorePrograms.onClicked,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(text = "Voir plus de programmes")
                }
            }
        }
    }
}