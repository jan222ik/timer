import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.jewel.themes.expui.standalone.theme.DarkTheme
import window.LocalTray
import window.LocalWindowActions
import window.TrayIcon
import window.WindowActions
import window.WindowsWindow

fun main() = application {
    val windowState = rememberWindowState(position = WindowPosition.Aligned(Alignment.BottomEnd))
    val trayState = rememberTrayState()

    var isVisible by remember { mutableStateOf(true) }

    WindowsWindow(
        onCloseRequest = { isVisible = false },
        theme = DarkTheme,
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
        val windowActions = remember { WindowActions(minimizeToTray = { isVisible = false })}
        CompositionLocalProvider(
            LocalTray provides trayState,
            LocalWindowActions provides windowActions
        ) {
            App()
        }
    }

    if (!isVisible) {
        Tray(
            state = trayState,
            icon = TrayIcon,
            tooltip = "Timer",
            onAction = { isVisible = true },
            menu = {
                Item("Exit", onClick = ::exitApplication)
            },
        )
    }
}


