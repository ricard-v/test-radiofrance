package tech.mksoft.testradiofrance.presentation.stationprograms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.domain.model.StationProgram
import tech.mksoft.testradiofrance.core.domain.usecase.GetStationsPrograms
import tech.mksoft.testradiofrance.presentation.stationprograms.model.LoadMorePrograms
import tech.mksoft.testradiofrance.presentation.stationprograms.model.StationProgramsUiState

class StationProgramsViewModel(private val getStationProgramsUseCase: GetStationsPrograms) : ViewModel() {
    private val _uiStateFlow: MutableStateFlow<StationProgramsUiState> = MutableStateFlow(StationProgramsUiState.Empty)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun fetchProgramsForStation(stationId: String) {
        viewModelScope.launch {
            _uiStateFlow.value = StationProgramsUiState.Loading
            _uiStateFlow.value = when (val result = getStationProgramsUseCase.execute(stationId)) {
                is DataRequestResult.Error -> StationProgramsUiState.Error(errorMessage = result.errorMessage ?: "Unknown Error")
                is DataRequestResult.Success -> result.buildSuccessResult(stationId)
            }
        }
    }

    private fun DataRequestResult.Success<List<StationProgram>>.buildSuccessResult(
        stationId: String,
        currentResult: List<StationProgram> = emptyList(),
    ): StationProgramsUiState.Success {
        val lastCursor = data.lastOrNull()?.cursor
        return StationProgramsUiState.Success(
            programs = (currentResult + data).toImmutableList(),
            loadMorePrograms = lastCursor?.let {
                LoadMorePrograms(
                    isLoading = false,
                    onClicked = {
                        loadMorePrograms(data, lastCursor, stationId)
                    },
                )
            }
        )
    }

    private fun loadMorePrograms(
        currentResult: List<StationProgram>,
        fromCursor: String,
        stationId: String,
    ) {
        viewModelScope.launch {
            (_uiStateFlow.value as? StationProgramsUiState.Success)?.let {
                _uiStateFlow.value = it.copy(loadMorePrograms = it.loadMorePrograms?.copy(isLoading = true))
            }

            _uiStateFlow.value = when (val result = getStationProgramsUseCase.execute(stationId = stationId, fromCursor = fromCursor)) {
                is DataRequestResult.Error -> StationProgramsUiState.Error(errorMessage = result.errorMessage ?: "Unknown Error")
                is DataRequestResult.Success -> result.buildSuccessResult(stationId, currentResult)
            }
        }
    }
}