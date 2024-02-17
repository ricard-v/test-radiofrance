package tech.mksoft.testradiofrance.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import tech.mksoft.testradiofrance.presentation.radiostations.RadioStationsViewModel

val appModule = module {
    viewModel { RadioStationsViewModel(get()) }
}