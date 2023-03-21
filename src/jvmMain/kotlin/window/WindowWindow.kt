package window

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.jewel.themes.expui.desktop.window.LocalWindow
import org.jetbrains.jewel.themes.expui.standalone.control.ActionButton
import org.jetbrains.jewel.themes.expui.standalone.control.BasicMainToolBar
import org.jetbrains.jewel.themes.expui.standalone.control.CustomWindowDecorationSupport
import org.jetbrains.jewel.themes.expui.standalone.control.Icon
import org.jetbrains.jewel.themes.expui.standalone.control.LocalActionButtonColors
import org.jetbrains.jewel.themes.expui.standalone.control.LocalContentActivated
import org.jetbrains.jewel.themes.expui.standalone.control.MainToolBarColors
import org.jetbrains.jewel.themes.expui.standalone.control.MainToolBarScope
import org.jetbrains.jewel.themes.expui.standalone.control.MainToolBarTitle
import org.jetbrains.jewel.themes.expui.standalone.style.LocalAreaColors
import org.jetbrains.jewel.themes.expui.standalone.style.LocalMainToolBarColors
import org.jetbrains.jewel.themes.expui.standalone.style.LocalWindowsCloseWindowButtonColors
import org.jetbrains.jewel.themes.expui.standalone.style.areaBackground
import org.jetbrains.jewel.themes.expui.standalone.theme.LightTheme
import org.jetbrains.jewel.themes.expui.standalone.theme.Theme
import sun.misc.Unsafe
import java.awt.Shape
import java.awt.Window
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Method

@Composable
internal fun WindowsWindow(
    onCloseRequest: () -> Unit,
    state: WindowState = rememberWindowState(),
    visible: Boolean = true,
    title: String = "",
    showTitle: Boolean = true,
    theme: Theme = LightTheme,
    icon: Painter? = null,
    undecorated: Boolean = false,
    transparent: Boolean = false,
    resizable: Boolean = true,
    enabled: Boolean = true,
    focusable: Boolean = true,
    alwaysOnTop: Boolean = false,
    onPreviewKeyEvent: (KeyEvent) -> Boolean = { false },
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    mainToolBar: (@Composable MainToolBarScope.(FrameWindowScope) -> Unit)?,
    content: @Composable FrameWindowScope.() -> Unit
) {
    Window(
        onCloseRequest,
        state,
        visible,
        title,
        icon,
        undecorated,
        transparent,
        resizable,
        enabled,
        focusable,
        alwaysOnTop,
        onPreviewKeyEvent,
        onKeyEvent
    ) {
        CompositionLocalProvider(
            LocalWindow provides window,
            LocalContentActivated provides LocalWindowInfo.current.isWindowFocused,
            *theme.provideValues()
        ) {
            Column(Modifier.fillMaxSize()) {
                MainToolBarOnWindows(icon, state, onCloseRequest, title, showTitle, resizable, content = mainToolBar)
                Spacer(Modifier.fillMaxWidth().height(1.dp).background(LocalAreaColors.current.startBorderColor))
                Box(Modifier.fillMaxSize().areaBackground()) {
                    content()
                }
            }
        }
    }
}

@Composable
internal fun FrameWindowScope.MainToolBarOnWindows(
    icon: Painter?,
    windowState: WindowState,
    onCloseRequest: () -> Unit,
    title: String,
    showTitle: Boolean,
    resizeable: Boolean,
    colors: MainToolBarColors = LocalMainToolBarColors.current,
    content: (@Composable MainToolBarScope.(FrameWindowScope) -> Unit)?
) {
    BasicMainToolBar(colors, JbrCustomWindowDecorationAccessing) {
        if (icon != null) {
            Box(
                modifier = Modifier.size(40.dp).mainToolBarItem(Alignment.Start, true),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon)
            }
        }
        WindowsSystemButtons(windowState, resizeable, onCloseRequest)
        if (showTitle) {
            MainToolBarTitle(title)
        }
        content?.invoke(this, this@MainToolBarOnWindows)
    }
}

