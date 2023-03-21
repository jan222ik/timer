package window

import androidx.compose.runtime.compositionLocalOf

val LocalWindowActions = compositionLocalOf<WindowActions> {
    error("CompositionLocal LocalWindowActions not provided")
}

class WindowActions(
    val minimizeToTray: () -> Unit
)
