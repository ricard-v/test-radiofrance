package tech.mksoft.testradiofrance.presentation.radiostations.model

import kotlinx.collections.immutable.ImmutableList
import tech.mksoft.testradiofrance.core.domain.model.RadioStation

sealed class RadioStationsUiState {
    data object Empty : RadioStationsUiState()
    data object Loading : RadioStationsUiState()
    data class Success(
        val stations: ImmutableList<RadioStation>,
        val onStationClicked: (RadioStation) -> Unit,
    ) : RadioStationsUiState()

    data class Error(val errorMessage: String) : RadioStationsUiState()
}