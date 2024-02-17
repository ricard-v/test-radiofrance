package tech.mksoft.testradiofrance.presentation.radiostations

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.domain.GetRadioStationsUseCase

class RadioStationsViewModel(
    private val getRadioStationsUseCase: GetRadioStationsUseCase,
) : ViewModel() {
    private val _uiStateFlow: MutableStateFlow<RadioStationsState> = MutableStateFlow(RadioStationsState.Empty)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun fetchRadioStations() {
        viewModelScope.launch {
            _uiStateFlow.value = RadioStationsState.Loading
            _uiStateFlow.value = when (val result = getRadioStationsUseCase.execute()) {
                is DataRequestResult.Error -> RadioStationsState.Error(errorMessage = result.errorMessage ?: "Unknown Error")
                is DataRequestResult.Success -> RadioStationsState.Success(stations = result.data.toImmutableList())
            }
        }
    }
}