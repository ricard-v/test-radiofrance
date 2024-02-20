package tech.mksoft.testradiofrance.core.common.graphql

import android.util.Log
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import tech.mksoft.testradiofrance.core.common.DataRequestResult

suspend fun <D : Operation.Data> safeApiCall(call: suspend () -> Flow<ApolloResponse<D>>): Flow<DataRequestResult<D>> {
    return call.invoke()
        .map { response -> response.asResult() }
        .catch { t -> emit(DataRequestResult.Error(errorMessage = t.message)) }
}

// Note: this extension is required  because the error field has the "@jvmField" annotation which is something Mockk does not support for unit testing.
// See ongoing issue: https://github.com/mockk/mockk/issues/488.
fun ApolloResponse<*>.getError() = errors?.firstOrNull()

// Same reason as above.
fun <T : Operation.Data> ApolloResponse<T>.getData() = data

infix fun <T, D : Operation.Data> Flow<DataRequestResult<D>>.mapResult(transformer: (data: D) -> DataRequestResult<T>): Flow<DataRequestResult<T>> {
    return this.map { result ->
        when (result) {
            is DataRequestResult.Error -> result
            is DataRequestResult.Success -> transformer.invoke(result.data)
        }
    }
}

private fun <D : Operation.Data> ApolloResponse<D>.asResult() = if (hasErrors()) {
    DataRequestResult.Error(errorMessage = getError()?.message)
} else {
    DataRequestResult.Success(data = getData()!!)
}