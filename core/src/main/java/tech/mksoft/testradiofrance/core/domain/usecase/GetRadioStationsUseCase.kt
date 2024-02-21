package tech.mksoft.testradiofrance.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.data.repository.RadioStationsRepository
import tech.mksoft.testradiofrance.core.domain.model.RadioStation

class GetRadioStationsUseCase(private val repository: RadioStationsRepository) {
    suspend fun getRadioStationsFlow(): Flow<DataRequestResult<List<RadioStation>>> = repository.getRadioStations()
}