@Composable
private fun MainToolBarScope.WindowsSystemButtons(
    windowState: WindowState,
    resizeable: Boolean,
    onCloseRequest: () -> Unit
) {
    val active = LocalContentActivated.current
    CompositionLocalProvider(
        LocalActionButtonColors provides LocalWindowsCloseWindowButtonColors.current
    ) {
        ActionButton(
            { onCloseRequest() },
            Modifier.focusProperties { canFocus = false }.size(40.dp).mainToolBarItem(Alignment.End),
            shape = RectangleShape
        ) {
            if (active) {
                Icon("icons/windows/closeActive.svg")
            } else {
                Icon("icons/windows/closeInactive.svg")
            }
        }
    }
    ActionButton(
        {
            windowState.placement = when (windowState.placement) {
                WindowPlacement.Floating -> WindowPlacement.Maximized
                WindowPlacement.Maximized -> WindowPlacement.Floating
                WindowPlacement.Fullscreen -> WindowPlacement.Fullscreen
            }
        },
        Modifier.focusProperties { canFocus = false }.size(40.dp).mainToolBarItem(Alignment.End),
        enabled = resizeable,
        shape = RectangleShape
    ) {
        if (windowState.placement == WindowPlacement.Floating) {
            if (active) {
                Icon("icons/windows/maximize.svg")
            } else {
                Icon("icons/windows/maximizeInactive.svg")
            }
        } else {
            if (active) {
                Icon("icons/windows/restore.svg")
            } else {
                Icon("icons/windows/restoreInactive.svg")
            }
        }
    }
    ActionButton(
        { windowState.isMinimized = true },
        Modifier.focusProperties { canFocus = false }.size(40.dp).mainToolBarItem(Alignment.End),
        shape = RectangleShape
    ) {
        if (active) {
            Icon("icons/windows/minimize.svg")
        } else {
            Icon("icons/windows/minimizeInactive.svg")
        }
    }
}



internal object JbrCustomWindowDecorationAccessing : CustomWindowDecorationSupport {
    init {
        UnsafeAccessing.assignAccessibility(
            UnsafeAccessing.desktopModule,
            listOf(
                "java.awt"
            )
        )
    }

    private val customWindowDecorationInstance: Any? = try {
        val customWindowDecoration = Class.forName("java.awt.Window\$CustomWindowDecoration")
        val constructor = customWindowDecoration.declaredConstructors.first()
        constructor.isAccessible = true
        constructor.newInstance()
    } catch (e: Throwable) {
        null
    }

    private val setCustomDecorationEnabledMethod: Method? =
        getMethod("setCustomDecorationEnabled", Window::class.java, Boolean::class.java)

    private val setCustomDecorationTitleBarHeightMethod: Method? =
        getMethod("setCustomDecorationTitleBarHeight", Window::class.java, Int::class.java)

    private val setCustomDecorationHitTestSpotsMethod: Method? =
        getMethod("setCustomDecorationHitTestSpots", Window::class.java, MutableList::class.java)

    private fun getMethod(name: String, vararg params: Class<*>): Method? {
        return try {
            val clazz = Class.forName("java.awt.Window\$CustomWindowDecoration")
            val method = clazz.getDeclaredMethod(
                name,
                *params
            )
            method.isAccessible = true
            method
        } catch (e: Throwable) {
            null
        }
    }

    override fun setCustomDecorationEnabled(window: Window, enabled: Boolean) {
        val instance = customWindowDecorationInstance ?: return
        val method = setCustomDecorationEnabledMethod ?: return
        method.invoke(instance, window, enabled)
    }

    override fun setCustomDecorationTitleBarHeight(window: Window, height: Int) {
        val instance = customWindowDecorationInstance ?: return
        val method = setCustomDecorationTitleBarHeightMethod ?: return
        method.invoke(instance, window, height)
    }

    override fun setCustomDecorationHitTestSpotsMethod(window: Window, spots: Map<Shape, Int>) {
        val instance = customWindowDecorationInstance ?: return
        val method = setCustomDecorationHitTestSpotsMethod ?: return
        method.invoke(instance, window, spots.entries.toMutableList())
    }
}

internal object UnsafeAccessing {

    private val unsafe: Any? by lazy {
        try {
            val theUnsafe = Unsafe::class.java.getDeclaredField("theUnsafe")
            theUnsafe.isAccessible = true
            theUnsafe.get(null) as Unsafe
        } catch (e: Throwable) {
            null
        }
    }

    val desktopModule by lazy {
        ModuleLayer.boot().findModule("java.desktop").get()
    }

    val ownerModule by lazy {
        this.javaClass.module
    }

    private val isAccessibleFieldOffset: Long? by lazy {
        try {
            (unsafe as? Unsafe)?.objectFieldOffset(Parent::class.java.getDeclaredField("first"))
        } catch (e: Throwable) {
            null
        }
    }

    private val implAddOpens by lazy {
        try {
            Module::class.java.getDeclaredMethod(
                "implAddOpens",
                String::class.java,
                Module::class.java
            ).accessible()
        } catch (e: Throwable) {
            null
        }
    }

    fun assignAccessibility(obj: AccessibleObject) {
        try {
            val theUnsafe = unsafe as? Unsafe ?: return
            val offset = isAccessibleFieldOffset ?: return
            theUnsafe.putBooleanVolatile(obj, offset, true)
        } catch (e: Throwable) {
            // ignore
        }
    }

    fun assignAccessibility(module: Module, packages: List<String>) {
        try {
            packages.forEach {
                implAddOpens?.invoke(module, it, ownerModule)
            }
        } catch (e: Throwable) {
            // ignore
        }
    }

    private class Parent {

        var first = false

        @Volatile
        var second: Any? = null
    }
}

internal fun <T : AccessibleObject> T.accessible(): T {
    return apply {
        UnsafeAccessing.assignAccessibility(this)
    }
}

