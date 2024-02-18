package tech.mksoft.testradiofrance.presentation.radiostations

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.domain.GetRadioStationsUseCase
import tech.mksoft.testradiofrance.core.domain.model.RadioStation
import tech.mksoft.testradiofrance.presentation.radiostations.model.RadioStationsUiState
import tech.mksoft.testradiofrance.presentation.radiostations.model.ShowProgramsForStation
import tech.mksoft.testradiofrance.presentation.tools.BaseTestClass
import tech.mksoft.testradiofrance.presentation.tools.testAndCancel
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RadioStationViewModelTest: BaseTestClass() {

    private val systemUnderTest = RadioStationsViewModel(mockedGetRadioStationsUseCase)

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
    fun `GIVEN everything went fine WHEN fetchRadioStations() THEN State is Loading and THEN Success`() = runTest {
        // GIVEN
        coEvery { mockedGetRadioStationsUseCase.execute() } returns DataRequestResult.Success(data = listOf(radioStationSample))

        // WHEN
        systemUnderTest.fetchRadioStations()

        systemUnderTest.uiStateFlow.testAndCancel {
            // THEN
            assertIs<RadioStationsUiState.Empty>(awaitItem())
            assertIs<RadioStationsUiState.Loading>(awaitItem())
            assertIs<RadioStationsUiState.Success>(awaitItem()).run {
                assertEquals(radioStationSample, stations.first())
            }
        }

        coVerify(exactly = 1) { mockedGetRadioStationsUseCase.execute() }
    }
    // endregion UI State Tests

    // region Navigation Direction Tests
    @Test
    fun `GIVEN stations are listed WHEN user clicks on a station THEN Nav directions are given to show its programs`() = runTest {
        // GIVEN
        coEvery { mockedGetRadioStationsUseCase.execute() } returns DataRequestResult.Success(data = listOf(radioStationSample))
        systemUnderTest.fetchRadioStations()

        systemUnderTest.uiStateFlow.testAndCancel {
            assertIs<RadioStationsUiState.Empty>(awaitItem())
            assertIs<RadioStationsUiState.Loading>(awaitItem())
            assertIs<RadioStationsUiState.Success>(awaitItem()).run {
                assertEquals(radioStationSample, stations.first())
                // WHEN
                onStationClicked.invoke(stations.first())
            }
        }

        systemUnderTest.navigationDirection.testAndCancel {
            assertIs<ShowProgramsForStation>(awaitItem())
        }

        coVerify(exactly = 1) { mockedGetRadioStationsUseCase.execute() }
    }
    // endregion Navigation Direction Tests

    @After
    fun tearDown() {
        confirmVerified(mockedGetRadioStationsUseCase)
        clearAllMocks()
    }

    companion object {
        private lateinit var mockedGetRadioStationsUseCase: GetRadioStationsUseCase

        private val radioStationSample = RadioStation(
            id = "id",
            name = "France Info",
            pitch = "La meilleure radio au monde ?",
            description = "Il faut écouter pour y croire ;-)"
        )

        @JvmStatic
        @BeforeClass
        fun setUpTestSuite() {
            mockedGetRadioStationsUseCase = mockk()
        }

        @JvmStatic
        @AfterClass
        fun tearDownTestSuite() {
            unmockkAll()
        }
    }
}

