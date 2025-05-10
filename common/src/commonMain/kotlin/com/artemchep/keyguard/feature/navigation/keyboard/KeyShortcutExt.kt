package com.artemchep.keyguard.feature.navigation.keyboard

import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import com.artemchep.keyguard.common.util.flow.combineToList
import com.artemchep.keyguard.feature.navigation.state.RememberStateFlowScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun RememberStateFlowScope.interceptKeyEvents(
    vararg shortcuts: Pair<KeyShortcut, Flow<(() -> Unit)?>>,
): () -> Unit {
    val interceptorFlow = shortcuts
        .map { (shortcut, callbackFlow) ->
            callbackFlow
                .map { callback ->
                    shortcut to callback
                }
        }
        .combineToList()
        .map { shortcutsToCallbacks ->
            val shortcutsToCallbacksMap = shortcutsToCallbacks
                .asSequence()
                .mapNotNull { (shortcut, callback) ->
                    callback
                        ?: return@mapNotNull null
                    shortcut to callback
                }
                .toMap()
            if (shortcutsToCallbacksMap.isEmpty()) {
                return@map null
            }

            // lambda
            interceptor@{ keyEvent: KeyEvent ->
                if (keyEvent.type != KeyEventType.KeyDown) {
                    return@interceptor false
                }

                val shortcut = KeyShortcut(
                    key = keyEvent.key,
                    isCtrlPressed = keyEvent.isCtrlPressed,
                    isShiftPressed = keyEvent.isShiftPressed,
                    isAltPressed = keyEvent.isAltPressed,
                )
                val callback = shortcutsToCallbacksMap[shortcut]
                    ?: return@interceptor false
                callback()
                true
            }
        }
    return interceptKeyEvent(interceptorFlow)
}
