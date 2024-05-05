package com.console.ratcord.api

import android.content.Context
import com.console.ratcord.domain.entity.group.Group
import com.console.ratcord.domain.entity.exception.AuthorizationException
import com.console.ratcord.domain.entity.group.GroupMinimal
import com.console.ratcord.domain.entity.group.GroupMinimalWithId
import com.console.ratcord.domain.entity.user.UserMinimalWithId
import com.console.ratcord.domain.entity.user.UserMinimalWithUserId
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class GroupService() {
    private val utils: Utils = Utils()
    private val client: HttpClient = utils.getHttpClient()
    suspend fun getGroups(context: Context): List<GroupMinimalWithId>? {
        val response: HttpResponse = try {
            client.get("http://10.0.2.2:5000/group") {
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
                return Json.decodeFromString<List<GroupMinimalWithId>>(body)
            }

            else -> {
                println("Received unexpected status: ${response.status}")
                return null
            }
        }
    }

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
}