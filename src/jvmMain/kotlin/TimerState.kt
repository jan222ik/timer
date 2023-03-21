import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.window.Notification
import kotlinx.coroutines.CoroutineScope

val LocalTimerState = compositionLocalOf<TimerState> {
    error("CompositionLocal LocalTimerState not provided")
}

class TimerState(
    private val scope: CoroutineScope,
    private val sendNotification: (Notification) -> Unit,
    val activeTimers: SnapshotStateList<Timer>
) {
    fun addAndStart(timerPlan: Timer.TimerPlan) {
        activeTimers.removeIf { it == timerPlan }

        val timerExecution = timerPlan.start(
            scope = scope,
            onFinish = {
                sendNotification(Notification("Timer", "Your timer has elapsed"))
                activeTimers.remove(it)
            }
        )
        activeTimers.add(timerExecution)

    }

    fun pause(timer: Timer.TimerExecution) {
        val idx = activeTimers.indexOf(timer)
        activeTimers.removeAt(idx)
        activeTimers.add(idx, timer.pause())
    }
}
