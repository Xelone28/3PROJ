package com.console.ratcord.api

import android.content.Context
import com.console.ratcord.domain.entity.LoginResponse
import com.console.ratcord.domain.entity.exception.AuthorizationException
import com.console.ratcord.domain.entity.user.User
import com.console.ratcord.domain.entity.user.UserMinimal
import com.console.ratcord.domain.entity.user.UserMinimalWithId
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.util.InternalAPI
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class UserService() {
    private val utils: Utils = Utils()
    private val client: HttpClient = utils.getHttpClient()
    suspend fun getUsers(context: Context): List<UserMinimalWithId>? {
        val response: HttpResponse = try {
            client.get("http://10.0.2.2:5000/api/users") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
            }
        } catch (e: Exception) {
            println("Network error occurred: ${e.localizedMessage}")
            return null
        }

        when (response.status) {
            HttpStatusCode.Unauthorized -> {
                // Throw a specific exception for unauthorized access
                throw AuthorizationException("Unauthorized access to user data.")
            }
            HttpStatusCode.OK -> {
                val body: String = response.bodyAsText()
                return Json.decodeFromString<List<UserMinimalWithId>>(body)
            }
            else -> {
                println("Received unexpected status: ${response.status}")
                return null
            }
        }
    }

    suspend fun getUserById(context: Context, userId: Int): UserMinimalWithId? {
        val response: HttpResponse = try {
            client.get("http://10.0.2.2:5000/api/users/$userId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
            }
        } catch (e: Exception) {
            println("Network error occurred: ${e.localizedMessage}")
            return null
        }

        when (response.status) {
            HttpStatusCode.Unauthorized -> {
                throw AuthorizationException("Unauthorized access to user data.")
            }
            HttpStatusCode.OK -> {
                val body: String = response.bodyAsText()
                return Json.decodeFromString<UserMinimalWithId>(body)
            }
            else -> {
                println("Received unexpected status: ${response.status}")
                return null
            }
        }
    }

    suspend fun createUser(user: UserMinimal): Boolean{
        val response: HttpResponse = try {
            client.post("http://10.0.2.2:5000/api/users") {
                contentType(ContentType.Application.Json)
                setBody(user)
            }
        } catch (e: Exception) {
            println(e)
            return false
        }

        if (response.status.isSuccess()) {
           return true
        } else {
            return false
        }
    }

    @OptIn(InternalAPI::class)
    suspend fun login(username: String, password: String, context: Context): Boolean{
        val jsonBody = buildJsonObject {
            put("username", username)
            put("password", password)
        }.toString()

        val response: HttpResponse = try {
            client.post("http://10.0.2.2:5000/api/users/login") {
                contentType(ContentType.Application.Json)
                body = jsonBody
            }
        } catch (e: Exception) {
            return false
        }

        if (response.status.isSuccess()) {
            val loginResponse = response.body<LoginResponse>()
            Utils.storeItem(context = context, value = loginResponse.token, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)
            val user: UserMinimalWithId? = getUserById(context = context, userId = loginResponse.id)
            // ask baba if login should return the entire user ? -- HELP HERE --
            if (user != null){
                Utils.storeItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.USER, value = Json.encodeToString(user))
            }
            return true
        } else {
            return false
        }
    }

    suspend fun updateUser(context: Context, user: UserMinimal, userId: Int): Boolean {
        val response: HttpResponse = try {
            client.patch("http://10.0.2.2:5000/api/users/$userId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                    contentType(ContentType.Application.Json)
                    setBody(user)
                }
            }
        } catch (e: Exception) {
            println("Network error occurred: ${e.localizedMessage}")
            return false
        }

        when (response.status) {
            HttpStatusCode.Unauthorized -> {
                throw AuthorizationException("Unauthorized access to user data.")
            }
            HttpStatusCode.NoContent -> {
                return true
            }
            else -> {
                println("Received unexpected status: ${response.status}")
                return false
            }
        }
    }
}
