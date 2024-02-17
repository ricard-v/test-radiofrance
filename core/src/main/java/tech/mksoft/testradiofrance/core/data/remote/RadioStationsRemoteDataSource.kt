package tech.mksoft.testradiofrance.core.data.remote

import com.apollographql.apollo3.ApolloClient
import tech.mksoft.testradiofrance.core.BrandsQuery
import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.common.asNotEmpty
import tech.mksoft.testradiofrance.core.common.graphql.safeApiCall
import tech.mksoft.testradiofrance.core.common.graphql.mapResult
import tech.mksoft.testradiofrance.core.common.toSuccess
import tech.mksoft.testradiofrance.core.data.source.RadioStationsDataSource
import tech.mksoft.testradiofrance.core.domain.model.RadioStation

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
                ?.toSuccess()
                ?: DataRequestResult.Error(errorMessage = "Mapping failed while getAvailableStations")
        }
    }
}

private fun BrandsQuery.Brand.toDomain(): RadioStation = RadioStation(
    id = this.id,
    name = this.title,
    description = this.description.orEmpty(),
)