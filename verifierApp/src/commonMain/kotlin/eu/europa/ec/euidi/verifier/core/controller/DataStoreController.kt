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

package eu.europa.ec.euidi.verifier.core.controller

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath

enum class PrefKey(val identifier: String) {
    AUTO_CLOSE_CONNECTION("auto_close_connection"),
    USE_L2CAP("use_l2cap"),
    CLEAR_BLE_CACHE("clear_ble_cache"),
    HTTP("http"),
    BLE_CENTRAL_CLIENT("ble_central_client"),
    BLE_PERIPHERAL_SERVER("ble_peripheral_server"),
}

interface DataStoreController {
    suspend fun putBoolean(key: PrefKey, value: Boolean)
    suspend fun putInt(key: PrefKey, value: Int)
    suspend fun putString(key: PrefKey, value: String)
    suspend fun putDouble(key: PrefKey, value: Double)
    suspend fun putFloat(key: PrefKey, value: Float)
    suspend fun putByteArray(key: PrefKey, value: ByteArray)
    suspend fun putLong(key: PrefKey, value: Long)

    suspend fun getBoolean(key: PrefKey): Boolean?
    suspend fun getInt(key: PrefKey): Int?
    suspend fun getString(key: PrefKey): String?
    suspend fun getDouble(key: PrefKey): Double?
    suspend fun getFloat(key: PrefKey): Float?
    suspend fun getByteArray(key: PrefKey): ByteArray?
    suspend fun getLong(key: PrefKey): Long?
}

class DataStoreControllerImpl(
    val dataStore: DataStore<Preferences>
) : DataStoreController {
    override suspend fun putBoolean(key: PrefKey, value: Boolean) =
        savePreference(booleanPreferencesKey(key.identifier), value)

    override suspend fun putInt(key: PrefKey, value: Int) =
        savePreference(intPreferencesKey(key.identifier), value)

    override suspend fun putString(key: PrefKey, value: String) =
        savePreference(stringPreferencesKey(key.identifier), value)

    override suspend fun putDouble(key: PrefKey, value: Double) =
        savePreference(doublePreferencesKey(key.identifier), value)

    override suspend fun putFloat(key: PrefKey, value: Float) =
        savePreference(floatPreferencesKey(key.identifier), value)

    override suspend fun putByteArray(key: PrefKey, value: ByteArray) =
        savePreference(byteArrayPreferencesKey(key.identifier), value)

    override suspend fun putLong(key: PrefKey, value: Long) =
        savePreference(longPreferencesKey(key.identifier), value)

    override suspend fun getBoolean(key: PrefKey): Boolean? =
        readPreference(booleanPreferencesKey(key.identifier))

    override suspend fun getInt(key: PrefKey): Int? =
        readPreference(intPreferencesKey(key.identifier))

    override suspend fun getString(key: PrefKey): String? =
        readPreference(stringPreferencesKey(key.identifier))

    override suspend fun getDouble(key: PrefKey): Double? =
        readPreference(doublePreferencesKey(key.identifier))

    override suspend fun getFloat(key: PrefKey): Float? =
        readPreference(floatPreferencesKey(key.identifier))

    override suspend fun getByteArray(key: PrefKey): ByteArray? =
        readPreference(byteArrayPreferencesKey(key.identifier))

    override suspend fun getLong(key: PrefKey): Long? =
        readPreference(longPreferencesKey(key.identifier))

    private suspend fun <T> savePreference(
        key: Preferences.Key<T>,
        value: T
    ) {
        dataStore.edit { prefs ->
            prefs[key] = value
        }
    }

    private suspend fun <T> readPreference(
        preferencesKey: Preferences.Key<T>
    ): T? {
        return dataStore.data
            .map { prefs -> prefs[preferencesKey] }
            .firstOrNull()
    }

    companion object {
        const val DATASTORE_FILENAME = "verifier.preferences_pb"

        fun createDataStore(producePath: () -> String): DataStore<Preferences> {
            return PreferenceDataStoreFactory.createWithPath { producePath().toPath() }
        }
    }
}