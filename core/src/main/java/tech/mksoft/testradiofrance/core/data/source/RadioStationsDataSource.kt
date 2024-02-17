package tech.mksoft.testradiofrance.core.data.source

import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.domain.model.RadioStation

interface RadioStationsDataSource {
    suspend fun getAvailableStations(): DataRequestResult<List<RadioStation>>
}