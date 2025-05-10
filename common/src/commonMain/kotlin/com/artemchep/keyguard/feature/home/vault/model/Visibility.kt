package com.artemchep.keyguard.feature.home.vault.model

import androidx.compose.runtime.Immutable
import arrow.core.None
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant

@Immutable
data class Visibility(
    val concealed: Boolean = false,
    /**
     * If `true`, then the value is hidden and should never be
     * revealed to a user: for example, the policy requires a
     * field to be hidden.
     */
    val hidden: Boolean = false,
    /**
     * Call this before toggling the visibility state. This will
     * output [None] if the event should be discarded.
     */
    val transformUserEvent: (Boolean, (Boolean) -> Unit) -> Unit = { value, setter ->
        setter(value)
    },
    // Global
    val globalConfig: Global? = null,
) {
    @Immutable
    data class Global(
        val globalRevealStateFlow: StateFlow<Event>,
    )

    @Immutable
    data class Event(
        val value: Boolean,
        val timestamp: Instant,
    )
}