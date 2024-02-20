package tech.mksoft.testradiofrance.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import tech.mksoft.testradiofrance.presentation.radiostations.RadioStationsViewModel
import tech.mksoft.testradiofrance.presentation.stationprograms.StationProgramsViewModel

val appModule = module {
    viewModel { RadioStationsViewModel(get(), get()) }
    viewModel { (stationId: String) ->
        StationProgramsViewModel(stationId = stationId, get(), get())
    }
}