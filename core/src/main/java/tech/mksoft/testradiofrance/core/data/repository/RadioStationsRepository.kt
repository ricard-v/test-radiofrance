package tech.mksoft.testradiofrance.core.data.repository

import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.data.source.RadioStationsDataSource
import tech.mksoft.testradiofrance.core.domain.model.RadioStation
import tech.mksoft.testradiofrance.core.domain.model.StationProgram

class RadioStationsRepository(private val dataSource: RadioStationsDataSource) {
    suspend fun getRadioStations(): DataRequestResult<List<RadioStation>> = dataSource.getAvailableStations()

    suspend fun getStationPrograms(stationId: String, count: Int, fromCursor: String?): DataRequestResult<List<StationProgram>> =
        dataSource.getProgramsByStationId(stationId, count, fromCursor)
}