package tech.mksoft.testradiofrance.presentation.tools

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import kotlinx.coroutines.flow.Flow
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Performs a test on a flow to be executed within 1 second (1000 millis).
 */
suspend fun <T> Flow<T>.testAndCancel(testBlock: suspend ReceiveTurbine<T>.() -> Unit) {
    test(1_000L.toDuration(unit = DurationUnit.MILLISECONDS)) {
        testBlock.invoke(this)
        cancelAndIgnoreRemainingEvents()
    }
}