package tech.mksoft.testradiofrance.core.common.graphql

import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import tech.mksoft.testradiofrance.core.common.DataRequestResult

suspend fun <D : Operation.Data> safeApiCall(call: suspend () -> ApolloResponse<D>): DataRequestResult<D> {
    return try {
        val response = call.invoke()
        if (response.hasErrors()) {
            DataRequestResult.Error(errorMessage = response.errors?.first()?.message)
        } else {
            DataRequestResult.Success(data = response.data!!)
        }
    } catch (t: Throwable) {
        DataRequestResult.Error(errorMessage = t.message)
    }
}

infix fun<T, D : Operation.Data> DataRequestResult<D>.mapResult(transformer: (data: D) -> DataRequestResult<T>): DataRequestResult<T> {
    return when(this) {
        is DataRequestResult.Error -> this
        is DataRequestResult.Success -> transformer.invoke(this.data)
    }
}