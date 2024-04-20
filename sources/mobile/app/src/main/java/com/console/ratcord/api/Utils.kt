package com.console.ratcord.api

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import android.util.Base64
import org.json.JSONObject

class Utils {
    private var httpClient: HttpClient? = null
    companion object {
        fun storeItem(context: Context, value: String?, fileKey: LocalStorage, key: LocalStorage) {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPreferences = EncryptedSharedPreferences.create(
                context,
                fileKey.value,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            sharedPreferences.edit().putString(key.value, value).apply()
        }

        fun getItem(context: Context, fileKey: LocalStorage, key: LocalStorage): String? {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPreferences = EncryptedSharedPreferences.create(
                context,
                fileKey.value,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            return sharedPreferences.getString(key.value, null)
        }

        private fun decodeJwt(jwtToken: String): String? {
            val parts = jwtToken.split(".")
            return if (parts.size == 3) {
                String(Base64.decode(parts[1], Base64.URL_SAFE))
            } else null
        }

        fun getUserIdFromJwt(jwtToken: String): Int? {
            return decodeJwt(jwtToken)?.let {
                JSONObject(it).getString("id").toInt()
            }
        }
    }

    fun getHttpClient(): HttpClient {
        if (httpClient == null) {
            httpClient = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json()
                }
            }
        }
        return httpClient!!
    }
}
