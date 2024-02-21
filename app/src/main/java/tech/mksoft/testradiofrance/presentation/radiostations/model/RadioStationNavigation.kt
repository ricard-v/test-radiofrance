package tech.mksoft.testradiofrance.presentation.radiostations.model

sealed class RadioStationNavigation

data class ShowProgramsForStation(val stationId: String) : RadioStationNavigation()