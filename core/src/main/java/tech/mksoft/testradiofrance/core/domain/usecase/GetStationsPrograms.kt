package tech.mksoft.testradiofrance.core.domain.usecase

import tech.mksoft.testradiofrance.core.data.repository.RadioStationsRepository

class GetStationsPrograms(private val repository: RadioStationsRepository) {

    suspend fun execute(stationId: String) = repository.getStationPrograms(stationId)

}