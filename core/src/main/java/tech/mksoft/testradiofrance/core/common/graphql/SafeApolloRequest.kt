package tech.mksoft.testradiofrance.core.common.graphql

import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import tech.mksoft.testradiofrance.core.common.DataRequestResult

suspend fun <D : Operation.Data> safeApiCall(call: suspend () -> ApolloResponse<D>): DataRequestResult<D> {
    return try {
        val response = call.invoke()
        if (response.hasErrors()) {
            DataRequestResult.Error(errorMessage = response.getError()?.message)
        } else {
            DataRequestResult.Success(data = response.getData()!!)
        }
    } catch (t: Throwable) {
        DataRequestResult.Error(errorMessage = t.message)
    }
}

// Note: this extension is required  because the error field has the "@jvmField" annotation which is something Mockk does not support for unit testing.
// See ongoing issue: https://github.com/mockk/mockk/issues/488.
fun ApolloResponse<*>.getError() = errors?.firstOrNull()

// Same reason as above.
fun <T: Operation.Data> ApolloResponse<T>.getData() = data

infix fun<T, D : Operation.Data> DataRequestResult<D>.mapResult(transformer: (data: D) -> DataRequestResult<T>): DataRequestResult<T> {
    return when(this) {
        is DataRequestResult.Error -> this
        is DataRequestResult.Success -> transformer.invoke(this.data)
    }
}