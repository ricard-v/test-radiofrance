package tech.mksoft.testradiofrance.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.data.repository.RadioStationsRepository
import tech.mksoft.testradiofrance.core.domain.model.StationProgram

class GetStationsPrograms(private val repository: RadioStationsRepository) {
    private lateinit var currentPrograms: List<StationProgram>

    suspend fun getProgramsForRadioStation(
        stationId: String,
        count: Int = 10,
        fromCursor: String? = null,
    ): Flow<DataRequestResult<List<StationProgram>>> {
        return repository.getStationPrograms(stationId, count, fromCursor).onStart {
            currentPrograms = emptyList()
        }.map { result ->
            if (result is DataRequestResult.Success) {
                currentPrograms += result.data
                result.copy(data = currentPrograms) // append result
            } else {
                result
            }
        }
    }

}