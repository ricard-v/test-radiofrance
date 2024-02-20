package tech.mksoft.testradiofrance.core.common.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidContext
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
    single<DataStore<Preferences>> { androidContext().dataStorePreferences }

    // region Data Sources
    factory<RadioStationsDataSource> { RadioStationsRemoteDataSource(get()) }
    factory<UserPreferencesSource> { UserPreferencesLocalSource(get()) }
    // endregion Data Sources

    // region Repositories
    single { RadioStationsRepository(get()) }
    single { UserPreferencesRepository(get()) }
    // endregion Repositories

    // region Use Cases
    factory { GetRadioStationsUseCase(get()) }
    factory { GetStationsPrograms(get()) }
    factory { UserPreferencesUseCase(get()) }
    // endregion Use Cases
}

private val Context.dataStorePreferences: DataStore<Preferences> by preferencesDataStore(name = "main")