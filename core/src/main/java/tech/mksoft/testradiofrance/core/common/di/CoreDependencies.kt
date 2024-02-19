package tech.mksoft.testradiofrance.core.common.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import org.koin.dsl.module
import tech.mksoft.testradiofrance.core.common.graphql.ApolloClientFactory
import tech.mksoft.testradiofrance.core.data.local.UserPreferencesLocalSource
import tech.mksoft.testradiofrance.core.data.remote.RadioStationsRemoteDataSource
import tech.mksoft.testradiofrance.core.data.repository.RadioStationsRepository
import tech.mksoft.testradiofrance.core.data.repository.UserPreferencesRepository
import tech.mksoft.testradiofrance.core.data.source.RadioStationsDataSource
import tech.mksoft.testradiofrance.core.data.source.UserPreferencesSource
import tech.mksoft.testradiofrance.core.domain.usecase.GetRadioStationsUseCase
import tech.mksoft.testradiofrance.core.domain.usecase.GetStationsPrograms
import tech.mksoft.testradiofrance.core.domain.usecase.UserPreferencesUseCase

val coreModule = module {
    single { ApolloClientFactory.makeClient() }
    single { provideSharedPreferences(get()) }

    // region Data Sources
    factory<RadioStationsDataSource> { RadioStationsRemoteDataSource(get()) }
    factory<UserPreferencesSource> { UserPreferencesLocalSource(get()) }
    // endregion Data Sources

    // region Repositories
    single { RadioStationsRepository(get()) }
    single { UserPreferencesRepository(get()) }
    // endregion Repositories

    // region Use Cases
    factory { GetRadioStationsUseCase(get(), get()) }
    factory { GetStationsPrograms(get()) }
    factory { UserPreferencesUseCase(get()) }
    // endregion Use Cases
}

private fun provideSharedPreferences(context: Context): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(context)
}