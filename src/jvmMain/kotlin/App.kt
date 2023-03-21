import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.rememberNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import window.LocalTray
import window.LocalWindowActions

@Composable
fun App() {
    val scope = rememberCoroutineScope()
    val tray = LocalTray.current
    val minimizeToTray = LocalWindowActions.current.minimizeToTray
    val endNotification = rememberNotification("Timer", "Your timer has elapsed")
    Button(onClick = {
        scope.launch(Dispatchers.IO) {
            minimizeToTray()
            delay(3000)
            tray.sendNotification(endNotification)
        }
    }) {
        Text(text = "Minimize and send notification after 3 sec")
    }
}
