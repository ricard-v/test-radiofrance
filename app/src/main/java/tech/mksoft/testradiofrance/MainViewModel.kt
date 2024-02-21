package tech.mksoft.testradiofrance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import tech.mksoft.testradiofrance.core.domain.model.RadioStation
import tech.mksoft.testradiofrance.design.components.LivePlayerState
import tech.mksoft.testradiofrance.design.components.LivePlayerState.INITIALIZING
import tech.mksoft.testradiofrance.design.components.LivePlayerState.PAUSED
import tech.mksoft.testradiofrance.design.components.LivePlayerState.PLAYING
import tech.mksoft.testradiofrance.design.components.LivePlayerState.READY
import tech.mksoft.testradiofrance.design.components.MediaButton
import tech.mksoft.testradiofrance.design.components.MediaButton.PLAY_PAUSE
import tech.mksoft.testradiofrance.design.components.MediaButton.STOP
import tech.mksoft.testradiofrance.services.media.RadioMediaPlayer

class MainViewModel(private val radioMediaPlayer: RadioMediaPlayer) : ViewModel() {
    private val _uiStateFlow: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState.Empty)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            radioMediaPlayer.livePlayerFlow.mapNotNull { livePlayer ->
                if (livePlayer == null) return@mapNotNull null

                when (val playerState = livePlayer.livePlayerState) {
                    INITIALIZING, READY, PLAYING, PAUSED -> MainUiState.LivePlayer(
                        playerState = playerState,
                        playingStation = livePlayer.radioStation,
                        onMediaButtonClicked = ::handleOnMediaButtonClicked,
                    )

                    else -> MainUiState.Empty
                }
            }.collectLatest {
                _uiStateFlow.value = it
            }
        }
    }

    private fun handleOnMediaButtonClicked(mediaButton: MediaButton) {
        when (mediaButton) {
            PLAY_PAUSE -> radioMediaPlayer.togglePlayPause()
            STOP -> stopPlayer()
        }
    }

    fun stopPlayer() {
        radioMediaPlayer.stop()
    }

    override fun onCleared() {
        super.onCleared()
        stopPlayer()
    }
}

sealed class MainUiState {
    data class LivePlayer(
        val playerState: LivePlayerState,
        val playingStation: RadioStation,
        val onMediaButtonClicked: (MediaButton) -> Unit,
    ) : MainUiState()

    data object Empty : MainUiState()
}