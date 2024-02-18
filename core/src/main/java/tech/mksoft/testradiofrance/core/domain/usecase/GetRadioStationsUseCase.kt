package tech.mksoft.testradiofrance.core.domain.usecase

import tech.mksoft.testradiofrance.core.data.repository.RadioStationsRepository

class GetRadioStationsUseCase(private val repository: RadioStationsRepository) {
    suspend fun execute() = repository.getRadioStations()
}