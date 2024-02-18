package tech.mksoft.testradiofrance.core.data.repository

import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.data.source.RadioStationsDataSource
import tech.mksoft.testradiofrance.core.domain.model.RadioStation

class RadioStationsRepository(private val dataSource: RadioStationsDataSource) {
    suspend fun getRadioStations(): DataRequestResult<List<RadioStation>> = dataSource.getAvailableStations()
}