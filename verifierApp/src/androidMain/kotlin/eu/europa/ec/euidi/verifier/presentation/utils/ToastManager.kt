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

package eu.europa.ec.euidi.verifier.presentation.utils

import android.widget.Toast
import eu.europa.ec.euidi.verifier.VerifierApplication

actual open class ToastManager actual constructor() {
    actual fun showToast(message: String) {
        val context = VerifierApplication.Companion.instance.baseContext
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}