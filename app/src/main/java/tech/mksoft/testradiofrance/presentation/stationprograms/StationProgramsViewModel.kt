package tech.mksoft.testradiofrance.presentation.stationprograms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.domain.model.StationProgram
import tech.mksoft.testradiofrance.core.domain.usecase.GetStationsPrograms
import tech.mksoft.testradiofrance.core.domain.usecase.UserPreferencesUseCase
import tech.mksoft.testradiofrance.presentation.stationprograms.model.CannotLoadMorePrograms
import tech.mksoft.testradiofrance.presentation.stationprograms.model.LoadMorePrograms
import tech.mksoft.testradiofrance.presentation.stationprograms.model.StationProgramsUiState

class StationProgramsViewModel(
    private val stationId: String,
    private val getStationProgramsUseCase: GetStationsPrograms,
    private val userPreferencesUseCase: UserPreferencesUseCase,
) : ViewModel() {
    private val _uiStateFlow: MutableStateFlow<StationProgramsUiState> = MutableStateFlow(StationProgramsUiState.Empty)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val lastProgramCursorFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    private var loadProgramJob: Job? = null

    fun startFetchingStationPrograms() {
        viewModelScope.launch {
            lastProgramCursorFlow
                .onStart { _uiStateFlow.value = StationProgramsUiState.Loading }
                .collectLatest(::loadProgramsForStation)
        }
    }

    private fun loadProgramsForStation(fromCursor: String?) {
        loadProgramJob?.cancel()
        loadProgramJob = viewModelScope.launch {
            combine(
                getStationProgramsUseCase.getProgramsForRadioStation(stationId = stationId, count = 10, fromCursor = fromCursor),
                userPreferencesUseCase.getFavoriteRadioStation(),
                transform = ::mapStationsPrograms,
            ).collectLatest {
                _uiStateFlow.value = it
            }
        }
    }

    private fun mapStationsPrograms(result: DataRequestResult<List<StationProgram>>, favoriteStationId: String?): StationProgramsUiState {
        val isFavorite = stationId == favoriteStationId

        return when (result) {
            is DataRequestResult.Error -> StationProgramsUiState.Error(
                errorMessage = result.errorMessage ?: "Unknown Error",
                onRetryClicked = ::startFetchingStationPrograms,
                isFavorite = isFavorite,
            )

            is DataRequestResult.Success -> {
                StationProgramsUiState.Success(
                    programs = result.data.toImmutableList(),
                    loadMorePrograms = result.data.lastOrNull()?.takeUnless { it.isLast }?.cursor?.let { lastCursor ->
                        LoadMorePrograms(
                            isLoading = false,
                            onClicked = {
                                (_uiStateFlow.value as? StationProgramsUiState.Success)?.let { current ->
                                    _uiStateFlow.value = current.copy(
                                        loadMorePrograms = current.loadMorePrograms?.copy(isLoading = true)
                                    )
                                }
                                lastProgramCursorFlow.value = lastCursor
                            },
                        )
                    },
                    cannotLoadMorePrograms = takeIf { result.data.lastOrNull()?.isLast == true }?.let { CannotLoadMorePrograms },
                    isFavorite = isFavorite,
                )
            }
        }
    }

    fun onFavoriteActionClicked(stationId: String) {
        viewModelScope.launch {
            val currentFavoriteStationId = userPreferencesUseCase.getFavoriteRadioStation().first()
            if (currentFavoriteStationId == stationId) { // was un-favorited
                userPreferencesUseCase.unFavoriteRadioStation()
            } else { // now is favorite
                userPreferencesUseCase.setFavoriteRadioStation(stationId = stationId)
            }
        }
    }
}