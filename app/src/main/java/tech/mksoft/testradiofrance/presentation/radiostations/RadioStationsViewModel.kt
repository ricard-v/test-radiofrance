package tech.mksoft.testradiofrance.presentation.radiostations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.domain.model.RadioStation
import tech.mksoft.testradiofrance.core.domain.usecase.GetRadioStationsUseCase
import tech.mksoft.testradiofrance.core.domain.usecase.UserPreferencesUseCase
import tech.mksoft.testradiofrance.presentation.radiostations.model.RadioStationNavigation
import tech.mksoft.testradiofrance.presentation.radiostations.model.RadioStationsUiState
import tech.mksoft.testradiofrance.presentation.radiostations.model.ShowProgramsForStation

class RadioStationsViewModel(
    private val getRadioStationsUseCase: GetRadioStationsUseCase,
    private val userPreferencesUseCase: UserPreferencesUseCase,
) : ViewModel() {
    private val _uiStateFlow: MutableStateFlow<RadioStationsUiState> = MutableStateFlow(RadioStationsUiState.Empty)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _navigation: MutableSharedFlow<RadioStationNavigation> = MutableSharedFlow()
    val navigationDirection = _navigation.asSharedFlow()

    private var positionBeforeFavorited: Int? = null

    fun fetchRadioStations() {
        viewModelScope.launch {
            _uiStateFlow.value = RadioStationsUiState.Loading
            _uiStateFlow.value = when (val result = getRadioStationsUseCase.execute()) {
                is DataRequestResult.Error -> RadioStationsUiState.Error(errorMessage = result.errorMessage ?: "Unknown Error")
                is DataRequestResult.Success -> RadioStationsUiState.Success(
                    stations = result.data
                        .mapIndexed { index, radioStation -> radioStation.copy(position = index) } // mapping index to remember position as provided by BE services
                        .sortWithFavorite()
                        .toImmutableList(),
                    onStationClicked = ::handleOnRadioStationClicked,
                    onFavoriteButtonClicked = ::handleOnFavoriteButtonClicked,
                )
            }
        }
    }

    private fun handleOnRadioStationClicked(radioStation: RadioStation) {
        viewModelScope.launch {
            _navigation.emit(ShowProgramsForStation(stationId = radioStation.id))
        }
    }

    private fun handleOnFavoriteButtonClicked(radioStation: RadioStation) {
        viewModelScope.launch {
            userPreferencesUseCase.saveRadioStationAsFavorite(radioStation)
            (_uiStateFlow.value as? RadioStationsUiState.Success)?.let { currentState ->
                _uiStateFlow.value = currentState.copy(
                    stations = currentState.stations.map { currentRadioStation ->
                        if (currentRadioStation === radioStation) {
                            currentRadioStation.copy(isFavorite = !radioStation.isFavorite)
                        } else {
                            currentRadioStation.copy(isFavorite = false)
                        }
                    }.sortWithFavorite().toImmutableList(),
                )
            }
        }
    }
}

/**
 * Sorts the current list of [RadioStation] taking into account the original position of each item and ensuring the favorited one stays on top,
 * or if un-favorited, returns to its original position.
 *
 * Adding +1 when the current item is not favorited shifts the whole collection by one leaving the top position to the favorited item.
 */
private fun Iterable<RadioStation>.sortWithFavorite() = sortedBy { if (it.isFavorite) 0 else it.position + 1 }