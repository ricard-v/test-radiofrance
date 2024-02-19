package tech.mksoft.testradiofrance.core.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import kotlinx.coroutines.delay
import tech.mksoft.testradiofrance.core.BrandsQuery
import tech.mksoft.testradiofrance.core.ShowsQuery
import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.common.asNotEmpty
import tech.mksoft.testradiofrance.core.common.extensions.nullIfEmpty
import tech.mksoft.testradiofrance.core.common.graphql.mapResult
import tech.mksoft.testradiofrance.core.common.graphql.safeApiCall
import tech.mksoft.testradiofrance.core.common.wrapAsSuccess
import tech.mksoft.testradiofrance.core.data.source.RadioStationsDataSource
import tech.mksoft.testradiofrance.core.domain.model.RadioStation
import tech.mksoft.testradiofrance.core.domain.model.StationProgram
import tech.mksoft.testradiofrance.core.type.StationsEnum

class RadioStationsRemoteDataSource(private val apolloClient: ApolloClient) : RadioStationsDataSource {
    override suspend fun getAvailableStations(): DataRequestResult<List<RadioStation>> {
        return safeApiCall {
            apolloClient
                .query(BrandsQuery())
                .execute()
        } mapResult { data ->
            val brands = data.brands?.asNotEmpty()
                ?: return@mapResult DataRequestResult.Error(errorMessage = "Empty Result for getAvailableStations")

            return@mapResult brands
                .mapNotNull { it?.toDomain() }
                .asNotEmpty()
                ?.wrapAsSuccess()
                ?: DataRequestResult.Error(errorMessage = "Mapping failed while getAvailableStations")
        }
    }

    override suspend fun getProgramsByStationId(stationId: String, count: Int, fromCursor: String?): DataRequestResult<List<StationProgram>> {
        return safeApiCall {
            val stationEnumValue: StationsEnum = StationsEnum.valueOf(stationId)
            apolloClient
                .query(
                    ShowsQuery(
                        station = stationEnumValue,
                        first = Optional.present(count),
                        after = Optional.presentIfNotNull(fromCursor),
                    )
                )
                .execute()
        } mapResult { data ->
            val edges = data.shows?.edges.asNotEmpty()
                ?: return@mapResult DataRequestResult.Error(errorMessage = "Empty Result for getProgramsByStationId for station id <$stationId>")

            return@mapResult edges
                .mapNotNull { it?.toDomain() }
                .asNotEmpty()
                ?.wrapAsSuccess()
                ?: DataRequestResult.Error(errorMessage = "Mapping failed while getProgramsByStationId for station id <$stationId>")
        }
    }
}

private fun BrandsQuery.Brand.toDomain(): RadioStation = RadioStation(
    id = this.id,
    name = this.title,
    pitch = this.baseline.nullIfEmpty(),
    description = this.description.nullIfEmpty(),
)

private fun ShowsQuery.Edge.toDomain(): StationProgram? {
    val node: ShowsQuery.Node = node ?: return null
    return StationProgram(
        id = node.id,
        cursor = cursor,
        title = node.title,
        description = node.standFirst,
    )
}