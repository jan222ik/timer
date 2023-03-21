import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

sealed class Timer {
    abstract val uuid: UUID
    abstract val duration: Duration
    data class TimerPlan(
        override val duration: Duration
    ) : Timer() {
        override val uuid: UUID = UUID.randomUUID()

        fun start(scope: CoroutineScope, onFinish: (TimerExecution) -> Unit): TimerExecution {
            val durationMillis = duration.toLong(DurationUnit.MILLISECONDS)
            val startMillis = System.currentTimeMillis()
            var timerExecution: TimerExecution? = null
            val job = scope.launch(Dispatchers.IO) {
                delay(durationMillis)
                Retry()
                    .times(10)
                    .test(
                        block = { timerExecution != null },
                        onSuccess = { onFinish(timerExecution!!) }
                    )
            }
            timerExecution = TimerExecution(
                timerPlan = this,
                timerStart = startMillis,
                timerEnd = startMillis.plus(durationMillis),
                job = job
            )
            return timerExecution
        }
    }

    data class TimerExecution(
        val timerPlan: TimerPlan,
        val timerStart: Long,
        val timerEnd: Long,
        val job: Job
    ) : Timer() {
        override val duration: Duration
            get() = timerPlan.duration

        override val uuid: UUID
            get() = timerPlan.uuid

        fun pause(): TimerPlan {
            val remDuration = System.currentTimeMillis().minus(timerStart).toDuration(DurationUnit.MILLISECONDS)
            job.cancel()
            return timerPlan.copy(duration = duration.minus(remDuration))
        }
    }

}

