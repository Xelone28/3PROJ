package com.console.ratcord.api

import android.content.Context
import com.console.ratcord.domain.entity.group.GroupMinimal
import com.console.ratcord.domain.entity.group.GroupMinimalWithId
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
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
import kotlinx.serialization.json.Json
class GroupService {
    private val client: HttpClient = Utils.getHttpClient()

    suspend fun getGroupById(context: Context, groupId: Int): Utils.Companion.Result<GroupMinimalWithId> {
        return try {
            val response: HttpResponse = client.get("http://10.0.2.2:5000/group/$groupId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
            }
            handleResponseWithBody(response)
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("Network error occurred: ${e.localizedMessage}"))
        }
    }

    suspend fun createGroup(context: Context, group: GroupMinimal): Utils.Companion.Result<Boolean> {
        return try {
            val response: HttpResponse = client.post("http://10.0.2.2:5000/group") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
                contentType(ContentType.Application.Json)
                setBody(group)
            }
            handleResponse(response)
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("Network error occurred: ${e.localizedMessage}"))
        }
    }

    suspend fun updateGroup(context: Context, groupMinimal: GroupMinimal, groupId: Int): Utils.Companion.Result<Boolean> {
        return try {
            val response: HttpResponse = client.patch("http://10.0.2.2:5000/group/$groupId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                    contentType(ContentType.Application.Json)
                    setBody(groupMinimal)
                }
            }
            handleResponse(response)
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("Network error occurred: ${e.localizedMessage}"))
        }
    }

    suspend fun deleteGroup(context: Context, groupId: Int): Utils.Companion.Result<Boolean> {
        return try {
            val response: HttpResponse = client.delete("http://10.0.2.2:5000/group/$groupId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
            }
            handleResponse(response)
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("Network error occurred: ${e.localizedMessage}"))
        }
    }

    private suspend fun handleResponse(response: HttpResponse): Utils.Companion.Result<Boolean> {
        return when (response.status) {
            HttpStatusCode.Unauthorized -> {
                Utils.Companion.Result.Error(Utils.Companion.AuthorizationException("Unauthorized access to data."))
            }
            HttpStatusCode.NoContent, HttpStatusCode.OK, HttpStatusCode.Created -> {
                Utils.Companion.Result.Success(true)
            }
            else -> {
                val errorMessage = response.bodyAsText() // Read the error message from the response body
                Utils.Companion.Result.Error(Utils.Companion.UnexpectedResponseException("Received unexpected status: ${response.status}. Message: $errorMessage"))
            }
        }
    }

    private suspend inline fun <reified T> handleResponseWithBody(response: HttpResponse): Utils.Companion.Result<T> {
        return when (response.status) {
            HttpStatusCode.Unauthorized -> {
                Utils.Companion.Result.Error(Utils.Companion.AuthorizationException("Unauthorized access to data."))
            }
            HttpStatusCode.OK -> {
                val body: String = response.bodyAsText()
                val json = Json { ignoreUnknownKeys = true }
                val data = json.decodeFromString<T>(body)
                Utils.Companion.Result.Success(data)
            }
            else -> {
                val errorMessage = response.bodyAsText() // Read the error message from the response body
                Utils.Companion.Result.Error(Utils.Companion.UnexpectedResponseException("Received unexpected status: ${response.status}. Message: $errorMessage"))
            }
        }
    }
}