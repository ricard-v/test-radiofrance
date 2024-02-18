package tech.mksoft.testradiofrance.core.common.graphql

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloRequest
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.interceptor.ApolloInterceptor
import com.apollographql.apollo3.interceptor.ApolloInterceptorChain
import kotlinx.coroutines.flow.Flow

object ApolloClientFactory {
    fun makeClient(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("https://openapi.radiofrance.fr/v1/graphql")
            .addInterceptor(RadioFranceAuthInterceptor)
            .build()
    }

    private data object RadioFranceAuthInterceptor : ApolloInterceptor {
        override fun <D : Operation.Data> intercept(request: ApolloRequest<D>, chain: ApolloInterceptorChain): Flow<ApolloResponse<D>> {
            return chain.proceed(
                request.newBuilder().addHttpHeader(
                    name = "X-Token",
                    value = "84c107b0-22f0-4958-883d-381edaa54174",
                ).build()
            )
        }
    }
}