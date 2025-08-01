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

package eu.europa.ec.euidi.verifier.domain.di

import eu.europa.ec.euidi.verifier.core.controller.DataStoreController
import eu.europa.ec.euidi.verifier.core.controller.PlatformController
import eu.europa.ec.euidi.verifier.core.provider.ResourceProvider
import eu.europa.ec.euidi.verifier.core.provider.UuidProvider
import eu.europa.ec.euidi.verifier.domain.config.ConfigProvider
import eu.europa.ec.euidi.verifier.domain.interactor.CustomRequestInteractor
import eu.europa.ec.euidi.verifier.domain.interactor.CustomRequestInteractorImpl
import eu.europa.ec.euidi.verifier.domain.interactor.DocumentsToRequestInteractor
import eu.europa.ec.euidi.verifier.domain.interactor.DocumentsToRequestInteractorImpl
import eu.europa.ec.euidi.verifier.domain.interactor.HomeInteractor
import eu.europa.ec.euidi.verifier.domain.interactor.HomeInteractorImpl
import eu.europa.ec.euidi.verifier.domain.interactor.MenuInteractor
import eu.europa.ec.euidi.verifier.domain.interactor.MenuInteractorImpl
import eu.europa.ec.euidi.verifier.domain.interactor.QrScanInteractor
import eu.europa.ec.euidi.verifier.domain.interactor.QrScanInteractorImpl
import eu.europa.ec.euidi.verifier.domain.interactor.ReverseEngagementInteractor
import eu.europa.ec.euidi.verifier.domain.interactor.ReverseEngagementInteractorImpl
import eu.europa.ec.euidi.verifier.domain.interactor.SettingsInteractor
import eu.europa.ec.euidi.verifier.domain.interactor.SettingsInteractorImpl
import eu.europa.ec.euidi.verifier.domain.interactor.ShowDocumentsInteractor
import eu.europa.ec.euidi.verifier.domain.interactor.ShowDocumentsInteractorImpl
import eu.europa.ec.euidi.verifier.domain.interactor.TransferStatusInteractor
import eu.europa.ec.euidi.verifier.domain.interactor.TransferStatusInteractorImpl
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class InteractorModule {

    @Factory
    fun provideHomeInteractor(
        platformController: PlatformController,
        uuidProvider: UuidProvider,
        resourceProvider: ResourceProvider,
    ): HomeInteractor = HomeInteractorImpl(
        platformController,
        uuidProvider,
        resourceProvider,
    )

    @Factory
    fun provideDocumentsToRequestInteractor(
        configProvider: ConfigProvider,
        resourceProvider: ResourceProvider
    ): DocumentsToRequestInteractor =
        DocumentsToRequestInteractorImpl(configProvider, resourceProvider)

    @Factory
    fun provideCustomRequestInteractor(
        resourceProvider: ResourceProvider,
        configProvider: ConfigProvider,
    ): CustomRequestInteractor =
        CustomRequestInteractorImpl(
            resourceProvider = resourceProvider,
            configProvider = configProvider
        )

    @Factory
    fun provideShowDocumentsInteractor(
        resourceProvider: ResourceProvider,
        uuidProvider: UuidProvider
    ): ShowDocumentsInteractor =
        ShowDocumentsInteractorImpl(resourceProvider, uuidProvider)

    @Factory
    fun provideTransferStatusInteractor(
        resourceProvider: ResourceProvider,
        uuidProvider: UuidProvider
    ): TransferStatusInteractor = TransferStatusInteractorImpl(resourceProvider, uuidProvider)

    @Factory
    fun provideMenuInteractor(
        uuidProvider: UuidProvider,
        resourceProvider: ResourceProvider,
    ): MenuInteractor = MenuInteractorImpl(
        uuidProvider,
        resourceProvider,
    )

    @Factory
    fun provideSettingsInteractor(
        uuidProvider: UuidProvider,
        resourceProvider: ResourceProvider,
        dataStoreController: DataStoreController,
    ): SettingsInteractor = SettingsInteractorImpl(
        uuidProvider,
        resourceProvider,
        dataStoreController,
    )

    @Factory
    fun provideReverseEngagementInteractor(
        resourceProvider: ResourceProvider,
    ): ReverseEngagementInteractor = ReverseEngagementInteractorImpl(
        resourceProvider,
    )

    @Factory
    fun provideQrScanInteractor(
        resourceProvider: ResourceProvider,
    ): QrScanInteractor = QrScanInteractorImpl(
        resourceProvider,
    )

}