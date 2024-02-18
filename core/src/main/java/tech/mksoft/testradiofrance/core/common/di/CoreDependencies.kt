package tech.mksoft.testradiofrance.core.common.di

import org.koin.dsl.module
import tech.mksoft.testradiofrance.core.common.graphql.ApolloClientFactory
import tech.mksoft.testradiofrance.core.data.remote.RadioStationsRemoteDataSource
import tech.mksoft.testradiofrance.core.data.repository.RadioStationsRepository
import tech.mksoft.testradiofrance.core.data.source.RadioStationsDataSource
import tech.mksoft.testradiofrance.core.domain.usecase.GetRadioStationsUseCase
import tech.mksoft.testradiofrance.core.domain.usecase.GetStationsPrograms

val coreModule = module {
    single { ApolloClientFactory.makeClient() }

    factory<RadioStationsDataSource> { RadioStationsRemoteDataSource(get()) }
    single { RadioStationsRepository(get()) }

    // region Use Cases
    factory { GetRadioStationsUseCase(get()) }
    factory { GetStationsPrograms(get()) }
    // endregion Use Cases
}