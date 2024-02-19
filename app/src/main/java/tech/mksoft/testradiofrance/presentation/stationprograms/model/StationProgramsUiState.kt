package tech.mksoft.testradiofrance.presentation.stationprograms.model

import kotlinx.collections.immutable.ImmutableList
import tech.mksoft.testradiofrance.core.domain.model.StationProgram

sealed class StationProgramsUiState {
    data object Empty : StationProgramsUiState()
    data object Loading : StationProgramsUiState()
    data class Error(val errorMessage: String) : StationProgramsUiState()
    data class Success(
        val programs: ImmutableList<StationProgram>,
        val loadMorePrograms: LoadMorePrograms?,
    ) : StationProgramsUiState()
}

data class LoadMorePrograms(val isLoading: Boolean, val onClicked: () -> Unit)