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
        private const val PREFERENCES_FILE_KEY = "SecretSharedPrefs"
        private const val TOKEN_KEY = "jwt_token"

        fun storeToken(context: Context, token: String) {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPreferences = EncryptedSharedPreferences.create(
                context,
                PREFERENCES_FILE_KEY,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
        }

        fun getToken(context: Context): String? {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPreferences = EncryptedSharedPreferences.create(
                context,
                PREFERENCES_FILE_KEY,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            return sharedPreferences.getString(TOKEN_KEY, null)
        }

        private fun decodeJwt(jwtToken: String): String? {
            val parts = jwtToken.split(".")
            return if (parts.size == 3) {
                String(Base64.decode(parts[1], Base64.URL_SAFE))
            } else null
        }

        fun getUserIdFromJwt(jwtToken: String): String? {
            return decodeJwt(jwtToken)?.let {
                JSONObject(it).getString("id")
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
