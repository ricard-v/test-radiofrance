package tech.mksoft.testradiofrance.core.data.source

import kotlinx.coroutines.flow.Flow

interface UserPreferencesSource {

    // region Store Values
    suspend fun storeStringValue(value: String, key: String)
    // endregion Store Values

    // region Get Values
    suspend fun getStringValue(key: String): Flow<String?>
    // endregion Get Values

    // region Clear Values
    suspend fun removeStringKey(key: String)
    // endregion Clear Values
}