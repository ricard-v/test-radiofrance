package tech.mksoft.testradiofrance.presentation.stationprograms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.domain.usecase.GetStationsPrograms
import tech.mksoft.testradiofrance.presentation.stationprograms.model.StationProgramsUiState

class StationProgramsViewModel(private val getStationProgramsUseCase: GetStationsPrograms): ViewModel() {
    private val _uiStateFlow: MutableStateFlow<StationProgramsUiState> = MutableStateFlow(StationProgramsUiState.Empty)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun fetchProgramsForStation(stationId: String) {
        viewModelScope.launch {
            _uiStateFlow.value = StationProgramsUiState.Loading
            _uiStateFlow.value = when (val result = getStationProgramsUseCase.execute(stationId)) {
                is DataRequestResult.Error -> StationProgramsUiState.Error(errorMessage = result.errorMessage ?: "Unknown Error")
                is DataRequestResult.Success -> StationProgramsUiState.Success(programs = result.data.toImmutableList())
            }
        }
    }
}