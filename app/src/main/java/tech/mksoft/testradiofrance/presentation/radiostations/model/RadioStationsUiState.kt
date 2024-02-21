package tech.mksoft.testradiofrance.presentation.radiostations.model

import kotlinx.collections.immutable.ImmutableList
import tech.mksoft.testradiofrance.core.domain.model.RadioStation

sealed class RadioStationsUiState {
    data object Empty : RadioStationsUiState()
    data object Loading : RadioStationsUiState()
    data class Success(
        val stations: ImmutableList<RadioStationUi>,
        val onStationClicked: (RadioStation) -> Unit,
        val onFavoriteButtonClicked: (RadioStationUi) -> Unit,
    ) : RadioStationsUiState()

    data class Error(val errorMessage: String, val onRetryClicked: () -> Unit) : RadioStationsUiState()
}