package tech.mksoft.testradiofrance.presentation.radiostations

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.domain.model.RadioStation
import tech.mksoft.testradiofrance.core.domain.usecase.GetRadioStationsUseCase
import tech.mksoft.testradiofrance.core.domain.usecase.UserPreferencesUseCase
import tech.mksoft.testradiofrance.design.components.LivePlayerState
import tech.mksoft.testradiofrance.presentation.radiostations.model.RadioStationsUiState
import tech.mksoft.testradiofrance.presentation.radiostations.model.ShowProgramsForStation
import tech.mksoft.testradiofrance.presentation.tools.BaseTestClass
import tech.mksoft.testradiofrance.presentation.tools.testAndCancel
import tech.mksoft.testradiofrance.services.media.LivePlayer
import tech.mksoft.testradiofrance.services.media.RadioMediaPlayer
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class RadioStationViewModelTest : BaseTestClass() {

    private val systemUnderTest = RadioStationsViewModel(
        getRadioStationsUseCase = mockedGetRadioStationsUseCase,
        userPreferencesUseCase = mockedUserPreferencesUseCase,
        radioMediaPlayer = mockedRadioMediaPlayer,
    )

    // region UI State Tests
    @Test
    fun `GIVEN Screen is loaded for the first time WHEN initializing the RadioStationsViewModel THEN state is Empty`() = runTest {
        // GIVEN

        // WHEN
        val result = systemUnderTest.uiStateFlow.first()

        // THEN
        assertIs<RadioStationsUiState.Empty>(result)
    }

    @Test
    fun `GIVEN everything went fine WHEN startFetchingRadioStations() THEN State is Loading and THEN Success`() = runTest {
        // GIVEN
        coEvery { mockedGetRadioStationsUseCase.getRadioStationsFlow() } returns flowOf(
            DataRequestResult.Success(data = listOf(radioStationSample))
        )
        coEvery { mockedUserPreferencesUseCase.getFavoriteRadioStation() } returns flowOf(radioStationSample.id)
        coEvery { mockedRadioMediaPlayer.livePlayerFlow } returns MutableStateFlow(
            LivePlayer(
                radioStation = radioStationSample,
                livePlayerState = LivePlayerState.PLAYING,
            )
        ).asStateFlow()

        // WHEN
        systemUnderTest.startFetchingRadioStations()

        systemUnderTest.uiStateFlow.testAndCancel {
            // THEN
            assertIs<RadioStationsUiState.Empty>(awaitItem())
            assertIs<RadioStationsUiState.Loading>(awaitItem())
            assertIs<RadioStationsUiState.Success>(awaitItem()).run {
                val stationItem = stations.first()
                assertEquals(radioStationSample, stationItem.data)
                assertTrue(stationItem.isFavorite)
                assertTrue(stationItem.isPlaying)
            }
        }

        coVerify(exactly = 1) { mockedGetRadioStationsUseCase.getRadioStationsFlow() }
        coVerify(exactly = 1) { mockedUserPreferencesUseCase.getFavoriteRadioStation() }
        coVerify(exactly = 1) { mockedRadioMediaPlayer.livePlayerFlow }
    }
    // endregion UI State Tests

    // region Navigation Direction Tests
    @Test
    fun `GIVEN stations are listed WHEN user clicks on a station THEN Nav directions are given to show its programs`() = runTest {
        // GIVEN
        coEvery { mockedGetRadioStationsUseCase.getRadioStationsFlow() } returns flowOf(
            DataRequestResult.Success(data = listOf(radioStationSample))
        )
        coEvery { mockedUserPreferencesUseCase.getFavoriteRadioStation() } returns flowOf(null)
        coEvery { mockedRadioMediaPlayer.livePlayerFlow } returns MutableStateFlow(null).asStateFlow()

        systemUnderTest.startFetchingRadioStations()

        systemUnderTest.uiStateFlow.testAndCancel {
            assertIs<RadioStationsUiState.Empty>(awaitItem())
            assertIs<RadioStationsUiState.Loading>(awaitItem())
            assertIs<RadioStationsUiState.Success>(awaitItem()).run {
                val stationItem = stations.first()
                assertFalse(stationItem.isFavorite)
                assertFalse(stationItem.isPlaying)

                // WHEN
                onStationClicked.invoke(stationItem.data)
            }
        }

        systemUnderTest.navigationDirection.testAndCancel {
            assertIs<ShowProgramsForStation>(awaitItem())
        }

        coVerify(exactly = 1) { mockedGetRadioStationsUseCase.getRadioStationsFlow() }
        coVerify(exactly = 1) { mockedUserPreferencesUseCase.getFavoriteRadioStation() }
        coVerify(exactly = 1) { mockedRadioMediaPlayer.livePlayerFlow }
    }
    // endregion Navigation Direction Tests

    @After
    fun tearDown() {
        confirmVerified(mockedGetRadioStationsUseCase, mockedUserPreferencesUseCase, mockedRadioMediaPlayer)
        clearAllMocks()
    }

    companion object {
        private lateinit var mockedGetRadioStationsUseCase: GetRadioStationsUseCase
        private lateinit var mockedUserPreferencesUseCase: UserPreferencesUseCase
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
            mockedGetRadioStationsUseCase = mockk()
            mockedUserPreferencesUseCase = mockk()
            mockedRadioMediaPlayer = mockk()
        }

        @JvmStatic
        @AfterClass
        fun tearDownTestSuite() {
            unmockkAll()
        }
    }
}

