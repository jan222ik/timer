import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import window.LocalWindowActions
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun App() {
    val minimizeToTray = LocalWindowActions.current.minimizeToTray

    val timerState = LocalTimerState.current


    if (timerState.activeTimers.isNotEmpty()) {
        LazyColumn {
            items(timerState.activeTimers) {
                var timeRem by remember(it) { mutableStateOf(it.timerEnd.minus(System.currentTimeMillis()).toDuration(DurationUnit.MILLISECONDS)) }
                LaunchedEffect(it) {
                    launch(Dispatchers.IO) {
                        while (true) {
                            timeRem = it.timerEnd.minus(System.currentTimeMillis()).toDuration(DurationUnit.MILLISECONDS)
                        }
                    }
                }
                Text(
                    text = it.timerPlan.uuid.toString() + " " + it.timerPlan.duration + " Remaining Time:" + timeRem.toString()
                )
            }
        }
    } else {
        CreateTimer(
            onDone = { timer, toTray ->
                timerState.addAndStart(timer)
                if (toTray) {
                    minimizeToTray.invoke()
                }
            }
        )
    }

}
