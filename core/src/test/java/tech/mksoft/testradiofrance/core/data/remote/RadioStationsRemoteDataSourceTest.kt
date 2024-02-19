package tech.mksoft.testradiofrance.core.data.remote

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloHttpException
import com.apollographql.apollo3.exception.ApolloNetworkException
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import tech.mksoft.testradiofrance.core.BrandsQuery
import tech.mksoft.testradiofrance.core.ShowsQuery
import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.common.graphql.getData
import tech.mksoft.testradiofrance.core.domain.model.RadioStation
import tech.mksoft.testradiofrance.core.domain.model.StationProgram
import tech.mksoft.testradiofrance.core.type.StationsEnum
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class RadioStationsRemoteDataSourceTest {
    private val systemUnderTest = RadioStationsRemoteDataSource(mockedApolloClient)

    // region getAvailableStations() Tests
    @Test
    fun `GIVEN request failed WHEN getAvailableStations() THEN returns Error`() = runTest {
        val expectedErrorMessage = "Internal Error"

        // GIVEN
        coEvery { mockedApolloClient.query(BrandsQuery()) } returns mockk {
            coEvery { execute() } throws ApolloHttpException(
                message = expectedErrorMessage,
                statusCode = 500,
                headers = emptyList(),
                body = null,
            )
        }

        // WHEN
        val result = systemUnderTest.getAvailableStations()

        // THEN
        assertIs<DataRequestResult.Error>(result).run {
            assertNotNull(errorMessage)
            assertEquals(expectedErrorMessage, errorMessage)
        }

        coVerify(exactly = 1) { mockedApolloClient.query(BrandsQuery()) }
    }

    @Test
    fun `GIVEN there is internet access WHEN getAvailableStations() THEN returns Error`() = runTest {
        val expectedErrorMessage = "Missing internet access"

        // GIVEN
        coEvery { mockedApolloClient.query(BrandsQuery()) } returns mockk {
            coEvery { execute() } throws ApolloNetworkException(message = expectedErrorMessage)
        }

        // WHEN
        val result = systemUnderTest.getAvailableStations()

        // THEN
        assertIs<DataRequestResult.Error>(result).run {
            assertNotNull(errorMessage)
            assertEquals(expectedErrorMessage, errorMessage)
        }

        coVerify(exactly = 1) { mockedApolloClient.query(BrandsQuery()) }
    }

    @Test
    fun `GIVEN the request is successful WHEN getAvailableStations() THEN returns Success`() = runTest {
        val sampleBrand = BrandsQuery.Brand(
            id = "id",
            title = "title",
            baseline = "baseline",
            description = "description",
        )

        // GIVEN
        val mockedApolloResponse: ApolloResponse<BrandsQuery.Data> = mockk()
        every { mockedApolloResponse.hasErrors() } returns false
        every { mockedApolloResponse.getData() } returns BrandsQuery.Data(
            brands = listOf(sampleBrand)
        )

        val mockedQuery: ApolloCall<BrandsQuery.Data> = mockk {
            coEvery { execute() } returns mockedApolloResponse
        }

        coEvery { mockedApolloClient.query(BrandsQuery()) } returns mockedQuery

        // WHEN
        val result = systemUnderTest.getAvailableStations()

        // THEN
        assertIs<DataRequestResult.Success<List<RadioStation>>>(result).run {
            val station = data.first()
            assertEquals(sampleBrand.id, station.id)
            assertEquals(sampleBrand.title, station.name)
            assertEquals(sampleBrand.baseline, station.pitch)
            assertEquals(sampleBrand.description, station.description)
        }

        coVerify(exactly = 1) { mockedApolloClient.query(BrandsQuery()) }
    }
    // endregion getAvailableStations() Tests

    // region getProgramsByStationId() Tests
    @Test
    fun `GIVEN request failed WHEN getProgramsByStationId() THEN returns Error`() = runTest {
        val stationId = "FRANCEINFO"
        val expectedErrorMessage = "Internal Error"

        // GIVEN
        coEvery {
            mockedApolloClient.query(
                ShowsQuery(
                    station = StationsEnum.FRANCEINFO,
                    first = Optional.present(10),
                    after = Optional.presentIfNotNull(null),
                ),
            )
        } returns mockk {
            coEvery { execute() } throws ApolloHttpException(
                message = expectedErrorMessage,
                statusCode = 500,
                headers = emptyList(),
                body = null,
            )
        }

        // WHEN
        val result = systemUnderTest.getProgramsByStationId(stationId = stationId, count = 10, fromCursor = null)

        // THEN
        assertIs<DataRequestResult.Error>(result).run {
            assertNotNull(errorMessage)
            assertEquals(expectedErrorMessage, errorMessage)
        }

        coVerify(exactly = 1) {
            mockedApolloClient.query(
                ShowsQuery(
                    station = StationsEnum.FRANCEINFO,
                    first = Optional.present(10),
                    after = Optional.presentIfNotNull(null),
                )
            )
        }
    }

    @Test
    fun `GIVEN there is internet access WHEN getProgramsByStationId() THEN returns Error`() = runTest {
        val stationId = StationsEnum.FRANCEINFO.name
        val expectedErrorMessage = "Missing internet access"

        // GIVEN
        coEvery {
            mockedApolloClient.query(
                ShowsQuery(
                    station = StationsEnum.FRANCEINFO,
                    first = Optional.present(10),
                    after = Optional.presentIfNotNull(null),
                ),
            )
        } returns mockk {
            coEvery { execute() } throws ApolloNetworkException(message = expectedErrorMessage)
        }

        // WHEN
        val result = systemUnderTest.getProgramsByStationId(stationId = stationId, count = 10, fromCursor = null)

        // THEN
        assertIs<DataRequestResult.Error>(result).run {
            assertNotNull(errorMessage)
            assertEquals(expectedErrorMessage, errorMessage)
        }

        coVerify(exactly = 1) {
            mockedApolloClient.query(
                ShowsQuery(
                    station = StationsEnum.FRANCEINFO,
                    first = Optional.present(10),
                    after = Optional.presentIfNotNull(null),
                )
            )
        }
    }

    @Test
    fun `GIVEN the request is successful WHEN getProgramsByStationId() THEN returns Success`() = runTest {
        val stationId = StationsEnum.FRANCEINFO.name
        val sampleEdges = listOf(
            ShowsQuery.Edge(
                cursor = "cursor_1",
                node = ShowsQuery.Node(
                    id = "id_1",
                    title = "title_1",
                    standFirst = "standfirst_1",
                )
            ),
            ShowsQuery.Edge(
                cursor = "cursor_2",
                node = ShowsQuery.Node(
                    id = "id_2",
                    title = "title_2",
                    standFirst = "null",
                )
            )
        )

        // GIVEN
        val mockedApolloResponse: ApolloResponse<ShowsQuery.Data> = mockk()
        every { mockedApolloResponse.hasErrors() } returns false
        every { mockedApolloResponse.getData() } returns ShowsQuery.Data(
            shows = ShowsQuery.Shows(edges = sampleEdges)
        )

        val mockedQuery: ApolloCall<ShowsQuery.Data> = mockk {
            coEvery { execute() } returns mockedApolloResponse
        }

        coEvery {
            mockedApolloClient.query(
                ShowsQuery(
                    station = StationsEnum.FRANCEINFO,
                    first = Optional.present(10),
                    after = Optional.presentIfNotNull(null),
                ),
            )
        } returns mockedQuery

        // WHEN
        val result = systemUnderTest.getProgramsByStationId(stationId = stationId, count = 10, fromCursor = null)

        // THEN
        assertIs<DataRequestResult.Success<List<StationProgram>>>(result).run {
            assertEquals(sampleEdges.size, data.size)
            sampleEdges.forEachIndexed { index, edge ->
                val actual = data[index]
                assertEquals(edge.cursor, actual.cursor)
                assertEquals(edge.node!!.id, actual.id)
                assertEquals(edge.node!!.title, actual.title)
                assertEquals(edge.node!!.standFirst, actual.description)
            }
        }

        coVerify(exactly = 1) {
            mockedApolloClient.query(
                ShowsQuery(
                    station = StationsEnum.FRANCEINFO,
                    first = Optional.present(10),
                    after = Optional.presentIfNotNull(null),
                )
            )
        }
    }

    @Test
    fun `GIVEN the request is successful WHEN requesting more programs THEN returns Success`() = runTest {
        val stationId = StationsEnum.FRANCEINFO.name
        val sampleEdges = listOf(
            ShowsQuery.Edge(
                cursor = "cursor_1",
                node = ShowsQuery.Node(
                    id = "id_1",
                    title = "title_1",
                    standFirst = "standfirst_1",
                )
            ),
            ShowsQuery.Edge(
                cursor = "cursor_2",
                node = ShowsQuery.Node(
                    id = "id_2",
                    title = "title_2",
                    standFirst = "null",
                )
            )
        )

        // GIVEN
        val mockedApolloResponse: ApolloResponse<ShowsQuery.Data> = mockk()
        every { mockedApolloResponse.hasErrors() } returns false
        every { mockedApolloResponse.getData() } returnsMany listOf(
            ShowsQuery.Data(
                shows = ShowsQuery.Shows(edges = sampleEdges.take(1))
            ),
            ShowsQuery.Data(
                shows = ShowsQuery.Shows(edges = sampleEdges.subList(1, 2))
            ),
        )

        val mockedQuery: ApolloCall<ShowsQuery.Data> = mockk {
            coEvery { execute() } returns mockedApolloResponse
        }

        coEvery {
            mockedApolloClient.query(
                ShowsQuery(
                    station = StationsEnum.FRANCEINFO,
                    first = Optional.present(1),
                    after = Optional.presentIfNotNull(null),
                ),
            )
        } returns mockedQuery

        coEvery {
            mockedApolloClient.query(
                ShowsQuery(
                    station = StationsEnum.FRANCEINFO,
                    first = Optional.present(1),
                    after = Optional.presentIfNotNull(sampleEdges[1].cursor),
                ),
            )
        } returns mockedQuery

        systemUnderTest.getProgramsByStationId(stationId = stationId, count = 1, fromCursor = null)

        // WHEN
        val loadMoreResult = systemUnderTest.getProgramsByStationId(stationId = stationId, count = 1, fromCursor = sampleEdges[1].cursor)

        // THEN
        assertIs<DataRequestResult.Success<List<StationProgram>>>(loadMoreResult).run {
            val expected = sampleEdges[1]
            val actual = data[0]
            assertEquals(expected.cursor, actual.cursor)
            assertEquals(expected.node!!.id, actual.id)
            assertEquals(expected.node!!.title, actual.title)
            assertEquals(expected.node!!.standFirst, actual.description)
        }

        coVerify(exactly = 1) {
            mockedApolloClient.query(
                ShowsQuery(
                    station = StationsEnum.FRANCEINFO,
                    first = Optional.present(1),
                    after = Optional.presentIfNotNull(null),
                )
            )
        }

        coVerify(exactly = 1) {
            mockedApolloClient.query(
                ShowsQuery(
                    station = StationsEnum.FRANCEINFO,
                    first = Optional.present(1),
                    after = Optional.presentIfNotNull(sampleEdges[1].cursor),
                )
            )
        }
    }
    // endregion getProgramsByStationId() Tests

    @After
    fun tearDown() {
        confirmVerified(mockedApolloClient)
        clearAllMocks()
    }

    companion object {
        private lateinit var mockedApolloClient: ApolloClient

        @JvmStatic
        @BeforeClass
        fun setUpTestSuite() {
            mockkStatic("tech.mksoft.testradiofrance.core.common.graphql.SafeApolloRequestKt")
            mockedApolloClient = mockk()
        }

        @JvmStatic
        @AfterClass
        fun tearDownTestSuite() {
            unmockkAll()
        }
    }
}