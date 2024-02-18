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
import tech.mksoft.testradiofrance.core.domain.usecase.GetRadioStationsUseCase
import tech.mksoft.testradiofrance.core.domain.model.RadioStation
import tech.mksoft.testradiofrance.presentation.radiostations.model.RadioStationNavigation
import tech.mksoft.testradiofrance.presentation.radiostations.model.RadioStationsUiState
import tech.mksoft.testradiofrance.presentation.radiostations.model.ShowProgramsForStation

class RadioStationsViewModel(
    private val getRadioStationsUseCase: GetRadioStationsUseCase,
) : ViewModel() {
    private val _uiStateFlow: MutableStateFlow<RadioStationsUiState> = MutableStateFlow(RadioStationsUiState.Empty)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _navigation: MutableSharedFlow<RadioStationNavigation> = MutableSharedFlow()
    val navigationDirection = _navigation.asSharedFlow()

    fun fetchRadioStations() {
        viewModelScope.launch {
            _uiStateFlow.value = RadioStationsUiState.Loading
            _uiStateFlow.value = when (val result = getRadioStationsUseCase.execute()) {
                is DataRequestResult.Error -> RadioStationsUiState.Error(errorMessage = result.errorMessage ?: "Unknown Error")
                is DataRequestResult.Success -> RadioStationsUiState.Success(
                    stations = result.data.toImmutableList(),
                    onStationClicked = ::handleOnRadioStationClicked,
                )
            }
        }
    }

    private fun handleOnRadioStationClicked(radioStation: RadioStation) {
        viewModelScope.launch {
            _navigation.emit(ShowProgramsForStation(stationId = radioStation.id))
        }
    }
}