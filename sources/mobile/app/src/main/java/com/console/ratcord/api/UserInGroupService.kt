package com.console.ratcord.api

import android.content.Context
import com.console.ratcord.domain.entity.group.Group
import com.console.ratcord.domain.entity.exception.AuthorizationException
import com.console.ratcord.domain.entity.user.UserExtraMinimal
import com.console.ratcord.domain.entity.user.UserMinimalWithUserId
import com.console.ratcord.domain.entity.userInGroup.UserInGroup
import com.console.ratcord.domain.entity.userInGroup.UserInGroupInvitation
import com.console.ratcord.domain.entity.userInGroup.UserInGroupMinimal
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
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class UserInGroupService() {
    private val client: HttpClient = Utils.getHttpClient()

    suspend fun addUserInGroup(context: Context, userInGroup: UserInGroupMinimal): Utils.Companion.Result<Boolean> {
        return try {
            val response: HttpResponse = client.post("http://10.0.2.2:5000/useringroup") {
                headers {
                    append(
                        "Authorization",
                        "Bearer ${
                            Utils.getItem(
                                context = context,
                                fileKey = LocalStorage.PREFERENCES_FILE_KEY,
                                key = LocalStorage.TOKEN_KEY
                            )
                        }"
                    )
                    contentType(ContentType.Application.Json)
                    setBody(userInGroup)
                }
            }
            handleResponse(response)
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("Network error occurred: ${e.localizedMessage}"))
        }
    }

    suspend fun updateUserInGroup(context: Context, isActive: Boolean?, isGroupAdmin: Boolean?, userId: Int, groupId: Int): Utils.Companion.Result<Boolean> {
        val jsonBody = buildJsonObject {
            put("isGroupAdmin", isGroupAdmin)
            put("isActive", isActive)
        }.toString()
        return try {
            val response: HttpResponse = client.patch("http://10.0.2.2:5000/useringroup/$userId/$groupId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                    contentType(ContentType.Application.Json)
                    setBody(jsonBody)
                }
            }
            handleResponse(response)
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("Network error occurred: ${e.localizedMessage}"))
        }
    }

    suspend fun getInvitationsToGroup(context: Context, userId: Int): Utils.Companion.Result<List<UserInGroupInvitation>> {
        return try {
            val response: HttpResponse = client.get("http://10.0.2.2:5000/useringroup/invitation/$userId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
            }
            handleResponseWithBody(response)
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("Network error occurred: ${e.localizedMessage}"))
        }
    }

    suspend fun getUsersInUserGroups(context: Context, userId: Int): Utils.Companion.Result<List<UserExtraMinimal>> {
        return try {
            val response: HttpResponse = client.get("http://10.0.2.2:5000/useringroup/$userId/groupusers") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
            }
            handleResponseWithBody(response)
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("Network error occurred: ${e.localizedMessage}"))
        }
    }

    suspend fun deleteUserFromGroup(context: Context, userId: Int, groupId: Int): Utils.Companion.Result<Boolean> {
        return try {
            val response: HttpResponse = client.delete("http://10.0.2.2:5000/useringroup/$userId/$groupId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
            }
            handleResponse(response)
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("Network error occurred: ${e.localizedMessage}"))
        }
    }

    suspend fun getUsersInGroup(context: Context, id: Int): Utils.Companion.Result<List<UserMinimalWithUserId>> {
        return try {
            val response: HttpResponse = client.get("http://10.0.2.2:5000/useringroup/users/$id") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
            }
            handleResponseWithBody(response)
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("Network error occurred: ${e.localizedMessage}"))
        }
    }

    suspend fun getGroupsFromUserId(context: Context, id: Int): Utils.Companion.Result<List<Group>> {
        return try {
            val response: HttpResponse = client.get("http://10.0.2.2:5000/useringroup/user/$id") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
            }
            handleResponseWithBody(response)
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("Network error occurred: ${e.localizedMessage}"))
        }
    }

    private suspend fun handleResponse(response: HttpResponse): Utils.Companion.Result<Boolean> {
        return when (response.status) {
            HttpStatusCode.Unauthorized -> {
                Utils.Companion.Result.Error(Utils.Companion.AuthorizationException("Unauthorized access to data."))
            }
            HttpStatusCode.NoContent -> {
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
            HttpStatusCode.NotFound -> {
                val errorMessage = response.bodyAsText()
                Utils.Companion.Result.Error(Utils.Companion.UnexpectedResponseException(errorMessage))
            }
            else -> {
                val errorMessage = response.bodyAsText()
                Utils.Companion.Result.Error(Utils.Companion.UnexpectedResponseException("Received unexpected status: ${response.status}. Message: $errorMessage"))
            }
        }
    }
}