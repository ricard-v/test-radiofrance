package tech.mksoft.testradiofrance.presentation.radiostations

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.domain.model.RadioStation
import tech.mksoft.testradiofrance.core.domain.usecase.GetRadioStationsUseCase
import tech.mksoft.testradiofrance.core.domain.usecase.UserPreferencesUseCase
import tech.mksoft.testradiofrance.presentation.radiostations.model.RadioStationNavigation
import tech.mksoft.testradiofrance.presentation.radiostations.model.RadioStationsUiState
import tech.mksoft.testradiofrance.presentation.radiostations.model.ShowProgramsForStation
import tech.mksoft.testradiofrance.services.media.RadioMediaPlayer

class RadioStationsViewModel(
    private val getRadioStationsUseCase: GetRadioStationsUseCase,
    private val userPreferencesUseCase: UserPreferencesUseCase,
    private val radioMediaPlayer: RadioMediaPlayer,
) : ViewModel() {
    private val _uiStateFlow: MutableStateFlow<RadioStationsUiState> = MutableStateFlow(RadioStationsUiState.Empty)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _navigation: MutableSharedFlow<RadioStationNavigation> = MutableSharedFlow()
    val navigationDirection = _navigation.asSharedFlow()

    fun startFetchingRadioStations() {
        viewModelScope.launch {
            _uiStateFlow.value = RadioStationsUiState.Loading
            combine(
                getRadioStationsUseCase.getRadioStationsFlow(),
                userPreferencesUseCase.getFavoriteRadioStation(),
                transform = ::mapRadioStations,
            ).collectLatest {
                _uiStateFlow.value = it
            }
        }
    }

    private fun mapRadioStations(
        radioStationsResult: DataRequestResult<List<RadioStation>>,
        favoriteRadioStationId: String?,
    ): RadioStationsUiState {
        return when (radioStationsResult) {
            is DataRequestResult.Error -> RadioStationsUiState.Error(
                errorMessage = radioStationsResult.errorMessage ?: "Unknown Error",
                onRetryClicked = ::startFetchingRadioStations,
            )

            is DataRequestResult.Success -> RadioStationsUiState.Success(
                stations = radioStationsResult.data
                    .mapIndexed { index, radioStation ->
                        radioStation.copy(
                            position = index,
                            isFavorite = radioStation.id == favoriteRadioStationId,
                        )
                    } // mapping index to remember position as provided by BE services
                    .sortWithFavorite()
                    .toImmutableList(),
                onStationClicked = ::handleOnRadioStationClicked,
                onFavoriteButtonClicked = ::handleOnFavoriteButtonClicked,
            )
        }
    }

    private fun handleOnRadioStationClicked(radioStation: RadioStation) {
        viewModelScope.launch {
            _navigation.emit(ShowProgramsForStation(stationId = radioStation.id))
        }
    }

    private fun handleOnFavoriteButtonClicked(radioStation: RadioStation) {
        viewModelScope.launch {
            if (radioStation.isFavorite) { // is now un-favorited
                userPreferencesUseCase.unFavoriteRadioStation()
            } else { // is now favorite
                userPreferencesUseCase.setFavoriteRadioStation(radioStation)
            }
        }
    }

    fun startPlaying(radioStation: RadioStation) {
        radioMediaPlayer.playLiveStreamFor(radioStation)
    }
}

/**
 * Sorts the current list of [RadioStation] taking into account the original position of each item and ensuring the favorited one stays on top,
 * or if un-favorited, returns to its original position.
 *
 * Adding +1 when the current item is not favorited shifts the whole collection by one leaving the top position to the favorited item.
 */
private fun Iterable<RadioStation>.sortWithFavorite() = sortedBy { if (it.isFavorite) 0 else it.position + 1 }