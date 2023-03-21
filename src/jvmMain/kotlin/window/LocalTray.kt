package window

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.window.TrayState

val LocalTray = compositionLocalOf<TrayState> {
    error("CompositionLocal LocalTray not provided")
}
