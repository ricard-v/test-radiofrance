package tech.mksoft.testradiofrance.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import tech.mksoft.testradiofrance.MainViewModel
import tech.mksoft.testradiofrance.presentation.radiostations.RadioStationsViewModel
import tech.mksoft.testradiofrance.presentation.stationprograms.StationProgramsViewModel
import tech.mksoft.testradiofrance.services.media.RadioMediaPlayer

val appModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { RadioStationsViewModel(get(), get(), get()) }
    viewModel { (stationId: String) ->
        StationProgramsViewModel(stationId = stationId, get(), get())
    }

    factory { provideExoPlayer(androidContext()) }
    single { RadioMediaPlayer(get()) }
}

private fun provideExoPlayer(context: Context): ExoPlayer = ExoPlayer.Builder(context).build()