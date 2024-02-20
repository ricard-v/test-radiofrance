package tech.mksoft.testradiofrance.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import tech.mksoft.testradiofrance.core.data.source.UserPreferencesSource

class UserPreferencesLocalSource(private val dataStore: DataStore<Preferences>) : UserPreferencesSource {
    override suspend fun storeStringValue(value: String, key: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    override suspend fun getStringValue(key: String): Flow<String?> {
        return dataStore.data.map { preferences -> preferences[stringPreferencesKey(key)] }
    }

    override suspend fun removeStringKey(key: String) {
        dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
        }
    }
}