/*
 * Copyright (c) 2025 European Commission
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European
 * Commission - subsequent versions of the EUPL (the "Licence"); You may not use this work
 * except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific language
 * governing permissions and limitations under the Licence.
 */


package eu.europa.ec.euidi.verifier.presentation.component.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.zIndex
import eu.europa.ec.euidi.verifier.presentation.component.AppIcons
import eu.europa.ec.euidi.verifier.presentation.component.IconDataUi
import eu.europa.ec.euidi.verifier.presentation.component.loader.LoadingIndicator
import eu.europa.ec.euidi.verifier.presentation.component.preview.PreviewTheme
import eu.europa.ec.euidi.verifier.presentation.component.preview.ThemeModePreviews
import eu.europa.ec.euidi.verifier.presentation.component.utils.MAX_TOOLBAR_ACTIONS
import eu.europa.ec.euidi.verifier.presentation.component.utils.TopSpacing
import eu.europa.ec.euidi.verifier.presentation.component.utils.Z_STICKY
import eu.europa.ec.euidi.verifier.presentation.component.utils.screenPaddings
import eu.europa.ec.euidi.verifier.presentation.component.utils.stickyBottomPaddings
import eu.europa.ec.euidi.verifier.presentation.component.wrap.WrapIcon
import eu.europa.ec.euidi.verifier.presentation.component.wrap.WrapIconButton

data class ToolbarActionUi(
    val icon: IconDataUi,
    val order: Int = 100,
    val enabled: Boolean = true,
    val customTint: Color? = null,
    val clickable: Boolean = true,
    val throttleClicks: Boolean = true,
    val onClick: () -> Unit,
)

data class ToolbarConfig(
    val title: String = "",
    val actions: List<ToolbarActionUi> = listOf()
)

enum class ScreenNavigateAction {
    BACKABLE, CANCELABLE, NONE
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ContentScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    isLoading: Boolean = false,
    toolBarConfig: ToolbarConfig? = null,
    navigatableAction: ScreenNavigateAction = ScreenNavigateAction.NONE,
    onBack: (() -> Unit)? = null,
    topBar: @Composable (() -> Unit)? = null,
    bottomBar: @Composable (() -> Unit)? = null,
    stickyBottom: @Composable ((PaddingValues) -> Unit)? = null,
    fab: @Composable () -> Unit = {},
    fabPosition: FabPosition = FabPosition.End,
    snackbarHost: @Composable () -> Unit = {},
    contentErrorConfig: ContentErrorConfig? = null,
    bodyContent: @Composable (PaddingValues) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val hasToolBar = contentErrorConfig != null
            || navigatableAction != ScreenNavigateAction.NONE
            || topBar != null
            || toolBarConfig?.actions?.isNotEmpty() == true
    val topSpacing = if (hasToolBar) TopSpacing.WithToolbar else TopSpacing.WithoutToolbar

    Scaffold(
        topBar = {
            if (topBar != null && contentErrorConfig == null) topBar.invoke()
            else if (hasToolBar) {
                DefaultToolBar(
                    navigatableAction = contentErrorConfig?.let {
                        ScreenNavigateAction.CANCELABLE
                    } ?: navigatableAction,
                    onBack = contentErrorConfig?.onCancel ?: onBack,
                    keyboardController = keyboardController,
                    toolbarConfig = toolBarConfig,
                )
            }
        },
        bottomBar = bottomBar ?: {},
        floatingActionButton = fab,
        floatingActionButtonPosition = fabPosition,
        snackbarHost = snackbarHost,
    ) { padding ->

        Box(
            modifier = modifier
        ) {

            if (contentErrorConfig != null) {
                ContentError(
                    config = contentErrorConfig,
                    paddingValues = screenPaddings(padding)
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {

                    Box(modifier = Modifier.weight(1f)) {
                        bodyContent(screenPaddings(padding, topSpacing))
                    }

                    stickyBottom?.let { stickyBottomContent ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .zIndex(Z_STICKY),
                            contentAlignment = Alignment.Center
                        ) {
                            stickyBottomContent(
                                stickyBottomPaddings(
                                    contentScreenPaddings = screenPaddings(),
                                    layoutDirection = LocalLayoutDirection.current
                                )
                            )
                        }
                    }
                }

                if (isLoading) LoadingIndicator()
            }
        }
    }

