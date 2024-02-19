package tech.mksoft.testradiofrance.core.domain.usecase

import tech.mksoft.testradiofrance.core.data.repository.RadioStationsRepository

class GetStationsPrograms(private val repository: RadioStationsRepository) {

    suspend fun execute(stationId: String, count: Int = 10, fromCursor: String? = null) =
        repository.getStationPrograms(stationId, count, fromCursor)

}