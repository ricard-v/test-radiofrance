package tech.mksoft.testradiofrance.core.data.repository

import tech.mksoft.testradiofrance.core.data.source.UserPreferencesSource

class UserPreferencesRepository(private val userPreferencesSource: UserPreferencesSource) {

    suspend fun storeStringPreference(key: String, value: String) = userPreferencesSource.storeStringValue(value, key)

    suspend fun getStringPreference(key: String) = userPreferencesSource.getStringValue(key)

    suspend fun removeStringPreference(key: String) = userPreferencesSource.removeStringKey(key)
}