    BackHandler(enabled = true) {
        contentErrorConfig?.let {
            contentErrorConfig.onCancel()
        } ?: onBack?.invoke()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultToolBar(
    navigatableAction: ScreenNavigateAction,
    onBack: (() -> Unit)?,
    keyboardController: SoftwareKeyboardController?,
    toolbarConfig: ToolbarConfig?,
) {

    TopAppBar(
        title = {
            Text(
                text = toolbarConfig?.title.orEmpty(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            // Check if we should add back/close button.
            if (navigatableAction != ScreenNavigateAction.NONE) {
                val navigationIcon = when (navigatableAction) {
                    ScreenNavigateAction.CANCELABLE -> AppIcons.Close
                    else -> AppIcons.ArrowBack
                }

                ToolbarIcon(
                    toolbarAction = ToolbarActionUi(
                        icon = navigationIcon,
                        onClick = {
                            onBack?.invoke()
                            keyboardController?.hide()
                        }
                    )
                )
            }
        },
        // Add toolbar actions.
        actions = {
            ToolBarActions(toolBarActions = toolbarConfig?.actions)
        }
    )
}

@Composable
internal fun ToolBarActions(
    toolBarActions: List<ToolbarActionUi>?
) {
    toolBarActions?.let { actions ->

        var dropDownMenuExpanded by remember { mutableStateOf(false) }

        // Show first [MAX_TOOLBAR_ACTIONS] actions.
        actions
            .sortedByDescending { it.order }
            .take(MAX_TOOLBAR_ACTIONS)
            .map { visibleToolbarAction ->
                ToolbarIcon(toolbarAction = visibleToolbarAction)
            }

        // Check if there are more actions to show.
        if (actions.size > MAX_TOOLBAR_ACTIONS) {
            Box {
                ToolbarIcon(
                    toolbarAction = ToolbarActionUi(
                        icon = AppIcons.VerticalMore,
                        onClick = { dropDownMenuExpanded = !dropDownMenuExpanded },
                        enabled = true,
                    )
                )
                DropdownMenu(
                    expanded = dropDownMenuExpanded,
                    onDismissRequest = { dropDownMenuExpanded = false }
                ) {
                    actions
                        .sortedByDescending { it.order }
                        .drop(MAX_TOOLBAR_ACTIONS)
                        .map { dropDownMenuToolbarAction ->
                            ToolbarIcon(toolbarAction = dropDownMenuToolbarAction)
                        }
                }
            }
        }
    }
}

@Composable
private fun ToolbarIcon(toolbarAction: ToolbarActionUi) {
    val customIconTint = toolbarAction.customTint
        ?: MaterialTheme.colorScheme.onSurface

    if (toolbarAction.clickable) {
        WrapIconButton(
            iconData = toolbarAction.icon,
            onClick = toolbarAction.onClick,
            enabled = toolbarAction.enabled,
            customTint = customIconTint,
            throttleClicks = toolbarAction.throttleClicks
        )
    } else {
        WrapIcon(
            modifier = Modifier.minimumInteractiveComponentSize(),
            iconData = toolbarAction.icon,
            enabled = toolbarAction.enabled,
            customTint = customIconTint,
        )
    }
}

@ThemeModePreviews
@Composable
private fun ToolbarIconClickablePreview() {
    PreviewTheme {
        val action = ToolbarActionUi(
            icon = AppIcons.ArrowBack,
            onClick = {},
            enabled = true,
            clickable = true,
        )

        ToolbarIcon(toolbarAction = action)
    }
}

@ThemeModePreviews
@Composable
private fun ToolbarIconNotClickablePreview() {
    PreviewTheme {
        val action = ToolbarActionUi(
            icon = AppIcons.ArrowBack,
            onClick = {},
            enabled = true,
            clickable = false,
        )

        ToolbarIcon(toolbarAction = action)
    }
}

@ThemeModePreviews
@Composable
private fun ToolBarActionsWithFourActionsPreview() {
    PreviewTheme {
        val icon = AppIcons.ArrowBack
        val toolBarActions = listOf(
            ToolbarActionUi(
                icon = icon,
                onClick = {},
                enabled = true,
                clickable = true,
            ),
            ToolbarActionUi(
                icon = icon,
                onClick = {},
                enabled = false,
                clickable = true,
            ),
            ToolbarActionUi(
                icon = icon,
                onClick = {},
                enabled = true,
                clickable = false,
            ),
            ToolbarActionUi(
                icon = icon,
                onClick = {},
                enabled = false,
                clickable = false,
            )
        )

        Row {
            ToolBarActions(toolBarActions)
        }
    }
}