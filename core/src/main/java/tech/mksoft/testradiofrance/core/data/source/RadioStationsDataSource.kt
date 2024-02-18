package tech.mksoft.testradiofrance.core.data.source

import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.domain.model.RadioStation
import tech.mksoft.testradiofrance.core.domain.model.StationProgram

interface RadioStationsDataSource {
    suspend fun getAvailableStations(): DataRequestResult<List<RadioStation>>

    suspend fun getProgramsByStationId(stationId: String): DataRequestResult<List<StationProgram>>
}