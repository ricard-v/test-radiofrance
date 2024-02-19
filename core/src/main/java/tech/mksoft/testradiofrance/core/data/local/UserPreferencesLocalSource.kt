package tech.mksoft.testradiofrance.core.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import tech.mksoft.testradiofrance.core.data.source.UserPreferencesSource
import kotlin.reflect.KClass

class UserPreferencesLocalSource(private val sharedPreferences: SharedPreferences): UserPreferencesSource {
    override fun <T> storeValue(value: T, key: String) {
        sharedPreferences.edit {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is String -> putString(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                else -> Unit // Unsupported
            }
        }
    }

    override fun <T> getValue(key: String, defaultValue: T?): T? {
        return if (sharedPreferences.contains(key)) {
            val value: Pair<String, Any?> = sharedPreferences.all.toList().first { it.first == key }
            @Suppress("UNCHECKED_CAST")
            (value.second as? T)
        } else {
            null
        }
    }
}