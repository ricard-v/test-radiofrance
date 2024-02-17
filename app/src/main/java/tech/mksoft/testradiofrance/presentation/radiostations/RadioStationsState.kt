package tech.mksoft.testradiofrance.presentation.radiostations

import kotlinx.collections.immutable.ImmutableList
import tech.mksoft.testradiofrance.core.domain.model.RadioStation

sealed class RadioStationsState {
    data object Empty : RadioStationsState()
    data object Loading: RadioStationsState()
    data class Success(val stations: ImmutableList<RadioStation>): RadioStationsState()
    data class Error(val errorMessage: String): RadioStationsState()
}