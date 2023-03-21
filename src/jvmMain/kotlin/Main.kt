import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.jewel.themes.expui.standalone.theme.DarkTheme
import window.WindowsWindow
import window.TrayIcon

@Composable
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    Button(onClick = {
        text = "Hello, Desktop!"
    }) {
        Text(text)
    }
}

fun main() = application {
    var isVisible by remember { mutableStateOf(true) }
    val theme = DarkTheme
    val windowState = rememberWindowState(position = WindowPosition.Aligned(Alignment.BottomEnd))
    WindowsWindow(
        onCloseRequest = { isVisible = false },
        theme = theme,
        resizable = false,
        undecorated = true,
        state = windowState,
        visible = isVisible,
        mainToolBar = {
            with(it) {
                WindowDraggableArea {
                    Box(Modifier.fillMaxSize())
                }
            }
        }
    ) {
        App()
    }

    if (!isVisible) {
        Tray(
            TrayIcon,
            tooltip = "Timer",
            onAction = { isVisible = true },
            menu = {
                Item("Exit", onClick = ::exitApplication)
            },
        )
    }
}


