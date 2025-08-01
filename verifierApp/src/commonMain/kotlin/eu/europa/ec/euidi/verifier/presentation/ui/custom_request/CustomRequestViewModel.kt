/*
 * Copyright (c) 2023 European Commission
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

package eu.europa.ec.euidi.verifier.presentation.ui.custom_request

import androidx.lifecycle.viewModelScope
import eu.europa.ec.euidi.verifier.domain.interactor.CustomRequestInteractor
import eu.europa.ec.euidi.verifier.presentation.architecture.MviViewModel
import eu.europa.ec.euidi.verifier.presentation.architecture.UiEffect
import eu.europa.ec.euidi.verifier.presentation.architecture.UiEvent
import eu.europa.ec.euidi.verifier.presentation.architecture.UiState
import eu.europa.ec.euidi.verifier.presentation.component.ListItemDataUi
import eu.europa.ec.euidi.verifier.presentation.model.RequestedDocsHolder
import eu.europa.ec.euidi.verifier.presentation.model.RequestedDocumentUi
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

sealed interface CustomRequestContract {
    data class State(
        val screenTitle: String = "",
        val requestedDoc: RequestedDocumentUi? = null,
        val items: List<ListItemDataUi> = emptyList()
    ) : UiState

    sealed interface Event : UiEvent {
        data class Init(val doc: RequestedDocumentUi? = null) : Event
        data class OnItemClicked(val identifier: String, val isChecked: Boolean) : Event
        data object OnDoneClick : Event
        data object OnCancelClick : Event
    }

    sealed interface Effect : UiEffect {
        sealed interface Navigation : Effect {
            data class GoBack(val requestedDocuments: RequestedDocsHolder) : Navigation
        }
    }
}

@KoinViewModel
class CustomRequestViewModel(
    private val interactor: CustomRequestInteractor,
) : MviViewModel<CustomRequestContract.Event, CustomRequestContract.State, CustomRequestContract.Effect>() {
    override fun createInitialState(): CustomRequestContract.State = CustomRequestContract.State()

    override fun handleEvent(event: CustomRequestContract.Event) {
        when (event) {
            is CustomRequestContract.Event.Init -> {
                viewModelScope.launch {
                    val doc = event.doc

                    doc?.let {
                        val attestationType = it.documentType
                        val claims = interactor.getDocumentClaims(attestationType = attestationType)

                        val uiItems = interactor.transformToUiItems(
                            documentType = it.documentType,
                            claims = claims
                        )

                        val screenTitle = interactor.getScreenTitle(attestationType = attestationType)

                        setState {
                            copy(
                                screenTitle = screenTitle,
                                requestedDoc = doc,
                                items = uiItems
                            )
                        }
                    }
                }
            }

            is CustomRequestContract.Event.OnDoneClick -> {
                viewModelScope.launch {
                    uiState.value.requestedDoc?.let {
                        val reqDoc = it.copy(
                            documentType = it.documentType,
                            mode = it.mode,
                            claims = interactor.transformToClaimItems(uiState.value.items)
                        )

                        setState {
                            copy(
                                requestedDoc = reqDoc
                            )
                        }

                        setEffect {
                            CustomRequestContract.Effect.Navigation.GoBack(
                                RequestedDocsHolder(
                                    items = listOf(reqDoc)
                                )
                            )
                        }
                    }
                }
            }

            is CustomRequestContract.Event.OnCancelClick -> {
                setEffect {
                    CustomRequestContract.Effect.Navigation.GoBack(
                        RequestedDocsHolder(
                            items = emptyList()
                        )
                    )
                }
            }

            is CustomRequestContract.Event.OnItemClicked -> {
                setState {
                    copy(
                        items = interactor.handleItemSelection(
                            items = uiState.value.items,
                            identifier = event.identifier,
                            isChecked = event.isChecked
                        )
                    )
                }
            }
        }
    }
}