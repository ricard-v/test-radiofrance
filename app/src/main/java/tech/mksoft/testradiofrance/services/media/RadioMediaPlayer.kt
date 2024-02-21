package tech.mksoft.testradiofrance.services.media

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tech.mksoft.testradiofrance.core.domain.model.RadioStation
import tech.mksoft.testradiofrance.design.components.LivePlayerState

class RadioMediaPlayer(private val exoPlayer: ExoPlayer) {
    private var currentlyPlayingStation: RadioStation? = null

    private val _livePlayerFlow: MutableStateFlow<LivePlayer?> = MutableStateFlow(null)
    val livePlayerFlow = _livePlayerFlow.asStateFlow()

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_IDLE) {
                    currentlyPlayingStation?.let { station ->
                        _livePlayerFlow.value = LivePlayer(
                            radioStation = station,
                            livePlayerState = LivePlayerState.NONE,
                        )
                    }
                    currentlyPlayingStation = null
                } else if (playbackState == Player.STATE_READY) {
                    currentlyPlayingStation?.let { station ->
                        _livePlayerFlow.value = LivePlayer(
                            radioStation = station,
                            livePlayerState = LivePlayerState.PLAYING,
                        )
                    }
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                super.onPlayWhenReadyChanged(playWhenReady, reason)
                currentlyPlayingStation?.let { station ->
                    _livePlayerFlow.value = LivePlayer(
                        radioStation = station,
                        livePlayerState = if (playWhenReady) LivePlayerState.PLAYING else LivePlayerState.PAUSED,
                    )
                }
            }
        })
    }

    fun playLiveStreamFor(radioStation: RadioStation) {
        radioStation.liveStreamUrl?.let { liveStreamUrl ->
            currentlyPlayingStation = radioStation
            _livePlayerFlow.value = LivePlayer(
                radioStation = radioStation,
                livePlayerState = LivePlayerState.INITIALIZING,
            )

            exoPlayer.apply {
                val mediaItem = MediaItem.fromUri(Uri.parse(liveStreamUrl))
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }
        }
    }

    fun togglePlayPause() {
        exoPlayer.playWhenReady = !exoPlayer.isPlaying
    }

    fun stop() {
        exoPlayer.stop()
    }
}

data class LivePlayer(
    val radioStation: RadioStation,
    val livePlayerState: LivePlayerState
)