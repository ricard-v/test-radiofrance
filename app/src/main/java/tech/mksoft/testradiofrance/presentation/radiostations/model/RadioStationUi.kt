package tech.mksoft.testradiofrance.presentation.radiostations.model

import tech.mksoft.testradiofrance.core.domain.model.RadioStation

data class RadioStationUi(
    val data: RadioStation,
    val isFavorite: Boolean,
    val position: Int,
    val isPlaying: Boolean,
    val onPlayLiveStreamClicked: ((RadioStationUi) -> Unit)?
)
