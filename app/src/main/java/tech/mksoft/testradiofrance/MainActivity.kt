package tech.mksoft.testradiofrance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerResource
import org.koin.androidx.compose.koinViewModel
import tech.mksoft.testradiofrance.design.components.RadioLivePlayerBanner
import tech.mksoft.testradiofrance.design.theme.TestRadioFranceTheme
import tech.mksoft.testradiofrance.navigation.SetupAppNavigation

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestRadioFranceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        SetupAppNavigation()
                        LivePlayerUi()
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopPlayer()
    }
}

@Composable
private fun BoxScope.LivePlayerUi() {
    val viewModel = koinViewModel<MainViewModel>()
    val uiState by viewModel.uiStateFlow.collectAsState()


    val animationDuration = integerResource(id = android.R.integer.config_mediumAnimTime)
    AnimatedVisibility(
        visible = uiState?.showPlayerBanner == true,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(durationMillis = animationDuration)),
        exit = slideOutVertically(targetOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(durationMillis = animationDuration)),
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
    ) {
        val livePlayer = uiState ?: return@AnimatedVisibility

        RadioLivePlayerBanner(
            playingStationName = livePlayer.playingStation.name,
            state = livePlayer.playerState,
            onMediaButtonClicked = livePlayer.onMediaButtonClicked,
        )
    }
}