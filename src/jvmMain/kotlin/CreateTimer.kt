import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import kotlin.time.Duration


@Composable
fun CreateTimer(onDone: (Timer.TimerPlan, Boolean) -> Unit) {
    Column {
        var duration by remember { mutableStateOf("10s") }
        Text(text = "Duration:")
        TextField(
            value = duration,
            onValueChange = { duration = it },
            textStyle = TextStyle()
        )
        Button(
            onClick = {
                onDone(Timer.TimerPlan(Duration.parse(duration)), false)
            }
        ) {
            Text("Start Timer")
        }
        Button(
            onClick = {
                onDone(Timer.TimerPlan(Duration.parse(duration)), true)
            }
        ) {
            Text("Start Timer and close")
        }
    }
}
