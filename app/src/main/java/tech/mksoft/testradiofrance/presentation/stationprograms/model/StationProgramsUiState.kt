package tech.mksoft.testradiofrance.presentation.stationprograms.model

import kotlinx.collections.immutable.ImmutableList
import tech.mksoft.testradiofrance.core.domain.model.StationProgram

sealed class StationProgramsUiState {
    abstract val isFavorite: Boolean
    abstract fun makeCopy(isFavorite: Boolean): StationProgramsUiState

    data object Empty : StationProgramsUiState() {
        override val isFavorite: Boolean = false
        override fun makeCopy(isFavorite: Boolean): StationProgramsUiState = Empty
    }

    data object Loading : StationProgramsUiState() {
        override val isFavorite: Boolean = false
        override fun makeCopy(isFavorite: Boolean): StationProgramsUiState = Loading
    }

    data class Error(
        val errorMessage: String,
        val onRetryClicked: () -> Unit,
        override val isFavorite: Boolean,
    ) : StationProgramsUiState() {
        override fun makeCopy(isFavorite: Boolean): StationProgramsUiState = copy(isFavorite = isFavorite)
    }

    data class Success(
        val programs: ImmutableList<StationProgram>,
        val loadMorePrograms: LoadMorePrograms?,
        val cannotLoadMorePrograms: CannotLoadMorePrograms?,
        override val isFavorite: Boolean,
    ) : StationProgramsUiState() {
        override fun makeCopy(isFavorite: Boolean): StationProgramsUiState = copy(isFavorite = isFavorite)
    }
}

data class LoadMorePrograms(val isLoading: Boolean, val onClicked: () -> Unit)

data object CannotLoadMorePrograms