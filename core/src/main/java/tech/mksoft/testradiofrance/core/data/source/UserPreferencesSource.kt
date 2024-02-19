package tech.mksoft.testradiofrance.core.data.source

interface UserPreferencesSource {
    fun <T> storeValue(value: T, key: String)

    fun <T> getValue(key: String, defaultValue: T?): T?
}