package tech.mksoft.testradiofrance.presentation

import io.mockk.clearAllMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import tech.mksoft.testradiofrance.MainUiState
import tech.mksoft.testradiofrance.MainViewModel
import tech.mksoft.testradiofrance.core.domain.model.RadioStation
import tech.mksoft.testradiofrance.design.components.LivePlayerState
import tech.mksoft.testradiofrance.design.components.MediaButton
import tech.mksoft.testradiofrance.presentation.tools.BaseTestClass
import tech.mksoft.testradiofrance.presentation.tools.testAndCancel
import tech.mksoft.testradiofrance.services.media.LivePlayer
import tech.mksoft.testradiofrance.services.media.RadioMediaPlayer
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class MainViewModelTest : BaseTestClass() {

    private lateinit var systemUnderTest: MainViewModel

    @Test
    fun `GIVEN there is radio being live streamed WHEN user starts live streaming a radio THEN main ui state is LivePlayer at different states`() =
        runTest {
            // GIVEN
            val inernalLivePlayerFlow: MutableStateFlow<LivePlayer?> = MutableStateFlow(null)
            every { mockedRadioMediaPlayer.livePlayerFlow } returns inernalLivePlayerFlow.asStateFlow()
            every { mockedRadioMediaPlayer.stop() } answers {
                inernalLivePlayerFlow.value = createLivePlayerInstanceWithState(state = LivePlayerState.NONE)
            }

            systemUnderTest = MainViewModel(radioMediaPlayer = mockedRadioMediaPlayer)

            systemUnderTest.uiStateFlow.testAndCancel {
                awaitItem() // initial

                // WHEN (step 1: user clicks play on a radio station)
                val initializingLivePlayer = createLivePlayerInstanceWithState(state = LivePlayerState.INITIALIZING)
                inernalLivePlayerFlow.value = initializingLivePlayer
                // THEN (step 1)
                assertIs<MainUiState>(awaitItem()).run {
                    assertEquals(initializingLivePlayer.radioStation, playingStation)
                    assertTrue(showPlayerBanner)
                }

                // WHEN (step 2: player is ready)
                val readyLivePlayer = createLivePlayerInstanceWithState(state = LivePlayerState.READY)
                inernalLivePlayerFlow.value = readyLivePlayer
                // THEN (step 2)
                assertIs<MainUiState>(awaitItem()).run {
                    assertEquals(readyLivePlayer.radioStation, playingStation)
                    assertTrue(showPlayerBanner)

                    // WHEN (step 3: player is stopped  by the user)
                    onMediaButtonClicked.invoke(MediaButton.STOP)
                }

                // THEN (step 3)
                assertIs<MainUiState>(awaitItem()).run {
                    assertEquals(LivePlayerState.NONE, playerState)
                    assertFalse(showPlayerBanner)
                }
            }

            verify(exactly = 1) { mockedRadioMediaPlayer.livePlayerFlow }
            verify(exactly = 1) { mockedRadioMediaPlayer.stop() }
        }

    private fun createLivePlayerInstanceWithState(state: LivePlayerState) = LivePlayer(
        radioStation = radioStationSample,
        livePlayerState = state,
    )

    @After
    fun tearDown() {
        confirmVerified(mockedRadioMediaPlayer)
        clearAllMocks()
    }

    companion object {
        private lateinit var mockedRadioMediaPlayer: RadioMediaPlayer

        private val radioStationSample = RadioStation(
            id = "id",
            name = "France Info",
            pitch = "La meilleure radio au monde ?",
            description = "Il faut écouter pour y croire ;-)",
            liveStreamUrl = "https://icecast.radiofrance.fr/franceinter-midfi.mp3?id=openapi",
        )

        @JvmStatic
        @BeforeClass
        fun setUpTestSuite() {
            mockedRadioMediaPlayer = mockk()
        }

        @JvmStatic
        @AfterClass
        fun tearDownTestSuite() {
            unmockkAll()
        }
    }
}