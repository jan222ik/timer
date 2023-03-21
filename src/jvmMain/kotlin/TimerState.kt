import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.window.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit

val LocalTimerState = compositionLocalOf<TimerState> {
    error("CompositionLocal LocalTimerState not provided")
}
class TimerState(
    private val scope: CoroutineScope,
    private val sendNotification: (Notification) -> Unit,
    val activeTimers: SnapshotStateList<Timer.TimerExecution>
) {
    fun addAndStart(timerPlan: Timer.TimerPlan) {
        val timerExecution = timerPlan.toExecution()
        activeTimers.add(timerExecution)
        scope.launch(Dispatchers.IO) {
            val timeMillis = timerPlan.duration.toLong(DurationUnit.MILLISECONDS)
            delay(timeMillis)
            sendNotification(Notification("Timer", "Your timer has elapsed"))
            activeTimers.remove(timerExecution)
        }
    }
}
