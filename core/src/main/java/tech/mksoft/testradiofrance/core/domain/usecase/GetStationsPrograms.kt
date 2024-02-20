package tech.mksoft.testradiofrance.core.domain.usecase

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import tech.mksoft.testradiofrance.core.common.DataRequestResult
import tech.mksoft.testradiofrance.core.data.repository.RadioStationsRepository
import tech.mksoft.testradiofrance.core.domain.model.StationProgram

class GetStationsPrograms(private val repository: RadioStationsRepository) {
    private var currentPrograms: List<StationProgram> = emptyList()

    suspend fun getProgramsForRadioStation(
        stationId: String,
        count: Int = 10,
        fromCursor: String? = null,
    ): Flow<DataRequestResult<List<StationProgram>>> {
        return repository.getStationPrograms(stationId, count, fromCursor).map { result ->
            when (result) {
                is DataRequestResult.Error -> result
                is DataRequestResult.Success -> {
                    if (result.data.isEmpty()) {
                        Log.e("TEST_UI", "is last ... will show <${currentPrograms.size}> items")
                        result.copy(
                            data = currentPrograms.mapIndexed { index, stationProgram ->
                                stationProgram.copy(
                                    isLast = index == currentPrograms.lastIndex
                                )
                            },
                        )
                    } else {
                        Log.e("TEST_UI", "is last ... NOt !")
                        currentPrograms += result.data
                        result.copy(data = currentPrograms) // append result
                    }
                }
            }
        }
    }

}