package tech.mksoft.testradiofrance.core.data.repository

import tech.mksoft.testradiofrance.core.data.source.UserPreferencesSource

class UserPreferencesRepository(private val userPreferencesSource: UserPreferencesSource) {
    fun <T> storePreference(value: T, key: String) = userPreferencesSource.storeValue(value, key)

    fun <T> getPreference(key: String, defaultValue: T? = null) = userPreferencesSource.getValue(key, defaultValue)
}