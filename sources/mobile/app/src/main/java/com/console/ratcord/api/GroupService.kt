package com.console.ratcord.api

import android.content.Context
import android.net.Uri
import com.console.ratcord.domain.entity.exception.AuthorizationException
import com.console.ratcord.domain.entity.group.GroupMinimal
import com.console.ratcord.domain.entity.group.GroupMinimalWithId
import com.console.ratcord.domain.entity.user.UserMinimal
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class GroupService() {
    private val client: HttpClient = Utils.getHttpClient()

    suspend fun getGroupById(context: Context, id: Int): GroupMinimalWithId? {
        val response: HttpResponse = try {
            client.get("http://10.0.2.2:5000/group/$id") {
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
                return Json.decodeFromString<GroupMinimalWithId>(body)
            }

            else -> {
                println("Received unexpected status: ${response.status}")
                return null
            }
        }
    }

    suspend fun createGroup(context: Context, group: GroupMinimal): Boolean {
        val response: HttpResponse = try {
            client.post("http://10.0.2.2:5000/group") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
                contentType(ContentType.Application.Json)
                setBody(group)
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

    suspend fun updateGroup(context: Context, groupMinimal: GroupMinimal, groupId: Int): Boolean {
        val response: HttpResponse = try {
            client.patch("http://10.0.2.2:5000/group/$groupId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                    contentType(ContentType.Application.Json)
                    setBody(groupMinimal)
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

    suspend fun deleteGroup(context: Context, groupId: Int): Boolean {
        val response: HttpResponse = try {
            client.delete("http://10.0.2.2:5000/group/$groupId") {
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
}