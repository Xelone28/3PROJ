package com.console.ratcord.api

import android.content.Context
import com.console.ratcord.domain.entity.group.Group
import com.console.ratcord.domain.entity.exception.AuthorizationException
import com.console.ratcord.domain.entity.group.GroupMinimal
import com.console.ratcord.domain.entity.group.GroupMinimalWithId
import com.console.ratcord.domain.entity.user.UserMinimal
import com.console.ratcord.domain.entity.user.UserMinimalWithId
import com.console.ratcord.domain.entity.user.UserMinimalWithUserId
import com.console.ratcord.domain.entity.userInGroup.UserInGroup
import com.console.ratcord.domain.entity.userInGroup.UserInGroupPerms
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
    private val utils: Utils = Utils()
    private val client: HttpClient = utils.getHttpClient()
    suspend fun addUserInGroup(context: Context, userId: Int, groupId: Int, isGroupAdmin: Boolean): Boolean {
        val jsonBody = buildJsonObject {
            put("userId", userId)
            put("groupId", groupId)
            put("isGroupAdmin", isGroupAdmin)
        }.toString()
        val response: HttpResponse = try {
            client.post("http://10.0.2.2:5000/useringroup") {
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
                    setBody(jsonBody)
                }
            }
        } catch (e: Exception) {
            println(e)
            return false
        }
        when (response.status) {
            HttpStatusCode.Unauthorized -> {
                throw AuthorizationException("Unauthorized access update user perms.")
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

    suspend fun updateUserInGroup(context: Context, isActive: Boolean?, isGroupAdmin: Boolean?, userId: Int, groupId: Int): Boolean {
        val jsonBody = buildJsonObject {
            put("isGroupAdmin", isGroupAdmin)
            put("isActive", isActive)
        }.toString()
        val response: HttpResponse = try {
            client.patch("http://10.0.2.2:5000/useringroup/$userId/$groupId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                    contentType(ContentType.Application.Json)
                    setBody(jsonBody)
                }
            }
        } catch (e: Exception) {
            println("Network error occurred: ${e.localizedMessage}")
            return false
        }

        when (response.status) {
            HttpStatusCode.Unauthorized -> {
                throw AuthorizationException("Unauthorized access to data.")
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

    suspend fun getInvitationsToGroup(context: Context, userId: Int): List<UserInGroup>? {
        val response: HttpResponse = try {
            client.get("http://10.0.2.2:5000/useringroup/invitation/$userId") {
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
                throw AuthorizationException("Unauthorized access to data.")
            }
            HttpStatusCode.OK -> {
                val body: String = response.bodyAsText()
                return Json.decodeFromString<List<UserInGroup>>(body)
            }
            else -> {
                println("Received unexpected status: ${response.status}")
                return null
            }
        }
    }

    suspend fun deleteUserFromGroup(context: Context, userId: Int, groupId: Int): Boolean {
        val response: HttpResponse = try {
            client.delete("http://10.0.2.2:5000/useringroup/$userId/$groupId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
            }
        } catch (e: Exception) {
            println("Network error occurred: ${e.localizedMessage}")
            return false
        }

        when (response.status) {
            HttpStatusCode.Unauthorized -> {
                throw AuthorizationException("Unauthorized access to data.")
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

    suspend fun getUsersInGroup(context: Context, id: Int): List<UserMinimalWithUserId>? {
        val response: HttpResponse = try {
            client.get("http://10.0.2.2:5000/useringroup/users/$id") {
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
                throw AuthorizationException("Unauthorized access to group data.")
            }

            HttpStatusCode.OK -> {
                val body: String = response.bodyAsText()
                val json = Json { ignoreUnknownKeys = true }
                return json.decodeFromString<List<UserMinimalWithUserId>>(body)
            }

            else -> {
                println("Received unexpected status: ${response.status}")
                return null
            }
        }
    }

    suspend fun getGroupsFromUserId(context: Context, id: Int): List<Group>? {
        val response: HttpResponse = try {
            client.get("http://10.0.2.2:5000/useringroup/user/$id") {
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
                throw AuthorizationException("Unauthorized access to group data.")
            }

            HttpStatusCode.OK -> {
                val body: String = response.bodyAsText()
                val json = Json { ignoreUnknownKeys = true }
                return json.decodeFromString<List<Group>>(body)
            }

            else -> {
                println("Received unexpected status: ${response.status}")
                return null
            }
        }
    }
}