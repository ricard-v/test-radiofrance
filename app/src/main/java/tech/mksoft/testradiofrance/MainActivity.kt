package tech.mksoft.testradiofrance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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

    when (val state = uiState) {
        MainUiState.Empty -> Unit // nothing to show
        is MainUiState.LivePlayer -> {
            RadioLivePlayerBanner(
                playingStationName = state.playingStation.name,
                state = state.playerState,
                onMediaButtonClicked = state.onMediaButtonClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
            )
        }
    }
}