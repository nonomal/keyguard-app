package com.artemchep.keyguard.feature.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.artemchep.keyguard.feature.navigation.LocalNavigationController
import com.artemchep.keyguard.feature.navigation.NavigationIntent
import com.artemchep.keyguard.platform.leIme
import com.artemchep.keyguard.platform.leSystemBars
import com.artemchep.keyguard.ui.DialogPopup
import com.artemchep.keyguard.ui.WunderPopup
import com.artemchep.keyguard.ui.theme.Dimens
import com.artemchep.keyguard.ui.theme.combineAlpha
import com.artemchep.keyguard.ui.util.HorizontalDivider

// See:
// https://m3.material.io/components/dialogs/specs#9a8c226b-19fa-4d6b-894e-e7d5ca9203e8

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun Dialog(
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable ColumnScope.() -> Unit)?,
    content: (@Composable BoxScope.() -> Unit)?,
    contentScrollable: Boolean = true,
    actions: @Composable FlowRowScope.() -> Unit,
) {
    val updatedNavController by rememberUpdatedState(LocalNavigationController.current)
    DialogPopup(
        onDismissRequest = {
            val intent = NavigationIntent.Pop
            updatedNavController.queue(intent)
        },
    ) {
        val scrollState = rememberScrollState()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        Column(
            modifier = Modifier,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            ) {
                if (title != null) {
                    Spacer(
                        modifier = Modifier
                            .height(24.dp),
                    )
                    val centerAlign = icon != null
                    Column(
                        modifier = Modifier
                            .padding(horizontal = Dimens.horizontalPadding)
                            .fillMaxWidth(),
                        horizontalAlignment = if (centerAlign) {
                            Alignment.CenterHorizontally
                        } else {
                            Alignment.Start
                        },
                    ) {
                        if (icon != null) {
                            icon()
                            Spacer(
                                modifier = Modifier
                                    .height(16.dp),
                            )
                        }
                        ProvideTextStyle(
                            MaterialTheme.typography.titleLarge
                                .copy(
                                    textAlign = if (centerAlign) {
                                        TextAlign.Center
                                    } else {
                                        TextAlign.Start
                                    },
                                ),
                        ) {
                            title()
                        }
                        Spacer(
                            modifier = Modifier
                                .height(16.dp),
                        )
                    }
                }
                if (content != null) {
                    Box {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (contentScrollable) {
                                        Modifier
                                            .verticalScroll(scrollState)
                                    } else {
                                        Modifier
                                    },
                                )
                                .then(
                                    if (title == null) {
                                        Modifier
                                            .padding(top = 24.dp)
                                    } else {
                                        Modifier
                                    },
                                ),
                            contentAlignment = Alignment.TopStart,
                        ) {
                            CompositionLocalProvider(
                                LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                            ) {
                                content()
                            }
                        }
                        androidx.compose.animation.AnimatedVisibility(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth(),
                            visible = scrollState.canScrollBackward && contentScrollable && title != null,
                        ) {
                            HorizontalDivider(transparency = false)
                        }
                        androidx.compose.animation.AnimatedVisibility(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth(),
                            visible = scrollState.canScrollForward && contentScrollable,
                        ) {
                            HorizontalDivider(transparency = false)
                        }
                    }
                    Spacer(
                        modifier = Modifier
                            .height(16.dp),
                    )
                }
            }
            FlowRow(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                    )
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                actions()
            }
            Spacer(
                modifier = Modifier
                    .height(16.dp),
            )
        }
    }
}

@Composable
fun PopupDialog(
    content: @Composable BoxScope.() -> Unit,
) {
    val updatedNavController by rememberUpdatedState(LocalNavigationController.current)
    WunderPopup(
        onDismissRequest = {
            val intent = NavigationIntent.Pop
            updatedNavController.queue(intent)
        },
    ) {
        Dialog(
            content = content,
        )
    }
}

@Composable
fun Dialog(
    content: @Composable BoxScope.() -> Unit,
) {
    val updatedNavController by rememberUpdatedState(LocalNavigationController.current)

    val tintColor = MaterialTheme.colorScheme.background
        .combineAlpha(0.8f)
    Box(
        modifier = Modifier
            .background(tintColor)
            .pointerInput(Unit) {
                detectTapGestures {
                    val intent = NavigationIntent.Pop
                    updatedNavController.queue(intent)
                }
            }
            .fillMaxSize(),
    ) {
        CompositionLocalProvider(
            LocalAbsoluteTonalElevation provides 8.dp,
        ) {
            val verticalInsets = WindowInsets.leSystemBars
                .union(WindowInsets.leIme)
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .windowInsetsPadding(verticalInsets)
                    .consumeWindowInsets(verticalInsets)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(
                        MaterialTheme.colorScheme
                            .surfaceColorAtElevation(LocalAbsoluteTonalElevation.current),
                    )
                    .widthIn(
                        min = 280.dp,
                        max = 560.dp,
                    )
                    .align(Alignment.Center)
                    // Eat all pointer inputs from within the
                    // dialog composable.
                    .pointerInput(Unit) {
                        detectTapGestures {
                        }
                    },
            ) {
                content()
            }
        }
    }
}
