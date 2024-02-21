package tech.mksoft.testradiofrance.core.common

sealed class DataRequestResult<out T> {
    data class Success<T>(val data: T) : DataRequestResult<T>()
    data class Error(val errorMessage: String?) : DataRequestResult<Nothing>()
}