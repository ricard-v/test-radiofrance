package tech.mksoft.testradiofrance.core.domain.usecase

import tech.mksoft.testradiofrance.core.data.repository.UserPreferencesRepository
import tech.mksoft.testradiofrance.core.domain.model.RadioStation

class UserPreferencesUseCase(private val userPreferencesRepository: UserPreferencesRepository) {

    suspend fun setFavoriteRadioStation(radioStation: RadioStation) {
        setFavoriteRadioStation(stationId = radioStation.id)
    }

    suspend fun setFavoriteRadioStation(stationId: String) {
        userPreferencesRepository.storeStringPreference(key = KEY_FAVORITE_STATION, value = stationId)
    }

    suspend fun getFavoriteRadioStation() = userPreferencesRepository.getStringPreference(key = KEY_FAVORITE_STATION)

    suspend fun unFavoriteRadioStation() {
        userPreferencesRepository.removeStringPreference(key = KEY_FAVORITE_STATION)
    }

    private companion object {
        const val KEY_FAVORITE_STATION = "KEY_FAVORITE_STATION"
    }
}