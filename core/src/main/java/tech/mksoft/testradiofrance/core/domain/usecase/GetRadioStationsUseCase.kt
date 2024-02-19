package tech.mksoft.testradiofrance.core.domain.usecase

import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.common.wrapAsSuccess
import tech.mksoft.testradiofrance.core.data.repository.RadioStationsRepository
import tech.mksoft.testradiofrance.core.domain.model.RadioStation

class GetRadioStationsUseCase(
    private val repository: RadioStationsRepository,
    private val userPreferencesUseCase: UserPreferencesUseCase,
) {
    suspend fun execute(): DataRequestResult<List<RadioStation>> {
        val result = repository.getRadioStations()
        val favoriteStationId = userPreferencesUseCase.getFavoriteRadioStationId() ?: return result

        return if (result is DataRequestResult.Success<List<RadioStation>>) {
            result.data.map {
                it.copy(isFavorite = favoriteStationId == it.id)
            }.wrapAsSuccess()
        } else {
            result
        }
    }
}