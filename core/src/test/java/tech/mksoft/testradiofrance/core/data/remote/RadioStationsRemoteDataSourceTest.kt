package tech.mksoft.testradiofrance.core.data.remote

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Error
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
import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.common.graphql.getData
import tech.mksoft.testradiofrance.core.common.graphql.getError
import tech.mksoft.testradiofrance.core.domain.model.RadioStation
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class RadioStationsRemoteDataSourceTest {
    private val systemUnderTest = RadioStationsRemoteDataSource(mockedApolloClient)

    // region getAvailableStations() Tests
    @Test
    fun `GIVEN there is no network WHEN getAvailableStations() THEN returns Error`() = runTest {
        // GIVEN
        val expectedErrorMessage = "No Internet Connection"

        val mockedError: Error = mockk<Error>()
        every { mockedError.message } returns expectedErrorMessage

        val mockedApolloResponse: ApolloResponse<BrandsQuery.Data> = mockk()
        every { mockedApolloResponse.hasErrors() } returns true
        every { mockedApolloResponse.getError() } returns mockedError

        val mockedQuery: ApolloCall<BrandsQuery.Data> = mockk {
            coEvery { execute() } returns mockedApolloResponse
        }

        coEvery { mockedApolloClient.query(BrandsQuery()) } returns mockedQuery

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
        // GIVEN
        val sampleBrand = BrandsQuery.Brand(
            id = "id",
            title = "title",
            baseline = "baseline",
            description = "description",
        )

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