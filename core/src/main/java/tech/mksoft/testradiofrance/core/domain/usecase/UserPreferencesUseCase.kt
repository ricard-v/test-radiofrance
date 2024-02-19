package tech.mksoft.testradiofrance.core.domain.usecase

import tech.mksoft.testradiofrance.core.data.repository.UserPreferencesRepository
import tech.mksoft.testradiofrance.core.domain.model.RadioStation

class UserPreferencesUseCase(private val userPreferencesRepository: UserPreferencesRepository) {

    fun saveRadioStationAsFavorite(radioStation: RadioStation) {
        userPreferencesRepository.storePreference(value = radioStation.id, KEY_FAVORITE_STATION)
    }

    fun getFavoriteRadioStationId() : String? {
        return userPreferencesRepository.getPreference(key = KEY_FAVORITE_STATION)
    }

    private companion object {
        const val KEY_FAVORITE_STATION = "KEY_FAVORITE_STATION"
    }
}