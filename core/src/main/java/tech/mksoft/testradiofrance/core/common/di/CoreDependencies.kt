package tech.mksoft.testradiofrance.core.common.di

import org.koin.dsl.module
import tech.mksoft.testradiofrance.core.common.graphql.ApolloClientFactory
import tech.mksoft.testradiofrance.core.data.remote.RadioStationsRemoteDataSource
import tech.mksoft.testradiofrance.core.data.repository.RadioStationsRepository
import tech.mksoft.testradiofrance.core.data.source.RadioStationsDataSource
import tech.mksoft.testradiofrance.core.domain.GetRadioStationsUseCase

val coreModule = module {
    single { ApolloClientFactory.makeClient() }

    // region Radio Stations
    factory<RadioStationsDataSource> { RadioStationsRemoteDataSource(get()) }
    single { RadioStationsRepository(get()) }
    factory { GetRadioStationsUseCase(get()) }
    // endregion Radio Stations
}