package tech.mksoft.testradiofrance.core.data.source

import kotlinx.coroutines.flow.Flow
import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.domain.model.RadioStation
import tech.mksoft.testradiofrance.core.domain.model.StationProgram

interface RadioStationsDataSource {
    suspend fun getAvailableStations(): Flow<DataRequestResult<List<RadioStation>>>

    suspend fun getProgramsByStationId(stationId: String, count: Int, fromCursor: String?): Flow<DataRequestResult<List<StationProgram>>>
}