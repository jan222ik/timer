import java.util.*
import kotlin.time.Duration
import kotlin.time.DurationUnit

sealed interface Timer {
    class TimerPlan(val duration: Duration) : Timer {
        val uuid: UUID = UUID.randomUUID()

        fun toExecution() : TimerExecution {
            val durationMillis = duration.toLong(DurationUnit.MILLISECONDS)
            val startMillis = System.currentTimeMillis()
            return TimerExecution(timerPlan = this, timerStart = startMillis, timerEnd = startMillis.plus(durationMillis))
        }
    }

    class TimerExecution(val timerPlan: TimerPlan, val timerStart: Long, val timerEnd: Long) : Timer

}

